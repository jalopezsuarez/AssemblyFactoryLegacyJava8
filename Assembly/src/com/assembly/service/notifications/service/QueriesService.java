/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assembly.service.notifications.service;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.sql.ResultSet;
import java.sql.Timestamp;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

import com.assembly.service.notifications.domain.Query;
import com.assembly.service.notifications.domain.Queue;
import com.assembly.core.source.service.ProviderService;
import com.assembly.core.source.db.ConnectorInterface;
import com.assembly.core.source.db.DBConnector;
import com.assembly.core.trace.Trace;

/**
 *
 * @author administrator
 */
public class QueriesService
{
    public final static String PARAMS_ID = "id";
    public final static String PARAMS_USERNAME = "username";

    public final static String ANNOUNCE_DIRECTED = "DIRECTED";
    public final static String ANNOUNCE_BROADCAST = "BROADCAST";

    private static final CronParser cronParser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX));

    public ArrayList<Query> fetchScheduler()
    {
        ArrayList<Query> scheduler = new ArrayList<>();
        ConnectorInterface database = null;

        String statement = " ";
        statement += " SELECT ";

        statement += " queries.`id` AS `id`, ";
        statement += " queries.`instance` AS `instance`, ";
        statement += " queries.`reference` AS `reference`, ";
        statement += " queries.`summary` AS `summary`, ";
        statement += " queries.`layout` AS `layout`, ";
        statement += " queries.`query` AS `query`, ";
        statement += " queries.`database` AS `database`, ";
        statement += " queries.`announce` AS `announce`, ";
        statement += " queries.`schedule` AS `schedule`, ";
        statement += " queries.`priorize` AS `priorize`, ";
        statement += " queries.`exclusions` AS `exclusions`, ";
        statement += " queries.`last_status` AS `last_status`, ";
        statement += " queries.`success` AS `success`, ";
        statement += " queries.`exception` AS `exception`, ";
        statement += " queries.`disable` AS `disable`, ";
        statement += " queries.`record` AS `record`, ";
        statement += " queries.`version` AS `version` ";

        statement += " FROM service_notifications queries ";

        statement += " WHERE 1=1 ";
        statement += " AND queries.`disable` = 0 ";

        statement += " ORDER BY queries.last_status ASC ";

        try
        {
            database = DBConnector.instance("ASSEMBLY");
            database.prepare(statement);

            ResultSet response = database.read();
            while (response.next())
            {
                try
                {
                    String expression = response.getString("schedule");
                    Cron cronExpression = cronParser.parse(expression);
                    if (expression != null && cronExpression != null && cronExpression.validate() != null)
                    {
                        Timestamp previousTime = response.getTimestamp("last_status");
                        ZonedDateTime previousExecution = ZonedDateTime.ofInstant(previousTime.toInstant(), ZoneId.of("Europe/Madrid"));
                        ExecutionTime executionTime = ExecutionTime.forCron(cronExpression);

                        Optional<ZonedDateTime> nextContainer = executionTime.nextExecution(previousExecution);
                        if (nextContainer.isPresent())
                        {
                            ZonedDateTime nextExecution = nextContainer.get();
                            if (nextExecution.isBefore(ZonedDateTime.now(ZoneId.of("Europe/Madrid"))))
                            {
                                Query notification = new Query();

                                notification.setIDQuery(response.getLong("id"));
                                notification.setInstance(response.getString("instance"));
                                notification.setReference(response.getString("reference"));
                                notification.setSummary(response.getString("summary"));
                                notification.setLayout(response.getString("layout"));
                                notification.setQuery(response.getString("query"));
                                notification.setDatabase(response.getString("database"));
                                notification.setAnnounce(response.getString("announce"));
                                notification.setScheduler(response.getString("schedule"));
                                notification.setPriorize(response.getInt("priorize"));
                                notification.setExclusions(response.getString("exclusions"));
                                notification.setLastStatus(response.getTimestamp("last_status").getTime());
                                notification.setSuccess(response.getBoolean("success"));
                                notification.setException(response.getString("exception"));
                                notification.setDisable(response.getBoolean("disable"));
                                notification.setRecord(response.getTimestamp("record").getTime());
                                notification.setVersion(response.getTimestamp("version").getTime());

                                scheduler.add(notification);
                            }
                        }
                    }
                }
                catch (Exception | Error ex)
                {
                    Trace.printStackTrace(ex);
                }
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

        return scheduler;
    }

    public List<Queue> executeScheduler(Query query)
    {
        String exception = "";
        boolean success = true;

        ConnectorInterface database = null;
        List<Queue> response = new ArrayList<>();

        try
        {
            String queryQuery = ProviderService.schemeQuery(query.getQuery());
            String queryDatabase = query.getDatabase();
            String queryAnnounce = query.getAnnounce();

            database = DBConnector.instance(queryDatabase);
            ResultSet rowdata = database.read(queryQuery);

            while (rowdata.next())
            {
                String IDQueue = rowdata.getString(QueriesService.PARAMS_ID);

                // =======================================================
                HashMap<String, String> context = new HashMap();
                for (int i = 1; i <= rowdata.getMetaData().getColumnCount(); i++)
                {
                    context.put(rowdata.getMetaData().getColumnLabel(i), rowdata.getObject(i).toString());
                }

                String summaryTemplate = query.getSummary();
                Template summaryRenderize = Mustache.compiler().compile(summaryTemplate);
                String queueSummary = summaryRenderize.execute(context);

                String layoutTemplate = query.getLayout();
                Template layoutRenderize = Mustache.compiler().compile(layoutTemplate);
                String queueLayout = layoutRenderize.execute(context);
                // =======================================================

                if (queryAnnounce != null && queryAnnounce.compareToIgnoreCase(QueriesService.ANNOUNCE_DIRECTED) == 0)
                {
                    String queueUsername = rowdata.getString(QueriesService.PARAMS_USERNAME);

                    Queue queue = new Queue();
                    queue.setResource(query.getIDQuery());
                    queue.setIDResource(IDQueue);
                    queue.setUsername(queueUsername);
                    queue.setReference(query.getReference());
                    queue.setSummary(queueSummary);
                    queue.setMessage(queueLayout);
                    queue.setPriorize(query.getPriorize());
                    queue.setExclusions(query.getExclusions());
                    response.add(queue);
                }
                else if (queryAnnounce != null && queryAnnounce.compareToIgnoreCase(QueriesService.ANNOUNCE_BROADCAST) == 0)
                {
                    for (String username : broadcast())
                    {
                        Queue queue = new Queue();
                        queue.setResource(query.getIDQuery());
                        queue.setIDResource(IDQueue);
                        queue.setUsername(username);
                        queue.setReference(query.getReference());
                        queue.setSummary(queueSummary);
                        queue.setMessage(queueLayout);
                        queue.setPriorize(query.getPriorize());
                        queue.setExclusions(query.getExclusions());
                        response.add(queue);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            Trace.printStackTrace(ex);

            exception = ex.getMessage();
            success = false;
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
            statement += " UPDATE service_notifications queries ";
            statement += " SET queries.last_status = NOW(), ";
            statement += " queries.success = :success, ";
            statement += " queries.exception = :exception ";
            statement += " WHERE 1=1 ";
            statement += " AND queries.id = :idquery ";

            database = DBConnector.instance("ASSEMBLY");
            database.prepare(statement);

            database.bind("idquery", query.getIDQuery());
            database.bind("success", success);
            database.bind("exception", exception);

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

        return response;
    }

    private ArrayList<String> broadcast()
    {
        ArrayList<String> response = new ArrayList<>();
        ConnectorInterface database = null;

        String statement = " ";
        statement += " SELECT ";
        statement += " system_session.username AS username ";
        statement += " FROM system_session ";
        statement += " WHERE 1=1 ";
        statement += " AND system_session.username IS NOT NULL ";
        statement += " ORDER BY system_session.version DESC ";

        try
        {
            database = DBConnector.instance("ASSEMBLY");
            database.prepare(statement);

            ResultSet rowdata = database.read();
            while (rowdata.next())
            {
                String username = rowdata.getString("username");
                if (username != null && username.length() > 0)
                {
                    response.add(username);
                }
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

        return response;
    }
}
