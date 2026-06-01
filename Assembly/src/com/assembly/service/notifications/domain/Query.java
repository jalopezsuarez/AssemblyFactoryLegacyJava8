/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assembly.service.notifications.domain;

/**
 *
 * @author administrator
 */
public class Query
{
    private long IDQuery;
    private String instance;
    private String reference;

    private String summary;
    private String layout;

    private String query;
    private String database;

    private String announce;
    private String scheduler;

    private int priorize;
    private String exclusions;

    private long lastStatus;
    private boolean success;
    private String exception;
    private boolean disable;

    private long record;
    private long version;

    public long getIDQuery()
    {
        return IDQuery;
    }

    public void setIDQuery(long IDQuery)
    {
        this.IDQuery = IDQuery;
    }

    public String getInstance()
    {
        return instance;
    }

    public void setInstance(String instance)
    {
        this.instance = instance;
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

    public String getLayout()
    {
        return layout;
    }

    public void setLayout(String layout)
    {
        this.layout = layout;
    }

    public String getQuery()
    {
        return query;
    }

    public void setQuery(String query)
    {
        this.query = query;
    }

    public String getDatabase()
    {
        return database;
    }

    public void setDatabase(String database)
    {
        this.database = database;
    }

    public String getAnnounce()
    {
        return announce;
    }

    public void setAnnounce(String announce)
    {
        this.announce = announce;
    }

    public String getScheduler()
    {
        return scheduler;
    }

    public void setScheduler(String scheduler)
    {
        this.scheduler = scheduler;
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

    public long getLastStatus()
    {
        return lastStatus;
    }

    public void setLastStatus(long lastStatus)
    {
        this.lastStatus = lastStatus;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public void setSuccess(boolean success)
    {
        this.success = success;
    }

    public String getException()
    {
        return exception;
    }

    public void setException(String exception)
    {
        this.exception = exception;
    }

    public boolean isDisable()
    {
        return disable;
    }

    public void setDisable(boolean disable)
    {
        this.disable = disable;
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
