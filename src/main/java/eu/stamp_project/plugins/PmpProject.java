package eu.stamp_project.plugins;

import java.io.File;
import java.util.*;
// **********************************************************************

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.pitest.maven.MojoToReportOptionsConverter;
import org.pitest.maven.PmpMojo;
import org.pitest.maven.SurefireConfigConverter;
import org.pitest.mutationtest.config.ReportOptions;
import org.pitest.mutationtest.tooling.AnalysisResult;
import org.pitest.mutationtest.tooling.CombinedStatistics;
import org.pitest.mutationtest.tooling.EntryPoint;

import eu.stamp_project.PmpContext;

// **********************************************************************
public class PmpProject
{

    private final static String ISSUES = "ISSUES";

    private final static String METHODS = "METHODS";

    // **********************************************************************
    // public
    // **********************************************************************
    // ******** attributes
    public String getName()
    {
        return(getTheMojo().getProject().getArtifactId());
    }

    public String getPmpMutationEngine()
    {
        return(_PmpMutationEngine);
    }
    public void setPmpMutationEngine(String value)
    {
        _PmpMutationEngine = value;
    }

    public Boolean getRunningDescartes()
    {
        return(_RunningDescartes);
    }
    public void setRunningDescartes(Boolean value)
    {
        _RunningDescartes = value;
    }

    // **********************************************************************
    // ******** associations
    public PmpMojo getTheMojo()
    {
        return(_TheMojo);
    }

    // **********************************************************************
    public MavenProject getTheMavenProject()
    {
        return(getTheMojo().getProject());
    }

    // **********************************************************************
    public ReportOptions getPitOptions()
    {
        return(_PitOptions);
    }

    // **********
    public void setPitOptions(ReportOptions options)
    {
        _PitOptions = options;
    }

    // **********************************************************************
    public CombinedStatistics getResults()
    {
        return(_Results);
    }

    // **********************************************************************
    public ArrayList<String> getSourceDirs()
    // merge the source and test source dirs for all dependencies
    {
        ArrayList<String> completeList = new ArrayList<String>();
        Set<Artifact> dependList = getTheMavenProject().getArtifacts();
        Iterator<Artifact> myIt = dependList.iterator();
        Artifact currentDepend;
        MavenProject theProject;

        addSourceDirs(completeList, getTheMavenProject());

        // and add classes of every dependencies which are project modules
        while (myIt.hasNext())
        {
            currentDepend = myIt.next();
            theProject = PmpContext.getInstance().getMavenProjectFromName
                (currentDepend.getArtifactId());
            if (theProject != null)
            {
                addSourceDirs(completeList, theProject);
            }
        }

        return(completeList);
    }

    // **********************************************************************
    public ArrayList<String> getDependsCodePaths()
    {
        ArrayList<String> codePaths = new ArrayList<String>();
        Set<Artifact> dependList = getTheMavenProject().getArtifacts();
        Iterator<Artifact> myIt = dependList.iterator();
        Artifact currentDepend;
        MavenProject theProject;

        // and add paths of every dependencies which are project modules
        while (myIt.hasNext())
        {
            currentDepend = myIt.next();
            theProject = PmpContext.getInstance().getMavenProjectFromName
                (currentDepend.getArtifactId());
            if (theProject != null)
            {
                codePaths.add(theProject.getBuild().getOutputDirectory());
            }
        }

        return(codePaths);
    }

    // **********************************************************************
    public ArrayList<String> getDependsClassPathElements()
    {
        ArrayList<String> completeList = new ArrayList<String>();
        Set<Artifact> dependList = getTheMavenProject().getArtifacts();
        MavenProject dependProject;
        Iterator<Artifact> myIt = dependList.iterator();
        Artifact currentDepend;
        String pathName;

        // add paths of every dependencies which are project modules
        while (myIt.hasNext())
        {
            currentDepend = myIt.next();

            dependProject = PmpContext.getInstance().getMavenProjectFromName
                (currentDepend.getArtifactId());
            if (dependProject != null)
            {
                completeList.add(dependProject.getBuild().getOutputDirectory());
                completeList.add(dependProject.getBuild().getTestOutputDirectory());
            }

            if (! currentDepend.getType().equals("pom"))
            {
                pathName = currentDepend.getFile().getAbsolutePath();
                completeList.add(pathName);
            }
        }

        return(completeList);
    }

    // **********************************************************************
    // ******** methods
    public PmpProject(PmpMojo mojo)
    {
        _TheMojo = mojo;
        // initialize the mutationEngine with the one from PIT
        _PmpMutationEngine = mojo.getMutationEngine();
        _RunningDescartes = false;
    }

    // **********************************************************************
    public CombinedStatistics execute() throws MojoExecutionException
    {
        EntryPoint pitEntryPoint = null;
        AnalysisResult execResult = null;

        // create the final ReportOptions
        setPitOptions(new MojoToReportOptionsConverter(getTheMojo(),
              new SurefireConfigConverter(), getTheMojo().getFilter())
            .convert());

        // and complete it to update codePaths, sourceDirs and classPathElements
        modifyReportOptions();
        // printOptionsInfo(getPitOptions());

        pitEntryPoint = new EntryPoint();
        execResult = pitEntryPoint.execute(getTheMojo().getBaseDir(),
            getPitOptions(), getTheMojo().getPlugins(),
            getTheMojo().getEnvironmentVariables());
        if (execResult.getError().isPresent())
        {
            throw new MojoExecutionException("fail", execResult.getError().get());
        }
        _Results = execResult.getStatistics().get();

        // <cael>: to do: combine results of test suites

        return(getResults());
    }

    private boolean isDefaultNotConfiguredReportOutput() {
        Collection<String> outputFormats = getPitOptions().getOutputFormats();
        return outputFormats.isEmpty() || (outputFormats.size() == 1 &&  outputFormats.contains("HTML"));
    }

    // **********************************************************************
    public void modifyReportOptions()
    {
        // require(getPitOptions() != null)
        ArrayList<File> fileList = null;
        ArrayList<String> sourceDirList = null;
        ArrayList<String> codePaths = null;
        ArrayList<String> dependsCodePaths = null;
        ArrayList<String> classPathElts = null;
        ArrayList<String> dependsClassPathElts = null;
        ArrayList<String> features = null;

        // Descartes behavior
        if (getRunningDescartes())
        {
            // If runnning descartes and no output format is configured, then set ISSUES and METHODS as default
            setPmpMutationEngine("descartes");
            if (isDefaultNotConfiguredReportOutput()) {
                getPitOptions().addOutputFormats(Arrays.asList(ISSUES, METHODS));
            }

        }
        else {
            // If not running descartes don't use STOP_METHODS unless manually configured
            features = new ArrayList<String>(getPitOptions().getFeatures());
            Optional<String> result = features.stream().filter(str -> str.startsWith("+STOP_METHODS")).findAny();
            if(!result.isPresent()) {
                features.add("-STOP_METHODS()");
                getPitOptions().setFeatures(features);
            }
        }

        // merge test and class source directories
        // <cael>: to do: check if the order impacts the execution
        sourceDirList = getSourceDirs();
        if (sourceDirList != null && ! sourceDirList.isEmpty())
        {
            fileList = PmpContext.stringsToFiles(sourceDirList);
            getPitOptions().setSourceDirs(fileList);
        }

        dependsCodePaths = getDependsCodePaths();
        if (getPitOptions().getCodePaths() != null &&
             ! getPitOptions().getCodePaths().isEmpty())
        {
            codePaths = new ArrayList<String>(getPitOptions().getCodePaths());
            if (dependsCodePaths != null && ! dependsCodePaths.isEmpty())
            {
                PmpContext.addNewStrings(codePaths, dependsCodePaths);
            }
        }
        else
        {
            codePaths = dependsCodePaths;
        }
        if (codePaths != null && ! codePaths.isEmpty())
        {
            getPitOptions().setCodePaths(codePaths);
        }

        dependsClassPathElts = getDependsClassPathElements();
        if (getPitOptions().getClassPathElements() != null &&
             ! getPitOptions().getClassPathElements().isEmpty())
        {
            classPathElts = new ArrayList<String>(getPitOptions().getClassPathElements());
            if (dependsClassPathElts != null && ! dependsClassPathElts.isEmpty())
            {
                PmpContext.addNewStrings(classPathElts, dependsClassPathElts);
            }
        }
        else
        {
            classPathElts = dependsClassPathElts;
        }
        if (classPathElts != null && ! classPathElts.isEmpty())
        {
            getPitOptions().setClassPathElements(classPathElts);
        }
        getPitOptions().setClassPathElements(classPathElts);

        // set the mutation engine
        getPitOptions().setMutationEngine(getPmpMutationEngine());
    }

    // **********************************************************************
    public Boolean hasCompileSourceRoots()
    // the module or one of the dependencies has src/main/java
    {
        Boolean result = PmpContext.oneFileExists
            (getTheMojo().getProject().getCompileSourceRoots());
        ArrayList<MavenProject> dependList = PmpContext.getInstance()
            .getDependingModules(getTheMojo().getProject());
        Iterator<MavenProject> myIt = dependList.iterator();
        MavenProject currentModule;

        // check for dependencies
        while (myIt.hasNext() && ! result)
        {
            currentModule = myIt.next();
            result = PmpContext.oneFileExists(currentModule.getCompileSourceRoots());
        }

        return(result);
    }

    // **********************************************************************
    public Boolean hasTestCompileSourceRoots()
    // the module has src/test/java
    {
        Boolean result = PmpContext.oneFileExists
            (getTheMojo().getProject().getTestCompileSourceRoots());

        return(result);
    }

    // **********************************************************************
    public void addSourceDirs(List<String> listToComplete,
        MavenProject theProject)
    {
        // require(getPitOptions() != null)
        List<String> nameList;

        nameList = theProject.getCompileSourceRoots();
        if (nameList != null && ! nameList.isEmpty())
        {
            listToComplete.addAll(nameList);
        }
        nameList = theProject.getTestCompileSourceRoots();
        if (nameList != null && ! nameList.isEmpty())
        {
            listToComplete.addAll(nameList);
        }
    }

    // **********************************************************************
    public void printInfo()
    {
        System.out.println("#### project: " + getName());
        System.out.println("#");
        if (getTheMojo().getProject().getParent() != null)
        {
            System.out.println("# Mvn Parent: " + getTheMojo().getProject().getParent()
                .getArtifactId());
        }
        System.out.println("# Mvn all dependencies: " +
            getTheMojo().getProject().getArtifacts());
        System.out.println("");
        System.out.println("####");
    }

    // **********************************************************************
    public void printMojoInfo()
    {
        System.out.println("######## mojo attributes");
        System.out.println("#");
        System.out.println("# targetTests: " + getTheMojo().getTargetTests());
        System.out.println("# mutationEngine: " + getTheMojo().getMutationEngine());
        System.out.println("# targetClasses: " + getTheMojo().getTargetClasses());
        System.out.println("# modified targetClasses: " + getTheMojo()
            .getTargetClasses());
        System.out.println("#");
        System.out.println("########");
    }

    // **********************************************************************
    public void printOptionsInfo(ReportOptions data)
    {
        System.out.println("####");
        System.out.println("#### BaseDir: " + getTheMojo().getBaseDir());
        System.out.println("#### mutationEngine: " + data.getMutationEngine());
        System.out.println("#### outputFormats: " + data.getOutputFormats());
        System.out.println("#### features: " + data.getFeatures());
        System.out.println("#### targetTests: " + data.getTargetTests());
        System.out.println("#### excludedClasses: " + data.getExcludedClasses());
        System.out.println("#### excludedMethods: " + data.getExcludedMethods());
        System.out.println("#### targetClasses: " + data.getTargetClasses());
        System.out.println("#### codePaths: " + data.getCodePaths());
        System.out.println("#### sourceDirs: " + data.getSourceDirs());
        System.out.println("#### classPathElements: " + data.getClassPathElements());
        System.out.println("####");
    }

    // **********************************************************************

    // **********************************************************************
    // protected
    // **********************************************************************
    // ******** associations
    protected PmpMojo _TheMojo = null;
    protected ReportOptions _PitOptions = null;
    protected CombinedStatistics _Results = null;
    protected String _PmpMutationEngine = null;
    protected Boolean _RunningDescartes = false;
}
