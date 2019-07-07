package hr.fer.symbiote.client;

import java.io.IOException;
import java.util.List;

import hr.fer.symbiote.client.model.CoreResponse;
import hr.fer.symbiote.client.model.Observation.Value;
import hr.fer.symbiote.client.model.SymbioteResource;

public class MainGetSensorData extends SymbioteClient {
	
	public static void main(String[] args) throws IOException {
	    new MainGetSensorData().run();
	}
	
	public void run() throws IOException {
		System.out.println("*** security");
		String guestToken = getGuestToken();
		String securityRequest = createSecurityRequest(guestToken);
		System.out.println("Security request: " + securityRequest);
		
		System.out.println("*** search"); 
		CoreResponse<List<SymbioteResource>> searchResult = searchForResources(securityRequest, 
		        searchQueryBuilder().platformName("OpenIoT").name("FER33TWL7672").build());
        		//searchQueryBuilder().platformName("GDi_Ensemble_IoT_Platform").name("Sensor09").build());
        		//searchQueryBuilder().platformName("AITopenUwedat").name("ZAGREB-1").build());
        
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
		int noOfReadings = 2;
		List<Value> values = getReadCurrentValue(securityRequest, resourceURL, noOfReadings);
		System.out.println("data received");
		values.stream()
		    .map(v -> v.getObsProperty().getName() + " = " + v.getValue() + v.getUom().getSymbol())
		    .forEach(System.out::println);
	}
}

