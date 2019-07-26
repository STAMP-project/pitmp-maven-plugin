package eu.stamp_project.examples.dhell;

// **********************************************************************
import java.util.logging.*;
import java.io.IOException;

// **********************************************************************
public class MyLogger
{
    // **********************************************************************
    // public
    // **********************************************************************
    // ******** attributes
    public static Logger Instance = Logger.getLogger(MyLogger.class.getName());

    // ******** methods
    // **********************************************************************
    public static void initLogs(String level)
    {
        Handler logHandler = null;
        Formatter logFormater = null;
        boolean fileCreated = false;

        Instance.setUseParentHandlers(false);

        try
        {
            logHandler = new FileHandler("dhell.log", false);
            logHandler.setFormatter(new SimpleFormatter());
            fileCreated = true;
        }
        catch (Exception e)
        {
            logHandler = new ConsoleHandler();
        }
        Instance.addHandler(logHandler);

        logHandler.setLevel(Level.ALL);
        Instance.setLevel(stringToLevel(level));

        if (fileCreated)
        {
            Instance.info("Log file created");
        }
        else
        {
            Instance.warning("Cannot create log file, redirecting logs to the console");
        }
        Instance.severe("checking log level: severe: Exiting initLogs");
        Instance.warning("checking log level: warning: Exiting initLogs");
        Instance.info("checking log level: info: Exiting initLogs");
        Instance.config("checking log level: config: Exiting initLogs");
        Instance.fine("checking log level: fine: Exiting initLogs");
        Instance.finer("checking log level: finer: Exiting initLogs");
        Instance.finest("checking log level: finest: Exiting initLogs");
    }

    // **********************************************************************
    public static Level stringToLevel(String level)
    {
        // levels: OFF, SEVERE, WARNING, INFO, CONFIG, FINE, FINER, FINEST, ALL
        Level theLevel = Level.WARNING;

        if (level.equals("OFF"))
        {
            theLevel = Level.OFF;
        }
        else if (level.equals("SEVERE"))
        {
            theLevel = Level.SEVERE;
        }
        else if (level.equals("WARNING"))
        {
            theLevel = Level.WARNING;
        }
        else if (level.equals("INFO"))
        {
            theLevel = Level.INFO;
        }
        else if (level.equals("CONFIG"))
        {
            theLevel = Level.CONFIG;
        }
        else if (level.equals("FINE"))
        {
            theLevel = Level.FINE;
        }
        else if (level.equals("FINER"))
        {
            theLevel = Level.FINER;
        }
        else if (level.equals("FINEST"))
        {
            theLevel = Level.FINEST;
        }
        else if (level.equals("ALL"))
        {
            theLevel = Level.ALL;
        }

        return(theLevel);
    }

    // **********************************************************************
    // private
    // **********************************************************************
}
