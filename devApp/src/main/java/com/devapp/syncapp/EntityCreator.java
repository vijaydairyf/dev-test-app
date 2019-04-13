package com.devapp.syncapp;

import android.util.Log;

import com.devapp.devmain.ConsolidationPost.SynchronizableElement;
import com.devapp.devmain.encryption.ConsolidatedData;
import com.devapp.devmain.postentities.AdditionFieldEditEntity;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.postentities.ConsolidatedMetadata;
import com.devapp.devmain.postentities.ConsolidatedPostData;
import com.devapp.devmain.postentities.EditedCenterPostEntity;
import com.devapp.devmain.postentities.EditedFarmerPostEntity;
import com.devapp.devmain.postentities.FarmerPostEntity;
import com.devapp.devmain.postentities.FarmerSplitCollectionEntity;
import com.devapp.devmain.postentities.IncompleteCenterCollection;
import com.devapp.devmain.postentities.MCCPostEntity;
import com.devapp.devmain.postentities.RoutePostEntity;
import com.devapp.devmain.postentities.SalesPostEntity;
import com.devapp.devmain.postentities.SamplePostEntity;
import com.devapp.devmain.postentities.TankerPostEntity;
import com.devapp.devmain.postentities.dispatchentities.DispatchPostEntity;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;

import java.util.ArrayList;
import java.util.Hashtable;

import static com.devapp.devmain.postentities.CollectionConstants.FARMER_MILK_COLLECTION;

/**
 * Created by Pankaj on 5/5/2018.
 */

public class EntityCreator {
    ObjectMapper mapper = new ObjectMapper();

    private static ArrayList<String> getAllCollectionList() {
        ArrayList<String> list = new ArrayList<>();
        list.add(CollectionConstants.FARMER_MILK_COLLECTION);
        list.add(CollectionConstants.MCC_MILK_COLLECTION);
        list.add(CollectionConstants.SALES_COLLECTION);
        list.add(CollectionConstants.DISPATCH_COLLECTION);
        list.add(CollectionConstants.SAMPLE_RECORDS);
        list.add(CollectionConstants.TANKER_MILK_COLLECTION);
        list.add(CollectionConstants.ROUTE_COLLECTION);
        list.add(CollectionConstants.EDITED_FARMER_COLLECTION);
        list.add(CollectionConstants.EDITED_MCC_COLLECTION);
        list.add(CollectionConstants.INCOMPLETE_MCC_RECORDS);
        list.add(CollectionConstants.EXTRA_PARAM_DETAILS);
        list.add(CollectionConstants.FARMER_SPLIT_COLLECTION);
        return list;
    }

    public ConsolidatedData getEntityCreated(String json) {
        ConsolidatedData consolidatedData = new ConsolidatedData();
        ArrayList<String> collectionNameList = getAllCollectionList();
        try {
            JsonNode jsonNode = mapper.readTree(json);
            consolidatedData.csvVersion = jsonNode.get("csvVersion").getTextValue();
            JsonNode records = jsonNode.get("records");
            if (records.isArray() && records.size() > 0) {
                ArrayList<ConsolidatedPostData> al = new ArrayList<>();
                ArrayNode recordArray = (ArrayNode) records;
                for (int i = 0; i < recordArray.size(); i++) {
                    ConsolidatedPostData consolidatedPostData = new ConsolidatedPostData();
                    Hashtable<String, ArrayList<? extends SynchronizableElement>> recordEntries = new Hashtable<>();
                    JsonNode metaData = recordArray.get(i).get("metadata");
                    consolidatedPostData.consolidatedMetadata = mapper.convertValue(metaData, ConsolidatedMetadata.class);

                    JsonNode data = recordArray.get(i).get("data");
                    for (String cName : collectionNameList) {
                        JsonNode collectionName = data.get(cName);
                        recordEntries.put(cName, getEntity(cName, (ArrayNode) collectionName));
                    }
                    consolidatedPostData.recordEntries = recordEntries;
                    al.add(consolidatedPostData);
                    consolidatedData.records = al;
                    Log.i("EntityCreator", "getEntityCreated: OVER");

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return consolidatedData;
    }

    private ArrayList<? extends SynchronizableElement> getEntity(String type, ArrayNode jsonNode) {

        ArrayList arrayList = new ArrayList();
        try {


            for (int i = 0; i < jsonNode.size(); i++) {

                switch (type) {
                    case FARMER_MILK_COLLECTION:
                        FarmerPostEntity farmerPostEntity = mapper.readValue(jsonNode.get(i).toString(), FarmerPostEntity.class);
                        arrayList.add(farmerPostEntity);
                        break;
                    case CollectionConstants.MCC_MILK_COLLECTION:
                        MCCPostEntity mccPostEntity = mapper.readValue(jsonNode.get(i).toString(), MCCPostEntity.class);
                        arrayList.add(mccPostEntity);
                        break;
                    case CollectionConstants.SALES_COLLECTION:
                        SalesPostEntity salesPostEntity = mapper.readValue(jsonNode.get(i).toString(), SalesPostEntity.class);
                        arrayList.add(salesPostEntity);
                        break;
                    case CollectionConstants.DISPATCH_COLLECTION:
                        DispatchPostEntity dispatchPostEntity = mapper.readValue(jsonNode.get(i).toString(), DispatchPostEntity.class);
                        arrayList.add(dispatchPostEntity);
                        break;
                    case CollectionConstants.SAMPLE_RECORDS:
                        SamplePostEntity samplePostEntity = mapper.readValue(jsonNode.get(i).toString(), SamplePostEntity.class);
                        arrayList.add(samplePostEntity);
                        break;
                    case CollectionConstants.TANKER_MILK_COLLECTION:
                        TankerPostEntity tankerPostEntity = mapper.readValue(jsonNode.get(i).toString(), TankerPostEntity.class);
                        arrayList.add(tankerPostEntity);
                        break;
                    case CollectionConstants.ROUTE_COLLECTION:
                        RoutePostEntity routePostEntity = mapper.readValue(jsonNode.get(i).toString(), RoutePostEntity.class);
                        arrayList.add(routePostEntity);
                        break;
                    case CollectionConstants.EDITED_FARMER_COLLECTION:
                        EditedFarmerPostEntity editedFarmerPostEntity = mapper.readValue(jsonNode.get(i).toString(), EditedFarmerPostEntity.class);
                        arrayList.add(editedFarmerPostEntity);
                        break;
                    case CollectionConstants.EDITED_MCC_COLLECTION:
                        EditedCenterPostEntity editedCenterPostEntity = mapper.readValue(jsonNode.get(i).toString(), EditedCenterPostEntity.class);
                        arrayList.add(editedCenterPostEntity);
                        break;
                    case CollectionConstants.INCOMPLETE_MCC_RECORDS:
                        IncompleteCenterCollection incompleteCenterCollection = mapper.readValue(jsonNode.get(i).toString(), IncompleteCenterCollection.class);
                        arrayList.add(incompleteCenterCollection);
                        break;
                    case CollectionConstants.EXTRA_PARAM_DETAILS:
                        AdditionFieldEditEntity additionFieldEditEntity = mapper.readValue(jsonNode.get(i).toString(), AdditionFieldEditEntity.class);
                        arrayList.add(additionFieldEditEntity);
                        break;
                    case CollectionConstants.FARMER_SPLIT_COLLECTION:
                        FarmerSplitCollectionEntity farmerSplitCollectionEntity = mapper.readValue(jsonNode.get(i).toString(), FarmerSplitCollectionEntity.class);
                        arrayList.add(farmerSplitCollectionEntity);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }
}
