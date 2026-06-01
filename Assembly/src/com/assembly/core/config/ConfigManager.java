package com.assembly.core.config;

import java.util.List;
import java.util.HashMap;
import java.io.InputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;

import com.assembly.core.commons.FileHelper;
import com.assembly.core.source.db.DriverAdapter;
import com.assembly.core.config.exceptions.EnvironmentRequiredException;
import com.assembly.core.resources.RelativeResource;
import com.assembly.core.trace.Trace;

public final class ConfigManager
{
    private static final String ReferenceResources = "/res/config/";
    private static final String ExtensionEnvironment = ".env";
    private static final String ExtensionProperties = ".properties";

    // =======================================================
    private final HashMap<String, DatabaseSession> databases = new HashMap<>();
    private final HashMap<DriverAdapter, PoolAdapter> pool = new HashMap<>();
    private final ConfigProperties properties = new ConfigProperties();

    // =======================================================
    private static final ConfigManager INSTANCE = new ConfigManager();

    public static ConfigManager instance()
    {
        return INSTANCE;
    }

    // =======================================================
    private ConfigManager()
    {
        String resourceProperties = null;

        // -------------------------------------------------------
        try
        {
            String currentDirectory = FileHelper.currentDirectory();
            File[] files = new File(currentDirectory).listFiles(new FilenameFilter()
            {
                @Override
                public boolean accept(File dir, String name)
                {
                    return name.toLowerCase().endsWith(ExtensionEnvironment);
                }
            });

            if (files != null && files.length > 0)
            {
                Arrays.sort(files, new Comparator<File>()
                {
                    @Override
                    public int compare(File f1, File f2)
                    {
                        return Long.valueOf(f2.lastModified()).compareTo(f1.lastModified());
                    }
                });
                File resource = files[0];
                if (resource.exists() && !resource.isDirectory())
                {
                    String filename = resource.getName();
                    if (filename.indexOf(".") > 0)
                    {
                        resourceProperties = filename.substring(0, filename.lastIndexOf(".")) + ExtensionProperties;
                    }
                }
            }

            if (resourceProperties == null || resourceProperties.isEmpty())
            {
                String exception = "Failed to load environment session \"" + currentDirectory + "\".";
                throw new EnvironmentRequiredException(exception);
            }
        }
        catch (Exception | Error ex)
        {
            Trace.printStackTrace(ex);
        }

        // -------------------------------------------------------
        if (resourceProperties != null && resourceProperties.trim().length() > 0)
        {
            InputStream inputStream = null;
            try
            {
                String relativeResource = FileHelper.buildResource(ReferenceResources, resourceProperties);
                inputStream = RelativeResource.instance().read(relativeResource);

                if (!properties.load(inputStream))
                {
                    String exception = "Unable to find environment resources \"" + relativeResource + "\".";
                    throw new EnvironmentRequiredException(exception);
                }

                // -------------------------------------------------------
                // Database Service
                for (int i = 0; i < 999; i++)
                {
                    String prefix = "resources.db" + i + ".";

                    String alias = properties.value(prefix + "alias");
                    if (alias != null && !alias.isEmpty())
                    {
                        DatabaseSession database = new DatabaseSession();
                        database.setAlias(alias);

                        String adapter = properties.value(prefix + "adapter");
                        DriverAdapter adapterValue = DriverAdapter.valueOf(adapter);
                        database.setAdapter(adapterValue);

                        String server = properties.value(prefix + "server");
                        database.setServer(server);

                        String instance = properties.value(prefix + "instance");
                        database.setInstance(instance);

                        String username = properties.value(prefix + "username");
                        database.setUsername(username);

                        String password = properties.value(prefix + "password");
                        database.setPassword(String.valueOf(password));

                        String dbname = properties.value(prefix + "dbname");
                        database.setDbname(String.valueOf(dbname));

                        try
                        {
                            String port = properties.value(prefix + "port");
                            int portValue = Integer.parseInt(port.replaceAll("[^0-9]", ""));
                            database.setPort(portValue);
                        }
                        catch (Exception | Error ex)
                        {
                            Trace.printStackTrace(ex);
                        }

                        String schema = properties.value(prefix + "schema");
                        database.setSchema(String.valueOf(schema));

                        database(alias, database);
                    }
                }

                // -------------------------------------------------------
                // Database Pooling
                for (DriverAdapter driver : DriverAdapter.values())
                {
                    String prefix = "resources.pool." + driver.name().toLowerCase() + ".";
                    PoolAdapter adapter = new PoolAdapter(driver);

                    try
                    {
                        String enable = properties.value(prefix + "enable");
                        int enableValue = Integer.parseInt(enable.replaceAll("[^0-9]", ""));
                        adapter.setEnable(enableValue);
                    }
                    catch (Exception | Error ex)
                    {
                    }
                    try
                    {
                        String maximum = properties.value(prefix + "maximum");
                        int maximumValue = Integer.parseInt(maximum.replaceAll("[^0-9]", ""));
                        adapter.setMaximum(maximumValue);
                    }
                    catch (Exception | Error ex)
                    {
                    }
                    try
                    {
                        String idle = properties.value(prefix + "idle");
                        int idleValue = Integer.parseInt(idle.replaceAll("[^0-9]", ""));
                        adapter.setIdle(idleValue);
                    }
                    catch (Exception | Error ex)
                    {
                    }
                    try
                    {
                        String minimum = properties.value(prefix + "minimum");
                        int minimumValue = Integer.parseInt(minimum.replaceAll("[^0-9]", ""));
                        adapter.setMinimum(minimumValue);
                    }
                    catch (Exception | Error ex)
                    {
                    }

                    if (adapter.getEnable() > 0)
                    {
                        pool(adapter.getAdapter(), adapter);
                    }
                }
            }
            catch (Exception | Error ex)
            {
                Trace.printStackTrace(ex);
            }
            finally
            {
                try
                {
                    if (inputStream != null)
                    {
                        inputStream.close();
                    }
                }
                catch (Exception | Error ex)
                {
                    Trace.printStackTrace(ex);
                }
            }
        }
    }

    // =======================================================
    private void database(String resource, DatabaseSession value)
    {
        if (resource != null && !resource.isEmpty())
        {
            if (value != null && value.getAdapter() != null)
            {
                databases.put(resource.trim().toUpperCase(), value);
            }
        }
    }

    public DatabaseSession database(String resource)
    {
        DatabaseSession value = null;
        if (resource != null && databases.containsKey(resource.trim().toUpperCase()))
        {
            value = databases.get(resource.trim().toUpperCase());
        }
        return value;
    }

    private void pool(DriverAdapter resource, PoolAdapter value)
    {
        if (resource != null && value != null)
        {
            pool.put(resource, value);
        }
    }

    public PoolAdapter pool(DriverAdapter resource)
    {
        PoolAdapter value = null;
        if (resource != null && pool.containsKey(resource))
        {
            value = pool.get(resource);
        }
        return value;
    }

    // =======================================================
    public String resource(ConfigReference resource)
    {
        return resource(resource.toString());
    }

    public List<String> resources(ConfigReference resource)
    {
        return resources(resource.toString());
    }

    public String resource(String resource)
    {
        return properties.value(resource);
    }

    public List<String> resources(String resource)
    {
        return properties.values(resource);
    }
}
