package eu.stamp_project.interceptors;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class DiffInfoTest {

	@Test
	void testFromFileWithANotExistingFile() {
		assertThrows(RuntimeException.class, () -> {
			DiffInfo.fromFile("notExistingPath");
		});
	}

}
