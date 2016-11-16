package com.softpo.teaencyclopedia.help;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsyncTaskHelper {
	private static final String TAG = "AsynTaskHelper";

	public interface OnDataDownloadListener {
		void onDataDownload(byte[] result);
	}

	public void downloadData(String url, OnDataDownloadListener downloadListener) {
		new MyTask(downloadListener).execute(url);
	}

	private class MyTask extends AsyncTask<String, Void, byte[]> {
		private OnDataDownloadListener downloadListener;

		public MyTask(OnDataDownloadListener downloadListener) {
			this.downloadListener = downloadListener;
		}

		@Override
		protected byte[] doInBackground(String... params) {
			BufferedInputStream bis = null;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				// Log.i(TAG, "==访问网络加载图片");
				URL url = new URL(params[0]);
				HttpURLConnection httpConn = (HttpURLConnection) url
						.openConnection();
				httpConn.setDoInput(true);
				httpConn.setRequestMethod("GET");
				httpConn.connect();
				if (httpConn.getResponseCode() == 200) {
					bis = new BufferedInputStream(httpConn.getInputStream());
					byte[] buffer = new byte[1024 * 8];
					int c = 0;
					while ((c = bis.read(buffer)) != -1) {
						baos.write(buffer, 0, c);
						baos.flush();
					}
					return baos.toByteArray();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(byte[] result) {
			super.onPostExecute(result);
			// 通过回调接口来传递数据
			downloadListener.onDataDownload(result);
		}
	}
}
