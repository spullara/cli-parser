package com.sampullara.cli;

import junit.framework.*;

public class EnumTest extends TestCase {
	
	public void testCanUseAnEnumValue() {
		CommandCLI cli = new CommandCLI();
		String[] args = new String[] {"-command", "START"};
		Args.parse(cli, args);
		
		assertNotNull("Commands enum value not built", cli.getCommand());
		assertEquals("retrieved command value is not Commands.START", Commands.START, cli.getCommand());
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