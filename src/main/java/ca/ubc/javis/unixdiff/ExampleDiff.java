package ca.ubc.javis.unixdiff;

import java.io.IOException;

public class ExampleDiff {

	public static void main(String[] args) throws IOException, InterruptedException {
		String result =
		        TargetedDiff.getTargetDiff("C:\\Users\\Jana\\Desktop\\s1.html",
		                "C:\\Users\\Jana\\Desktop\\s2.html");

		        TargetedDiff.getTargetDiff("/ubc/ece/home/am/grads/janab/s1.html",
		                "/ubc/ece/home/am/grads/janab/s2.html");

		System.out.println("results: " + result);
	}

}
