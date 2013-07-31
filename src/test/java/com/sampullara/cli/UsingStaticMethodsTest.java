package com.sampullara.cli;

import java.nio.charset.*;

import junit.framework.*;

public class UsingStaticMethodsTest extends TestCase {
	private static final String UTF_8_STR = "UTF-8";
	
	@Override
	protected void setUp() throws Exception {
		Args.resetValueCreators();
	}
	
	public void testCharsetCannotBeParsedByDefault() {
		CommandCLI cli = new CommandCLI();
		
		String[] args = new String[] {"-charset", UTF_8_STR};
		
		try {
			Args.parse(cli, args);
			fail("without specified ValueCreator Charset object could not be created");
		} catch (IllegalArgumentException e) {
			// expected
		}
	}
	
	public void testCharsetCanBeParsedByRegisteringAValueCreator() {
		CommandCLI cli = new CommandCLI();
		
		Args.registerValueCreator(Args.byStaticMethodInvocation(Charset.class, "forName"));
		
		String[] args = new String[] {"-charset", UTF_8_STR};
		Args.parse(cli, args);
		
		assertNotNull("charset value not built", cli.getCharset());
		assertTrue("built object is not a " + Charset.class + " class object", Charset.class.isAssignableFrom(cli.getCharset().getClass()));
		assertEquals("retrieved charset is not " + UTF_8_STR, UTF_8_STR, cli.getCharset().name());
	}

	
	public static class CommandCLI {
		@Argument()
		private Charset charset;

		public Charset getCharset() {
			return charset;
		}
	}
}