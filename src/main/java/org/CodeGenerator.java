package org;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import javax.xml.rpc.Service;

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
            /*
             * General properties
             */
            Properties properties = Utils.getProperites();

            /*
             * Call center WSDL path
             */
            StringBuilder callCenterWSDLPath = new StringBuilder().append(properties.getProperty(ConstantsProperty.CALL_CENTER_PROTOCOL))
                    .append("://").append(properties.getProperty(ConstantsProperty.CALL_CENTER_SERVER_ADDRESS)).append(":")
                    .append(properties.getProperty(ConstantsProperty.CALL_CENTER_SERVER_PORT)).append("/")
                    .append(properties.getProperty(ConstantsProperty.CALL_CENTER_POINT)).append("/")
                    .append(properties.getProperty(ConstantsProperty.CALL_CENTER_AUTHENTICATION_WSDL)).append("?wsdl");

            /*
             * Path to generated from WSDL java sources
             */
            StringBuilder javaSourcePath = new StringBuilder().append(System.getProperty("user.dir"))
                    .append(System.getProperty("file.separator")).append(properties.getProperty(ConstantsProperty.WSDL_SOURCE_FOLDER));

            /*
             * Path to compiled WSDL java classes
             */
            StringBuilder javaCompilePath = new StringBuilder().append(System.getProperty("user.dir"))
                    .append(System.getProperty("file.separator")).append(properties.getProperty(ConstantsProperty.WSDL_CLASSES_FOLDER));

            /*
             * For details execute as WSDL2Code.main(new String[] { "" } ); and see usage details
             */
            WSDL2Code.main(new String[] { "-uri", callCenterWSDLPath.toString(), "-l", "java", "-d", "adb", "-S",
                    properties.getProperty(ConstantsProperty.WSDL_SOURCE_FOLDER), "-ss", "-ssi", "-g", "-u", "-or", "--noWSDL",
                    "--noBuildXML" });

            /*
             * Get folder for WSDL generated java sources
             */
            File sourceDirectory = new File(javaSourcePath.append(System.getProperty("file.separator"))
                    .append(properties.getProperty(ConstantsProperty.WSDL_PACKAGE).replace(".", System.getProperty("file.separator")))
                    .toString());

            /*
             * Create directory for compiled classes
             */
            File compileDirectory = new File(javaCompilePath.toString());
            if (!compileDirectory.exists()) {
                compileDirectory.mkdirs();
            }

            /*
             * Get list of java sources generated from WSDL
             */
            String[] javaSources = sourceDirectory.list(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith(".java");
                }
            });

            /*
             * Convert sources list to its full pathes
             */
            for (int i = 0; i < javaSources.length; i++) {
                javaSources[i] = javaSourcePath.toString() + System.getProperty("file.separator") + javaSources[i];
            }

            if (logger.isDebugEnabled()) {
                logger.debug("List of files generated from " + properties.getProperty(ConstantsProperty.CALL_CENTER_AUTHENTICATION_WSDL)
                        + " to be compiled:");
                for (String javaSource : javaSources) {
                    logger.debug(javaSource);
                }
            }

            /*
             * Compose arguments for java compiler
             */
            List<String> arguments = new ArrayList<String>();
            // Output directory
            arguments.add("-d");
            arguments.add(compileDirectory.toString());
            // Current class path
            arguments.add("-cp");
            arguments.add(System.getProperty("java.class.path"));
            // Enable all warnings
            arguments.add("-Xlint:all");
            // Add sources which need to be compiled
            for (String javaSource : javaSources) {
                arguments.add(javaSource);
            }

            if (logger.isDebugEnabled()) {
                logger.debug("Call java compiler with next arguments:");
                logger.debug(arguments);
            }

            /*
             * Compile java sources
             */
            JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
            javaCompiler.run(null, null, null, arguments.toArray(new String[0]));

            /*
             * Load AuthenticationWSBindingStub class generated from AuthenticationWS
             */
            Class<?> authenticationWSBindingStubClass = ClassLoader.getSystemClassLoader().loadClass(
                    properties.getProperty(ConstantsProperty.WSDL_PACKAGE) + "."
                            + properties.getProperty(ConstantsProperty.AUTHENTICATION_WS_BINDING_STUB));

            /*
             * Load AuthenticationWSServiceLocator class generated from AuthenticationWS
             */
            Class<?> authenticationWSServiceLocatorClass = ClassLoader.getSystemClassLoader().loadClass(
                    properties.getProperty(ConstantsProperty.WSDL_PACKAGE) + "."
                            + properties.getProperty(ConstantsProperty.AUTHENTICATION_WS_BINDING_SERVICE_LOCATOR));

            /*
             * Load AuthenticationResult class generated from AuthenticationWS
             */
            Class<?> authenticationResultClass = ClassLoader.getSystemClassLoader().loadClass(
                    properties.getProperty(ConstantsProperty.WSDL_PACKAGE) + "."
                            + properties.getProperty(ConstantsProperty.AUTHENTICATION_WS_AUTHENTICATION_RESULT));

            /*
             * Load CredentialsDTO class generated from AuthenticationWS
             */
            Class<?> CredentialsDTOClass = ClassLoader.getSystemClassLoader().loadClass(
                    properties.getProperty(ConstantsProperty.WSDL_PACKAGE) + "."
                            + properties.getProperty(ConstantsProperty.AUTHENTICATION_WS_CREDENTIALS_DTO));

            /*
             * Get constructor AuthenticationWSBindingStub(URL, Service)
             */
            Constructor<?> authenticationWSBindingStubConstructor = authenticationWSBindingStubClass.getDeclaredConstructor(URL.class,
                    Service.class);
            authenticationWSBindingStubConstructor.setAccessible(true);

            /*
             * Get constructor AuthenticationWSServiceLocator()
             */
            Constructor<?> authenticationWSServiceLocatorConstructor = authenticationWSServiceLocatorClass.getDeclaredConstructor();
            authenticationWSServiceLocatorConstructor.setAccessible(true);

            /*
             * Get constructor AuthenticationResult()
             */
            Constructor<?> authenticationResultConstructor = authenticationResultClass.getDeclaredConstructor();
            authenticationResultConstructor.setAccessible(true);

            /*
             * Get constructor CredentialsDTO(String, String)
             */
            Constructor<?> credentialsDTOConstructor = CredentialsDTOClass.getDeclaredConstructor(String.class, String.class);
            credentialsDTOConstructor.setAccessible(true);

            /*
             * Instantiate AuthenticationWSBindingStub object
             */
            Object authenticationWSBindingStubObject = authenticationWSBindingStubConstructor.newInstance(
                    new URL(callCenterWSDLPath.toString()), authenticationWSServiceLocatorConstructor.newInstance());

            /*
             * Get login(CredentialsDTO) method of AuthenticationWSBindingStub object
             */
            Method authenticationWSBindingStubObjectLoginMethod = authenticationWSBindingStubObject.getClass().getMethod("login",
                    CredentialsDTOClass);

            /*
             * Invoke login(CredentialsDTO) method of AuthenticationWSBindingStub
             */
            Object authenticationResultObject = authenticationWSBindingStubObjectLoginMethod.invoke(
                    authenticationWSBindingStubObject,
                    credentialsDTOConstructor.newInstance(properties.getProperty(ConstantsProperty.CALL_CENTER_LOGIN),
                            properties.getProperty(ConstantsProperty.CALL_CENTER_PASSWORD)));

            /*
             * Get isSuccessful() method of AuthenticationResultObject object
             */
            Method authenticationResultObjectIsSuccessfulMethod = authenticationResultObject.getClass().getMethod("isSuccessful");

            /*
             * Verify authentication results
             */
            if (Boolean.valueOf(String.valueOf(authenticationResultObjectIsSuccessfulMethod.invoke(authenticationResultObject)))) {
                /*
                 * Get getSessionID() method of AuthenticationResultObject object
                 */
                Method authenticationResultObjectGetSessionIdMethod = authenticationResultObject.getClass().getMethod("getSessionID");

                /*
                 * Invoke getSessionID() method of AuthenticationResultObject
                 */
                String sessionId = String.valueOf(authenticationResultObjectGetSessionIdMethod.invoke(authenticationResultObject));

                /*
                 * Get getWebServices() method of AuthenticationResultObject object
                 */
                Method authenticationResultObjectGetWebServicesMethod = authenticationResultObject.getClass().getMethod("getWebServices");

                /*
                 * Invoke getWebServices() method of AuthenticationResultObject
                 */
                String[] webServices = (String[]) authenticationResultObjectGetWebServicesMethod.invoke(authenticationResultObject);

                if (logger.isDebugEnabled()) {
                    for (String webService : webServices) {
                        logger.debug("Returned WebServices:");
                        logger.debug(webService);
                    }
                }

                /*
                 * WSDL point path
                 */
                StringBuilder WSDLPath = new StringBuilder().append(properties.getProperty(ConstantsProperty.CALL_CENTER_PROTOCOL))
                        .append("://").append(properties.getProperty(ConstantsProperty.CALL_CENTER_SERVER_ADDRESS)).append(":")
                        .append(properties.getProperty(ConstantsProperty.CALL_CENTER_SERVER_PORT)).append("/")
                        .append(properties.getProperty(ConstantsProperty.CALL_CENTER_POINT)).append("/");

                /*
                 * Generate remaining WSDL
                 */
                for (String webService : webServices) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Working with WebService:");
                        logger.debug(WSDLPath.toString() + webService + ";jsessionid=" + sessionId + "?wsdl");
                    }

                    /*
                     * For details execute as WSDL2Code.main(new String[] { "" } ); and see usage details
                     */
                    WSDL2Code.main(new String[] { "-uri", WSDLPath.toString() + webService + ";jsessionid=" + sessionId + "?wsdl", "-l",
                            "java", "-d", "adb", "-S", properties.getProperty(ConstantsProperty.WSDL_SOURCE_FOLDER), "-ss", "-ssi", "-g",
                            "-u", "-or", "--noWSDL", "--noBuildXML" });
                }
            } else {
                throw new RuntimeException("User is not authenticated");
            }

            // // Web WSDL path
            // StringBuilder webWSDLPath = new
            // StringBuilder().append(properties.getProperty(ConstantsProperty.WEB_PROTOCOL)).append("://")
            // .append(properties.getProperty(ConstantsProperty.WEB_SERVER_ADDRESS)).append(":")
            // .append(properties.getProperty(ConstantsProperty.WEB_SERVER_PORT)).append("/")
            // .append(properties.getProperty(ConstantsProperty.WEB_POINT)).append("/")
            // .append(properties.getProperty(ConstantsProperty.WEB_AUTHENTICATION_WSDL)).append("?wsdl");
            //
            // /*
            // * For details execute as WSDL2Code.main(new String[] { "" } );
            // and
            // * see usage details
            // */
            // WSDL2Code.main(new String[] { "-uri", webWSDLPath.toString(), "-l", "java", "-d", "adb", "-S",
            // properties.getProperty(ConstantsProperty.WSDL_SOURCE_FOLDER), "-ss", "-ssi", "-g", "-u", "-or",
            // "--noWSDL", "--noBuildXML" });
        } catch (Exception e) {
            logger.fatal(e);
        }
    }
}
