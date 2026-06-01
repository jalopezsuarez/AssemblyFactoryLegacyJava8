/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assembly.core.source.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.assembly.core.source.db.DBConnector;

/**
 *
 * @author administrator
 */
public class ProviderService
{
    public static String schemeQuery(String query)
    {
        String render = query;
        if (render != null && !render.isEmpty())
        {
            {
                Pattern regex = Pattern.compile("(\\s+\\{\\s*(\\S*)\\.(\\S*)\\s*\\}\\s+)");
                Matcher matcher = regex.matcher(render);
                while (matcher.find())
                {
                    String replacement = DBConnector.schema(matcher.group(2), matcher.group(3));
                    render = render.replace(matcher.group(1), replacement);
                }
            }
            {
                Pattern regex = Pattern.compile("(\\s+\\{\\s*(\\S[^,]*)\\s*,\\s*(\\s\\S[^}]*)\\s*\\}\\s+)");
                Matcher matcher = regex.matcher(render);
                while (matcher.find())
                {
                    String replacement = DBConnector.schema(matcher.group(2), matcher.group(3));
                    render = render.replace(matcher.group(1), replacement);
                }
            }
        }
        return render;
    }
}
