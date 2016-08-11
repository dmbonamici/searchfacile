package it.uniroma3.agiw.ProgettoBingSearch;

import java.io.IOException;
import java.util.ArrayList;

public class BingSearchEngine{
    
    public static void main( String[] args ) throws IOException{
    	SearchPage sp = new SearchPage();
    	ListGenerator gl = new ListGenerator();
    	ExecuteQuery eq = new ExecuteQuery();    
    	
    	// Prendiamo la lista delle persone di cui andremo a fare le query
    	
    	ArrayList<String> people = gl.getList();
    	eq.execute(sp,people);
    	URLtoHtml.main(args);
    
    }
      
}

