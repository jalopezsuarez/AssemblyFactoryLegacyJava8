/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assembly.service.push.service;

import java.util.List;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.json.JSONObject;

import com.assembly.core.trace.Trace;
import com.assembly.service.push.business.PushInterface;
import com.assembly.service.push.domain.PushPacket;
import com.assembly.core.source.db.ConnectorInterface;
import com.assembly.core.source.db.DBConnector;
import com.assembly.core.config.ConfigManager;
import com.assembly.core.config.ConfigReference;

/**
 *
 * @author administrator
 */
public class ServiceFcm implements PushInterface
{
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
        statement += " WHERE TRUE ";
        statement += " AND system_session.username = :username ";
        statement += " AND system_session_token.platform = :platform ";
        statement += " AND system_session_token.bearer IS NOT NULL ";
        statement += " AND system_session_token.version > NOW() - INTERVAL 28 DAY ";
        statement += " ORDER BY system_session_token.version DESC ";

        try
        {
            database = DBConnector.instance("ASSEMBLY");
            database.prepare(statement);
            database.bind("username", username);
            database.bind("platform", PushPacket.PLATFORM_ANDROID);
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

        try
        {
            URL resource = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection connection = (HttpURLConnection) resource.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(false);

            connection.setRequestProperty("Authorization", "key=" + ConfigManager.instance().resource(ConfigReference.PushFCMKey));
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestMethod("POST");

            JSONObject payload = new JSONObject();

            JSONObject notification = new JSONObject();
            notification.put("title", pushPacket.getSummary());
            notification.put("body", pushPacket.getMessage());
            payload.put("notification", notification);

            JSONObject data = new JSONObject();
            data.put(PushPacket.PUSH_RESOURCE, pushPacket.getResource());
            data.put(PushPacket.PUSH_ID_RESOURCE, pushPacket.getIDResource());
            data.put(PushPacket.PUSH_REFERENCE, pushPacket.getReference());
            payload.put("data", data);

            //payload.put("to", token);
            payload.put("registration_ids", pushPacket.tokens());

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(payload.toString());
            writer.flush();
            writer.close();

            String readLine;
            StringBuilder response = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((readLine = reader.readLine()) != null)
            {
                response.append(readLine);
            }
            reader.close();
            Trace.printStackTrace(getClass().getCanonicalName() + response.toString());

            results = results || (connection.getResponseCode() == HttpURLConnection.HTTP_OK);
        }
        catch (Exception | Error ex)
        {
            Trace.printStackTrace(ex);
        }

        return results;
    }

}
