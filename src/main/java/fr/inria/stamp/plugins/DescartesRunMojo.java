package fr.inria.stamp.plugins;

// **********************************************************************
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

// **********************************************************************
@Mojo(name = "run")
public class DescartesRunMojo extends AbstractMojo
{
   // **********************************************************************
   // public
   // **********************************************************************
   // ******** methods

   // **********************************************************************
   // org.apache.maven.plugin.MojoExecutionException
   //    if an unexpected problem occurs: ==> "BUILD ERROR"
   // org.apache.maven.plugin.MojoFailureException
   //    if an expected problem: ==> "BUILD FAILURE"
   public void execute() throws MojoExecutionException
   {
      getLog().info("Hello, I'll run Descartes ASAP. :-)");
   }
}
