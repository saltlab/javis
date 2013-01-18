package ca.ubc.javis.unixdiff;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TargetedDiff {

	public static String getTargetDiff(final String file1, final String file2)
	        throws IOException, InterruptedException {
		List<String> commands = new ArrayList<String>();
		commands.add("/bin/sh");
		commands.add("-c");
		commands.add("/usr/bin/diff " + file1 + " " + file2 + " | grep -v '^<'");

		SystemCommandExecutor commandExecutor = new SystemCommandExecutor(commands);
		int result = commandExecutor.executeCommand();

		// stdout and stderr of the command are returned as StringBuilder objects
		StringBuilder stdout = commandExecutor.getStandardOutputFromCommand();
		StringBuilder stderr = commandExecutor.getStandardErrorFromCommand();
		// System.out.println("The numeric result of the command was: " + result);
		if (!"".equals(stderr.toString())) {
			System.out.println("\nSTDERR:");
			System.out.println(stderr);
		}

		return stdout.toString();
	}
}