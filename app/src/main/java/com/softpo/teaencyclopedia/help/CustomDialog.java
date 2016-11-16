package com.softpo.teaencyclopedia.help;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ListView;
import android.widget.TextView;

import com.softpo.teaencyclopedia.R;
import com.softpo.teaencyclopedia.adapter.MyListViewAdapter;

import java.util.List;
import java.util.Map;

/**
 * Created with Eclipse J2EE + ADT
 * 
 * @author Liu YanChao
 */
public class CustomDialog {
	private AlertDialog dlg;
	private Context context;
	private int  myPosition;
	private MyListViewAdapter adapter;
	private List<Map<String, Object>> newsList;
	private View view;// 当前点击的listView item的View对象
	private ListView listView;
	
	public CustomDialog(Context context, ListView listView, MyListViewAdapter adapter, List<Map<String, Object>> newsList, View view, int position) {
		super();
		this.context = context;
		this.listView = listView;
		this.adapter = adapter;
		this.newsList = newsList;
		this.view = view;
		this.myPosition = position;
	}

	public void showDialog() {
		if (dlg == null) {
			dlg = new AlertDialog.Builder(context).create();
			dlg.show();
			Window window = dlg.getWindow();
			window.setContentView(R.layout.style_dialog);
			TextView login = (TextView) window.findViewById(R.id.dialog_login);
			login.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dlg.cancel();

					//获取每个item对象
					final View itemLayout = view;
					
					TranslateAnimation translateAnimation = new TranslateAnimation(0, -itemLayout.getWidth(), 0, 0);
					translateAnimation.setDuration(800);
					translateAnimation.setFillAfter(false);
					itemLayout.startAnimation(translateAnimation);
					
					translateAnimation.setAnimationListener(new AnimationListener() {
						
						@Override
						public void onAnimationStart(Animation animation) {
							
						}
						
						@Override
						public void onAnimationRepeat(Animation animation) {
							
						}
						
						@Override
						public void onAnimationEnd(Animation animation) {
							// 注意这个开始坐标要写成View此时此刻的坐标，然后它再挪到我们ListView排列后的位置上
							TranslateAnimation upAnimation = new TranslateAnimation(0, 0, itemLayout.getHeight(), 0);
							upAnimation.setDuration(500);
							upAnimation.setInterpolator(context, android.R.anim.linear_interpolator);
//							upAnimation.setFillAfter(true);
							for (int i = 0+2; i < listView.getChildCount(); i++) {
								if (listView.getChildAt(i).getTop()>= view.getTop()) {
									listView.getChildAt(i).startAnimation(upAnimation);
								}
							}
							
							//执行删除刷新ListView
							newsList.remove(myPosition-2);
							adapter.notifyDataSetChanged();
							
						}
					});
					
				}
			});
			TextView cancel = (TextView) window.findViewById(R.id.dialog_cancel);
			cancel.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					dlg.cancel();
				}
			});

		} else {
			dlg.show();
		}

	}
}
