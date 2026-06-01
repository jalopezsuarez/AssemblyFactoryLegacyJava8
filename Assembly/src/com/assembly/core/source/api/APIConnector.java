package com.assembly.core.source.api;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.StringJoiner;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.assembly.core.trace.Trace;

public final class APIConnector
{

    // =======================================================
    public static final String STATUS_OK = "OK";
    protected Gson gson;

    private final Map<String, String> params = new HashMap<>();

    // =======================================================
    private static final APIConnector INSTANCE = new APIConnector();

    public static APIConnector instance()
    {
        return INSTANCE;
    }

    // =======================================================
    private APIConnector()
    {
        gson = new Gson();
    }

    // =======================================================
    public void param(String param, String value)
    {

    }

    public String execute(String url)
    {
        String execute = null;
        boolean success = false;

        try
        {
            URL resource = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) resource.openConnection();

            // -------------------------------------------------------
            StringJoiner postData = new StringJoiner("&");
            for (Map.Entry<String, String> item : params.entrySet())
            {
                postData.add(URLEncoder.encode(item.getKey(), "UTF-8") + "=" + URLEncoder.encode(item.getValue(), "UTF-8"));
            }
            byte[] postDataBytes = postData.toString().getBytes(Charset.forName("UTF-8"));
            int dataLength = postDataBytes.length;

            // -------------------------------------------------------
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(false);
            connection.setFixedLengthStreamingMode(dataLength);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Content-Length", Integer.toString(dataLength));

            // -------------------------------------------------------
            connection.getOutputStream().write(postDataBytes);

            // -------------------------------------------------------
            InputStream response = null;
            if (connection.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST)
            {
                response = connection.getInputStream();
            }
            else
            {
                response = connection.getErrorStream();
            }

            String json = response(response);
            Map<String, Object> results = gson.fromJson(json, new TypeToken<Map<String, Object>>()
            {
            }.getType());
            if (results != null && results instanceof Map && results.size() > 0)
            {
                if (((String) results.get("status")).compareToIgnoreCase(APIConnector.STATUS_OK) == 0)
                {
                    success = true;
                }
            }

            // -------------------------------------------------------
            connection.disconnect();

            // -------------------------------------------------------
            // CachedRowSet cachedRowSet = RowSetProvider.newFactory().createCachedRowSet();
            // crs.populate(myResultSet);
            // -------------------------------------------------------
            params.clear();
        }
        catch (Exception ex)
        {
            Trace.printStackTrace(ex);
        }

        return execute;
    }

    private String response(InputStream stream) throws Exception
    {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(stream)))
        {
            String line;
            while ((line = in.readLine()) != null)
            {
                builder.append(line);
            }
            in.close();
        }
        return builder.toString();
    }
}
