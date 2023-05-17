package org.pitest.maven;

import eu.stamp_project.PmpContext;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

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
        PmpContext.getInstance().getCurrentProject().setRunningDescartes(true);
        super.updateTargetClasses();
    }
}
