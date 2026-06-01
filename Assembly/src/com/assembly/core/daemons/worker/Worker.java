/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assembly.core.daemons.worker;

import net.johnewart.gearman.common.Job;
import net.johnewart.gearman.net.Connection;
import net.johnewart.gearman.common.interfaces.GearmanFunction;
import net.johnewart.gearman.client.NetworkGearmanWorkerPool;
import net.johnewart.gearman.common.events.WorkEvent;

import com.assembly.core.config.ConfigReference;
import com.assembly.core.config.ConfigManager;
import com.assembly.core.trace.Trace;

/**
 *
 * @author administrator
 */
public abstract class Worker
{
    public Worker(int poolsize)
    {
        try
        {
            String gearmanServer = ConfigManager.instance().resource(ConfigReference.GearmanServer);
            int gearmanPort = Integer.parseInt(ConfigManager.instance().resource(ConfigReference.GearmanPort));

            Connection connection = new Connection(gearmanServer, gearmanPort);
            NetworkGearmanWorkerPool workerPool = new NetworkGearmanWorkerPool.Builder().threads(poolsize).withConnection(connection).build();

            workerPool.registerCallback(Worker.callback(getClass()), new WorkerListener(this));
            workerPool.doWork();
        }
        catch (Exception | Error ex)
        {
            Trace.printStackTrace(ex);
        }
    }

    public static final String callback(Class clazz)
    {
        return clazz.getCanonicalName();
    }

    protected abstract void work(WorkEvent workEvent);

    private class WorkerListener implements GearmanFunction
    {
        private Worker worker = null;

        public WorkerListener(Worker worker)
        {
            this.worker = worker;
        }

        @Override
        public byte[] process(WorkEvent workEvent)
        {
            Job job = workEvent.job;
            byte[] data = job.getData();
            worker.work(workEvent);
            return data;
        }
    }
}
