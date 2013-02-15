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

	@Override
	public void preStateCrawling(CrawlSession session, List<CandidateElement> candidateElements) {

		String filename =
		        CrawljaxRunner.path + CrawljaxRunner.counter + "//CandidateElementCounter.txt";
		try {
			for (CandidateElement candidateElement : candidateElements){
					if(candidateElement.getElement().getTagName()
					        .equalsIgnoreCase("A"))
						COUNTER.incrementAnchors();
					else if(candidateElement.getElement().getTagName().equalsIgnoreCase("DIV"))
						COUNTER.incrementDivs();
					else if(candidateElement.getElement().getTagName().equalsIgnoreCase("SPAN"))
						COUNTER.incrementSpans();
					else if(candidateElement.getElement().getTagName().equalsIgnoreCase("IMG"))
						COUNTER.incrementImages();
					else if(candidateElement.getElement().getTagName().equalsIgnoreCase("BUTTON"))
						COUNTER.incrementButton();
					else if(candidateElement.getElement().getTagName().equalsIgnoreCase("INPUT"))
						COUNTER.incrementInputs();
				}
			String result = ("-----------------------------------\n" + "A candidate Elements: "
			        + ElementCounter.getAnchors()
			        + "\nDiv candidate Elements: " + ElementCounter.getDivs()
			        + "\nSpan candidate Elements: " + ElementCounter.getSpans()
			        + "\nImg candidate Elements: " + ElementCounter.getImages()
			        + "\nInput candidate Elements: " + ElementCounter.getInputs()
			        + "\nButton candidate Elements: " + ElementCounter.getButtons() + "\n");
			Files.write(result, new File(filename), Charsets.UTF_8);
		} catch (IOException e) {
			LOG.error("I couldnt run prestate crawling on " + filename);
		}

	}
}