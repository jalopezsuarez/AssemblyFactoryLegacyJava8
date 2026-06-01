/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assembly.service.push.daemons;

import net.johnewart.gearman.common.events.WorkEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.assembly.core.daemons.worker.Worker;
import com.assembly.service.notifications.domain.Queue;
import com.assembly.service.notifications.service.QueueService;
import com.assembly.service.push.business.PushManager;
import com.assembly.service.push.domain.PushPacket;
import com.assembly.core.trace.Trace;

/**
 *
 * @author administrator
 */
public class PushWorker extends Worker
{
    public PushWorker(int poolsize)
    {
        super(poolsize);
    }

    @Override
    protected void work(WorkEvent workEvent)
    {
        try
        {
            byte[] workload = workEvent.job.getData();
            ObjectMapper mapper = new ObjectMapper();
            Queue queue = mapper.readValue(workload, Queue.class);

            QueueService queueService = new QueueService();
            try
            {
                queue.setDelivery(null);
                queue.setStatus(Queue.STATUS_PROGRESS);
                queueService.updateQueue(queue);

                PushPacket pushPacket = new PushPacket();
                pushPacket.setResource(queue.getResource());
                pushPacket.setIDResource(queue.getIDResource());
                pushPacket.setUsername(queue.getUsername());
                pushPacket.setReference(queue.getReference());
                pushPacket.setSummary(queue.getSummary());
                pushPacket.setMessage(queue.getMessage());
                pushPacket.setExclusions(queue.getExclusions());

                PushManager pushManager = new PushManager();
                String delivery = pushManager.pushService(pushPacket);

                queue.setDelivery(delivery);
                queue.setStatus(Queue.STATUS_COMPLETED);
                queueService.updateQueue(queue);
            }
            catch (Exception ex)
            {
                queue.setDelivery(null);
                queue.setStatus(Queue.STATUS_EXCEPTION);
                queueService.updateQueue(queue);
            }
        }
        catch (Exception | Error ex)
        {
            Trace.printStackTrace(ex);
        }
    }
}
