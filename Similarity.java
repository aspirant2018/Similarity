import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;

public class Similarity {
	
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
		return listfile;
	}

	
	public static HashMap <String,String> readfiles(){
		ArrayList<String> listfile = new ArrayList<>();
		listfile=listFiles("./src/US_Inaugural_Addresses/US_Inaugural_Addresses");
		for (String file:listfile) {
			String path= "./src/US_Inaugural_Addresses/US_Inaugural_Addresses"+file;
			
		}
		
		return null;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		readfiles();
		

	}

}
