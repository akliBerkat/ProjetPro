package up.visulog.config;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.io.*;
public class Configuration {

    private final Path gitPath;
    private final Map<String, PluginConfig> plugins;
    //private final Path gen; //change akli
    
    public Configuration(Path gitPath, Path gen,Map<String, PluginConfig> plugins) {
        this.gitPath = gitPath;
        this.plugins = Map.copyOf(plugins);
        // change akli this.gen=gen;
    }

    public Path getGitPath() {
        return gitPath;
    }

    public Map<String, PluginConfig> getPluginConfigs() {
        return plugins;
    }
   // public Path getgen(){
   // 	return this.gen;
   // }
   // public String toString() {
   // String r =
   //     String.format("gitpath: %s\ntemplate: %s\npluginlist: ", gitPath, gen);
   // for (Map.Entry<String, PluginConfig> p : plugins.entrySet()) {
   //   r += String.format("%s:", p.getKey());
   // }
   // r += "\n";
   // return r;
  //}
  //affichage
    public void print(){
        for (String key: plugins.keySet()){  
            System.out.println(key + " = " + plugins.get(key));
        } 
    }

	public Configuration get() {
		// TODO Auto-generated method stub
		return this;
	}
	public boolean isPresent() {
		return(this.getGitPath()!=null);
	}
}

