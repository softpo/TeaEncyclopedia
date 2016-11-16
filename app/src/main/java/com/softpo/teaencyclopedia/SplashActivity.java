package com.softpo.teaencyclopedia;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 开机动画界面,在这里判断程序是不是第一次安装
 * 如果是第一次安装,开机动画结束后进入引导界面,否则进入主界面
 *
 */
public class SplashActivity extends Activity {
	private SharedPreferences sharedPreferences;
	private Boolean isFirstIn;
	private Timer timer;
	private int num = 3;
	private TextView textView_inter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		textView_inter = (TextView) findViewById(R.id.textView_inter);
		//SharedPreferences本身是一个接口，无法直接创建实例，通过Context的getSharedPreferences(String name, int  mode)方法来获取实例。
		//MODE_PRIVATE:指定该SharedPreferences的数据只能被本应用程序读、写.
		sharedPreferences=getSharedPreferences("firstIn_spf", MODE_PRIVATE);
		//取得相应的值，如果没有该值，说明还未写入，用true作为默认值
		isFirstIn=sharedPreferences.getBoolean("isFirstIn", true);
//		new Handler().postDelayed(new Runnable() {
//			
//			@Override
//			public void run() {
//				// 判断程序第几次运行，如果是第一次运行则跳转到引导界面，否则跳转到主界面
//				if (isFirstIn) {
//					Intent intent=new Intent(SplashActivity.this, GuideActivity.class);
//					startActivity(intent);
//					//更新提交sharedPreferences中的值
//					Editor editor=sharedPreferences.edit();
//					editor.putBoolean("isFirstIn", false);
//					editor.commit();
//				}else {
//					Intent intent=new Intent(SplashActivity.this, MainActivity.class);
//					startActivity(intent);
//				}
//				SplashActivity.this.finish();
//				finish();
//			}
//		}, 3000);
		
		timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						num--;
						textView_inter.setText(num +"秒后自动进入");
						if (num<1) {
							timer.cancel();
							toNextActivity();
							
						}
					}

					private void toNextActivity() {
						if (isFirstIn) {
							Intent intent=new Intent(SplashActivity.this, GuideActivity.class);
							startActivity(intent);
							//更新提交sharedPreferences中的值
							Editor editor=sharedPreferences.edit();
							editor.putBoolean("isFirstIn", false);
							editor.commit();
						}else {
							Intent intent=new Intent(SplashActivity.this, MainActivity.class);
							startActivity(intent);
						}
//						SplashActivity.this.finish();
						finish();
					}

				});
			}
		}, 1000, 1000);
		
		
//		// 使用定时器，延迟执行页面跳转
//				new Timer().schedule(new TimerTask() {
//					@Override
//					public void run() {
//						Intent intent = new Intent();
//						if (isFirstIn) {
//							intent.setClass(SplashActivity.this, GuideActivity.class);
//							Editor editor=sharedPreferences.edit();
//							editor.putBoolean("isFirstIn", false);
//							editor.commit();
//						} else {
//							intent.setClass(SplashActivity.this, MainActivity.class);
//						}
//						startActivity(intent);
//						finish();
//					}
//				}, 3000);
	
	}
}

