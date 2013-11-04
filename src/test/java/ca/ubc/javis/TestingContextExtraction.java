package ca.ubc.javis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestingContextExtraction {
	public static void main(String[] args) {
	    final String str = "<tag>apple</tag><b>hello</b><tag>orange</tag><tag>pear</tag>";
	    System.out.println(Arrays.toString(getTagValues(str).toArray())); 
	}

	private static final Pattern TAG_REGEX = Pattern.compile(">(.+?)</.+?>");	

	private static List<String> getTagValues(final String str) {
	    final List<String> tagValues = new ArrayList<String>();
	    final Matcher matcher = TAG_REGEX.matcher(str);
	    while (matcher.find()) {
	        tagValues.add(matcher.group(1));
	    }
	    return tagValues;
	}
}
