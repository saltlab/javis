package ca.ubc.javis;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.junit.Test;

public class TestingContentExtractions {

	@Test
	public void testContentExtraction() {
	
		final String str = "<tag>apple</tag><b>hello</b><tag>orange</tag><tag>pear</tag>";
		assertNotNull(getTagValues(str));
		assertEquals(4, getTagValues(str).size());
		assertEquals("hello", getTagValues(str).get(1));
		assertEquals("orange", getTagValues(str).get(2));
		
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
