package eu.stamp_project.interceptors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.pitest.mutationtest.build.InterceptorParameters;
import org.pitest.mutationtest.build.InterceptorType;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.plugin.FeatureSetting;

class DiffMutationFilterFactoryTest {

	@Test
	void testInterceptorType() {
		DiffMutationFilterFactory diffMutationFilterFactory = new DiffMutationFilterFactory();
		MutationInterceptor diffMutationFilter = diffMutationFilterFactory
				.createInterceptor(makeFor("file", "LICENSE"));
		assertEquals(InterceptorType.FILTER, diffMutationFilter.type());
	}

	private InterceptorParameters makeFor(String key, String... vals) {
		final Map<String, List<String>> values = new HashMap<>();
		values.put(key, Arrays.asList(vals));
		final FeatureSetting fs = new FeatureSetting(null, null, values);
		return new InterceptorParameters(fs, null, null);
	}

}
