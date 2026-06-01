/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assembly.service.business;

import java.util.List;

import com.assembly.core.monitor.MonitorMode;
import com.assembly.core.monitor.MonitorProtocol;
import com.assembly.service.dao.SchedulerDAO;
import com.assembly.service.domain.SchedulerTask;
import com.assembly.core.trace.Trace;

/**
 *
 * @author administrator
 */
public class SchedulerManager
{
    private final SchedulerDAO service;
    private final List<SchedulerTask> schedulers;

    public SchedulerManager()
    {
        service = new SchedulerDAO();
        schedulers = service.fetchScheduler();
    }

    public void initialize()
    {
        for (SchedulerTask scheduler : schedulers)
        {
            SchedulerMonitor schedulerMonitor = new SchedulerMonitor(scheduler);
            schedulerMonitor.monitorize(scheduler.getQuartz(), MonitorMode.IMMEDIATE);
        }
    }

    private class SchedulerMonitor extends MonitorProtocol
    {
        SchedulerTask scheduler = null;

        public SchedulerMonitor(SchedulerTask scheduler)
        {
            this.scheduler = scheduler;
        }

        @Override
        protected Object execute() throws Exception
        {
            try
            {
                scheduler.setStatus(SchedulerTask.STATUS_EXECUTION);
                scheduler.setException(null);
                service.save(scheduler);

                String reference = scheduler.getScheduler();
                Class clazz = Class.forName(reference);
                clazz.newInstance();
            }
            catch (Exception | Error ex)
            {
                Trace.printStackTrace(ex);

                scheduler.setStatus(SchedulerTask.STATUS_EXCEPTION);
                scheduler.setException(ex.getMessage() + ex.toString());
                service.save(scheduler);
            }

            return null;
        }
    }

}
