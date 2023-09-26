package up.visulog.cli;
import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import up.visulog.analyzer.Analyzer;
import up.visulog.config.Configuration;
import up.visulog.config.PluginConfig;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.FileSystems;
import java.nio.file.Files;
//import up.visulog.webgen.Generator;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;


public class CLILauncher {

    public static void main(String[] args) {
        var config = makeConfigFromCommandLineArgs(args);
        if (config.isPresent()) {
            var analyzer = new Analyzer(config.get());
            var results = analyzer.computeResults();
            System.out.println(results.toHTML());
        } else displayHelpAndExit();
    }

    static Configuration makeConfigFromCommandLineArgs(String[] args) {
    Path gitPath = null;
    Path templatePath = null;
    var plugins = new HashMap<String, PluginConfig>();
    var options=new Options();
    CommandLineParser parser=null;
    try {
      CommandLine commandLine = parser.parse(options, args);
      if (commandLine == null) {
        displayHelpAndExit();
      }
      // The following statement checks if the argument is correct, so if you have a
      // pluginConfig, make sure to add it to the isValid()
      if (commandLine.hasOption("a")) {
        String[] optionValues = commandLine.getOptionValues("a");
        for (String s : optionValues) {
          if (!isValidPlugin(s)) {
            System.err.println("WRONG COMMAND...\nThe argument of --addPlugin isn't correct\n");
            displayHelpAndExit();
          } else {
            plugins.put(s, new PluginConfig() {});
          }
        }
      }
      if (commandLine.hasOption("l")) {
        try {
          String argsFromFile =
              new String(Files.readAllBytes(Paths.get(commandLine.getOptionValue("l"))));

          String[] optionSplit;
          ArrayList<String> fakeArgs = new ArrayList<String>();
          for (String option : argsFromFile.split("\n")) {
            optionSplit = option.split(": ");
            switch (optionSplit[0]) {
              case "gitpath":
                fakeArgs.add("-r");
                fakeArgs.add(optionSplit[1]);
                break;
              case "template": // TODO case null, ne pas passer l'arg
                if (!optionSplit[1].equals("null")) {
                  fakeArgs.add("-t");
                  fakeArgs.add(optionSplit[1]);
                }
                break;
              case "pluginlist":
                fakeArgs.add("-a");
                for (String plug : optionSplit[1].split(":")) fakeArgs.add(plug);
                break;
              default:
                System.out.println("Unknown config option met.");
                break;
            }
          }
          String[] fakeArgsUsable = new String[fakeArgs.size()];
          fakeArgsUsable = fakeArgs.toArray(fakeArgsUsable);
          return makeConfigFromCommandLineArgs(fakeArgsUsable);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (commandLine.hasOption("j")) {
        var writeToFile = commandLine.getOptionValue("j");
      }
      if (commandLine.hasOption("t")) {
        templatePath = Paths.get(commandLine.getOptionValue("t"));
      }
      if (commandLine.hasOption("h")) {
        displayHelpAndExit();
      }
      if (commandLine.hasOption("r")) {
        gitPath = FileSystems.getDefault().getPath(commandLine.getOptionValue("r"));
      }
    } catch (ParseException e) {
      displayHelpAndExit();
    }
    return new Configuration(gitPath, templatePath, plugins);
      
    }
    private static boolean isValidPlugin(String s) {
        if (s.equals("countCommits")
        | s.equals("commitsPerDay")
        | s.equals("mergesPerAuthor")
        | s.equals("commitsPerAuthorChart")
        | s.equals("signedCommitsProportion")
        | s.equals("linesPerAuthor")) return true;
    return false;
    }
    private static void displayHelpAndExit() {
        System.out.println("Wrong command...");
        //TODO: print the list of options and their syntax
        System.exit(0);
    }
}
