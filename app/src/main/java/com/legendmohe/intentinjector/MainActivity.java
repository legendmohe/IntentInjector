package com.legendmohe.intentinjector;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @InjectIntent("int")
    private int mInt;
    @InjectIntent("boolean")
    public Boolean mBoolean;
    @InjectIntent("object")
    private MainActivity.DummyItem mDummyItem;

    @InjectIntent("string")
    public void onInjectString(String data) {
        Log.d(TAG, "onInjectString() called with: " + "data = [" + data + "]");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.inject_activity_button);
        assert button != null;

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SubActivity.class);
                intent.putExtra("string", "string");
                intent.putExtra("int", 12345);
                intent.putExtra("boolean", true);
                intent.putExtra("object", new DummyItem("dummy"));
                startActivity(intent);
            }
        });

        button = (Button) findViewById(R.id.inject_object_button);
        assert button != null;

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("string", "string");
                intent.putExtra("int", 12345);
                intent.putExtra("boolean", true);
                intent.putExtra("object", new DummyItem("dummy"));

                IntentInjector.inject(MainActivity.this, intent);
            }
        });
    }

    public static class DummyItem implements Parcelable {
        String name;

        public DummyItem(String name) {
            this.name = name;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.name);
        }

        public DummyItem() {
        }

        protected DummyItem(Parcel in) {
            this.name = in.readString();
        }

        public static final Parcelable.Creator<DummyItem> CREATOR = new Parcelable.Creator<DummyItem>() {
            @Override
            public DummyItem createFromParcel(Parcel source) {
                return new DummyItem(source);
            }

            @Override
            public DummyItem[] newArray(int size) {
                return new DummyItem[size];
            }
        };
    }
}
