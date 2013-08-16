package ca.ubc.javis;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.io.Files;


public class ContentExtraction {

	public void getContents() throws IOException {
		
		List<String> diffLines =
		        Files.readLines(new File(JavisRunner.path+JavisRunner.counter+JavisRunner.name+"//TotalChangeResultLog.txt"),
		                Charsets.UTF_8);

		StringBuilder html = new StringBuilder();
		for (String line : diffLines) {
			if (hasContent(line) && line.length() > 1) {
				html.append(line.substring(1));
			}
		}

		String onlyContent = html.toString().replaceAll("<.*?>", System.lineSeparator());
		onlyContent = onlyContent.replaceAll(".*\\{.*?}", System.lineSeparator());
		onlyContent = onlyContent.replaceAll(".*;", System.lineSeparator());
		onlyContent.trim();
		onlyContent = onlyContent.replaceAll("\\W"," ");
		onlyContent = onlyContent.replaceAll("\\s+"," ");
		Files.write(onlyContent.trim(),new File(JavisRunner.path + JavisRunner.counter
		        + JavisRunner.name + "//TotalContent.txt"), Charsets.UTF_8);
		/*System.out.println("Only HTML = " + html);
		System.out.println("Only content: " + onlyContent.trim());*/
	}
	
	private static boolean hasContent(String line) {
		return line.startsWith("<") || line.startsWith(">");
	
	}
}
