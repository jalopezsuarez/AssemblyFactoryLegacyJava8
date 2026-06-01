package com.assembly.service.ui.application;

import com.assembly.core.web.WebResource.ContentType;
import com.assembly.core.web.WebResource.Security;
import com.assembly.core.web.WebServer;

import com.assembly.service.business.SchedulerManager;
import com.assembly.service.business.WorkerManager;
import com.assembly.service.web.ServiceController;
import com.assembly.service.web.TesterController;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author administrator
 */
public class ApplicationDelegate
{
    public static void main(String[] args)
    {
        SchedulerManager schedulerManager = new SchedulerManager();
        schedulerManager.initialize();

        WorkerManager workerManager = new WorkerManager();
        workerManager.initialize();

        WebServer.instance().resource("/service", ServiceController.class, ContentType.WEB, Security.PUBLIC);
        WebServer.instance().resource("/tester", TesterController.class, ContentType.JSON, Security.PUBLIC);
        WebServer.instance().start(8080);
    }
}
