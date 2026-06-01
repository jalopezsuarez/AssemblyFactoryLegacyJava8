package com.assembly.core.commons;

import java.io.File;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileHelper
{
    public static String currentDirectory()
    {
        File directory = new File(Paths.get(".").toAbsolutePath().normalize().toString());
        String currentDirectory = directory.getAbsolutePath();

        currentDirectory = currentDirectory.replaceAll("[\\s" + Pattern.quote("/") + Pattern.quote("\\") + "]+$", "");
        currentDirectory = currentDirectory.replaceAll("[" + Pattern.quote("/") + Pattern.quote("\\") + "]+", Matcher.quoteReplacement(File.separator));

        return currentDirectory;
    }

    public static String buildResource(String resource, String extra)
    {
        resource = resource.replaceAll("[\\s" + Pattern.quote("/") + Pattern.quote("\\") + "]+$", "");
        resource = resource.replaceAll("[" + Pattern.quote("/") + Pattern.quote("\\") + "]+", Matcher.quoteReplacement(File.separator));

        extra = extra.replaceAll("^[\\s" + Pattern.quote("/") + Pattern.quote("\\") + "]+", "");
        extra = extra.replaceAll("[\\s" + Pattern.quote("/") + Pattern.quote("\\") + "]+$", "");
        extra = extra.replaceAll("[" + Pattern.quote("/") + Pattern.quote("\\") + "]+", Matcher.quoteReplacement(File.separator));

        String path = resource + File.separator + extra;
        return path;
    }

}
