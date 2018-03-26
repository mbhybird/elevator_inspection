package com.mbhybird.elevatorinspection;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.List;

public class BalanceFourActivity extends BaseActivity {
    WifiManager mWifiManager;
    UDPThread ut = null;
    LineChart mUSPChart;
    LineChart mDSPChart;
    Button btnEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_four);

        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        Button btnBack = (Button) findViewById(R.id.btnBalanceFourBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BalanceFourActivity.this, BalanceThreeActivity.class));
                BalanceFourActivity.this.finish();
            }
        });

        Button btnBalanceFive = (Button) findViewById(R.id.btnBalanceFive);
        btnBalanceFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BalanceFourActivity.this, BalanceFiveActivity.class));
                BalanceFourActivity.this.finish();
            }
        });

        final Button btnSpeedTest = (Button) findViewById(R.id.balance_four_btnSpeedTest);
        final EditText etUDPower = (EditText) findViewById(R.id.four_et_udPower);
        final ProgressBar pb = (ProgressBar) findViewById(R.id.four_check_pb);

        ut = new UDPThread(etUDPower, myApp.RecDataList);
        ut.setRemoteIP("255.255.255.255");
        ut.setRemotePort(8432);
        ut.setLocalPort(8432);
        ut.setRHex(true);
        ut.setSHex(true);

        mUSPChart = (LineChart) findViewById(R.id.balance_four_chart_USpeed);
        mUSPChart.getDescription().setEnabled(false);
        mUSPChart.setDrawGridBackground(true);
        YAxis uLeftAxis = mUSPChart.getAxisLeft();
        uLeftAxis.setAxisMaximum(6f);
        uLeftAxis.setAxisMinimum(0f);
        mUSPChart.getAxisRight().setEnabled(false);
        XAxis uXAxis = mUSPChart.getXAxis();
        uXAxis.setEnabled(false);
        mUSPChart.setNoDataText("");
        mUSPChart.setBackgroundColor(Color.WHITE);
        mUSPChart.getAxisLeft().setDrawGridLines(false);

        mDSPChart = (LineChart) findViewById(R.id.balance_four_chart_DSpeed);
        mDSPChart.getDescription().setEnabled(false);
        mDSPChart.setDrawGridBackground(true);
        YAxis dLeftAxis = mDSPChart.getAxisLeft();
        dLeftAxis.setAxisMaximum(0f);
        dLeftAxis.setAxisMinimum(-6f);
        mDSPChart.getAxisRight().setEnabled(false);
        XAxis dXAxis = mDSPChart.getXAxis();
        dXAxis.setEnabled(false);
        mDSPChart.setNoDataText("");
        mDSPChart.setBackgroundColor(Color.WHITE);
        mDSPChart.getAxisLeft().setDrawGridLines(false);

        btnSpeedTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mWifiManager.getWifiState() != 3) {
                    showAlert("WIFI未连接,请检查！");
                    return;
                }

                /*
                if (!mWifiManager.getConnectionInfo().getSSID().equals("\"shaowencai\"")) {
                    showAlert("WIFI连接不正确,请检查！(提示：WIFI名称为\"shaowencai\")");
                    return;
                }*/

                btnSpeedTest.setEnabled(false);
                btnEnd.setEnabled(true);
                etUDPower.getText().clear();
                myApp.RecDataList.clear();
//                //for test
//                myApp.TestSpeedMethod = "在轿厢测量速度";
//                myApp.RecDataList.add("AA040102"+"111111111111"+"03220322"+"03110311"+"11");
                if (ut.ConnectSocket()) {
                    pb.setVisibility(View.VISIBLE);
                    if (myApp.TestSpeedMethod.equals("在轿厢测量速度")) {
                        ut.SendData("5502040208");
                        //ut.SendData("5501040207");
                    } else {
                        ut.SendData("5503040209");
                        //ut.SendData("5501040207");
                    }
                } else {
                    showAlert("连接失败，请检查网络设置！");
                }
            }
        });

        btnEnd = (Button) findViewById(R.id.balance_four_btnEnd);
        btnEnd.setEnabled(false);

        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnEnd.setEnabled(false);
                if (myApp.TestSpeedMethod.equals("在轿厢测量速度")) {
                    //ut.SendData("5502040308");
                } else {
                    //ut.SendData("5503040309");
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ut.DisConnectSocket();
                        pb.setVisibility(View.INVISIBLE);
                        btnSpeedTest.setEnabled(true);

                        List<Entry> entriesU = new ArrayList<Entry>();
                        List<Entry> entriesD = new ArrayList<Entry>();

                        int i = 0;

                        entriesU.add(new Entry(i, 0f));
                        entriesD.add(new Entry(i, 0f));

                        for (String s : myApp.RecDataList) {
                            //totally 19 bytes
                            if (s.length() == 38) {
                                for (int j = 0; j <= 4; j++) {
                                    i++;
                                    if (myApp.TestSpeedMethod.equals("在轿厢测量速度")) {
                                        //加速度上行
                                        //高位
                                        float uHightVal = strToInt(s.substring(28, 30));
                                        //低位
                                        float uLowVal = strToFloat(s.substring(30, 32));
                                        entriesU.add(new Entry(i, (uHightVal + uLowVal)));
                                        Log.i("BalanceU", "" + (uHightVal + uLowVal));

                                        //加速度下行
                                        //高位
                                        float dHightVal = strToInt(s.substring(32, 34));
                                        //低位
                                        float dLowVal = strToFloat(s.substring(34, 36));
                                        entriesD.add(new Entry(i, -(dHightVal + dLowVal)));
                                        Log.i("BalanceD", dHightVal + dLowVal + "");

                                        myApp.mUSpeed = uHightVal + uLowVal;
                                        myApp.mDSpeed = dHightVal + dLowVal;

                                        myApp.writeLog(String.format("AC_mUSpeed:%s", myApp.mUSpeed));
                                        myApp.writeLog(String.format("AC_mDSpeed:%s", myApp.mDSpeed));

                                        LimitLine uLimitLine = new LimitLine(myApp.mUSpeed,""+myApp.mUSpeed);
                                        uLimitLine.setLineColor(Color.DKGRAY);
                                        uLimitLine.setTextColor(Color.DKGRAY);
                                        uLimitLine.enableDashedLine(10f, 5f, 0f);
                                        mUSPChart.getAxisLeft().addLimitLine(uLimitLine);

                                        LimitLine dLimitLine = new LimitLine(-myApp.mDSpeed,""+myApp.mDSpeed);
                                        dLimitLine.setLineColor(Color.DKGRAY);
                                        dLimitLine.setTextColor(Color.DKGRAY);
                                        dLimitLine.enableDashedLine(10f, 5f, 0f);
                                        mDSPChart.getAxisLeft().addLimitLine(dLimitLine);

                                    } else {
                                        //脉冲上行
                                        //高位
                                        float uHightVal = strToInt(s.substring(20, 22));
                                        //低位
                                        float uLowVal = strToFloat(s.substring(22, 24));
                                        entriesU.add(new Entry(i, (uHightVal + uLowVal)));
                                        Log.i("BalanceU", "" + (uHightVal + uLowVal));

                                        //脉冲下行
                                        //高位
                                        float dHightVal = strToInt(s.substring(24, 26));
                                        //低位
                                        float dLowVal = strToFloat(s.substring(26, 28));
                                        entriesD.add(new Entry(i, -(dHightVal + dLowVal)));
                                        Log.i("BalanceD", dHightVal + dLowVal + "");

                                        myApp.mUSpeed = uHightVal + uLowVal;
                                        myApp.mDSpeed = dHightVal + dLowVal;

                                        myApp.writeLog(String.format("MC_mUSpeed:%s", myApp.mUSpeed));
                                        myApp.writeLog(String.format("MC_mDSpeed:%s", myApp.mDSpeed));

                                        LimitLine uLimitLine = new LimitLine(myApp.mUSpeed,""+myApp.mUSpeed);
                                        uLimitLine.setLineColor(Color.DKGRAY);
                                        uLimitLine.setTextColor(Color.DKGRAY);
                                        mUSPChart.getAxisLeft().addLimitLine(uLimitLine);

                                        LimitLine dLimitLine = new LimitLine(-myApp.mDSpeed,""+myApp.mDSpeed);
                                        dLimitLine.setLineColor(Color.DKGRAY);
                                        dLimitLine.setTextColor(Color.DKGRAY);
                                        mDSPChart.getAxisLeft().addLimitLine(dLimitLine);
                                    }
                                }
                            }
                        }

                        entriesU.add(new Entry(i+1, 0f));
                        entriesD.add(new Entry(i+1, 0f));

                        mUSPChart.setData(generateLineData("速度上行", entriesU));
                        mUSPChart.animateX(1000);

                        mDSPChart.setData(generateLineData("速度下行", entriesD));
                        mDSPChart.animateX(1000);

                        //btnEnd.setEnabled(true);
                    }
                }, 3000);
            }
        });
    }

    protected LineData generateLineData(String label, List<Entry> entries) {

        ArrayList<ILineDataSet> sets = new ArrayList<ILineDataSet>();
        LineDataSet ds1 = new LineDataSet(entries, label);
        //LineDataSet ds2 = new LineDataSet(FileUtils.loadEntriesFromAssets(getActivity().getAssets(), "cosine.txt"), "Cosine function");

        ds1.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        ds1.setLineWidth(3f);
        //ds2.setLineWidth(2f);

        ds1.setDrawCircles(false);
        //ds2.setDrawCircles(false);

        ds1.setColor(Color.RED);
        //ds2.setColor(ColorTemplate.VORDIPLOM_COLORS[1]);

        ds1.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                //return "" + value;
                return "";
            }
        });

        // load DataSets from textfiles in assets folders
        sets.add(ds1);
        //sets.add(ds2);

        LineData d = new LineData(sets);
        //d.setValueTypeface(tf);
        return d;
    }
}
