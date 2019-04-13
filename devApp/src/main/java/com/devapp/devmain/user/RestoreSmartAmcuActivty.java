package com.devapp.devmain.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.devapp.devmain.DevAppApplication;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.dao.RateChartNameDao;
import com.devapp.devmain.dao.RateDao;
import com.devapp.devmain.dao.UserDao;
import com.devapp.devmain.devicemanager.DatabaseManager;
import com.devapp.devmain.entity.RatechartDetailsEnt;
import com.devapp.devmain.entity.UserEntity;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devApp.R;

import java.util.ArrayList;

/**
 * Created by Upendra on 6/3/2015.
 */
public class RestoreSmartAmcuActivty extends Activity implements View.OnClickListener {

    static final int GET_OPERATOR_PASSWORD = 0;
    static final int GET_MANAGER_PASSWORD = 1;
    static final int DELETE_ALL_USERS = 2;
    static final int DELETE_ALL_RATECHART = 3;
    Button btnFOP, btnFMP, btnRestoreUsers, btnDeleteRatecharts, btnOperatorSubmit, btnManagerSubmit, btnSubmitPassword;
    LinearLayout lnForgotManager, lnForgotOperator, lnPassword;
    EditText etManager, etOperator, etPassword;
    boolean isLnOperator, isLnManager, isLnPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore_data);

        lnForgotManager = (LinearLayout) findViewById(R.id.lnForgetManager);
        lnForgotOperator = (LinearLayout) findViewById(R.id.lnforgotOperator);
        lnPassword = (LinearLayout) findViewById(R.id.lnDeleteRateChart);

        btnFMP = (Button) findViewById(R.id.btnForgotManager);
        btnFOP = (Button) findViewById(R.id.btnForgotOperator);

        btnRestoreUsers = (Button) findViewById(R.id.btnResotreAllUser);
        btnDeleteRatecharts = (Button) findViewById(R.id.btnDeleteRateCharts);

        btnOperatorSubmit = (Button) findViewById(R.id.btnsubmitOperator);
        btnManagerSubmit = (Button) findViewById(R.id.btnSubmitManager);
        btnSubmitPassword = (Button) findViewById(R.id.btnsubmitPassword);

        etManager = (EditText) findViewById(R.id.etEntermanager);
        etOperator = (EditText) findViewById(R.id.etEnterOperatorName);
        etPassword = (EditText) findViewById(R.id.etDeleteRateChart);


        btnManagerSubmit.setOnClickListener(this);
        btnOperatorSubmit.setOnClickListener(this);
        btnDeleteRatecharts.setOnClickListener(this);
        btnRestoreUsers.setOnClickListener(this);
        btnFMP.setOnClickListener(this);
        btnFOP.setOnClickListener(this);
        btnSubmitPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnForgotManager: {
                isLnOperator = false;
                isLnPassword = false;
                lnForgotOperator.setVisibility(View.GONE);
                lnPassword.setVisibility(View.GONE);
                if (!isLnManager) {
                    isLnManager = true;
                    lnForgotManager.setVisibility(View.VISIBLE);

                } else {
                    isLnManager = false;
                    lnForgotManager.setVisibility(View.GONE);
                }

            }
            break;

            case R.id.btnForgotOperator: {
                isLnManager = false;
                isLnPassword = false;
                lnForgotManager.setVisibility(View.GONE);
                lnPassword.setVisibility(View.GONE);
                if (!isLnOperator) {
                    isLnOperator = true;
                    lnForgotOperator.setVisibility(View.VISIBLE);
                } else {
                    isLnOperator = false;
                    lnForgotOperator.setVisibility(View.GONE);
                }

            }
            break;

            case R.id.btnResotreAllUser: {
                hideInfo();
                createAlert("Restore default users", getResources().getString(R.string.restore_explanation), "Restore", DELETE_ALL_USERS);
            }
            break;

            case R.id.btnDeleteRateCharts:

            {
                isLnManager = false;
                isLnOperator = false;
                lnForgotManager.setVisibility(View.GONE);
                lnForgotOperator.setVisibility(View.GONE);
                if (!isLnPassword) {
                    isLnPassword = true;
                    lnPassword.setVisibility(View.VISIBLE);
                } else {
                    isLnPassword = false;
                    lnPassword.setVisibility(View.GONE);
                }

            }

            break;

            case R.id.btnSubmitManager: {
                hideInfo();
                getManagerPassword();
            }
            break;

            case R.id.btnsubmitOperator: {
                hideInfo();
                getOperatorPassword();
            }
            break;
            case R.id.btnsubmitPassword: {
                if (etPassword.getText().toString() != null && etPassword.getText().toString().equalsIgnoreCase("$m@rt@mcu"))

                {
                    etPassword.setText("");
                    hideInfo();
                    createAlert("Delete All rate charts", getResources().getString(R.string.delete_rate_explanation), "Delete", DELETE_ALL_RATECHART);
                } else {
                    etPassword.setText("");
                    Toast.makeText(RestoreSmartAmcuActivty.this, "Invalid password. Please contact with smartAmcu team.", Toast.LENGTH_SHORT).show();
                }

            }
            break;

            default:
                break;
        }
    }

    public void createAlert(String header, String body, String button, final int toDo) {

        AlertDialog.Builder builder = new AlertDialog.Builder(RestoreSmartAmcuActivty.this);

        builder.setTitle(header);
        builder.setMessage(body);

        builder.setPositiveButton(button,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if (toDo == DELETE_ALL_RATECHART) {
                            DeleteAllrateCharts();
                            dialog.dismiss();
                        } else if (toDo == DELETE_ALL_USERS) {
                            restoreAllUsers();
                            dialog.dismiss();
                        } else {
                            dialog.dismiss();
                        }
                    }
                });


        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        // show it
        alertDialog.show();

    }

    public void DeleteAllrateCharts() {
        ArrayList<RatechartDetailsEnt> allRateChartDetailsEnt = null;
        DatabaseHandler dataBasehandler = DatabaseHandler.getDatabaseInstance();

        AmcuConfig amcuConfig = AmcuConfig.getInstance();

        RateDao rateDao = (RateDao) DaoFactory.getDao(CollectionConstants.RATES);
        RateChartNameDao rateChartNameDao = (RateChartNameDao) DaoFactory.getDao(CollectionConstants.RATECHART_NAME);

        long refId = rateChartNameDao.findRateRefIdFromName(amcuConfig.getRateChartName());


        try {
            allRateChartDetailsEnt = rateChartNameDao.findRateChartFromInputs(null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Removed database close statement

        if (allRateChartDetailsEnt != null && allRateChartDetailsEnt.size() > 0) {
            DatabaseManager rcm = new DatabaseManager(RestoreSmartAmcuActivty.this);
            for (int i = 0; i < allRateChartDetailsEnt.size(); i++) {
                rcm.deleteRatechart(allRateChartDetailsEnt.get(i));
            }

        } else {
            Toast.makeText(RestoreSmartAmcuActivty.this, "No rate chart to delete.", Toast.LENGTH_SHORT).show();
        }

    }

    public void restoreAllUsers() {

        UserDao userDao = (UserDao) DaoFactory.getDao(CollectionConstants.USER);

        try {
            userDao.deleteByKey(DatabaseHandler.KEY_USER_SOCIETYID,
                    String.valueOf(new SessionManager(
                            DevAppApplication.getAmcuContext()).getSocietyColumnId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Removed database close;

        Util.addUsers(RestoreSmartAmcuActivty.this);
    }

    public void getOperatorPassword() {
        DatabaseHandler db = DatabaseHandler.getDatabaseInstance();
        UserEntity userEntity;
        try {
            userEntity = db.getPassword(etOperator.getText().toString().replaceAll(" ", "")
                    .toUpperCase());
            String passWord = userEntity.password;
            String mobileNum = userEntity.mobile;

            String role = userEntity.role;
            if (userEntity != null) {

                if (userEntity.role != null && userEntity.role.equalsIgnoreCase("Operator")) {
                    createAlert("Operator password", passWord, "OK", GET_OPERATOR_PASSWORD);
                } else {
                    Toast.makeText(RestoreSmartAmcuActivty.this, "No such operator exist.", Toast.LENGTH_SHORT).show();
                }

            } else Toast.makeText(RestoreSmartAmcuActivty.this, "Incorrect UserID!!",
                    Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void getManagerPassword() {

        DatabaseHandler db = DatabaseHandler.getDatabaseInstance();
        UserEntity userEntity;
        try {
            userEntity = db.getPassword(etManager.getText().toString().replaceAll(" ", "")
                    .toUpperCase());
            String passWord = userEntity.password;
            String mobileNum = userEntity.mobile;
            if (userEntity != null) {
                if (userEntity.role != null && userEntity.role.equalsIgnoreCase("Manager")) {

                    createAlert("Manager password", passWord, "OK", GET_MANAGER_PASSWORD);
                } else {
                    Toast.makeText(RestoreSmartAmcuActivty.this, "No such manager exist.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(RestoreSmartAmcuActivty.this, "Incorrect UserID!!",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void hideInfo() {

        isLnOperator = false;
        isLnManager = false;
        isLnPassword = false;
        lnForgotOperator.setVisibility(View.GONE);
        lnForgotManager.setVisibility(View.GONE);
        lnPassword.setVisibility(View.GONE);
    }


}
