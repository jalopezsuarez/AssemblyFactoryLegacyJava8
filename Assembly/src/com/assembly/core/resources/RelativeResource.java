/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assembly.core.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.assembly.core.commons.FileHelper;
import com.assembly.core.commons.TextFormatter;
import com.assembly.core.trace.Trace;

/**
 *
 * @author administrator
 */
public class RelativeResource
{
    private final HashMap<String, ResourceLocation> resources = new HashMap<>();

    private enum ResourceLocation
    {
        ABSOLUTE, RELATIVE, CLASSPATH
    }

    // =======================================================
    private static final RelativeResource INSTANCE = new RelativeResource();

    public static RelativeResource instance()
    {
        return INSTANCE;
    }

    // =======================================================
    private RelativeResource()
    {
    }

    public InputStream read(String relative)
    {
        InputStream resourceStream = null;

        try
        {
            if (resources.containsKey(relative) && resources.get(relative) == ResourceLocation.RELATIVE)
            {
                String resource = FileHelper.buildResource(FileHelper.currentDirectory(), relative);
                resourceStream = new FileInputStream(new File(resource));
            }
            else if (resources.containsKey(relative) && resources.get(relative) == ResourceLocation.CLASSPATH)
            {
                String classpath = buildClasspath(relative);
                resourceStream = Thread.currentThread().getClass().getResourceAsStream(classpath);
            }
            else
            {
                String resource = FileHelper.buildResource(FileHelper.currentDirectory(), relative);
                File resourceFile = new File(resource);
                if (resourceFile.exists())
                {
                    resourceStream = new FileInputStream(resourceFile);
                    resources.put(relative, ResourceLocation.RELATIVE);
                }
                else
                {
                    String classpath = buildClasspath(relative);
                    resourceStream = Thread.currentThread().getClass().getResourceAsStream(classpath);
                    resources.put(relative, ResourceLocation.CLASSPATH);
                }
            }
        }
        catch (Exception | Error ex)
        {
            Trace.printStackTrace(ex);
        }

        return resourceStream;
    }

    private static String buildClasspath(String resource)
    {
        resource = TextFormatter.trim(resource, "/");
        resource = TextFormatter.trim(resource, "\\");
        resource = resource.replaceAll("[" + Pattern.quote("/") + Pattern.quote("\\") + "]+", Matcher.quoteReplacement("/"));

        String classpath = "/" + resource;
        return classpath;
    }

}
