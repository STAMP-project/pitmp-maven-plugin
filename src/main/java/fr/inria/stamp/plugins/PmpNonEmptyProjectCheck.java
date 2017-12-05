package fr.inria.stamp.plugins;

// **********************************************************************
import java.io.File;
import java.util.logging.Logger;

import org.apache.maven.project.MavenProject;

import org.pitest.functional.FCollection;
import org.pitest.functional.predicate.Predicate;
import org.pitest.util.Log;

// **********************************************************************
public class PmpNonEmptyProjectCheck implements Predicate<MavenProject>
{

   // **********************************************************************
   @SuppressWarnings("unchecked")
   @Override
   public Boolean apply(MavenProject project)
   {
      Logger pitLogger = Log.getLogger();
      Boolean hasTests = FCollection.contains(project.getTestCompileSourceRoots(),
            exists());
      PmpProject theProject = PmpContext.getInstance().getCurrentProject();
      // assert(theProject != null)
      Boolean hasClasses = theProject.hasCompileSourceRoots();
 
      System.out.print("#### PmpNonEmptyProjectCheck: project = " +
         project.getArtifactId() + " - packaging = " + project.getPackaging());
      System.out.println(" - hasTests = " + hasTests + " - hasClasses = " +
         hasClasses);

      if (! hasTests)
      {
         pitLogger.info("Project " + project.getArtifactId() +
            " has no test to execute. testSrcRoots = " +
            project.getTestCompileSourceRoots());
      }
      if (! hasClasses)
      {
         pitLogger.warning("Project " + project.getArtifactId() +
            " has no class to mutate in the project. \nDependencies: " +
            project.getDependencies());
      }

      return(hasTests && hasClasses);
   }

   // **********************************************************************
   private Predicate<String> exists()
   {
      return new Predicate<String>()
      {
         @Override
         public Boolean apply(String root)
         {
            return new File(root).exists();
         }
      };
   }
}
