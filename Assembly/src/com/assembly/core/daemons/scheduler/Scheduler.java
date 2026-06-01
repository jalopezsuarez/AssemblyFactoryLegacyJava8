/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assembly.core.daemons.scheduler;

import com.assembly.core.secure.EncryptionManager;
import com.assembly.core.source.db.ConnectorInterface;
import com.assembly.core.source.db.DBConnector;
import com.assembly.core.secure.Encryption;
import com.assembly.core.trace.Trace;

/**
 *
 * @author administrator
 */
public abstract class Scheduler
{
    public final static String STATUS_OK = "OK";
    public final static String STATUS_ERROR = "ERROR";

    public Scheduler()
    {
        ConnectorInterface database = null;

        String status = Scheduler.STATUS_OK;
        String response = null;
        double duration = System.currentTimeMillis();

        try
        {
            this.cron();
        }
        catch (Exception ex)
        {
            status = Scheduler.STATUS_ERROR;
            response = ex.getMessage() + ex.toString();
        }

        try
        {
            String reference = EncryptionManager.instance().encode(this.getClass().getCanonicalName(), Encryption.MD5);
            String scheduler = this.getClass().getCanonicalName();

            String statement = " ";
            statement += " INSERT INTO system_cron (`id`, `scheduler`, `counter`, `status`, `response`, `duration`, `version`) ";
            statement += " VALUES (:id, :scheduler, 1, :status, :response, :duration, NOW()) ";
            statement += " ON DUPLICATE KEY UPDATE ";
            statement += " `counter` = counter + 1, ";
            statement += " `status` = :status, ";
            statement += " `response` = :response, ";
            statement += " `duration` = :duration, ";
            statement += " `interval` = TIMESTAMPDIFF(SECOND,`version`,NOW()), ";
            statement += " `version` = NOW() ";

            database = DBConnector.instance("ASSEMBLY");
            database.prepare(statement);

            database.bind("id", reference);
            database.bind("scheduler", scheduler);
            database.bind("status", status);
            database.bind("response", response);
            database.bind("duration", (System.currentTimeMillis() - duration) / 1000);

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

    protected abstract void cron() throws Exception;
}
