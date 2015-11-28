/**
 * LogsUtil.java
 * 版权所有(C) 2013 
 * 创建:cuiran 2013-4-17 上午11:42:15
 */
package com.summerrc.com.video.record;


import android.util.Log;

/**
 * @author cuiran
 * @version 1.0
 */
public class LogsUtil {
	private static boolean flag=true;
	public static void d(String tag,String msg){
		if(flag){
			Log.d(tag, msg);
		}
		
	}

	public static void d(String tag,String msg,Throwable tr){
		if(flag){
			Log.d(tag, msg,tr);
		}
		
	}
	
	public static void i(String tag,String msg){
		if(flag){
			Log.i(tag, msg);
		}
		
	}

	public static void i(String tag,String msg,Throwable tr){
		if(flag){
			Log.i(tag, msg,tr);
		}
		
	}
	
	public static void e(String tag,String msg){
		if(flag){
			Log.e(tag, msg);
		}
		
	}
	public static void e(String tag,Exception e){
		if(flag){
			if(null!=e){
				Log.e(tag, e.getMessage());
			}
		
		}
		
	}

	public static void e(String tag,String msg,Throwable tr){
		if(flag){
			Log.e(tag, msg,tr);
		}
		
	}
}
