/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assembly.core.web;

import java.util.ArrayList;

/**
 *
 * @author Administrator
 */
public class ResponseExchange
{
    public enum Status
    {
        OK, ERROR, EXCEPTION
    }

    private Status status;
    private int code;
    private String message;
    private Object results;

    public ResponseExchange()
    {
        this.status = Status.OK;
        this.code = 0;
        this.message = "";
        
        this.results = new ArrayList();
    }

    public void setStatus(Status status)
    {
        this.status = status;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public void setResults(Object results)
    {
        this.results = results;
    }

}
