package com.summerrc.com.video;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Video;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public class MediaTestActivity extends Activity {

    private final int Refresh_Menu_ID = Menu.FIRST + 1;
    private final int Exit_Menu_ID = Menu.FIRST + 2;
    private static final String VIDEO_FILE_PATH = "/sdcard/";
    private Gallery thumbnailG;
    private MenuItem refresh, exit;
    private Bundle bundle;
    private ArrayList<Map<Bitmap, String>> thumbnailList;
    private ProgressDialog dialog;
    private VideoView vv;
    private TextView tv_content;
    private boolean shouldRun = true;
    private List<String> list = new ArrayList<>();
    private int n = 0;
    private MyHandler myHandler;

    public static void startSelf(Context context) {
        context.startActivity(new Intent(context, MediaTestActivity.class));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = savedInstanceState;
        setContentView(R.layout.main1);
        myHandler = new MyHandler(this);

        tv_content = (TextView) findViewById(R.id.tv_content);
        thumbnailG = (Gallery) findViewById(R.id.thumbnail);
        vv = (VideoView) findViewById(R.id.vv);
        thumbnailList = getVideoThumbnail();
        if (thumbnailList == null || thumbnailList.size() == 0) {
            Log.e("error", "thumbnailList is null");
        } else {
            ImageAdapter adapter = new ImageAdapter(MediaTestActivity.this,
                    thumbnailList);
            Log.e("Count", "count =  " + adapter.getCount());
            thumbnailG.setAdapter(adapter);
            thumbnailG.setOnItemClickListener(listener);
        }
        registerForContextMenu(thumbnailG);

        list.add("公鸡下的蛋能吃吗？");
        list.add("先有鸡先有蛋？");
        list.add("更喜欢媳妇更喜欢妈？");
        list.add("三块钱的盒饭能吃吗？");
        list.add("喜欢夏雨吗？");
        list.add("面试官是gay吗？");
        list.add("你会选程序员当老婆吗？");
        list.add("你会跟面试官约会吗？");
    }

    OnItemClickListener listener = new OnItemClickListener() {
        @SuppressWarnings("unchecked")
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position,
                                long id) {
            Log.e("ItemSelected", "thumbnailList");
            Set<Entry<Bitmap, String>> set = thumbnailList.get(position).entrySet();
            String filename = "";
            for (Entry<Bitmap, String> entryObj : set) {
                filename = entryObj.getValue();
            }
            playVideo(MediaTestActivity.this, vv, filename);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        refresh = menu.add(0, this.Refresh_Menu_ID, 1, "Refresh");
        exit = menu.add(0, this.Exit_Menu_ID, 2, "Exit");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case Refresh_Menu_ID:
                onCreate(bundle);
                break;
            case Exit_Menu_ID:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // ����ڴ��е�������Ƶ������ͼ��Ϣ ,����ƵΪmp4 ���� 3gp ��ʽ����
    private ArrayList<Map<Bitmap, String>> getVideoThumbnail() {
        ArrayList<Map<Bitmap, String>> thumbnaiLlist = new ArrayList<Map<Bitmap, String>>();
        File folder = new File(VIDEO_FILE_PATH);
        File[] files = folder.listFiles();
        boolean is3gp = false, ismp4 = false, isf4v = false, ismp3 = false;
        for (int i = 0; i < files.length; i++) {
            is3gp = files[i].getName().toLowerCase().endsWith(".3gp");
            ismp4 = files[i].getName().toLowerCase().endsWith(".mp4");
            isf4v = files[i].getName().toLowerCase().endsWith(".f4v");
            ismp3 = files[i].getName().toLowerCase().endsWith(".mp3");
            if (is3gp || ismp4 || isf4v || ismp3) {
                Log.e("Files Name", "Files Name = " + files[i].getName());
                Map<Bitmap, String> map = new HashMap<Bitmap, String>();
                Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(
                        VIDEO_FILE_PATH + files[i].getName(),
                        Video.Thumbnails.MINI_KIND);
                String fileName = files[i].getName();
                map.put(bitmap, fileName);
                thumbnaiLlist.add(map);
            }
        }
        return thumbnaiLlist;
    }

    /*
     * Play the video what is your choose Context context VideoView vv String
     * videoName,your file name content file Type ,eg:(aaa.3pg or bbb.mp4)
     */
    private void playVideo(Context context, VideoView vv, String videoName) {
        if ("".equals(videoName) || videoName == null) {
            Toast.makeText(MediaTestActivity.this, "Can't find this video,Please Check it!", Toast.LENGTH_SHORT).show();
        } else {
            vv.setVideoURI(Uri.parse(VIDEO_FILE_PATH + videoName));
            vv.setMediaController(new MediaController(context));
            vv.requestFocus();
            vv.start();
            n = 0;
            postMessage();
        }
    }


    private static class MyHandler extends Handler {
        WeakReference<MediaTestActivity> weakReference;     //���е�ǰActivity�����������

        MyHandler(MediaTestActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MediaTestActivity activity = weakReference.get();
            if (activity == null) return;
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if(activity.n == 8) {
                        activity.n = 0;
                        return;
                    }
                    String str = activity.list.get(activity.n % 8);
                    activity.n++;
                    activity.tv_content.setText(str);
                    activity.postMessage();
                    break;
            }
        }
    }

    private void postMessage() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (shouldRun) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message msg = new Message();
                    msg.what = 1;
                    myHandler.sendMessage(msg);
                }
            }
        });
        thread.start();
    }
}
