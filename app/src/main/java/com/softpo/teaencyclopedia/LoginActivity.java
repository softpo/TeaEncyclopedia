package com.softpo.teaencyclopedia;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;


/**
 * @ClassName:  LoginActivity   
 * @Description:登陆Activity  
 * @author: Liu YanChao  
 * @date:   2016-10-25 下午11:16:39   
 *
 */

public class LoginActivity extends Activity implements OnClickListener {

	private ImageButton imageButton_qq;
	private ImageButton imageButton_sina;
	private ImageButton imageButton_renren;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		imageButton_qq = (ImageButton) findViewById(R.id.login_page_shareqq);
		imageButton_sina = (ImageButton) findViewById(R.id.login_page_sharesina);
		imageButton_renren = (ImageButton) findViewById(R.id.login_page_sharerenren);
		imageButton_qq.setOnClickListener(this);
		imageButton_sina.setOnClickListener(this);
		imageButton_renren.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_page_shareqq:
			login(new QQ(this));
			break;
		case R.id.login_page_sharesina:
			login(new SinaWeibo(this));
			break;
		case R.id.login_page_sharerenren:
			
			break;
		default:
			break;
		}
		
	}
	
	public void login(Platform platform){
		String userId = platform.getDb().getUserId();
		platform.SSOSetting(true);//true表示不使用SSO方式授权
		if (!TextUtils.isEmpty(userId)) {
			//跳转
			Toast.makeText(this, "您已经成功登陆啦", Toast.LENGTH_SHORT).show();
		}else {
			platform.setPlatformActionListener(new PlatformActionListener() {
				
				@Override
				public void onError(Platform arg0, int arg1, Throwable arg2) {
					Log.i("TAG", "onError当前线程--->"+ Thread.currentThread().getName());//主线程
					Log.i("TAG", "登陆发生错误--->"+arg2.getMessage());
//					arg0.removeAccount(true);
				}
				
				/**
				 * int arg1:登陆方式
				 * Platform.ACTION_AUTHORIZING: 1 要功能不要数据
				 * Platform.ACTION_USER_INFOR:  8 要数据不要功能
				 */
				@Override
				public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
					Log.i("TAG", "onComplete当前线程--->"+ Thread.currentThread().getName());//子线程
					switch (arg1) {
					case Platform.ACTION_AUTHORIZING:
						if (arg2 != null ) {
							Log.i("TAG", "第三方登陆成功:要功能不要数据-->"+arg2);
						}
//						不能Toast
//						Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
						break;
					case Platform.ACTION_USER_INFOR:
						Set<Entry<String,Object>> entrySet = arg2.entrySet();
						for (Entry<String, Object> entry:entrySet) {
							String key = entry.getKey();
							Object value = entry.getValue();
							Log.i("TAG", "第三方登陆成功：要数据不要功能-->key:"+key+",value:"+value);
						}
						break;
					default:
						break;
					}
				}
				
				@Override
				public void onCancel(Platform arg0, int arg1) {
					Log.i("TAG", "onCancel当前线程--->"+ Thread.currentThread().getName());//主线程
					Log.i("TAG", "登陆取消");
				}
			});
			
//			platform.authorize();//授权 要功能不要数据
			platform.showUser(null);//授权 要数据不要功能
		}
	}


}
