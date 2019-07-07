package hr.fer.symbiote.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SearchQueryBuilder {
    private String platformId;                  //    platform_id:           String
    private String platformName;                //    platform_name:         String
    private String owner;                       //    owner:                 String
    private String name;                        //    name:                  String
    private String id;                          //    id:                    String
    private String description;                 //    description:           String
    private String locationName;                //    location_name:         String
    private Double locationLatitude;            //    location_lat:          Double
    private Double locationLongitude;           //    location_long:         Double
    private Integer maxDistance;                    //    max_distance:          Integer
    private List<String> observedProperties;    //    observed_property:     List<String>
    private String resourceType;                //    resource_type:         String
    private Boolean shouldRank;                 //    should_rank:           Boolean
    
    public SearchQueryBuilder platformId(String id) {
        platformId = id;
        return this;
    }
    
    public SearchQueryBuilder platformName(String name) {
        platformName = name;
        return this;
    }
    
    public SearchQueryBuilder owner(String name) {
        owner = name;
        return this;
    }
    
    public SearchQueryBuilder name(String name) {
        this.name = name;
        return this;
    }
    
    public SearchQueryBuilder id(String id) {
        this.id = id;
        return this;
    }
    
    public SearchQueryBuilder description(String text) {
        this.description = text;
        return this;
    }
 
    public SearchQueryBuilder locationName(String name) {
        this.locationName = name;
        return this;
    }
    
    public SearchQueryBuilder locationLatitude(double value) {
        this.locationLatitude = value;
        return this;
    }
    
    public SearchQueryBuilder locationLongitude(double value) {
        this.locationLongitude = value;
        return this;
    }
    
    public SearchQueryBuilder maxDistance(int value) {
        this.maxDistance = value;
        return this;
    }
    
    public SearchQueryBuilder observedProperties(String ...values) {
        observedProperties = List.of(values);
        return this;
    }
    
    public SearchQueryBuilder resourceType(String type) {
        this.resourceType = type;
        return this;
    }
    
    public SearchQueryBuilder shouldRank(boolean value) {
        this.shouldRank = value;
        return this;
    }
    
    public Map<String, String> build() {
        Map<String,String> queryMap = new HashMap<>();

        if(platformId != null)
            queryMap.put("platform_id", platformId);
        if(platformName != null)
            queryMap.put("platform_name", platformName);
        if(owner != null)
            queryMap.put("owner", owner);
        if(name != null)
            queryMap.put("name", name);
        if(id != null)
            queryMap.put("id", id);
        if(description != null)
            queryMap.put("description", description);
        if(locationName != null)
            queryMap.put("location_name", locationName);
        if(locationLatitude != null)
            queryMap.put("location_lat", locationLatitude.toString());
        if(locationLongitude != null)
            queryMap.put("location_long", locationLongitude.toString());
        if(maxDistance != null)
            queryMap.put("max_distance", maxDistance.toString());
        if(observedProperties != null && !observedProperties.isEmpty())
            queryMap.put("observed_property", observedProperties.stream().collect(Collectors.joining(",")));
        if(resourceType != null)
            queryMap.put("resource_type", resourceType);
        if(shouldRank != null)
            queryMap.put("should_rank", shouldRank.toString());

        return queryMap;
    }
}
