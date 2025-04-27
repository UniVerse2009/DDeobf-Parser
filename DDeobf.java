import java.lang.ArrayIndexOutOfBoundsException;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.io.File;

import java.io.FileReader;
import java.io.FileWriter;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class DDeobf{
	public static void main(String[] args)throws IOException{
		String obfFolderPath =  "";
		String obfDictPath = "";

		System.out.println("==========Parser App==========");

		try{
			obfFolderPath = args[0];
			obfDictPath = args[1];
		}catch(ArrayIndexOutOfBoundsException e){
			System.out.println("Usage: java -jar Parser <obfFolderPath> <obfDictPath>");
			System.exit(1);
		}

		Map<String, String> map = loadDict(obfDictPath);
		ArrayList<String> fileLists = allFileList(obfFolderPath);

		int count = 1;
		System.out.println("Start to modify file...\n");
		for(String str : fileLists){
			System.out.print("\rModify: " + str + " (" + count + "/" + fileLists.size() + ")");
			System.out.flush();
			String content = new String(Files.readAllBytes(Paths.get(str)));
			String result = modify(content, map);
			try(BufferedWriter bf = new BufferedWriter(new FileWriter(str))){
				bf.write(result);
			}catch(IOException e){
				e.printStackTrace();
			}
			count++;
		}

		System.out.println("\nDone!");
	}

	public static Map<String, String> loadDict(String path){
		Map<String, String> map = new HashMap<>();
		System.out.println("Load Dictonary... \n");
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
		String cache = content;
		for(Map.Entry<String, String> entry : dict.entrySet()){
			cache = cache.replaceAll("\\b" + entry.getKey() + "\\b", entry.getValue());
		}
		return cache;
	}

	public static ArrayList<String> allFileList(String path){
		System.out.println("Get all File list");
		ArrayList<String> allFilePath = new ArrayList<>();
		ArrayList<String> allDirPath = new ArrayList<>();

		allDirPath.add(path);
		while(!allDirPath.isEmpty()){
			String target = allDirPath.get(allDirPath.size() - 1);
			File file = new File(target);
			for(File f : file.listFiles()){
				String name = new File(target, f.getName()).getPath();
				if(f.isFile()){
					allFilePath.add(name);
				}else{
					allDirPath.add(0, new File(name).getPath());
				}
			}
		allDirPath.remove(allDirPath.size() - 1);
		}
		System.out.println("\nGet: " + allFilePath.size() + " file");
		return allFilePath;
	}
}
