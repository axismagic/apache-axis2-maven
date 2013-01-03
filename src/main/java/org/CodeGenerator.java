package org;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.axis2.util.CommandLineOption;
import org.apache.axis2.util.CommandLineOptionParser;
import org.apache.axis2.wsdl.WSDL2Code;
import org.apache.axis2.wsdl.WSDL2Java;
import org.apache.axis2.wsdl.codegen.CodeGenConfiguration;
import org.apache.axis2.wsdl.codegen.CodeGenerationEngine;

public class CodeGenerator {

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        StringBuffer path = new StringBuffer().append(System.getProperty("user.dir")).append(System.getProperty("file.separator"))
                .append("wsdl").append(System.getProperty("file.separator")).append("AuthenticationWS.wsdl");

        // Map<String, CommandLineOption> commandLineOptions = new
        // HashMap<String, CommandLineOption>();
        //
        // CommandLineOption commandLineOption = ;
        //
        // commandLineOptions.put("uri", path.toString());
        // commandLineOptions.put("l", "java");
        // commandLineOptions.put("g", "");
        // commandLineOptions.put("or", "");
        // commandLineOptions.put("ss", "");
        //
        // CommandLineOptionParser commandLineOptionParser = new
        // CommandLineOptionParser(commandLineOptions);
        // CodeGenerationEngine codeGenerationEngine = new
        // CodeGenerationEngine(commandLineOptionParser);
        //
        // codeGenerationEngine.generate();

        WSDL2Code.main(new String[] { "-uri", path.toString(), "-l", "java" });

        WSDL2Code.main(new String[] { "X", "Y" });
    }

}
