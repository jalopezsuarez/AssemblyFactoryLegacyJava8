/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assembly.service.domain;

/**
 *
 * @author administrator
 */
public class SchedulerTask
{
    private long IDScheduler;
    private String scheduler;
    private String quartz;

    private long counter;
    private String status;
    private String exception;
    private long executed;

    private int disable;
    private long record;
    private long version;

    public final static String STATUS_EXECUTION = "EXECUTION";
    public final static String STATUS_EXCEPTION = "EXCEPTION";

    public long getIDScheduler()
    {
        return IDScheduler;
    }

    public void setIDScheduler(long IDScheduler)
    {
        this.IDScheduler = IDScheduler;
    }

    public String getScheduler()
    {
        return scheduler;
    }

    public void setScheduler(String scheduler)
    {
        this.scheduler = scheduler;
    }

    public String getQuartz()
    {
        return quartz;
    }

    public void setQuartz(String quartz)
    {
        this.quartz = quartz;
    }

    public long getCounter()
    {
        return counter;
    }

    public void setCounter(long counter)
    {
        this.counter = counter;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getException()
    {
        return exception;
    }

    public void setException(String exception)
    {
        this.exception = exception;
    }

    public long getExecuted()
    {
        return executed;
    }

    public void setExecuted(long executed)
    {
        this.executed = executed;
    }

    public int getDisable()
    {
        return disable;
    }

    public void setDisable(int disable)
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
