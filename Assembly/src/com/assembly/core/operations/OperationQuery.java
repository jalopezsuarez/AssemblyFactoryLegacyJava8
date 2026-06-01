package com.assembly.core.operations;

public class OperationQuery implements Comparable
{

    private Object data;

    private Class<? extends OperationProtocol> clazz;

    // =======================================================
    public OperationQuery(Object data)
    {
        this.data = data;
    }

    public OperationQuery()
    {
    }

    // =======================================================
    public void operation(Class<? extends OperationProtocol> clazz)
    {
        this.clazz = clazz;
    }

    public boolean isOperation(Class<? extends OperationProtocol> clazz)
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
