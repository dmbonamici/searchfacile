package it.uniroma3.agiw.ProgettoBingSearch;

import java.io.BufferedReader;


import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class URLtoHtml {

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("../ProgettoBingSearch/src/listaResults.txt"));
		String line;
		String s1;
		
		while((line = br.readLine()) != null)
		try{
			System.out.println(line);
			s1 = line.trim();

			Document doc = Jsoup.connect(s1).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36").timeout(Integer.MAX_VALUE-1).ignoreContentType(true).get();
			String title = doc.title();
		
				try( 
						PrintWriter out = new PrintWriter(title + ".html" )  ){
					out.println(doc.html());
				}
		
		}
		catch (NullPointerException e) {
			
	        e.printStackTrace();
	    } catch (HttpStatusException e) {
	    	
	        e.printStackTrace();
	    } catch (IOException e) {
	    
	        e.printStackTrace();
	    }
		in.close();	

	}

}
