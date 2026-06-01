package com.assembly.core.operations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.PriorityQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.SwingUtilities;

public abstract class OperationProtocol implements Runnable
{

    private OperationInterface operation;
    protected Collection<OperationQuery> collection;

    protected int maxOperations;
    protected int maxConcurrentOperations;

    private Lock synchronize;
    private ArrayList<Thread> operationThreads;

    // =================================================================
    public OperationProtocol(OperationInterface operation)
    {
        this.operation = operation;
        this.collection = new PriorityQueue<OperationQuery>();

        this.maxOperations = -1;
        this.maxConcurrentOperations = -1;
        this.synchronize = new ReentrantLock(true);
        this.operationThreads = new ArrayList<Thread>();
    }

    // =================================================================
    public abstract Object execute(OperationQuery query) throws Exception;

    public Object success(OperationQuery query, Object response)
    {
        return response;
    }

    public Object error(OperationQuery query, Throwable response)
    {
        return response;
    }

    // =================================================================
    @Override
    public void run()
    {
        do
        {
            PriorityQueue<OperationQuery> operationQueue = (PriorityQueue<OperationQuery>) collection;
            OperationQuery operationQuery = operationQueue.poll();

            try
            {
                Object operationResponse = execute(operationQuery);
                SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        Object response = success(operationQuery, operationResponse);
                        operation.operationSuccess(operationQuery, response);
                    }
                });
            }
            catch (Error | Exception ex)
            {
                SwingUtilities.invokeLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Object response = error(operationQuery, ex);
                        operation.operationError(operationQuery, response);
                    }
                });
            }
        }
        while (collection.size() > 0);

        try
        {
            synchronize.lock();
            operationThreads.remove(Thread.currentThread());
        }
        catch (Exception ex)
        {
        }
        finally
        {
            synchronize.unlock();
        }
    }

    // =================================================================
    public void addOperation(OperationQuery query)
    {
        try
        {
            synchronize.lock();
            if (maxOperations <= 0 || collection.size() < maxOperations)
            {
                PriorityQueue<OperationQuery> operationQueue = (PriorityQueue<OperationQuery>) collection;
                query.operation(this.getClass());
                operationQueue.add(query);

                while (operationThreads.size() < Math.max(maxConcurrentOperations, 1))
                {
                    Thread operationThread = new Thread(this);
                    operationThreads.add(operationThread);
                    operationThread.start();
                }
            }
        }
        catch (Exception ex)
        {
        }
        finally
        {
            synchronize.unlock();
        }
    }

    public void cancellAllOperations()
    {
        try
        {
            synchronize.lock();
            for (Thread operationThread : operationThreads)
            {
                try
                {
                    operationThread.interrupt();
                }
                catch (Exception ex)
                {
                }
            }
        }
        catch (Exception ex)
        {
        }
        finally
        {
            synchronize.unlock();
        }
    }

    public int size()
    {
        int size = -1;
        try
        {
            size = collection.size();
        }
        catch (Exception ex)
        {
            size = -1;
        }
        return size;
    }

    // =================================================================
    public int getMaxOperations()
    {
        return maxOperations;
    }

    public void setMaxOperations(int maxOperations)
    {
        this.maxOperations = maxOperations;
    }

    public int getMaxConcurrentOperations()
    {
        return maxConcurrentOperations;
    }

    public void setMaxConcurrentOperations(int maxConcurrentOperations)
    {
        this.maxConcurrentOperations = maxConcurrentOperations;
    }
}
