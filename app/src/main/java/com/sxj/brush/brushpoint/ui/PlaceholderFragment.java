package com.sxj.brush.brushpoint.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sxj.brush.brushpoint.R;
import com.sxj.brush.brushpoint.Service.InstallService;
import com.sxj.brush.brushpoint.model.DeviceInfomation;
import com.sxj.brush.brushpoint.model.FileDownload;
import com.sxj.brush.brushpoint.model.Install;
import com.sxj.brush.brushpoint.utils.LogUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment implements View.OnClickListener {
    private TextView sectionLabel;
    private TextView state;
    private TextView message;
    private EditText url_text;
    private Button startButton;
    private InfoReceiver receiver;
    private CheckBox checkBox;
    private String msg;
    private int testCount = 0;

    private StringBuilder stringBuilder = new StringBuilder();

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public PlaceholderFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        sectionLabel = (TextView) rootView.findViewById(R.id.section_label);
        startButton = (Button) rootView.findViewById(R.id.start);
        startButton.setOnClickListener(this);

        state = (TextView) rootView.findViewById(R.id.state);
        message = (TextView) rootView.findViewById(R.id.message);
        url_text = (EditText) rootView.findViewById(R.id.url_text);
        checkBox = (CheckBox)rootView.findViewById(R.id.checkBox);
        List<String> list = getTestData();// need create thread ??? when data is large.
        if (list.size() > 0) {
            url_text.setText(list.get(0));
        }

//        DeviceInfomation deviceInfomation = new DeviceInfomation();
//        deviceInfomation.setMac(DeviceInfomation.getLocalMacAddress(getActivity()));
//        deviceInfomation.setDeviceId(DeviceInfomation.getDeviceId(getActivity()));
//        deviceInfomation.setSn(DeviceInfomation.getSnNumber());
//        message.setText(deviceInfomation.toJson());

        if (msg != null) {
            message.setText(msg);
        }

        if (receiver == null) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(InstallService.MESSAGE_BROADCASE);
            receiver = new InfoReceiver();
            getActivity().registerReceiver(receiver, intentFilter);
        }
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (receiver == null) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(InstallService.MESSAGE_BROADCASE);
            receiver = new InfoReceiver();
            getActivity().registerReceiver(receiver, intentFilter);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (receiver != null) {
            getActivity().unregisterReceiver(receiver);
            receiver = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            getActivity().unregisterReceiver(receiver);
            receiver = null;
        }
    }

    private EditText getUrlText() {
        return (EditText) getView().findViewById(R.id.url_text);
    }

    private EditText getTestTimeText() {
        return (EditText) getView().findViewById(R.id.test_times);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start:
                Intent intent = new Intent(getActivity(), InstallService.class);
                intent.putExtra("url", getUrlText().getText().toString());
                String timesStr = getTestTimeText().getText().toString();
                testCount = Integer.parseInt(timesStr);
                String delayStr = ((EditText)getView().findViewById(R.id.delay)).getText().toString();
                long delay = 20000 ;
                try{
                    delay = Long.parseLong(delayStr);
                }catch(Exception e){}
                intent.putExtra("times", testCount);
                intent.putExtra("isUninstall",checkBox.isChecked());
                intent.putExtra("delay",delay);
                getActivity().startService(intent);
                startButton.setClickable(false);
                Toast.makeText(getActivity(),"已经添加任务到队列中，请注意查看下面结果",Toast.LENGTH_LONG).show();
                break;
        }
    }


    private List<String> getTestData() {

        List<String> list = new ArrayList<>();
        InputStream is = getActivity().getResources().openRawResource(R.raw.test);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String temp = null;
        try {
            while ((temp = br.readLine()) != null) {
                list.add(temp);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }


    public class InfoReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String info = intent.getStringExtra("msg");
            String progress = intent.getStringExtra("progress");
            if (progress != null) {
                info = info + progress;
            }
            if (message != null) {
                msg = info;
                message.setText("更多信息请见：" + LogUtil.LOG_PATH + "BrushPoint.log" + "\n" + info);
            }

            int testC = intent.getIntExtra("success",1);
            if(testC >= testCount){
                startButton.setClickable(true);
            }
        }
    }
}