package fr.inria.stamp.plugins;

// **********************************************************************
import java.util.*;

import java.io.File;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.plugins.annotations.LifecyclePhase;

import org.pitest.functional.Option;
import org.pitest.functional.predicate.Predicate;
import org.pitest.mutationtest.config.PluginServices;
import org.pitest.mutationtest.config.ReportOptions;
import org.pitest.mutationtest.tooling.CombinedStatistics;

import org.pitest.maven.AbstractPitMojo;
import org.pitest.maven.GoalStrategy;
import org.pitest.maven.DependencyFilter;
import org.pitest.maven.NonEmptyProjectCheck;
import org.pitest.maven.MojoToReportOptionsConverter;
import org.pitest.maven.SurefireConfigConverter;

// **********************************************************************
@Mojo(name = "run", defaultPhase = LifecyclePhase.VERIFY,
   requiresDependencyResolution = ResolutionScope.TEST)
public class DescartesRunMojo extends AbstractPitMojo
{
   // **********************************************************************
   // public
   // **********************************************************************
   // ******** attributes

   // **********************************************************************
   // ******** methods
   public DescartesRunMojo()
   {
      super(new RunDescartesStrategy(),
         new DependencyFilter(new PluginServices
            (AbstractPitMojo.class.getClassLoader())),
         new PluginServices(AbstractPitMojo.class.getClassLoader()),
         new NonEmptyProjectCheck());
   }

   // **********************************************************************
   // public DescartesRunMojo(final GoalStrategy strategy,
   //   final Predicate<Artifact> filter, final PluginServices plugins,
   //   final Predicate<MavenProject> nonEmptyProjectCheck)
   // {
   //    super(strategy, filter, plugins, nonEmptyProjectCheck);
   // }

   // **********************************************************************
   // protected
   // **********************************************************************
   // ******** methods
   @Override
   protected Option<CombinedStatistics> analyse()
      throws MojoExecutionException
   {
      Option<CombinedStatistics> result;

      final ReportOptions data = new MojoToReportOptionsConverter(this,
        new SurefireConfigConverter(), filter).convert();

      // ?? data.setFailWhenNoMutations(false);
      DescartesProject.initialize(getProject());

      result = Option.some(this.goalStrategy.execute(detectBaseDir(), data,
         this.plugins, this.getEnvironmentVariables()));

      return(result);
   }
}
