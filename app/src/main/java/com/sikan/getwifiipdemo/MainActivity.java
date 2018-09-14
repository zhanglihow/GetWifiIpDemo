package com.sikan.getwifiipdemo;

import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity implements NsdHelper.NsdFoundListener {

    private NsdHelper mNsdHelper;

    private int okNum;
    private int errorNum;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNsdHelper = new NsdHelper(this,this);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNsdHelper.discoverServices();
            }
        });

        mHandler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                mNsdHelper.discoverServices();
                Log.e("Handler","开始监听 成功："+okNum+"次 失败："+errorNum+"次");
                return false;
            }
        });
    }

    @Override
    protected void onPause() {
        if (mNsdHelper != null) {
            mNsdHelper.stopDiscovery();
        }
        super.onPause();
    }

    @Override
    public void foundWifi(NsdServiceInfo mService) {
        okNum++;
        Log.e("foundWifi",mService.toString());
        mNsdHelper.stopDiscovery();
//        mHandler.sendEmptyMessageDelayed(1,500);
    }

    @Override
    public void foundWifiError(NsdServiceInfo mService) {
        errorNum++;
        Log.e("foundWifiError",mService.toString());
        mNsdHelper.stopDiscovery();
//        mHandler.sendEmptyMessageDelayed(1,500);
    }
}
