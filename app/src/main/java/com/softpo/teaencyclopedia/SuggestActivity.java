package com.softpo.teaencyclopedia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.softpo.teaencyclopedia.help.Constants;
import com.softpo.teaencyclopedia.help.HttpURLConnHelper;
import com.softpo.teaencyclopedia.help.NetworkHelper;

import java.net.URLEncoder;

public class SuggestActivity extends Activity {
	private EditText editText_suggestion_title;
	private EditText editText_suggestion_content;
	private Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_suggest);
		editText_suggestion_title = (EditText) findViewById(R.id.editText_suggestion_title);
		editText_suggestion_content = (EditText) findViewById(R.id.editText_suggestion_content);
	}

	public void clickButton(View view) {
		switch (view.getId()) {
		case R.id.imageView_suggestion_submit:
			final String title = editText_suggestion_title.getText().toString();
			final String content = editText_suggestion_content.getText()
					.toString();
			if ("".equals(title) || content == null || title == null
					|| "".equals(content)) {
				Toast.makeText(this, R.string.prompt_title_empty,
						Toast.LENGTH_SHORT).show();
			} else {
				// 通过网络提交表单信息到服务器
				if (!NetworkHelper.isNetworkConnected(this)) {
					Toast.makeText(this, R.string.prompt_network_error,
							Toast.LENGTH_SHORT).show();
				} else {
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								String params = "title="
										+ URLEncoder.encode(title, "utf-8")
										+ "&content="
										+ URLEncoder.encode(content, "utf-8");
								final byte[] resultString = HttpURLConnHelper
										.doPostSubmit(Constants.SUGGEST_URL,
												params);

								// 将子线程中获取到的内容返回到主线程，加载到WebView控件上
								handler.post(new Runnable() {
									@Override
									public void run() {
										if (new String(resultString)
												.indexOf("ok") >= 0) {
											Toast.makeText(
													SuggestActivity.this,
													R.string.prompt_suggest_ok,
													Toast.LENGTH_SHORT).show();
										} else {
											Toast.makeText(
													SuggestActivity.this,
													R.string.prompt_suggest_error,
													Toast.LENGTH_SHORT).show();
										}
									}
								});
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}).start();
				}
			}
			break;
		case R.id.imageView_suggestion_back:
			finish();
			break;
		case R.id.imageView_suggestion_home:
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);
			break;
		}
	}
}
