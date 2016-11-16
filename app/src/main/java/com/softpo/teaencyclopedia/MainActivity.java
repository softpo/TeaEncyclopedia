package com.softpo.teaencyclopedia;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.softpo.teaencyclopedia.adapter.MyFragmentPagerAdapter;
import com.softpo.teaencyclopedia.fragment.FirstPagerFragment;
import com.softpo.teaencyclopedia.fragment.PagerFragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import animation.FlipHorizontalTransformer;

public class MainActivity extends FragmentActivity {
	private ViewPager viewPager_main;

	public ViewPager getViewPager_main() {
		return viewPager_main;
	}

	private LinearLayout layout_main_container;
	private LinearLayout layout_main_underline;
	private DrawerLayout layout_drawer;//DrawableLayout功能单一,问题较多,兼容性较差,推荐使用SlidingMenu实现侧滑效果
	private LinearLayout layout_main_rightdrawer;//右侧抽屉内部的布局
	private EditText editText_drawer_keyword;//关键字
	private String[] arrTabTitleNames = null;
	private List<Fragment> list = null;
	private TextView[] arrTitles = null;//导航标题
	private View[] arrUnderlines = null;//指示线

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		new Thread(new Runnable() {
			@Override
			public void run() {
				String path = "http://sns.maimaicha.com/api?apikey=b4f4ee31a8b9acc866ef2afb754c33e6&format=" +
						"json&method=news.getHeadlines&rows=15&page=1";

				try {
					URL url = new URL(path);

					HttpURLConnection conn = (HttpURLConnection) url.openConnection();

					conn.setConnectTimeout(5000);

					if(conn.getResponseCode() ==200){
						InputStream is =
								conn.getInputStream();

						ByteArrayOutputStream baos = new ByteArrayOutputStream();

						int len = 0;

						byte[] buf = new byte[1024*8];

						while ((len = is.read(buf))!=-1){
							baos.write(buf,0,len);
						}

						byte[] bytes = baos.toByteArray();
						Log.d("ttff", "----------------->run: " +new String(bytes));
					}

				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
		viewPager_main = (ViewPager) findViewById(R.id.viewPager_main);
		viewPager_main.setOffscreenPageLimit(1);//ViewPager 预加载
		layout_main_container = (LinearLayout) findViewById(R.id.layout_main_container);
		layout_main_underline = (LinearLayout) findViewById(R.id.layout_main_underline);
		layout_drawer = (DrawerLayout) findViewById(R.id.layout_drawer);
		layout_main_rightdrawer = (LinearLayout) findViewById(R.id.layout_main_rightdrawer);
		editText_drawer_keyword = (EditText) findViewById(R.id.editText_drawer_keyword);

		// 设置抽屉出现时的宽度
		LayoutParams layoutParams = layout_main_rightdrawer.getLayoutParams();
		int screenWidthPixels = getResources().getDisplayMetrics().widthPixels;
		layoutParams.width = (int) (screenWidthPixels*0.9f+0.5f);
		layout_main_rightdrawer.setLayoutParams(layoutParams);

		// 从资源中获取所有TAB导航标签上的文字
		arrTabTitleNames = getResources().getStringArray(R.array.arrTabTitles);
		// 初始化所有TAB导航标签
		initTabHost();

		list = new ArrayList<Fragment>();
		FirstPagerFragment firstFragment = new FirstPagerFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("tabindex", 0);
		firstFragment.setArguments(bundle);
		list.add(firstFragment);

		for (int i = 1; i < arrTabTitleNames.length; i++) {
			PagerFragment fragment = new PagerFragment();
			bundle = new Bundle();
			bundle.putInt("tabindex", i);
			fragment.setArguments(bundle);
			list.add(fragment);
		}
		viewPager_main.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), list));

		//添加动画效果
		/**
		 * * @param reverseDrawingOrder
		 * true if the supplied PageTransformer requires page views
		 *  to be drawn from last to first instead of first to last
		 */
		viewPager_main.setPageTransformer(true, new FlipHorizontalTransformer());
		
		viewPager_main.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				for (int i = 0; i < arrTitles.length; i++) {
					arrTitles[i].setEnabled(true);
					arrTitles[i].setTextSize(16);
					arrTitles[i].setTextColor(Color.GRAY);
					arrUnderlines[i].setBackgroundColor(Color.rgb(230, 230, 230));
				}
				arrTitles[position].setEnabled(false);
				arrTitles[position].setTextColor(Color.rgb(0, 156, 0));
				arrTitles[position].setTextSize(18);
				arrUnderlines[position].setBackgroundColor(Color.rgb(0, 205, 0));
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	private void initTabHost() {
		arrTitles = new TextView[arrTabTitleNames.length];
		arrUnderlines = new View[arrTabTitleNames.length];
		for (int i = 0; i < arrTitles.length; i++) {
			arrTitles[i] = (TextView) layout_main_container.getChildAt(i);
			arrTitles[i].setEnabled(true);
			arrTitles[i].setBackgroundColor(Color.argb(255, 230, 230, 230));
			arrTitles[i].setTextColor(Color.GRAY);
			arrTitles[i].setText(arrTabTitleNames[i]);
			arrTitles[i].setTag(i);
			arrTitles[i].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					viewPager_main.setCurrentItem((Integer) v.getTag());
				}
			});
			arrUnderlines[i] = (View) layout_main_underline.getChildAt(i);
			arrUnderlines[i].setBackgroundColor(Color.rgb(230, 230, 230));
		}
		arrTitles[0].setEnabled(false);
		arrTitles[0].setTextColor(Color.rgb(0, 156, 0));
		arrUnderlines[0].setBackgroundColor(Color.rgb(0, 205, 0));
	}

	public void clickButton(View view) {
		Intent intent = new Intent();
		switch (view.getId()) {
		//打开抽屉
		case R.id.imageView_main_more:
			layout_drawer.openDrawer(layout_main_rightdrawer);
			break;
		//关闭抽屉
		case R.id.imageView_drawer_back:
			layout_drawer.closeDrawer(layout_main_rightdrawer);
			break;
		case R.id.imageView_drawer_home:
			intent.setAction(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);
			break;
		case R.id.text_drawer_myfavorite:
			intent.putExtra("type", 1);
			intent.setClass(this, FavoriteActivity.class);
			startActivity(intent);
			break;
		case R.id.text_drawer_history:
			intent.putExtra("type", 2);
			intent.setClass(this, FavoriteActivity.class);
			startActivity(intent);
			break;
		case R.id.text_drawer_copyright:
			intent.setClass(this, CopyRightActivity.class);
			startActivity(intent);
			break;
		case R.id.imageView_drawer_search:
			String keyword = editText_drawer_keyword.getText().toString();
			if (keyword != null && !"".equals(keyword)) {
				intent.setClass(this, SearchActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("keywords", keyword);
				intent.putExtras(bundle);
				startActivity(intent);
			} else {
				Toast.makeText(this, R.string.prompt_keyword_empty, Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.text_drawer_suggest:
			intent.setClass(this, SuggestActivity.class);
			startActivity(intent);
			break;
			
		case R.id.text_drawer_login://用户登陆
			intent.setClass(this, LoginActivity.class);
			startActivity(intent);
			break;
			
		case R.id.text_drawer_exit://用户退出
			File file = this.getDatabasePath("sharesdk.db");
			Log.i("TAG", "数据库sharesdk.db--->"+file.toString());
			if (file.exists()) {
				file.delete();
				Toast.makeText(MainActivity.this, "退出登陆", Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}
	//退出应用
	private long lasttime;

	@Override
	public void onBackPressed() {
		if (System.currentTimeMillis() - lasttime < 5000) {
			finish();
		} else {
			lasttime = System.currentTimeMillis();
			Toast.makeText(getApplicationContext(), "再按一次退出茶百科", Toast.LENGTH_SHORT).show();
		}
	}
}
