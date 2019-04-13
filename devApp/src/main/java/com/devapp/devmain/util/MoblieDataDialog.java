package com.devapp.devmain.util;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.devApp.R;


/**
 * Created by Upendra on 7/28/2016.
 */
public class MoblieDataDialog extends DialogFragment {

    MobileData mobileData;
    Button btnOk, btnCancel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.mobile_data_dialog, container, false);

        btnOk = (Button) view.findViewById(R.id.btnOn);
        btnCancel = (Button) view.findViewById(R.id.btnCancel);

        mobileData = (MobileData) getActivity();

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mobileData.switchOn();
                dismiss();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    public interface MobileData {

        void switchOn();

        void switchOff();

    }

}
