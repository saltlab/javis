package ca.ubc.javis;

import java.io.BufferedReader;
import java.io.FileReader;

public class ContentExtraction2 {
	
	public static String path = "src/main/resources/TotalChangeResultLog.txt";
		 public static void main (String[] args) throws Exception{
		     StringBuilder sb = new StringBuilder();
		     BufferedReader br = new BufferedReader(new FileReader(path));
		     String line;
		     while ( (line=br.readLine()) != null) {
		       sb.append(line);
		     }
		     sb.toString().trim();
		     System.out.println("Main String:\n "+sb.toString());
		     String nohtml = sb.toString().replaceAll("<!-*>","");
		     System.out.println("First Replacement:\n "+nohtml);
		     //String newString = nohtml.replaceAll("\\[^.]*\\-*>","");
		     String newString = nohtml.replaceAll("\\<[^\\>]*\\>","");
		  //   newString.replaceAll("\\<[^\\>]*\\>","");
		     System.out.println("New String: \n"+newString);
		     //nohtml.replaceAll("-*>","");\<[^\>]*\>
		//     System.out.println(nohtml);
	}
		 
}
