package com.wyy.zhbj.base.impl;

import java.util.ArrayList;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.wyy.zhbj.MainActivity;
import com.wyy.zhbj.base.BaseMenuDetailPager;
import com.wyy.zhbj.base.BasePager;
import com.wyy.zhbj.base.impl.menu.InteractMenuDetailPager;
import com.wyy.zhbj.base.impl.menu.NewsMenuDetailPager;
import com.wyy.zhbj.base.impl.menu.PhotosMenuDetailPager;
import com.wyy.zhbj.base.impl.menu.TopicMenuDetailPager;
import com.wyy.zhbj.domain.NewsMenu;
import com.wyy.zhbj.fragment.LeftMenuFragment;
import com.wyy.zhbj.global.GlobalConstants;
import com.wyy.zhbj.utils.CacheUtils;

/**
 * 新闻中心
 * 
 * @author WANGYOUYU
 * 
 */
public class NewsCenterPager extends BasePager {
	
	private ArrayList<BaseMenuDetailPager> mMenuDetailPagers;//菜单详情页集合
	
	private NewsMenu mNewsData;// 分类信息网络数据

	public static String tag = "NewsCenterPager";

	public NewsCenterPager(Activity activity) {
		super(activity);
	}

	@Override
	public void initData() {
		System.out.println("新闻中心初始化啦....");

//		// 给帧布局填充布局对象
//		TextView view = new TextView(mActivity);
//		view.setText("新闻中心");
//		view.setTextColor(Color.RED);
//		view.setTextSize(22);
//		view.setGravity(Gravity.CENTER);
//		flContent.addView(view);

		// 修改页面布局
		tvTitle.setText("新闻");

		// 显示菜单按钮
		btnMenu.setVisibility(View.VISIBLE);

		// 先判断有没有缓存,如果有的话就加载缓存
		String cache = CacheUtils.getCache(GlobalConstants.CATEGORY_URL,
				mActivity);
		if (!TextUtils.isEmpty(cache)) {
			Log.i(tag, "发现缓存啦......");
			processData(cache);
		}
		// 请求服务器,获取数据
		// 开源框架：XUtils
		getDataFromServer();
	}

	/**
	 * 从服务器获取数据 需要权限
	 */
	private void getDataFromServer() {
		HttpUtils utils = new HttpUtils();
		utils.send(HttpMethod.GET, GlobalConstants.CATEGORY_URL,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						// 请求成功
						String result = responseInfo.result;// 获取服务器返回结果
						Log.i(tag, "服务器返回结果：" + result);

						// 写缓存
						CacheUtils.setCache(GlobalConstants.CATEGORY_URL,
								result, mActivity);

						processData(result);

					}

					@Override
					public void onFailure(HttpException error, String msg) {
						// 请求失败
						error.printStackTrace();
						Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT)
								.show();
					}
				});
	}

	protected void processData(String json) {
		Gson gson = new Gson();
		mNewsData = gson.fromJson(json, NewsMenu.class);
		Log.i(tag, "解析结果：" + mNewsData);
		
		//获取侧边栏对象
		MainActivity mainUI = (MainActivity) mActivity;
		LeftMenuFragment fragment = mainUI.getLeftMenuFragment();
		
		//给侧边栏设置数据
		fragment.setMenuData(mNewsData.data);
		
		//初始化4个菜单详情页
		mMenuDetailPagers = new ArrayList<BaseMenuDetailPager>();
		mMenuDetailPagers.add(new NewsMenuDetailPager(mActivity,mNewsData.data.get(0).children));//新闻
		mMenuDetailPagers.add(new TopicMenuDetailPager(mActivity));//专题
		mMenuDetailPagers.add(new PhotosMenuDetailPager(mActivity,btnPhoto));//组图
		mMenuDetailPagers.add(new InteractMenuDetailPager(mActivity));//互动
		
		//将新闻菜单详情页设置为默认页面
		setCurrentDetailPager(0);
	}
	
	//设置菜单详情页
	public void setCurrentDetailPager(int position){
		//重新给FrameLayout添加内容
		BaseMenuDetailPager pager = mMenuDetailPagers.get(position);//获取当前该显示的页面
		View view = pager.mRootView;//当前页面的布局
		flContent.removeAllViews();//清除之前的布局
		flContent.addView(view);//给帧布局添加布局
		pager.initData();//初始化页面数据
		tvTitle.setText(mNewsData.data.get(position).title);//更新标题
		
		//如果是组图页面,需要显示切换页面
		if(pager instanceof PhotosMenuDetailPager){
			btnPhoto.setVisibility(View.VISIBLE);
		}else{
			//隐藏切换按钮
			btnPhoto.setVisibility(View.GONE);
		}
	}

}
