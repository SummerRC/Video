/**
 * RecordActivity.java
 * 版权所有(C) 2013 
 * 创建:cuiran 2013-10-15 下午3:20:45
 */
package com.summerrc.com.video.record;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.summerrc.com.video.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 * @author cuiran
 * @version 1.0.0
 */
public class RecordActivity extends Activity implements Callback,MediaPlayer.OnPreparedListener, OnBufferingUpdateListener,
		OnCompletionListener
{
	private static final String TAG="RecordActivity";

	private String videoPath="/sdcard/love.3gp";

	private MediaRecorder mediarecorder;// 录制视频的类
	private MediaPlayer mediaPlayer;//播放视频的类
	private SurfaceView surfaceview;// 显示视频的控件
	private  Camera camera;
	//实现这个接口的Callback接口
	private SurfaceHolder surfaceHolder;
	/**
	 * 是否正在录制true录制中 false未录制
	 */
	private boolean isRecord=false;

	public boolean isCameraBack=true;
	private List<String> list = new ArrayList<String>();

	private Button recordIv;
	private ImageView recordPlayIv;
	private int n = 0;

	private int mVideoWidth;
	private int mVideoHeight;
	int cameraCount = 0;

	private int cameraPosition = 1;//0代表前置摄像头，1代表后置摄像头


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record_two);
		setTitleStr("SummerRC");

		// 选择支持半透明模式,在有surfaceview的activity中使用。
		getWindow().setFormat(PixelFormat.TRANSLUCENT);

		initView();
		list.add("公鸡下的蛋能吃吗？");
		list.add("先有鸡先有蛋？");
		list.add("更喜欢媳妇更喜欢妈？");
		list.add("更喜欢媳妇更喜欢妈？");
		list.add("喜欢夏雨吗？");
		list.add("超哥是gay吗？");
	}


	/**
	 *
	 */
	private void initView() {

		surfaceview = (SurfaceView) this.findViewById(R.id.surfaceview);
		recordIv=(Button)findViewById(R.id.recordIv);
		recordPlayIv=(ImageView)findViewById(R.id.recordPlayIv);

		SurfaceHolder holder = surfaceview.getHolder();// 取得holder
		holder.addCallback(this); // holder加入回调接口
		// setType必须设置，要不出错.
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		recordIv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				recordVideo(v);
			}

		});

		this.findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String str = list.get(n%6);
				n++;
				((TextView)findViewById(R.id.tv_content)).setText(str);
			}

		});

	}


	/**
	 * 播放视频
	 * TODO
	 * @param v
	 */
	public void playVideo(View v){
		recordPlayIv.setVisibility(View.GONE);
		try {
			mediaPlayer=new MediaPlayer();
			mediaPlayer.setDataSource(videoPath);
			mediaPlayer.setDisplay(surfaceHolder);
			mediaPlayer.prepareAsync();

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block       
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mediaPlayer.setOnBufferingUpdateListener(this);
		mediaPlayer.setOnCompletionListener(this);
		mediaPlayer.setOnPreparedListener(this);
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

	}

	/**
	 * 开始录制/停止录制
	 * TODO
	 * @param v
	 */
	public void recordVideo(View v){
		if(isRecord){
			isRecord=false;
			recordIv.setText("开始录制");
			recordPlayIv.setVisibility(View.VISIBLE);
			if (mediarecorder != null) {
				// 停止录制
				mediarecorder.stop();
				// 释放资源
				mediarecorder.release();
				mediarecorder = null;
			}
			if(camera!=null){
				camera.release();
			}
		}else{
			isRecord=true;
			recordIv.setText("停止录制");
			recordPlayIv.setVisibility(View.GONE);
			mediarecorder = new MediaRecorder();// 创建mediarecorder对象
//			// 从麦克风源进行录音 
//			mediarecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT); 
//			// 设置输出格式 
//			mediarecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT); 
//			// 设置编码格式 
//			mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
			/**
			 * 设置竖着录制
			 */
			if(camera!=null){
				camera.release();
			}

			if(cameraPosition==1){
				camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);//打开摄像头
//				 camera = Camera.open(cameraPosition);//打开摄像头
//		         Camera.Parameters parameters = camera.getParameters();
//		         camera.setDisplayOrientation(90);


//		         camera.setParameters(parameters); 

				camera=deal(camera);
				mediarecorder.setOrientationHint(90);//视频旋转90度


			}else{
				camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);//打开摄像头
				Camera.Parameters parameters = camera.getParameters();
				camera.setDisplayOrientation(90);
				camera.setParameters(parameters);
				mediarecorder.setOrientationHint(270);//视频旋转90度
			}

			camera.unlock();

			mediarecorder.setCamera(camera);

			// 设置录制视频源为Camera(相机)
			mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			// 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4
			mediarecorder
					.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			// 设置录制的视频编码h263 h264
			mediarecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
			// 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
			mediarecorder.setVideoSize(176, 144);
			// 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错
			mediarecorder.setVideoFrameRate(20);


			mediarecorder.setPreviewDisplay(surfaceHolder.getSurface());
			// 设置视频文件输出的路径
			mediarecorder.setOutputFile(videoPath);
			try {
				// 准备录制
				mediarecorder.prepare();
				mediarecorder.start();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}

		}
	}

	protected void setTitleStr(String str) {
		TextView titleText = (TextView) findViewById(R.id.common_title_text);
		titleText.setText(str.trim());

		Button left_button=(Button)findViewById(R.id.left_button);
		left_button.setVisibility(View.GONE);

		Button right_button=(Button)findViewById(R.id.right_button);
		right_button.setVisibility(View.VISIBLE);
		right_button.setText("");
		right_button.setText("切换摄像头");
		right_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cameraCount=Camera.getNumberOfCameras();
				if(isCameraBack){
					isCameraBack=false;
				}else{
					isCameraBack=true;
				}
//					SurfaceHolder holder = surfaceview.getHolder();// 取得holder
//					holder.addCallback(RecordActivity.this); // holder加入回调接口
//					LogsUtil.i(TAG, "cameraCount="+cameraCount);

				int cameraCount = 0;
				CameraInfo cameraInfo = new CameraInfo();
				cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数

				for(int i = 0; i < cameraCount; i++) {

					Camera.getCameraInfo(i, cameraInfo);//得到每一个摄像头的信息
					if(cameraPosition == 1) {
						//现在是后置，变更为前置
						if(cameraInfo.facing  == Camera.CameraInfo.CAMERA_FACING_FRONT) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置

							camera.stopPreview();//停掉原来摄像头的预览
							camera.release();//释放资源
							camera = null;//取消原来摄像头
							camera = Camera.open(i);//打开当前选中的摄像头
							try {
								deal(camera);
								camera.setPreviewDisplay(surfaceHolder);//通过surfaceview显示取景画面
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							camera.startPreview();//开始预览
							cameraPosition = 0;
							break;
						}
					} else {
						//现在是前置， 变更为后置
						if(cameraInfo.facing  == Camera.CameraInfo.CAMERA_FACING_BACK) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
							camera.stopPreview();//停掉原来摄像头的预览
							camera.release();//释放资源
							camera = null;//取消原来摄像头
							camera = Camera.open(i);//打开当前选中的摄像头
							try {
								deal(camera);
								camera.setPreviewDisplay(surfaceHolder);//通过surfaceview显示取景画面
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							camera.startPreview();//开始预览
							cameraPosition = 1;
							break;
						}
					}

				}

			}
		});

	}

	/**
	 * 返回
	 *<b>function:</b>
	 *@author cuiran
	 *@createDate 2013-8-20 下午2:22:48
	 */
	public void back(){

		finish();


	}

	public Camera deal(Camera camera){
		//设置camera预览的角度，因为默认图片是倾斜90度的
		camera.setDisplayOrientation(90);

		Size pictureSize=null;
		Size previewSize=null;
		Camera.Parameters parameters = camera.getParameters();
		parameters.setPreviewFrameRate(5);
		//设置旋转代码
		parameters.setRotation(90);
//			parameters.setPictureFormat(PixelFormat.JPEG);

		List<Size> supportedPictureSizes
				= SupportedSizesReflect.getSupportedPictureSizes(parameters);
		List<Size> supportedPreviewSizes
				= SupportedSizesReflect.getSupportedPreviewSizes(parameters);

		if ( supportedPictureSizes != null &&
				supportedPreviewSizes != null &&
				supportedPictureSizes.size() > 0 &&
				supportedPreviewSizes.size() > 0) {

			//2.x
			pictureSize = supportedPictureSizes.get(0);

			int maxSize = 1280;
			if(maxSize > 0){
				for(Size size : supportedPictureSizes){
					if(maxSize >= Math.max(size.width,size.height)){
						pictureSize = size;
						break;
					}
				}
			}

			WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
			Display display = windowManager.getDefaultDisplay();
			DisplayMetrics displayMetrics = new DisplayMetrics();
			display.getMetrics(displayMetrics);

			previewSize = getOptimalPreviewSize(
					supportedPreviewSizes,
					display.getWidth(),
					display.getHeight());

			parameters.setPictureSize(pictureSize.width, pictureSize.height);
			parameters.setPreviewSize(previewSize.width, previewSize.height);

		}
		camera.setParameters(parameters);
		return camera;
	}
	/* (non-Javadoc)
	 * @see android.view.SurfaceHolder.Callback#surfaceChanged(android.view.SurfaceHolder, int, int, int)
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		// 将holder，这个holder为开始在oncreat里面取得的holder，将它赋给surfaceHolder
		surfaceHolder = holder;
	}
	private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
		final double ASPECT_TOLERANCE = 0.1;
		double targetRatio = (double) w / h;
		if (sizes == null) return null;

		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		int targetHeight = h;

		// Try to find an size match aspect ratio and size
		for (Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}

		// Cannot find the one match the aspect ratio, ignore the requirement
		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalSize;
	}
	/* (non-Javadoc)
	 * @see android.view.SurfaceHolder.Callback#surfaceCreated(android.view.SurfaceHolder)
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		surfaceHolder = holder;

		try {
			if(isCameraBack){
				camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);//打开摄像头

			}else{
				camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);//打开摄像头

			}

			//设置camera预览的角度，因为默认图片是倾斜90度的   
			camera.setDisplayOrientation(90);

			Size pictureSize=null;
			Size previewSize=null;
			Camera.Parameters parameters = camera.getParameters();
			parameters.setPreviewFrameRate(5);
			//设置旋转代码
			parameters.setRotation(90);
//			parameters.setPictureFormat(PixelFormat.JPEG);

			List<Size> supportedPictureSizes
					= SupportedSizesReflect.getSupportedPictureSizes(parameters);
			List<Size> supportedPreviewSizes
					= SupportedSizesReflect.getSupportedPreviewSizes(parameters);

			if ( supportedPictureSizes != null &&
					supportedPreviewSizes != null &&
					supportedPictureSizes.size() > 0 &&
					supportedPreviewSizes.size() > 0) {

				//2.x
				pictureSize = supportedPictureSizes.get(0);

				int maxSize = 1280;
				if(maxSize > 0){
					for(Size size : supportedPictureSizes){
						if(maxSize >= Math.max(size.width,size.height)){
							pictureSize = size;
							break;
						}
					}
				}

				WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
				Display display = windowManager.getDefaultDisplay();
				DisplayMetrics displayMetrics = new DisplayMetrics();
				display.getMetrics(displayMetrics);

				previewSize = getOptimalPreviewSize(
						supportedPreviewSizes,
						display.getWidth(),
						display.getHeight());

				parameters.setPictureSize(pictureSize.width, pictureSize.height);
				parameters.setPreviewSize(previewSize.width, previewSize.height);

			}
			camera.setParameters(parameters);
			camera.setPreviewDisplay(holder);
			camera.startPreview();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	/* (non-Javadoc)
	 * @see android.view.SurfaceHolder.Callback#surfaceDestroyed(android.view.SurfaceHolder)
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		if(camera!=null){
			camera.release();
		}
		surfaceview = null;
		surfaceHolder = null;
		if (surfaceHolder != null) {
			surfaceHolder=null;
		}
		if (mediarecorder != null) {
			mediarecorder=null;
		}
		if (mediaPlayer != null) {
			mediaPlayer.release();
			mediaPlayer = null;
		}

	}

	/* (non-Javadoc)
	 * @see android.media.MediaPlayer.OnCompletionListener#onCompletion(android.media.MediaPlayer)
	 */
	@Override
	public void onCompletion(MediaPlayer arg0) {
		// TODO Auto-generated method stub
		LogsUtil.i(TAG, "onCompletion");
		if (mediaPlayer != null) {
			mediaPlayer.release();
			mediaPlayer = null;
		}
		recordPlayIv.setVisibility(View.VISIBLE);
	}

	/* (non-Javadoc)
	 * @see android.media.MediaPlayer.OnPreparedListener#onPrepared(android.media.MediaPlayer)
	 */
	@Override
	public void onPrepared(MediaPlayer arg0) {
		// TODO Auto-generated method stub
		mVideoWidth = mediaPlayer.getVideoWidth();
		mVideoHeight = mediaPlayer.getVideoHeight();
		if (mVideoWidth != 0 && mVideoHeight != 0)
		{
			 
		   /* 设置视频的宽度和高度 */
			surfaceHolder.setFixedSize(mVideoWidth,mVideoHeight);
		  
		   /* 开始播放 */
			mediaPlayer.start();
		}
	}

	/* (non-Javadoc)
	 * @see android.media.MediaPlayer.OnBufferingUpdateListener#onBufferingUpdate(android.media.MediaPlayer, int)
	 */
	@Override
	public void onBufferingUpdate(MediaPlayer player, int arg1) {
		// TODO Auto-generated method stub

	}

}
