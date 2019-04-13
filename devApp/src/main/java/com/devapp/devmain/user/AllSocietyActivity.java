package com.devapp.devmain.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.devapp.devmain.entity.SocietyEntity;
import com.devapp.devmain.main.FarmerScannerActivity;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devApp.R;

import java.util.ArrayList;

public class AllSocietyActivity extends Activity {

    private ListView lvSocieties;
    private FarmerAdapter userAdapter;
    private SocietyEntity societyEnt;
    private ArrayList<SocietyEntity> allSocieties = new ArrayList<SocietyEntity>();
    private Button btnNewUser;
    private Button btnLogout;
    private TextView tvUser;
    private ImageView ivLogout;

    private SessionManager session;
    private AmcuConfig amcuConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allsociety_activity);

        session = new SessionManager(AllSocietyActivity.this);
        amcuConfig = AmcuConfig.getInstance();
        lvSocieties = (ListView) findViewById(R.id.lvSocietydetails);
        btnNewUser = (Button) findViewById(R.id.btnNewSociety);
        tvUser = (TextView) findViewById(R.id.tvheader);
        ivLogout = (ImageView) findViewById(R.id.ivLogout);

        btnLogout = (Button) findViewById(R.id.btnLogout);

        tvUser.setText("Societies");

        lvSocieties.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                societyEnt = allSocieties.get(arg2);

                session.setCollectionID(societyEnt.socCode);
                session.setSocietyColumnId(societyEnt.colId);
                session.setSocManagerEmail(societyEnt.socEmail1);
                session.setSocietyName(societyEnt.name);

                Intent intent = new Intent(AllSocietyActivity.this,
                        FarmerScannerActivity.class);
                intent.putExtra("SocietyEntity", societyEnt);
                intent.putExtra("isNew", false);

                startActivityForResult(intent, 0);
                overridePendingTransition(R.anim.slide_in_left,
                        R.anim.slide_out_left);

            }
        });
        lvSocieties.requestFocus();

        // As per requirement it should support only one society
        btnNewUser.setVisibility(View.GONE);

        btnNewUser.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllSocietyActivity.this,
                        EnrollSocietyActivity.class);

                societyEnt = null;

                intent.putExtra("SocietyEntity", societyEnt);
                intent.putExtra("isNew", true);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left,
                        R.anim.slide_out_left);

            }
        });

        btnLogout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Util.Logout(AllSocietyActivity.this);

            }
        });

        ivLogout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Util.Logout(AllSocietyActivity.this);

            }
        });
    }

    @Override
    protected void onStart() {
        DisplayList();
        super.onStart();
    }

    private void DisplayList() {

        final DatabaseHandler dbHandler = DatabaseHandler.getDatabaseInstance(
        );
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                userAdapter = new FarmerAdapter(AllSocietyActivity.this,
                        dbHandler.getSocieties(), 2);
                lvSocieties.setAdapter(userAdapter);
            }
        });
        getAllusers();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private void getAllusers() {
        final DatabaseHandler dbHandler = DatabaseHandler.getDatabaseInstance(
        );
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    allSocieties = dbHandler.getSocietyEntity(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {

            case KeyEvent.KEYCODE_F9:
                Util.Logout(AllSocietyActivity.this);
                finish();
                return true;

            case KeyEvent.KEYCODE_F10:
                Util.restartApp(AllSocietyActivity.this);
                return true;

            case KeyEvent.KEYCODE_F11:

                Util.restartTab(AllSocietyActivity.this);
                return true;

            case KeyEvent.KEYCODE_F12:
                Util.shutDownTab(AllSocietyActivity.this, null);
                return true;

            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
