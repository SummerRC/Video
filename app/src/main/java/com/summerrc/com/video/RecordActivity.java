package com.summerrc.com.video;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.io.RandomAccessFile;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class RecordActivity extends Activity {
    private static final String TAG = "MainActivity";
    private SurfaceView surfaceView;
    private MediaRecorder mediaRecorder;
    private boolean record;
    private File videoFile;
    private ProgressDialog progressDialog = null;                           // 上传进度条
    private Thread thread;
    private Camera camera = null;
    private Socket receiver;
    private MyHandler myHandler;
    private List<String> list = new ArrayList<>();
    private int n = 0;
    private boolean shouldRun = true;

    public static void startSelf(Context context) {
        context.startActivity(new Intent(context, RecordActivity.class));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();                                        // 得到窗口
        requestWindowFeature(Window.FEATURE_NO_TITLE);                      // 没有标题
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);        // 设置全屏
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);    // 屏幕高亮

        setContentView(R.layout.activity_record);

        myHandler = new MyHandler(this);

        mediaRecorder = new MediaRecorder();
        surfaceView = (SurfaceView) this.findViewById(R.id.surfaceView);
        /** 下面设置Surface不维护自己的缓冲区，而是等待屏幕的渲染引擎将内容推送到用户面前 */
        this.surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        this.surfaceView.getHolder().setFixedSize(800, 600);                // 设置分辨率

        ButtonClickListener listener = new ButtonClickListener();           // 修改这里切换传输
        Button stopButton = (Button) this.findViewById(R.id.stop);
        Button recordButton = (Button) this.findViewById(R.id.record);
        stopButton.setOnClickListener(listener);
        recordButton.setOnClickListener(listener);

        list.add("公鸡下的蛋能吃吗？");
        list.add("先有鸡先有蛋？");
        list.add("更喜欢媳妇更喜欢妈？");
        list.add("三块钱的盒饭能吃吗？");
        list.add("喜欢夏雨吗？");
        list.add("面试官是gay吗？");
        list.add("你会选程序员当老婆吗？");
        list.add("你会跟面试官约会吗？");

        this.findViewById(R.id.next).setVisibility(View.GONE);
        this.findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = list.get(n % 8);
                n++;
                ((TextView) findViewById(R.id.tv_content)).setText(str);
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    /**
     * 显示对话框的IP
     */
    @Override
    protected void onDestroy() {
        mediaRecorder.release();
        super.onDestroy();
    }

    /**
     * 实现Dialog的回调方法
     */
    private final class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                Toast.makeText(RecordActivity.this, R.string.sdcarderror, Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            try {
                switch (v.getId()) {
                    case R.id.record:
                        mediaRecorder.reset();
                        if (camera != null) {
                            camera.release();
                        }
                        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);    // 从照相机采集视频
                        Camera.Parameters parameters = camera.getParameters();
                        camera.setParameters(parameters);
                        camera.unlock();
                        mediaRecorder.setCamera(camera);
                        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//                        mediaRecorder.setVideoSize(480, 320);
//                        mediaRecorder.setVideoFrameRate(3);                           // 每秒3帧
                        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H263); // 设置视频编码方式
                        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                        videoFile = new File(Environment.getExternalStorageDirectory(),
                                System.currentTimeMillis() + ".MP4");
                        mediaRecorder.setPreviewDisplay(surfaceView.getHolder()
                                .getSurface());
                        mediaRecorder.setOutputFile(videoFile.getAbsolutePath());
                        mediaRecorder.prepare();                                        // 预期准备
                        mediaRecorder.start();                                          // 开始刻录
                        record = true;
                        postMessage();
                        break;

                    case R.id.stop:

                        break;
                }
            } catch (Exception e) {
                Toast.makeText(RecordActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                Log.e(TAG, e.toString());
            }
        }

    }

    /**
     * 实时上传到PC端(边录边传)
     *
     * @author Administrator 吕俊
     */
    private final class ButtonClickListener1 implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                Toast.makeText(RecordActivity.this, R.string.sdcarderror, Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                switch (v.getId()) {
                    case R.id.record:
                        if (record) {
                            return;
                        }
                        mediaRecorder.reset();
                        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);      // 从照相机采集视频
                        //mediaRecorder
                        //.setAudioSource(MediaRecorder.AudioSource.MIC);
                        mediaRecorder
                                .setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                        mediaRecorder.setVideoSize(480, 320);
                        mediaRecorder.setVideoFrameRate(3);                                  // 每秒3帧
                        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);   // 设置视频编码方式
                        //mediaRecorder
                        //		.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                        mediaRecorder.setPreviewDisplay(surfaceView.getHolder().getSurface());

                        // 实时上传的代码
                        receiver = new Socket("192.168.1.149", 9999);
                        ParcelFileDescriptor pfd = ParcelFileDescriptor.fromSocket(receiver);
                        mediaRecorder.setOutputFile(pfd.getFileDescriptor());
                        mediaRecorder.prepare();        // 预期准备
                        mediaRecorder.start();          // 开始刻录
                        record = true;
                        break;

                    case R.id.stop:

                        finish();

                        break;
                }
            } catch (Exception e) {
                Toast.makeText(RecordActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                Log.e(TAG, e.toString());
            }
        }

    }

    /**
     * 文件上传连接服务器的
     */
    class UploadTask implements Runnable {
        @Override
        public void run() {
            try {
                long videoLength = videoFile.length();
                progressDialog.setMax((int) videoLength);       // 得到文件的大小
                System.out.println("连接之前");
                String ip = StreamTool.readIP();                // 从配置文件里读取Ip
                Socket socket = new Socket(ip, 6789);
                OutputStream outStream = socket.getOutputStream();
                System.out.println("连接之后");
                System.out.println(videoFile);
                String head = "Content-Length=" + videoFile.length() + ";filename=" + videoFile.getName() + ";sourceid=\r\n";
                System.out.println(videoFile.getName());
                outStream.write(head.getBytes());
                PushbackInputStream inStream = new PushbackInputStream(socket.getInputStream());
                String response = StreamTool.readLine(inStream);
                System.out.println(response);
                String[] items = response.split(";");
                String position = items[1].substring(items[1].indexOf("=") + 1);

                RandomAccessFile fileOutStream = new RandomAccessFile(videoFile, "r");
                fileOutStream.seek(Integer.valueOf(position));
                byte[] buffer = new byte[1024];
                int len;
                int i = 1;
                while ((len = fileOutStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                    i = i + len;
                    progressDialog.setProgress(i);
                }

                fileOutStream.close();
                outStream.close();
                inStream.close();
                socket.close();
            } catch (Exception e) {
                System.out.println("报错" + e.getMessage());
                e.printStackTrace();
            } finally {
                progressDialog.dismiss();

            }
        }
    }


    private static class MyHandler extends Handler {
        WeakReference<RecordActivity> weakReference;     //持有当前Activity对象的弱引用

        MyHandler(RecordActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            RecordActivity activity = weakReference.get();
            if (activity == null) return;
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (activity.n == 8) {
                        activity.stop();
                        return;
                    }
                    String str = activity.list.get(activity.n % 8);
                    activity.n++;
                    ((TextView) activity.findViewById(R.id.tv_content)).setText(str);
                    activity.postMessage();
                    break;
            }
        }
    }

    private void stop() {
        if (record) {
            ((TextView) findViewById(R.id.tv_content)).setText("答题结束！");
            shouldRun = false;
            System.out.println(videoFile);
            mediaRecorder.stop();
            record = false;
            progressDialog = new ProgressDialog(RecordActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setIcon(R.drawable.camera);
            progressDialog.setTitle(R.string.upload);
            progressDialog.setButton("取消上传", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    thread.interrupt();
                }
            });
            progressDialog.show();
            thread = new Thread(new UploadTask());
            thread.start();
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
