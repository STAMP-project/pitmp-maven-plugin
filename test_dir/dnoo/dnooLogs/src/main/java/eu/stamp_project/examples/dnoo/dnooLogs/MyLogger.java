package eu.stamp_project.examples.dnoo.dnooLogs;

import java.io.File;
import java.util.Locale;
// **********************************************************************
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

// **********************************************************************
public class MyLogger
{
   // **********************************************************************
   // public
   // **********************************************************************
   // ******** attributes
   public static String LogFileName = "dnoo.log";

   // **********************************************************************
   // Level: String, public read/write class attribute
   // levels: "OFF", "SEVERE", "WARNING", "INFO", "CONFIG", "FINE", "FINER",
   //   "FINEST", "ALL"
   public static String getLevel()
   {
      Level currentLevel = getLogger().getLevel();
      return(levelToString(currentLevel));
   }

   // ***********
   public static void setLevel(String level)
   {
      getLogger().setLevel(stringToLevel(level));
      Instance.StdLogHandler.setLevel(stringToLevel(level));
   }

   // **********************************************************************
   // ******** methods
   public static Logger getLogger()
   {
      initLogs();
      return(Instance.StandardLogger);
   }

   // **********************************************************************
   public static void clearLogs()
   {
      File theFile = null;

      if (Instance != null)
      {
         Instance.StdLogHandler = null;
         Instance.StandardLogger = null;
         Instance = null;
      }
      // delete file if it already exists
      theFile = new File(LogFileName);
      if (theFile.exists())
      {
         theFile.delete();
      }

      return;
   }

   // **********************************************************************
   // protected
   // **********************************************************************
   // ******** attributes
   protected static MyLogger Instance = null;
   protected Logger StandardLogger = null;
   protected Handler StdLogHandler = null;
   protected MyUselessClass MyValues = null;

   // **********************************************************************
   // ******** methods
   protected static Level stringToLevel(String level)
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
   protected static String levelToString(Level level)
   {
      // levels: OFF, SEVERE, WARNING, INFO, CONFIG, FINE, FINER, FINEST, ALL
      String theLevel = "WARNING";

      if (level == Level.OFF)
      {
         theLevel = "OFF";
      }
      else if (level == Level.SEVERE)
      {
         theLevel = "SEVERE";
      }
      else if (level == Level.WARNING)
      {
         theLevel = "WARNING";
      }
      else if (level == Level.INFO)
      {
         theLevel = "INFO";
      }
      else if (level == Level.CONFIG)
      {
         theLevel = "CONFIG";
      }
      else if (level == Level.FINE)
      {
         theLevel = "FINE";
      }
      else if (level == Level.FINER)
      {
         theLevel = "FINER";
      }
      else if (level == Level.FINEST)
      {
         theLevel = "FINEST";
      }
      else if (level == Level.ALL)
      {
         theLevel = "ALL";
      }

      return(theLevel);
   }

   // **********************************************************************
   protected static void initLogs()
   {
      Formatter logFormater = null;
      boolean fileCreated = false;
      Locale.setDefault(Locale.ENGLISH);

      if (Instance == null)
      {
         Instance = new MyLogger();
         Instance.StandardLogger = Logger.getLogger(MyLogger.class.getName());
         Instance.StandardLogger.setUseParentHandlers(false);
         Instance.MyValues = new MyUselessClass();
   
         try
         {
            Instance.StdLogHandler = new FileHandler(LogFileName, false);
            Instance.StdLogHandler.setFormatter(new SimpleFormatter());
            fileCreated = true;
         }
         catch (Exception e)
         {
            Instance.StdLogHandler = new ConsoleHandler();
         }
         Instance.StandardLogger.addHandler(Instance.StdLogHandler);
   
         Instance.StandardLogger.setLevel(Level.WARNING);
         Instance.StdLogHandler.setLevel(Level.WARNING);
   
         if (fileCreated)
         {
            Instance.StandardLogger.info("Log file created");
            Instance.MyValues.setAll();
         }
         else
         {
            Instance.MyValues.clearAll();
            Instance.StandardLogger.warning
               ("Cannot create log file, redirecting logs to the console");
         }
      }
   }
}
