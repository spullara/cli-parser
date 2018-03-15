[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Fspullara%2Fcli-parser.svg?type=shield)](https://app.fossa.io/projects/git%2Bgithub.com%2Fspullara%2Fcli-parser?ref=badge_shield)

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
        // unparsed will contain all unparsed arguments to the command line
        List<String> unparsed = Args.parseOrExit(Loader.class, args);
        // Loader's fields will be populated after this line or the program will exit with usage
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
for the parameter if there is one. You can add it to your code with:

    <dependency>
      <groupId>com.github.spullara.cli-parser</groupId>
      <artifactId>cli-parser</artifactId>
      <version>1.1</version>
    </dependency>
    
Here is the license:

    Copyright 2012 Sam Pullara

       Licensed under the Apache License, Version 2.0 (the "License");
       you may not use this file except in compliance with the License.
       You may obtain a copy of the License at

           http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing, software
       distributed under the License is distributed on an "AS IS" BASIS,
       WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
       See the License for the specific language governing permissions and
       limitations under the License.


[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Fspullara%2Fcli-parser.svg?type=large)](https://app.fossa.io/projects/git%2Bgithub.com%2Fspullara%2Fcli-parser?ref=badge_large)