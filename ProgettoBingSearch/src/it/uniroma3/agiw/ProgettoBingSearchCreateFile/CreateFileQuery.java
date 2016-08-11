package it.uniroma3.agiw.ProgettoBingSearchCreateFile;

import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONObject;

public class CreateFileQuery {
	
	private FileWriter listaQuery;

	public CreateFileQuery() throws IOException{
		this.listaQuery = new FileWriter("../ProgettoBingSearch/src/listaQuery.txt");
	}
	
	public void writeFileQuery(String query,JSONObject aQuery, JSONObject aResult) throws IOException {;
		this.listaQuery.append(query+"\t"+aQuery.get("uri")+"\t"+aResult.get("Url")+"\n");
	}

	public FileWriter getListaQuery() {
		return listaQuery;
	}

	public void setListaQuery(FileWriter listaQuery) {
		this.listaQuery = listaQuery;
	}

	public void close() throws IOException {
		this.listaQuery.close();
		
	}
}
