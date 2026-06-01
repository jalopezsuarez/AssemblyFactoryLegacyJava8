/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assembly.service.push.business;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;

import com.assembly.core.config.ConfigManager;
import com.assembly.core.config.ConfigReference;
import com.assembly.service.push.domain.PushPacket;
import com.assembly.service.push.exceptions.PushTokenException;
import com.assembly.service.push.service.ServiceApns;
import com.assembly.service.push.service.ServiceFcm;
import com.assembly.core.trace.Trace;

/**
 *
 * @author administrator
 */
public class PushManager
{
    public String pushService(PushPacket packet) throws Exception
    {
        boolean success = false;
        String delivery = "";

        List<PushInterface> services = new ArrayList<>();
        services.add(new ServiceApns());
        services.add(new ServiceFcm());

        List<String> extras = ConfigManager.instance().resources(ConfigReference.PushServices);
        for (String reference : extras)
        {
            try
            {
                Class clazz = Class.forName(reference);
                PushInterface instance = (PushInterface) clazz.newInstance();
                services.add(instance);
            }
            catch (Exception | Error ex)
            {
                Trace.printStackTrace(ex);
            }
        }

        // =======================================================
        // =======================================================
        for (PushInterface service : services)
        {
            try
            {
                String exclusions = packet.getExclusions();
                if (exclusions == null || !exclusions.contains(service.getClass().getCanonicalName()))
                {
                    List<String> tokens = service.tokens(packet.getUsername());
                    packet.tokens(tokens);

                    if (packet.tokens().size() > 0 && service.push(packet) == true)
                    {
                        delivery += service.getClass().getCanonicalName() + ",";
                        success = true;
                    }
                }
            }
            catch (Exception | Error ex)
            {
                Trace.printStackTrace(ex);
            }
        }

        if (success == false)
        {
            throw new PushTokenException();
        }

        return delivery;
    }
}
