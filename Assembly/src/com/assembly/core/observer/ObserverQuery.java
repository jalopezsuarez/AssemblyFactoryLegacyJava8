package com.assembly.core.observer;

public class ObserverQuery implements Comparable
{
    private Object data;

    private Class<? extends ObserverQuery> clazz;

    // =======================================================
    public ObserverQuery(Object data)
    {
        this.data = data;
    }

    public ObserverQuery()
    {
    }

    // =======================================================
    public void observer(Class<? extends ObserverQuery> clazz)
    {
        this.clazz = clazz;
    }

    public Class<? extends ObserverQuery> observer()
    {
        return clazz;
    }

    public boolean isObserver(Class<? extends ObserverQuery> clazz)
    {
        return this.clazz == clazz;
    }

    // =======================================================
    public Object getData()
    {
        return data;
    }

    // =======================================================
    @Override
    public int compareTo(Object o)
    {
        return 0;
    }

}
