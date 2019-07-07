package hr.fer.symbiote.client;

import java.io.IOException;
import java.util.List;

import hr.fer.symbiote.client.model.CoreResponse;
import hr.fer.symbiote.client.model.SymbioteResource;

public class CallTramCheckService extends SymbioteClient {
	
	public static void main(String[] args) throws IOException {
	    new CallTramCheckService().run();
	}
	
	public void run() throws IOException {
		System.out.println("*** security");
		String guestToken = getGuestToken();
		String securityRequest = createSecurityRequest(guestToken);
		System.out.println("Security request: " + securityRequest);
		
		System.out.println("*** search"); 
		CoreResponse<List<SymbioteResource>> searchResult = searchForResources(securityRequest, 
		         searchQueryBuilder().platformName("TramCheck").name("TramCheckService").build());

		System.out.println("searchResult: " + searchResult);
		
		// choose one resource
		searchResult.getBody().stream()
			.findFirst()
			.ifPresentOrElse((SymbioteResource resource) -> Util.wrapException(() -> {
			    System.out.println("Resource: " + resource);
			    String resourceId = resource.getId();
			    
			    System.out.println("*** get url");
			    String resourceURL = getResourceUrl(securityRequest, resourceId);
			    System.out.println("URL: " + resourceURL);
			    
			    System.out.println("*** call service");
			    String result = callService(securityRequest, resourceURL, "[ { \"stopId\": \"166\" } ]");
			    System.out.println("data received");
			    System.out.println(result);
			}), 
			() -> System.out.println("No resources found in search"));
	}
}

