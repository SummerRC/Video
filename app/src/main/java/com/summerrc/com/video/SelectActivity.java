package com.summerrc.com.video;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class SelectActivity extends Activity implements View.OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        innitView();
    }

    private void innitView() {
        findViewById(R.id.bt_record).setOnClickListener(this);
        findViewById(R.id.bt_setting).setOnClickListener(this);
        findViewById(R.id.bt_viewing).setOnClickListener(this);
        findViewById(R.id.account).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_record:        //录制视频
                RecordActivity.startSelf(this);
                break;
            case R.id.bt_viewing:       //查看视频
                MediaTestActivity.startSelf(this);
                break;
            case R.id.bt_setting:       //设置
                DialogActivity.startSelf(this);
                break;
            case R.id.bt_account:       //拍照
                MyCameraActivity.startSelf(this);
                break;
        }
    }
}
