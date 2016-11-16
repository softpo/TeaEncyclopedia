package com.softpo.teaencyclopedia;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.softpo.teaencyclopedia.adapter.MyListViewAdapter;
import com.softpo.teaencyclopedia.help.ChaBaikeJsonHelper;
import com.softpo.teaencyclopedia.help.Constants;
import com.softpo.teaencyclopedia.help.HttpURLConnHelper;
import com.softpo.teaencyclopedia.help.NetworkHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchActivity extends Activity {
	private TextView text_search_title;
	private ListView listView_search_resultlist;
	private TextView text_search_emptyinfo;
	private List<Map<String, Object>> newsList = null;
	private ProgressDialog pDialog;
	private MyListViewAdapter newsAdapter;
	private String keywords = "";
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Constants.STATE:
				pDialog.show();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		text_search_title = (TextView) findViewById(R.id.text_search_title);
		listView_search_resultlist = (ListView) findViewById(R.id.listView_search_resultlist);
		text_search_emptyinfo = (TextView) findViewById(R.id.text_search_emptyinfo);

		// 初始化ListView，设置适配器
		newsList = new ArrayList<Map<String, Object>>();
		newsAdapter = new MyListViewAdapter(this, newsList);
		listView_search_resultlist.setAdapter(newsAdapter);
		listView_search_resultlist.setEmptyView(text_search_emptyinfo);

		// 初始化进度对话框
		pDialog = new ProgressDialog(this);
		pDialog.setIcon(R.drawable.ic_logo);
		pDialog.setTitle(R.string.prompt_alert);
		pDialog.setMessage("Loading...");

		// 接收传递过来的查询关键字
		Intent intent = getIntent();
		keywords = intent.getExtras().getString("keywords");
		try {
			keywords = URLEncoder.encode(keywords, "utf-8");//防止乱码,请求不到搜索数据
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		text_search_title.setText(keywords);

		// 访问网络，查询该关键字所对应的数据
		if (!NetworkHelper.isNetworkConnected(this)) {
			Toast.makeText(this, R.string.prompt_network_error,
					Toast.LENGTH_SHORT).show();
		} else {
			new Thread(new Runnable() {
				@Override
				public void run() {
					handler.sendEmptyMessage(Constants.STATE);
					final byte[] newsResult = HttpURLConnHelper
							.loadByteFromURL(Constants.SEARCH_URL + keywords);
					if (newsResult == null) {
						handler.post(new Runnable() {
							public void run() {
								Toast.makeText(SearchActivity.this,
										R.string.prompt_network_error,
										Toast.LENGTH_SHORT).show();
							}
						});
					} else {
						handler.post(new Runnable() {
							@Override
							public void run() {
								fillListView(new String(newsResult));
								pDialog.dismiss();
							}
						});
					}
				}
			}).start();
		}

		listView_search_resultlist
				.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
											int position, long id) {
						Intent intent = new Intent(SearchActivity.this,
								DetailActivity.class);
						Bundle bundle = new Bundle();
						bundle.putBoolean("isFavorite", false);
						bundle.putString("id", newsList.get(position).get("id")
								.toString());
						bundle.putString("title",
								newsList.get(position).get("title").toString());
						bundle.putString("nickname", newsList.get(position)
								.get("nickname").toString());
						bundle.putString("create_time", newsList.get(position)
								.get("create_time").toString());
						bundle.putString("source",
								newsList.get(position).get("source").toString());
						intent.putExtras(bundle);
						startActivity(intent);
					}
				});
	}

	// 刷新加载Listview的数据
	public void fillListView(String jsonString) {
		newsList.clear();
		newsList.addAll(ChaBaikeJsonHelper.parseJsonToList(jsonString));
		newsAdapter.notifyDataSetChanged();
	}

	public void clickButton(View view) {
		switch (view.getId()) {
		case R.id.imageView_search_back:
			finish();
			break;
		case R.id.imageView_search_home:
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);
			break;
		}
	}

}
