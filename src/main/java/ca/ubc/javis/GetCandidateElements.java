package ca.ubc.javis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.crawljax.core.CandidateElement;
import com.crawljax.core.CrawlSession;
import com.crawljax.core.plugin.PreStateCrawlingPlugin;

public class GetCandidateElements implements PreStateCrawlingPlugin {

	private static final Logger LOG = LoggerFactory.getLogger(GetCandidateElements.class);
	public String[] elementNames = { "A", "DIV", "SPAN", "IMG", "INPUT", "BUTTON" };
	public static int[] elementCounter = { 0, 0, 0, 0, 0, 0 };

	public static final ElementCounter COUNTER = new ElementCounter();

	@Override
	public void preStateCrawling(CrawlSession session, List<CandidateElement> candidateElements) {

		String filename =
		        CrawljaxRunner.path + CrawljaxRunner.counter + "//CandidateElementCounter.txt";
		try {
			FileWriter fstream1 =

			new FileWriter(filename, true);
			BufferedWriter out1 = new BufferedWriter(fstream1);
			for (CandidateElement candidateElement : candidateElements)
				for (int i = 0; i < 6; i++) {
					if (elementNames[i].equals(candidateElement.getElement().getTagName()
					        .toString()))
						elementCounter[i] += 1;
				}
			out1.write("-----------------------------------\n" + "A candidate Elements: "
			        + elementCounter[0] + "\nDiv candidate Elements: " + elementCounter[1]
			        + "\nSpan candidate Elements: " + elementCounter[2]
			        + "\nImg candidate Elements: " + elementCounter[3]
			        + "\nInput candidate Elements: " + elementCounter[4]
			        + "\nButton candidate Elements: " + elementCounter[5] + "\n");
			out1.close();
		} catch (IOException e) {
			LOG.error("I couldnt run prestate crawling on " + filename);
		}

	}
}