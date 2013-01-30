package ca.ubc.javis;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestingGetURL {

	@Test
	public void testGetURL() {
		String path = "C:\\Users\\Jana\\Desktop\\URLTest.txt";
		int size = 3;
		String [] array = new String[size];
		array = GetUrls.getArray(path, size);
		assertNotNull(array);
	}

}
