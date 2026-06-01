package com.assembly.core.source.db;

import java.sql.Connection;
import java.sql.ResultSet;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;

import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

import com.assembly.core.config.DatabaseSession;
import com.assembly.core.trace.Trace;

public class SQLiteConnector implements ConnectorInterface
{

    private Connection connection;
    private NamedParameterStatement statement;
    private ResultSet reader;
    private boolean identity;

    public SQLiteConnector(DatabaseSession session)
    {
        try
        {
            Class.forName("org.sqlite.JDBC").newInstance();

            SQLiteDataSource connectionBuilder = new SQLiteDataSource();
            connectionBuilder.setEncoding("UTF-8");
            connectionBuilder.setUrl("jdbc:sqlite:" + session.getAlias().toLowerCase() + ".db");
            connectionBuilder.setDatabaseName(session.getDbname());
            SQLiteConfig sqliteConfig = new SQLiteConfig();
            sqliteConfig.setReadOnly(false);
            connectionBuilder.setConfig(sqliteConfig);

            connection = connectionBuilder.getConnection(session.getUsername(), session.getPassword());
        }
        catch (Exception | Error ex)
        {
            Trace.printStackTrace(ex);
        }
    }

    @Override
    protected void finalize() throws Throwable
    {
        super.finalize();
        dispose();
    }

    @Override
    public void prepare(String query, boolean identity) throws Exception
    {
        try
        {
            if (reader != null)
            {
                reader.close();
            }
        }
        catch (Exception | Error ex)
        {
        }
        finally
        {
            reader = null;
        }
        try
        {
            if (statement != null)
            {
                statement.close();
            }
        }
        catch (Exception | Error ex)
        {
        }
        finally
        {
            statement = null;
        }

        this.identity = identity;
        statement = new NamedParameterStatement(connection, query);
    }

    @Override
    public void prepare(String query) throws Exception
    {
        prepare(query, false);
    }

    @Override
    public void bind(String parameter, Object value)
    {
        try
        {
            statement.setObject(parameter, value);
        }
        catch (Exception | Error ex)
        {
            Trace.printStackTrace(ex);
        }
    }

    @Override
    public ResultSet read() throws Exception
    {
        try
        {
            if (reader != null)
            {
                reader.close();
            }
        }
        catch (Exception | Error ex)
        {
        }
        finally
        {
            reader = null;
        }

        reader = statement.executeQuery();
        return reader;
    }

    @Override
    public ResultSet read(String query) throws Exception
    {
        prepare(query);
        return read();
    }

    @Override
    public CachedRowSet cache() throws Exception
    {
        try
        {
            if (reader != null)
            {
                reader.close();
            }
        }
        catch (Exception | Error ex)
        {
        }
        finally
        {
            reader = null;
        }

        reader = statement.executeQuery();
        CachedRowSet response = RowSetProvider.newFactory().createCachedRowSet();
        response.populate(reader);
        reader.close();
        reader = null;

        return response;
    }

    @Override
    public CachedRowSet cache(String query) throws Exception
    {
        prepare(query);
        return cache();
    }

    @Override
    public Object write() throws Exception
    {
        try
        {
            if (reader != null)
            {
                reader.close();
            }
        }
        catch (Exception | Error ex)
        {
        }
        finally
        {
            reader = null;
        }

        Object execute = -1;
        int affected = statement.executeUpdate();
        if (this.identity && affected > 0)
        {
            reader = statement.getGeneratedKeys();
            if (reader.next())
            {
                execute = reader.getObject(1);
            }
            reader.close();
            reader = null;
        }

        return execute;
    }

    @Override
    public Object write(String query, boolean identity) throws Exception
    {
        prepare(query, identity);
        return write();
    }

    @Override
    public Object write(String query) throws Exception
    {
        prepare(query, false);
        return write();
    }

    @Override
    public void transaction(Transaction mode)
    {
    }

    @Override
    public void dispose()
    {
        try
        {
            if (reader != null)
            {
                reader.close();
            }
        }
        catch (Exception | Error ex)
        {
        }
        finally
        {
            reader = null;
        }
        try
        {
            if (statement != null)
            {
                statement.close();
            }
        }
        catch (Exception | Error ex)
        {
        }
        finally
        {
            statement = null;
        }
        try
        {
            if (connection != null)
            {
                connection.close();
            }
        }
        catch (Exception | Error ex)
        {
        }
        finally
        {
            connection = null;
        }
    }

}
