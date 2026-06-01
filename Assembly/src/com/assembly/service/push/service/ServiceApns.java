/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assembly.service.push.service;

import java.util.List;
import java.util.ArrayList;
import java.io.InputStream;
import java.sql.ResultSet;

import javapns.Push;
import javapns.notification.PushedNotifications;
import javapns.notification.PushNotificationBigPayload;

import com.assembly.core.commons.FileHelper;
import com.assembly.service.push.business.PushInterface;
import com.assembly.service.push.domain.PushPacket;
import com.assembly.core.source.db.ConnectorInterface;
import com.assembly.core.source.db.DBConnector;
import com.assembly.core.config.ConfigReference;
import com.assembly.core.config.ConfigManager;
import com.assembly.core.resources.RelativeResource;
import com.assembly.core.trace.Trace;

/**
 *
 * @author administrator
 */
public class ServiceApns implements PushInterface
{
    private static final String ReferenceResources = "/res/push/";

    @Override
    public List<String> tokens(String username)
    {
        ArrayList<String> response = new ArrayList<>();
        ConnectorInterface database = null;

        String statement = " ";
        statement += " SELECT ";
        statement += " system_session_token.bearer AS token ";
        statement += " FROM system_session ";
        statement += " INNER JOIN system_session_token ON system_session_token.id_session = system_session.id ";
        statement += " WHERE 1=1 ";
        statement += " AND system_session.username = :username ";
        statement += " AND system_session_token.platform = :platform ";
        statement += " AND system_session_token.bearer IS NOT NULL ";
        statement += " AND system_session_token.version > NOW() - INTERVAL 32 DAY ";
        statement += " ORDER BY system_session_token.version DESC ";

        try
        {
            database = DBConnector.instance("ASSEMBLY");
            database.prepare(statement);
            database.bind("username", username);
            database.bind("platform", PushPacket.PLATFORM_APPLE);
            ResultSet rowdata = database.read();
            while (rowdata.next())
            {
                response.add(rowdata.getString("token"));
            }
        }
        catch (Exception ex)
        {
            Trace.printStackTrace(ex);
        }
        finally
        {
            try
            {
                if (database != null)
                {
                    database.dispose();
                }
            }
            catch (Exception ex)
            {
            }
        }

        return response;
    }

    @Override
    public boolean push(PushPacket pushPacket)
    {
        boolean results = false;

        InputStream keystore = null;
        try
        {
            String certificate = ConfigManager.instance().resource(ConfigReference.PushAPNSCertificate);
            String passphrase = ConfigManager.instance().resource(ConfigReference.PushAPNSPassphrase);
            String sandbox = ConfigManager.instance().resource(ConfigReference.PushAPNSSandbox);

            boolean production = (sandbox == null || sandbox.toLowerCase().contains("0") || sandbox.toLowerCase().contains("false"));
            String relativeResource = FileHelper.buildResource(ReferenceResources, certificate);
            keystore = RelativeResource.instance().read(relativeResource);

            PushNotificationBigPayload payload = PushNotificationBigPayload.complex();
            payload.addCustomAlertTitle(pushPacket.getSummary());
            payload.addCustomAlertBody(pushPacket.getMessage());
            payload.addCustomDictionary(PushPacket.PUSH_RESOURCE, pushPacket.getResource());
            payload.addCustomDictionary(PushPacket.PUSH_ID_RESOURCE, pushPacket.getIDResource());
            payload.addCustomDictionary(PushPacket.PUSH_REFERENCE, pushPacket.getReference());
            payload.addSound("default");
            payload.addBadge(0);

            List<String> tokens = new ArrayList<>();
            for (String token : pushPacket.tokens())
            {
                tokens.add(token);
            }

            PushedNotifications response = Push.payload(payload, keystore, passphrase, production, tokens);
            results = results || (response.getSuccessfulNotifications().size() > 0);
        }
        catch (Exception | Error ex)
        {
            Trace.printStackTrace(ex);
        }
        finally
        {
            try
            {
                if (keystore != null)
                {
                    keystore.close();
                }
            }
            catch (Exception | Error ex)
            {
            }
        }

        return results;
    }
}
