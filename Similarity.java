import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Similarity {
	
// 1) Contexte
	
	public static ArrayList<String> listFiles(String path) {
		ArrayList<String> listfile = new ArrayList<>();
		File directory = new File(path);

		// get all the files in the directory
        File[] files = directory.listFiles();
        
        if (files!=null) {
        	for (File file:files) {
        		if (file.isFile()) {
        			listfile.add(file.getName());	
        		}
        	}
        	
        }
        //System.out.println(listfile);
		return listfile;
	}

	
	public static HashMap <String,String> readfiles(ArrayList<String> listfile){
		HashMap <String,String> NameContent = new HashMap <>();
		for (String namefile:listfile) {
			String path= "./src/US_Inaugural_Addresses/US_Inaugural_Addresses/"+namefile;
			try {
				String content = new String(Files.readAllBytes(Paths.get(path)));
				NameContent.put(namefile, content);
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
		}
		//System.out.println(NameContent.keySet());
		return NameContent;
	}
	
	private static HashMap<String, ArrayList<String>>tokenization(HashMap<String, String> nameContent) {
		
		HashMap<String, String> NameContent = new HashMap<>();
		NameContent=nameContent;
		
		// Initialisation
		
		HashMap<String, ArrayList<String>> NameContentTokenized = new HashMap<>();
		
		for (String namefile:NameContent.keySet()) {
			
			ArrayList<String> Tokens=new ArrayList<>();
			for (String word:NameContent.get(namefile).split("[\\s,:.()-;\\[\\]]+")) {
				word.toLowerCase();
				Tokens.add(word);				
			}
			NameContentTokenized.put(namefile, Tokens);

		}
		
		return NameContentTokenized;
		
	}
	
// 2) Réprésentation en « sac de mots » des documents
	
	private static HashMap<String,TreeMap<String,Integer>> similarity(HashMap<String, ArrayList<String>> NameContentTokenized,int n) {
		
		HashMap <String,TreeMap<String,Integer>> AllfilesSimilarity = new HashMap<>();
		
		
		for (String namefile_first: NameContentTokenized.keySet()) {
			
			HashMap <String,Integer> NombreCommun = new HashMap<>();
			
			for (String namefile_second:NameContentTokenized.keySet()) {
				
				if (namefile_first != namefile_second) {
				
					HashSet<String> firstwords = new HashSet<String>(NameContentTokenized.get(namefile_first));
					HashSet<String> secondwords = new HashSet<String>(NameContentTokenized.get(namefile_second));
					int n1 = firstwords.size();
					firstwords.removeAll(secondwords);
					NombreCommun.putIfAbsent(namefile_second, n1-firstwords.size());
				}
			}			
			
			AllfilesSimilarity.put(namefile_first,sorting(NombreCommun,n));
			//System.out.println(AllfilesSimilarity);
			
		}
		return AllfilesSimilarity;
	}
	
	private static HashMap<String,Integer> sorting(HashMap<String,Integer> NombreCommun,int n) {
		
		HashMap <String,Integer> SortedNombreCommun = new HashMap<>();
		for (int i = 1 ; i < n ; i++) {
			for ()
		}
		return SortedNombreCommun;
		
	}
	
	
	
	public static void main(String[] args) {
		
		
		ArrayList<String> listfile = new ArrayList<>();
		HashMap <String,String> NameContent = new HashMap <>();
		
		listfile=listFiles("./src/US_Inaugural_Addresses/US_Inaugural_Addresses");
		NameContent=readfiles(listfile);
		
		HashMap<String, ArrayList<String>> NameContentTokenized = new HashMap<>();
		NameContentTokenized= tokenization(NameContent);
		
		HashMap<String, TreeMap<String, Integer>> dic = similarity(NameContentTokenized);
		System.out.println(dic);
		//System.out.println(NameContentTokenized.get("57_obama_2013.txt"));
		
		
	}


	

}
