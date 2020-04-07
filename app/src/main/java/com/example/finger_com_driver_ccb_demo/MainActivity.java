package com.example.finger_com_driver_ccb_demo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.zz.finger.FingerDevice;

import org.zz.jni.zzFingerAlg;

import java.io.UnsupportedEncodingException;

public class MainActivity extends Activity {

    private static final int PROMTP_MSG = 0;    // 提示信息
    private static final int SUCCESS_MSG = 1;    // 成功
    private static final int FAILED_MSG = 2;    // 失败
    private static final String PORT_NAME = "/dev/ttyMT1";
    private static final int COM_BAUD_RATE = 9600;
    private static final int FINGER_WAIT_TIME_OUT = 10 * 1000;

    //中正算法库
    static {
        System.loadLibrary("FingerAlg");
    }

    byte[] m_B64_bTemplate = new byte[400];
    byte[] m_B64_bFeature = new byte[400];
    int i = 0;
    private Button btnReadFeature, btnReadTemplate, btnReadFingerMatch, btnReadVersion = null;
    private EditText edtPortName, edtBaudrate;
    private FingerDevice fingerDev = null;
    private zzFingerAlg fingerAlg = null;
    private View.OnTouchListener myTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Button upStepBtn = (Button) v;
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                upStepBtn.setBackgroundResource(R.drawable.button_down);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                upStepBtn.setBackgroundResource(R.drawable.button_up);
            }
            return false;
        }
    };
    private Handler LinkDetectedHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PROMTP_MSG:
                    ShowMessage("" + msg.obj, true);
                    break;
                case SUCCESS_MSG:
                case FAILED_MSG:
                    ShowMessage("" + msg.obj, true);
                    break;
                default:
                    ShowMessage("" + msg.obj, true);
                    break;
            }
        }
    };

    public static void scrollToBottom(final View scroll, final View inner) {
        Handler mHandler = new Handler();
        mHandler.post(new Runnable() {
            public void run() {
                if (scroll == null || inner == null) {
                    return;
                }
                int offset = inner.getMeasuredHeight() - scroll.getHeight();
                if (offset < 0) {
                    offset = 0;
                }
                scroll.scrollTo(0, offset);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        fingerDev = new FingerDevice();
        fingerAlg = new zzFingerAlg();

        btnReadVersion = (Button) findViewById(R.id.btnReadVersion);
        btnReadFeature = (Button) findViewById(R.id.btnReadFeature);
        btnReadTemplate = (Button) findViewById(R.id.btnReadTemplage);
        btnReadFingerMatch = (Button) findViewById(R.id.btnFingerMatch);

        edtPortName = (EditText) findViewById(R.id.edit_port_name);
        edtBaudrate = (EditText) findViewById(R.id.edit_baud_rate);

        //  实现按钮按下及弹起时的效果
        btnReadVersion.setOnTouchListener(myTouchListener);
        btnReadFeature.setOnTouchListener(myTouchListener);
        btnReadTemplate.setOnTouchListener(myTouchListener);
        btnReadFingerMatch.setOnTouchListener(myTouchListener);


    }

    @Override
    protected void onResume() {
        super.onResume();
        Power.getIntance().powerOn();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Power.getIntance().powerOff();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void SendMsg(int what, String obj) {
        Message message = new Message();
        message.what = what;
        message.obj = obj;
        message.arg1 = 0;
        LinkDetectedHandler.sendMessage(message);
    }

    /**
     * 提示信息
     */
    private void ShowMessage(String strMsg, Boolean bAdd) {
        EditText edit_show_msg = (EditText) findViewById(R.id.edit_show_msg);
        if (bAdd) {
            String strShowMsg = edit_show_msg.getText().toString();
            strMsg = strShowMsg + strMsg;
        }
        edit_show_msg.setText(strMsg + "\r\n");
        ScrollView scrollView_show_msg = (ScrollView) findViewById(R.id.scrollView_show_msg);

        //滚动条到最下面
        scrollToBottom(scrollView_show_msg, edit_show_msg);
    }

    /* Toast控件显示提示信息 */
    public void DisplayToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    public void OnClickDevVersion(View view) {
        int nResult = 0;
        byte[] devVersion = new byte[200];
        byte[] errMsg = new byte[200];

        try {
            nResult = fingerDev.GetDevVersion(
                    edtPortName.getText().toString(),
                    Integer.parseInt(edtBaudrate.getText().toString()),
                    devVersion.length, devVersion, errMsg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (nResult != 0) {
            try {
                SendMsg(PROMTP_MSG, "错误信息:[" + new String(errMsg, "GBK").trim() + "]");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return;
        }
        SendMsg(PROMTP_MSG, "设备版本:[" + new String(devVersion).trim() + "]");
    }

    public void OnClickReadFeature(View view) {
        int nResult = 0;
        byte[] errMsg = new byte[200];
        try {
            nResult = fingerDev.GetFeature(
                    edtPortName.getText().toString(),
                    Integer.parseInt(edtBaudrate.getText().toString()),
                    FINGER_WAIT_TIME_OUT, m_B64_bFeature, errMsg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (nResult != 0) {
            try {
                SendMsg(PROMTP_MSG, "错误信息:[" + new String(errMsg, "GBK").trim() + "]");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return;
        }
        String feat_b64 = new String(m_B64_bFeature).trim();
        SendMsg(PROMTP_MSG, "指纹特征:[" + feat_b64 + "],长度[" + feat_b64.length() + "]");
    }

    public void OnClickReadTemplate(final View view) {

        ShowMessage("同一指纹，需要采集三次生成模板", true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                int nResult = 0;
                byte[] errMsg = new byte[200];
                try {
                    nResult = fingerDev.GetTemplate(
                            edtPortName.getText().toString(),
                            Integer.parseInt(edtBaudrate.getText().toString()),
                            FINGER_WAIT_TIME_OUT, m_B64_bTemplate, errMsg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (nResult != 0) {
                    try {
                        SendMsg(PROMTP_MSG, "错误信息:[" + new String(errMsg, "GBK").trim() + "]");
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    return;
                }
                String temp_b64 = new String(m_B64_bTemplate).trim();
                SendMsg(PROMTP_MSG, "指纹模板:[" + temp_b64 + "],长度[" + temp_b64.length() + "]");

            }
        }).start();


    }

    public void OnClickFingerMatch(View view) {
        SendMsg(PROMTP_MSG, "指纹比对成功");
        int iRet = fingerAlg.mxFingerMatchBase64(m_B64_bFeature, m_B64_bTemplate, 3);
        if (iRet == 0) {
            SendMsg(PROMTP_MSG, "指纹比对成功");
        } else {
            SendMsg(PROMTP_MSG, "指纹比对失败");
        }
    }
}
