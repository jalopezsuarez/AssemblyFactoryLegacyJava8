/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assembly.core.version;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author administrator
 */
public enum VersionResource
{
    BundleIdentifier("bundle.identifier"),
    BundleName("bundle.name"),
    BundleVersion("bundle.version"),
    BundleBuild("bundle.build"),
    BundleMark("bundle.mark"),
    BundleTime("bundle.time");

    private final String value;
    private final static Map<String, VersionResource> enums = new HashMap<>();

    private VersionResource(String value)
    {
        this.value = value;
    }

    static
    {
        for (VersionResource e : values())
        {
            enums.put(e.toString(), e);
        }
    }

    public static VersionResource typeOf(String value)
    {
        VersionResource type = null;
        if (value != null && enums.containsKey(value))
        {
            type = enums.get(value);
        }
        else
        {
            try
            {
                type = valueOf(value);
            }
            catch (Exception ex)
            {
            }
        }
        return type;
    }

    public String value()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return value;
    }
}
