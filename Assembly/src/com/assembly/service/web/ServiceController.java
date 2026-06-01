/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assembly.service.web;

import java.util.HashMap;

import com.assembly.core.web.WebController;
import com.assembly.core.web.RequestExchange;
import com.assembly.core.version.VersionBundle;
import com.assembly.core.version.VersionResource;
import com.assembly.core.trace.Trace;

/**
 *
 * @author administrator
 */
public class ServiceController extends WebController
{
    @Override
    public Object execute(RequestExchange exchange)
    {
        HashMap<String, String> execute = new HashMap();

        try
        {
            String bundleVersion = VersionBundle.instance().resource(VersionResource.BundleVersion);
            String bundleBuild = VersionBundle.instance().resource(VersionResource.BundleBuild);
            String bundleMark = VersionBundle.instance().resource(VersionResource.BundleMark);

            String applicationVersion = "Version " + bundleVersion + " (" + bundleBuild + "." + bundleMark + ")";
            execute.put("applicationVersion", applicationVersion);
        }
        catch (Exception ex)
        {
            Trace.printStackTrace(ex);
        }

        return execute;
    }

}
