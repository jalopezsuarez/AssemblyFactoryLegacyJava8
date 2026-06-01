/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assembly.core.preferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Properties;

import com.assembly.core.commons.FileHelper;
import com.assembly.core.trace.Trace;

/**
 *
 * @author Administrator
 */
public class Preferences
{
    private static final String ExtensionResources = ".ini";
    private static final HashMap<String, PreferencesProtocol> perferences = new HashMap<String, PreferencesProtocol>();

    public static <T extends PreferencesProtocol> T read(Class<T> clazz)
    {
        PreferencesProtocol preferences = null;

        InputStream inputStream = null;
        OutputStream outputStream = null;
        Properties properties = null;

        try
        {
            if (!perferences.containsKey(clazz.getSimpleName()))
            {
                preferences = clazz.newInstance();

                // -------------------------------------------------------                
                String resourcePreferences = FileHelper.buildResource(FileHelper.currentDirectory(), clazz.getSimpleName()) + ExtensionResources;
                
                File resource = new File(resourcePreferences);
                if (!resource.exists() && !resource.isDirectory())
                {
                    resource.createNewFile();
                }

                // =======================================================
                inputStream = new FileInputStream(resource);
                properties = new Properties();
                properties.load(inputStream);
                // =======================================================

                boolean changesProperties = false;

                for (Field field : clazz.getDeclaredFields())
                {
                    field.setAccessible(true);
                    String preference = properties.getProperty(field.getName());
                    Object assignable = null;

                    Class<?> type = field.getType();
                    if (Boolean.class.equals(type) || boolean.class.equals(type))
                    {
                        try
                        {
                            assignable = Boolean.parseBoolean(preference);
                        }
                        catch (Exception ex)
                        {
                            assignable = false;
                        }
                    }
                    else if (Integer.class.equals(type) || int.class.equals(type))
                    {
                        try
                        {
                            assignable = Integer.parseInt(preference);
                        }
                        catch (Exception ex)
                        {
                            assignable = 0;
                        }
                    }
                    else if (Long.class.equals(type) || long.class.equals(type))
                    {
                        try
                        {
                            assignable = Long.parseLong(preference);
                        }
                        catch (Exception ex)
                        {
                            assignable = 0L;
                        }
                    }
                    else if (Float.class.equals(type) || float.class.equals(type))
                    {
                        try
                        {
                            assignable = Float.parseFloat(preference);
                        }
                        catch (Exception ex)
                        {
                            assignable = 0.0f;
                        }
                    }
                    else if (Double.class.equals(type) || double.class.equals(type))
                    {
                        try
                        {
                            assignable = Double.parseDouble(preference);
                        }
                        catch (Exception ex)
                        {
                            assignable = 0.0;
                        }
                    }
                    else if (String.class.equals(type))
                    {
                        if (preference != null)
                        {
                            assignable = preference;
                        }
                        else
                        {
                            assignable = "";
                        }
                    }

                    if (assignable != null)
                    {
                        field.set(preferences, assignable);
                        if (preference == null)
                        {
                            properties.setProperty(field.getName(), String.valueOf(assignable));
                            changesProperties = true;
                        }
                    }
                }

                // -------------------------------------------------------
                if (changesProperties)
                {
                    outputStream = new FileOutputStream(resource);
                    properties.store(outputStream, null);
                }

                // -------------------------------------------------------
                perferences.put(clazz.getSimpleName(), preferences);
            }
            else
            {
                preferences = perferences.get(clazz.getSimpleName());
            }
        }
        catch (Exception | Error ex)
        {
            Trace.printStackTrace(ex);
        }
        finally
        {
            try
            {
                inputStream.close();
            }
            catch (Exception | Error ex)
            {
            }
            try
            {
                outputStream.close();
            }
            catch (Exception | Error ex)
            {
            }
        }

        return (T) preferences;
    }

    public static void write(PreferencesProtocol preferences)
    {
        boolean storageProperties = false;
        try
        {
            Class<? extends PreferencesProtocol> clazz = preferences.getClass();
            if (perferences.containsKey(clazz.getSimpleName()))
            {
                PreferencesProtocol instance = perferences.get(clazz.getSimpleName());
                for (Field field : clazz.getDeclaredFields())
                {
                    field.setAccessible(true);
                    Object preferencesValue = (Object) field.get(preferences);
                    Object instanceValue = (Object) field.get(instance);
                    storageProperties = preferencesValue != null && !preferencesValue.equals(instanceValue);
                    if (storageProperties)
                    {
                        break;
                    }
                }
            }
            else
            {
                storageProperties = true;
            }
        }
        catch (Exception | Error ex)
        {
            Trace.printStackTrace(ex);
        }

        if (storageProperties)
        {
            OutputStream outputStream = null;
            Properties properties = null;

            try
            {
                Class<? extends PreferencesProtocol> clazz = preferences.getClass();

                // -------------------------------------------------------
                String resourcePreferences = FileHelper.buildResource(FileHelper.currentDirectory(), clazz.getSimpleName()) + ExtensionResources;
                
                File resource = new File(resourcePreferences);
                if (!resource.exists() && !resource.isDirectory())
                {
                    resource.createNewFile();
                }

                // -------------------------------------------------------
                properties = new Properties();
                for (Field field : clazz.getDeclaredFields())
                {
                    field.setAccessible(true);
                    Class<?> type = field.getType();
                    boolean assignable = Boolean.class.equals(type) || boolean.class.equals(type) || Integer.class.equals(type) || int.class.equals(type) || Long.class.equals(type) || long.class.equals(type) || Float.class.equals(type) || float.class.equals(type) || Double.class.equals(type) || double.class.equals(type) || String.class.equals(type);
                    if (assignable)
                    {
                        properties.setProperty(field.getName(), String.valueOf(field.get(preferences)));
                    }
                }

                // =======================================================
                outputStream = new FileOutputStream(resource);
                properties.store(outputStream, null);
                // =======================================================

                perferences.put(clazz.getSimpleName(), preferences);
            }
            catch (Exception | Error ex)
            {
                Trace.printStackTrace(ex);
            }
            finally
            {
                try
                {
                    if (outputStream != null)
                    {
                        outputStream.close();
                    }
                }
                catch (Exception | Error ex)
                {
                }
            }
        }
    }

}
