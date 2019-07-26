package eu.stamp_project.examples.dnoo.dnooStorage;

import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;
import java.io.File;
import java.util.*;

import eu.stamp_project.examples.dnoo.dnooStorage.MyStorage;

public class MyStorageTest
{
   // **********************************************************************
   // public
   // **********************************************************************
   @BeforeClass
   public static void runOnceAtTheBeginning()
   {
       System.out.println("dnoo.MyStorageTest: @BeforeClass - runOnceAtTheBeginning");
   }

   @AfterClass
   public static void runOnceAtTheEnd()
   {
       System.out.println("dnoo.MyStorageTest: @AfterClass - runOnceAtTheEnd");
   }

   @Test
   public void testMyStorageDefault() throws Exception
   {
      MyStorage myFile = null;
      String myFileName = "my_storage.txt";
      File theFile = null;

      MyStorage.deleteFile(myFileName);

      myFile = new MyStorage();
      assertEquals(myFileName, myFile.getFileName());
      assertEquals(0, myFile.getDataSize());

      theFile = new File(myFileName);
      theFile = new File(myFileName);
      assertEquals(false, theFile.exists());
   }

   @Test
   public void testMyStorageString() throws Exception
   {
      MyStorage myFile = null;
      String myFileName = "foofoo.txt";
      File theFile = null;

      MyStorage.deleteFile(myFileName);

      myFile = new MyStorage(myFileName);
      assertEquals(myFileName, myFile.getFileName());
      assertEquals(0, myFile.getDataSize());

      theFile = new File(myFileName);
      assertEquals(false, theFile.exists());
   }

   @Test
   public void testAddData() throws Exception
   {
      MyStorage myFile = null;
      String myFileName = "foo.txt";
      ArrayList<String> myData = new ArrayList<String>();

      // file content
      // 1st line
      // a second line, longer than the first one
      // 3rd line with something else: 2, 4, 8, 16
      myData.add("1st line");
      myData.add("a second line, longer than the first one");
      myData.add("3rd line with something else: 2, 4, 8, 16");

      myFile = new MyStorage(myFileName);
      for (int i = 0; i < myData.size(); i++)
      {
         myFile.addData(myData.get(i));
      }

      assertEquals(myData.size(), myFile.getDataSize());
      assertEquals(true, myFile.dataAreEqual(myData));
   }

   @Test
   public void testSaveReadData() throws Exception
   {
      MyStorage myFile = null;
      MyStorage mySecondFile = null;
      String myFileName = "bar.txt";
      File theFile = null;
      ArrayList<String> myData = new ArrayList<String>();

      MyStorage.deleteFile(myFileName);

      // file content
      // 1st line
      // a second line, longer than the first one
      // 3rd line with something else: 2, 4, 8, 16
      myData.add("1st line");
      myData.add("a second line, longer than the first one");
      myData.add("3rd line with something else: 2, 4, 8, 16");

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
