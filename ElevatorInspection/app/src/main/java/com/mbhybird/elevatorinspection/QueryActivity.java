package com.mbhybird.elevatorinspection;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import java.io.FileNotFoundException;

public class QueryActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);

        Button btnBack = (Button) findViewById(R.id.btnQueryBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(QueryActivity.this, MainActivity.class));
                QueryActivity.this.finish();
            }
        });

        final EditText etNumber = (EditText) findViewById(R.id.etQueryByNo);
        final EditText etDate = (EditText) findViewById(R.id.etQueryByDate);

        Button btnQueryByDate = (Button) findViewById(R.id.btnQueryByDate);
        btnQueryByDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(etDate.getText().toString())) {
                    try {
                        if (ACache.get(QueryActivity.this).get(etDate.getText().toString()) != null) {
                            showAlert("电梯平衡系数为：" + String.valueOf(ACache.get(QueryActivity.this).getAsObject(etDate.getText().toString())));
                        } else {
                            showAlert("找不到记录!");
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
//                startActivity(new Intent(QueryActivity.this, QueryResultActivity.class));
//                QueryActivity.this.finish();
            }
        });

        Button btnQueryByNo = (Button) findViewById(R.id.btnQueryByNo);
        btnQueryByNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(etNumber.getText().toString())) {
                    try {
                        if (ACache.get(QueryActivity.this).get(etNumber.getText().toString()) != null) {
                            showAlert("电梯平衡系数为：" + String.valueOf(ACache.get(QueryActivity.this).getAsObject(etNumber.getText().toString())));
                        } else {
                            showAlert("找不到记录!");
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
//                startActivity(new Intent(QueryActivity.this, QueryResultActivity.class));
//                QueryActivity.this.finish();
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
