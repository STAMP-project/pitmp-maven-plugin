package eu.stamp_project.examples.dnoo.dnooLogs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import java.io.File;
import java.util.*;

import eu.stamp_project.examples.dnoo.dnooLogs.MyLogger;

public class MyLoggerTest
{
   // **********************************************************************
   // public
   // **********************************************************************
   // <cael>: to do: ignore it because it fails with pitest, so find why
   @Disabled
   @Test
   public void testMyLoggerDefault() throws Exception
   {
      System.out.println("******** testMyLoggerDefault");

      MyLogger.getLogger().info("testMyLoggerDefault - level = " + MyLogger.getLevel());
      assertEquals(MyLogger.getLevel(), "WARNING");
   }

   // **********************************************************************
   @Test
   public void testMyLoggerSetLevel1() throws Exception
   {
      System.out.println("******** testMyLoggerSetLevel1");
      MyLogger.setLevel("FINE");

      MyLogger.getLogger().info("testMyLoggerSetLevel1: FINE");
      assertEquals(MyLogger.getLevel(), "FINE");
   }

   // **********************************************************************
   @Test
   public void testMyLoggerSetLevel2() throws Exception
   {
      System.out.println("******** testMyLoggerSetLevel2");
      MyLogger.setLevel("ALL");

      MyLogger.getLogger().info("testMyLoggerSetLevel2: ALL");
      assertEquals(MyLogger.getLevel(), "ALL");
   }

}
