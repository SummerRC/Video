package com.summerrc.com.video;

import android.app.Activity;
import android.os.Bundle;

import java.io.File;
import java.util.ArrayList;

public class ViewActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
    }

    /**
     * 遍历sdcard目录下的所有文件
     *
     * @param path
     * @return
     */
    private ArrayList<String> doSearch(String path) {
        ArrayList<String> fileTempList = new ArrayList<String>();
        File file = new File(path);

        if (file.exists()) {
            if (file.isDirectory()) {
                File[] fileArray = file.listFiles();
                for (File f : fileArray) {
                    if (f.isDirectory()) {
                        doSearch(f.getPath());
                    } else {
                        if (f.getName().endsWith("mp4")
                                || f.getName().endsWith("jpeg")
                                || f.getName().endsWith("bmp")
                                || f.getName().endsWith("gif")) {
                            fileTempList.add(f.getAbsolutePath());
                        }
                    }
                }
            }
        }

        return fileTempList;
    }


}
