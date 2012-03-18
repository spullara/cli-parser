package com.sampullara.cli;

import junit.framework.TestCase;

import java.io.File;
import java.util.Properties;

/**
 * User: sam
 * Date: Dec 27, 2005
 * Time: 3:31:44 PM
 */
public class PropertiesArgsTest extends TestCase {
    public void testArgsParseWithProperties() {
        TestCommand tc = new TestCommand();
        Args.usage(tc);
        Properties p = new Properties();
        p.put("input", "inputfile");
        p.put("o", "outputfile");
        p.put("someflag", "true");
        p.put("m", "10");
        p.put("values", "1:2:3");
        p.put("strings", "sam;dave;jolly");
        PropertiesArgs.parse(tc, p);
        assertEquals("inputfile", tc.inputFilename);
        assertEquals(new File("outputfile"), tc.outputFile);
        assertEquals(true, tc.someflag);
        assertEquals(10, tc.minimum.intValue());
        assertEquals(3, tc.values.length);
        assertEquals(2, tc.values[1].intValue());
        assertEquals("dave", tc.strings[1]);
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
        private Integer[] values;

        @Argument(description = "List of strings", delimiter = ";")
        private String[] strings;

        @Argument(description = "not required")
        private boolean notRequired;

    }
}