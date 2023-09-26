package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class commitparjour implements AnalyzerPlugin {
    
    private final Configuration configuration;
    private Result result;

    public commitparjour(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }
    //methode
    static Result processLog(List<Commit> gitLog) {
        var result = new Result();
        
        for (var commit : gitLog) {
         var nb = result.commitsParJour.getOrDefault(commit.date, 0);
         result.commitsParJour.put(commit.date, nb + 1);
       }
    
        return result;
    }
    @Override
    public void run() {
        result = processLog(Commit.parseLogFromCommand(configuration.getGitPath()));
    }

    @Override
    public Result getResult() {
        if (result == null) run();
        return result;
    }

    static class Result implements AnalyzerPlugin.Result {
        private final Map<String, Integer> commitsParJour = new HashMap<>();

        Map<String, Integer> getCommitsParjour() {
            return commitsParJour;
        }

        @Override
        public String getResultAsString() {
            return commitsParJour.toString();
        }

        @Override
        public String getResultAsHtmlDiv() {
            StringBuilder html = new StringBuilder("<div>Numbers of Commits par jour: <ul>");
            for (var item : commitsParJour.entrySet()) {
                html.append("<li>")
                .append(item.getKey())
                .append(": ")
                .append(item.getValue())
                .append("</li>");
            }
            html.append("</ul></div>");
            return html.toString();
        }
    }
    
}
