package eu.stamp_project.examples.dhell;

// **********************************************************************
import eu.stamp_project.examples.dhell.HelloApp;
import eu.stamp_project.examples.dhell.MyLogger;

// **********************************************************************
public class MyGod
{
    // **********************************************************************
    // public
    // **********************************************************************
    // ******** methods

    // **********************************************************************
    public static void main(String[] args)
    {
        int indentCount = 1;
        int argIndex = 0;
        String indentArg = "";
        String fileName = "";
        String logLevel = "WARNING";
        boolean printOnStdout = true;
        boolean shouldRun = true;

        while (argIndex < args.length)
        {
            if (args[argIndex].equals("-h"))
            {
                printHelp();
                shouldRun = false;
                argIndex = args.length;
            }
            else if (args[argIndex].equals("-no_stdout"))
            {
                printOnStdout = false;
                argIndex = argIndex + 1;
            }
            else if (args[argIndex].equals("-log"))
            {
                if (argIndex + 1 < args.length)
                {
                    logLevel = args[argIndex + 1];
                }
                else
                {
                    System.err.println("Missing argument: -log <level>, -h for help");
                    System.exit(1);
                }
                argIndex = argIndex + 2;
            }
            else if (indentArg.length() == 0)
            {
                indentArg = args[argIndex];
                argIndex = argIndex + 1;
            }
            else if (fileName.length() == 0)
            {
                fileName = args[argIndex];
                argIndex = argIndex + 1;
            }
            else
            {
                System.err.println("Unknown argument: " + args[argIndex] + ", -h for help");
                System.exit(1);
            }
        }

        if (shouldRun)
        {
            if (indentArg.length() > 0)
            {
                try
                {
                    indentCount = Integer.parseInt(indentArg);
                }
                catch (NumberFormatException e)
                {
                    System.err.println("Argument " + indentArg +
                        " must be an integer, -h for help");
                    System.exit(1);
                }
            }

            MyLogger.initLogs(logLevel);

            HelloApp myApp = new HelloApp(indentCount, fileName);
            myApp.setShouldPrintOnStdout(printOnStdout);
            myApp.run();
        }
    }

    // **********************************************************************
    public static void printHelp()
    {
        System.out.println("eu.stamp_project.examples.dhell.MyGod");
        System.out.println("Print on stdout a 'Hello World !' message formatted according to");
        System.out.println("<indentation_count>, and generate an output file");
        System.out.println("");
        System.out.println("args: [-h | <indentation_count> [<file_name>] [-no_stdout] [-log <level>]]");
        System.out.println("    -h: this help");
        System.out.println("    <indentation_count>: number of '-' for message indentation, default is 1");
        System.out.println("    <file_name>: output file name, default is my_storage.txt");
        System.out.println("        output file name contains <indentation_count> and the stdout");
        System.out.println("    -no_stdout: if specified, do not print anything on stdout,");
        System.out.println("        generate only the output file");
        System.out.println("    -log <level>: specify the level of information to log,");
        System.out.println("        <level> is one of {OFF, SEVERE, WARNING, INFO, CONFIG, FINE, FINER, FINEST, ALL}, default is WARNING");
        System.out.println("        generate a log file, dhell.log, in the current directory");
    }
}
