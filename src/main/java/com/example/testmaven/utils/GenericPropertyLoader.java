package com.example.testmaven.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Properties;

/**
 * @apiNote Singleton. Generic configuration updater from properties file.
 *  If required, loadProperties() may be executed periodically.
 * @implNote IMPORTANT. No specific validations are applied.
 * @author Gadiel
 */
public class GenericPropertyLoader {

    private static final Logger logger
            = LoggerFactory.getLogger(GenericPropertyLoader.class);
    private static String PROPERTIES_FILE = "application.properties";

    private static GenericPropertyLoader propertyLoader = null;

    public static GenericPropertyLoader getInstance() {
        if (propertyLoader == null) {
            propertyLoader = new GenericPropertyLoader();
        }
        return propertyLoader;
    }

    public GenericPropertyLoader() {
    }


    public void loadProperties() {
        // create and load default properties
        try (
                FileInputStream in = new FileInputStream(PROPERTIES_FILE);
        ) {
            Properties properties = new Properties();
            properties.load(in);
            initConstantsFromPropertiesFile(properties);
        } catch (IOException e) {
            logger.warn("Properties file not found, using default values...");
        }

        loadPortFromSystemProperties();

    }

    public void loadPortFromSystemProperties() {
        try {
            Constants.SERVER_PORT = Integer.parseInt(System.getProperty("http.port"));
            logger.info("Overriding port number through system property");
        } catch (NumberFormatException | NullPointerException e) {
            logger.info("No port defined by command line, using file from properties, or default value");
        }
    }

    private void initConstantsFromPropertiesFile(Properties properties) {

        for (Field field : Constants.class.getDeclaredFields())
            if (Modifier.isStatic(field.getModifiers())) {
                try {
                    field.set(null, getValue(properties, field.getName(), field.getType()));
                } catch (IllegalAccessException e) {
                    logger.warn(e.getMessage());
                }
            }
    }

    private Object getValue(Properties properties, String name, Class<?> type) {
        String value = properties.getProperty(name);
        if (value == null)
            throw new IllegalArgumentException("Property not set in file, using default value for property: " + name);
        if (type == String.class)
            return value;
        if (type == boolean.class)
            return Boolean.parseBoolean(value);
        if (type == int.class)
            return Integer.parseInt(value);
        if (type == float.class)
            return Float.parseFloat(value);
        if (type == long.class)
            return Long.parseLong(value);
        throw new IllegalArgumentException("Unknown configuration value type: " + type.getName());
    }
}

