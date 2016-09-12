package com.wyy.zhbj.utils;

import com.wyy.zhbj.R;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

/**
 * 自定义三级缓存图片加载工具
 * 
 * @author WANGYOUYU
 * 
 */
public class MyBitmapUtils {

	public static String tag = "MyBitmapUtils";

	private NetCacheUtils mNetCacheUtils;
	private LocalCacheUtils mLocalCacheUtils;
	private MemoryCacheUtils mMemoryCacheUtils;

	public MyBitmapUtils() {
		mMemoryCacheUtils = new MemoryCacheUtils();
		mLocalCacheUtils = new LocalCacheUtils();
		mNetCacheUtils = new NetCacheUtils(mLocalCacheUtils,mMemoryCacheUtils);

	}

	public void display(ImageView imageView, String url) {
		// 设置默认图片
		imageView.setImageResource(R.drawable.pic_item_list_default);

		// 优先从内存图片，其次从本地(sdcard)加载图片，最后从网络下载图片
		Bitmap bitmap = mMemoryCacheUtils.getMemoryCache(url);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
			Log.i(tag, "从内存加载图片啦");
			return;
		}

		// 其次从本地(sdcard)加载图片, 速度快, 不浪费流量
		bitmap = mLocalCacheUtils.getLocalCache(url);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
			Log.i(tag, "从本地加载图片啦");

			 //写内存缓存
			 mMemoryCacheUtils.setMemoryCache(url, bitmap);
			return;
		}

		// 从网络下载图片
		mNetCacheUtils.getBitmapFromNet(imageView, url);

	}

	private void setMemoryCache(String url, Bitmap bitmap) {
		// TODO Auto-generated method stub
		
	}

}
