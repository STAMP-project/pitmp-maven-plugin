package eu.stamp_project.examples.dnoo.dnooLogs;

// **********************************************************************
import java.util.logging.*;
import java.io.IOException;
import java.io.File;

// **********************************************************************
public class MyUselessClass
{
   // **********************************************************************
   // public
   // **********************************************************************
   // ******** attributes
   public String getAttribute1()
   {
      return(_Attribute1);
   }

   // ********
   public void setAttribute1(String value)
   {
      _Attribute1 = value;
   }

   // **********************************************************************
   public boolean getAttribute2()
   {
      return(_Attribute2);
   }

   // ********
   public void setAttribute2(boolean value)
   {
      _Attribute2 = value;
   }

   // **********************************************************************
   // ******** methods
   public MyUselessClass()
   {
      setAttribute1("initial value");
      setAttribute2(true);
   }

   // **********************************************************************
   public void setAll()
   {
      setAttribute1("another value");
      setAttribute2(false);
   }

   // **********************************************************************
   public void clearAll()
   {
      setAttribute1("initial value");
      setAttribute2(true);
   }

   // **********************************************************************
   // protected
   // **********************************************************************
   // ******** attributes
   protected String _Attribute1;
   protected boolean _Attribute2;
}
