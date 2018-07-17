package eu.stamp_project.examples.dnoo.dnooStorage;

// **********************************************************************
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.util.*;

import eu.stamp_project.examples.dnoo.dnooLogs.MyLogger;

// **********************************************************************
public class MyStorage
{
   // **********************************************************************
   // public
   // **********************************************************************
   // ******** methods

   // **********************************************************************
   public static void deleteFile(String fileName)
   {
      String methodName = "deleteFile";
      MyLogger.getLogger().entering("MyStorage", methodName);

      File theFile = null;

      // delete file if it already exists
      theFile = new File(fileName);
      if (theFile.exists())
      {
         theFile.delete();
      }

      MyLogger.getLogger().exiting("MyStorage", methodName);
   }

   // **********************************************************************
   public MyStorage()
   {
      String methodName = "MyStorage";
      MyLogger.getLogger().entering(getClass().getName(), methodName);

      FileName = "my_storage.txt";
      MyData = new ArrayList<String>();

      MyLogger.getLogger().exiting(getClass().getName(), methodName);
   }

   // **********************************************************************
   public MyStorage(String fileName)
   {
      String methodName = "MyStorage_String";
      MyLogger.getLogger().entering(getClass().getName(), methodName);

      this.FileName = fileName;
      MyData = new ArrayList<String>();

      MyLogger.getLogger().exiting(getClass().getName(), methodName);
   }

   // **********************************************************************
   public void readData()
   {
      String methodName = "readData";
      MyLogger.getLogger().entering(getClass().getName(), methodName);

      BufferedReader myBuffer = null;
      FileReader myFile = null;
      String currentLine;

      try
      {
         myFile = new FileReader(FileName);
         myBuffer = new BufferedReader(myFile);
         while ((currentLine = myBuffer.readLine()) != null)
         {
            addData(currentLine);
         }
      }
      catch(IOException e)
      {
         System.out.println("Error: cannot read " + FileName);
      }

      try
      {
         if (myBuffer != null)
         {
            myBuffer.close();
         }
         if (myFile != null)
         {
            myFile.close();
         }
      }
      catch(IOException e)
      {
         System.out.println("Error: closing " + FileName);
      }

      MyLogger.getLogger().exiting(getClass().getName(), methodName);
   }

   // **********************************************************************
   public void saveData()
   {
      String methodName = "saveData";
      MyLogger.getLogger().entering(getClass().getName(), methodName);

      try
      {
         PrintStream writer = new PrintStream(new File(FileName));
         for (int i = 0; i < getDataSize(); i++)
         {
            writer.println(getData(i));
         }
         writer.close();
      }
      catch(IOException e)
      {
         System.out.println("Error: cannot write into " + FileName);
      }

      MyLogger.getLogger().exiting(getClass().getName(), methodName);
   }

   // **********************************************************************
   public boolean isEqual(MyStorage anotherStorage)
   {
      String methodName = "isEqual";
      MyLogger.getLogger().entering(getClass().getName(), methodName);

      boolean areEqual = (getDataSize() == anotherStorage.getDataSize());

      for (int i = 0; i < getDataSize() && areEqual; i++)
      {
         if (! (getData(i).equals(anotherStorage.getData(i))))
         {
            areEqual = false;
         } 
      }

      MyLogger.getLogger().exiting(getClass().getName(), methodName);
      return(areEqual);
   }

   // **********************************************************************
   public boolean dataAreEqual(ArrayList<String> otherData)
   {
      String methodName = "dataAreEqual";
      MyLogger.getLogger().entering(getClass().getName(), methodName);

      boolean areEqual = (getDataSize() == otherData.size());

      for (int i = 0; i < getDataSize() && areEqual; i++)
      {
         if (! (getData(i).equals(otherData.get(i))))
         {
            areEqual = false;
         } 
      }

      MyLogger.getLogger().exiting(getClass().getName(), methodName);
      return(areEqual);
   }

   // **********************************************************************
   // ******** attributes

   // **********************************************************************
   public String getFileName()
   {
      return(FileName);
   }

   // **********************************************************************
   public int getDataSize()
   {
      return(MyData.size());
   }

   // **********************************************************************
   public String getData(int index)
   {
      return(MyData.get(index));
   }

   // **********************************************************************
   public void addData(String aData)
   {
      MyLogger.getLogger().finest("IN : DataSize = " + Integer.toString(getDataSize())
         + " - aData = " + aData);

      MyData.add(aData);

      MyLogger.getLogger().finest("OUT: DataSize = " + Integer.toString(getDataSize()));
   }

   // **********************************************************************
   public void delData(String aData)
   {
      MyLogger.getLogger().finest("IN : DataSize = " + Integer.toString(getDataSize()));

      boolean found = false;

      for (int i = 0; i < getDataSize() && ! found; i++)
      {
         if (getData(i).equals(aData))
         {
            MyData.remove(i);
            found = true;
         } 
      } 

      MyLogger.getLogger().finest("OUT: DataSize = " + Integer.toString(getDataSize())
         + " - found = " + Boolean.toString(found));
   }

   // **********************************************************************
   // private
   // **********************************************************************
   // ******** attributes
   private String FileName;
   private ArrayList<String> MyData;
}
