package com.assembly.core.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import javax.swing.SwingUtilities;

import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import com.assembly.core.trace.Trace;

public abstract class MonitorProtocol implements Runnable
{

    private final static HashMap<Class<?>, HashMap<Class<?>, MonitorInterface>> observables = new HashMap<Class<?>, HashMap<Class<?>, MonitorInterface>>();
    private final MonitorProtocol context = this;
    private final Class<? extends MonitorProtocol> clazz = getClass();

    private Thread thread;
    private String expression;

    private ExecutionTime executionTime;
    private ZonedDateTime lastExecution;

    public void monitorize(String cron, MonitorMode mode)
    {
        try
        {
            thread = null;
            expression = cron;

            CronParser cronParser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX));
            executionTime = ExecutionTime.forCron(cronParser.parse(expression));

            lastExecution = ZonedDateTime.now(ZoneId.of("Europe/Madrid"));
            if (mode == MonitorMode.IMMEDIATE)
            {
                lastExecution = ZonedDateTime.ofInstant(Instant.ofEpochSecond(0), ZoneId.of("Europe/Madrid"));
            }

            // -------------------------------------------------------
            MonitorScheduler.instance().schedule(this);
            // -------------------------------------------------------
        }
        catch (Exception ex)
        {
            Trace.printStackTrace(ex);
        }
    }

    public boolean isMonitor(Class<? extends MonitorProtocol> clazz)
    {
        return this.clazz == clazz;
    }

    @Override
    public void run()
    {
        try
        {
            lastExecution = ZonedDateTime.now(ZoneId.of("Europe/Madrid"));
            Object response = this.execute();

            HashMap<Class<?>, MonitorInterface> observable = observables.get(clazz);
            if (observable != null && observable.size() > 0)
            {
                for (Map.Entry<Class<?>, MonitorInterface> entry : observable.entrySet())
                {
                    MonitorInterface observer = entry.getValue();
                    try
                    {
                        SwingUtilities.invokeLater(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                observer.monitorize(context, response);
                            }
                        });
                    }
                    catch (Exception ex)
                    {
                        Trace.printStackTrace(ex);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            Trace.printStackTrace(ex);
        }
    }

    protected abstract Object execute() throws Exception;

    private boolean executionTime()
    {
        boolean response = false;

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Madrid"));
        Optional<ZonedDateTime> nextOptional = executionTime.lastExecution(now);

        if (nextOptional.isPresent())
        {
            ZonedDateTime nextExecution = nextOptional.get();
            if (nextExecution.isAfter(lastExecution) || nextExecution.isEqual(lastExecution))
            {
                response = true;
            }
        }

        return response;
    }

    // =======================================================   
    public static void addObserver(Class<?> clazz, MonitorInterface observer)
    {
        if (!observables.containsKey(clazz))
        {
            observables.put(clazz, new HashMap<>());
        }

        HashMap<Class<?>, MonitorInterface> observable = observables.get(clazz);
        observable.put(observer.getClass(), observer);
    }

    public static void removeObserver(Class<?> clazz, MonitorInterface observer)
    {
        if (!observables.containsKey(clazz))
        {
            observables.put(clazz, new HashMap<>());
        }

        HashMap<Class<?>, MonitorInterface> observable = observables.get(clazz);
        observable.remove(observer.getClass(), observer);
    }

    // =======================================================  
    private static class MonitorScheduler extends TimerTask
    {

        private static final MonitorScheduler INSTANCE = new MonitorScheduler();

        private static MonitorScheduler instance()
        {
            return INSTANCE;
        }

        private final ArrayList<MonitorProtocol> monitorize = new ArrayList<>();
        private final ThreadGroup threads = new ThreadGroup(MonitorScheduler.class.getSimpleName());
        private final Lock synchronize = new ReentrantLock(true);

        private MonitorScheduler()
        {
            Timer monitorScheduler = new Timer();
            monitorScheduler.schedule(this, 0, 1000);
        }

        @Override
        public void run()
        {
            if (synchronize.tryLock())
            {
                try
                {
                    for (MonitorProtocol monitor : monitorize)
                    {
                        if (monitor != null && monitor.executionTime())
                        {
                            if (monitor.thread == null || monitor.thread.getThreadGroup() == null)
                            {
                                monitor.thread = new Thread(threads, monitor);
                                monitor.thread.start();
                            }
                        }
                    }
                }
                finally
                {
                    synchronize.unlock();
                }
            }
        }

        private void schedule(MonitorProtocol monitor)
        {
            try
            {
                synchronize.lock();
                monitorize.add(monitor);
            }
            finally
            {
                synchronize.unlock();
            }
        }
    }

}
