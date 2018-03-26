package com.mbhybird.elevatorinspection;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Date;
import java.text.SimpleDateFormat;

public class BalanceOneActivity extends BaseActivity {

    static final String TAG = BalanceOneActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_one);

        final Spinner spMethod = (Spinner) findViewById(R.id.spMethod);

        Button btnBack = (Button) findViewById(R.id.btnBalanceOneBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BalanceOneActivity.this, FunctionActivity.class));
                BalanceOneActivity.this.finish();
            }
        });

        final EditText etLoad = (EditText) findViewById(R.id.one_et_load);
        final EditText etNumber = (EditText) findViewById(R.id.one_et_number);
        final EditText etManualSpeed = (EditText) findViewById(R.id.one_et_manualSpeed);

        Button btnBalanceOneOK = (Button) findViewById(R.id.btnBalanceOneOK);
        btnBalanceOneOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(etNumber.getText().toString())) {
                    showAlert("编号必须输入，请检查!");
                    return;
                }
                if (TextUtils.isEmpty(etLoad.getText().toString())) {
                    showAlert("载荷必须输入，请检查!");
                    return;
                }
                myApp.TestSpeedMethod = spMethod.getSelectedItem().toString();
                Log.i(TAG, myApp.TestSpeedMethod);
                myApp.mLoad = Float.valueOf(etLoad.getText().toString());
                myApp.writeLog(String.format("mLoad:%s", myApp.mLoad));

                if(myApp.mIsManualSpeed) {
                    if (TextUtils.isEmpty(etManualSpeed.getText().toString())) {
                        showAlert("额定速度必须输入，请检查!");
                        return;
                    }

                    float manualSpeed = Float.valueOf(etManualSpeed.getText().toString());
                    if (manualSpeed > 0) {
                        myApp.mManualSpeed = manualSpeed;
                        myApp.writeLog(String.format("mManualSpeed:%s", myApp.mManualSpeed));
                    } else {
                        showAlert("额定速度必须大于0，请检查!");
                        return;
                    }
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                myApp.mNumber = etNumber.getText().toString();
                myApp.mDate = sdf.format(new Date());
                ACache.get(BalanceOneActivity.this).put(myApp.mNumber, "");
                ACache.get(BalanceOneActivity.this).put(myApp.mDate, "");

                startActivity(new Intent(BalanceOneActivity.this, BalanceTwoActivity.class));
                BalanceOneActivity.this.finish();
            }
        });

        //init
        Spinner spType = (Spinner) findViewById(R.id.spType);
        Spinner spModel = (Spinner) findViewById(R.id.spModel);
        final Spinner spScale = (Spinner) findViewById(R.id.spScale);
        final ArrayAdapter spScaleAd = ArrayAdapter.createFromResource(this,
                R.array.scale, R.layout.custom_spiner_text_item);
        spScaleAd.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        spModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    myApp.mElevator = 1f;
                } else {
                    myApp.mElevator = 1f;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spMethod.setSelection(i);
                spScale.setEnabled(i == 0);
                if (i == 0) {
                    spScale.setAdapter(spScaleAd);
                } else {
                    spScale.setAdapter(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // 通过加载xml文件配置的数据源
        ArrayAdapter spTypeAd = ArrayAdapter.createFromResource(this,
                R.array.type, R.layout.custom_spiner_text_item);
        spTypeAd.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        spType.setAdapter(spTypeAd);

        ArrayAdapter spMethodAd = ArrayAdapter.createFromResource(this,
                R.array.method, R.layout.custom_spiner_text_item);
        spMethodAd.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        spMethod.setAdapter(spMethodAd);

        spMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    myApp.mIsManualSpeed = false;
                } else {
                    myApp.mIsManualSpeed = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter spModelAd = ArrayAdapter.createFromResource(this,
                R.array.model, R.layout.custom_spiner_text_item);
        spModelAd.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        spModel.setAdapter(spModelAd);

        spScale.setAdapter(spScaleAd);
        spScale.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    myApp.mTractProp = 1f;
                } else if (i == 1) {
                    myApp.mTractProp = 1f;
                } else {
                    myApp.mTractProp = 1f;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }
}
