package com.wyy.zhbj.base.impl.menu;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.viewpagerindicator.TabPageIndicator;
import com.wyy.zhbj.MainActivity;
import com.wyy.zhbj.R;
import com.wyy.zhbj.base.BaseMenuDetailPager;
import com.wyy.zhbj.domain.NewsMenu.NewsTabData;

/**
 * 新闻菜单详情页 ViewPagerIndicator使用流程： 1.引入库 2.解决support-v4冲突(让两个版本一致)
 * 3.从例子程序中拷贝布局文件 4.从例子程序中拷贝相关代码(指示器和viewpager绑定，重写getPageTitle返回标题)
 * 5.在清单文件中增加样式 6.在布局文件中修改背景 7.修改样式--背景样式&文字样式
 * 
 * @author WANGYOUYU
 * 
 */
public class NewsMenuDetailPager extends BaseMenuDetailPager implements
		OnPageChangeListener {
	public static String tag = "NewsMenuDetailPager";

	@ViewInject(R.id.vp_news_menu_detail)
	private ViewPager mViewPager;

	@ViewInject(R.id.indicator)
	private TabPageIndicator mIndicator;

	private ArrayList<NewsTabData> mTabData;// 页签网络数据
	private ArrayList<TabDetailPager> mPagers;// 页签页面集合

	public NewsMenuDetailPager(Activity activity,
			ArrayList<NewsTabData> children) {
		super(activity);
		mTabData = children;
	}

	@Override
	public View initView() {
		View view = View.inflate(mActivity, R.layout.pager_news_menu_datail,
				null);
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	public void initData() {
		// 初始化页签
		mPagers = new ArrayList<TabDetailPager>();
		for (int i = 0; i < mTabData.size(); i++) {
			TabDetailPager pager = new TabDetailPager(mActivity,
					mTabData.get(i));
			mPagers.add(pager);
		}
		mViewPager.setAdapter(new NewsMenuDetailAdapter());

		// 将viewpager和指示器绑定在一起. 注意：必须在viewpager设置完数据之后再绑定
		mIndicator.setViewPager(mViewPager);

		// 设置页面滑动监听
		// mViewPager.setOnPageChangeListener(this);
		mIndicator.setOnPageChangeListener(this);// 此处必须给指示器设置页面监听，不能设置给viewPager

	}

	class NewsMenuDetailAdapter extends PagerAdapter {

		// 指定指示器的标题
		@Override
		public CharSequence getPageTitle(int position) {
			NewsTabData data = mTabData.get(position);
			return data.title;
		}

		@Override
		public int getCount() {
			return mPagers.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			TabDetailPager pager = mPagers.get(position);
			View view = pager.mRootView;
			container.addView(view);
			pager.initData();
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
	}

	@Override
	public void onPageSelected(int position) {
		Log.i(tag, "当前位置：" + position);
		if (position == 0) {
			// 开启侧边栏
			setSlidingMenuEnable(true);
		} else {
			// 禁用侧边栏
			setSlidingMenuEnable(false);

		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}

	/**
	 * 开启或禁用侧边栏
	 * 
	 * @param enable
	 */
	protected void setSlidingMenuEnable(boolean enable) {
		// 获取侧边栏对象
		MainActivity mainUI = (MainActivity) mActivity;
		SlidingMenu slidingMenu = mainUI.getSlidingMenu();
		if (enable) {
			slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		} else {
			slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		}

	}

	@OnClick(R.id.btn_next)
	public void nextPage(View view) {
		//跳到下个页面
		int currentItem = mViewPager.getCurrentItem();
		currentItem++;
		mViewPager.setCurrentItem(currentItem);
		
  
	}

}
