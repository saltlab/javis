package ca.ubc.javis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import ca.ubc.javis.unixdiff.TargetedDiff;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class getDomDifferences {
	;
	public static StringBuilder buffer = new StringBuilder();

	public static void getDifferences(String previous, String next, String id)
	        throws IOException, InterruptedException {

		FileWriter fstream2;
		try {
			// TODO repeat this
			Files.write(previous, new File(CrawljaxRunner.path + CrawljaxRunner.counter
			        + "//firstDom.txt"), Charsets.UTF_8);

			fstream2 =
			        new FileWriter(CrawljaxRunner.path + CrawljaxRunner.counter
			                + "//secondDom.txt");
			BufferedWriter out2 = new BufferedWriter(fstream2);
			out2.write(next);
			out2.close();
		} catch (IOException e) {
			e.printStackTrace();
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
		FileWriter fstream1, fstream2;
		try {
			fstream1 =
			        new FileWriter(CrawljaxRunner.path + CrawljaxRunner.counter
			                + "//TotalChangeResultLog.txt");
			BufferedWriter out1 = new BufferedWriter(fstream1);
			out1.write(str2);
			out1.close();
			fstream2 =
			        new FileWriter(CrawljaxRunner.path + CrawljaxRunner.counter
			                + "//individualChangeResultLog" + id + ".txt");
			BufferedWriter out2 = new BufferedWriter(fstream2);
			out2.write(str1);
			out2.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
