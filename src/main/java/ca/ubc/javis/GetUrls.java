package ca.ubc.javis;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class GetUrls {

	public static String[] getArray(String path, int size) {
		try {
			// TODO use the list instead of the array everywhere else.
			List<String> readLines = Files.readLines(new File(path), Charsets.UTF_8);
			return readLines.toArray(new String[readLines.size()]);
		} catch (IOException e) {
			throw new RuntimeException("Oh no"); // TODO
		}

	}
}
