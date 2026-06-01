/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assembly.service.business;

import java.util.List;

import com.assembly.service.dao.WorkerDAO;
import com.assembly.service.domain.WorkerTask;
import com.assembly.core.trace.Trace;

/**
 *
 * @author administrator
 */
public class WorkerManager
{
    private final WorkerDAO service;
    private final List<WorkerTask> workers;

    private final ThreadGroup threads;
    private Thread thread;

    public WorkerManager()
    {
        threads = new ThreadGroup(getClass().getCanonicalName());
        service = new WorkerDAO();
        workers = service.fetchWorker();
    }

    public void initialize()
    {
        for (WorkerTask worker : workers)
        {
            thread = new Thread(threads, new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        worker.setStatus(WorkerTask.STATUS_EXECUTION);
                        worker.setException(null);
                        service.save(worker);

                        String reference = worker.getWorker();
                        int poolsize = worker.getPoolsize();
                        Class clazz = Class.forName(reference);
                        clazz.getDeclaredConstructor(int.class).newInstance(poolsize);
                    }
                    catch (Exception | Error ex)
                    {
                        Trace.printStackTrace(ex);

                        worker.setStatus(WorkerTask.STATUS_EXCEPTION);
                        worker.setException(ex.getMessage() + ex.toString());
                        service.save(worker);
                    }
                }
            });
            thread.start();
        }
    }
}
