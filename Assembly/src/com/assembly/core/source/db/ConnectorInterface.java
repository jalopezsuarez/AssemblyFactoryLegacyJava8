package com.assembly.core.source.db;

import java.sql.ResultSet;
import javax.sql.rowset.CachedRowSet;

public interface ConnectorInterface
{

    public void prepare(String query, boolean identity) throws Exception;

    public void prepare(String query) throws Exception;

    public void bind(String parameter, Object value);

    public ResultSet read() throws Exception;

    public ResultSet read(String query) throws Exception;

    public CachedRowSet cache() throws Exception;

    public CachedRowSet cache(String query) throws Exception;

    public Object write() throws Exception;

    public Object write(String query, boolean identity) throws Exception;

    public Object write(String query) throws Exception;

    public void transaction(Transaction mode);

    public void dispose();

}
