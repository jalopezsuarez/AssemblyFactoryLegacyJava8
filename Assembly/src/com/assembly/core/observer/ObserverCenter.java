package com.assembly.core.observer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.SwingUtilities;

public final class ObserverCenter implements Runnable
{

    private static final HashMap<Class<? extends ObserverQuery>, HashMap<Class<?>, ObserverInterface>> observables = new HashMap<Class<? extends ObserverQuery>, HashMap<Class<?>, ObserverInterface>>();
    private static final Collection<ObserverQuery> collection = new PriorityQueue<ObserverQuery>();

    // =======================================================
    private Thread observerThread;
    private ReentrantLock synchronize;

    // =======================================================
    private static final ObserverCenter INSTANCE = new ObserverCenter();

    public static ObserverCenter instance()
    {
        return INSTANCE;
    }

    // =======================================================
    private ObserverCenter()
    {
        observerThread = new Thread(this);
        observerThread.start();

        synchronize = new ReentrantLock(true);
    }

    public void addObserver(Class<? extends ObserverQuery> clazz, ObserverInterface observer)
    {
        if (!observables.containsKey(clazz))
        {
            observables.put(clazz, new HashMap());
        }

        HashMap<Class<?>, ObserverInterface> observable = observables.get(clazz);
        observable.put(observer.getClass(), observer);
    }

    public void removeObserver(Class<? extends ObserverQuery> clazz, ObserverInterface observer)
    {
        if (!observables.containsKey(clazz))
        {
            observables.put(clazz, new HashMap());
        }

        HashMap<Class<?>, ObserverInterface> observable = observables.get(clazz);
        observable.remove(observer.getClass());
    }

    // =======================================================
    public void observer(Class<? extends ObserverQuery> clazz, ObserverQuery query)
    {
        if (query != null && observables.containsKey(clazz))
        {
            query.observer(clazz);

            synchronize.lock();
            collection.add(query);
            synchronize.unlock();

            observerThread = new Thread(this);
            observerThread.start();
        }
    }

    public void observer(Class<? extends ObserverQuery> clazz)
    {
        ObserverCenter.this.observer(clazz, new ObserverQuery());
    }

    // =======================================================
    @Override
    public void run()
    {
        do
        {
            PriorityQueue<ObserverQuery> observerQueue = (PriorityQueue<ObserverQuery>) collection;

            synchronize.lock();
            ObserverQuery query = observerQueue.poll();
            synchronize.unlock();

            if (query != null && observables.containsKey(query.observer()))
            {
                Class<? extends ObserverQuery> clazz = query.observer();
                HashMap<Class<?>, ObserverInterface> observable = observables.get(clazz);
                if (observable != null && observable.size() > 0)
                {
                    for (Map.Entry<Class<?>, ObserverInterface> entry : observable.entrySet())
                    {
                        ObserverInterface observer = entry.getValue();
                        if (observer != null)
                        {
                            try
                            {
                                SwingUtilities.invokeLater(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        observer.observer(query);
                                    }
                                });
                            }
                            catch (Exception ex)
                            {
                            }
                        }
                    }
                }
            }

        }
        while (collection.size() > 0);
    }

}
