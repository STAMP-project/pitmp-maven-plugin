package fr.inria.stamp.plugins;

// **********************************************************************
import java.io.File;

import org.apache.maven.project.MavenProject;
import org.pitest.functional.FCollection;
import org.pitest.functional.predicate.Predicate;

// **********************************************************************
public class PmpNonEmptyProjectCheck implements Predicate<MavenProject>
{

   // **********************************************************************
   @SuppressWarnings("unchecked")
   @Override
   public Boolean apply(MavenProject project)
   {
      Boolean hasTests =  FCollection.contains(project.getTestCompileSourceRoots(),
            exists());
      PmpProject theProject = PmpContext.getInstance().findInProjects
         (project.getArtifactId());
      // assert(theProject != null)
      Boolean hasClasses = theProject.hasCompileSourceRoots();
 
      // System.out.println("######## PmpNonEmptyProjectCheck: project = " +
      //    project.getArtifactId() + " - theProject = " + theProject);

      return (hasTests && hasClasses);
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
