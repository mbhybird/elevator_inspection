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
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.FileUtils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BalanceThreeActivity extends BaseActivity {
    WifiManager mWifiManager;
    UDPThread ut = null;
    LineChart mUPChart;
    LineChart mDPChart;
    String mTestType;
    Button btnUPowerMon;
    Button btnDPowerMon;
    EditText etUDPower;
    ProgressBar pb;
    Button btnEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_three);

        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        Button btnBack = (Button) findViewById(R.id.btnBalanceThreeBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BalanceThreeActivity.this, BalanceTwoActivity.class));
                BalanceThreeActivity.this.finish();
            }
        });

        Button btnBalanceFour = (Button) findViewById(R.id.btnBalanceFour);
        btnBalanceFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BalanceThreeActivity.this, BalanceFourActivity.class));
                BalanceThreeActivity.this.finish();
            }
        });

        btnUPowerMon = (Button) findViewById(R.id.btnUPowerMon);
        btnDPowerMon = (Button) findViewById(R.id.btnDPowerMon);
        etUDPower = (EditText) findViewById(R.id.et_udPower);
        pb = (ProgressBar) findViewById(R.id.three_check_pb);
        btnEnd = (Button) findViewById(R.id.btnEnd);
        btnEnd.setEnabled(false);

        ut = new UDPThread(etUDPower, myApp.RecDataList);
        ut.setRemoteIP("255.255.255.255");
        ut.setRemotePort(8432);
        ut.setLocalPort(8432);
        ut.setRHex(true);
        ut.setSHex(true);

        mUPChart = (LineChart) findViewById(R.id.balance_three_chart_UPower);
        mUPChart.getDescription().setEnabled(false);
        mUPChart.setNoDataText("");
        mUPChart.setBackgroundColor(Color.WHITE);
        mUPChart.setDrawGridBackground(true);
        YAxis uLeftAxis = mUPChart.getAxisLeft();
        uLeftAxis.setAxisMaximum(20000f);
        uLeftAxis.setAxisMinimum(0f);
        mUPChart.getAxisRight().setEnabled(false);
        XAxis uXAxis = mUPChart.getXAxis();
        uXAxis.setEnabled(false);

        mDPChart = (LineChart) findViewById(R.id.balance_three_chart_DPower);
        mDPChart.getDescription().setEnabled(false);
        mDPChart.setDrawGridBackground(true);
        mDPChart.setBackgroundColor(Color.WHITE);
        mDPChart.setNoDataText("");
        YAxis dLeftAxis = mDPChart.getAxisLeft();
        dLeftAxis.setAxisMaximum(20000f);
        dLeftAxis.setAxisMinimum(0f);
        mDPChart.getAxisRight().setEnabled(false);
        XAxis dXAxis = mDPChart.getXAxis();
        dXAxis.setEnabled(false);

        btnUPowerMon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myApp.mUPower = 0f;
                myApp.mUPowerFactor = 0f;
                setTypeThenDraw("UP");

//                //for test
//                mTestType = "DOWN";
//                btnUPowerMon.setEnabled(false);
//                btnEnd.setEnabled(true);
//                btnDPowerMon.setEnabled(false);
//                etUDPower.getText().clear();
//                myApp.RecDataList.clear();
//                myApp.RecDataList.add("AA040102"+"000000"+"111111"+"111111111111111111");
//                myApp.RecDataList.add("AA040102"+"010000"+"111111"+"111111111111111111");
//                myApp.RecDataList.add("AA040102"+"010000"+"111111"+"111111111111111111");
//                myApp.RecDataList.add("AA040102"+"010000"+"111111"+"111111111111111111");
//                myApp.RecDataList.add("AA040102"+"010000"+"111111"+"111111111111111111");
//                myApp.RecDataList.add("AA040102"+"011000"+"111111"+"111111111111111111");
//                myApp.RecDataList.add("AA040102"+"022100"+"111111"+"111111111111111111");
//                myApp.RecDataList.add("AA040102"+"032110"+"111111"+"111111111111111111");
//                myApp.RecDataList.add("AA040102"+"032110"+"111111"+"111111111111111111");
            }
        });

        btnDPowerMon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myApp.mDPower = 0f;
                myApp.mDPowerFactor = 0f;
                setTypeThenDraw("DOWN");
            }
        });

        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnEnd.setEnabled(false);
                ut.SendData("5501040308");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ut.DisConnectSocket();
                        pb.setVisibility(View.INVISIBLE);
                        btnUPowerMon.setEnabled(true);
                        btnDPowerMon.setEnabled(true);
                        List<Entry> entries = new ArrayList<Entry>();
                        int i = 0;
                        int pos = 0;
                        if (mTestType == "UP") {
                            ArrayList<Float> data = new ArrayList<>();
                            for (String s : myApp.RecDataList) {
                                //totally 19 bytes
                                if (s.length() == 38 && s.startsWith("AA040102")) {
                                    //高位
                                    float hightVal = strToInt(s.substring(8, 10)) * 100;
                                    //中位
                                    float midVal = strToInt(s.substring(10, 12));
                                    //低位
                                    float lowVal = strToFloat(s.substring(12, 14));
                                    //增大3倍
                                    entries.add(new Entry(i, (hightVal + midVal + lowVal)));
                                    Log.i("Balance", hightVal + midVal + lowVal + "");
                                    i++;


                                    //功率因素
                                    //高位
                                    float hFactor = strToInt(s.substring(14, 16));
                                    //中位
                                    float mFactor = strToInt(s.substring(16, 18)) * 0.01f;
                                    //低位
                                    float lFactor = strToInt(s.substring(18, 20)) * 0.0001f;

                                    if(hightVal + midVal + lowVal > 0) {
                                        float compValue = hightVal + midVal + lowVal;
                                        myApp.writeLog(String.format("mUPower：%s", compValue));
                                        data.add(hightVal + midVal + lowVal);
                                    }
//                                    if (hightVal + midVal + lowVal > 0) {
//                                        pos++;
//                                        //取5个后的最大值
//                                        if (pos > 5) {
//                                            float compValue = hightVal + midVal + lowVal;
//                                            myApp.writeLog(String.format("mUPower:%s", compValue));
//                                            myApp.writeLog(String.format("mUPowerFactor:%s", hFactor + mFactor + lFactor));
//                                            if (myApp.mUPower < compValue) {
//                                                myApp.mUPower = compValue;
//                                                myApp.mUPowerFactor = hFactor + mFactor + lFactor;
//                                                mUPChart.getAxisLeft().setAxisMaximum(myApp.mUPower);
//                                            }
//                                        }
//
//                                        Log.i("Balance-UPowerFactor", myApp.mUPowerFactor + "");
//                                    }
                                }
                            }

                            if (entries.size() > 0) {
                                try {
                                    myApp.mUPower = GetPowerAlgorithm(data);
                                    myApp.writeLog(String.format("mUPower(avg)：%s", myApp.mUPower));
                                    Collections.sort(data);
                                    if(data.size() > 0) {
                                        mUPChart.getAxisLeft().setAxisMaximum(data.get(data.size() - 1));
                                    }
                                    mUPChart.setData(generateLineData("上行功率测量", entries));
                                    mUPChart.animateX(1000);
                                }catch (Exception e){
                                    myApp.writeLog(e.toString());
                                }
                            }


                        } else {
                            ArrayList<Float> data = new ArrayList<>();
                            for (String s : myApp.RecDataList) {
                                //totally 19 bytes
                                if (s.length() == 38 && s.startsWith("AA040102")) {
                                    //高位
                                    float hightVal = strToInt(s.substring(8, 10)) * 100;
                                    //中位
                                    float midVal = strToInt(s.substring(10, 12));
                                    //低位
                                    float lowVal = strToFloat(s.substring(12, 14));
                                    //增大3倍
                                    entries.add(new Entry(i, (hightVal + midVal + lowVal)));
                                    Log.i("Balance", hightVal + midVal + lowVal + "");
                                    i++;

                                    //功率因素
                                    //高位
                                    float hFactor = strToInt(s.substring(14, 16));
                                    //中位
                                    float mFactor = strToInt(s.substring(16, 18)) * 0.01f;
                                    //低位
                                    float lFactor = strToInt(s.substring(18, 20)) * 0.0001f;

                                    if(hightVal + midVal + lowVal > 0) {
                                        float compValue = hightVal + midVal + lowVal;
                                        myApp.writeLog(String.format("mDPower：%s", compValue));
                                        data.add(hightVal + midVal + lowVal);
                                    }
//                                    if (hightVal + midVal + lowVal > 0) {
//                                        pos++;
//                                        //取5个后的最大值
//                                        if (pos > 5) {
//                                            float compValue = hightVal + midVal + lowVal;
//                                            myApp.writeLog(String.format("mDPower:%s", compValue));
//                                            myApp.writeLog(String.format("mDPowerFactor:%s", hFactor + mFactor + lFactor));
//                                            if (myApp.mDPower < compValue) {
//                                                myApp.mDPower = compValue;
//                                                myApp.mDPowerFactor = hFactor + mFactor + lFactor;
//                                                mDPChart.getAxisLeft().setAxisMaximum(myApp.mDPower);
//                                            }
//                                        }
//
//                                        Log.i("Balance-DPowerFactor", myApp.mDPowerFactor + "");
//                                    }
                                }
                            }
                            if (entries.size() > 0) {
                                try {
                                    myApp.mDPower = GetPowerAlgorithm(data);
                                    myApp.writeLog(String.format("mDPower(avg)：%s", myApp.mDPower));
                                    Collections.sort(data);
                                    if(data.size() > 0) {
                                        mDPChart.getAxisLeft().setAxisMaximum(data.get(data.size() - 1));
                                    }
                                    mDPChart.setData(generateLineData("下行功率测量", entries));
                                    mDPChart.animateX(1000);
                                }catch (Exception e){
                                    myApp.writeLog(e.toString());
                                }
                            }
                        }
                        //btnEnd.setEnabled(true);
                    }
                }, 3000);
            }
        });
    }

    private void setTypeThenDraw(String type) {
        if (mWifiManager.getWifiState() != 3) {
            showAlert("WIFI未连接,请检查！");
            return;
        }

        /*
        if (!mWifiManager.getConnectionInfo().getSSID().equals("\"shaowencai\"")) {
            showAlert("WIFI连接不正确,请检查！(提示：WIFI名称为\"shaowencai\")");
            return;
        }*/

        mTestType = type;
        btnUPowerMon.setEnabled(false);
        btnEnd.setEnabled(true);
        btnDPowerMon.setEnabled(false);
        etUDPower.getText().clear();
        myApp.RecDataList.clear();
        if (ut.ConnectSocket()) {
            pb.setVisibility(View.VISIBLE);
            ut.SendData("5501040207");
        } else {
            showAlert("连接失败，请检查网络设置！");
        }
    }

    private float GetPowerAlgorithm(ArrayList<Float> data) {
        ArrayList<Float> target = new ArrayList<>();
        float sum = 0f;  //所有元素之和(>=500f)
        float average = 0f;  //平均值
        float phase = 0f;
        float temp = 0f;
        int total = 0;
        int avgIndex = 0;
        int midIndex = 0;
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i) >= 500) {
                //System.out.println(data.get(i));
                myApp.writeLog(String.format("功率(>=500)：%s", data.get(i)));
                target.add(data.get(i));
//                sum += data.get(i);
                total++;
            }
        }

        if (total >= 11) {
//            average = sum / total;
//            Collections.sort(target);
            /*
            System.out.println("总和:" + sum);
            System.out.println("总数:" + total);
            System.out.println("平均:" + average);
            for (int i = 0; i < target.size(); i++) {
                System.out.println(target.get(i));
            }*/

//            myApp.writeLog(String.format("总和：%s", sum));
//            myApp.writeLog(String.format("总数：%s", total));
//            myApp.writeLog(String.format("平均：%s", average));
//
//            phase = Math.abs(average - target.get(0));
//            for (int i = 0; i < target.size(); i++) {
//                if (phase > Math.abs(average - target.get(i))) {
//                    phase = Math.abs(average - target.get(i));  //差值的绝对值
//                    temp = target.get(i);
//                    avgIndex = i;
//                }
//            }

            /*
            System.out.println("最接近其平均值的数字是：" + temp);
            System.out.println("最接近其平均值的索引是：" + avgIndex);
            System.out.println("目标队列：");*/

//            myApp.writeLog(String.format("最接近其平均值的数字是：%s", temp));
//            myApp.writeLog(String.format("最接近其平均值的索引是：%s", avgIndex));

            midIndex = (int)Math.floor(total / 2f);
            myApp.writeLog(String.format("中间值的索引是：%s", midIndex));
            myApp.writeLog("目标队列：");

            sum = 0f;
            average = 0f;
            total = 0;
            for (int i = midIndex - 5; i <= midIndex + 5; i++) {
                //System.out.println(target.get(i));
                if (i >= 0 && i <= target.size() - 1) {
                    sum += target.get(i);
                    total++;
                    myApp.writeLog(String.format("%s", target.get(i)));
                }
            }

            average = sum / total;

            //System.out.println("目标队列平均值：" + String.format("%.3f", average));
            myApp.writeLog("目标队列总和：" + sum);
            myApp.writeLog("目标队列总数：" + total);
            myApp.writeLog("目标队列平均值：" + String.format("%.3f", average));

            return Float.parseFloat(String.format("%.3f", average));
        } else {
            //System.out.println("数据队列不满足要求！");
            myApp.writeLog("数据队列不满足要求！");
            return -1;
        }
    }

    protected LineData generateLineData(String label, List<Entry> entries) {

        ArrayList<ILineDataSet> sets = new ArrayList<ILineDataSet>();
        LineDataSet ds1 = new LineDataSet(entries, label);
        //LineDataSet ds2 = new LineDataSet(FileUtils.loadEntriesFromAssets(BalanceThreeActivity.this.getAssets(), "sine.txt"), "Cosine function");

        //ds1.setMode(LineDataSet.Mode.LINEAR);

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
