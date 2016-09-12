package com.wyy.zhbj;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.wyy.zhbj.fragment.ContentFragment;
import com.wyy.zhbj.fragment.LeftMenuFragment;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

/**
 * 主页面
 * @author WANGYOUYU
 */
public class MainActivity extends SlidingFragmentActivity{
	
	private static final String TAG_LEFT_MENU = "TAG_LEFT_MENU";
	private static final String TAG_CONTENT = "TAG_CONTENT";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题,必须在setContentView之前调用
		setContentView(R.layout.activity_main);
		
		setBehindContentView(R.layout.left_menu);//设置侧边栏
		SlidingMenu slidingMenu = getSlidingMenu();//获取slidingMenu对象
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);//设置全屏触摸
		// 设置侧边栏宽度
		slidingMenu.setBehindOffset(200);//设置屏幕预留宽度(200像素)
		
		initFragment();
	}

	/**
	 * 初始化fragment
	 */
	private void initFragment(){
		FragmentManager fm = getSupportFragmentManager();//获取fragment管理器
		FragmentTransaction transaction = fm.beginTransaction();//开始事务
		//用fragment替换帧布局，参1：帧布局容器的id;参2:是要替换的fragment;参3:标记
		transaction.replace(R.id.fl_left_menu,new LeftMenuFragment(),TAG_LEFT_MENU);
		transaction.replace(R.id.fl_main,new ContentFragment(),TAG_CONTENT);
		transaction.commit();//提交事务
		
	}
	
	/**
	 * 获取侧边栏fragment对象
	 * @return
	 */
	public LeftMenuFragment getLeftMenuFragment(){
		FragmentManager fm = getSupportFragmentManager();//获取fragment管理器
		LeftMenuFragment fragment = (LeftMenuFragment) fm
				.findFragmentByTag(TAG_LEFT_MENU);//根据标记找到对应的fragment
		return fragment;
	}
	/**
	 * 获取主页的fragment对象
	 * @return
	 */
	public ContentFragment getContentFragment(){
		FragmentManager fm = getSupportFragmentManager();//获取fragment管理器
		ContentFragment fragment = (ContentFragment) fm
				.findFragmentByTag(TAG_CONTENT);//根据标记找到对应的fragment
		return fragment;
	}

}
