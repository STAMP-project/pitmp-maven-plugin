package eu.stamp_project.examples.dhell;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import java.io.File;

import eu.stamp_project.examples.dhell.HelloApp;

public class HelloAppTest
{
   // **********************************************************************
   // public
   // **********************************************************************
   @BeforeAll
   public static void runOnceAtTheBeginning()
   {
       System.out.println("dhell5.HelloAppTest: @BeforeAll - runOnceAtTheBeginning");
   }

   @AfterAll
   public static void runOnceAtTheEnd()
   {
       System.out.println("dhell5.HelloAppTest: @AfterAll - runOnceAtTheEnd");
   }

   @Test
   public void testHelloAppDefault() throws Exception
   {
      HelloApp myApp = null;
      File theFile = null;

      myApp = new HelloApp();
      assertEquals(1, myApp.getMyPrintCount());
      assertEquals("myHelloApp.traces", myApp.getMyTracesName());
      assertEquals(0, myApp.cardMyTraces());

      theFile = new File(myApp.getMyTracesName());
      assertEquals(false, theFile.exists());
   }

   @Test
   public void testHelloAppInt() throws Exception
   {
      HelloApp myApp = null;
      File theFile = null;
      int myCount = 8;

      myApp = new HelloApp(myCount);
      assertEquals(myCount, myApp.getMyPrintCount());
      assertEquals("myHelloApp.traces", myApp.getMyTracesName());
      assertEquals(0, myApp.cardMyTraces());

      theFile = new File(myApp.getMyTracesName());
      assertEquals(false, theFile.exists());
   }

   @Test
   public void testHelloAppString() throws Exception
   {
      HelloApp myApp = null;
      File theFile = null;
      String MyTracesName = "foo1.traces";

      myApp = new HelloApp(MyTracesName);
      assertEquals(1, myApp.getMyPrintCount());
      assertEquals(MyTracesName, myApp.getMyTracesName());
      assertEquals(0, myApp.cardMyTraces());

      theFile = new File(myApp.getMyTracesName());
      assertEquals(false, theFile.exists());
   }

   @Test
   public void testHelloAppIntString() throws Exception
   {
      HelloApp myApp = null;
      File theFile = null;
      int myCount = 8;
      String MyTracesName = "foo2.traces";

      myApp = new HelloApp(myCount, MyTracesName);
      assertEquals(myCount, myApp.getMyPrintCount());
      assertEquals(MyTracesName, myApp.getMyTracesName());
      assertEquals(0, myApp.cardMyTraces());

      theFile = new File(myApp.getMyTracesName());
      assertEquals(false, theFile.exists());
   }

   @Test
   public void testHelloAppRun1() throws Exception
   {
      HelloApp myApp = null;
      File theFile = null;
      MyStorage fileContent = null;
      String countString;
      // String message;

      myApp = new HelloApp();
      myApp.run();

      // message = "#### TracesName = " + myApp.getMyTracesName();
      // System.out.println(message);

      theFile = new File(myApp.getMyTracesName());
      countString = Integer.toString(myApp.getMyPrintCount());
      assertEquals(true, theFile.exists());
      assertEquals(true, countString.equals(myApp.getMyTraces().getData(0)));

      fileContent = new MyStorage(myApp.getMyTracesName());
      fileContent.readData();
      assertEquals(true, fileContent.isEqual(myApp.getMyTraces()));
   }

   @Test
   public void testHelloAppRun2() throws Exception
   {
      HelloApp myApp = null;
      File theFile = null;
      MyStorage fileContent = null;
      int myCount = 8;
      String countString = Integer.toString(myCount);
      String MyTracesName = "foo3.traces";
      // String message;

      myApp = new HelloApp(myCount, MyTracesName);
      myApp.run();

      // message = "#### TracesName = " + myApp.getMyTracesName();
      // System.out.println(message);

      theFile = new File(myApp.getMyTracesName());
      assertEquals(true, theFile.exists());
      assertEquals(true, countString.equals(myApp.getMyTraces().getData(0)));

      fileContent = new MyStorage(myApp.getMyTracesName());
      fileContent.readData();
      assertEquals(true, fileContent.isEqual(myApp.getMyTraces()));
      assertEquals(true, countString.equals(fileContent.getData(0)));
   }

   @Test
   public void testHelloAppRun3() throws Exception
   {
      HelloApp myApp = null;
      File theFile = null;
      MyStorage fileContent = null;
      int myCount = 22;
      String countString = Integer.toString(myCount);
      String helloString = "---------------------- Hello World !";
      String MyTracesName = "hello_run3.traces";
      // String message;

      myApp = new HelloApp(myCount, MyTracesName);
      myApp.run();

      // message = "#### TracesName = " + myApp.getMyTracesName();
      // System.out.println(message);

      fileContent = new MyStorage(myApp.getMyTracesName());
      fileContent.readData();
      assertEquals(true, fileContent.isEqual(myApp.getMyTraces()));
      assertEquals(true, countString.equals(fileContent.getData(0)));
      assertEquals(true, helloString.equals(fileContent.getData(2)));
   }

   // **********************************************************************
   // private
   // **********************************************************************
   // ******** attributes
}
