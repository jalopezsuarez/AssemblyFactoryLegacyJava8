/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assembly.core.web;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import com.google.gson.Gson;

import com.assembly.core.commons.FileHelper;
import com.assembly.core.resources.RelativeResource;
import com.assembly.core.web.WebResource.ContentType;
import com.assembly.core.trace.Trace;

@SuppressWarnings("restriction")
public class WebProvider implements HttpHandler
{
    private static final String ReferenceResources = "/res/web/";
    private static final String ReferenceExtension = "html";

    private HashMap<String, WebResource> resources = new HashMap<>();

    public WebProvider(HashMap<String, WebResource> resources)
    {
        this.resources = resources;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException
    {
        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.add("Cache-Control", "no-cache, no-store, must-revalidate, public, max-age=0");
        responseHeaders.add("Pragma", "no-cache, no-store");
        responseHeaders.add("Expires", "0");
        responseHeaders.add("Access-Control-Allow-Origin", "*");
        responseHeaders.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        responseHeaders.add("Access-Control-Allow-Credentials", "true");

        InputStream inputStream = null;
        OutputStream outputStream = null;

        InputStreamReader intpuStreamReader = null;
        BufferedReader bufferedReader = null;

        try
        {
            String contextURL = exchange.getRequestURI().getPath();
            contextURL = contextURL.replaceAll("[" + Pattern.quote("/") + Pattern.quote("\\") + "]+", Matcher.quoteReplacement("/"));
            contextURL = contextURL.replaceAll("^[\\s" + Pattern.quote("/") + Pattern.quote("\\") + "]+", "");
            contextURL = contextURL.replaceAll("[\\s" + Pattern.quote("/") + Pattern.quote("\\") + "]+$", "");

            if (!resources.containsKey(contextURL))
            {
                String relativeResource = FileHelper.buildResource(ReferenceResources, contextURL);
                inputStream = RelativeResource.instance().read(relativeResource);
                if (inputStream == null)
                {
                    throw new Exception();
                }

                exchange.sendResponseHeaders(200, 0);
                outputStream = exchange.getResponseBody();
                byte[] buffer = new byte[1024];
                int read = 0;
                while ((read = inputStream.read(buffer, 0, buffer.length)) != -1)
                {
                    outputStream.write(buffer, 0, read);
                }
            }
            else
            {
                WebResource resource = resources.get(contextURL);
                Class clazz = resource.clazz;
                WebController instance = (WebController) clazz.newInstance();
                RequestExchange params = RequestExchange.parseRequest(exchange);
                Object execute = instance.execute(params);

                ContentType contentType = resource.contentType;
                if (contentType.equals(ContentType.WEB))
                {
                    String relativeResource = FileHelper.buildResource(ReferenceResources, clazz.getSimpleName()) + "." + ReferenceExtension;
                    inputStream = RelativeResource.instance().read(relativeResource);
                    if (inputStream == null)
                    {
                        throw new Exception();
                    }

                    intpuStreamReader = new InputStreamReader(inputStream);
                    bufferedReader = new BufferedReader(intpuStreamReader);
                    StringBuilder builder = new StringBuilder();
                    String read;
                    while ((read = bufferedReader.readLine()) != null)
                    {
                        builder.append(read);
                    }
                    String response = builder.toString();

                    String responseBody;
                    if (execute != null && execute instanceof HashMap)
                    {
                        Template template = Mustache.compiler().compile(response);
                        responseBody = template.execute(execute);
                    }
                    else
                    {
                        responseBody = response;
                    }

                    exchange.sendResponseHeaders(200, responseBody.length());
                    outputStream = exchange.getResponseBody();
                    outputStream.write(responseBody.getBytes());
                }
                else
                {
                    ResponseExchange response = new ResponseExchange();
                    response.setResults(execute);
                    String responseJSON = new Gson().toJson(response);

                    exchange.sendResponseHeaders(200, responseJSON.length());
                    outputStream = exchange.getResponseBody();
                    outputStream.write(responseJSON.getBytes());
                }
            }
        }
        catch (Exception | Error ex)
        {
            Trace.printStackTrace(ex);

            ResponseExchange response = new ResponseExchange();
            response.setStatus(ResponseExchange.Status.ERROR);
            response.setMessage("HTTP 500 Internal Server Error");
            response.setResults(null);
            String responseException = new Gson().toJson(response);

            exchange.sendResponseHeaders(500, responseException.length());
            outputStream = exchange.getResponseBody();
            outputStream.write(responseException.getBytes());
        }
        finally
        {
            try
            {
                if (bufferedReader != null)
                {
                    bufferedReader.close();
                }
            }
            catch (Exception | Error ex)
            {
            }
            try
            {
                if (intpuStreamReader != null)
                {
                    intpuStreamReader.close();
                }
            }
            catch (Exception | Error ex)
            {
            }
            try
            {
                if (outputStream != null)
                {
                    outputStream.flush();
                    outputStream.close();
                }
            }
            catch (Exception | Error ex)
            {
            }
            try
            {
                if (inputStream != null)
                {
                    inputStream.close();
                }
            }
            catch (Exception | Error ex)
            {
            }
        }
    }
}
