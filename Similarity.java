import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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

	
	public static HashMap <String,String> readfiles(ArrayList<String> listfile){
		HashMap <String,String> NameContent = new HashMap <>();
		for (String namefile:listfile) {
			String path= "./src/US_Inaugural_Addresses/US_Inaugural_Addresses/"+namefile;
			System.out.println(path);
			try {
				String content = new String(Files.readAllBytes(Paths.get(path)));
				NameContent.put(namefile, content);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return NameContent;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		ArrayList<String> listfile = new ArrayList<>();
		HashMap <String,String> NameContent = new HashMap <>();
		
		listfile=listFiles("./src/US_Inaugural_Addresses/US_Inaugural_Addresses");
		NameContent=readfiles(listfile);
	}

}
