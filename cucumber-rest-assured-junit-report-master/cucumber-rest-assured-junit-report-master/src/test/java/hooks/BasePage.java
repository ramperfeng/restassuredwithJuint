package hooks;

import io.qameta.allure.Attachment;


import utils.LogManagerPreRun;
import org.apache.log4j.Logger;
public class BasePage {
    public Logger logman=null;

    public BasePage()
    {
        logman= LogManagerPreRun.getInstance();
    }

    public void logInfo(String Message)
    {
        logman.info(Message);
        System.out.println("info = "+Message);
    }

    public void logMessage(String Message)
    {
        logman.debug(Message);
        System.out.println("Mesage = "+Message);

    }
    public void logWarning(String Message)
    {
        logman.warn(Message);
        System.out.println("Warning = "+Message);

    }
    public void logError(String ErrorMessage)
    {
        logman.error("\n"+ErrorMessage+"\n");
        System.out.println("Error = "+ErrorMessage);

    }

    public void injectMessageToCucumberReport(String Message)
    {
      //  Hooks.testscenario.write("MESSAGE :"+Message);
        logWarning(Message);
    }
    public void injectWarningMessageToCucumberReport(String Message)
    {
       // Hooks.testscenario.write("WARNING :"+Message);

        logWarning(Message);
    }
    public void injectErrorToCucumberReport(String Message)
    {

       // Hooks.testscenario.write("Message :"+Message);
    }
    @Attachment(value="{0}",type ="text/plain")
    public String allureSaveLogs(String message)
    {
        return message;

    }
}
