package com.softpo.teaencyclopedia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 版权信息界面
 * @author Administrator
 *
 */
public class CopyRightActivity extends Activity {
	
	private static final int VIDEO_CONTENT_DESC_MAX_LINE = 2;// 默认展示最大行数2行
	private static final int SHOW_CONTENT_NONE_STATE = 0;// 扩充
	private static final int SHRINK_UP_STATE = 1;// 收起状态
	private static final int SPREAD_STATE = 2;// 展开状态
	private static int mState = SHRINK_UP_STATE;//默认收起状态

	private TextView mContentText;// 展示文本内容的TextView
	private TextView mTextViewMore;// 更多TextView
//	private RelativeLayout mRelativeLayout;// 展示更多所在的RelativeLayout
	private ImageView mImageSpread;// 展开
	private ImageView mImageShrinkUp;// 收起
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_copyright);
		
		initView();
		initData();
	}

	private void initData() {
		mContentText.setText(R.string.about_chabaike_info);
		mTextViewMore = (TextView) findViewById(R.id.textView_more);
	}

	private void initView() {
		mContentText = (TextView) findViewById(R.id.textView6);
//		mRelativeLayout = (RelativeLayout) findViewById(R.id.show_more);
		mImageSpread = (ImageView) findViewById(R.id.spread);
		mImageShrinkUp = (ImageView) findViewById(R.id.shrink_up);
	}

	public void clickButton(View view) {
		switch (view.getId()) {
		case R.id.show_more:
			if (mState == SPREAD_STATE) {//展开状态
				mContentText.setMaxLines(VIDEO_CONTENT_DESC_MAX_LINE);
				mContentText.requestLayout();
				mImageShrinkUp.setVisibility(View.GONE);
				mImageSpread.setVisibility(View.VISIBLE);
				mTextViewMore.setText("更多");
				mState = SHRINK_UP_STATE;
			} else if (mState == SHRINK_UP_STATE) {//收起状态
				mContentText.setMaxLines(Integer.MAX_VALUE);
				mContentText.requestLayout();
				mImageShrinkUp.setVisibility(View.VISIBLE);
				mImageSpread.setVisibility(View.GONE);
				mTextViewMore.setText("收起");
				mState = SPREAD_STATE;
			}
			break;
		case R.id.imageView_copyright_back:
			finish();
			break;
		case R.id.imageView_copyright_home:
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);
			break;
		}
	}

}
