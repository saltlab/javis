package ca.ubc.javis;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.crawljax.core.CandidateElement;
import com.crawljax.core.CrawlSession;
import com.crawljax.core.plugin.PreStateCrawlingPlugin;
import com.google.common.io.Files;

public class GetCandidateElements implements PreStateCrawlingPlugin {

	private static final Logger LOG = LoggerFactory.getLogger(GetCandidateElements.class);
	public static final ElementCounter COUNTER = new ElementCounter();

	@SuppressWarnings("static-access")
	@Override
	public void preStateCrawling(CrawlSession session, List<CandidateElement> candidateElements) {

		String filename =
		        CrawljaxRunner.path + CrawljaxRunner.counter + "//CandidateElementCounter.txt";
		String result;
		try {
			COUNTER.sortCandidateElements(candidateElements);
			if((COUNTER.getAnchors() != 0) || (COUNTER.getButtons() != 0)||(COUNTER.getDivs() != 0)||(COUNTER.getImages() != 0)||
					(COUNTER.getInputs()!= 0)|| (COUNTER.getSpans() != 0)){
			result = ("-----------------------------------\n" + "A candidate Elements: "
			        + ElementCounter.getAnchors()
			        + "\nDiv candidate Elements: " + COUNTER.getDivs()
			        + "\nSpan candidate Elements: " + COUNTER.getSpans()
			        + "\nImg candidate Elements: " + COUNTER.getImages()
			        + "\nInput candidate Elements: " + COUNTER.getInputs()
			        + "\nButton candidate Elements: " + COUNTER.getButtons() + "\n");
			Files.write(result, new File(filename), Charsets.UTF_8);
			}
		} catch (IOException e) {
			LOG.error("I couldnt run prestate crawling on " + filename + " "+ e);
		}

	}
}