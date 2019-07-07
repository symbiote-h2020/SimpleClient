package hr.fer.symbiote.client;

import java.io.IOException;
import java.util.List;

import hr.fer.symbiote.client.model.CoreResponse;
import hr.fer.symbiote.client.model.SymbioteResource;

public class OnOffActuate extends SymbioteClient {
    public static void main(String[] args) throws IOException {
        new OnOffActuate().run();
    }

	private void run() throws IOException {
        System.out.println("*** security");
        String guestToken = getGuestToken();
        String securityRequest = createSecurityRequest(guestToken);
        System.out.println("Security request: " + securityRequest);
        
        System.out.println("*** search");
        SearchQueryBuilder builder = searchQueryBuilder()
                .resourceType("Actuator")
                .description("switch");

        CoreResponse<List<SymbioteResource>> searchResult = searchForResources(securityRequest, 
                builder.platformName("OpenHAB-FER").build());
//                builder.platformName("IoT_Hackaton").build());
//                builder.platformName("MK_Platform").build());
//                builder.build()); // all actuators
        System.out.println("searchResult: " + searchResult);
        
        // choose resource with name
        searchResult.getBody().stream()
                //.filter(res -> res.getDescription().equals("This is door lock"))
                .findFirst()
                .ifPresentOrElse((SymbioteResource resource) -> Util.wrapException( () -> {
                    System.out.println("Resource: " + resource);
                    String resourceId = resource.getId();

                    System.out.println("*** get url");
                    String resourceURL = getResourceUrl(securityRequest, resourceId);
                    System.out.println("URL: " + resourceURL);
                            
                    System.out.println("*** actuate ON");
                    boolean state = false; // actuator state
                    int status = actuate(securityRequest, resourceURL, createOnOffCapabilityContent(state));
                    System.out.println(status);
                }), () -> {
                    System.out.println("There is no resource with specified filter in result of search");
                });
    }

    private String createOnOffCapabilityContent(boolean state) {
        return "{" + 
		        " \"OnOffCapabililty\" : [" + 
		        "    {" + 
		        "      \"on\" : " + state + 
		        "    }" + 
		        "  ]" + 
		        "}";
    }    
}

