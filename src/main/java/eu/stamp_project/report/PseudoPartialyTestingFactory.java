package eu.stamp_project.report;

import org.pitest.mutationtest.ListenerArguments;
import org.pitest.mutationtest.MutationResultListener;
import org.pitest.mutationtest.MutationResultListenerFactory;

import java.util.Properties;

public class PseudoPartialyTestingFactory implements MutationResultListenerFactory {


    public MutationResultListener getListener(Properties props, ListenerArguments args) {
        return new PseudoPartialyTestingListener(args);
    }

    public String name() {
        return "CHECKTHRESHOLDS";
    }

    public String description() {
        return "Check if Pseudo/Partially Tested scores are  above thresholds parameters";
    }
}
