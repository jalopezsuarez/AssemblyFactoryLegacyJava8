/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assembly.core.config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.assembly.core.commons.TextFormatter;
import com.assembly.core.trace.Trace;

/**
 *
 * @author administrator
 */
public class ConfigProperties
{
    private final HashMap<String, Object> properties = new HashMap<>();

    public ConfigProperties()
    {
    }

    public boolean load(InputStream resource)
    {
        boolean response = true;

        InputStreamReader stream = null;
        BufferedReader reader = null;

        try
        {
            stream = new InputStreamReader(resource);
            reader = new BufferedReader(stream);
            String read;
            while ((read = reader.readLine()) != null)
            {
                String param = "";
                String value = "";

                try
                {
                    String rawLine = TextFormatter.trim(read, "");
                    if (rawLine.matches("^[A-Za-z0-9].*$"))
                    {
                        int equals = read.indexOf('=');
                        String rawParam = read.substring(0, equals);
                        param = rawParam.replaceAll("^[\\s\"\']+", "");
                        param = param.replaceAll("[\\s\"\']+$", "");

                        String rawValue = read.substring(equals + 1, read.length());
                        value = rawValue.replaceAll("^[\\s\"\']+", "");
                        value = value.replaceAll("[\\s\"\']+$", "");
                    }
                }
                catch (Exception ex)
                {
                    Trace.printStackTrace(ex);
                }

                if (param != null && param.length() > 0)
                {
                    if (param.contains("[]"))
                    {
                        List<String> values = new ArrayList<>();
                        if (properties.containsKey(param))
                        {
                            Object instance = properties.get(param);
                            if (instance != null && instance instanceof List)
                            {
                                values = (List) instance;
                            }
                        }
                        values.add(value);
                        properties.put(param, values);
                    }
                    else
                    {
                        properties.put(param, value);
                    }
                }
            }
        }
        catch (Exception | Error ex)
        {
            response = false;
        }
        finally
        {
            try
            {
                if (reader != null)
                {
                    reader.close();
                }
            }
            catch (Exception | Error ex)
            {
            }
            try
            {
                if (stream != null)
                {
                    stream.close();
                }
            }
            catch (Exception | Error ex)
            {
            }
        }

        return response;
    }

    public String value(String param)
    {
        String value = "";
        if (param != null && properties.containsKey(param))
        {
            Object instance = properties.get(param);
            if (instance != null && instance instanceof String)
            {
                value = (String) instance;
            }
        }
        return value;
    }

    public List<String> values(String param)
    {
        List<String> values = new ArrayList();
        if (param != null && properties.containsKey(param))
        {
            Object instance = properties.get(param);
            if (instance != null && instance instanceof List)
            {
                values = (List) instance;
            }
        }
        return values;
    }

    public List<String> names()
    {
        return new ArrayList<>(properties.keySet());
    }
}
