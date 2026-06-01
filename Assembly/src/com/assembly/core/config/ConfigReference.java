/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assembly.core.config;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author administrator
 */
public enum ConfigReference
{
    GearmanServer("resources.gearman.server"),
    GearmanPort("resources.gearman.port"),
    MailTransportHost("resources.mail.transport.host"),
    MailTransportPort("resources.mail.transport.port"),
    MailTransportUsername("resources.mail.transport.username"),
    MailTransportPassword("resources.mail.transport.password"),
    MailDefaultFromEmail("resources.mail.defaultFrom.email"),
    MailDefaultFromName("resources.mail.defaultFrom.name"),
    MailDefaultReplyToEmail("resources.mail.defaultReplyTo.email"),
    MailDefaultReplyToName("resources.mail.defaultReplyTo.name"),
    PushAPNSCertificate("resources.push.apns.certificate"),
    PushAPNSPassphrase("resources.push.apns.passphrase"),
    PushAPNSSandbox("resources.push.apns.sandbox"),
    PushFCMKey("resources.push.fcm.key"),
    PushServices("resources.push.services[]");

    private final String value;
    private final static Map<String, ConfigReference> enums = new HashMap<>();

    private ConfigReference(String value)
    {
        this.value = value;
    }

    static
    {
        for (ConfigReference e : values())
        {
            enums.put(e.toString(), e);
        }
    }

    public static ConfigReference typeOf(String value)
    {
        ConfigReference type = null;
        if (value != null && enums.containsKey(value))
        {
            type = enums.get(value);
        }
        else
        {
            try
            {
                type = valueOf(value);
            }
            catch (Exception ex)
            {
            }
        }
        return type;
    }

    @Override
    public String toString()
    {
        return value;
    }
}
