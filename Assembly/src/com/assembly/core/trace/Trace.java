/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assembly.core.trace;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.varia.NullAppender;

/**
 *
 * @author administrator
 */
public class Trace
{
    private static boolean initialized = false;
    private static boolean traceable = false;

    private static boolean traceable()
    {
        if (!initialized)
        {
            initialized = true;

            // -------------------------------------------------------
            Logger.getRootLogger().removeAllAppenders();
            Logger.getRootLogger().addAppender(new NullAppender());
            Logger.getRootLogger().setLevel(Level.OFF);

            // -------------------------------------------------------
            try
            {
                Path path = FileSystems.getDefault().getPath("logs");
                traceable = Files.exists(path) && Files.isDirectory(path);
            }
            catch (Exception ex)
            {
            }

            // -------------------------------------------------------
            if (initialized && traceable)
            {
                Properties properties = new Properties();

                properties.setProperty("log4j.rootLogger", "DEBUG,console,file");
                properties.setProperty("log4j.rootCategory", "DEBUG");

                properties.setProperty("log4j.appender.console", "org.apache.log4j.ConsoleAppender");
                properties.setProperty("log4j.appender.console.layout", "org.apache.log4j.PatternLayout");
                properties.setProperty("log4j.appender.console.layout.ConversionPattern", "%d %-5p [%t] %m%n");

                properties.setProperty("log4j.appender.file", "org.apache.log4j.RollingFileAppender");
                properties.setProperty("log4j.appender.file.file", "logs/assembly.log");
                properties.setProperty("log4j.appender.file.maxFileSize", "32MB");
                properties.setProperty("log4j.appender.file.maxBackupIndex", "8");
                properties.setProperty("log4j.appender.file.append", "true");
                properties.setProperty("log4j.appender.file.layout", "org.apache.log4j.PatternLayout");
                properties.setProperty("log4j.appender.file.layout.ConversionPattern", "%d %-5p [%t] %m%n");

                PropertyConfigurator.configure(properties);
            }
        }

        return initialized && traceable;
    }

    public static void printStackTrace(Throwable exception)
    {
        if (traceable() && exception != null)
        {
            Logger logger = LogManager.getLogger(Trace.class);
            logger.debug(exception.getMessage(), exception);
        }
    }

    public static void printStackTrace(String exception)
    {
        if (traceable() && exception != null)
        {
            Logger logger = LogManager.getLogger(Trace.class);
            logger.debug(exception);
        }
    }

}
