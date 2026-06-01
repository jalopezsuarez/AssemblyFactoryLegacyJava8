package com.assembly.core.web;

public class WebResource
{
    public final String uri;
    public final Class clazz;
    public final ContentType contentType;
    public final Security security;

    public enum ContentType
    {
        WEB, JSON
    }

    public enum Security
    {
        PUBLIC, PRIVATE
    }

    public <T extends WebController> WebResource(String uri, Class<T> clazz, ContentType contentType, Security security)
    {
        this.uri = uri;
        this.clazz = clazz;
        this.contentType = contentType;
        this.security = security;
    }

}
