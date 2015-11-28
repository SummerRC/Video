package com.summerrc.com.video;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class MyCameraActivity extends Activity implements Callback {
	/** Called when the activity is first created. */
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	private Button button1;
	private Button button2;
	private Button button3;
	private Button button4;
	private Button button5;
	private boolean isCameraOpen = false;
	private Camera camera;
	private boolean isPreeTake = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Window window = getWindow();// 得到窗口
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.cameraht);
		surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		

	/*	button1 = (Button) findViewById(R.id.myButton1);
		button1.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openCamera();
			}
		});
		button2 = (Button) findViewById(R.id.myButton2);
		button2.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				closeCamera();
			}
		});*/
		button3 = (Button) findViewById(R.id.myButton3);
		button3.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AutoFocus();
			}
		});
		button4 = (Button) findViewById(R.id.myButton4);

		button4.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (checkSDCard() && camera != null && isCameraOpen
						&& isPreeTake) {

					camera.takePicture(null, null, pictureCallback);
					button4.setText(R.string.continiue);
				} else if (camera != null && !isPreeTake) {

					ContiniuTakepictrue();
					button4.setText(R.string.takePictus);

				}
			}
		});
		button5=(Button)findViewById(R.id.myButton5);
		button5.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				Intent intent=new Intent(MyCameraActivity.this,ThumbnailActivity.class);
				startActivity(intent);
				
			}
		});

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
	
		
		    
		openCamera();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
	
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		closeCamera();
		
            this.finish();
	}

	/**
	 * 打开摄像头
	 */

	private void openCamera() {

		try {
			if (!isCameraOpen) {
				camera = Camera.open();
				Camera.Parameters parameters = camera.getParameters();
				parameters.setPictureFormat(PixelFormat.JPEG);
				WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
				Display display = wm.getDefaultDisplay();
				int picWidth = display.getWidth();
				int picHeight = display.getHeight();
				parameters.setPreviewSize(picWidth, picHeight);
				//parameters.setPreviewFrameRate(2);//每秒3帧
				parameters.set("jpeg-quality", 85);
				camera.setParameters(parameters);
				camera.setPreviewDisplay(surfaceHolder);
				camera.startPreview();
				isCameraOpen = true;
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * 关闭摄像头
	 */
	private void closeCamera() {

		try {
			if (camera != null && isCameraOpen) {
				camera.stopPreview();
				camera.release();
				camera = null;
				isCameraOpen = false;
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * 摄像头对焦
	 */
	private void AutoFocus() {
		if (camera != null) {
			camera.autoFocus(null);
		}
	}

	/**
	 * 继续拍照
	 */
	private void ContiniuTakepictrue() {
		if (camera != null) {
			camera.startPreview();
			isPreeTake = true;
		}
	}

	/**
	 * 摄像头拍照
	 */
	private PictureCallback pictureCallback = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			try {
				// 生成图片
				Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
						data.length);
				// 创建文件
				File file=new File(Environment.getExternalStorageDirectory()+"/CameraHTKJ");
				if(!file.exists())
				{
					file.mkdirs();
				}
				File myCaptureFile = new File(
						Environment.getExternalStorageDirectory()+"/CameraHTKJ",
						System.currentTimeMillis() + ".jpg");
				
				BufferedOutputStream outputStream = new BufferedOutputStream(
						new FileOutputStream(myCaptureFile));
				// 压缩转档保存到本地
				bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
				outputStream.flush();
				outputStream.close();

				Toast.makeText(MyCameraActivity.this, "保存成功,保存路径"
						+ myCaptureFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
				isPreeTake = false;
				// closeCamera();
				// camera.startPreview();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

		}
	};

	/**
	 * 检查SDcard是否存在
	 * 
	 * @return
	 */
	private boolean checkSDCard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}

	/**
	 * 摧毁时结束
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
		this.finish();
		super.onDestroy();
	}
}