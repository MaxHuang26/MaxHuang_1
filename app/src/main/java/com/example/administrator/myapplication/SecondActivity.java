package com.example.administrator.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.administrator.myapplication.view.LBarChartView;

public class SecondActivity extends AppCompatActivity {

    private boolean flag = true;
    private LBarChartView[] views = new LBarChartView[6];
    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        views[0] = findViewById(R.id.chart_0);
        views[1] = findViewById(R.id.chart_1);
        views[2] = findViewById(R.id.chart_2);
        views[3] = findViewById(R.id.chart_3);
        views[4] = findViewById(R.id.chart_4);
        views[5] = findViewById(R.id.chart_5);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                double[] datas = intent.getDoubleArrayExtra("data");
                for (int i = 0; i < 6; i++) {
                    views[i].addAData(datas[i]);
                }
            }
        };

        final Datas datas = (Datas) getIntent().getBundleExtra("dataBundle").getSerializable("datas");

        for (int i = 0; i < 6; i++) {
            views[i].setDatas(datas.getValuelist(i + 1), true);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction("ACTION_DATA_RECEIVED");
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mReceiver);
    }

//    class DataBroadcastReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            double[] datas = intent.getDoubleArrayExtra("data");
//            for (int i = 0; i < 6; i++) {
//                views[i].addAData(datas[i]);
//            }
//        }
//    }
//    //

}
