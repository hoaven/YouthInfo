package com.wyy.zhbj.utils;

import android.content.Context;

/**
 * 网络缓存工具类
 * @author WANGYOUYU
 *
 */
public class CacheUtils {
	
	/**
	 * 写缓存
	 * 以url为key，以json为value,保存到本地
	 * @param url
	 * @param json
	 */
    public static void setCache(String url,String json,Context ctx){
    	//也可以用文件缓存：以MD5(url)为文件名，以json为文件内容
    	PrefUtils.setString(ctx, url, json);
    }
    
    /**
     * 获取缓存
     * @param url
     * @param ctx
     */
    public static String getCache(String url,Context ctx){
    	//文件缓存：查找有没有一个文件叫做MD5(url)，有的话，说明有缓存
    	return PrefUtils.getString(ctx, url, null);
    }
}
