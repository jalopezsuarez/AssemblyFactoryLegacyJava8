/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assembly.service.push.domain;

import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author administrator
 */
public class PushPacket
{
    protected final List<String> tokens = new ArrayList<>();

    protected long resource;
    protected String IDResource;
    protected String username;
    protected String reference;

    protected String summary;
    protected String message;

    protected String exclusions;

    public static final String PUSH_RESOURCE = "resource";
    public static final String PUSH_ID_RESOURCE = "id_resource";
    public static final String PUSH_USERNAME = "username";
    public static final String PUSH_REFERENCE = "reference";
    public static final String PUSH_SUMMARY = "summary";
    public static final String PUSH_MESSAGE = "message";

    public static final String PLATFORM_APPLE = "APPLE";
    public static final String PLATFORM_ANDROID = "ANDROID";
    public static final String PLATFORM_UNKNOWN = "UNKNOWN";

    public void tokens(List<String> pushes)
    {
        this.tokens.clear();

        if (pushes != null && pushes.size() > 0)
        {
            for (String push : pushes)
            {
                if (push != null && !push.trim().isEmpty())
                {
                    this.tokens.add(push);
                }
            }
        }
    }

    public List<String> tokens()
    {
        return this.tokens;
    }

    public long getResource()
    {
        return resource;
    }

    public void setResource(long resource)
    {
        this.resource = resource;
    }

    public String getIDResource()
    {
        return IDResource;
    }

    public void setIDResource(String IDResource)
    {
        this.IDResource = IDResource;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getReference()
    {
        return reference;
    }

    public void setReference(String reference)
    {
        this.reference = reference;
    }

    public String getSummary()
    {
        return summary;
    }

    public void setSummary(String summary)
    {
        this.summary = summary;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getExclusions()
    {
        return exclusions;
    }

    public void setExclusions(String exclusions)
    {
        this.exclusions = exclusions;
    }

}
