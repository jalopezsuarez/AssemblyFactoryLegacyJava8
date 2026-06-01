/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assembly.service.push.daemons;

import java.util.List;
import java.util.ArrayList;

import com.assembly.core.daemons.scheduler.Scheduler;
import com.assembly.service.notifications.business.QueueManager;
import com.assembly.service.notifications.domain.Query;
import com.assembly.service.notifications.domain.Queue;
import com.assembly.service.notifications.service.QueriesService;
import com.assembly.service.notifications.service.QueueService;

/**
 *
 * @author administrator
 */
public class PushScheduler extends Scheduler
{

    @Override
    protected void cron() throws Exception
    {
        QueriesService notificationsService = new QueriesService();
        List<Query> notifications = notificationsService.fetchScheduler();

        List<Queue> batch = new ArrayList<>();
        for (Query scheduler : notifications)
        {
            List<Queue> execute = notificationsService.executeScheduler(scheduler);
            batch.addAll(execute);
        }
        QueueService queueService = new QueueService();
        queueService.pushQueue(batch);

        QueueManager queueManager = new QueueManager();
        List<Queue> queues = queueService.fetchQueue();
        queueManager.process(queues);
    }
}
