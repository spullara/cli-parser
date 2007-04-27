package com.sampullara.cli;

import java.io.Writer;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: sam
 * Date: May 4, 2006
 * Time: 6:31:20 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Command {
    public void help(PrintWriter writer);
    public String usage();
    public void execute(String... args);
}
