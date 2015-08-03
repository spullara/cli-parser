package com.sampullara.cli;

import junit.framework.TestCase;

import java.io.*;

public class EnumTest extends TestCase {
	
	public void testCanUseAnEnumValue() {
		CommandCLI cli = new CommandCLI();
		String[] args = new String[] {"-command", "START"};
		Args.parse(cli, args);
		
		assertNotNull("Commands enum value not built", cli.getCommand());
		assertEquals("retrieved command value is not Commands.START", Commands.START, cli.getCommand());
	}

    public void testUsageForEnum() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos, true, "UTF-8");

        Args.usage(ps, new CommandCLI());
        ps.close();


        BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()), "UTF-8"));

        String line;
        StringBuilder foundUsageInfo = new StringBuilder(1024);

        while ((line = br.readLine()) != null) {
            line = line.trim();
            int index = line.indexOf("Possible values are:");
            if (index != -1) {
                foundUsageInfo.append(line.substring(index + "Possible values are:".length()).trim());
                foundUsageInfo.append("\n");
            }
        }

        assertEquals("START, STOP, PAUSE", foundUsageInfo.toString().trim());
    }
	
	public static class CommandCLI {
		@Argument()
		private Commands command;

		public Commands getCommand() {
			return command;
		}
	}
	public static enum Commands {
		START, STOP, PAUSE;
	}
}