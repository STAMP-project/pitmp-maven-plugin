package eu.stamp_project.examples.dnoo.dnooIntegration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import java.io.File;

import eu.stamp_project.examples.dnoo.dnooHello.HelloApp;
import eu.stamp_project.examples.dnoo.dnooStorage.MyStorage;
import eu.stamp_project.examples.dnoo.dnooLogs.MyLogger;

public class Session1BisTest
{
   // **********************************************************************
   // public
   // **********************************************************************
   @Test
   public void testSessionBis1() throws Exception
   {
      HelloApp myApp = null;
      File theFile = null;
      MyStorage fileContent = null;
      int myCount = 9;
      String countString = Integer.toString(myCount);
      String helloString = "--------- Hello World !";
      String myTracesName = "session51.traces";
      MyStorage logsContent = null;

      MyLogger.clearLogs();
      MyLogger.LogFileName = "session51.log";
      MyLogger.getLogger().info("testing default logs level");
      assertEquals("WARNING", MyLogger.getLevel());
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

      // MyLogger.setLevel("OFF");
      // check logs file content
      logsContent = new MyStorage(MyLogger.LogFileName);
      logsContent.readData();
      assertEquals(4, logsContent.getDataSize());
      assertEquals("SEVERE: checking log level: severe", logsContent.getData(1));
      assertEquals("WARNING: checking log level: warning", logsContent.getData(3));
   }
}
