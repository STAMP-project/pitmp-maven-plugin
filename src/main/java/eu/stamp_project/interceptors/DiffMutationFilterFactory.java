package eu.stamp_project.interceptors;

import org.pitest.mutationtest.build.InterceptorParameters;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.build.MutationInterceptorFactory;
import org.pitest.plugin.Feature;
import org.pitest.plugin.FeatureParameter;

import java.util.Optional;

public class DiffMutationFilterFactory implements MutationInterceptorFactory {

    private String defaultPath = "target/descartes_changes.txt";
    private FeatureParameter fileParameter = FeatureParameter.named("file");


    @Override
    public MutationInterceptor createInterceptor(InterceptorParameters params) {
        Optional<String> path = params.getString(fileParameter);
        return new DiffMutationFilter(DiffInfo.fromFile(path.orElse(defaultPath)));
    }

    @Override
    public Feature provides() {
        return Feature.named("DIFF_FILTER")
                .withDescription(description())
                .withParameter(FeatureParameter.named("file"));
    }

    @Override
    public String description() {
        return "Filters the mutations by their location in the source code. The wanted locations are passed via an external file";
    }
}
