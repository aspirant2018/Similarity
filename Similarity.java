import java.util.*;
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
		return NameContent;
	}
	
	private static HashMap<String, ArrayList<String>>tokenization(HashMap<String, String> nameContent) {
				
		HashMap<String, ArrayList<String>> NameContentTokenized = new HashMap<>();
		
		for (String namefile:nameContent.keySet()) {
			
			ArrayList<String> Tokens=new ArrayList<>();
			for (String word:nameContent.get(namefile).split("[\\s,\\'?\":.!()-;\\[\\]]+")) {
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
			// trier la liste des mots en commun 
			HashMap<String, Integer> SortedNombreCommun = Sorting(NombreCommun);
			// les  documents les plus similaires
			HashMap<String, Integer> LesPlusSimilaire = PlusSimilaire(SortedNombreCommun,n);
			AllfilesSimilarity.put(namefile_first,Sorting(LesPlusSimilaire));
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
	
	
	public static HashMap<String,Integer> TermFrequency (ArrayList<String> MotsDeDoc ) {
		HashMap<String, Integer> tf = new HashMap<>();

	    // calculer le nombre d'occurence de chaque mot dans un seul document
	    for (String Mot : MotsDeDoc) {
	        if (tf.containsKey(Mot)) {
	            tf.put(Mot, tf.get(Mot) + 1);
	        } else {
	            tf.put(Mot, 1);
	        }
	    }
	    return tf;
	}
	
	public static HashMap<String,Double> DocumentFrequency (HashMap<String, ArrayList<String>>NameContentTokenized){
		HashMap<String, Double> df = new HashMap<>();
		
		for (String filename:NameContentTokenized.keySet()) {
			HashSet<String> listMot = new HashSet<>(NameContentTokenized.get(filename));
			for (String mot:listMot){
				if (df.containsKey(mot)){
					df.put(mot, df.get(mot) + 1);
				}
				else {
					df.put(mot, (double) 1);
				}
			}
		}
		for (String Mot: df.keySet()) {
			double ratio = (double) 58 / df.getOrDefault(Mot, (double) 0);
			df.put(Mot, ratio);
		}		
		return df;
	}
	
	public static void tfidf (HashMap<String, ArrayList<String>>NameContentTokenized, List<String> mots) {
		
		HashMap<String, Integer> tf = new HashMap<>();
		
		HashMap<String,Double> df = DocumentFrequency(NameContentTokenized);
		
		for (String mot: mots) {
			for (String filename:NameContentTokenized.keySet()) {
				tf = TermFrequency(NameContentTokenized.get(filename));
				
					if (tf.containsKey(mot)){
						
						double tfscore=tf.get(mot)/ (double) tf.size();
						double idfscore=Math.log10(df.get(mot));
						double score = (tfscore * idfscore) ;
						System.out.println(filename+" : "+score+"  ===>"+ tfscore +"*"+idfscore);
					}
				}
			}		
	}
	
	public static void MotsImportant(HashMap<String, ArrayList<String>>docs) {
		
		TreeMap <String,ArrayList<String>> TousMotsImportant = new TreeMap<>();
		HashMap<String, Integer> tf = new HashMap<>();
		HashMap<String,Double> df = DocumentFrequency(docs);

		// 3 instructions pour enlever les mots repetée 
		ArrayList<String> touslesmots = docs.get("58_trump_2017.txt");	
		HashSet<String> touslesmotsSet= new HashSet<String>(touslesmots);
		ArrayList<String> AllWords = new ArrayList<String>(touslesmotsSet);
		
		
		for (String mot: AllWords) {
			
			tf = TermFrequency(docs.get("58_trump_2017.txt"));
				
				if (tf.containsKey(mot)){
						
					double tfscore=tf.get(mot)/ (double) tf.size();
					double idfscore=Math.log10(df.get(mot));
					double score = (tfscore * idfscore)*100 ;
					System.out.println(mot+" "+score);
					

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
		
		List<String> mots = Arrays.asList("and", "government", "people", "obama", "war", "honor", "foreign", "men", "women", "children");
		
		//tfidf(NameContentTokenized,mots);
		
		//System.out.println(DocumentFrequency(NameContentTokenized));
		//MotsImportant(NameContentTokenized);
		MotsCommun(NameContentTokenized);
	}


	

}
