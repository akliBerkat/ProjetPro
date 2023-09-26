package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountCommitsPerAuthorPlugin implements AnalyzerPlugin {
    private final Configuration configuration;
    private Result result;

    public CountCommitsPerAuthorPlugin(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }

    static Result processLog(List<Commit> gitLog) {
        var result = new Result();
        for (var commit : gitLog) {
            var nb = result.commitsPerAuthor.getOrDefault(commit.author, 0);
            //var mb = result.commitsPerMail.getOrDefault(commit.email, 0);         //  ajouter
            result.commitsPerAuthor.put(commit.author, nb + 1);
            //result.commitsPerMail.put(commit.email, mb + 1);              //ajouter
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
        private final Map<String, Integer> commitsPerAuthor = new HashMap<>();
        
        //private final Map<String, Integer> commitsPerMail = new HashMap<>();

        Map<String, Integer> getCommitsPerAuthor() {
            return commitsPerAuthor;
        }
        
      //on a ajouté ça
        /*
        Map<String, Integer> getCommitsPerMail() {
            //return commitsPerAuthor;
            return commitsPerMail;
        }

         */

        @Override
        public String getResultAsString() {
            return commitsPerAuthor.toString();
            //return commitsPerMail.toString();
        }
        
        @Override
       // public String getPageType(){
       //     return "perAuthor";
       // }
        
        public String getResultAsHtmlDiv() {
            StringBuilder js = new StringBuilder("<html><script src='https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.5.0/Chart.min.js'></script><body> <canvas id='myChart'></canvas><script> var xValues = [''");
            for (var item : commitsPerAuthor.entrySet()){
                js.append(",'").append(item.getKey()).append("'");
            }
            js.append("]; new Chart('myChart', {type:'bar', data:{labels: xValues,datasets: [{ data: [0 ");
            for(var item : commitsPerAuthor.entrySet()){
                js.append(",").append(item.getValue());
            }
            js.append("], borderColor:'blue',backgroundColor:'blue' , fill:true}]}, options: {legend: {display: false}}}); </script></body></html>");
            return js.toString();
        }

        
        //TODO: creer une fonction qui genere un fichier js avec les graphes
        public String getResultAsJs (){
            return null;
        }

//        public String getResultAsHtmlDiv() {
//            StringBuilder html = new StringBuilder("<div>Numbers of Commits per author: <ul>");
//            for (var item : commitsPerAuthor.entrySet()) {
//                html.append("<li>").append(item.getKey()).append(": ").append(item.getValue()).append("</li>");
//            }
//            html.append("</ul></div>");
//            return html.toString();
//        }
   }
}

