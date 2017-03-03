package com.sampullara.cli;

import org.junit.Test;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import static org.junit.Assert.assertEquals;

public class ValuesProviderTest {
    @Test
    public void testMain() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos, true, "UTF-8");

        Args.usage(ps, new TestCLI());
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

        assertEquals("v1, v2", foundUsageInfo.toString().trim());
    }

    public static class TestCLI {
        @Argument(valuesProvider = ArgProvider.class)
        public String arg;
    }

    public static class ArgProvider implements Callable<List<String>> {
        public List<String> call() throws Exception {
            return Arrays.asList("v1", "v2");
        }
    }
}
