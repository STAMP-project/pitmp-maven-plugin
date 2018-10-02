package eu.stamp_project.examples.dnoo.dnooMain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import java.io.File;

import eu.stamp_project.examples.dnoo.dnooHello.HelloApp;
import eu.stamp_project.examples.dnoo.dnooStorage.MyStorage;
import eu.stamp_project.examples.dnoo.dnooLogs.MyLogger;

public class GlobalAppTest
{
   // **********************************************************************
   // public
   // **********************************************************************
   @Test
   public void testGlobalAppRun1() throws Exception
   {
      HelloApp myApp = null;
      File theFile = null;
      MyStorage fileContent = null;
      int myCount = 11;
      String countString = Integer.toString(myCount);
      String helloString = "----------- Hello World !";
      String myTracesName = "global1.traces";

      MyLogger.clearLogs();

      myApp = new HelloApp(myCount, myTracesName);
      myApp.run();

      fileContent = new MyStorage(myApp.getMyTracesName());
      fileContent.readData();
      assertEquals(true, fileContent.isEqual(myApp.getMyTraces()));
      assertEquals(true, countString.equals(fileContent.getData(0)));
      assertEquals(true, helloString.equals(fileContent.getData(2)));
   }

   // **********************************************************************
   @Test
   public void testGlobalAppRun2() throws Exception
   {
      HelloApp myApp = null;
      File theFile = null;
      MyStorage fileContent = null;
      int myCount = 8;
      String countString = Integer.toString(myCount);
      String helloString = "-------- Hello World !";
      String myTracesName = "global2.traces";

      MyLogger.clearLogs();

      myApp = new HelloApp(myCount, myTracesName);
      myApp.run();

      fileContent = new MyStorage(myApp.getMyTracesName());
      fileContent.readData();
      assertEquals(true, fileContent.isEqual(myApp.getMyTraces()));
      assertEquals(true, countString.equals(fileContent.getData(0)));
      assertEquals(true, helloString.equals(fileContent.getData(2)));
   }
}
