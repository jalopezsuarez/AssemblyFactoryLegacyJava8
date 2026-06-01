package com.assembly.core.i18n;

import java.util.Locale;
import java.util.Properties;
import java.io.InputStream;

import com.assembly.core.commons.FileHelper;
import com.assembly.core.resources.RelativeResource;
import com.assembly.core.trace.Trace;

public class Localizable
{
    private static final String ReferenceResources = "/res/i18n/";
    private static final String ExtensionResources = ".xml";

    private static Boolean initializedProperties = false;
    private static Locale initializedLocale = Locale.getDefault();

    private static InputStream fileInputStream = null;
    private static Properties properties = null;

    public static void initialize(String code)
    {
        initializedLocale = new Locale(code);
        initializedProperties = false;
    }

    public static String string(String key)
    {
        String localizedString = null;
        try
        {
            if (!initializedProperties)
            {
                initializedProperties = true;

                String relativeResource = FileHelper.buildResource(ReferenceResources, initializedLocale.getLanguage().toLowerCase()) + ExtensionResources;
                fileInputStream = RelativeResource.instance().read(relativeResource);
                properties = new Properties();
                properties.loadFromXML(fileInputStream);
            }
            if (properties != null && properties.getClass().equals(Properties.class) && !properties.isEmpty())
            {
                localizedString = properties.getProperty(key);
            }
        }
        catch (Exception | Error ex)
        {
            Trace.printStackTrace(ex);
        }

        if (localizedString == null || !localizedString.getClass().equals(String.class) || localizedString.length() <= 0)
        {
            localizedString = key;
        }

        return localizedString;
    }

    public static String string(String key, String code)
    {
        initializedLocale = new Locale(code);
        return string(key);
    }
}
