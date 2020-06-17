package io.fabric8.quickstarts.camel;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

import com.sap.conn.jco.ext.DestinationDataProvider;

//import com.sap.conn.jco.ext.DestinationDataProvider;

public class DestinationConcept {
    
    public static class SomeSampleDestinations
    {

        public static final String ABAP_SYSTEM1="ABAP_SYSTEM1";
        public static final String ABAP_MS="ABAP_MS";
        public static final String ABAP_WS="ABAP_WS";
        static
    {
        Properties connectProperties = new Properties();
        connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, "172.28.3.26");
        connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR,  "11");
        connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, "100");
        connectProperties.setProperty(DestinationDataProvider.JCO_USER,   "sviluppo");
        connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, "pocredhat2020");
        connectProperties.setProperty(DestinationDataProvider.JCO_LANG,   "IT");
        createDataFile(ABAP_SYSTEM1, "jcoDestination", connectProperties);

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
    }

        // public static final String ABAP_AS2 = "<put any name for the destination here>";

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
}