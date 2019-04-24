package org.pitest.maven;

import java.io.File;
import java.util.*;
import java.util.function.Predicate;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.pitest.mutationtest.config.PluginServices;
import org.pitest.mutationtest.tooling.CombinedStatistics;

import eu.stamp_project.PmpContext;
import eu.stamp_project.plugins.PmpNonEmptyProjectCheck;
import eu.stamp_project.plugins.PmpProject;
import eu.stamp_project.report.MethodThresholds;

// **********************************************************************
@Mojo(name = "run", defaultPhase = LifecyclePhase.VERIFY,
    requiresDependencyResolution = ResolutionScope.TEST, threadSafe
    = true)

public class PmpMojo extends AbstractPitMojo
{
    // **********************************************************************
    // properties
    // **********************************************************************
    @Parameter(property = "targetModules")
    protected ArrayList<String> targetModules;

    @Parameter(property = "skippedModules")
    protected ArrayList<String> skippedModules;

    @Parameter(property = "targetDependencies")
    protected ArrayList<String> targetDependencies;

    @Parameter(property = "ignoredDependencies")
    protected ArrayList<String> ignoredDependencies;

    // If specified, modules before this module will be skipped.
    @Parameter(property = "continueFromModule")
    protected String continueFromModule;

    private static Set<String> alreadyVisitedModules = new HashSet<>();

    // if true: do not execute PIT, only display information about shouldRun or not
    @Parameter(defaultValue = "false", property = "shouldDisplayOnly")
    protected boolean _ShouldDisplayOnly;

    /**
     * Pseudo Tested threshold at which to fail build
     */
    @Parameter(defaultValue = "0", property = "pseudoTestedThreshold")
    private int pseudoTestedThreshold;

    /**
     * Partially Tested threshold at which to fail build
     */
    @Parameter(defaultValue = "0", property = "partiallyTestedThreshold")
    private int partiallyTestedThreshold;

    // **********************************************************************
    // public
    // **********************************************************************
    // ******** attributes
    public File getBaseDir()
    {
        return(detectBaseDir());
    }

    // **********************************************************************
    public boolean shouldDisplayOnly()
    {
        return(_ShouldDisplayOnly);
    }

    // **********************************************************************
    public ArrayList<String> getTargetModules()
    {
        return(targetModules);
    }

    // **********
    public void setTargetModules(ArrayList<String> newClasses)
    {
        targetModules = newClasses;
    }

    // **********
    public boolean isInTargetModules(String name)
    {
        boolean result = false;

        for (int i = 0; i < getTargetModules().size() && ! result; i++)
        {
            result = getTargetModules().get(i).equals(name);
        }

        return(result);
    }

    // **********************************************************************
    public ArrayList<String> getSkippedModules()
    {
        return(skippedModules);
    }

    // **********
    public void setSkippedModules(ArrayList<String> newClasses)
    {
        skippedModules = newClasses;
    }

    // **********
    public boolean isInSkippedModules(MavenProject module)
    {
        return isInSkippedModules(module.getArtifactId());
    }

    public boolean isInSkippedModules(String name)
    {
        boolean result = false;

        for (int i = 0; i < getSkippedModules().size() && ! result; i++)
        {
            result = getSkippedModules().get(i).equals(name);
        }

        return(result);
    }

    // **********************************************************************
    public ArrayList<String> getTargetDependencies()
    {
        // cael: debug only
        // if (targetDependencies == null)
        // {
        //     System.out.println("######## !!!!!! targetDependencies == null");
        // }
        return(targetDependencies);
    }

    // **********
    public void setTargetDependencies(ArrayList<String> moduleList)
    {
        targetDependencies = moduleList;
    }

    // **********
    public boolean isInTargetDependencies(String name)
    {
        boolean result = false;

        for (int i = 0; i < getTargetDependencies().size() && ! result; i++)
        {
            result = getTargetDependencies().get(i).equals(name);
        }

        return(result);
    }

    // **********************************************************************
    public ArrayList<String> getIgnoredDependencies()
    {
        // cael: debug only
        // if (ignoredDependencies == null)
        // {
        //     System.out.println("######## !!!!!! ignoredDependencies == null");
        // }
        return(ignoredDependencies);
    }

    // **********
    public void setIgnoredDependencies(ArrayList<String> moduleList)
    {
        ignoredDependencies = moduleList;
    }

    // **********
    public boolean isInIgnoredDependencies(String name)
    {
        boolean result = false;

        for (int i = 0; i < getIgnoredDependencies().size() && ! result; i++)
        {
            result = getIgnoredDependencies().get(i).equals(name);
        }

        return(result);
    }

    // **********************************************************************
    public void setContinueFromModule(String continueFromModule)
    {
        this.continueFromModule = continueFromModule;
    }

    // **********
    public String getContinueFromModule()
    {
        return continueFromModule;
    }

    // **********
    public boolean isContinueFromModuleSatisfied(MavenProject module)
    {
        if (continueFromModule == null)
        {
            // property is not specified
            return true;
        }

        // note that the current module has already been added
        return alreadyVisitedModules.contains(continueFromModule);
    }

    // **********************************************************************
    // ******** associations
//    public Predicate<Artifact> getFilter()
//    {
//        return(filter);
//    }

    // **********************************************************************
//    public PluginServices getPlugins()
//    {
//        return(plugins);
//    }

    // **********************************************************************
    // ******** methods
    public PmpMojo()
    {
        super(new RunPitStrategy(),
          new DependencyFilter(new PluginServices(AbstractPitMojo.class.getClassLoader())),
          new PluginServices(AbstractPitMojo.class.getClassLoader()),
          new PmpNonEmptyProjectCheck());
    }

    // **********************************************************************
    // ******** methods
    public void updateTargetClasses()
    {
        // require(getProject() != null)

        ArrayList<String> classList;
        ArrayList<MavenProject> moduleList = null;
        MavenProject mvnProject;

        List<String> originaltargetClasses = getTargetClasses();
        ArrayList<String> targetClasses = new ArrayList<>();

        if(originaltargetClasses != null && !originaltargetClasses.isEmpty()) {
            targetClasses.addAll(originaltargetClasses);
        }
        else {
            // If no list of target classes is given, add all classes in the project
            targetClasses.addAll(PmpContext.getClasses(getProject()));
        }
        // complete the target classes with other (dependencies) modules classes
        // and add target classes of all getArtifacts which are a project module
        moduleList = PmpContext.getInstance().getDependingModules(getProject());
        for (int i = 0; i < moduleList.size(); i++)
        {
            MavenProject module = moduleList.get(i);

            if ((getTargetDependencies().size() == 0 ||
                 isInTargetDependencies(module.getArtifactId())) &&
                ! isInIgnoredDependencies(module.getArtifactId()))
            {
                classList = PmpContext.getClasses(module);
                if (! classList.isEmpty())
                {
                    PmpContext.addNewStrings(targetClasses, classList);
                }
            }
        }
        setTargetClasses(targetClasses);
    }

    public void updateTargetTests()
    {
        List<String> targetTests = getTargetTests();
        if (targetTests == null || targetTests.isEmpty())
        {
            setTargetTests(PmpContext.getTestClasses(getProject()));
        }
    }

    // **********************************************************************
    // protected
    // **********************************************************************
    // ******** methods
    @Override
    protected Optional<CombinedStatistics> analyse()
        throws MojoExecutionException
    {
        Optional<CombinedStatistics> result = null;

        result = Optional.ofNullable(PmpContext.getInstance().getCurrentProject()
            .execute());

        return(result);
    }

    // **********************************************************************
    // called at the beginning of AbstractPitMojo.execute
    @Override
    protected RunDecision shouldRun()
    {
        MethodThresholds.getInstance().setPartialyTestedThresold(partiallyTestedThreshold);
        MethodThresholds.getInstance().setPseudoTestedThresold(pseudoTestedThreshold);

        RunDecision theDecision;
        PmpProject myPmpProject;
        String projectName = getProject().getArtifactId();
        alreadyVisitedModules.add(projectName);
        // if no targetModules are specified, take all modules
        boolean isTargetModule = (getTargetModules() == null ||
            getTargetModules().isEmpty() || isInTargetModules(projectName));
        String message;

        PmpContext.getInstance().updateData(this);

        myPmpProject = new PmpProject(this);
        PmpContext.getInstance().setCurrentProject(myPmpProject);

        updateTargetClasses();
        updateTargetTests();

        if (getProject().getPackaging().equals("pom") &&
             myPmpProject.hasTestCompileSourceRoots() &&
             myPmpProject.hasCompileSourceRoots())
        // force packaging to not pom before calling super.shouldRun
        {
            getProject().setPackaging("jar");
        }
        theDecision = super.shouldRun();

        if (! isTargetModule)
        {
            message = projectName + " is not a target module";
            theDecision.addReason(message);
        }

        if (isInSkippedModules(projectName))
        {
            message = projectName + " is a skipped module";
            theDecision.addReason(message);
        }

        if (! isContinueFromModuleSatisfied(getProject()))
        {
            message = projectName + " is before " + continueFromModule +
                " from which the execution shall be continued";
            theDecision.addReason(message);
        }

        if (shouldDisplayOnly())
        {
            message = "Display only option is true";
            theDecision.addReason(message);
        }

        // PitMojo displays the reasons about why we skip the project

        return(theDecision);
    }

    @Override
    public PluginServices getPlugins() {
        return super.getPlugins();
    }

    @Override
    public Predicate<Artifact> getFilter() {
        return super.getFilter();
    }
}
