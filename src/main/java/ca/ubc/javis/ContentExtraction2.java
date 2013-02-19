package ca.ubc.javis;

import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class ContentExtraction2 {

	public static void main(String[] args) throws Exception {
		List<String> diffLines =
		        Resources.readLines(Resources.getResource("TotalChangeResultLog.txt"),
		                Charsets.UTF_8);

		StringBuilder html = new StringBuilder();
		for (String line : diffLines) {
			if (hasContent(line) && line.length() > 1) {
				html.append(line.substring(1));
			}
		}

		String onlyContent = html.toString().replaceAll("<.*?>", System.lineSeparator());
		onlyContent = onlyContent.replaceAll(".*;", System.lineSeparator());
		onlyContent = onlyContent.replaceAll(".*,", System.lineSeparator());
		onlyContent.trim();
		onlyContent = onlyContent.replaceAll("\\W"," ");
		onlyContent = onlyContent.replaceAll("\\s+"," ");
		
		System.out.println("Only HTML = " + html);
		System.out.println("Only content: " + onlyContent.trim());
	}

	private static boolean hasContent(String line) {
		return line.startsWith("<") || line.startsWith(">");
	}

}
