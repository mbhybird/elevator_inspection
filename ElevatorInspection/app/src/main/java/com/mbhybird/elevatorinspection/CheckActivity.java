package com.mbhybird.elevatorinspection;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class CheckActivity extends BaseActivity {
    boolean mPowerRecv = false;
    boolean mAccRecv = false;
    boolean mPluRecv = false;
    //String mLocalIPSegment = "";
    WifiManager mWifiManager;
    UDPThread ut = null;
    Handler mHandler = null;
    Runnable mRunnable = null;
    Runnable mRunableNext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        Button btnBack = (Button) findViewById(R.id.btnCheckBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CheckActivity.this, MainActivity.class));
                CheckActivity.this.finish();
            }
        });

        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        String localIPAddress = getLocalIPAddress();
//        if (localIPAddress != null) {
//            mLocalIPSegment = localIPAddress.substring(0, localIPAddress.lastIndexOf("."));
//        }

        final Button btnCommCheck = (Button) findViewById(R.id.btnCommCheck);
        final LinearLayout layoutPower = (LinearLayout) findViewById(R.id.layoutPower);
        final LinearLayout layoutAcc = (LinearLayout) findViewById(R.id.layoutAcc);
        final LinearLayout layoutPlu = (LinearLayout) findViewById(R.id.layoutPlu);
        final EditText etRevData = (EditText) findViewById(R.id.check_etRevData);
        final MyApplication myApp = (MyApplication) getApplication();
        final ProgressBar pb = (ProgressBar) findViewById(R.id.check_pb);
        final CheckBox chkPower = (CheckBox) findViewById(R.id.check_chkPower);
        final CheckBox chkAcc = (CheckBox) findViewById(R.id.check_chkAcc);
        final CheckBox chkPlu = (CheckBox) findViewById(R.id.check_chkPlu);

        mHandler = new Handler();
        mRunableNext = new Runnable() {
            @Override
            public void run() {
                ut.DisConnectSocket();
                for (String rd : myApp.RecDataList) {
                    Log.i("UDP", rd);
                    if (rd.equals("AA04010106")) {
                        mPowerRecv = true;
                    }
                    if (rd.equals("AA04020107")) {
                        mAccRecv = true;
                    }
                    if (rd.equals("AA04030108")) {
                        mPluRecv = true;
                    }
                }

                pb.setVisibility(View.INVISIBLE);
                if (mPowerRecv) {
                    layoutPower.setVisibility(View.VISIBLE);
                } else {
                    layoutPower.setVisibility(View.INVISIBLE);
                }

                if (mAccRecv) {
                    layoutAcc.setVisibility(View.VISIBLE);
                } else {
                    layoutAcc.setVisibility(View.INVISIBLE);
                }

                if (mPluRecv) {
                    layoutPlu.setVisibility(View.VISIBLE);
                } else {
                    layoutPlu.setVisibility(View.INVISIBLE);
                }
                //chkPower.setChecked(mPowerRecv);
                //chkAcc.setChecked(mAccRecv);
                //chkPlu.setChecked(mPluRecv);

                btnCommCheck.setEnabled(true);
            }
        };

        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (myApp.RecDataList.size() > 0) {
                    mHandler.postDelayed(mRunableNext, 500);
                } else {
                    mHandler.postDelayed(this, 100);
                }
            }
        };

        ut = new UDPThread(etRevData, myApp.RecDataList);
        ut.setRemoteIP("255.255.255.255");
        ut.setRemotePort(8432);
        ut.setLocalPort(8432);
        ut.setRHex(true);
        ut.setSHex(true);

        btnCommCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutPower.setVisibility(View.INVISIBLE);
                layoutAcc.setVisibility(View.INVISIBLE);
                layoutPlu.setVisibility(View.INVISIBLE);
                myApp.RecDataList.clear();
                mPowerRecv = false;
                mAccRecv = false;
                mPluRecv = false;

                if (mWifiManager.getWifiState() != 3) {
                    showAlert("WIFI未连接,请检查！");
                    return;
                }

                /*
                if (!mWifiManager.getConnectionInfo().getSSID().equals("\"shaowencai\"")) {
                    showAlert("WIFI连接不正确,请检查！(提示：WIFI名称为\"shaowencai\")");
                    return;
                }*/

                btnCommCheck.setEnabled(false);

                //check power,acc,plu
                if (ut.ConnectSocket()) {
                    pb.setVisibility(View.VISIBLE);
                    mHandler.post(mRunnable);
                    ut.SendData("5500040105");
                } else {
                    showAlert("连接失败，请检查网络设置！");
                }
            }
        });
    }

    private String getLocalIPAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            showAlert(ex.toString());
        }
        return null;
    }
}
