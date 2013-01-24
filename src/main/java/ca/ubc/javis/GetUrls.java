package ca.ubc.javis;

import java.util.Scanner;
import java.io.*;

public class GetUrls {

	public static String[] getArray(){
		Scanner x = null;
		try {
			x = new Scanner(new File("C:\\Users\\Jana\\Desktop\\AlexaURLs1.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String [] array=new String[416];
		int i=0;
		while(x.hasNext()){
			array[i]=x.next();
			i++;
		}
		return array;
	}
	
	public static void main(String[] args) {
		String[] urlArray= new String[418];
		urlArray=GetUrls.getArray();
		for(int j=0;j<urlArray.length;j++)
			System.out.println((j+1)+": "+urlArray[j]);

	}

}
