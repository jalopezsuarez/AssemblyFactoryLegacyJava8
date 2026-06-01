package com.assembly.core.web;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

import com.assembly.core.trace.Trace;
import com.assembly.core.web.WebResource.Security;
import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.HttpContext;
import java.util.Map;

@SuppressWarnings("restriction")
public class WebServer
{
    private static WebServer INSTANCE = null;

    public static WebServer instance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new WebServer();
        }
        return INSTANCE;
    }

    private final HashMap<String, WebResource> resources = new HashMap<>();
    private final HashMap<String, String> credentials = new HashMap<>();

    private ExecutorService executor = null;
    private HttpServer server = null;

    private WebServer()
    {
    }

    public <T extends WebController> void resource(String uri, Class<T> clazz, WebResource.ContentType contentType, WebResource.Security security)
    {
        String resource = uri;
        resource = resource.replaceAll("[" + Pattern.quote("/") + Pattern.quote("\\") + "]+", Matcher.quoteReplacement("/"));
        resource = resource.replaceAll("^[\\s" + Pattern.quote("/") + Pattern.quote("\\") + "]+", "");
        resource = resource.replaceAll("[\\s" + Pattern.quote("/") + Pattern.quote("\\") + "]+$", "");

        resources.put(resource, new WebResource(resource, clazz, contentType, security));
    }

    public void credentials(String username, String password)
    {
        if (username != null && !username.trim().isEmpty() && password != null && !password.trim().isEmpty())
        {
            credentials.put(username.trim(), password.trim());
        }
    }

    public void start(int port)
    {
        try
        {
            WebProvider provider = new WebProvider(resources);
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/", provider);

            for (Map.Entry<String, WebResource> entity : resources.entrySet())
            {
                WebResource resource = entity.getValue();
                if (resource.security.equals(Security.PRIVATE))
                {
                    HttpContext context = server.createContext("/" + resource.uri, provider);
                    BasicAuthenticator authenticator = new WebAuthenticator(WebServer.class.getSimpleName())
                    {
                        @Override
                        public boolean checkCredentials(String username, String password)
                        {
                            boolean checkCredentials = false;
                            String authorize = null;
                            if (username != null && credentials.containsKey(username.trim()))
                            {
                                authorize = username.trim();
                            }
                            if (authorize != null && !authorize.isEmpty() && password != null && !password.isEmpty())
                            {
                                String token = credentials.get(authorize);
                                checkCredentials = token.equals(password.trim());
                            }
                            return checkCredentials;
                        }
                    };
                    context.setAuthenticator(authenticator);
                }
            }

            executor = Executors.newCachedThreadPool();
            server.setExecutor(executor);
            server.start();
        }
        catch (Exception | Error ex)
        {
            Trace.printStackTrace(ex);
        }
    }

    public void stop()
    {
        try
        {
            server.stop(1);
            executor.shutdownNow();
        }
        catch (Exception ex)
        {
            Trace.printStackTrace(ex);
        }
    }
}
