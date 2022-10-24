package utils;

import java.io.*;
import java.util.Properties;

public class configReader {
    private Properties properties;
    private static configReader configReader;

    public configReader() {
        BufferedReader reader;
        String propertyFilePath = "D:\\GITPractice\\cucumber-rest-assured-junit-report-master\\cucumber-rest-assured-junit-report-master\\src\\test\\resources\\applicationProperties\\config.properties";
        try {
            reader = new BufferedReader(new FileReader(propertyFilePath));
            properties = new Properties();
            try {
                properties.load(reader);
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Configuration.properties not found at " + propertyFilePath);
        }
    }

    public static configReader getInstance() {
        if (configReader == null) {
            configReader = new configReader();
        }
        return configReader;
    }

    public String getENVIRONMENT() {
        String env = properties.getProperty("ENVIRONMENT");
        if (env != null) return env;
        else throw new RuntimeException("base_Url not specified in the Configuration.properties file.");
    }


}