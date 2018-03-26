package com.mbhybird.elevatorinspection;

import android.content.DialogInterface;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BaseActivity extends AppCompatActivity {
    private long firstTime = 0;
    private static final boolean DEV_MODE = true;
    MyApplication myApp = null;

    public BaseActivity() {
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        myApp = (MyApplication) getApplication();

//        //back door
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        try {
//            Date expiredDate = sdf.parse("2017-11-25 00:00:00");
//            if (System.currentTimeMillis() >= expiredDate.getTime()) {
//                this.finish();
//                System.exit(0);
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 3000) {
                Toast.makeText(getApplicationContext(), "Quit?", Toast.LENGTH_SHORT).show();
                firstTime = System.currentTimeMillis();
                return true;
            } else {
                this.finish();
                System.exit(0);
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    public void showAlert(String message) {
        new AlertDialog.Builder(this)
                .setTitle("系统提示")
                .setMessage(message)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("返回", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    public int strToInt(String s) {
        return Integer.parseInt(s, 16);
    }

    public float strToFloat(String s) {
        return Float.parseFloat("0." + String.valueOf(strToInt(s)));
    }
}
