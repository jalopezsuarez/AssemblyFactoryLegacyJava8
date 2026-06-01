/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assembly.core.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpExchange;

import org.apache.commons.io.IOUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * @author Administrator
 */
public class RequestExchange
{

    private final HashMap<String, String> params = new HashMap();
    private final ArrayList<FileItem> files = new ArrayList();

    public HashMap<String, String> params()
    {
        return params;
    }

    public ArrayList<FileItem> files()
    {
        return files;
    }

    public void params(String key, String value)
    {
        params.put(key, value);
    }

    public String params(String key)
    {
        String value = "";
        if (params.containsKey(key))
        {
            value = params.get(key);
        }
        return value;
    }

    public void files(FileItem file)
    {
        files.add(file);
    }

    public FileItem files(int index)
    {
        FileItem item = null;
        if (index < files.size())
        {
            item = files.get(index);
        }
        return item;
    }

    public static RequestExchange parseRequest(HttpExchange exchange)
    {
        RequestExchange request;

        Object source = (RequestExchange) exchange.getAttribute(RequestExchange.class.getCanonicalName());
        if (source != null && source instanceof RequestExchange)
        {
            request = (RequestExchange) source;
        }
        else
        {
            request = new RequestExchange();
            exchange.setAttribute(RequestExchange.class.getCanonicalName(), request);
        }

        try
        {
            DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
            ServletFileUpload servletFileUpload = new ServletFileUpload(diskFileItemFactory);
            List<FileItem> resources = servletFileUpload.parseRequest(new RequestContext()
            {
                @Override
                public String getCharacterEncoding()
                {
                    return "UTF-8";
                }

                @Override
                public int getContentLength()
                {
                    return 0;
                }

                @Override
                public String getContentType()
                {
                    return exchange.getRequestHeaders().getFirst("Content-type");
                }

                @Override
                public InputStream getInputStream() throws IOException
                {
                    return exchange.getRequestBody();
                }
            });

            for (FileItem item : resources)
            {
                if (item.isFormField())
                {
                    request.params(item.getFieldName(), item.getString());
                }
                else
                {
                    request.files(item);
                }
            }
        }
        catch (Exception | Error ex)
        {
        }

        try
        {
            StringWriter writer = new StringWriter();
            IOUtils.copy(exchange.getRequestBody(), writer, StandardCharsets.UTF_8.name());
            String query = writer.toString();

            String defs[] = query.split("[&]");
            for (String def : defs)
            {
                int ix = def.indexOf('=');
                String name;
                String value;
                if (ix < 0)
                {
                    name = URLDecoder.decode(def, StandardCharsets.UTF_8.name());
                    value = "";
                }
                else
                {
                    name = URLDecoder.decode(def.substring(0, ix), StandardCharsets.UTF_8.name());
                    value = URLDecoder.decode(def.substring(ix + 1), StandardCharsets.UTF_8.name());
                }
                request.params(name, value);
            }
        }
        catch (Exception | Error ex)
        {
        }

        try
        {
            String query = exchange.getRequestURI().getQuery();
            if (query != null && !query.isEmpty())
            {
                for (String param : query.split("&"))
                {
                    String pair[] = param.split("=");
                    if (pair.length > 1)
                    {
                        request.params(pair[0], pair[1]);
                    }
                    else
                    {
                        request.params(pair[0], "");
                    }
                }
            }
        }
        catch (Exception ex)
        {
        }

        return request;
    }
}
