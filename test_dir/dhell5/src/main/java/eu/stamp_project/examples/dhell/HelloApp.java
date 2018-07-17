package eu.stamp_project.examples.dhell;

// **********************************************************************
import eu.stamp_project.examples.dhell.MyStorage;
import eu.stamp_project.examples.dhell.MyLogger;

// **********************************************************************
public class HelloApp
{
   // **********************************************************************
   // public
   // **********************************************************************
   // ******** methods

   // **********************************************************************
   public HelloApp()
   {
      String methodName = "HelloApp";
      MyLogger.Instance.entering(getClass().getName(), methodName);

      MyPrintCount = 1;
      MyTraces = null;
      MyTracesName = "myHelloApp.traces";
      ShouldPrintOnStdout = true;
      MyStorage.deleteFile(MyTracesName);

      MyLogger.Instance.info("MyPrintCount = " + Integer.toString(MyPrintCount)
         + " - MyTracesName = " + MyTracesName);

      MyLogger.Instance.exiting(getClass().getName(), methodName);
   }

   // **********************************************************************
   public HelloApp(int printCount)
   {
      String methodName = "HelloApp_int";
      MyLogger.Instance.entering(getClass().getName(), methodName);

      MyPrintCount = printCount;
      MyTraces = null;
      MyTracesName = "myHelloApp.traces";
      ShouldPrintOnStdout = true;
      MyStorage.deleteFile(MyTracesName);

      MyLogger.Instance.info("MyPrintCount = " + Integer.toString(MyPrintCount)
         + " - MyTracesName = " + MyTracesName);

      MyLogger.Instance.exiting(getClass().getName(), methodName);
   }

   // **********************************************************************
   public HelloApp(String tracesName)
   {
      String methodName = "HelloApp_String";
      MyLogger.Instance.entering(getClass().getName(), methodName);

      MyPrintCount = 1;
      MyTraces = null;
      MyTracesName = tracesName;
      ShouldPrintOnStdout = true;
      MyStorage.deleteFile(MyTracesName);

      MyLogger.Instance.info("MyPrintCount = " + Integer.toString(MyPrintCount)
         + " - MyTracesName = " + MyTracesName);

      MyLogger.Instance.exiting(getClass().getName(), methodName);
   }

   // **********************************************************************
   public HelloApp(int printCount, String tracesName)
   {
      String methodName = "HelloApp_int_String";
      MyLogger.Instance.entering(getClass().getName(), methodName);

      MyPrintCount = printCount;
      MyTraces = null;
      MyTracesName = tracesName;
      ShouldPrintOnStdout = true;
      MyStorage.deleteFile(MyTracesName);

      MyLogger.Instance.info("MyPrintCount = " + Integer.toString(MyPrintCount)
         + " - MyTracesName = " + MyTracesName);

      MyLogger.Instance.exiting(getClass().getName(), methodName);
   }

   // **********************************************************************
   public void run()
   {
      String methodName = "run";
      MyLogger.Instance.entering(getClass().getName(), methodName);

      String indent = "-";
      String countString = Integer.toString(MyPrintCount);

      if (MyTracesName.length() > 0)
      {
         MyTraces = new MyStorage(MyTracesName);
      }
      else
      {
         MyTraces = new MyStorage();
      }

      MyTraces.addData(countString);

      for (int i = 1; i < MyPrintCount; i++)
      {
         indent = indent + "-";
      }
      MyLogger.Instance.fine("indent = '" + indent + "'");
      myPrint(indent);
      myPrint(indent + " Hello World !");
      myPrint(indent);

      MyTraces.saveData();

      MyLogger.Instance.exiting(getClass().getName(), methodName);
   }

   // **********************************************************************
   public void computeMyUselessResult()
   {
      // PI = 3,141 592 653 589 793 - PI (10 chiffres exactes) ~ 104348 / 33215
      // PI = 3,1415926535 8979323846 2643383279 5028841971 6939937510 5820974944
      //      5923078164 0628620899 8628034825 3421170679
      // PHI = (1 + rootsquare(5)) / 2 = 1,618 033 988 7
      // PHI = 1,618 033 988 749 894 848 204 586 834 365 638 117 720 309 179 805
      //      762 862 135 448 622 705 260 462 189 024 497 072 072 041
   }

   // **********************************************************************
   // ******** attributes

   // **********************************************************************
   public int getMyPrintCount()
   {
      return(MyPrintCount);
   }

   // **********************************************************************
   public String getMyTracesName()
   {
      return(MyTracesName);
   }

   // **********************************************************************
   public boolean getShouldPrintOnStdout()
   {
      return(ShouldPrintOnStdout);
   }

   // **********
   public void setShouldPrintOnStdout(boolean value)
   {
      ShouldPrintOnStdout = value;
   }

   // **********************************************************************
   // ******** associations

   // **********************************************************************
   public int cardMyTraces()
   {
      int theCard = 0;

      if (MyTraces != null)
      {
         theCard = 1;
      }

      return(theCard);
   }

   // **********
   public MyStorage getMyTraces()
   {
      return(MyTraces);
   }

   // **********************************************************************
   // protected
   // **********************************************************************
   // ******** methods

   // **********************************************************************
   protected void myPrint(String message)
   {
      if (getShouldPrintOnStdout())
      {
         System.out.println(message);
      }
      MyTraces.addData(message);
   }

   // **********************************************************************
   // private
   // **********************************************************************
   // ******** attributes
   private int MyPrintCount;
   private String MyTracesName;
   private MyStorage MyTraces;
   private boolean ShouldPrintOnStdout;
}
