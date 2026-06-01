/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assembly.service.web;

import java.util.HashMap;

import com.assembly.core.web.RequestExchange;
import com.assembly.core.web.WebController;

/**
 *
 * @author administrator
 */
public class TesterController extends WebController
{
    @Override
    public Object execute(RequestExchange exchange)
    {
        HashMap<String, String> execute = new HashMap();
        execute.put("param1", exchange.params("param1"));
        execute.put("param2", exchange.params("param2"));
        return execute;
    }
}
