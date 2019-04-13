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
import com.devapp.smartcc.adapters.TruckAdapter;
import com.devapp.smartcc.database.TruckCCDatabase;
import com.devapp.smartcc.entities.TrucKInfo;
import com.devApp.R;

import java.util.ArrayList;

public class AllTruckActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    RecyclerView recycleView;
    ArrayList<TrucKInfo> allTruckDetails;
    TruckAdapter truckAdapter;
    DatabaseHandler dbHandler;
    TruckCCDatabase truckCcDatabase;
    TextView tvNoDataFound;

    public static ArrayList<TrucKInfo> filterTruckDetails(ArrayList<TrucKInfo> allTruckDetails,
                                                          String query) {
        String searchQuery = query.toLowerCase();
        ArrayList<TrucKInfo> filterDetailsList = new ArrayList<>();
        for (TrucKInfo truckDetailsEntry : allTruckDetails) {
            if (truckDetailsEntry.truckId.toLowerCase().contains(searchQuery)) {
                filterDetailsList.add(truckDetailsEntry);
            }
        }
        return filterDetailsList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_truck);
        recycleView = (RecyclerView) findViewById(R.id.recylerList);
        tvNoDataFound = (TextView) findViewById(R.id.tvNoDataFound);

    }

    @Override
    protected void onStart() {
        super.onStart();

        dbHandler = DatabaseHandler.getDatabaseInstance();
        truckCcDatabase = dbHandler.getTruckCCDatabase();
        // addTruckDetails();
        allTruckDetails = truckCcDatabase.getAllTruckInfoEntity();
        truckAdapter = new TruckAdapter(allTruckDetails, this, recycleView);
        recycleView.setAdapter(truckAdapter);
        recycleView.setLayoutManager(new GridLayoutManager(AllTruckActivity.this, 3));
        if (allTruckDetails.size() > 0) {
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
        searchView.setOnQueryTextListener(AllTruckActivity.this);


        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {

        ArrayList<TrucKInfo> filteredArrayList;
        filteredArrayList = filterTruckDetails(allTruckDetails, query);
        if (filteredArrayList.size() > 0) {
            setVisiblityNoDataFound(false);
        } else {
            setVisiblityNoDataFound(true);
        }
        truckAdapter = new TruckAdapter(filteredArrayList, AllTruckActivity.this, recycleView);
        recycleView.setAdapter(truckAdapter);
        recycleView.setLayoutManager(new GridLayoutManager(AllTruckActivity.this, 3));
        return false;
    }

    void setVisiblityNoDataFound(boolean enable) {
        if (enable)
            tvNoDataFound.setVisibility(View.VISIBLE);
        else
            tvNoDataFound.setVisibility(View.GONE);
    }


}


