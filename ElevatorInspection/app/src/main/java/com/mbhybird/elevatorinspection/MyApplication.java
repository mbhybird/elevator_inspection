package com.mbhybird.elevatorinspection;

import android.app.Application;
import android.os.Environment;
import android.os.StrictMode;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by NickChung on 29/08/2017.
 */

public class MyApplication extends Application {
    private static final boolean DEV_MODE = true;
    public List<String> RecDataList = new ArrayList<String>();
    public String TestSpeedMethod = "";
    public float mUPower = 0f;
    public float mUPowerFactor = 0f;
    public float mDPower = 0f;
    public float mDPowerFactor = 0f;
    public float mUSpeed = 0f;
    public float mDSpeed = 0f;
    public float mLoad = 0f;
    public String mNumber = "";
    public String mDate = "";
    public float mElevator = 1f;
    public boolean mIsManualSpeed = false;
    public float mManualSpeed = 1f;
    public float mTractProp = 1f;
    public String mLogDate = "";

    public float getBalanceFactor() {
        if (mLoad > 0) {

            writeLog("=======getBalanceFactor Start=========");
            writeLog(String.format("mUPower:%s", mUPower));
            writeLog(String.format("mUPowerFactor:%s", mUPowerFactor));
            writeLog(String.format("mDSpeed:%s", mDSpeed));
            writeLog(String.format("mDPower:%s", mDPower));
            writeLog(String.format("mDPowerFactor:%s", mDPowerFactor));
            writeLog(String.format("mUSpeed:%s", mUSpeed));
            writeLog(String.format("mLoad:%s", mLoad));
            writeLog(String.format("mIsManualSpeed:%s", mIsManualSpeed));
            writeLog(String.format("mManualSpeed:%s", mManualSpeed));
            writeLog(String.format("mTractProp:%s", mTractProp));

            //remove the power factor
            if (!mIsManualSpeed) {
                writeLog(String.format("(%s + %s) * %s * %s " +
                        "/ (2 * 9.81f * %s * %s)", mUPower, mDPower, mElevator, mTractProp, mLoad, mUSpeed));
            } else {
                writeLog(String.format("(%s + %s) * %s * %s " +
                        "/ (2 * 9.81f * %s * %s)", mUPower, mDPower, mElevator, mTractProp, mLoad, mManualSpeed));
            }

            writeLog("=======getBalanceFactor End=========");
            if (!mIsManualSpeed) {
                if (mUSpeed > 0) {
                    return (mUPower + mDPower) * mElevator * mTractProp
                            / (2 * 9.81f * mLoad * mUSpeed);
                } else {
                    return 0;
                }
            } else {
                return (mUPower + mDPower) * mElevator * mTractProp
                        / (2 * 9.81f * mLoad * mManualSpeed);
            }
        } else {
            return 0;
        }

    }

    public void writeLog(String content) {
        try {
            File file = new File(Environment.getExternalStorageDirectory(),
                    mLogDate + "-log.txt");
            FileOutputStream fos = new FileOutputStream(file, true);
            fos.write((content + "\r\n").getBytes());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MyApplication() {

        if (DEV_MODE) {
            StrictMode.setThreadPolicy(
                    new StrictMode
                            .ThreadPolicy
                            .Builder()
                            .detectDiskReads()
                            .detectDiskWrites()
                            .detectNetwork()
                            .penaltyLog()
                            .build());

            StrictMode.setVmPolicy(
                    new StrictMode
                            .VmPolicy
                            .Builder()
                            .detectLeakedSqlLiteObjects()
                            .detectLeakedClosableObjects()
                            .penaltyLog()
                            //.penaltyDeath()
                            .build());
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date curDate = new Date(System.currentTimeMillis());
            mLogDate = sdf.format(curDate);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);

    }
}
