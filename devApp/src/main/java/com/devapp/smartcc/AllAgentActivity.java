package com.devapp.smartcc;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.smartcc.adapters.AgentListAdapter;
import com.devapp.smartcc.database.AgentDatabase;
import com.devapp.smartcc.entities.AgentEntity;
import com.devApp.R;

import java.util.ArrayList;

public class AllAgentActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    RecyclerView recycleView;
    ArrayList<AgentEntity> allAgentDetails;
    AgentListAdapter agentAdapter;
    DatabaseHandler dbHandler;
    AgentDatabase agentDatabase;
    TextView tvNoDataFound;

    public static ArrayList<AgentEntity> filterTruckDetails(ArrayList<AgentEntity> allAgentDetails,
                                                            String query) {
        String searchQuery = query.toLowerCase();
        ArrayList<AgentEntity> filterDetailsList = new ArrayList<>();
        for (AgentEntity agentDetailsEntry : allAgentDetails) {
            if (agentDetailsEntry.agentID.toLowerCase().contains(searchQuery)) {
                filterDetailsList.add(agentDetailsEntry);
            }
        }
        return filterDetailsList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_agent_details);
        recycleView = (RecyclerView) findViewById(R.id.recylerList);
        tvNoDataFound = (TextView) findViewById(R.id.tvNoDataFound);

    }

    @Override
    protected void onStart() {
        super.onStart();

        dbHandler = DatabaseHandler.getDatabaseInstance();
        agentDatabase = dbHandler.getAgentDatabase();
        // addTruckDetails();
        allAgentDetails = agentDatabase.getAllAgentDetails();
        agentAdapter = new AgentListAdapter(allAgentDetails, AllAgentActivity.this, recycleView);
        recycleView.setAdapter(agentAdapter);
        recycleView.setLayoutManager(new GridLayoutManager(AllAgentActivity.this, 3));

        if (allAgentDetails.size() > 0) {
            setVisiblityNoDataFound(false);
        } else {
            setVisiblityNoDataFound(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_recycle_view, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.requestFocus();
        searchView.setOnQueryTextListener(AllAgentActivity.this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {


        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {

        ArrayList<AgentEntity> filteredArrayList;
        filteredArrayList = filterTruckDetails(allAgentDetails, query);
        if (filteredArrayList.size() > 0) {
            setVisiblityNoDataFound(false);
        } else {
            setVisiblityNoDataFound(true);
        }
        agentAdapter = new AgentListAdapter(filteredArrayList, AllAgentActivity.this, recycleView);
        recycleView.setAdapter(agentAdapter);
        recycleView.setLayoutManager(new GridLayoutManager(AllAgentActivity.this, 3));
        return false;
    }

    void setVisiblityNoDataFound(boolean enable) {
        if (enable)
            tvNoDataFound.setVisibility(View.VISIBLE);
        else
            tvNoDataFound.setVisibility(View.GONE);
    }


}
