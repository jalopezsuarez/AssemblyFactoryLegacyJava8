package com.assembly.core.source.db;

import com.assembly.core.config.ConfigManager;
import com.assembly.core.config.DatabaseSession;
import com.assembly.core.source.db.exception.DatabaseNotFoundException;
import com.assembly.core.trace.Trace;

public class DBConnector
{
    public static ConnectorInterface instance(String source)
    {
        ConnectorInterface connector = null;

        if (ConfigManager.instance().database(source) != null)
        {
            DatabaseSession session = ConfigManager.instance().database(source);
            if (session.getAdapter().equals(DriverAdapter.SQLITE))
            {
                connector = new SQLiteConnector(session);
            }
            else if (session.getAdapter().equals(DriverAdapter.MYSQL))
            {
                connector = new MysqlConnector(session);
            }
            else if (session.getAdapter().equals(DriverAdapter.MSSQL))
            {
                connector = new SQLServerConnector(session);
            }
        }

        if (connector == null)
        {
            try
            {
                String exception = "";
                exception += "Database alias or database name \"" + source.trim().toUpperCase() + "\" could not be found.";
                throw new DatabaseNotFoundException(exception);
            }
            catch (Exception | Error ex)
            {
                Trace.printStackTrace(ex);
            }
        }

        return connector;
    }

    public static String schema(String source)
    {
        return schema(source, null);
    }

    public static String schema(String source, String table)
    {
        String schema = "";
        DatabaseSession session = ConfigManager.instance().database(source);
        if (session != null && session.getSchema() != null && !session.getSchema().trim().isEmpty())
        {
            schema = " " + session.getSchema().trim() + " ";
        }
        if (session != null && table != null && !table.trim().isEmpty())
        {
            schema = " " + schema.trim() + "." + table.trim() + " ";
        }
        return schema;
    }
}
