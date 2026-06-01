/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assembly.core.source.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import org.sqlite.SQLiteConfig;

import com.assembly.core.source.db.NamedParameterStatement;
import com.assembly.core.source.cache.Cache.CacheType;
import com.assembly.core.source.cache.Cache.CacheStorage;
import com.assembly.core.trace.Trace;

/**
 *
 * @author Administrator
 */
public class CacheManager
{

    private final static HashMap<Class<?>, CacheStream> INSTANCES = new HashMap();

    public static <T extends CacheInterface> CacheStream<T> cache(Class<T> clazz)
    {
        if (!INSTANCES.containsKey(clazz))
        {
            INSTANCES.put(clazz, new CacheStream(clazz));
        }
        return INSTANCES.get(clazz);
    }

    private CacheManager()
    {
    }

    public static class CacheStream<T extends CacheInterface>
    {

        private Connection connection;
        private Lock synchronize;

        private Class<T> clazz = null;

        private CacheStream(Class<T> clazz)
        {
            this.clazz = clazz;
            this.synchronize = new ReentrantLock(true);

            try
            {
                Class.forName("org.sqlite.JDBC").newInstance();
                SQLiteConfig sqliteConfig = new SQLiteConfig();
                sqliteConfig.setReadOnly(false);

                CacheStorage mode = CacheStorage.DISK;
                if (clazz.isAnnotationPresent(CacheMemory.class))
                {
                    mode = CacheStorage.MEMORY;
                }

                String connectionBuilder = "jdbc:sqlite:" + clazz.getSimpleName() + ".cache";
                if (mode.equals(CacheStorage.MEMORY))
                {
                    connectionBuilder = "jdbc:sqlite:file: " + clazz.getSimpleName() + "?mode=memory&cache=shared";
                }
                connection = DriverManager.getConnection(connectionBuilder, sqliteConfig.toProperties());
            }
            catch (Exception | Error ex)
            {
                Trace.printStackTrace(ex);
            }

            try
            {
                prepare(clazz);
            }
            catch (Exception ex)
            {
                Trace.printStackTrace(ex);
            }
        }

        public void write(T data)
        {
            T instance = (T) data;
            save(instance, true);
        }

        public void write(ArrayList<T> data)
        {
            ArrayList<T> instance = (ArrayList<T>) data;
            save(instance, true);
        }

        public void write(CachedRowSet data)
        {
            CachedRowSet instance = (CachedRowSet) data;
            save(instance, true);
        }

        public void write(ResultSet data)
        {
            ResultSet instance = (ResultSet) data;
            save(instance, true);
        }

        public void queue(T data)
        {
            T instance = (T) data;
            save(instance, false);
        }

        public void queue(ArrayList<T> data)
        {
            ArrayList<T> instance = (ArrayList<T>) data;
            save(instance, false);
        }

        public void queue(CachedRowSet data)
        {
            CachedRowSet instance = (CachedRowSet) data;
            save(instance, false);
        }

        public void queue(ResultSet data)
        {
            ResultSet instance = (ResultSet) data;
            save(instance, false);
        }

        public ArrayList<T> read(String expression)
        {
            ArrayList<T> response = new ArrayList<>();

            Statement statement = null;
            ResultSet dataset = null;
            try
            {
                synchronize.lock();

                // -------------------------------------------------------
                String preareQuery = " SELECT * FROM cache ";
                if (expression != null && !expression.isEmpty())
                {
                    if (expression.toUpperCase().contains("SELECT") && expression.toUpperCase().contains("FROM"))
                    {
                        preareQuery = expression;
                    }
                    else
                    {
                        preareQuery += " WHERE " + expression;
                    }
                }

                // -------------------------------------------------------
                statement = connection.createStatement();
                dataset = statement.executeQuery(preareQuery);

                while (dataset.next())
                {
                    T instance = (T) clazz.newInstance();
                    for (Field field : clazz.getDeclaredFields())
                    {
                        if (field.isAnnotationPresent(Cache.class))
                        {
                            Annotation annotation = field.getAnnotation(Cache.class);
                            Cache cache = (Cache) annotation;
                            String column = cache.column();

                            field.setAccessible(true);
                            field.set(instance, dataset.getObject(column));
                        }
                        else
                        {
                            String column = field.getName();
                            try
                            {
                                field.setAccessible(true);
                                field.set(instance, dataset.getObject(column));
                            }
                            catch (Exception ex2)
                            {
                            }
                        }
                    }
                    response.add(instance);
                }

                dataset.close();
                statement.close();
                dataset = null;
                statement = null;
            }
            catch (Exception | Error ex)
            {
                Trace.printStackTrace(ex);
            }
            finally
            {
                try
                {
                    if (dataset != null)
                    {
                        dataset.close();
                        dataset = null;
                    }
                }
                catch (Exception | Error ex)
                {
                }
                try
                {
                    if (statement != null)
                    {
                        statement.close();
                        statement = null;
                    }
                }
                catch (Exception ex)
                {
                }
                synchronize.unlock();
            }

            return response;
        }

        public ArrayList<T> read()
        {
            return read("");
        }

        public CachedRowSet fetch(String expression)
        {
            CachedRowSet response = null;

            Statement statement = null;
            ResultSet dataset = null;
            try
            {
                synchronize.lock();

                // -------------------------------------------------------
                String preareQuery = " SELECT * FROM cache ";
                if (expression != null && !expression.isEmpty())
                {
                    if (expression.toLowerCase().contains("SELECT") && expression.toLowerCase().contains("FROM"))
                    {
                        preareQuery = expression;
                    }
                    else
                    {
                        preareQuery += " WHERE " + expression;
                    }
                }

                // -------------------------------------------------------
                statement = connection.createStatement();
                dataset = statement.executeQuery(preareQuery);

                response = RowSetProvider.newFactory().createCachedRowSet();
                response.populate(dataset);

                dataset.close();
                statement.close();
                dataset = null;
                statement = null;
            }
            catch (Exception | Error ex)
            {
                Trace.printStackTrace(ex);
            }
            finally
            {
                try
                {
                    if (dataset != null)
                    {
                        dataset.close();
                        dataset = null;
                    }
                }
                catch (Exception | Error ex)
                {
                }
                try
                {
                    if (statement != null)
                    {
                        statement.close();
                        statement = null;
                    }
                }
                catch (Exception ex)
                {
                }
                synchronize.unlock();
            }

            return response;
        }

        public CachedRowSet fetch()
        {
            return fetch("");
        }

        public ArrayList<T> dequeue()
        {
            ArrayList<T> response = new ArrayList<>();

            Statement statementExecute = null;
            Statement statement = null;
            ResultSet dataset = null;
            try
            {
                synchronize.lock();

                // -------------------------------------------------------
                {
                    String prepareQuery = " UPDATE cache SET dequeue = 1 WHERE dequeue = 0 ";
                    statementExecute = connection.createStatement();
                    statementExecute.executeUpdate(prepareQuery);
                    statementExecute.close();
                    statementExecute = null;
                }
                // -------------------------------------------------------
                {
                    String prepareQuery = " SELECT * FROM cache WHERE dequeue = 1 ORDER BY version DESC ";
                    statement = connection.createStatement();
                    dataset = statement.executeQuery(prepareQuery);

                    while (dataset.next())
                    {
                        T instance = (T) clazz.newInstance();
                        for (Field field : clazz.getDeclaredFields())
                        {
                            if (field.isAnnotationPresent(Cache.class))
                            {
                                Annotation annotation = field.getAnnotation(Cache.class);
                                Cache cache = (Cache) annotation;
                                String column = cache.column();

                                field.setAccessible(true);
                                field.set(instance, dataset.getObject(column));
                            }
                        }
                        response.add(instance);
                    }

                    dataset.close();
                    statement.close();
                    dataset = null;
                    statement = null;
                }
                // -------------------------------------------------------
                {
                    String prepareQuery = " DELETE FROM cache WHERE dequeue = 1 ";
                    statementExecute = connection.createStatement();
                    statementExecute.executeUpdate(prepareQuery);
                    statementExecute.close();
                    statementExecute = null;
                }
            }
            catch (Exception | Error ex)
            {
                Trace.printStackTrace(ex);
            }
            finally
            {
                try
                {
                    if (statementExecute != null)
                    {
                        statementExecute.close();
                    }
                }
                catch (Exception | Error ex)
                {
                }
                try
                {
                    if (dataset != null)
                    {
                        dataset.close();
                    }
                }
                catch (Exception | Error ex)
                {
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
                synchronize.unlock();
            }

            return response;
        }

        public void empty()
        {
            Statement statementExecute = null;
            try
            {
                synchronize.lock();

                // String prepareQuery = " DROP TABLE IF EXISTS cache ";
                String prepareQuery = " DELETE FROM cache ";
                statementExecute = connection.createStatement();
                statementExecute.executeUpdate(prepareQuery);
                statementExecute.close();
                statementExecute = null;
            }
            catch (Exception | Error ex)
            {
                Trace.printStackTrace(ex);
            }
            finally
            {
                try
                {
                    if (statementExecute != null)
                    {
                        statementExecute.close();
                    }
                }
                catch (Exception | Error ex)
                {
                }
                synchronize.unlock();
            }
        }

        // =======================================================
        public ArrayList<T> toArray(ResultSet dataset)
        {
            ArrayList<T> response = new ArrayList<>();

            try
            {
                while (dataset.next())
                {
                    T instance = (T) clazz.newInstance();
                    for (Field field : clazz.getDeclaredFields())
                    {
                        if (field.isAnnotationPresent(Cache.class))
                        {
                            Annotation annotation = field.getAnnotation(Cache.class);
                            Cache cache = (Cache) annotation;
                            String column = cache.column();

                            field.setAccessible(true);
                            field.set(instance, dataset.getObject(column));
                        }
                    }
                    response.add(instance);
                }
            }
            catch (Exception | Error ex)
            {
                Trace.printStackTrace(ex);
            }

            return response;
        }

        // =======================================================
        private void save(T response, boolean truncate)
        {
            Statement statementExecute = null;
            NamedParameterStatement statement = null;
            try
            {
                synchronize.lock();

                // -------------------------------------------------------
                if (truncate)
                {
                    String prepareQuery = " DELETE FROM cache ";
                    statementExecute = connection.createStatement();
                    statementExecute.executeUpdate(prepareQuery);
                    statementExecute.close();
                    statementExecute = null;
                }
                // -------------------------------------------------------
                String prepareQuery = prepare(clazz);
                statement = new NamedParameterStatement(connection, prepareQuery);
                for (Field field : clazz.getDeclaredFields())
                {
                    if (field.isAnnotationPresent(Cache.class))
                    {
                        Annotation annotation = field.getAnnotation(Cache.class);
                        Cache cache = (Cache) annotation;
                        String column = cache.column();

                        field.setAccessible(true);
                        statement.setObject(column, field.get(response));
                    }
                }
                statement.executeUpdate();
                statement.close();
                statement = null;
            }
            catch (Exception | Error ex)
            {
                Trace.printStackTrace(ex);
            }
            finally
            {
                try
                {
                    if (statementExecute != null)
                    {
                        statementExecute.close();
                        statementExecute = null;
                    }
                }
                catch (Exception | Error ex)
                {
                }
                try
                {
                    if (statement != null)
                    {
                        statement.close();
                        statement = null;
                    }
                }
                catch (Exception ex)
                {
                }
                synchronize.unlock();
            }
        }

        private void save(ArrayList<T> response, boolean truncate)
        {
            Statement statementExecute = null;
            NamedParameterStatement statement = null;
            try
            {
                synchronize.lock();

                // -------------------------------------------------------
                if (truncate)
                {
                    String prepareQuery = " DELETE FROM cache ";
                    statementExecute = connection.createStatement();
                    statementExecute.executeUpdate(prepareQuery);
                    statementExecute.close();
                    statementExecute = null;
                }
                // -------------------------------------------------------
                String prepareQuery = prepare(clazz);
                for (T instance : response)
                {
                    statement = new NamedParameterStatement(connection, prepareQuery);
                    for (Field field : clazz.getDeclaredFields())
                    {
                        if (field.isAnnotationPresent(Cache.class))
                        {
                            Annotation annotation = field.getAnnotation(Cache.class);
                            Cache cache = (Cache) annotation;
                            String column = cache.column();

                            field.setAccessible(true);
                            statement.setObject(column, field.get(instance));
                        }
                    }
                    statement.executeUpdate();
                    statement.close();
                    statement = null;
                }
            }
            catch (Exception | Error ex)
            {
                Trace.printStackTrace(ex);
            }
            finally
            {
                try
                {
                    if (statementExecute != null)
                    {
                        statementExecute.close();
                    }
                }
                catch (Exception | Error ex)
                {
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
                synchronize.unlock();
            }
        }

        private void save(CachedRowSet response, boolean truncate)
        {
            Statement statementExecute = null;
            NamedParameterStatement statement = null;
            try
            {
                synchronize.lock();

                // -------------------------------------------------------
                if (truncate)
                {
                    String prepareQuery = " DELETE FROM cache ";
                    statementExecute = connection.createStatement();
                    statementExecute.executeUpdate(prepareQuery);
                    statementExecute.close();
                    statementExecute = null;
                }
                // -------------------------------------------------------
                String prepareQuery = prepare(clazz);
                ResultSetMetaData rsmd = response.getMetaData();
                int numCols = rsmd.getColumnCount();
                while (response.next())
                {
                    statement = new NamedParameterStatement(connection, prepareQuery);
                    for (int j = 0; j < numCols; j++)
                    {
                        statement.setObject(rsmd.getColumnName(j + 1), response.getObject(j + 1));
                    }
                    statement.executeUpdate();
                    statement.close();
                    statement = null;
                }
                response.beforeFirst();
            }
            catch (Exception | Error ex)
            {
                Trace.printStackTrace(ex);
            }
            finally
            {
                try
                {
                    if (statementExecute != null)
                    {
                        statementExecute.close();
                    }
                }
                catch (Exception | Error ex)
                {
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
                synchronize.unlock();
            }
        }

        private void save(ResultSet response, boolean truncate)
        {
            Statement statementExecute = null;
            NamedParameterStatement statement = null;
            try
            {
                synchronize.lock();

                // -------------------------------------------------------
                if (truncate)
                {
                    String prepareQuery = " DELETE FROM cache ";
                    statementExecute = connection.createStatement();
                    statementExecute.executeUpdate(prepareQuery);
                    statementExecute.close();
                    statementExecute = null;
                }
                // -------------------------------------------------------
                String prepareQuery = prepare(clazz);
                ResultSetMetaData rsmd = response.getMetaData();
                int numCols = rsmd.getColumnCount();
                while (response.next())
                {
                    statement = new NamedParameterStatement(connection, prepareQuery);
                    for (int j = 0; j < numCols; j++)
                    {
                        statement.setObject(rsmd.getColumnName(j + 1), response.getObject(j + 1));
                    }
                    statement.executeUpdate();
                    statement.close();
                    statement = null;
                }
            }
            catch (Exception | Error ex)
            {
                Trace.printStackTrace(ex);
            }
            finally
            {
                try
                {
                    if (statementExecute != null)
                    {
                        statementExecute.close();
                    }
                }
                catch (Exception | Error ex)
                {
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
                synchronize.unlock();
            }
        }

        private String prepare(Class<T> clazz)
        {
            String prepareTable = " ";
            String prepareQuery = " ";
            String prepareBind = " ";

            {
                prepareTable += " CREATE TABLE IF NOT EXISTS cache ( ";
                prepareQuery += " INSERT INTO cache ( ";
                prepareBind += " VALUES ( ";
            }

            for (Field field : clazz.getDeclaredFields())
            {
                if (field.isAnnotationPresent(Cache.class))
                {
                    Annotation annotation = field.getAnnotation(Cache.class);
                    Cache cache = (Cache) annotation;
                    String column = cache.column();
                    CacheType type = cache.type();

                    if (type == CacheType.INTEGER)
                    {
                        prepareTable += " " + column + " INTEGER , ";
                    }
                    else if (type == CacheType.REAL)
                    {
                        prepareTable += " " + column + " REAL , ";
                    }
                    else
                    {
                        prepareTable += " " + column + " TEXT , ";
                    }
                    prepareQuery += " " + column + " , ";
                    prepareBind += " :" + column + " , ";
                }
            }

            {
                prepareTable += " dequeue INTEGER NOT NULL DEFAULT 0 ";
                prepareTable += " , version INTEGER NOT NULL ); ";

                prepareQuery += " dequeue ";
                prepareQuery += " , version ) ";

                prepareBind += " " + 0 + " ";
                prepareBind += " , " + System.currentTimeMillis() + " ); ";

                prepareQuery += prepareBind;
            }

            // -------------------------------------------------------
            Statement statement = null;
            try
            {
                statement = connection.createStatement();
                statement.executeUpdate(prepareTable);
                statement.close();
                statement = null;
            }
            catch (Exception | Error ex)
            {
                Trace.printStackTrace(ex);
            }
            finally
            {
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
            }

            return prepareQuery;
        }
    }
}
