package ca.ubc.javis;

import java.util.Scanner;
import java.io.*;

public class GetUrls {

	public static String[] getArray(String path,int size){
		Scanner x = null;
		try {
			x = new Scanner(new File(path));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String [] array=new String[size];
		int i=0;
		while(x.hasNext()){
			array[i]=x.next();
			i++;
		}
		return array;
	}

}
