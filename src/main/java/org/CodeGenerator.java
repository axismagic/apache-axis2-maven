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

            StringBuffer wsdlPath = new StringBuffer().append(properties.getProperty(ConstantsProperty.PROPERTY_CALL_CENTER_PROTOCOL))
                    .append("://").append(properties.getProperty(ConstantsProperty.PROPERTY_CALL_CENTER_SERVER_ADDRESS)).append(":")
                    .append(properties.getProperty(ConstantsProperty.PROPERTY_CALL_CENTER_SERVER_PORT)).append("/")
                    .append(properties.getProperty(ConstantsProperty.PROPERTY_CALL_CENTER_POINT)).append("/")
                    .append(properties.getProperty(ConstantsProperty.PROPERTY_CALL_CENTER_AUTHENTICATION_WSDL)).append("?wsdl");

            /*
             * Used options: -uri <url or path> : A url or path to a WSDL -l
             * <language> Valid languages are java and c -S <path> Specify a
             * directory path for generated source -b Generate Axis 1.x backward
             * compatible code -ss Generate server side code (i.e. skeletons)
             * -sd Generate service descriptor (i.e. services.xml) -d
             * <databinding> Valid databinding(s) are adb, xmlbeans, jibx and
             * jaxbri -g Generates all the classes -u Unpacks the databinding
             * classes -ssi Generate an interface for the service implementation
             * -ap Generate code for all ports -or Overwrite the existing
             * classes --noBuildXML Dont generate the build.xml in the output
             * directory --noWSDL Dont generate WSDLs in the resources directory
             */
            WSDL2Code.main(new String[] { "-uri", wsdlPath.toString(), "-l", "java", "-S",
                    properties.getProperty(ConstantsProperty.PROPERTY_WSDL_SOURCE_CLASSES_FOLDER), "-ss", "-ssi", "-g", "-u", "-or",
                    "--noWSDL", "--noBuildXML" });
        } catch (Exception e) {
            logger.fatal(e);
        }
    }
}
