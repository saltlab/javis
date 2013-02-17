package ca.ubc.javis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.Charsets;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;

public class ContentExtraction {
	private static final Pattern TAG_REGEX = Pattern.compile("<.+?>(.+?)</.+?>");
	public static Logger LOG = LoggerFactory.getLogger(ContentExtraction.class);

	public void getTagValues(final String str) {
	    final List<String> tagValues = new ArrayList<String>();
	    final Matcher matcher = TAG_REGEX.matcher(str.trim());
	    while (matcher.find()) {
	        tagValues.add(matcher.group(1));
	    }
	    try {
			Files.write(tagValues.toString(),new File(CrawljaxRunner.path+CrawljaxRunner.counter+CrawljaxRunner.name+"//TotalContent.txt"), Charsets.UTF_8);
		} catch (IOException e) {
			LOG.error("Cannot write to file",e);
		}
	}
}