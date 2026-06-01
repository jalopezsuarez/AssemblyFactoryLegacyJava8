package com.assembly.core.config;

import com.assembly.core.source.db.DriverAdapter;

public class DatabaseSession
{
    private String alias;
    private DriverAdapter adapter;
    private String server;
    private String instance;
    private String username;
    private String password;
    private String dbname;
    private int port;
    private String schema;

    public DatabaseSession()
    {
        alias = "";
        adapter = DriverAdapter.NONE;
        server = "";
        instance = "";
        username = "";
        password = "";
        dbname = "";
        port = 0;
        schema = "";
    }

    public String getAlias()
    {
        return alias;
    }

    public void setAlias(String alias)
    {
        this.alias = alias;
    }

    public DriverAdapter getAdapter()
    {
        return adapter;
    }

    public void setAdapter(DriverAdapter adapter)
    {
        this.adapter = adapter;
    }

    public String getServer()
    {
        return server;
    }

    public void setServer(String server)
    {
        this.server = server;
    }

    public String getInstance()
    {
        return instance;
    }

    public void setInstance(String instance)
    {
        this.instance = instance;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getDbname()
    {
        return dbname;
    }

    public void setDbname(String dbname)
    {
        this.dbname = dbname;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public String getSchema()
    {
        return schema;
    }

    public void setSchema(String schema)
    {
        this.schema = schema;
    }

}
