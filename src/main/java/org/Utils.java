package org;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Utility class
 * 
 * @author oleksandr_kyetov
 * 
 */
public class Utils {

    /**
     * 
     */
    private static final Logger logger = Logger.getLogger(Utils.class);

    /**
     * Validate properties
     * 
     * @param propertiesClazz
     * @param properties
     * @return Map with invalid properties
     */
    public static Map<String, String> validateProperties(final Class<?> propertiesClazz, final Properties properties) {
        Field[] fields = propertiesClazz.getDeclaredFields();
        Map<String, String> validatedProperties = new HashMap<String, String>();

        try {
            for (Field field : fields) {
                if (properties.getProperty(String.valueOf(field.get(propertiesClazz))) == null
                        || "".equals(properties.getProperty(String.valueOf(field.get(propertiesClazz))))) {
                    validatedProperties.put(field.getName(), String.valueOf(field.get(propertiesClazz)));
                }
            }
        } catch (IllegalArgumentException iare) {
            logger.error(iare);
        } catch (IllegalAccessException iace) {
            logger.error(iace);
        }

        return validatedProperties;
    }

    /**
     * 
     * @return
     * @throws IOException
     */
    public static Properties getProperites() throws IOException {
        // Try to load properties using Class Loader
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Loading properties using Class Loader ...");
            }

            return Utils.getProperitesUsingClassLoader();
        } catch (IOException ioe) {
            if (logger.isDebugEnabled()) {
                logger.debug("attemt failed");
            }

            logger.error(ioe);
        }

        // Try to load properties using User Dir
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Loading properties using User Dir ...");
            }

            return Utils.getProperitesUsingUserDir();
        } catch (Exception ioe) {
            if (logger.isDebugEnabled()) {
                logger.debug("attemt failed");
            }

            logger.error(ioe);
        }

        // If properties can not be read either one of two ways, throw an
        // exception
        logger.error(new IOException("Properties file can not be read"));
        throw new IOException("Properties file can not be read");
    }

    /**
     * 
     * @return
     * @throws IOException
     */
    private static Properties getProperitesUsingUserDir() throws IOException {
        final Properties properties = new Properties();
        final StringBuffer stringBuffer = new StringBuffer().append(System.getProperty("user.dir"))
                .append(System.getProperty("file.separator")).append(ConstantsGlobal.PROPERTIES_FILE_NAME);

        FileReader fileReader = null;
        BufferedReader bufferedReader = null;

        try {
            fileReader = new FileReader(stringBuffer.toString());
            bufferedReader = new BufferedReader(fileReader);

            properties.load(bufferedReader);
        } catch (FileNotFoundException fnfe) {
            logger.error(fnfe);
            throw fnfe;
        } catch (IOException ioe) {
            logger.error(ioe);
            throw ioe;
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (IOException ioe) {
                // Exception during stream closing
                // Do nothing, just write exception into log
                logger.error(ioe);
            }
        }

        return properties;
    }

    /**
     * 
     * @return
     * @throws IOException
     */
    private static Properties getProperitesUsingClassLoader() throws IOException {
        final Properties properties = new Properties();

        InputStream inputStream = null;

        try {
            ClassLoader.getSystemClassLoader();

            inputStream = Utils.class.getClass().getResourceAsStream("/" + ConstantsGlobal.PROPERTIES_FILE_NAME);

            properties.load(inputStream);
        } catch (IOException ioe) {
            logger.error(ioe);
            throw ioe;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException ioe) {
                // Exception during stream closing
                // Do nothing, just write exception into log
                logger.error(ioe);
            }
        }

        return properties;
    }
}
