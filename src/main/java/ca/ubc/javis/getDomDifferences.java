package ca.ubc.javis;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ubc.javis.unixdiff.TargetedDiff;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class getDomDifferences {
	public static Logger LOG = LoggerFactory.getLogger(getDomDifferences.class);
	public static StringBuilder buffer = new StringBuilder();

	public static void getDifferences(String previous, String next, String id)
	        throws IOException, InterruptedException {

		try {
			Files.write(previous, new File(CrawljaxRunner.path + CrawljaxRunner.counter
			        + "//firstDom.txt"), Charsets.UTF_8);
			Files.write(next, new File(CrawljaxRunner.path+ CrawljaxRunner.counter+"//secondDom.txt"), Charsets.UTF_8);
		} catch (IOException e) {
			LOG.error("Cannot write to file(s). Reason: ",e);
		}

		String result =
		        TargetedDiff.getTargetDiff(CrawljaxRunner.path + CrawljaxRunner.counter
		                + "//firstDom.txt", CrawljaxRunner.path + CrawljaxRunner.counter
		                + "//secondDom.txt");
		buffer.append(result);
		printResult(result, buffer.toString().trim(), id);

	}

	public static void printResult(String str1, String str2, String id) throws SecurityException,
	        IOException {
		
		try {
			Files.write(str2, new File(CrawljaxRunner.path+CrawljaxRunner.counter+"//TotalChangeResultLog.txt"), Charsets.UTF_8);
			Files.write(str1, new File(CrawljaxRunner.path + CrawljaxRunner.counter
	                + "//individualChangeResultLog" + id + ".txt"), Charsets.UTF_8);
			
		} catch (IOException e) {
			LOG.error("Cannot write to file(s). Reason: ",e);
		}

	}

}
