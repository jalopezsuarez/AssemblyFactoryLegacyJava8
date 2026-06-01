/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assembly.service.notifications.service;

import java.util.List;
import java.util.ArrayList;
import java.sql.ResultSet;

import com.assembly.service.notifications.domain.Queue;
import com.assembly.core.source.db.ConnectorInterface;
import com.assembly.core.source.db.DBConnector;
import com.assembly.core.source.db.Transaction;
import com.assembly.core.trace.Trace;

/**
 *
 * @author administrator
 */
public class QueueService
{
    public final static int QUEUE_PURGE_DAYS = 16;
    public final static int QUEUE_PROCESS_INTENTS = 32;
    public final static int QUEUE_INTENTS_MINUTES = 4;

    public void pushQueue(List<Queue> queues)
    {
        ConnectorInterface database = null;

        try
        {
            String statement = " ";
            statement += " INSERT INTO system_notifications_queue ( ";

            statement += " resource, ";
            statement += " id_resource, ";
            statement += " username, ";
            statement += " reference, ";
            statement += " summary, ";
            statement += " message, ";
            statement += " priorize, ";
            statement += " exclusions ";

            statement += " ) VALUES ( ";

            statement += " :resource, ";
            statement += " :id_resource, ";
            statement += " :username, ";
            statement += " :reference, ";
            statement += " :summary,";
            statement += " :message,";
            statement += " :priorize, ";
            statement += " :exclusions ";

            statement += " ) ON DUPLICATE KEY UPDATE ";
            statement += " system_notifications_queue.version = NOW(); ";

            database = DBConnector.instance("ASSEMBLY");
            database.prepare(statement);
            database.transaction(Transaction.BEGIN);

            for (Queue queue : queues)
            {
                database.bind("resource", queue.getResource());
                database.bind("id_resource", queue.getIDResource());
                database.bind("username", queue.getUsername());
                database.bind("reference", queue.getReference());
                database.bind("summary", queue.getSummary());
                database.bind("message", queue.getMessage());
                database.bind("priorize", queue.getPriorize());
                database.bind("exclusions", queue.getExclusions());

                database.write();
            }

            database.transaction(Transaction.COMMIT);
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

        // =======================================================
        // =======================================================
        try
        {
            String statement = " ";
            statement += " DELETE FROM system_notifications_queue ";
            statement += " WHERE system_notifications_queue.record < NOW() - INTERVAL " + QueueService.QUEUE_PURGE_DAYS + " DAY; ";

            database = DBConnector.instance("ASSEMBLY");
            database.prepare(statement);
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

    public List<Queue> fetchQueue()
    {
        List<Queue> results = new ArrayList<>();
        ConnectorInterface database = null;

        // =======================================================
        // =======================================================
        try
        {
            String statement = " ";
            statement += " UPDATE system_notifications_queue ";
            statement += " SET system_notifications_queue.`status` = " + Queue.STATUS_PENDING + " ";
            statement += " WHERE 1=1 ";
            statement += " AND system_notifications_queue.intents < :intents ";
            statement += " AND system_notifications_queue.`status` <> " + Queue.STATUS_COMPLETED + " ";
            statement += " AND system_notifications_queue.`status` <> " + Queue.STATUS_PROGRESS + " ";
            statement += " AND TIMESTAMPDIFF(minute, system_notifications_queue.last_status, NOW()) > :minutes ";

            database = DBConnector.instance("ASSEMBLY");
            database.prepare(statement);
            database.bind("intents", QueueService.QUEUE_PROCESS_INTENTS);
            database.bind("minutes", QueueService.QUEUE_INTENTS_MINUTES);
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

        // =======================================================
        // =======================================================
        try
        {
            String statement = " ";
            statement += " UPDATE system_notifications_queue ";
            statement += " SET system_notifications_queue.`status` = " + Queue.STATUS_QUEUED + " ";
            statement += " WHERE 1=1 ";
            statement += " AND system_notifications_queue.intents < :intents ";
            statement += " AND system_notifications_queue.`status` = :status ";

            database = DBConnector.instance("ASSEMBLY");
            database.prepare(statement);
            database.bind("intents", QueueService.QUEUE_PROCESS_INTENTS);
            database.bind("status", Queue.STATUS_PENDING);
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

        // =======================================================
        // =======================================================
        try
        {
            String statement = " SELECT ";

            statement += " system_notifications_queue.resource, ";
            statement += " system_notifications_queue.id_resource, ";
            statement += " system_notifications_queue.username, ";

            statement += " system_notifications_queue.reference, ";
            statement += " system_notifications_queue.summary, ";
            statement += " system_notifications_queue.message, ";

            statement += " system_notifications_queue.review, ";
            statement += " system_notifications_queue.intents, ";
            statement += " system_notifications_queue.delivery, ";

            statement += " system_notifications_queue.priorize, ";
            statement += " system_notifications_queue.exclusions, ";

            statement += " system_notifications_queue.`status`, ";
            statement += " system_notifications_queue.last_status, ";
            statement += " system_notifications_queue.record, ";
            statement += " system_notifications_queue.version ";

            statement += " FROM system_notifications_queue ";

            statement += " WHERE 1=1 ";
            statement += " AND system_notifications_queue.intents < :intents ";
            statement += " AND system_notifications_queue.`status` = :status ";

            statement += " ORDER BY ";
            statement += " system_notifications_queue.intents DESC, ";
            statement += " system_notifications_queue.version ASC, ";
            statement += " system_notifications_queue.record ASC ";

            database = DBConnector.instance("ASSEMBLY");
            database.prepare(statement);
            database.bind("intents", QueueService.QUEUE_PROCESS_INTENTS);
            database.bind("status", Queue.STATUS_QUEUED);
            database.read();

            ResultSet response = database.read();
            while (response.next())
            {
                Queue queue = new Queue();
                queue.setResource(response.getLong("resource"));
                queue.setIDResource(response.getString("id_resource"));
                queue.setUsername(response.getString("username"));

                queue.setReference(response.getString("reference"));
                queue.setSummary(response.getString("summary"));
                queue.setMessage(response.getString("message"));

                queue.setReview(response.getBoolean("review"));
                queue.setIntents(response.getLong("intents"));
                queue.setDelivery(response.getString("delivery"));

                queue.setPriorize(response.getInt("priorize"));
                queue.setExclusions(response.getString("exclusions"));

                queue.setStatus(response.getInt("status"));
                queue.setLastStatus(response.getTimestamp("last_status").getTime());
                queue.setRecord(response.getTimestamp("record").getTime());
                queue.setVersion(response.getTimestamp("version").getTime());

                results.add(queue);
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

    public void updateQueue(Queue queue)
    {
        ConnectorInterface database = null;

        try
        {
            long resource = queue.getResource();
            String IDResource = queue.getIDResource();
            String username = queue.getUsername();

            String delivery = queue.getDelivery();
            int status = queue.getStatus();

            String statement = " ";
            statement += " UPDATE system_notifications_queue ";
            statement += " SET system_notifications_queue.last_status = NOW() ";

            statement += " , system_notifications_queue.intents = system_notifications_queue.intents + 1 ";
            statement += " , system_notifications_queue.delivery = :delivery ";
            statement += " , system_notifications_queue.`status` = :status ";

            statement += " WHERE 1=1 ";
            statement += " AND system_notifications_queue.resource = :resource ";
            statement += " AND system_notifications_queue.id_resource = :idresource ";
            statement += " AND system_notifications_queue.username = :username ";

            database = DBConnector.instance("ASSEMBLY");
            database.prepare(statement);

            database.bind("delivery", delivery);
            database.bind("status", status);
            database.bind("resource", resource);
            database.bind("idresource", IDResource);
            database.bind("username", username);

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
