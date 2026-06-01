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
import com.assembly.service.domain.SchedulerTask;

/**
 *
 * @author administrator
 */
public class SchedulerDAO
{
    public List<SchedulerTask> fetchScheduler()
    {
        List<SchedulerTask> results = new ArrayList<>();
        ConnectorInterface database = null;

        try
        {
            String statement = "";
            statement += " SELECT ";
            statement += " scheduler.`id` AS idscheduler, ";
            statement += " scheduler.`scheduler` AS scheduler, ";
            statement += " scheduler.`quartz` AS quartz, ";
            statement += " scheduler.`counter` AS counter, ";
            statement += " scheduler.`status` AS status, ";
            statement += " scheduler.`exception` AS exception, ";
            statement += " scheduler.`executed` AS executed, ";
            statement += " scheduler.`disable` AS disable, ";
            statement += " scheduler.`record` AS record,";
            statement += " scheduler.`version` AS version ";

            statement += " FROM system_scheduler scheduler ";

            statement += " WHERE 1=1 ";
            statement += " AND scheduler.`disable` = 0 ";

            statement += " ORDER BY scheduler.`executed` ASC ";

            database = DBConnector.instance("ASSEMBLY");
            database.prepare(statement);

            ResultSet response = database.read();
            while (response.next())
            {
                SchedulerTask scheduler = new SchedulerTask();
                scheduler.setIDScheduler(response.getLong("idscheduler"));

                scheduler.setScheduler(response.getString("scheduler"));
                scheduler.setQuartz(response.getString("quartz"));
                scheduler.setCounter(response.getLong("counter"));
                scheduler.setStatus(response.getString("status"));
                scheduler.setException(response.getString("exception"));
                scheduler.setExecuted(response.getTimestamp("executed").getTime());
                scheduler.setDisable(response.getInt("disable"));
                scheduler.setRecord(response.getTimestamp("record").getTime());
                scheduler.setVersion(response.getTimestamp("version").getTime());

                results.add(scheduler);
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

    public void save(SchedulerTask scheduler)
    {
        ConnectorInterface database = null;

        try
        {
            String statement = "";
            statement += " UPDATE system_scheduler scheduler SET  ";
            statement += " scheduler.counter = scheduler.counter + 1, ";
            statement += " scheduler.`status` = :status, ";
            statement += " scheduler.exception = :exception, ";
            statement += " scheduler.executed = NOW(), ";
            statement += " scheduler.version = NOW() ";

            statement += " WHERE 1=1 ";
            statement += " AND scheduler.id = :idscheduler ";

            database = DBConnector.instance("ASSEMBLY");
            database.prepare(statement);

            database.bind("idscheduler", scheduler.getIDScheduler());
            database.bind("status", scheduler.getStatus());
            database.bind("exception", scheduler.getException());

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
