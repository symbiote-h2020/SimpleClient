package hr.fer.symbiote.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
import hr.fer.symbiote.client.model.Observation.Value;
import hr.fer.symbiote.client.model.SymbioteResource;

public class SymbioteClient {

    public static final boolean LOGGING = true;
    private String symbioteCoreUrl;

    protected SymbioteClient() {
        symbioteCoreUrl = "https://symbiote-open.man.poznan.pl/coreInterface/";
    }
    
    protected SymbioteClient(String symbioteCoreUrl) {
        this.symbioteCoreUrl = symbioteCoreUrl;
    }
    
    protected String getGuestToken() throws IOException {
    	URL url = new URL(symbioteCoreUrl + "aam/get_guest_token");
    	HttpURLConnection con = (HttpURLConnection) url.openConnection();
    	con.setRequestMethod("POST");
    	
    	// log request
    	if(LOGGING) {
    		for (Map.Entry<String, List<String>> entries : con.getRequestProperties().entrySet()) {    
    		    StringBuilder values = new StringBuilder();
    		    for (String value : entries.getValue()) {
    		        values.append(value).append(",");
    		    }
    		    System.out.println("Request: " + entries.getKey() + " - " +  values );
    		}
    	}
    	
    	con.connect();
    	
    	// log reposnse
    	if(LOGGING) {
    		for (Map.Entry<String, List<String>> entries : con.getHeaderFields().entrySet()) {
    		    StringBuilder values = new StringBuilder();
    		    for (String value : entries.getValue()) {
    		        values.append(value).append(",");
    		    }
    		    System.out.println("Response: " +  entries.getKey() + " - " +  values );
    		}
    	}
    	
    	return con.getHeaderField("x-auth-token");
    }

    protected static String createSecurityRequest(String guestToken) {
    	return "{"
    			+ "\"token\":\"" + guestToken + "\","
    			+ "\"authenticationChallenge\":\"\","
    			+ "\"clientCertificate\":\"\","
    			+ "\"clientCertificateSigningAAMCertificate\":\"\","
    			+ "\"foreignTokenIssuingAAMCertificate\":\"\""
    			+ "}";
    }

    protected SearchQueryBuilder searchQueryBuilder() {
        return new SearchQueryBuilder();
    }
    //https://github.com/symbiote-h2020/SymbioteCloud/wiki/3.2-Search-for-resources#321-searching-by-configurable-query
    protected CoreResponse<List<SymbioteResource>> searchForResources(String securityRequest, Map<String,String> query) throws IOException {
    	StringBuilder urlString = new StringBuilder(symbioteCoreUrl).append("query?");

    	urlString.append(query.entrySet().stream()
    	    .map(entry -> entry.getKey() + "=" + entry.getValue())
    	    .collect(Collectors.joining("&"))
    	);
    	
    	URL url = new URL(urlString.toString());
    	
    	if(LOGGING)
    	    System.out.println("Request URL: " + urlString);
    	   
    	HttpURLConnection con = (HttpURLConnection) url.openConnection();
    	con.setRequestMethod("GET");
    	setSecurityHeaders(securityRequest, con);
    	
    	con.connect();
    
    	Gson gson = new Gson();
    	Reader reader = readAndLog(con.getInputStream());
    	return gson.fromJson(reader, 
    			new TypeToken<CoreResponse<List<SymbioteResource>>>(){}.getType());
    }

    protected String getResourceUrl(String securityRequest, String id) throws IOException {
    	URL url = new URL(symbioteCoreUrl + "resourceUrls?id=" + id);
    	HttpURLConnection con = (HttpURLConnection) url.openConnection();
    	con.setRequestMethod("GET");
    	setSecurityHeaders(securityRequest, con);
    	
    	con.connect();
    
    	Gson gson = new Gson();
    	Reader reader = readAndLog(con.getInputStream());
    	CoreResponse<Map<String,String>> response = gson.fromJson(reader, new TypeToken<CoreResponse<Map<String,String>>>(){}.getType());
        
    	return response.getBody().get(id);
    }

    protected int actuate(String securityRequest, String resourceURL, String payload) throws IOException {
        if(LOGGING)
            System.out.println("Request payload: " + payload);
    	URL url = new URL(resourceURL);
    	HttpURLConnection con = (HttpURLConnection) url.openConnection();
    	con.setRequestMethod("PUT");
    	setSecurityHeaders(securityRequest, con);
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
    	con.connect();
    	
        OutputStream outputStream = con.getOutputStream();
        outputStream.write(payload.getBytes());
        outputStream.flush();
    
    	readAndLogString(con.getInputStream());
    	return con.getResponseCode();
    }

    protected static void setSecurityHeaders(String securityRequest, HttpURLConnection con) {
    	con.setRequestProperty("x-auth-timestamp", Long.toString(System.currentTimeMillis()));
    	con.setRequestProperty("x-auth-1", securityRequest);
    	con.setRequestProperty("x-auth-size", "1");
    }

    protected static Reader readAndLog(InputStream inputStream) throws IOException {
    	String result = readAndLogString(inputStream);
        
        return new StringReader(result);
    }

    protected static String readAndLogString(InputStream inputStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        String temp = null;
        StringBuilder sb = new StringBuilder();
        while((temp = in.readLine()) != null){
            sb.append(temp).append(" ");
        }
        String result = sb.toString();
        System.out.println("Body: " + result);
        return result;
    }

    protected static Value getReadCurrentValue(String securityRequest, String resourceURL) throws IOException {
        List<Value> values = getReadCurrentValue(securityRequest, resourceURL, 1);
        if(values.isEmpty())
            throw new RuntimeException("There is no current value");
        return values.get(0);
    }
    
    protected static List<Value> getReadCurrentValue(String securityRequest, String resourceURL, int top) throws IOException {
    	URL url = new URL(resourceURL + "/Observations?$top=" + top);
    	HttpURLConnection con = (HttpURLConnection) url.openConnection();
    	con.setRequestMethod("GET");
    	setSecurityHeaders(securityRequest, con);
    	con.setRequestProperty("Accept", "application/json");
    	
    	con.connect();
    
    	Gson gson = new Gson();
    	Reader reader = readAndLog(con.getInputStream());
    	List<Observation> response = gson.fromJson(reader, new TypeToken<List<Observation>>(){}.getType());
    
    	System.out.println("data received1");
    	return response.stream()
            .flatMap(o -> o.getObsValues().stream())
            .collect(Collectors.toList());
    }

    protected static String callService(String securityRequest, String resourceURL, String payload) throws IOException {
    	URL url = new URL(resourceURL);
    	HttpURLConnection con = (HttpURLConnection) url.openConnection();
    	con.setRequestMethod("PUT");
    	setSecurityHeaders(securityRequest, con);
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
    	con.connect();
    	
        OutputStream outputStream = con.getOutputStream();
        outputStream.write(payload.getBytes());
        outputStream.flush();
    
    	return readAndLogString(con.getInputStream());
    }

}
