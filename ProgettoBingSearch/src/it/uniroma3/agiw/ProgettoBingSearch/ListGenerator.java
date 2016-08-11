package it.uniroma3.agiw.ProgettoBingSearch;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;;

public class ListGenerator {
	private ArrayList<String> list;
	
	public ListGenerator(){
		this.list = new ArrayList<String>();
	}
	
	public void createList() throws IOException{
    	String currentLine;
    	BufferedReader br = new BufferedReader(new FileReader("../ProgettoBingSearch/src/avv.txt"));
    	while ((currentLine = br.readLine()) != null) {
    		this.lista.add(currentLine);
    	}
	}
	
	public ArrayList<String> getList() throws IOException {
		this.createList();
		return this.list;
	}
}

