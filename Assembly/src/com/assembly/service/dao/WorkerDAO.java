/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assembly.service.dao;

import com.assembly.core.trace.Trace;
import java.util.List;
import java.util.ArrayList;
import java.sql.ResultSet;

import com.assembly.core.source.db.ConnectorInterface;
import com.assembly.core.source.db.DBConnector;
import com.assembly.service.domain.WorkerTask;

/**
 *
 * @author administrator
 */
public class WorkerDAO
{
    public List<WorkerTask> fetchWorker()
    {
        List<WorkerTask> results = new ArrayList<>();
        ConnectorInterface database = null;

        try
        {
            String statement = "";
            statement += " SELECT ";
            statement += " worker.`id` AS idworker, ";
            statement += " worker.`worker` AS worker, ";
            statement += " worker.`poolsize` AS poolsize, ";
            statement += " worker.`counter` AS counter, ";
            statement += " worker.`status` AS status, ";
            statement += " worker.`exception` AS exception, ";
            statement += " worker.`executed` AS executed, ";
            statement += " worker.`disable` AS disable, ";
            statement += " worker.`record` AS record,";
            statement += " worker.`version` AS version ";

            statement += " FROM system_worker worker ";

            statement += " WHERE 1=1 ";
            statement += " AND worker.`disable` = 0 ";

            statement += " ORDER BY worker.`executed` ASC ";

            database = DBConnector.instance("ASSEMBLY");
            database.prepare(statement);

            ResultSet response = database.read();
            while (response.next())
            {
                WorkerTask worker = new WorkerTask();
                worker.setIDWorker(response.getLong("idworker"));

                worker.setWorker(response.getString("worker"));
                worker.setPoolsize(response.getInt("poolsize"));
                worker.setCounter(response.getLong("counter"));
                worker.setStatus(response.getString("status"));
                worker.setException(response.getString("exception"));
                worker.setExecuted(response.getTimestamp("executed").getTime());
                worker.setDisable(response.getInt("disable"));
                worker.setRecord(response.getTimestamp("record").getTime());
                worker.setVersion(response.getTimestamp("version").getTime());

                results.add(worker);
            }
        }
        catch (Exception ex)
        {
            Trace.printStackTrace(ex);
        }
        finally
        {
            try
            {
                if (database != null)
                {
                    database.dispose();
                }
            }
            catch (Exception ex)
            {
            }
        }

        return results;
    }

    public void save(WorkerTask worker)
    {
        ConnectorInterface database = null;
        try
        {
            String statement = "";
            statement += " UPDATE system_worker worker SET  ";
            statement += " worker.counter = worker.counter + 1, ";
            statement += " worker.`status` = :status, ";
            statement += " worker.exception = :exception, ";
            statement += " worker.executed = NOW(), ";
            statement += " worker.version = NOW() ";

            statement += " WHERE 1=1 ";
            statement += " AND worker.id = :idworker ";

            database = DBConnector.instance("ASSEMBLY");
            database.prepare(statement);

            database.bind("idworker", worker.getIDWorker());
            database.bind("status", worker.getStatus());
            database.bind("exception", worker.getException());

            database.write();
        }
        catch (Exception ex)
        {
            Trace.printStackTrace(ex);
        }
        finally
        {
            try
            {
                if (database != null)
                {
                    database.dispose();
                }
            }
            catch (Exception ex)
            {
            }
        }
    }

}
