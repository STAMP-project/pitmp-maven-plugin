package eu.stamp_project.interceptors;

import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.mutationtest.build.InterceptorType;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationDetails;

import java.util.Collection;
import java.util.stream.Collectors;

public class DiffMutationFilter implements MutationInterceptor {

    private ClassTree classTree;
    private DiffInfo diff;
    public DiffMutationFilter(DiffInfo diff) {
        this.diff = diff;
    }

    @Override
    public InterceptorType type() {
        return InterceptorType.FILTER;
    }

    @Override
    public void begin(ClassTree clazz) {
        this.classTree = clazz;
    }

    @Override
    public Collection<MutationDetails> intercept(Collection<MutationDetails> mutations, Mutater m) {
        return mutations.stream()
                .filter(mutation ->
                        diff.contains(mutation.getClassName().getPackage().asInternalName() + "/" + mutation.getFilename()))
                .collect(Collectors.toList());
    }


    @Override
    public void end() {
        classTree = null;
    }
}
