// TODO (imports, package, etc)
package concurr;

import java.util.concurrent.*;
import java.io.File;
import java.io.*;
import java.util.Set;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.List;

public class IndexJob implements Callable<Object> {

  private final File file;
  private final ConcurrentHashMap<String, ConcurrentSkipListSet<String>> index;
  private final ExecutorService executorService;

  public IndexJob(File file,
                  ConcurrentHashMap<String, ConcurrentSkipListSet<String>> index,
                  ExecutorService executorService) {
    this.file = file;
    this.index = index;
    this.executorService = executorService;
  }

  @Override
  public Object call() throws Exception {
    System.out.println("Indexing " + file);
    if (file.isDirectory()) {
      File[] fils = file.listFiles();
      ArrayList<Future<Object>> fs = new ArrayList<Future<Object>>();
      for(File file: fils){
		Future<Object> f = executorService.submit(new IndexJob(file, index, executorService));
		fs.add(f);
      }
	  //TODO:verifier tous les fs sont finis.
      
	  while(!isAllDone(fs));
	  return "OK";
    } else{
      ConcurrentSkipListSet<String> mots = new ConcurrentSkipListSet<String>(wordsInFile(file));
      index.put(file.getAbsolutePath(), mots);
	  return "OK";
    }
  }

  private Set<String> wordsInFile(File file) {
    TreeSet<String> words = new TreeSet<>();
    try (BufferedReader reader = new BufferedReader(
                                 new InputStreamReader(
                                 new FileInputStream(file))) ) {
      String line = reader.readLine();
      while (line != null) {
        for (String word : wordsInLine(line)) {
          words.add(word);
        }
        line = reader.readLine();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return words;
  }

  private String[] wordsInLine(String line) {
    return line.trim().split("(\\s|\\p{Punct})+");
  }
  
  public static boolean isAllDone(List<Future<Object>> futures) {
	  for (Future<Object> future : futures) {
		  //④
		if(!future.isDone()) 
		{
			return false;
		}
	  }
		return true;
  }
}
