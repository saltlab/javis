package ca.ubc.javis;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

public class TestingDomDifferences {

	@Test
	public void testGetDomDifferences() throws IOException, InterruptedException {
		String id = "1";
		String firstDom =
		        "<html><body><div id='div1'>Hello</div><a id='a1'>Go to Google</a></body></html>";
		String secondDom =
		        "<html><body><div id='div2'>Bye</div><img id='img1' width='23' height='21'/></body></html>";
		getDomDifferences.getDifferences(firstDom, secondDom, id);
		assertNotNull(getDomDifferences.buffer);
	}

}
