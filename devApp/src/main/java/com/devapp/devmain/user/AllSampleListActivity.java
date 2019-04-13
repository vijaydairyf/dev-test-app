package com.devapp.devmain.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.dao.SampleDao;
import com.devapp.devmain.entity.SampleDataEntity;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devApp.R;

import java.util.ArrayList;

public class AllSampleListActivity extends Activity {

    ListView lvUsers;
    FarmerAdapter sampleAdapter;
    SampleDataEntity sampleEnt;
    ArrayList<SampleDataEntity> allSampleList = new ArrayList<SampleDataEntity>();
    Button btnNewUser, btnEdit;
    TextView tvUser;
    SessionManager session;
    RelativeLayout progressLayoutScan;
    private SampleDao sampleDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allfarmerdetails);
        session = new SessionManager(AllSampleListActivity.this);
        sampleDao = (SampleDao) DaoFactory.getDao(CollectionConstants.Sample);

        // ///////////////////////////////////
        progressLayoutScan = (RelativeLayout) findViewById(R.id.progress_layout);
        progressLayoutScan.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        progressLayoutScan.setVisibility(View.GONE);
        // ///////////////////////////////////

        lvUsers = (ListView) findViewById(R.id.lvFarmerdetails);
        btnNewUser = (Button) findViewById(R.id.btnNewUser);
        tvUser = (TextView) findViewById(R.id.tvheader);
        btnEdit = (Button) findViewById(R.id.btnImportFarmer);

        btnEdit.setVisibility(View.GONE);

        tvUser.setText("Sample details");

        lvUsers.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                sampleEnt = allSampleList.get(arg2);

                Intent intent = new Intent(AllSampleListActivity.this,
                        AddSampleDataActivity.class);
                intent.putExtra("SampleDataEntity", sampleEnt);
                intent.putExtra("isNew", false);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left,
                        R.anim.slide_out_left);

            }
        });

        lvUsers.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {

                final int pos = arg2;

                String mVaildation = Util.checkForRegisteredCode(allSampleList.get(pos).sampleId,
                        AmcuConfig.getInstance().getFarmerIdDigit(), false);

                if (mVaildation == null) {
                    PopupMenu popup = new PopupMenu(AllSampleListActivity.this,
                            arg1);
                    popup.getMenuInflater().inflate(R.menu.popup_menu,
                            popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                        @Override
                        public boolean onMenuItemClick(MenuItem arg0) {


                            long deleteCk = 0;
                            try {
                                deleteCk = sampleDao.deleteByKey(DatabaseHandler.KEY_SAMPLE_ID,
                                        allSampleList.get(pos).sampleId);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (deleteCk > 0) {
                                Toast.makeText(AllSampleListActivity.this,
                                        "Sample details successfully deleted!",
                                        Toast.LENGTH_SHORT).show();
                                DisplayList();
                            } else {
                                Toast.makeText(AllSampleListActivity.this,
                                        "Not deleted", Toast.LENGTH_SHORT).show();
                            }

                            return false;
                        }
                    });

                    popup.show();
                }

                return false;
            }
        });

        btnNewUser.setText("Add new data");

        btnNewUser.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllSampleListActivity.this,
                        AddSampleDataActivity.class);

                sampleEnt = null;
                intent.putExtra("SampleEntity", sampleEnt);
                intent.putExtra("isNew", true);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        DisplayList();
        super.onStart();
    }

    public void DisplayList() {

        final DatabaseHandler dbHandler = DatabaseHandler.getDatabaseInstance(
        );
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                sampleAdapter = new FarmerAdapter(AllSampleListActivity.this,
                        dbHandler.getSamples(String.valueOf(session
                                .getSocietyColumnId())), 3);
                lvUsers.setAdapter(sampleAdapter);
            }
        });
        //Removed database close;

        getAllusers();

    }

    public void getAllusers() {
        final DatabaseHandler dbHandler = DatabaseHandler.getDatabaseInstance(
        );
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    allSampleList = dbHandler.getSampleData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //Removed database close;

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {

            case KeyEvent.KEYCODE_F9:
                Util.Logout(AllSampleListActivity.this);
                finish();
                return true;

            case KeyEvent.KEYCODE_F10:
                Util.restartApp(AllSampleListActivity.this);
                return true;

            case KeyEvent.KEYCODE_F11:

                Util.restartTab(AllSampleListActivity.this);
                return true;

            case KeyEvent.KEYCODE_F12:
                Util.shutDownTab(AllSampleListActivity.this, null);
                return true;
            case KeyEvent.KEYCODE_DEL: {
                finish();
                return true;
            }
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

}
