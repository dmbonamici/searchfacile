package it.uniroma3.agiw.progettohtmlindexer.indexer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

//classe per identificare pagine html malformate, conteneti campi null
public class SearchingHTML {
	static Document doc;
	public static void main(String[] args) throws IOException {
		//modificare path
		Files.walk(Paths.get("/Users/federico/Documents/AGIW/workspace/ProgettoBingSearch/Output")).forEach(filePath -> {
			if (Files.isRegularFile(filePath)) {
				File input = new File(filePath.toString());
				try {
					doc = Jsoup.parse(input, "UTF-8", "http://example.com/");


					Elements title = doc.getElementsByTag("title");
					String dirtyt = title.toString();
					String clean = Jsoup.clean(dirtyt, Whitelist.none());

					Elements body = doc.getElementsByTag("body");
					String dirtyb = body.toString();
					String cleanb = Jsoup.clean(dirtyb, Whitelist.none());





					if(clean.equals(null) || clean.equals("") || cleanb.equals(null)|| cleanb.equals("")){
						System.out.println(filePath.toString());
						Files.delete(filePath);
						System.out.println("male");

					}


				}catch(Exception e){
					e.printStackTrace();
				}

			}
		});


	}

}

