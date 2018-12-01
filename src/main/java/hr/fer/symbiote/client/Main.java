package hr.fer.symbiote.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import hr.fer.symbiote.client.model.CoreResponse;
import hr.fer.symbiote.client.model.Observation;
import hr.fer.symbiote.client.model.SymbioteResource;

public class Main {
	public static final String BASE_URL = "https://symbiote-open.man.poznan.pl/coreInterface/";
	
	public static void main(String[] args) throws IOException {
		System.out.println("*** security");
		String guestToken = getGuestToken();
		String securityRequest = createSecurityRequest(guestToken);
		System.out.println("Security request: " + securityRequest);
		
		System.out.println("*** search"); 
		CoreResponse<List<SymbioteResource>> searchResult = searchForResources(securityRequest, "fer1");
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
		String value = getReadCurrentValue(securityRequest, resourceURL);
		System.out.println("Value: " + value);
	}

	// https://github.com/symbiote-h2020/SymbioteCloud/wiki/3.1-Security#311-getting-security-headers-for-guest-users
	private static String getGuestToken() throws IOException {
		URL url = new URL(BASE_URL + "aam/get_guest_token");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		
		// log request
//		for (Map.Entry<String, List<String>> entries : con.getRequestProperties().entrySet()) {    
//		    String values = "";
//		    for (String value : entries.getValue()) {
//		        values += value + ",";
//		    }
//		    System.out.println("Request: " + entries.getKey() + " - " +  values );
//		}
		
		con.connect();
		
		// log reposnse
//		for (Map.Entry<String, List<String>> entries : con.getHeaderFields().entrySet()) {
//		    String values = "";
//		    for (String value : entries.getValue()) {
//		        values += value + ",";
//		    }
//		    System.out.println("Response: " +  entries.getKey() + " - " +  values );
//		}
		
		String token = con.getHeaderField("x-auth-token");
		
		return token;
	}

	// https://github.com/symbiote-h2020/SymbioteCloud/wiki/3.1-Security#3112-create-security-request
	private static String createSecurityRequest(String guestToken) {
		return "{"
				+ "\"token\":\"" + guestToken + "\","
				+ "\"authenticationChallenge\":\"\","
				+ "\"clientCertificate\":\"\","
				+ "\"clientCertificateSigningAAMCertificate\":\"\","
				+ "\"foreignTokenIssuingAAMCertificate\":\"\""
				+ "}";
	}
	
	// https://github.com/symbiote-h2020/SymbioteCloud/wiki/3.2-Search-for-resources
	private static CoreResponse<List<SymbioteResource>> searchForResources(String securityRequest, String platform) throws IOException {
		URL url = new URL(BASE_URL + "query?platform_id=" + platform);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		setSecurityHeaders(securityRequest, con);
		
		con.connect();

		Gson gson = new Gson();
		Reader reader = readAndLog(con.getInputStream());
		CoreResponse<List<SymbioteResource>> response = gson.fromJson(reader, 
				new TypeToken<CoreResponse<List<SymbioteResource>>>(){}.getType());
        
		return response;
	}
	
	// https://github.com/symbiote-h2020/SymbioteCloud/wiki/3.3-Obtaining-resource-access-URL
	private static String getResourceUrl(String securityRequest, String id) throws IOException {
		URL url = new URL(BASE_URL + "resourceUrls?id=" + id);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		setSecurityHeaders(securityRequest, con);
		
		con.connect();

		Gson gson = new Gson();
		Reader reader = readAndLog(con.getInputStream());
		CoreResponse<Map<String,String>> response = gson.fromJson(reader, new TypeToken<CoreResponse<Map<String,String>>>(){}.getType());
        
		return response.getBody().get(id);
	}

	// https://github.com/symbiote-h2020/SymbioteCloud/wiki/3.4-Accessing-the-resource-and-actuating-and-invoking-service-for-default-(dummy)-resources#341-reading-current-value
	private static String getReadCurrentValue(String securityRequest, String resourceURL) throws IOException {
		URL url = new URL(resourceURL + "/Observations?$top=1");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		setSecurityHeaders(securityRequest, con);
		con.setRequestProperty("Accept", "application/json");
		
		con.connect();

		Gson gson = new Gson();
		Reader reader = readAndLog(con.getInputStream());
		List<Observation> response = gson.fromJson(reader, new TypeToken<List<Observation>>(){}.getType());
        
		Observation observation = response.get(0);
		String result = observation.getObsValues().stream()
			.map(o -> o.getObsProperty().getName() + ": " + o.getValue() + " " + o.getUom().getSymbol())
			.collect(Collectors.joining(", "));
		
		return result;
	}
	
	private static void setSecurityHeaders(String securityRequest, HttpURLConnection con) {
		con.setRequestProperty("x-auth-timestamp", Long.toString(System.currentTimeMillis()));
		con.setRequestProperty("x-auth-1", securityRequest);
		con.setRequestProperty("x-auth-size", "1");
	}

	private static Reader readAndLog(InputStream inputStream) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        String temp = null;
        StringBuilder sb = new StringBuilder();
        while((temp = in.readLine()) != null){
            sb.append(temp).append(" ");
        }
        String result = sb.toString();
        System.out.println("Body: " + result);
        
        return new StringReader(result);
	}
}

