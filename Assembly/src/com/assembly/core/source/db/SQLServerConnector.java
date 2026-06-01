package com.assembly.core.source.db;

import java.util.HashMap;
import java.sql.Connection;
import java.sql.ResultSet;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.commons.dbcp2.PoolingDataSource;

import com.assembly.core.config.DatabaseSession;
import com.assembly.core.config.PoolAdapter;
import com.assembly.core.config.ConfigManager;
import com.assembly.core.trace.Trace;

public class SQLServerConnector implements ConnectorInterface
{

    private static final HashMap<String, PoolingDataSource<PoolableConnection>> cluster = new HashMap();

    private Connection connection;
    private NamedParameterStatement statement;
    private ResultSet reader;
    private boolean identity;

    public SQLServerConnector(DatabaseSession session)
    {
        if (ConfigManager.instance().pool(session.getAdapter()) != null)
        {
            try
            {
                if (!cluster.containsKey(session.getAlias()))
                {
                    Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();

                    String serverURL = "jdbc:sqlserver://;";
                    serverURL += "serverName=" + session.getServer() + ";";
                    serverURL += "instanceName=" + session.getInstance() + ";";
                    serverURL += "user=" + session.getUsername() + ";";
                    serverURL += "password=" + session.getPassword() + ";";
                    serverURL += "databaseName=" + session.getDbname() + ";";
                    serverURL += "portNumber=" + session.getPort() + ";";

                    PoolAdapter adapter = ConfigManager.instance().pool(session.getAdapter());

                    GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
                    poolConfig.setMaxTotal(adapter.getMaximum());
                    poolConfig.setMaxIdle(adapter.getIdle());
                    poolConfig.setMinIdle(adapter.getMinimum());

                    ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(serverURL);
                    PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);
                    GenericObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<>(poolableConnectionFactory);
                    connectionPool.setConfig(poolConfig);
                    poolableConnectionFactory.setPool(connectionPool);
                    PoolingDataSource<PoolableConnection> pool = new PoolingDataSource(connectionPool);
                    connection = pool.getConnection();

                    cluster.put(session.getAlias(), pool);
                }
                else
                {
                    PoolingDataSource<PoolableConnection> pool = cluster.get(session.getAlias());
                    connection = pool.getConnection();
                }
            }
            catch (Exception | Error ex)
            {
                Trace.printStackTrace(ex);
            }
        }
        else
        {
            try
            {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();

                SQLServerDataSource connectionBuilder = new SQLServerDataSource();
                connectionBuilder.setServerName(session.getServer());
                connectionBuilder.setInstanceName(session.getInstance());
                connectionBuilder.setUser(session.getUsername());
                connectionBuilder.setPassword(session.getPassword());
                connectionBuilder.setDatabaseName(session.getDbname());
                connectionBuilder.setPortNumber(session.getPort());

                connection = connectionBuilder.getConnection();
            }
            catch (Exception | Error ex)
            {
                Trace.printStackTrace(ex);
            }
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
        catch (Exception ex)
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
        catch (Exception ex)
        {
        }
        finally
        {
            statement = null;
        }

        this.identity = identity;
        query += identity ? " ; SELECT SCOPE_IDENTITY() ; " : " ";
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
        catch (Exception ex)
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
        catch (Exception ex)
        {
        }
        finally
        {
            reader = null;
        }

        CachedRowSet response = null;
        reader = statement.executeQuery();
        response = RowSetProvider.newFactory().createCachedRowSet();
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
        catch (Exception ex)
        {
        }
        finally
        {
            reader = null;
        }

        Object execute = -1;
        int affected = statement.executeUpdate();
        if (identity && affected > 0)
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
        catch (Exception ex)
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
        catch (Exception ex)
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
        catch (Exception ex)
        {
        }
        finally
        {
            connection = null;
        }
    }
}
