package com.softpo.teaencyclopedia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.softpo.teaencyclopedia.help.ChaBaikeSQLiteOpenHelper;
import com.softpo.teaencyclopedia.help.Constants;
import com.softpo.teaencyclopedia.help.HttpURLConnHelper;
import com.softpo.teaencyclopedia.help.NetworkHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.sharesdk.onekeyshare.OnekeyShare;

public class DetailActivity extends Activity {
	private TextView text_content_title;
	private TextView text_content_updatetime;
	private TextView text_content_source;
	private LinearLayout layout_detail_container;
	private WebView webView_content;
	private ChaBaikeSQLiteOpenHelper dbHelper;
	private String newsid = "";
	private String time = "";
	private String title = "";
	private String nickname = "";
	private String source = "";
	private boolean isFavorite = false;
	private Map<String, String> resultMap = null;
	private Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

		text_content_title = (TextView) findViewById(R.id.text_content_title);
		text_content_updatetime = (TextView) findViewById(R.id.text_content_updatetime);
		text_content_source = (TextView) findViewById(R.id.text_content_source);
		layout_detail_container = (LinearLayout) findViewById(R.id.layout_detail_container);

		// 初始化WebView及属性设置
		webView_content = (WebView) findViewById(R.id.webView_content);
		webView_content.getSettings().setJavaScriptEnabled(true);
		webView_content.setWebChromeClient(new WebChromeClient());
		webView_content.setWebViewClient(new WebViewClient());
		
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		isFavorite = bundle.getBoolean("isFavorite");
		if (isFavorite) {
			layout_detail_container.setVisibility(View.GONE);
		}
		newsid = bundle.getString("id");
		nickname = bundle.getString("nickname");
		time = bundle.getString("create_time");
		title = bundle.getString("title");
		source = bundle.getString("source");

		text_content_title.setText(title);
		text_content_updatetime.setText(time);
		text_content_source.setText(source);

		// 通过网络获取信息的详细内容
		if (!NetworkHelper.isNetworkConnected(this)) {
			Toast.makeText(this, R.string.prompt_network_error,
					Toast.LENGTH_SHORT).show();
		} else {
			new Thread(new Runnable() {
				@Override
				public void run() {
					byte[] data = HttpURLConnHelper
							.loadByteFromURL(Constants.CONTENT_URL + newsid);
					if (data != null) {
						resultMap = parseDetailJson(new String(data));
					}
					// 将子线程中获取到的内容返回到主线程，加载到WebView控件上
					handler.post(new Runnable() {
						@Override
						public void run() {
							webView_content.loadDataWithBaseURL(null,
									resultMap.get("wap_content"), "text/html",
									"utf-8", null);
						}
					});
				}
			}).start();
		}
	}

	private Map<String, String> parseDetailJson(String jsonString) {
		try {
			JSONObject object = new JSONObject(jsonString);
			JSONObject object_data = object.getJSONObject("data");
			Map<String, String> map = new HashMap<String, String>();
			map.put("wap_content", object_data.getString("wap_content"));
			return map;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void clickButton(View view) {
		switch (view.getId()) {
		case R.id.imageView_content_back://返回
			finish();
			break;
		case R.id.imageView_content_collect://收藏
			dbHelper = new ChaBaikeSQLiteOpenHelper(this);
			int num = dbHelper.selectCount(
					"select count(title) from tb_favorites where newsid=?",
					new String[] { newsid });
			if (num != 0) {
				Toast.makeText(this, R.string.prompt_collected,
						Toast.LENGTH_SHORT).show();
			} else {
				String sql = "insert into tb_favorites(newsid , title , source,nickname,create_time) values(?,?,?,?,?)";
				boolean flag = dbHelper.execData(sql, new Object[] { newsid,
						title, source, nickname, time });
				if (flag) {
					Toast.makeText(this, R.string.prompt_collect_ok,
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(this, R.string.prompt_collect_error,
							Toast.LENGTH_SHORT).show();
				}
			}
			break;
		case R.id.imageView_content_share:
			showShare();
			break;
		}
	}

	
	private void showShare() {
//		 ShareSDK.initSDK(this);
		 OnekeyShare oks = new OnekeyShare();
		 //关闭sso授权
		 oks.disableSSOWhenAuthorize(); 
		 
		// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
		 //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
		 // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		 oks.setTitle("标题");
		 // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		 oks.setTitleUrl("http://sharesdk.cn");
		 // text是分享文本，所有平台都需要这个字段
		 oks.setText("我是分享文本");
		 //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
		 oks.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
		 // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		 //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
		 // url仅在微信（包括好友和朋友圈）中使用
		 oks.setUrl("http://sharesdk.cn");
		 // comment是我对这条分享的评论，仅在人人网和QQ空间使用
		 oks.setComment("我是测试评论文本");
		 // site是分享此内容的网站名称，仅在QQ空间使用
		 oks.setSite("ShareSDK");
		 // siteUrl是分享此内容的网站地址，仅在QQ空间使用
		 oks.setSiteUrl("http://sharesdk.cn");
		 
		// 启动分享GUI
		 oks.show(this);
		 }
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (dbHelper != null) {
			dbHelper.destroy();
		}
	}

}
