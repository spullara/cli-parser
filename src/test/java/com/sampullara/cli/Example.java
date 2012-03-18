package com.sampullara.cli;

import java.util.List;

public class Example {

  @Argument
  private static Boolean hdfs = false;

  @Argument(alias = "r", description = "Regular expression to parse lines", required = true)
  private static String regex;

  @Argument(alias = "k", description = "Key column", required = true)
  private static String key;

  @Argument(alias = "p", description = "Key prefix")
  private static String prefix;

  @Argument(alias = "c", description = "Column groups", delimiter = ",")
  private static String[] columns;

  @Argument(alias = "n", description = "Column names", delimiter = ",")
  private static String[] names;

  @Argument(alias = "h", description = "Redis host")
  private static String host = "localhost";

  @Argument(alias = "p", description = "Redis port")
  private static Integer port = 6379;

  public static void main(String[] args) {
    final List<String> parse;
    try {
      parse = Args.parse(Example.class, args);
    } catch (IllegalArgumentException e) {
      Args.usage(Example.class);
      System.exit(1);
      return;
    }

    // Use all those parameters...

  }
}
