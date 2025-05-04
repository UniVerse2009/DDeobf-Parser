import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.io.File;

import java.io.FileReader;
import java.io.FileWriter;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.Queue;
import java.util.ArrayDeque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class DDeobf{
	public static void main(String[] args)throws IOException, InterruptedException{
		long start = System.nanoTime();
		String obfFolderPath =  "";
		String obfDictPath = "";
		int threadCount = 0;

		System.out.println("==========Parser App==========");

		try{
			obfFolderPath = args[0];
			obfDictPath = args[1];
		}catch(ArrayIndexOutOfBoundsException e){
			System.out.println("Usage: java -jar DDeobf <obfFolderPath> <obfDictPath> <threadCount>");
			System.exit(1);
		}

		try{
			if(args[2].equals("dynamic")){
				threadCount = Runtime.getRuntime().availableProcessors();
			}else{
				threadCount = Integer.parseInt(args[2]);
			}
		}catch(ArrayIndexOutOfBoundsException e){
			threadCount = 1;
		}

		Map<String, String> map = loadDict(obfDictPath);
		Queue<String> q;

		if(threadCount > 1){
			q = allFileList1(obfFolderPath);
			start_mt(map, q, threadCount);
		}else if(threadCount == 1){
			q = allFileList0(obfFolderPath);
			start_st(map, q);
		}else{
			System.exit(1);
		}

		System.out.println("\nDone!");
		System.out.println("Time: " + (System.nanoTime() - start) + "ns");
	}

	public static Map<String, String> loadDict(String path){
		Map<String, String> map = new HashMap<>();
		System.out.println("Load Dictionary... ");
		try(BufferedReader bf = new BufferedReader(new FileReader(path))){
			String line;
			int count = 0;
			while((line = bf.readLine()) != null){
				String[] parts = line.split(",");
				if(parts.length >= 2){
					map.put(parts[0].trim(), parts[1].trim());
					count++;
				}
			System.out.print("Rows:"+count+"\r");
			System.out.flush();
			}
		System.out.println("\n\rDone");
		}catch(IOException e){
			e.printStackTrace();
		}
		return map;
	}

	public static String modify(String content, Map<String, String> dict){
		StringBuilder sb = new StringBuilder(content);
		for(Map.Entry<String, String> entry : dict.entrySet()){
			String key = entry.getKey();
			int index = sb.indexOf(key);
			while(index != -1){
				String value = entry.getValue();
				sb.replace(index, index + key.length(), value);
				index = sb.indexOf(key, index + value.length());
			}
		}
		return sb.toString();
	}

	public static Queue<String> allFileList1(String path){
		System.out.println("\nGet all File list");
		Queue<String> file = new ConcurrentLinkedDeque<>();
		Queue<String> folder = new ConcurrentLinkedDeque<>();

		folder.add(path);
		while(!folder.isEmpty()){
			File f = new File(folder.poll());
			for(File f_list : f.listFiles()){
				if(f_list.isFile()){
					file.add(f_list.getPath());
				}else{
					folder.add(f_list.getPath());
				}
			}
		}

		return file;
	}

	public static Queue<String> allFileList0(String path){
		System.out.println("\nGet all File list");
		Queue<String> file = new ArrayDeque<>();
		Queue<String> folder = new ArrayDeque<>();

		folder.add(path);
		while(!folder.isEmpty()){
			File f = new File(folder.poll());
			for(File f_list : f.listFiles()){
				if(f_list.isFile()){
					file.add(f_list.getPath());
				}else{
					folder.add(f_list.getPath());
				}
			}
		}

		return file;
	}

	public static void start_mt(Map<String, String> map, Queue<String> path, int thread)throws InterruptedException{
		// Queue must ConcurrentLinkedDeque
		ExecutorService executor = Executors.newFixedThreadPool(thread);
		for(String s : path){
			final String current_s = s;
			executor.submit(() -> {
				try {
 					System.out.println(Thread.currentThread().getName() + " -> Modify: " + new File(current_s).getName());
					String content = new String(Files.readAllBytes(Paths.get(current_s)));
					final String result = modify(content, map);
					try(BufferedWriter bf = new BufferedWriter(new FileWriter(current_s))){
                				bf.write(result);
					}
					}catch(IOException e){
						e.printStackTrace();
					}
				});
		}

		executor.shutdown();
		executor.awaitTermination(Long.MAX_VALUE, java.util.concurrent.TimeUnit.NANOSECONDS);
	}

	public static void start_st(Map<String, String> map, Queue<String> path)throws IOException{
		// Queue must ArrayDeque
		for(String current_s : path){
			System.out.println(Thread.currentThread().getName() + " ->Modify: " + new File(current_s).getName());
			String content = new String(Files.readAllBytes(Paths.get(current_s)));
			final String result = modify(content, map);
			try(BufferedWriter bf = new BufferedWriter(new FileWriter(current_s))){
				bf.write(result);
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
}
