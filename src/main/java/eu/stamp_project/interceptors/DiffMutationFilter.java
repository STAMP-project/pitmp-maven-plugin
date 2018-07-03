package eu.stamp_project.interceptors;

import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.mutationtest.build.InterceptorType;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.mutationtest.engine.MutationIdentifier;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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
        Collection<MutationDetails> result = new LinkedList<>();
        for(MutationDetails mutation : mutations) {
            int line = diff.getFirstLineIn(
                    mutation.getFilename(),
                    mutation.getLineNumber(),
                    mutation.getLineNumber() + getIndexCount(mutation.getId()));
            if(line < 0 ) continue;
                result.add(new MutationDetails(mutation.getId(), mutation.getFilename(), mutation.getDescription(), line, mutation.getBlock()));
        }
        return result;
    }

    private int getIndexCount(MutationIdentifier id) {
        try {
            Field indexesField = id.getClass().getField("indexes");
            indexesField.setAccessible(true);
            return ((List<Integer>)indexesField.get(id)).size();
        }
        catch (Throwable exc) {
            return 0;
        }
    }

    @Override
    public void end() {
        classTree = null;
    }
}
