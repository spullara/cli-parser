package com.sampullara.cli;

import java.io.PrintWriter;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: sam
 * Date: May 4, 2006
 * Time: 6:34:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class RunCommand implements Command {

    public void help(PrintWriter writer) {
        writer.println("This is the help for the Run command");
    }

    public String usage() {
        return " [classname]";
    }

    public void execute(String... args) {
        System.out.println(Arrays.asList(args));
    }
}
