package com.legendmohe.intentinjector;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class SubActivity extends AppCompatActivity {
    private static final String TAG = "SubActivity";

    @InjectIntent("int")
    private int mInt;
    @InjectIntent("boolean")
    public Boolean mBoolean;
    @InjectIntent("object")
    private MainActivity.DummyItem mDummyItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        IntentInjector.inject(this);
//        IntentInjector.inject(this, getIntent());
    }

    @InjectIntent("string")
    public void onInjectString(String data) {
        Log.d(TAG, "onInjectString() called with: " + "data = [" + data + "]");
    }
}
