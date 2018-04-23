package eu.stamp_project.examples.dnoo.dnooMain;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import java.io.File;

import eu.stamp_project.examples.dnoo.dnooHello.HelloApp;
import eu.stamp_project.examples.dnoo.dnooStorage.MyStorage;
import eu.stamp_project.examples.dnoo.dnooLogs.MyLogger;

public class Session2Test
{
   // **********************************************************************
   // public
   // **********************************************************************
   @Test
   public void testSession2() throws Exception
   {
      HelloApp myApp = null;
      File theFile = null;
      MyStorage fileContent = null;
      int myCount = 2;
      String countString = Integer.toString(myCount);
      String helloString = "-- Hello World !";
      String myTracesName = "session2.traces";
      MyStorage logsContent = null;

      MyLogger.clearLogs();
      MyLogger.LogFileName = "session2.log";
      MyLogger.setLevel("FINE");
      MyLogger.getLogger().info("testing logs level FINE");
      assertEquals(MyLogger.getLevel(), "FINE");

      MyLogger.getLogger().severe("checking log level: severe");
      MyLogger.getLogger().warning("checking log level: warning");
      MyLogger.getLogger().info("checking log level: info");
      MyLogger.getLogger().config("checking log level: config");
      MyLogger.getLogger().fine("checking log level: fine");
      MyLogger.getLogger().finer("checking log level: finer");
      MyLogger.getLogger().finest("checking log level: finest");

      myApp = new HelloApp(myCount, myTracesName);
      myApp.run();

      fileContent = new MyStorage(myApp.getMyTracesName());
      fileContent.readData();
      assertEquals(true, fileContent.isEqual(myApp.getMyTraces()));
      assertEquals(true, countString.equals(fileContent.getData(0)));
      assertEquals(true, helloString.equals(fileContent.getData(2)));

      MyLogger.setLevel("OFF");
      // check logs file content
      logsContent = new MyStorage(MyLogger.LogFileName);
      logsContent.readData();
      assertEquals(16, logsContent.getDataSize());
      assertEquals("INFO: testing logs level FINE", logsContent.getData(1));
      assertEquals("SEVERE: checking log level: severe", logsContent.getData(3));
      assertEquals("WARNING: checking log level: warning", logsContent.getData(5));
      assertEquals("INFO: checking log level: info", logsContent.getData(7));
      assertEquals("CONFIG: checking log level: config", logsContent.getData(9));
      assertEquals("FINE: checking log level: fine", logsContent.getData(11));
      assertEquals("INFO: MyPrintCount = 2 - MyTracesName = session2.traces",
         logsContent.getData(13));
      assertEquals("FINE: indent = '--'", logsContent.getData(15));
   }
}
