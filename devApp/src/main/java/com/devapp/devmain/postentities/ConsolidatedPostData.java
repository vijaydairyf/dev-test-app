package com.devapp.devmain.postentities;

import com.devapp.devmain.ConsolidationPost.SynchronizableElement;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by u_pendra on 17/1/18.
 */

public class ConsolidatedPostData {

    @JsonProperty("metadata")
    public ConsolidatedMetadata consolidatedMetadata;

    @JsonProperty("data")
    public Hashtable<String, ArrayList<? extends SynchronizableElement>> recordEntries;

//    @JsonProperty("farmerMilkCollections")
//    public ArrayList<? extends SynchronizableElement> farmerMilkCollections;

//    @JsonProperty("tankerMilkCollections")
//    public ArrayList<TankerPostEntity> tankerMilkCollections;
//
//    @JsonProperty("salesCollections")
//    public ArrayList<SalesPostEntity> salesCollections;
//
//    @JsonProperty("routeCollections")
//    public ArrayList<RoutePostEntity> routeCollections;
//
//    @JsonProperty("farmerSplitCollections")
//    public ArrayList<FarmerSplitCollectionEntity> farmerSplitCollections;
//
//    @JsonProperty("extraParametersDetails")
//    public ArrayList<AdditionFieldEditEntity> extraParametersDetails;
//
//    @JsonProperty("collectionCenterMilkCollections")
//    public ArrayList<MCCPostEntity> collectionCenterMilkCollections;
//
//    @JsonProperty("testRecords")
//    public ArrayList<SamplePostEntity> testRecords;

//    public void setAttribute(ArrayList<? extends SynchronizableElement> se,String type)
//    {
//        if(type.equalsIgnoreCase("FARMER"))
//        {
//            this.farmerMilkCollections =  se;
//        }
//    }
//
//    public ArrayList<? extends SynchronizableElement> getAttribute(String type)
//    {
//        if(type.equalsIgnoreCase("FARMER"))
//        {
//            return this.farmerMilkCollections;
//        }else
//            return null;
//    }
}
