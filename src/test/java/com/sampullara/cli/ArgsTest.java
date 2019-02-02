package com.sampullara.cli;

import junit.framework.TestCase;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

/**
 * User: sam
 * Date: Dec 27, 2005
 * Time: 3:31:44 PM
 */
public class ArgsTest extends TestCase {
    public void testArgsParse() {
        TestCommand tc = new TestCommand();
        Args.usage(tc);
        String[] args = {"-input", "inputfile", "-o", "outputfile", "extra1", "-someflag", "extra2", "-m", "10", "-values", "1:2:3", "-strings", "sam;dave;jolly"};
        List<String> extra = Args.parse(tc, args);
        assertEquals("inputfile", tc.inputFilename);
        assertEquals(new File("outputfile"), tc.outputFile);
        assertEquals(true, tc.someflag);
        assertEquals(10, tc.minimum.intValue());
        assertEquals(3, tc.values.length);
        assertEquals(2, tc.values[1].intValue());
        assertEquals("dave", tc.strings[1]);
        assertEquals(2, extra.size());
    }

    public void testMultipleArgs() {
      TestCommand tc = new TestCommand();
      Args.usage(tc);
      String[] args = {"-input", "inputfile", "-o", "outputfile", "extra1", "-someflag", "extra2", "-m", "10", "-values", "1", "-values", "2", "-values", "3", "-strings", "sam", "-strings", "dave", "-strings", "jolly"};
      List<String> extra = Args.parse(tc, args);
      assertEquals("inputfile", tc.inputFilename);
      assertEquals(new File("outputfile"), tc.outputFile);
      assertEquals(true, tc.someflag);
      assertEquals(10, tc.minimum.intValue());
      assertEquals(3, tc.values.length);
      assertEquals(2, tc.values[1].intValue());
      assertEquals("dave", tc.strings[1]);
      assertEquals(2, extra.size());
    }

    public void testStaticFields() {
        Args.usage(TestCommand4.class);
        String[] args = {"-input", "inputfile", "-output", "outputfile"};
        List<String> extra = Args.parse(TestCommand4.class, args);
        assertEquals("inputfile", TestCommand4.input);
        assertEquals("outputfile", TestCommand4.output);
    }

    public void testBadArgsParse() {
        String[] args = {"-fred", "inputfile", "-output", "outputfile"};
        try {
            List<String> extra = Args.parse(TestCommand4.class, args);
            fail("Should have thrown an exception");
        } catch (IllegalArgumentException iae) {
            assertEquals("Invalid argument: -fred", iae.getMessage());
        }
        args = new String[] {"-input", "inputfile"};
        try {
            List<String> extra = Args.parse(TestCommand4.class, args);
            fail("Should have thrown an exception");
        } catch (IllegalArgumentException iae) {
            assertEquals("You must set argument output", iae.getMessage());
        }
    }

    public void testMethodArgsParse() {
        TestCommand2 tc = new TestCommand2();
        Args.usage(tc);
        String[] args = {"-input", "inputfile", "-o", "outputfile", "extra1", "-someflag", "extra2", "-m", "10", "-values", "1:2:3"};
        List<String> extra = Args.parse(tc, args);
        assertEquals("inputfile", tc.inputFilename);
        assertEquals(new File("outputfile"), tc.outputFile);
        assertEquals(true, tc.someflag);
        assertEquals(10, tc.minimum.intValue());
        assertEquals(3, tc.values.length);
        assertEquals(2, tc.values[1].intValue());
        assertEquals(2, extra.size());
    }

    public void testMixedArgsParse() {
        TestCommand3 tc = new TestCommand3();
        Args.usage(tc);
        String[] args = {"-input", "inputfile", "-o", "outputfile", "extra1", "-someflag", "extra2", "-m", "10", "-values", "1:2:3"};
        List<String> extra = Args.parse(tc, args);
        assertEquals("inputfile", tc.inputFilename);
        assertEquals(new File("outputfile"), tc.outputFile);
        assertEquals(true, tc.someflag);
        assertEquals(10, tc.minimum.intValue());
        assertEquals(3, tc.values.length);
        assertEquals(2, tc.values[1].intValue());
        assertEquals(2, extra.size());
    }

    public void testStaticCommand() {
        Args.usage(StaticTestCommand.class);
        String[] args = { "-num", "1", "extra" };
        List<String> extra = Args.parse(StaticTestCommand.class, args);
        assertEquals(1, (int) StaticTestCommand.num);
        assertEquals("extra", extra.get(0));
    }

    public void testDerivedCommand() {
        String[] args = { "-help", "-verbose" };
        TestCommand6 tc = new TestCommand6();
        Args.parse(tc, args);
        assertTrue(tc.help);
        assertTrue(tc.verbose);
    }

    
    public void testUsageShowsParameterClassByDefault() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        Args.usage(ps, Example.class);
        
        ps.flush();
        String usage = new String(baos.toByteArray());
        assertTrue(usage.startsWith("Usage: " + Example.class.getName()));
    }
    
    public void testUsageShowsGivenMainClass() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        Args.usage(ps, Example.class, ArgsTest.class);
        
        ps.flush();
        String usage = new String(baos.toByteArray());
        assertTrue(usage.startsWith("Usage: " + ArgsTest.class.getName()));
    }

    public void testEnvvar() {
        String envvar = System.getenv("TEST_ENV_VAR");
        if (envvar == null) {
            // there is no way to setenv from java, has to be set externally
            // sadly, there is no way to conditionally ignore junit3 tests either
            return;
        }

        TestCommand7 tc = new TestCommand7();
        Args.parse(tc, new String[0]);
        assertEquals(envvar, tc.arg);

        // explicit cli value wins
        Args.parse(tc, new String[] { "-arg", "value provided via cli" });
        assertEquals("value provided via cli", tc.arg);
    }

    public void testRequiredUnsetEnvvar() {
        assertNull("TEST_UNSET_ENV_VAR must not be set", System.getenv("TEST_UNSET_ENV_VAR"));

        try {
            TestCommand8 tc = new TestCommand8();
            Args.parse(tc, new String[0]);
            fail();
        } catch (IllegalArgumentException expected) {
            // java.lang.IllegalArgumentException: You must set argument arg
        }
    }

    public static class TestCommand {
        @Argument(value = "input", description = "This is the input file", required = true)
        private String inputFilename;

        @Argument(value = "output", alias = "o", description = "This is the output file", required = true)
        private File outputFile;

        @Argument(description = "This flag can optionally be set")
        private boolean someflag;

        @Argument(description = "Minimum", alias = "m")
        private Integer minimum;

        @Argument(description = "List of values", delimiter = ":")
        public void setValues(Integer[] values) {
            this.values = values;
        }
        public Integer[] getValues() {
            return values;
        }
        private Integer[] values;

        @Argument(description = "List of strings", delimiter = ";")
        private String[] strings;

        @Argument(description = "not required")
        private boolean notRequired;

    }

    public static class StaticTestCommand {
        @Argument
        private static Integer num;
    }

    public static class TestCommand2 {
        private String inputFilename;

        private File outputFile;

        private boolean someflag;

        private Integer minimum = 0;

        private Integer[] values = new Integer[] { 10 };

        public String getInputFilename() {
            return inputFilename;
        }

        @Argument(value = "input", description = "This is the input file", required = true)
        public void setInputFilename(String inputFilename) {
            this.inputFilename = inputFilename;
        }

        public File getOutputFile() {
            return outputFile;
        }

        @Argument(value = "output", alias = "o", description = "This is the output file", required = true)
        public void setOutputFile(File outputFile) {
            this.outputFile = outputFile;
        }

        public boolean isSomeflag() {
            return someflag;
        }

        @Argument(description = "This flag can optionally be set")
        public void setSomeflag(boolean someflag) {
            this.someflag = someflag;
        }

        public Integer getMinimum() {
            return minimum;
        }

        @Argument(description = "Minimum", alias = "m")
        public void setMinimum(Integer minimum) {
            this.minimum = minimum;
        }

        public Integer[] getValues() {
            return values;
        }

        @Argument(description = "List of values", delimiter = ":")
        public void setValues(Integer[] values) {
            this.values = values;
        }
    }


    public static class TestCommand3 {
        private String inputFilename;

        @Argument(value = "output", alias = "o", description = "This is the output file", required = true)
        private File outputFile;

        private boolean someflag;

        private boolean someotherflag;

        private Integer minimum;

        private Integer[] values;

        public String getInputFilename() {
            return inputFilename;
        }

        @Argument(value = "input", description = "This is the input file", required = true)
        public void setInputFilename(String inputFilename) {
            this.inputFilename = inputFilename;
        }

        public File getOutputFile() {
            return outputFile;
        }

        public void setOutputFile(File outputFile) {
            this.outputFile = outputFile;
        }

        public boolean isSomeflag() {
            return someflag;
        }

        @Argument(description = "This flag can optionally be set")
        public void setSomeflag(boolean someflag) {
            this.someflag = someflag;
        }

        @Argument(description = "This flag can optionally be set")
        public void setSomeotherflag(boolean someotherflag) {
            this.someotherflag = someotherflag;
        }

        public Integer getMinimum() {
            return minimum;
        }

        @Argument(description = "Minimum", alias = "m")
        public void setMinimum(Integer minimum) {
            this.minimum = minimum;
        }

        public Integer[] getValues() {
            return values;
        }

        @Argument(description = "List of values", delimiter = ":")
        public void setValues(Integer[] values) {
            this.values = values;
        }
    }

    public static class TestCommand4 {
        @Argument()
        private static String input;

        @Argument(required = true)
        private static String output;
    }

    public static abstract class TestCommand5 {
        @Argument
        public boolean help;
    }

    public static class TestCommand6 extends TestCommand5 {
        @Argument
        public boolean verbose;
    }

    public static class TestCommand7 {
        @Argument(envvar = "TEST_ENV_VAR")
        public String arg;
    }

    public static class TestCommand8 {
        @Argument(envvar = "TEST_UNSET_ENV_VAR", required = true)
        public String arg;
    }
}
