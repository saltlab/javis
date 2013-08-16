package ca.ubc.javis;

import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;

import com.crawljax.core.CandidateElement;
import com.crawljax.core.CrawlerContext;
import com.crawljax.core.plugin.PreStateCrawlingPlugin;
import com.crawljax.core.state.StateVertex;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;

public class GetCandidateElements implements PreStateCrawlingPlugin {

	public static final ElementCounter COUNTER = new ElementCounter();
	private final Logger siteLog;

	public GetCandidateElements(Logger siteLog) {
		this.siteLog = siteLog;
	}

	@SuppressWarnings("static-access")
	@Override
	public void preStateCrawling(CrawlerContext context,
			ImmutableList<CandidateElement> candidateElements, StateVertex state) {

		String filename =
		        JavisRunner.path + JavisRunner.counter + JavisRunner.name
		                + "//CandidateElementCounter.txt";
		String result;
		try {
			COUNTER.sortCandidateElements(candidateElements);
			if ((COUNTER.getAnchors() != 0) || (COUNTER.getButtons() != 0)
			        || (COUNTER.getDivs() != 0) || (COUNTER.getImages() != 0)
			        || (COUNTER.getInputs() != 0) || (COUNTER.getSpans() != 0)) {
				result =
				        ("-----------------------------------\n" + "A candidate Elements: "
				                + ElementCounter.getAnchors() + "\nDiv candidate Elements: "
				                + COUNTER.getDivs() + "\nSpan candidate Elements: "
				                + COUNTER.getSpans() + "\nImg candidate Elements: "
				                + COUNTER.getImages() + "\nInput candidate Elements: "
				                + COUNTER.getInputs() + "\nButton candidate Elements: "
				                + COUNTER.getButtons() + "\n");
				Files.write(result, new File(filename), Charsets.UTF_8);
			}
		} catch (IOException e) {
			siteLog.error("I couldnt run prestate crawling on " + filename + " " + e);
		}

	}

}