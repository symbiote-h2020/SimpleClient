package hr.fer.symbiote.client;

import java.io.IOException;
import java.util.List;

import hr.fer.symbiote.client.model.CoreResponse;
import hr.fer.symbiote.client.model.Observation.Value;
import hr.fer.symbiote.client.model.SymbioteResource;

public class MainOpenIoT extends SymbioteClient {
	
	public static void main(String[] args) throws IOException {
	    new MainOpenIoT().run();
	}
	
	public void run() throws IOException {
		System.out.println("*** security");
		String guestToken = getGuestToken();
		String securityRequest = createSecurityRequest(guestToken);
		System.out.println("Security request: " + securityRequest);
		 
		System.out.println("*** search"); 
		CoreResponse<List<SymbioteResource>> searchResult = searchForResources(securityRequest, 
		        searchQueryBuilder().platformName("OpenIoT").build());
		System.out.println("searchResult: " + searchResult);
		
		// choose one resource
		SymbioteResource resource = searchResult.getBody().stream()
				.filter(r -> {
					return r.getResourceType().stream()
						.filter(type -> type.toLowerCase().contains("sensor"))
						.count() > 0; 
				})
				.findFirst()
				.get();
		
		System.out.println("Resource: " + resource);
		String resourceId = resource.getId();

		System.out.println("*** get url");
		String resourceURL = getResourceUrl(securityRequest, resourceId);
		System.out.println("URL: " + resourceURL);
				
		System.out.println("*** get data");
		Value value = getReadCurrentValue(securityRequest, resourceURL);
		System.out.println("Value for " + value.getObsProperty().getName() + ": " + value.getValue() + " " + value.getUom().getSymbol());
	}
}

