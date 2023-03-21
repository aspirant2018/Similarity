import java.util.*;
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
			for (String word:NameContent.get(namefile).split("[\\s,\\'?\":.!()-;\\[\\]]+")) {
				Tokens.add(word.toLowerCase());				
			}
			NameContentTokenized.put(namefile, Tokens);

		}
		
		return NameContentTokenized;
		
	}
	
// 2) Réprésentation en « sac de mots » des documents
	
	private static HashMap<String,HashMap<String,Integer>> similarity(HashMap<String, ArrayList<String>> NameContentTokenized,int n) {
		
		HashMap <String,HashMap<String,Integer>> AllfilesSimilarity = new HashMap<>();
		
		
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
			// sorting a HashMap
			HashMap<String, Integer> SortedNombreCommun = Sorting(NombreCommun);
			// les n documents les plus similaires
			HashMap<String, Integer> LesPlusSimilaire = PlusSimilaire(SortedNombreCommun,n);
			AllfilesSimilarity.put(namefile_first,Sorting(LesPlusSimilaire));
			//System.out.println(AllfilesSimilarity);
			
		}
		return AllfilesSimilarity;
	}
	
	private static  void MotsCommun (HashMap<String, ArrayList<String>>NameContentTokenized) {
		
		HashSet<String> discourAncien= new HashSet<>(NameContentTokenized.get("01_washington_1789.txt"));
		HashSet<String> discourRecent= new HashSet<>(NameContentTokenized.get("58_trump_2017.txt"));
		 // Find common words
	    discourAncien.retainAll(discourRecent);

	    // Print common words
	    for (String word : discourAncien) {
	        System.out.println(word);
	    }
		
	}
	// alot of function words 
	
	
	private static HashMap<String,Integer> PlusSimilaire(HashMap<String, Integer> sortedNombreCommun, int n) {
	// TODO Auto-generated method stub
		HashMap<String, Integer> result = new HashMap<>();
	    int count = 0;
	    for (Map.Entry<String, Integer> entry : sortedNombreCommun.entrySet()) {
	        if (count == n) {
	            break;
	        }
	        result.put(entry.getKey(), entry.getValue());
	        count++;
	    }
	    return result;
}


	// sorting function HashMap
	
	private static HashMap<String, Integer> Sorting(HashMap<String, Integer> map) {
		
	    List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
	    list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
	    HashMap<String, Integer> sortedMap = new LinkedHashMap<>();
	    
	    for (Map.Entry<String, Integer> entry : list) {
	        sortedMap.put(entry.getKey(), entry.getValue());
	    }
	    return sortedMap;
	}
	
	
	
	
// 3)  Représentation tf.idf
	
	
	public static HashMap<String,Double> TermFrequency (ArrayList<String> MotsDeDoc ) {
		HashMap<String, Double> tf = new HashMap<>();

	    // calculer le nombre d'occurence de chaque mot dans un seul document
	    for (String Mot : MotsDeDoc) {
	        if (tf.containsKey(Mot)) {
	            tf.put(Mot, tf.get(Mot) + 1);
	        } else {
	            tf.put(Mot, (double) 1);
	        }
	    }
	    for (String Mot:tf.keySet()) {
	    	tf.put(Mot,tf.get(Mot)/tf.size());
	    }
	    //System.out.println(tf.size());
	    
	    return tf;
	}
	
	public static HashMap<String,Double> InverseDocumentFrequency (HashMap<String, ArrayList<String>>NameContentTokenized){
		HashMap<String, Double> idf = new HashMap<>();
		
		for (String filename:NameContentTokenized.keySet()) {
			HashSet<String> listMot = new HashSet<>(NameContentTokenized.get(filename));
			for (String mot:listMot){
				if (idf.containsKey(mot)){
					idf.put(mot, idf.get(mot) + 1);
				}
				else {
					idf.put(mot, (double) 1);
				}
			}
		}
		
		for (String Mot: idf.keySet()) {
			double ratio = Math.log10(58/idf.get(Mot))+1;
			idf.put(Mot, ratio);
		}		
		
		return idf;
	}
	
	public static void itidf (HashMap<String, ArrayList<String>>NameContentTokenized, String mot) {
		
		HashMap<String, Double> tf = new HashMap<>();
		HashMap<String,Double> idf = InverseDocumentFrequency(NameContentTokenized);
		for (String filename:NameContentTokenized.keySet()) {
			tf = TermFrequency(NameContentTokenized.get(filename));
			if (tf.containsKey(mot)){
				
				double score = (tf.get(mot))*idf.get(mot);
				System.out.println(filename+score);
			}
			
		}
		
		
	}
	
	// fonction principale
	public static void main(String[] args) {
		
		
		ArrayList<String> listfile = new ArrayList<>();
		HashMap <String,String> NameContent = new HashMap <>();
		
		listfile=listFiles("./src/US_Inaugural_Addresses/US_Inaugural_Addresses");
		NameContent=readfiles(listfile);
		
		HashMap<String, ArrayList<String>> NameContentTokenized = new HashMap<>();
		NameContentTokenized= tokenization(NameContent);
		
		HashMap<String, HashMap<String, Integer>> dic = similarity(NameContentTokenized, 5);
		
		//MotsCommun(NameContentTokenized);
		List<String> words = Arrays.asList("government", "borders", "people", "obama", "war", "honor", "foreign", "men", "women", "children");
		for (String word:words) {
			System.out.println("the word: "+word);
			itidf(NameContentTokenized,word);
		}
		
		
		
	}


	

}
