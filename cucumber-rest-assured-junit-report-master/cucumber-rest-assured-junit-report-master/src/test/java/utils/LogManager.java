package utils;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LogManager {

    public Logger logger;
    private static LogManager instance = null;
    public static String ScenarioName="";
    private LogManager()
    {
        Calendar cal= Calendar.getInstance();
        SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy HH_mm_ss");
        if(System.getProperty("ScenarioID") !=null)
        {
            System.setProperty("FileName",System.getProperty("ScenarioName")+"_"+ System.getProperty("ScenarioID")+"-"+sdf.format(cal.getTime())+".Log");
        }
        else
        {
            System.setProperty("FIleName",System.getProperty("ScenarioName")+"_"+sdf.format(cal.getTime())+".Log");
        }
        PropertyConfigurator.configure(System.getProperty("user.dir")+"/src/test/resources/log4j.properties");
        logger=Logger.getLogger("devpinoyLogger");

    }

    public static Logger getInstance()
    {
        if(ScenarioName.trim().equalsIgnoreCase(System.getProperty("ScenarioName"))==false)
        {
            ScenarioName=System.getProperty("ScenarioName");
            resetLogger();
            if(instance==null)
            {
                instance=new LogManager();
            }
        }
        return instance.logger;
    }

    public static void resetLogger() {
        instance = null;
    }


}
