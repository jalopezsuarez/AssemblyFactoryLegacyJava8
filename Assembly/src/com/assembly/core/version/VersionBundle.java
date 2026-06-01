/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assembly.core.version;

import java.util.HashMap;
import java.util.Properties;
import java.io.InputStream;

import com.assembly.core.commons.FileHelper;
import com.assembly.core.resources.RelativeResource;
import com.assembly.core.trace.Trace;

/**
 *
 * @author administrator
 */
public final class VersionBundle
{
    private static final String ReferenceResources = "/res/version/";

    private final HashMap<String, String> resources = new HashMap<>();

    // =======================================================
    private static final VersionBundle INSTANCE = new VersionBundle();

    public static VersionBundle instance()
    {
        return INSTANCE;
    }

    // =======================================================
    private VersionBundle()
    {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try
        {
            String relativeResource = FileHelper.buildResource(ReferenceResources, "version.properties");
            inputStream = RelativeResource.instance().read(relativeResource);
            if (inputStream == null)
            {
                throw new Exception("Unable to find " + relativeResource);
            }
            properties.load(inputStream);

            // -------------------------------------------------------
            for (VersionResource resource : VersionResource.values())
            {
                resource(resource, "");
            }

            // -------------------------------------------------------
            for (final String name : properties.stringPropertyNames())
            {
                resource(name, properties.getProperty(name));
            }
        }
        catch (Exception ex)
        {
            Trace.printStackTrace(ex);
        }
        finally
        {
            try
            {
                if (inputStream != null)
                {
                    inputStream.close();
                }
            }
            catch (Exception | Error ex)
            {
                Trace.printStackTrace(ex);
            }
        }
    }

    public void resource(String resource, String value)
    {
        if (resource != null && !resource.isEmpty())
        {
            if (value != null && value.trim().length() > 0)
            {
                resources.put(resource.trim().toLowerCase(), value.trim());
            }
        }
    }

    public void resource(VersionResource resource, String value)
    {
        resource(resource.value(), value);
    }

    public String resource(String resource)
    {
        String values = "";
        if (resource != null && resources.containsKey(resource.trim().toLowerCase()))
        {
            values = resources.get(resource.trim().toLowerCase());
        }
        return values;
    }

    public String resource(VersionResource resource)
    {
        return resource(resource.value());
    }
}
