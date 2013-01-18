package ca.ubc.javis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;


public class testing {

	@Test
	public void getDifferences() throws IOException{
	String after = "<html><body><div>hello<p>world</p><span>some texxtthat is important</span></div></body></html>";
	FileWriter fstream;
	BufferedWriter out1;
	fstream = new FileWriter("Differences.html",true);
	out1 = new BufferedWriter(fstream);
	out1.write(after);
	out1.close();
	}
}
