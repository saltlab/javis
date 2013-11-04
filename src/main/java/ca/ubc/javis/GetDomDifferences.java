package ca.ubc.javis;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ubc.javis.unixdiff.TargetedDiff;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class GetDomDifferences {

	public static Logger LOG = LoggerFactory.getLogger(GetDomDifferences.class);
	public static StringBuilder buffer = new StringBuilder();

	public static void calculateAndSave(String previous, String next, String id)
	        throws IOException, InterruptedException {

		
		try {
			Files.write(previous, new File(JavisRunner.path + JavisRunner.counter
			        + JavisRunner.name + "//firstDom.txt"), Charsets.UTF_8);
			Files.write(next, new File(JavisRunner.path + JavisRunner.counter
			        + JavisRunner.name + "//secondDom.txt"), Charsets.UTF_8);
		} catch (IOException e) {
			LOG.error("Cannot write to file(s). Reason: ", e);
		}

		String result =
		        TargetedDiff.getTargetDiff(JavisRunner.path + JavisRunner.counter
		                + JavisRunner.name + "//firstDom.txt", JavisRunner.path
		                + JavisRunner.counter + JavisRunner.name + "//secondDom.txt");
		buffer.append(result);
		printResult(result, buffer.toString().trim(), id);

	}

	public static void printResult(String str1, String str2, String id) throws SecurityException,
	        IOException {
			
		try {
			File totalChange = new File(JavisRunner.path + JavisRunner.counter
			        + JavisRunner.name + "//TotalChangeResultLog.txt");
			Files.write(str2, totalChange , Charsets.UTF_8);
			Files.write(str1, new File(JavisRunner.path + JavisRunner.counter
			        + JavisRunner.name + "//individualChangeResultLog" + id + ".txt"),
			        Charsets.UTF_8);

		} catch (IOException e) {
			LOG.error("Cannot write to file(s). Reason: ", e);
		}

	}

}
