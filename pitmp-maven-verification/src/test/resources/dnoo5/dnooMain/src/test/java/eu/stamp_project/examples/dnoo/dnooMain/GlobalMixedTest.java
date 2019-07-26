package eu.stamp_project.examples.dnoo.dnooMain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.*;

import eu.stamp_project.examples.dnoo.dnooStorage.MyStorage;
import eu.stamp_project.examples.dnoo.dnooHello.HelloApp;

public class GlobalMixedTest
{
   // **********************************************************************
   // public
   // **********************************************************************
   @Test
   public void testGlobalMixed1HelloAppRun() throws Exception
   {
      HelloApp myApp = null;
      File theFile = null;
      MyStorage fileContent = null;
      int myCount = 40;
      String countString = Integer.toString(myCount);
      String helloString = "---------------------------------------- Hello World !";
      String MyTracesName = "global_mixed1.traces";
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

   @Test
   public void testGlobalMixed2SaveReadData() throws Exception
   {
      MyStorage myFile = null;
      MyStorage mySecondFile = null;
      String myFileName = "global_mixed2.txt";
      File theFile = null;
      ArrayList<String> myData = new ArrayList<String>();

      MyStorage.deleteFile(myFileName);

      // files content
      myData.add("a first line, longer than the first one");
      myData.add("2nd line with something else: 2, 4, 8, 16");
      myData.add("3rd line");
      myData.add("the end");

      // write data in the first file
      myFile = new MyStorage(myFileName);
      for (int i = 0; i < myData.size(); i++)
      {
         myFile.addData(myData.get(i));
      }
      assertEquals(myData.size(), myFile.getDataSize());
      assertEquals(true, myFile.dataAreEqual(myData));

      myFile.saveData();
      theFile = new File(myFileName);
      assertEquals(true, theFile.exists());

      // read data in the 2nd file
      mySecondFile = new MyStorage(myFileName);
      assertEquals(true, theFile.exists());

      mySecondFile.readData();
      assertEquals(myData.size(), mySecondFile.getDataSize());
      assertEquals(true, mySecondFile.dataAreEqual(myData));

      // compare content
      assertEquals(true, myFile.isEqual(mySecondFile));
   }
}
