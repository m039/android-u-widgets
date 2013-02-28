package com.m039.ut.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.m039.ut.library.widgets.USwitch;

public class UWidgetsDemoActivity extends Activity
{
    public static final String TAG = "m039-UWidgetsDemoActivity";

    Switch mASwitch = null;
    USwitch mUSwitch = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_uwidgets_demo);

        mASwitch = (Switch) findViewById(R.id.aswitch);
        if (mASwitch != null) {
            mASwitch.setOnCheckedChangeListener(mOnCheckedChangeListener);
        }

        mUSwitch = (USwitch) findViewById(R.id.uswitch);
        if (mUSwitch != null) {
            mUSwitch.setOnCheckedChangeListener(new USwitch.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChange (USwitch uswitch, boolean isChecked) {
                        Log.d(TAG, String.format("onCheckedChanged: uswitch = %s : isChecked = %s",
                                                 uswitch, isChecked));
                    }
                });
            mUSwitch.setChecked(false);
        }
    }

    CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener =
        new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged (CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, String.format("onCheckedChanged: buttonView = %s : isChecked = %s",
                                         buttonView, isChecked));

                if (mUSwitch != null) {
                    // mUSwitch.setChecked(isChecked);
                }
            }
        };
}
