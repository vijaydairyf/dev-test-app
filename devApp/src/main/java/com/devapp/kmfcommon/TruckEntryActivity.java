package com.devapp.kmfcommon;

import android.app.Activity;


/**
 * Created by Nitin on 2/2/2016.
 */

public class TruckEntryActivity extends Activity {
    /*EditText e_quantity, e_avgfat, e_avgsnf, e_rate, e_truck_number;
    TextView totalAmount;

    Button addBtn, finishBtn;
    SalesActivity salesActivity;
    String shift = "M", milktype = "COW", purity = Util.SALES_TXN_SUBTYPE_NORMAL, sTotal_Rate = "";
    double i_rate;
    String socid;
    String s_quan;
    private SalesRecordEntity repEntity;
    private Dialog syncDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_truck_entry);

        syncDialog = Util.showSyncing(this);
        e_quantity = (EditText) findViewById(R.id.e_quantity);
        e_avgfat = (EditText) findViewById(R.id.e_avgfat);
        e_avgsnf = (EditText) findViewById(R.id.e_avgsnf);
        e_rate = (EditText) findViewById(R.id.e_rate);

        // need text watcher for rate
        // e_rate.setText(salesActivity.getRateFromRateChart(e_avgsnf.getText().toString().trim(), e_avgfat.getText().toString().trim()));

        e_truck_number = (EditText) findViewById(R.id.e_truck_number);
        totalAmount = (TextView) findViewById(R.id.amount);

        // focus change

        e_quantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && e_quantity.getText().length() >= 1) {
                    double quant = Double.valueOf(e_quantity.getText().toString().trim());
                    DecimalFormat decimalFormatAmount = new DecimalFormat("#0.00");
                    s_quan = decimalFormatAmount.format(quant);

                    e_quantity.setText(s_quan);
                }
            }
        });
        e_avgfat.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && e_avgfat.getText().length() >= 1) {
                    double quant = Double.valueOf(e_avgfat.getText().toString().trim());
                    DecimalFormat decimalFormatAmount = new DecimalFormat("#0.0");
                    s_quan = decimalFormatAmount.format(quant);

                    e_avgfat.setText(s_quan);
                }
            }
        });
        e_avgsnf.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && e_avgsnf.getText().length() >= 1) {
                    double quant = Double.valueOf(e_avgsnf.getText().toString().trim());
                    DecimalFormat decimalFormatAmount = new DecimalFormat("#0.0");
                    s_quan = decimalFormatAmount.format(quant);

                    e_avgsnf.setText(s_quan);
                }
            }
        });
        e_rate.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && e_rate.getText().length() >= 1) {
                    double quant = Double.valueOf(e_rate.getText().toString().trim());
                    DecimalFormat decimalFormatAmount = new DecimalFormat("#0.00");
                    s_quan = decimalFormatAmount.format(quant);

                    e_rate.setText(s_quan);
                }
            }
        });
        // setting rate in textwatcher

        e_rate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (0 != e_rate.getText().length()) {

                    if (e_quantity.getText().toString().trim().length() >= 1 && e_rate.getText().toString().trim().length() >= 1 && e_avgfat.getText().toString().trim().length() >= 1 && e_avgsnf.getText().toString().trim().length() >= 1) {
                        i_rate = Double.valueOf(e_quantity.getText().toString().trim()) * Double.valueOf(e_rate.getText().toString().trim());
                        DecimalFormat decimalFormatAmount = new DecimalFormat("#0.00");
                        String s_rate = decimalFormatAmount.format(i_rate);
                        totalAmount.setText(s_rate);
                    }
                } else {
                    // Toast.makeText(getApplicationContext(), "Truck Number Required", Toast.LENGTH_LONG).show();
                }
            }
        });


        addBtn = (Button) findViewById(R.id.addBtn);
        finishBtn = (Button) findViewById(R.id.finishBtn);
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Purity Radio
        RadioGroup radioPurity = (RadioGroup) findViewById(R.id.radio_purity);

        radioPurity.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_quality:
                        // Toast.makeText(getApplicationContext(), "Quality", Toast.LENGTH_LONG).show();
                        purity = Util.SALES_TXN_SUBTYPE_NORMAL;
                        break;
                    case R.id.radio_cob:
                        //  Toast.makeText(getApplicationContext(), "COB", Toast.LENGTH_LONG).show();
                        purity = Util.SALES_TXN_SUBTYPE_COB;
                        break;

                }
            }
        });
        // Shift Radio
        RadioGroup radioShift = (RadioGroup) findViewById(R.id.radio_shift);

        radioShift.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_morning:
                        shift = "M";
                        // Toast.makeText(getApplicationContext(), "Morning", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.radio_evening:
                        shift = "E";
                        // Toast.makeText(getApplicationContext(), "Evening", Toast.LENGTH_LONG).show();
                        break;

                }
            }
        });

        // Milk typle radio
        RadioGroup radioMilkTypeOf = (RadioGroup) findViewById(R.id.radio_milk_type);

        radioMilkTypeOf.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_cow:
                        // Toast.makeText(getApplicationContext(), "Cow", Toast.LENGTH_LONG).show();
                        milktype = "COW";
                        break;
                    case R.id.radio_buffalo:
                        // Toast.makeText(getApplicationContext(), "Buffalo", Toast.LENGTH_LONG).show();
                        milktype = "BUFFALO";
                        break;
                    case R.id.radio_mixed:
                        //  Toast.makeText(getApplicationContext(), "Mixed", Toast.LENGTH_LONG).show();
                        milktype = "MIXED";
                        break;

                }
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {


                                          String date = Util.getTodayDateAndTime(1, TruckEntryActivity.this, false);
                                          SessionManager session = new SessionManager(getApplicationContext());
                                          salesActivity = new SalesActivity();
                                          socid = session.getCollectionID();
                                          String startTime = Util.getTodayDateAndTime(3, TruckEntryActivity.this, false);
                                          Calendar calendar = Calendar.getInstance();
                                          calendar.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));

                                          if (!(e_truck_number.getText().toString().trim().isEmpty()) && !(e_avgsnf.getText().toString().trim().isEmpty()) && !(e_avgfat.getText().toString().trim().isEmpty()) && !(e_rate.getText().toString().trim().isEmpty())) {

                                              repEntity = setSalesRecordEntryObject(
                                                      e_truck_number.getText().toString().trim(),
                                                      "NAN",
                                                      e_avgsnf.getText().toString().trim(),
                                                      e_avgfat.getText().toString().trim(),
                                                      e_rate.getText().toString().trim(),
                                                      "NAN",
                                                      "manual",
                                                      date,
                                                      shift,
                                                      totalAmount.getText().toString().trim(),
                                                      e_quantity.getText().toString().trim(),
                                                      "NAN",
                                                      milktype.toUpperCase(Locale.ENGLISH),
                                                      socid,
                                                      startTime,
                                                      "0.0",
                                                      "accept",
                                                      "0.0",
                                                      "Manual",
                                                      "Manual",
                                                      Util.SALES_TXN_TYPE_TRUCK,
                                                      purity,
                                                      "NAN",
                                                      calendar.getTimeInMillis());

                                              DatabaseManager dbManager = new DatabaseManager(getApplicationContext());
                                              long colRecStatusIndex = -1;
                                              try {
                                                  colRecStatusIndex = dbManager.addSalesRecord(repEntity);
                                                  resetView();
                                                  syncDialog.show();
                                                  final Handler handler = new Handler();
                                                  final Runnable runnable = new Runnable() {
                                                      @Override
                                                      public void run() {
                                                          if (syncDialog.isShowing()) {
                                                              syncDialog.dismiss();
                                                          }
                                                      }
                                                  };

                                                  handler.postDelayed(runnable, 2000);

                                                  syncDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                                      @Override
                                                      public void onDismiss(DialogInterface dialog) {
                                                          handler.removeCallbacks(runnable);
                                                      }
                                                  });


                                              } catch (Exception e) {
                                                  e.printStackTrace();
                                              }
                                              if (colRecStatusIndex == -1) {
                                                  Toast.makeText(getApplicationContext(), "Error while creating collection record.", Toast.LENGTH_LONG).show();
                                              }
                                          } else {
                                              Toast.makeText(getApplicationContext(), "Data Cant Be Blank", Toast.LENGTH_LONG).show();
                                          }
                                      }

                                  }

        );

    }


    public SalesRecordEntity setSalesRecordEntryObject(String salesId, String name, String snf, String fat, String rate, String user,
                                                       String manual, String date, String shift, String amount, String quantity, String txnNumber,

                                                       String milkType, String socId, String time, String awm, String status,
                                                       String clr, String quantityMode, String qualityMode, String txnType, String txnSubType,
                                                       String temp, long miliTime) {
        SalesRecordEntity reportEntity = new SalesRecordEntity();
        //reportEntity.columnId = columnId;
        reportEntity.salesId = salesId;
        reportEntity.name = name;
        reportEntity.snf = snf;
        reportEntity.fat = fat;
        reportEntity.user = user;
        reportEntity.manual = manual;
        reportEntity.shift = shift;
        reportEntity.amount = amount;
        reportEntity.Quantity = quantity;
        reportEntity.date = date;
        reportEntity.txnNumber = txnNumber;
        reportEntity.milkType = milkType;
        reportEntity.rate = rate;
        reportEntity.socId = socId;
        reportEntity.time = time;

        reportEntity.collectionTime = miliTime;

        reportEntity.awm = awm;
        reportEntity.clr = clr;
        reportEntity.status = status;
        reportEntity.quantityMode = quantityMode;
        reportEntity.qualityMode = qualityMode;

        reportEntity.temperature = temp;
        reportEntity.txnType = txnType;
        reportEntity.txnSubType = txnSubType;

        return reportEntity;
    }

    public void resetView() {
        e_quantity.setText("");
        e_rate.setText("");
        e_avgfat.setText("");
        e_avgsnf.setText("");
        e_truck_number.setText("");
        totalAmount.setText("");
    }*/

}