package com.softpo.teaencyclopedia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.softpo.teaencyclopedia.adapter.MyPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends Activity {

	private ViewPager viewPager;
	private int[] images = { R.drawable.slide1, R.drawable.slide2,R.drawable.slide3 };//图片资源
	private List<View> list;
	private ImageView[] icons;// 存储指示图片的数组
	private Button button_go;//进入主界面的按钮(立即体验)

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		viewPager = (ViewPager) findViewById(R.id.viewPager_guide);
		button_go=(Button) findViewById(R.id.button_go);
		list = new ArrayList<View>();
		for (int i = 0; i < images.length; i++) {
			ImageView imageView = new ImageView(this);
			imageView.setScaleType(ScaleType.FIT_XY);
			imageView.setImageResource(images[i]);
			list.add(imageView);
		}
		viewPager.setAdapter(new MyPagerAdapter(list, this));
		initicons();
		/*
		 * 表示当前测viewpager中的page的状态发生改变时回调的接口
		 */
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			/*
			 * 表示当一个新的page界面被选中时回调的方法 参数表示 当前选择的page在viewpager中的下标
			 */
			@Override
			public void onPageSelected(int position) {
				// 通过循环将指示性的图片全部设置为未选中状态
				for (int i = 0; i < images.length; i++) {
					icons[i].setEnabled(true);
				}
				icons[position].setEnabled(false);// 根据下标找到当前选中图片对应的imageview设置为选中
				//当加载第三页的时候 将立即体验的按钮显示出来并设置监听器
				if (position==2) {
					button_go.setVisibility(View.VISIBLE);
					button_go.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(GuideActivity.this, MainActivity.class);
							startActivity(intent);
							GuideActivity.this.finish();//当前页面关闭
							
						}
					});
				}									
			}

			/*
			 * 表示当viewpager中的page发生滚动时回调的方法 第一个参数表示 当前滚动的page的下标 第二个参数表示
			 * 当前page页滑动的偏移量 ［0，1） 第三个参数表示 当前page页滑动的偏移量 像素值表示
			 */
			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
				// System.out.println("--------onPageScrolled------"+positionOffset);
				// System.out.println("----onPageScrolled--"+positionOffsetPixels);
			}

			/*
			 * 表示当前viewpager中的page的状态改变的时候回调的方法 参数表示 当前page页的状态
			 * SCROLL_STATE_DRAGGING 正在滑动状态 SCROLL_STATE_IDLE 停止滑动状态
			 * SCROLL_STATE_SETTLING 选中状态
			 */
			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
	}

	/**
	 * 初始化指示性图片的方法
	 */
	public void initicons() {
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear_guide_dots);
		icons = new ImageView[images.length];// 初始化指示性图片的存储数组
		for (int i = 0; i < images.length; i++) {
			icons[i] = (ImageView) linearLayout.getChildAt(i);// 以此从LinearLayout中取出imageview
																// 存放到icons数组中
			icons[i].setEnabled(true);
			icons[i].setTag(i);// 给指示性图片添加下标指示
			// 指示性图片添加单击事件
			icons[i].setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 将viewpager中的page页设置为指定的下标
					viewPager.setCurrentItem((Integer) v.getTag()); // 单击那个指示图标
																	// 就将该指示图片下标对应的page展示
				}
			});
		}
		icons[0].setEnabled(false);// icons数组中的第一个imageview默认显示选中状态的图片
	}

}
