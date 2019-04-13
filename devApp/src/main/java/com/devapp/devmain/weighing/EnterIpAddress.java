package com.devapp.devmain.weighing;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.devApp.R;


/**
 * Created by Upendra on 7/25/2016.
 */
public class EnterIpAddress extends DialogFragment implements View.OnClickListener {

    EditText etIpAddress;
    Button btnConnect, btnCancel;

    Connector conn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.alert_enter_ip_address, container, false);
        setCancelable(false);
        getActivity().getWindow().getAttributes().windowAnimations = R.style.Animation_AppCompat_Dialog;

        etIpAddress = (EditText) view.findViewById(R.id.etIpAddress);
        btnConnect = (Button) view.findViewById(R.id.btnConnect);
        btnCancel = (Button) view.findViewById(R.id.btnCancel);

        conn = (Connector) getActivity();

        btnConnect.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        return view;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnCancel: {
                conn.onCancel();
                dismiss();
            }
            break;
            case R.id.btnConnect: {

                onDismiss();
                conn.onConnect();
            }
            break;
            default:
                break;
        }
    }

    public void onDismiss() {

//        getActivity().overridePendingTransition(R.anim.slide_in_left,
//                R.anim.slide_out_left);
        super.dismiss();
    }

}
