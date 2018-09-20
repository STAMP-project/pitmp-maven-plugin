package org.pitest.maven;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.pitest.mutationtest.config.ReportOptions;

import eu.stamp_project.PmpContext;

// **********************************************************************
@Mojo(name = "descartes", defaultPhase = LifecyclePhase.VERIFY, requiresDependencyResolution = ResolutionScope.TEST, threadSafe = true)
public class PmpDescartesMojo extends PmpMojo {

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
    // ******** methods
    @Override public void updateTargetClasses()
    {
        super.updateTargetClasses();
        System.out.println("MUTATION ENGINE >>> " +
            PmpContext.getInstance().getCurrentProject().getPmpMutationEngine());
        PmpContext.getInstance().getCurrentProject().
            setPmpMutationEngine("descartes");
        ReportOptions reportOptions =
            PmpContext.getInstance().getCurrentProject().getPitOptions();
        Collection<String> outputFormats = null;

        if (reportOptions != null)
        {
            outputFormats = reportOptions.getOutputFormats();
        } else
        {
            reportOptions = new ReportOptions();
            outputFormats = new ArrayList<String>();
        }
        outputFormats.add("METHODS");
        reportOptions.addOutputFormats(outputFormats);
        PmpContext.getInstance().getCurrentProject().setPitOptions(reportOptions);
    }
}
