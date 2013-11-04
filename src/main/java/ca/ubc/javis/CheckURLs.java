package ca.ubc.javis;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.*;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class CheckURLs {

	public static ArrayList<String> checkDuplicity(String path, int length){
		
		boolean duplicateExists;
		String[] array = new String[8];
		ArrayList<String> arrayList = new ArrayList<>();
		
		array = GetUrls.getArray(path, length);
		for (int i = 0; i < array.length  ; i++){
			if(!array[i].endsWith("/"))
				array[i] = array[i].concat("/");
		}
		for( int j = 0; j < array.length; j++){
			String checkThisURL = array[j];
			duplicateExists = false;
			for (int n = j + 1 ; n < array.length; n++){
				if(checkThisURL.equals(array[n])){
					duplicateExists = true;
				}
				continue;
			}
			if(!duplicateExists)
				arrayList.add(array[j]);
		}
		return arrayList;
	}
	
	// Removes duplicates from two different files, by comparing the URLs and removing the replicate in the second file.
	
	public static ArrayList<String> compareListsForDuplication(ArrayList<String> list1, ArrayList<String> list2) throws URISyntaxException, MalformedURLException, IOException{
		
		ArrayList<String> result = new ArrayList<>();             
		boolean duplicateExists;
		
		for (String str : list2){
			duplicateExists = false;
		  	for(int j = 0 ; j < list1.size(); j ++){
		   		if(list1.get(j).equals(str)){
		   			duplicateExists = true;
		   			break;
		   		}	
		   	}
		   	if(!duplicateExists)
		   		result.add(str);
		}
		return result;
	}
	
	public static void finalizeResult(ArrayList<String> list) throws IOException{
		StringBuilder buffer = new StringBuilder();
		for(int i=0;i<list.size();i++){
			buffer.append(list.get(i)+"\n");
		}
		Files.write(buffer.toString(), new File("/ubc/ece/home/am/grads/janab/finalRandomSites.txt"), Charsets.UTF_8);
	}
	
	public static void main(String[] args) throws MalformedURLException, URISyntaxException, IOException {
		ArrayList<String> str1 = CheckURLs.checkDuplicity("src/main/resources/500URL.txt", 500); 
		ArrayList<String> str2 = CheckURLs.checkDuplicity("/ubc/ece/home/am/grads/janab/sites.txt", 500);
		ArrayList<String> result = compareListsForDuplication(str1,str2);
		
		CheckURLs.finalizeResult(result);
			
	}

}
