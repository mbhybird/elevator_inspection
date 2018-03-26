package com.mbhybird.elevatorinspection;

import android.os.Bundle;
import android.os.Process;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class UDPTestActivity extends BaseActivity implements OnClickListener, OnItemSelectedListener, OnCheckedChangeListener {

    private static final String[] codes = {"UTF-8", "GBK", "GB2312", "ISO-8895-1"};
    private EditText etremoteip;
    private EditText etremoteport;
    private EditText etlocalport;
    private EditText etrdata;
    private EditText etsdata;
    private EditText etlogfilepath;
    private CheckBox cbshex;
    private CheckBox cbrhex;
    private Spinner spscharcodes;
    private Spinner sprcharcodes;
    private Button btnconnect;
    private Button btndisconnect;
    private Button btnsend;
    private Button btnClear;

    private UDPThread ut = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_udptest);

        initUI();
    }

    private void initUI() {
        btnClear = (Button) findViewById(R.id.btnClear);
        etremoteip = (EditText) findViewById(R.id.etremoteip);
        etremoteport = (EditText) findViewById(R.id.etremoteport);
        etlocalport = (EditText) findViewById(R.id.etlocalport);
        etrdata = (EditText) findViewById(R.id.etrdata);
        etrdata.setMovementMethod(ScrollingMovementMethod.getInstance());
        etsdata = (EditText) findViewById(R.id.etsdata);
        cbshex = (CheckBox) findViewById(R.id.cbshex);
        cbrhex = (CheckBox) findViewById(R.id.cbrhex);
        cbshex.setChecked(true);
        cbrhex.setChecked(true);
        cbshex.setOnCheckedChangeListener(this);
        cbrhex.setOnCheckedChangeListener(this);
        btnconnect = (Button) findViewById(R.id.btnconnect);
        btnconnect.setOnClickListener(this);
        btndisconnect = (Button) findViewById(R.id.btndisconnect);
        btndisconnect.setEnabled(false);
        btndisconnect.setOnClickListener(this);
        btnsend = (Button) findViewById(R.id.btnsend);
        btnsend.setEnabled(false);
        btnsend.setOnClickListener(this);
        initCharCodes();

        ut = new UDPThread(etrdata,null);

        etsdata.setText("5501040207");
        btnClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                etrdata.getText().clear();
            }
        });
    }

    private void initCharCodes() {
        spscharcodes = (Spinner) findViewById(R.id.spscharcodes);
        sprcharcodes = (Spinner) findViewById(R.id.sprcharcodes);
        spscharcodes.setEnabled(false);
        sprcharcodes.setEnabled(false);
        spscharcodes.setOnItemSelectedListener(this);
        sprcharcodes.setOnItemSelectedListener(this);
        ArrayAdapter<String> itemvalues = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, codes);
        itemvalues.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spscharcodes.setAdapter(itemvalues);
        sprcharcodes.setAdapter(itemvalues);
    }

    private void uiState(boolean state) {
        btnconnect.setEnabled(state);
        btndisconnect.setEnabled(!state);
        btnsend.setEnabled(!state);
        etremoteip.setEnabled(state);
        etremoteport.setEnabled(state);
        etlocalport.setEnabled(state);
        cbshex.setEnabled(state);
        cbrhex.setEnabled(state);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(btnconnect)) {
            String RemoteIP = etremoteip.getText().toString().trim();
            int RemotePort = Integer.parseInt(etremoteport.getText().toString().trim());
            int LocalPort = Integer.parseInt(etlocalport.getText().toString().trim());
            ut.setRemoteIP(RemoteIP);
            ut.setRemotePort(RemotePort);
            ut.setLocalPort(LocalPort);
            ut.setRHex(cbrhex.isChecked());
            ut.setSHex(cbshex.isChecked());
            if (ut.ConnectSocket())
                uiState(false);
        }
        if (v.equals(btndisconnect)) {
            ut.DisConnectSocket();
            uiState(true);
        }
        if (v.equals(btnsend)) {
            String SData = etsdata.getText().toString().trim();
            if (!SData.trim().equals(""))
                ut.SendData(SData);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Process.killProcess(Process.myPid());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        Spinner tmp = (Spinner) parent;
        if (tmp.equals(sprcharcodes))
            ut.setCurrentRCodes(codes[position]);
        if (tmp.equals(spscharcodes))
            ut.setCurrentSCodes(codes[position]);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.equals(cbrhex))
            sprcharcodes.setEnabled(!cbrhex.isChecked());
        if (buttonView.equals(cbshex))
            spscharcodes.setEnabled(!cbshex.isChecked());
    }
}

