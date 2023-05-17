package eu.stamp_project;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.pitest.classinfo.ClassName;
import org.pitest.classpath.DirectoryClassPathRoot;
import org.pitest.maven.PmpMojo;

import eu.stamp_project.plugins.PmpProject;

// **********************************************************************
public class PmpContext
{
    // **********************************************************************
    // public
    // **********************************************************************
    // ******** associations
    public static PmpContext getInstance()
    {
        if (_Instance == null)
        {
            _Instance = new PmpContext();
        }
        return(_Instance);
    }

    // **********************************************************************
    public List<MavenProject> getModules()
    {
        return(_Modules);
    }

    // **********
    protected void setModules(List<MavenProject> value)
    {
        _Modules = value;
    }

    // **********************************************************************
    public PmpProject getCurrentProject()
    {
        return(_CurrentProject);
    }

    // **********
    public void setCurrentProject(PmpProject value)
    {
        _CurrentProject = value;
    }

    // **********************************************************************
    // ******** methods
    public static void addNewStrings(List<String> targetList,
        List<String> listToAdd)
    {
        Iterator<String> myIt = listToAdd.iterator();
        String currentElt;

        while (myIt.hasNext())
        {  
            currentElt = myIt.next();
            if (! targetList.contains(currentElt))
            {
                targetList.add(currentElt);
            }
        }
    }

    // **********************************************************************
    public static ArrayList<Path> stringsToFiles(ArrayList<String> nameList)
    {
        ArrayList<Path> fileList = new ArrayList<Path>();
        File newFile;

        for (int i = 0; i < nameList.size(); i++)
        {
            newFile = new File(nameList.get(i));
            if (newFile.exists())
            {
                fileList.add(newFile.toPath());
            }
        }

        return(fileList);
    }

    // **********************************************************************
    public static Boolean oneFileExists(List<String> fileNameList)
    {
        Iterator<String> myIt = fileNameList.iterator();
        String currentName;
        Boolean result = false;
        File theFile;

        while (myIt.hasNext() && ! result)
        {  
            currentName = myIt.next();
            theFile = new File(currentName);
            result = theFile.exists();
        }

        return(result);
    }

    // **********************************************************************
    public static ArrayList<String> getClasses(MavenProject theProject)
    {
        return getClassesInternal(theProject.getBuild().getOutputDirectory());
    }

    public static ArrayList<String> getTestClasses(MavenProject theProject)
    {
        return getClassesInternal(theProject.getBuild().getTestOutputDirectory());
    }

    private static ArrayList<String> getClassesInternal(String buildOutputDirectory)
    {
        ArrayList<String> classList = new ArrayList<String>();
        ArrayList<String> classFilterList = new ArrayList<String>();
        String aFilter;
        File outputDir = new File(buildOutputDirectory);

        if (outputDir.exists())
        {
            DirectoryClassPathRoot classRoot = new DirectoryClassPathRoot(outputDir);
            classList.addAll(classRoot.classNames());
            Iterator<String> myIt = classList.iterator();
            while (myIt.hasNext())
            {
                aFilter = ClassName.fromString(myIt.next()).getPackage().asJavaName() + ".*";
                if (! classFilterList.contains(aFilter))
                {
                    classFilterList.add(aFilter);
                }
            }
        }
        // else
        // <cael>: check if this could happen, and what does it mean

        return(classFilterList);
    }

    // **********************************************************************
    public PmpContext()
    {
        _Modules = null;
        _CurrentProject = null;
    }

    // **********************************************************************
    public void updateData(PmpMojo mojo)
    {
        List<MavenProject> moduleList = getProjectModules(mojo);

        // build once at the beginning; the complete list of project modules
        if (getModules() == null)
        {
            setModules(moduleList);
        }
    }

    // **********************************************************************
    public List<MavenProject> getProjectModules(PmpMojo mojo)
    {
        // <cael>: getCollectedProjects() returns the modules of the project
        // <cael>  only when called from the root directory, i.e. the directory
        // <cael>  that contains the main pom.xml
        // <cael>  So PitMP can be called only from the root directory otherwise
        // <cael>  it will not work

        List<MavenProject> moduleList = mojo.getProject().getCollectedProjects();

        return(moduleList);
    }

    // **********************************************************************
    // returns the list of dependencies of aProject that are a module of the
    // project
    public ArrayList<MavenProject> getDependingModules(MavenProject aProject)
    {
        ArrayList<MavenProject> moduleList = new ArrayList<MavenProject>();
        Set<Artifact> allDepend = aProject.getArtifacts();
        Iterator<Artifact> myIt = allDepend.iterator();
        Artifact currentArtifact;
        MavenProject aModule;

        while (myIt.hasNext())
        {
            currentArtifact = myIt.next();
            aModule = getMavenProjectFromName(currentArtifact.getArtifactId());
            if (aModule != null)
            {
                moduleList.add(aModule);
            }
        }

        return(moduleList);
    }

    // **********************************************************************
    public Boolean isProjectModule(String name)
    {
        Iterator<MavenProject> myIt = getModules().iterator();
        MavenProject currentModule;
        Boolean result = false;

        while (myIt.hasNext() && ! result)
        {  
            currentModule = myIt.next();
            result = currentModule.getArtifactId().equals(name);
        }

        return(result);
    }

    // **********************************************************************
    public MavenProject getMavenProjectFromName(String name)
    {
        Iterator<MavenProject> myIt = getModules().iterator();
        MavenProject currentModule;
        MavenProject module = null;

        while (myIt.hasNext() && module == null)
        {  
            currentModule = myIt.next();
            if (currentModule.getArtifactId().equals(name))
            {
                module = currentModule;
            }
        }

        return(module);
    }

    // **********************************************************************
    public static void printCollectedProjects(MavenProject aProject)
    {
        List<MavenProject> collectedProjects = aProject.getCollectedProjects();
        Iterator<MavenProject> myIt;
        MavenProject currentModule;

        System.out.print("#### collectedProjects(" + aProject.getArtifactId()
            + "): ");
        if (collectedProjects != null)
        {
            myIt = collectedProjects.iterator();
            while (myIt.hasNext())
            {  
                currentModule = myIt.next();
                System.out.print(currentModule.getArtifactId() + ", ");
            }
        }
        System.out.println("");
    }

    // **********************************************************************
    public static void printArtifacts(MavenProject aProject)
    {
        Set<Artifact> dependProjects = aProject.getArtifacts();
        Artifact currentModule;

        System.out.print("#### artifacts(" + aProject.getArtifactId()
            + "): ");
        if (dependProjects != null)
        {
            Iterator<Artifact> myIt = dependProjects.iterator();
            while (myIt.hasNext())
            {  
                currentModule = myIt.next();
                System.out.print(currentModule.getArtifactId() + ", ");
            }
        }
        System.out.println("");
    }

    // **********************************************************************
    public static void printDependModules(MavenProject aProject,
        ArrayList<MavenProject> moduleList)
    {
        MavenProject currentModule;

        System.out.print("#### dependModules(" + aProject.getArtifactId()
            + "): ");
        if (moduleList != null)
        {
            Iterator<MavenProject> myIt = moduleList.iterator();
            while (myIt.hasNext())
            {  
                currentModule = myIt.next();
                System.out.print(currentModule.getArtifactId() + ", ");
            }
        }
        System.out.println("");
    }

    // **********************************************************************
    // protected
    // **********************************************************************
    // ******** attributes
    protected static PmpContext _Instance = null;

    protected List<MavenProject> _Modules;
    protected PmpProject _CurrentProject;
}
