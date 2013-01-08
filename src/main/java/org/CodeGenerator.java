package org;

import java.util.Properties;

import org.apache.axis2.wsdl.WSDL2Code;
import org.apache.log4j.Logger;

/**
 * 
 * @author oleksandr_kyetov
 * 
 */
public class CodeGenerator {

    /**
     * 
     */
    private static final Logger logger = Logger.getLogger(CodeGenerator.class);

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) {
        try {
            Properties properties = Utils.getProperites();

            // Call center WSDL path
            StringBuilder callCenterWSDLPath = new StringBuilder()
                    .append(properties.getProperty(ConstantsProperty.PROPERTY_CALL_CENTER_PROTOCOL)).append("://")
                    .append(properties.getProperty(ConstantsProperty.PROPERTY_CALL_CENTER_SERVER_ADDRESS)).append(":")
                    .append(properties.getProperty(ConstantsProperty.PROPERTY_CALL_CENTER_SERVER_PORT)).append("/")
                    .append(properties.getProperty(ConstantsProperty.PROPERTY_CALL_CENTER_POINT)).append("/")
                    .append(properties.getProperty(ConstantsProperty.PROPERTY_CALL_CENTER_AUTHENTICATION_WSDL)).append("?wsdl");

            /*
             * For details execute as WSDL2Code.main(new String[] { "" } ); and
             * see usage details
             */
            WSDL2Code.main(new String[] { "-uri", callCenterWSDLPath.toString(), "-l", "java", "-d", "adb", "-S",
                    properties.getProperty(ConstantsProperty.PROPERTY_WSDL_SOURCE_CLASSES_FOLDER), "-ss", "-ssi", "-g", "-u", "-or",
                    "--noWSDL", "--noBuildXML" });

            // Web WSDL path
            StringBuilder webWSDLPath = new StringBuilder().append(properties.getProperty(ConstantsProperty.PROPERTY_WEB_PROTOCOL))
                    .append("://").append(properties.getProperty(ConstantsProperty.PROPERTY_WEB_SERVER_ADDRESS)).append(":")
                    .append(properties.getProperty(ConstantsProperty.PROPERTY_WEB_SERVER_PORT)).append("/")
                    .append(properties.getProperty(ConstantsProperty.PROPERTY_WEB_POINT)).append("/")
                    .append(properties.getProperty(ConstantsProperty.PROPERTY_WEB_AUTHENTICATION_WSDL)).append("?wsdl");

            /*
             * For details execute as WSDL2Code.main(new String[] { "" } ); and
             * see usage details
             */
            WSDL2Code.main(new String[] { "-uri", webWSDLPath.toString(), "-l", "java", "-d", "adb", "-S",
                    properties.getProperty(ConstantsProperty.PROPERTY_WSDL_SOURCE_CLASSES_FOLDER), "-ss", "-ssi", "-g", "-u", "-or",
                    "--noWSDL", "--noBuildXML" });
        } catch (Exception e) {
            logger.fatal(e);
        }
    }
}
