/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assembly.core.mailer;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.PasswordAuthentication;
import javax.mail.Address;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

import com.assembly.core.commons.FileHelper;
import com.assembly.core.config.ConfigManager;
import com.assembly.core.config.ConfigReference;
import com.assembly.core.resources.RelativeResource;
import com.assembly.core.trace.Trace;

/**
 *
 * @author administrator
 */
public class MailerTemplate
{
    private static final String MailerPublicStorage = "/res/mailer/";
    private static final String MailerTemplateHTML = ".html";
    private static final String MailerTemplateText = ".txt";

    private Recipient from;
    private Recipient replyTo;

    private List<Recipient> to = new ArrayList<>();
    private List<Recipient> cc = new ArrayList<>();
    private List<Recipient> bcc = new ArrayList<>();

    private HashMap<String, String> context = new HashMap<>();
    private String subject;
    private String html;
    private String text;

    public MailerTemplate(String template)
    {
        InputStream resource = null;
        InputStreamReader stream = null;
        BufferedReader reader = null;

        try
        {
            String relativeResource = FileHelper.buildResource(MailerPublicStorage, template) + MailerTemplateHTML;
            resource = RelativeResource.instance().read(relativeResource);
            stream = new InputStreamReader(resource, "UTF-8");
            reader = new BufferedReader(stream);
            StringBuilder response = new StringBuilder();
            String read;
            while ((read = reader.readLine()) != null)
            {
                response.append(read);
            }
            html = response.toString();
        }
        catch (Exception | Error ex)
        {
            Trace.printStackTrace(ex);
        }
        finally
        {
            try
            {
                if (reader != null)
                {
                    reader.close();
                }
            }
            catch (Exception | Error ex)
            {
            }
            try
            {
                if (stream != null)
                {
                    stream.close();
                }
            }
            catch (Exception | Error ex)
            {
            }
            try
            {
                if (resource != null)
                {
                    resource.close();
                }
            }
            catch (Exception | Error ex)
            {
            }
        }

        try
        {
            String relativeResource = FileHelper.buildResource(MailerPublicStorage, template) + MailerTemplateText;
            resource = RelativeResource.instance().read(relativeResource);
            stream = new InputStreamReader(resource, "UTF-8");
            reader = new BufferedReader(stream);
            StringBuilder response = new StringBuilder();
            String read;
            while ((read = reader.readLine()) != null)
            {
                response.append(read);
            }
            text = response.toString();
        }
        catch (Exception | Error ex)
        {
            Trace.printStackTrace(ex);
        }
        finally
        {
            try
            {
                if (reader != null)
                {
                    reader.close();
                }
            }
            catch (Exception | Error ex)
            {
            }
            try
            {
                if (stream != null)
                {
                    stream.close();
                }
            }
            catch (Exception | Error ex)
            {
            }
            try
            {
                if (resource != null)
                {
                    resource.close();
                }
            }
            catch (Exception | Error ex)
            {
            }
        }

        from = new Recipient(ConfigManager.instance().resource(ConfigReference.MailDefaultFromEmail), ConfigManager.instance().resource(ConfigReference.MailDefaultFromName));
        replyTo = new Recipient(ConfigManager.instance().resource(ConfigReference.MailDefaultReplyToEmail), ConfigManager.instance().resource(ConfigReference.MailDefaultReplyToName));

        to = new ArrayList<>();
        cc = new ArrayList<>();
        bcc = new ArrayList<>();
    }

    public void template(String param, String value)
    {
        context.put(param, value);
    }

    public void subject(String subject)
    {
        this.subject = subject;
    }

    public void from(String email)
    {
        from(email, email);
    }

    public void from(String email, String name)
    {
        from = new Recipient(email, name);
    }

    public void replyTo(String email)
    {
        replyTo(email, email);
    }

    public void replyTo(String email, String name)
    {
        replyTo = new Recipient(email, name);
    }

    public void to(String email)
    {
        to(email, email);
    }

    public void to(String email, String name)
    {
        to.add(new Recipient(email, name));
    }

    public void cc(String email)
    {
        cc(email, email);
    }

    public void cc(String email, String name)
    {
        cc.add(new Recipient(email, name));
    }

    public void bcc(String email)
    {
        bcc(email, email);
    }

    public void bcc(String email, String name)
    {
        bcc.add(new Recipient(email, name));
    }

    public boolean send() throws Exception
    {
        boolean results = true;

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.connectiontimeout", "10000");
        properties.put("mail.smtp.debug", "true");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.socketFactory.fallback", "true");
        properties.put("mail.smtp.ssl.checkserveridentity", "false");
        properties.put("mail.smtp.ssl.trust", "*");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.timeout", "10000");
        properties.put("mail.transport.protocol", "smtps");

        String host = ConfigManager.instance().resource(ConfigReference.MailTransportHost);
        String port = ConfigManager.instance().resource(ConfigReference.MailTransportPort);
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.socketFactory.port", port);

        String username = ConfigManager.instance().resource(ConfigReference.MailTransportUsername);
        String password = ConfigManager.instance().resource(ConfigReference.MailTransportPassword);
        Session session = Session.getInstance(properties, new javax.mail.Authenticator()
        {
            @Override
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(username, password);
            }
        });
        session.setDebug(true);

        MimeMessage message = new MimeMessage(session);
        message.setSubject(subject);

        message.setFrom(new InternetAddress(from.getEmail(), from.getName(), "UTF-8"));
        message.setReplyTo(new Address[]
        {
            new InternetAddress(replyTo.getEmail(), replyTo.getName(), "UTF-8")
        });
        for (Recipient recipient : to)
        {
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient.getEmail(), recipient.getName(), "UTF-8"));
        }
        for (Recipient recipient : cc)
        {
            message.addRecipient(Message.RecipientType.CC, new InternetAddress(recipient.getEmail(), recipient.getName(), "UTF-8"));
        }
        for (Recipient recipient : bcc)
        {
            message.addRecipient(Message.RecipientType.BCC, new InternetAddress(recipient.getEmail(), recipient.getName(), "UTF-8"));
        }

        Template templateHTML = Mustache.compiler().compile(html);
        String responseHTML = templateHTML.execute(context);
        Template templateText = Mustache.compiler().compile(text);
        String responseText = templateText.execute(context);

        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setHeader("Content-Type", "text/html; charset=\"utf-8\"");
        htmlPart.setContent(responseHTML, "text/html; charset=\"utf-8\"");

        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setHeader("Content-Type", "text/plain; charset=\"utf-8\"");
        textPart.setContent(responseText, "text/plain; charset=\"utf-8\"");

        Multipart content = new MimeMultipart("alternative");
        content.addBodyPart(textPart);
        content.addBodyPart(htmlPart);
        message.setContent(content);

        Transport transport = session.getTransport("smtps");
        transport.send(message);
        transport.close();

        return results;
    }

    private class Recipient
    {
        private final String email;
        private final String name;

        public Recipient(String email, String name)
        {
            this.email = email;
            this.name = (name != null && name.length() > 0) ? name : email;
        }

        public String getEmail()
        {
            return email;
        }

        public String getName()
        {
            return name;
        }
    }

}
