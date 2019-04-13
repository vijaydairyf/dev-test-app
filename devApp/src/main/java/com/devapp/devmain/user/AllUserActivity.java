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
import com.devapp.devmain.dao.UserDao;
import com.devapp.devmain.entity.UserEntity;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devApp.R;

import java.util.ArrayList;

public class AllUserActivity extends Activity {

    ListView lvUsers;
    FarmerAdapter userAdapter;
    UserEntity userEnt;
    ArrayList<UserEntity> allUsers = new ArrayList<UserEntity>();
    Button btnNewUser, btnEdit;
    TextView tvUser;
    SessionManager session;
    RelativeLayout progressLayoutScan;
    UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allfarmerdetails);
        session = new SessionManager(AllUserActivity.this);
        userDao = (UserDao) DaoFactory.getDao(CollectionConstants.USER);

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
        tvUser.setText("Users");

        lvUsers.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                userEnt = allUsers.get(arg2);

                Intent intent = new Intent(AllUserActivity.this,
                        AddUserActivity.class);
                intent.putExtra("UserEntity", userEnt);
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

                PopupMenu popup = new PopupMenu(AllUserActivity.this, arg1);
                popup.getMenuInflater().inflate(R.menu.popup_menu,
                        popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem arg0) {

                        if (allUsers.get(pos).userId.equalsIgnoreCase(UserDetails.OPERATOR) ||
                                allUsers.get(pos).userId.equalsIgnoreCase(UserDetails.MANAGER)) {
                            Util.displayErrorToast("Default users can not be deleted", AllUserActivity.this);
                            return false;
                        }

                        long deleteCk = 0;
                        try {

                            deleteCk = userDao.deleteByKey(DatabaseHandler.KEY_USER_EMAIL, allUsers.get(pos).userId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (deleteCk > 0) {
                            Toast.makeText(AllUserActivity.this,
                                    "User details successfully deleted!",
                                    Toast.LENGTH_SHORT).show();
                            DisplayList();
                        } else {
                            Toast.makeText(AllUserActivity.this, "Not deleted",
                                    Toast.LENGTH_SHORT).show();
                        }

                        return true;
                    }
                });

                popup.show();

                return true;
            }
        });

        //Made this invisible after version11.1.1
        btnNewUser.setVisibility(View.VISIBLE);

        btnNewUser.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllUserActivity.this,
                        AddUserActivity.class);

                userEnt = null;
                intent.putExtra("UserEntity", userEnt);
                intent.putExtra("isNew", true);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left,
                        R.anim.slide_out_left);
            }
        });

    }

    @Override
    protected void onStart() {
        setCreateEnableOrDisable();
        DisplayList();
        super.onStart();
    }

    public void DisplayList() {

        final DatabaseHandler dbHandler = DatabaseHandler.getDatabaseInstance(
        );
        new Handler().post(new Runnable() {
            @Override
            public void run() {

                String id = String.valueOf(session.getSocietyColumnId());
                userAdapter = new FarmerAdapter(AllUserActivity.this,
                        dbHandler.getUsers(String.valueOf(session
                                .getSocietyColumnId())), 1);
                lvUsers.setAdapter(userAdapter);
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
                    allUsers = dbHandler.getAllUsers(
                            String.valueOf(session.getSocietyColumnId()), 0);
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
                Util.Logout(AllUserActivity.this);
                finish();
                return true;

            case KeyEvent.KEYCODE_F10:
                Util.restartApp(AllUserActivity.this);
                return true;

            case KeyEvent.KEYCODE_F11:

                Util.restartTab(AllUserActivity.this);
                return true;

            case KeyEvent.KEYCODE_F12:
                Util.shutDownTab(AllUserActivity.this, null);
                return true;
            case KeyEvent.KEYCODE_DEL:
                finish();
                return true;

            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    public void setCreateEnableOrDisable() {
        btnEdit.setVisibility(View.GONE);

        if (AmcuConfig.getInstance().getMultipleUser()) {
            btnNewUser.setVisibility(View.VISIBLE);
        } else {
            btnNewUser.setVisibility(View.GONE);
        }
    }


}
