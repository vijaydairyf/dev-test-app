package com.devapp.devmain.agentfarmersplit;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by u_pendra on 16/10/17.
 */

public class SplitCollectionEntity implements Serializable {


    public int parentSeqNum;
    public String collectionDate;
    public String collectionShift;
    public String agentId;
    public ArrayList<AgentSplitEntity> agentSplitEntities;

    @JsonIgnore
    public long collectionTime;


}
