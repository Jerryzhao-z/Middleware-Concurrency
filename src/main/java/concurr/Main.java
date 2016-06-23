package concurr;

import java.util.concurrent.*;
import java.io.File;
import java.util.ArrayList;

public class Main
{

	public static void main(String[] args)
	{
		//declaration de index, executoservice, file
		ConcurrentHashMap<String, ConcurrentSkipListSet<String>> index = 
			new ConcurrentHashMap<String, ConcurrentSkipListSet<String>>();
		ExecutorService executor = Executors.newFixedThreadPool(10);
		File file = null;
		Future f=null;
		if(args.length > 0)
			file = new File(args[0]);
		else
		{
			System.out.println("tu doit taper un path");
			System.exit(0);	
		}
		
		//
		if(file.exists())
		{
			f = executor.submit(new IndexJob(file, index, executor));
		}else
		{
			System.out.println("this file n'existe pas");
			System.exit(1);	
		}
		//attendre le fin de tous les threads
		System.out.println("Indexing " + file.getName());
		while(!f.isDone()){}
		System.out.println(index);
		executor.shutdown();
		while (!executor.isTerminated()) {}
        System.out.println("Finished all threads");
		
	}
	
}
