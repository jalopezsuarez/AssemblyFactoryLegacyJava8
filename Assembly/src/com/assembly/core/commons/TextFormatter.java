/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assembly.core.commons;

import java.util.regex.Pattern;

/**
 *
 * @author administrator
 */
public class TextFormatter
{
    public static String trim(String value, String mask)
    {
        String trimmer = value.replaceAll("^[\\s" + Pattern.quote(mask) + "]+", "");
        trimmer = trimmer.replaceAll("[\\s" + Pattern.quote(mask) + "]+$", "");

        String response = trimmer;
        return response;
    }

}
