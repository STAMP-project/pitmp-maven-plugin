package org.pitest.maven;

// **********************************************************************
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.io.File;
import java.util.logging.Logger;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.plugins.annotations.LifecyclePhase;

import java.util.Optional;
import java.util.function.Predicate;
import org.pitest.mutationtest.config.PluginServices;
import org.pitest.mutationtest.tooling.CombinedStatistics;
import org.pitest.maven.MojoToReportOptionsConverter;
import org.pitest.maven.SurefireConfigConverter;
import org.pitest.maven.AbstractPitMojo;
import org.pitest.maven.RunPitStrategy;
import org.pitest.maven.DependencyFilter;
import org.pitest.util.Log;

import eu.stamp_project.plugins.*;

// **********************************************************************
@Mojo(name = "descartes", defaultPhase = LifecyclePhase.VERIFY,
    requiresDependencyResolution = ResolutionScope.TEST, threadSafe
    = true)
public class PmpDescartesMojo extends PmpMojo
{
    // **********************************************************************
    // ******** methods
    @Override
    public void updateTargetClasses()
    {
       super.updateTargetClasses();
       PmpContext.getInstance().getCurrentProject().setPmpMutationEngine("descartes");
    }
}
