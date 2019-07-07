package hr.fer.symbiote.client;

import java.io.IOException;
import java.util.List;

import hr.fer.symbiote.client.model.CoreResponse;
import hr.fer.symbiote.client.model.SymbioteResource;

public class FindServices extends SymbioteClient {
	
	public static void main(String[] args) throws IOException {
	    new FindServices().run();
	}
	
	public void run() throws IOException {
		System.out.println("*** security");
		String guestToken = getGuestToken();
		String securityRequest = createSecurityRequest(guestToken);
		System.out.println("Security request: " + securityRequest);
		
		System.out.println("*** search"); 
		CoreResponse<List<SymbioteResource>> searchResult = searchForResources(securityRequest, 
		        searchQueryBuilder().resourceType("Service").platformName("IoT_Hackaton").build());
		        //searchQueryBuilder().resourceType("Service").platformName("MK_Platform").build());
        		//searchQueryBuilder().resourceType("Service").build()); // all services
		System.out.println("searchResult: " + searchResult);		
	}

	// https://github.com/symbiote-h2020/SymbioteCloud/wiki/3.2-Search-for-resources
//	private static CoreResponse<List<SymbioteResource>> searchForResources(String securityRequest, String platformName) throws IOException {
//		URL url;
//		if(platformName != null)
//		    url = new URL(BASE_URL + "query?resource_type=Service&platform_name=" + platformName);
//		else
//		    url = new URL(BASE_URL + "query?resource_type=Service");
//		    
//		HttpURLConnection con = (HttpURLConnection) url.openConnection();
//		con.setRequestMethod("GET");
//		setSecurityHeaders(securityRequest, con);
//		
//		con.connect();
//
//		Gson gson = new Gson();
//		Reader reader = readAndLog(con.getInputStream());
//		CoreResponse<List<SymbioteResource>> response = gson.fromJson(reader, 
//				new TypeToken<CoreResponse<List<SymbioteResource>>>(){}.getType());
//        
//		return response;
//	}	
}

