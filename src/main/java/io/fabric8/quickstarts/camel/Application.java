/**
 *  Copyright 2005-2016 Red Hat, Inc.
 *
 *  Red Hat licenses this file to you under the Apache License, version
 *  2.0 (the "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package io.fabric8.quickstarts.camel;

import org.apache.camel.main.Main;
// import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.nio.file.Paths;
import java.util.Properties;

import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;
import com.sap.conn.jco.ext.DestinationDataProvider;
/**
 * A spring-boot application that includes a Camel route builder to setup the Camel routes
 */
@RestController
@SpringBootApplication
@ImportResource({"classpath:spring/camel-context.xml"})
public class Application /* extends RouteBuilder */  {
    static JCoDestination destination;
    // must have a main method spring-boot can run
    public static void main(String[] args) throws Exception{
        System.out.println("runtimeclasspath: " + System.getProperty("java.class.path"));
       /*  URLClassLoader child = new URLClassLoader(
        new URL[] {new URL("file://{basedir}/src/main/resources/sapjco3.jar")},
        Main.class.getClassLoader()
        );
        Class classToLoad = Class.forName("com.MyClass", true, child);
        Method method = classToLoad.getDeclaredMethod("myMethod");
        Object instance = classToLoad.newInstance();
        Object result = method.invoke(instance); */

        String ABAP_SYSTEM2="ABAP_SYSTEM2";
        Properties connectProperties = new Properties();
        connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, "172.28.3.26");
        connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR,  "11");
        connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, "100");
        connectProperties.setProperty(DestinationDataProvider.JCO_USER,   "sviluppo");
        connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, "pocredhat2020");
        connectProperties.setProperty(DestinationDataProvider.JCO_LANG,   "IT");
        createDataFile(ABAP_SYSTEM2, "jcoDestination", connectProperties);
        
        destination=JCoDestinationManager.getDestination("ABAP_SYSTEM2");
        if(destination == null)
            throw new RuntimeException("Destination not found.");
        JCoFunction function=destination.getRepository().getFunction("STFC_CONNECTION");
        if (function==null)
            throw new RuntimeException("STFC_CONNECTION not found in SAP.");


        try
        {
            function.execute(destination);
        }
        catch (AbapException e)
        {
            System.out.println(e);
        }

        System.out.println("STFC_CONNECTION finished:");
        System.out.println(" Echo: "+function.getExportParameterList().getString("ECHOTEXT"));
        System.out.println(" Response: "+function.getExportParameterList().getString("RESPTEXT"));
        System.out.println(); 

       /*  connectProperties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, "3");
        connectProperties.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT,    "10");
        createDataFile(ABAP_AS_POOLED, "jcoDestination", connectProperties);

        connectProperties.clear();
        connectProperties.setProperty(DestinationDataProvider.JCO_MSHOST, "HOST");
        connectProperties.setProperty(DestinationDataProvider.JCO_R3NAME,  "SID");
        connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, "200");
        connectProperties.setProperty(DestinationDataProvider.JCO_USER,   "USER");
        connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, "PASSWORD");
        connectProperties.setProperty(DestinationDataProvider.JCO_GROUP, "GROUP");
        connectProperties.setProperty(DestinationDataProvider.JCO_LANG,   "en");
        createDataFile(ABAP_MS, "jcoDestination", connectProperties); */
    
        
        SpringApplication.run(Application.class, args);
    }

    @GetMapping("/greeting")
	public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) throws JCoException {

        JCoFunction function=destination.getRepository().getFunction("BAPI_FLCUST_GETLIST");
        if (function==null){
            throw new RuntimeException("BAPI_FLCUST_GETLIST not found in SAP.");
        }
        function.getImportParameterList().setValue("CUSTOMER_NAME", "*");
        function.getImportParameterList().setValue("MAX_ROWS", 10);
        function.getImportParameterList().setValue("WEB_USER", "*");
        

        try
        {
            function.execute(destination);
        }
        catch (AbapException e)
        {
            System.out.println(e);
            return e.getMessage();
        }
        JCoTable tableDetail = function.getTableParameterList().getTable("RETURN");
        String value = "";
        do{
            value = tableDetail.getString("MESSAGE");
            System.out.println((tableDetail.getString("NUMBER")));
            System.out.println((tableDetail.getString("MESSAGE")));
        }while(tableDetail.nextRow());


        return " Echo: " + value;
        /* return "ciao"; */
    }
    
    static void createDataFile(String name, String suffix, Properties properties)
    {
        File cfg = new File(name+"."+suffix);
        if(!cfg.exists())
        {
            try
            {
                FileOutputStream fos = new FileOutputStream(cfg, false);
                properties.store(fos, "for tests only !");
                fos.close();
            }
            catch (Exception e)
            {
                throw new RuntimeException("Unable to create the destination file " + cfg.getName(), e);
            }
        }
    }
}
