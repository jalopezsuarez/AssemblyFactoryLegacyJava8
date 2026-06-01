/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assembly.service.notifications.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author administrator
 */
public class Queue
{
    @JsonProperty("resource")
    protected long resource;

    @JsonProperty("id_resource")
    protected String IDResource;

    @JsonProperty("username")
    protected String username;

    @JsonProperty("reference")
    protected String reference;

    @JsonProperty("summary")
    protected String summary;

    @JsonProperty("message")
    protected String message;

    @JsonProperty("review")
    protected boolean review;

    @JsonProperty("intents")
    protected long intents;

    @JsonProperty("delivery")
    protected String delivery;

    @JsonProperty("priorize")
    protected int priorize;

    @JsonProperty("exclusions")
    protected String exclusions;

    @JsonProperty("status")
    protected int status;

    @JsonProperty("last_status")
    protected long lastStatus;

    @JsonProperty("record")
    protected long record;

    @JsonProperty("version")
    protected long version;

    public static final int STATUS_PENDING = 0;
    public static final int STATUS_QUEUED = 1;
    public static final int STATUS_PROGRESS = 2;
    public static final int STATUS_COMPLETED = 3;
    public static final int STATUS_EXCEPTION = 4;

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

    public boolean isReview()
    {
        return review;
    }

    public void setReview(boolean review)
    {
        this.review = review;
    }

    public long getIntents()
    {
        return intents;
    }

    public void setIntents(long intents)
    {
        this.intents = intents;
    }

    public String getDelivery()
    {
        return delivery;
    }

    public void setDelivery(String delivery)
    {
        this.delivery = delivery;
    }

    public int getPriorize()
    {
        return priorize;
    }

    public void setPriorize(int priorize)
    {
        this.priorize = priorize;
    }

    public String getExclusions()
    {
        return exclusions;
    }

    public void setExclusions(String exclusions)
    {
        this.exclusions = exclusions;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public long getLastStatus()
    {
        return lastStatus;
    }

    public void setLastStatus(long lastStatus)
    {
        this.lastStatus = lastStatus;
    }

    public long getRecord()
    {
        return record;
    }

    public void setRecord(long record)
    {
        this.record = record;
    }

    public long getVersion()
    {
        return version;
    }

    public void setVersion(long version)
    {
        this.version = version;
    }

}
