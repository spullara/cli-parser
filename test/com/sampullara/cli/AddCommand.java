package com.sampullara.cli;

import java.io.Writer;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: sam
 * Date: May 4, 2006
 * Time: 6:34:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class AddCommand implements Command {
    public void help(PrintWriter writer) {
        writer.println("This is the help for the Add command");
    }

    public String usage() {
        return " repository url | dependency groupid artifactid version";
    }

    public void execute(String... args) {
        System.out.println(Arrays.asList(args));
    }
}
