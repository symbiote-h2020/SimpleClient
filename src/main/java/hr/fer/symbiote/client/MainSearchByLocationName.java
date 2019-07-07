package hr.fer.symbiote.client;

import java.io.IOException;
import java.util.List;

import hr.fer.symbiote.client.model.CoreResponse;
import hr.fer.symbiote.client.model.SymbioteResource;

public class MainSearchByLocationName extends SymbioteClient {
	
	public static void main(String[] args) throws IOException {
	    new MainSearchByLocationName().run();
	}
	
	public void run() throws IOException {
		System.out.println("*** security");
		String guestToken = getGuestToken();
		String securityRequest = createSecurityRequest(guestToken);
		System.out.println("Security request: " + securityRequest);
		
		System.out.println("*** search"); 
		CoreResponse<List<SymbioteResource>> searchResult = searchForResources(securityRequest, 
		        searchQueryBuilder().locationName("*Zagreb*").build());
		System.out.println("searchResult: " + searchResult);		
	}
}

