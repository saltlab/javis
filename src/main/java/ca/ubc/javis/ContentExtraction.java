package ca.ubc.javis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.Charsets;

import com.google.common.io.Files;

public class ContentExtraction {
	private static final Pattern TAG_REGEX = Pattern.compile("<.+?>(.+?)</.+?>");
	
	public void getTagValues(final String str) {
	    final List<String> tagValues = new ArrayList<String>();
	    final Matcher matcher = TAG_REGEX.matcher(str);
	    while (matcher.find()) {
	        tagValues.add(matcher.group(1));
	    }
	    try {
			Files.write(tagValues.toString(),new File(CrawljaxRunner.path+CrawljaxRunner.counter+"//TotalContent.txt"), Charsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
