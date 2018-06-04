package eu.stamp_project.plugins;

// **********************************************************************
import java.io.File;

import org.apache.maven.project.MavenProject;
import java.util.function.Predicate;

// **********************************************************************
public class PmpNonEmptyProjectCheck implements Predicate<MavenProject>
{

    // **********************************************************************
    @SuppressWarnings("unchecked")
    @Override
    public boolean test(MavenProject project)
    {
        PmpProject theProject = PmpContext.getInstance().getCurrentProject();
        // assert(theProject != null)

        boolean hasTests = theProject.hasTestCompileSourceRoots().booleanValue();
        boolean hasClasses = theProject.hasCompileSourceRoots().booleanValue();

        return(hasTests || hasClasses);
    }
}
