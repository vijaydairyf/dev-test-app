package com.devapp.smartcc;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.smartcc.entities.AgentEntity;
import com.devApp.R;

import java.util.ArrayList;

/**
 * Created by Upendra on 10/15/2016.
 */
public class AgentDetailsActivity extends Activity {

    TextView tvAgentName, tvAgentId, tvAgentUniqueId, tvAgentMob, tvAgentNumOfCans, tvDistance;
    Spinner spCenters, spRoutes;
    AgentEntity agentEntity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_details);
        agentEntity = (AgentEntity) getIntent().getSerializableExtra("AGENT_ENTITY");


        onInitializeView();
        setAgentData();
    }

    private void onInitializeView() {
        tvAgentId = (TextView) findViewById(R.id.tvAgentId);

        tvAgentMob = (TextView) findViewById(R.id.tvagentMob);
        tvAgentName = (TextView) findViewById(R.id.tvAgentName);
        tvAgentNumOfCans = (TextView) findViewById(R.id.tvNumberOfCans);
        tvAgentUniqueId = (TextView) findViewById(R.id.tvAgentUniqueId);
        tvDistance = (TextView) findViewById(R.id.tvDistanceToDilevery);

        spCenters = (Spinner) findViewById(R.id.spCenters);
        spRoutes = (Spinner) findViewById(R.id.spAgentRoutes);
    }

    private void setAgentData() {
        tvAgentId.setText(agentEntity.agentID);
        tvAgentUniqueId.setText(agentEntity.uniqueID1);
        tvDistance.setText(String.valueOf(agentEntity.distanceToDelivery));
        tvAgentNumOfCans.setText(String.valueOf(agentEntity.numCans));
        tvAgentMob.setText(agentEntity.mobileNum);
        tvAgentName.setText(agentEntity.firstName);

        ArrayList<String> centerList = getCenters();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                AgentDetailsActivity.this, android.R.layout.simple_spinner_item,
                centerList);
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCenters.setAdapter(dataAdapter);

        ArrayList<String> routesList = getRoutes(centerList);

        dataAdapter = new ArrayAdapter<String>(
                AgentDetailsActivity.this, android.R.layout.simple_spinner_item,
                routesList);
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRoutes.setAdapter(dataAdapter);


    }

    private ArrayList<String> getCenters() {
        ArrayList<String> allCenters = new ArrayList<>();

        if (agentEntity.centerList != null && agentEntity.centerList.size() > 0) {
            for (int i = 0; i < agentEntity.centerList.size(); i++) {
                allCenters.add(agentEntity.centerList.get(i));
            }
        } else {
            allCenters.add("No Centers");
        }

        return getCenters();
    }

    private ArrayList<String> getRoutes(ArrayList<String> allCenters) {
        ArrayList<String> allRoutes = new ArrayList<>();
        DatabaseHandler database = DatabaseHandler.getDatabaseInstance();

        if (!allCenters.get(0).equalsIgnoreCase("No Centers")) {

            for (int i = 0; i < allCenters.size(); i++) {
                String route = database.getRouteFromChillingCenterTable(allCenters.get(i));
                allRoutes.add(route);
            }
        } else {
            allRoutes.add("No Routes");
        }

        return allRoutes;

    }


}
