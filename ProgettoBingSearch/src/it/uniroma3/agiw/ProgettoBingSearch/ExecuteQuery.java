package it.uniroma3.agiw.ProgettoBingSearch;

import java.io.IOException;
import java.util.ArrayList;

/* In questa classe ci facciamo restituire all'inzio i primi 50 risultati dalle query (per ogni persona) 
 * e poi successivamente al secondo passaggio altri 50 risultati. Per un totale di 100 risultati a persona.
 */

public class ExecuteQuery {
	
	public void execute(SearchPage sp, ArrayList<String> people) throws IOException {
		
		for(String s:people){
			sp.executeQuery(s,1);   
			sp.executeQuery(s,2);	
		}
			
	
		sp.getFile().close();
		sp.getFile2().close();
   	}
}
