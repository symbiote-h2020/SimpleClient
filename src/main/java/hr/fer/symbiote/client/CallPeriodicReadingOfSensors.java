package hr.fer.symbiote.client;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import hr.fer.symbiote.client.model.CoreResponse;
import hr.fer.symbiote.client.model.Observation.Value;
import hr.fer.symbiote.client.model.SymbioteResource;

public class CallPeriodicReadingOfSensors extends SymbioteClient {
	
	public static void main(String[] args) throws IOException {
	    new CallPeriodicReadingOfSensors().run();
	}
	
	public void run() throws IOException {
	    while (true) {
    		System.out.println("*** security");
    		String guestToken = getGuestToken();
    		
    		String securityRequest = createSecurityRequest(guestToken);
    		System.out.println("Security request: " + securityRequest);
    		
    		System.out.println("*** search"); 
    		CoreResponse<List<SymbioteResource>> searchResult = searchForResources(securityRequest, 
    		        searchQueryBuilder().platformName("IoT_Hackaton").build());
    		System.out.println("searchResult: " + searchResult);
    		
    		Set<String> resourceNames = new HashSet<>();
    		resourceNames.add("Grada Vukovara");
    		resourceNames.add("Andrije Hebranga");
    		
    		// choose one resource
    		List<SymbioteResource> resources = searchResult.getBody().stream()
    				.filter(r -> {
    					return r.getResourceType().stream()
    						.filter(type -> type.toLowerCase().contains("sensor"))
    						.count() > 0; 
    				})
    				.filter(r -> resourceNames.contains(r.getName()))
    				.collect(Collectors.toList());
    		
    		System.out.println("Resources: " + resources);
    		
    		resources.stream()
    		    .forEach(resource -> {
    		        try {
                		String resourceId = resource.getId();
                
                		System.out.println("*** get url");
                		String resourceURL;
                        resourceURL = getResourceUrl(securityRequest, resourceId);
                		System.out.println("URL: " + resourceURL);
                				
                		System.out.println("*** get data");
                		List<Value> values = getReadCurrentValue(securityRequest, resourceURL, 2);
                		System.out.println("data received");
                		values.stream()
                		    .map(v -> v.getObsProperty().getName() + " = " + v.getValue() + v.getUom().getSymbol())
                		    .forEach(System.out::println);
    		        } catch (IOException e) {
    		            e.printStackTrace();
    		        }
		    });
    		
    		final long waitTime = 1000L*60*15;
    		System.out.println(LocalDateTime.now() + " Waiting for " + waitTime + "ms ...");
    		try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
	    }
	}
}

