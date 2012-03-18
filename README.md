CLI Parser
==========

**CLI Parser** is a tiny (10k jar), super easy to use library for parsing various kinds of command line arguments or
property lists. Using annotations on your fields or JavaBean properties you can specify what configuration
is available. Here is an example:

    public class Loader {

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

      public static void main(String[] args) throws IOException {
        final List<String> parse;
        try {
          parse = Args.parse(Loader.class, args);
        } catch (IllegalArgumentException e) {
          Args.usage(Loader.class);
          System.exit(1);
          return;
        }

        // Use all those parameters...

      }
    }

In this case we are configuring static fields, but you can also use the same system with instances. If you pass
in a wrong command line argument you will get the usage message:

    Usage: com.sampullara.cli.Example
      -hdfs [flag]
      -regex (-r) [String] Regular expression to parse lines
      -key (-k) [String] Key column
      -prefix (-p) [String] Key prefix
      -columns (-c) [String[,]] Column groups
      -names (-n) [String[,]] Column names
      -host (-h) [String] Redis host (localhost)
      -port (-p) [Integer] Redis port (6379)

That message will print out the names and aliases of the arguments, type, description and a default value
for the parameter if there is one.
