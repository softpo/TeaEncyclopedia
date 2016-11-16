package com.softpo.teaencyclopedia.help;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

public class ImageDownloadHelper {
	public final String TAG = "ImageDownloaderHelper";
	// public HashMap<String, MyAsyncTask> map = new HashMap<String,
	// MyAsyncTask>();
	public Map<String, SoftReference<Bitmap>> softCaches = new LinkedHashMap<String, SoftReference<Bitmap>>();
	public LruCache<String, Bitmap> lruCache = null;

	public interface OnImageDownloadListener {
		void onImageDownload(Bitmap bitmap, String imgUrl);
	}

	public ImageDownloadHelper() {
		int memoryAmount = (int) Runtime.getRuntime().maxMemory();
		// 获取剩余内存的8分之一作为缓存
		int cacheSize = memoryAmount / 8;
		if (lruCache == null) {
			lruCache = new MyLruCache(cacheSize);
		}
	}

	// 异步加载图片方法
	public void myDownloadImage(Context context, String url,
								ImageView imageView, OnImageDownloadListener downloadListener) {
		Bitmap bitmap = null;
		// 先从强引用中拿数据
		if (lruCache != null) {
			bitmap = lruCache.get(url);
		}
		if (bitmap != null && url.equals(imageView.getTag())) {
			// Log.i(TAG, "==从强引用中找到数据" + bitmap.toString());
			imageView.setImageBitmap(bitmap);
		} else {
			SoftReference<Bitmap> softReference = softCaches.get(url);
			if (softReference != null) {
				bitmap = softReference.get();
			}
			// 从软引用中拿数据
			if (bitmap != null && url.equals(imageView.getTag())) {
				// Log.i(TAG, "==从软引用中找到数据" + bitmap.toString());
				imageView.setImageBitmap(bitmap);

				// 添加到强引用中
				lruCache.put(url, bitmap);
				// 从软引用集合中移除
				softCaches.remove(url);
			} else {
				// 从文件缓存中拿数据
				if (url != null) {
					String imageName = "";
					imageName = getImageName(url);
					String cachePath = SDCardHelper
							.getInstance().getSDCardCachePath(context);
//					Log.i("TAG", "外部存储的私有Cache目录--->"+ cachePath);
					bitmap = SDCardHelper.getInstance()
							.loadBitmapFromSDCard(
									cachePath + File.separator + imageName);

					if (bitmap != null && url.equals(imageView.getTag())) {
						// Log.i(TAG, "==从文件缓存中找到数据" + bitmap.toString());
						imageView.setImageBitmap(bitmap);
						// 放入强缓存
						lruCache.put(url, bitmap);
					} else {
						// 从网络中拿数据
						// if (url != null && needCreateNewTask(imageView)) {
						// MyAsyncTask task = new MyAsyncTask(context, url,
						// imageView, downloadListener);
						// if (imageView != null) {
						// task.execute(url);
						// // 将对应的url对应的任务存起来
						// map.put(url, task);
						// }
						// }
						new MyAsyncTask(context, url, imageView,
								downloadListener).execute(url);
					}
				}
			}
		}
	}

	/*
	 * // 判断是否需要重新创建线程下载图片，如果需要，返回值为true public boolean
	 * needCreateNewTask(ImageView imageView) { boolean flag = true; if
	 * (imageView != null) { String currentTaskUrl = (String)
	 * imageView.getTag(); if (isTaskCreated(currentTaskUrl)) { flag = false; }
	 * } return flag; }
	 *
	 * // 检查该url所对应的任务是否被创建过（最终反映的是当前的ImageView的tag，tag会根据position的不同而不同） public
	 * boolean isTaskCreated(String url) { boolean flag = false; if (map != null
	 * && map.get(url) != null) { flag = true; } return flag; }
	 *
	 * // 删除map中该url对应的任务信息，这一步很重要，不然MyAsyncTask的引用会“一直”存在于map中，而无法再次执行该任务
	 * public void removeTaskFromMap(String url) { if (url != null && map !=
	 * null && map.get(url) != null) { map.remove(url); } }
	 */

	// 异步任务类
	class MyAsyncTask extends AsyncTask<String, Void, Bitmap> {
		public Context context;
		public ImageView mImageView;
		public String url;
		public OnImageDownloadListener downloadListener;

		public MyAsyncTask(Context context, String url, ImageView mImageView,
						   OnImageDownloadListener downloadListener) {
			this.context = context;
			this.url = url;
			this.mImageView = mImageView;
			this.downloadListener = downloadListener;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			Bitmap bm = null;
			try {
				String urlString = params[0];
				URL urlObj = new URL(urlString);
				HttpURLConnection httpConn = (HttpURLConnection) urlObj
						.openConnection();
				httpConn.setDoInput(true);
				httpConn.setRequestMethod("GET");
				httpConn.connect();
				if (httpConn.getResponseCode() == 200) {
					InputStream is = httpConn.getInputStream();
					bm = BitmapFactory.decodeStream(is);
				}
				if (bm != null) {
					String imageName = getImageName(urlString);
					boolean flag = SDCardHelper
							.getInstance().saveBitmapToSDCardPrivateCacheDir(
									bm, imageName, context);
					Log.i("TAG", "保存到外部存储的私有Cache目录--->"+ SDCardHelper.getInstance().getSDCardCachePath(context));
					if (flag) {
						// Log.i(TAG, "==从网络中找到数据" + bm.toString());
						// 放入强缓存
						lruCache.put(urlString, bm);
					} else {
						// removeTaskFromMap(urlString);
					}
					return bm;
				} else {
					// removeTaskFromMap(urlString);
				}
			} catch (Exception e) {
				e.printStackTrace();
				// removeTaskFromMap(urlString);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			// 回调设置图片
			if (downloadListener != null && result != null) {
				downloadListener.onImageDownload(result, url);
				// 该url对应的task已经下载完成，从map中将其删除
				// removeTaskFromMap(url);
			}
		}
	}

	// 强引用缓存类
	class MyLruCache extends LruCache<String, Bitmap> {
		public MyLruCache(int maxSize) {
			super(maxSize);
		}

		@Override
		protected int sizeOf(String key, Bitmap value) {
			return value.getHeight() * value.getWidth() * 4;
			// Bitmap图片的一个像素是4个字节
			// return value.getRowBytes() * value.getHeight();
		}

		@Override
		protected void entryRemoved(boolean evicted, String key,
									Bitmap oldValue, Bitmap newValue) {
			if (evicted) {
				SoftReference<Bitmap> softReference = new SoftReference<Bitmap>(
						oldValue);
				softCaches.put(key, softReference);
			}
		}
	}

	// SDCard工具类
	static class SDCardHelper {
		public static SDCardHelper sdCardHelper;

		public static SDCardHelper getInstance() {
			if (sdCardHelper == null) {
				sdCardHelper = new SDCardHelper();
			}
			return sdCardHelper;
		}

		// 判断SDCard是否挂载
		public boolean isSDCardMounted() {
			return Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED);
		}

		// 获取SDCard的根目录路径
		public String getSDCardBasePath() {
			if (isSDCardMounted()) {
				return Environment.getExternalStorageDirectory()
						.getAbsolutePath();
			} else {
				return null;
			}
		}

		// 获取SDCard的完整空间大小
		public long getSDCardTotalSize() {
			long size = 0;
			if (isSDCardMounted()) {
				StatFs statFs = new StatFs(getSDCardBasePath());
				if (Build.VERSION.SDK_INT >= 18) {
					size = statFs.getTotalBytes();
				} else {
					size = statFs.getBlockCount() * statFs.getBlockSize();
				}
				return size / 1024 / 1024;
			} else {
				return 0;
			}
		}

		// 获取SDCard的可用空间大小
		public long getSDCardAvailableSize() {
			long size = 0;
			if (isSDCardMounted()) {
				StatFs statFs = new StatFs(getSDCardBasePath());
				if (Build.VERSION.SDK_INT >= 18) {
					size = statFs.getAvailableBytes();
				} else {
					size = statFs.getAvailableBlocks() * statFs.getBlockSize();
				}
				return size / 1024 / 1024;
			} else {
				return 0;
			}
		}

		// 获取SDCard的剩余空间大小
		public long getSDCardFreeSize() {
			long size = 0;
			if (isSDCardMounted()) {
				StatFs statFs = new StatFs(getSDCardBasePath());
				if (Build.VERSION.SDK_INT >= 18) {
					size = statFs.getFreeBytes();
				} else {
					size = statFs.getFreeBlocks() * statFs.getBlockSize();
				}
				return size / 1024 / 1024;
			} else {
				return 0;
			}
		}

		// 保存byte[]文件到SDCard的指定公有目录
		public boolean saveFileToSDCardPublicDir(byte[] data, String type,
				String fileName) {
			if (isSDCardMounted()) {
				BufferedOutputStream bos = null;
				File file = Environment.getExternalStoragePublicDirectory(type);

				try {
					bos = new BufferedOutputStream(new FileOutputStream(
							new File(file, fileName)));
					bos.write(data);
					bos.flush();
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				} finally {
					if (bos != null) {
						try {
							bos.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			} else {
				return false;
			}
		}

		// 保存byte[]文件到SDCard的自定义目录
		public boolean saveFileToSDCardCustomDir(byte[] data, String dir,
				String fileName) {
			if (isSDCardMounted()) {
				BufferedOutputStream bos = null;
				File file = new File(getSDCardBasePath() + File.separator + dir);
				if (!file.exists()) {
					file.mkdirs();// 递归创建子目录
				}
				try {
					bos = new BufferedOutputStream(new FileOutputStream(
							new File(file, fileName)));
					bos.write(data, 0, data.length);
					bos.flush();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (bos != null) {
						try {
							bos.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				return true;
			} else {
				return false;
			}
		}

		// 保存byte[]文件到SDCard的指定私有Files目录
		public boolean saveFileToSDCardpublicDir(byte[] data, String type,
												 String fileName, Context context) {
			if (isSDCardMounted()) {
				BufferedOutputStream bos = null;
				// 获取私有Files目录
				File file = context.getExternalFilesDir(type);
				try {
					bos = new BufferedOutputStream(new FileOutputStream(
							new File(file, fileName)));
					bos.write(data, 0, data.length);
					bos.flush();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (bos != null) {
						try {
							bos.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				return true;
			} else {
				return false;
			}
		}

		// 保存byte[]文件到SDCard的私有Cache目录
		public boolean saveFileToSDCardpublicCacheDir(byte[] data,
													  String fileName, Context context) {
			if (isSDCardMounted()) {
				BufferedOutputStream bos = null;
				// 获取私有的Cache缓存目录
				File file = context.getExternalCacheDir();
				// Log.i("SDCardHelper", "==" + file);
				try {
					bos = new BufferedOutputStream(new FileOutputStream(
							new File(file, fileName)));
					bos.write(data, 0, data.length);
					bos.flush();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (bos != null) {
						try {
							bos.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				return true;
			} else {
				return false;
			}
		}

		// 保存bitmap图片到SDCard的私有Cache目录
		public boolean saveBitmapToSDCardPrivateCacheDir(Bitmap bitmap,
														 String fileName, Context context) {
			if (isSDCardMounted()) {
				BufferedOutputStream bos = null;
				// 获取私有的Cache缓存目录
				File file = context.getExternalCacheDir();
				try {
					bos = new BufferedOutputStream(new FileOutputStream(
							new File(file, fileName)));
					if (fileName != null
							&& (fileName.contains(".png") || fileName
									.contains(".PNG"))) {
						bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
					} else {
						bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
					}
					bos.flush();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (bos != null) {
						try {
							bos.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				return true;
			} else {
				return false;
			}
		}

		// 从SDCard中寻找指定目录下的文件，返回byte[]
		public byte[] loadFileFromSDCard(String filePath) {
			BufferedInputStream bis = null;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			File file = new File(filePath);
			if (file.exists()) {
				try {
					bis = new BufferedInputStream(new FileInputStream(file));
					byte[] buffer = new byte[1024 * 8];
					int c = 0;
					while ((c = (bis.read(buffer))) != -1) {
						baos.write(buffer, 0, c);
						baos.flush();
					}
					return baos.toByteArray();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (bis != null) {
						try {
							bis.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (baos != null) {
						try {
							baos.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
			return null;
		}

		// 从SDCard中寻找指定目录下的文件，返回Bitmap
		public Bitmap loadBitmapFromSDCard(String filePath) {
			byte[] data = loadFileFromSDCard(filePath);
			if (data != null) {
				Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
				if (bm != null) {
					return bm;
				}
			}
			return null;
		}

		// 获取SDCard私有的Cache目录
		public String getSDCardCachePath(Context context) {
			return context.getExternalCacheDir().getAbsolutePath();
		}

		// 获取SDCard私有的Files目录
		public String getSDCardFilePath(Context context, String type) {
			return context.getExternalFilesDir(type).getAbsolutePath();
		}

		// 从sdcard中删除文件
		public boolean removeFileFromSDCard(String filePath) {
			File file = new File(filePath);
			if (file.exists()) {
				try {
					file.delete();
					return true;
				} catch (Exception e) {
					return false;
				}
			} else {
				return false;
			}
		}
	}

	public String getImageName(String url) {
		String imageName = "";
		if (url != null) {
			imageName = url.substring(url.lastIndexOf("/") + 1);
		}
		return imageName;
	}

}
