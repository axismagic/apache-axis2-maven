package org;

import java.util.Properties;

import org.apache.axis2.wsdl.WSDL2Code;
import org.apache.log4j.Logger;

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
            StringBuffer stringBuffer = new StringBuffer().append(System.getProperty("user.dir"))
                    .append(System.getProperty("file.separator")).append("wsdl").append(System.getProperty("file.separator"))
                    .append("AuthenticationWS.wsdl");

            Properties properties = Utils.getProperites();

            WSDL2Code.main(new String[] { "-uri", stringBuffer.toString(), "-l", "java", "-S",
                    properties.getProperty(ConstantsProperty.PROPERTY_WSDL_SOURCE_CLASSES_FOLDER), "-b", "-ss", "-g", "-u", "-ssi", "-ap",
                    "-or", "--noWSDL", "--noBuildXML" });

        } catch (Exception e) {
            logger.fatal(e);
        }
    }
}
