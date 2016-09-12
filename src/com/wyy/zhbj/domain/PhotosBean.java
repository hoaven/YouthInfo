package com.wyy.zhbj.domain;

import java.util.ArrayList;

/**
 * 组图对象
 * @author WANGYOUYU
 *
 */
public class PhotosBean {
	public PhotosData data;
	
	public class PhotosData{
		public ArrayList<PhotoNews> news;
	}
	
	public class PhotoNews{
		public int id;
		public String listimage;
		public String title;
	}

}
