package ca.ubc.javis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.crawljax.browser.EmbeddedBrowser;
import com.crawljax.core.CandidateElement;
import com.crawljax.core.CandidateElementExtractor;
import com.crawljax.core.CrawlSession;
import com.crawljax.core.TagElement;
import com.crawljax.core.plugin.PreCrawlingPlugin;
import com.crawljax.core.plugin.PreStateCrawlingPlugin;

public class GetCandidateElements implements PreStateCrawlingPlugin{

	public String[] elementNames={"A","DIV","SPAN","IMG","INPUT","BUTTON"};
	public static int[] elementCounter={0,0,0,0,0,0};
	

	@Override
	public void preStateCrawling(CrawlSession session,
			List<CandidateElement> candidateElements) {
	
	try {
		FileWriter fstream1 = new FileWriter("CandidateElementCounter.txt",true);
		BufferedWriter out1 = new BufferedWriter(fstream1);
		for(CandidateElement candidateElement:candidateElements)
			for(int i=0;i<6;i++){
				if(elementNames[i].equals(candidateElement.getElement().getTagName().toString()))
					elementCounter[i]+=1;
			}
		out1.write("-----------------------------------\n"+"A candidate Elements: "+elementCounter[0]+"\nDiv candidate Elements: "+elementCounter[1]+"\nSpan candidate Elements: "+elementCounter[2]+	
				"\nImg candidate Elements: "+elementCounter[3]+"\nInput candidate Elements: "+elementCounter[4]+"\nButton candidate Elements: "+elementCounter[5]+"\n");
		out1.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
		

}


}