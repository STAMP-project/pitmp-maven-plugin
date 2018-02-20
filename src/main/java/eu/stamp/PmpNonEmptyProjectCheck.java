package eu.stamp.plugins;

// **********************************************************************
import java.io.File;

import org.apache.maven.project.MavenProject;

import org.pitest.functional.predicate.Predicate;

// **********************************************************************
public class PmpNonEmptyProjectCheck implements Predicate<MavenProject>
{

   // **********************************************************************
   @SuppressWarnings("unchecked")
   @Override
   public Boolean apply(MavenProject project)
   {
      PmpProject theProject = PmpContext.getInstance().getCurrentProject();
      // assert(theProject != null)

      Boolean hasTests = theProject.hasTestCompileSourceRoots();
      Boolean hasClasses = theProject.hasCompileSourceRoots();

      return(hasTests && hasClasses);
   }
}
