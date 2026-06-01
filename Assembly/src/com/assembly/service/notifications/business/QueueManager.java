/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assembly.service.notifications.business;

import java.util.List;

import net.johnewart.gearman.net.Connection;
import net.johnewart.gearman.constants.JobPriority;
import net.johnewart.gearman.client.NetworkGearmanClient;
import net.johnewart.gearman.exceptions.NoServersAvailableException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.assembly.core.secure.EncryptionManager;
import com.assembly.core.config.ConfigManager;
import com.assembly.core.config.ConfigReference;
import com.assembly.service.notifications.domain.Queue;
import com.assembly.core.daemons.worker.Worker;
import com.assembly.service.push.daemons.PushWorker;
import com.assembly.core.secure.Encryption;
import com.assembly.core.trace.Trace;

/**
 *
 * @author administrator
 */
public class QueueManager
{
    public final static NetworkGearmanClient gearman = new NetworkGearmanClient();

    public QueueManager()
    {
        try
        {
            String gearmanServer = ConfigManager.instance().resource(ConfigReference.GearmanServer);
            String gearmanPort = ConfigManager.instance().resource(ConfigReference.GearmanPort);

            Connection connection = new Connection(gearmanServer, Integer.parseInt(gearmanPort));
            gearman.addConnection(connection);
        }
        catch (Exception | Error ex)
        {
            Trace.printStackTrace(ex);
        }
    }

    public void process(List<Queue> queues)
    {
        if (queues != null && queues.size() > 0)
        {
            for (Queue queue : queues)
            {
                try
                {
                    ObjectMapper mapper = new ObjectMapper();
                    byte[] data = mapper.writeValueAsBytes(queue);

                    JobPriority priority = JobPriority.LOW;
                    int priorize = queue.getPriorize();
                    if (priorize <= 0)
                    {
                        priority = JobPriority.LOW;
                    }
                    else if (priorize <= 1)
                    {
                        priority = JobPriority.NORMAL;
                    }
                    else if (priorize <= 2)
                    {
                        priority = JobPriority.HIGH;
                    }

                    String jobid = queue.getResource() + queue.getIDResource() + queue.getUsername();
                    jobid = EncryptionManager.instance().encode(jobid, Encryption.MD5);

                    gearman.submitJobInBackground(Worker.callback(PushWorker.class), data, priority, jobid);
                }
                catch (Exception | NoServersAvailableException ex)
                {
                    Trace.printStackTrace(ex);
                }
            }
        }
    }
}
