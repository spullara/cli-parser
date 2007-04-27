package com.sampullara.cli;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: sam
 * Date: Dec 27, 2005
 * Time: 3:31:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class ArgsTest extends AssertJUnit {
    @Test
    public void testArgsParse() {
        TestCommand tc = new TestCommand();
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

    @Test
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

    @Test
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

    @Test
    public void testCommands() {
        TestCommand4 tc = new TestCommand4();
        Args.usage(tc);
        String[] args = {"-input", "inputfile", "-o", "outputfile", "-someflag", "-m", "10", "-values", "1:2:3"};
        List<String> extra = Args.parse(tc, args);
        assertEquals("inputfile", tc.inputFilename);
        assertEquals(new File("outputfile"), tc.outputFile);
        assertEquals(true, tc.someflag);
        assertEquals(10, tc.minimum.intValue());
        assertEquals(3, tc.values.length);
        assertEquals(2, tc.values[1].intValue());
        args = new String[] {"help", "run", "-o", "outputfile", "-input", "inputfile",};
        Args.parse(tc, args);
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

    }

    public static class TestCommand2 {
        private String inputFilename;

        private File outputFile;

        private boolean someflag;

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

    @CommandMap({
    @Mapping(name = "run", command = RunCommand.class),
    @Mapping(name = "add", command = AddCommand.class)
            })
    public static class TestCommand4 {
        private String inputFilename;

        @Argument(value = "output", alias = "o", description = "This is the output file", required = true)
        private File outputFile;

        private boolean someflag;

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
}
