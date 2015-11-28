package com.summerrc.com.video;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class SelectActivity extends Activity {
    private Button record;
    private Button setting;
    private Button view;
    private Button camera;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_select);
        /**
         * 录制按钮
         */
        record = (Button) findViewById(R.id.record);
        record.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectActivity.this, RecordActivity.class);
                startActivity(intent);
            }
        });

        /**
         * 设置按钮
         */
        setting = (Button) findViewById(R.id.setting);
        setting.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(SelectActivity.this, DialogActivity.class);
                startActivity(intent1);

            }
        });

        /**
         * 观看按钮
         */
        view = (Button) findViewById(R.id.viewing);
        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(SelectActivity.this, MediaTestActivity.class);
                startActivity(intent2);

            }
        });

        /**
         * 拍照按钮
         */
        camera = (Button) findViewById(R.id.account);
        camera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(SelectActivity.this, MyCameraActivity.class);
                startActivity(intent2);

            }
        });
    }

}
