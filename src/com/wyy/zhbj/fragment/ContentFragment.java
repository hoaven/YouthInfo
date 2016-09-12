package com.wyy.zhbj.fragment;

import java.util.ArrayList;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.wyy.zhbj.MainActivity;
import com.wyy.zhbj.R;
import com.wyy.zhbj.base.BasePager;
import com.wyy.zhbj.base.impl.GovAffairsPager;
import com.wyy.zhbj.base.impl.HomePager;
import com.wyy.zhbj.base.impl.NewsCenterPager;
import com.wyy.zhbj.base.impl.SettingPager;
import com.wyy.zhbj.base.impl.SmartServicePager;
import com.wyy.zhbj.view.NoScrollViewPager;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class ContentFragment extends BaseFragment {

	private NoScrollViewPager mViewPager;
	private RadioGroup rgGroup;

	private ArrayList<BasePager> mPagers;// 五个标签页的集合

	@Override
	public View initView() {
		View view = View.inflate(mActivity, R.layout.fragment_content, null);
		mViewPager = (NoScrollViewPager) view.findViewById(R.id.vp_content);
		rgGroup = (RadioGroup) view.findViewById(R.id.rg_group);
		return view;
	}

	@Override
	public void initData() {
		mPagers = new ArrayList<BasePager>();
		// 添加5个标签页
		mPagers.add(new HomePager(mActivity));// 首页
		mPagers.add(new NewsCenterPager(mActivity));// 新闻中心
		mPagers.add(new SmartServicePager(mActivity));// 智慧服务
		mPagers.add(new GovAffairsPager(mActivity));// 政务
		mPagers.add(new SettingPager(mActivity));// 设置

		mViewPager.setAdapter(new ContentAdapter());

		rgGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			// 底栏标签切换监听
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_home:  //首页
                  mViewPager.setCurrentItem(0,false);//参2：表示是否具有滑动动画
					break;
				case R.id.rb_news:  //新闻中心
					mViewPager.setCurrentItem(1,false);
					break;
				case R.id.rb_smart:  //智慧服务
					mViewPager.setCurrentItem(2,false);
					break;
				case R.id.rb_gov:  //政务
					mViewPager.setCurrentItem(3,false);
					break;
				case R.id.rb_setting:  //设置
					mViewPager.setCurrentItem(4,false);

					break;
				}

			}
		});
		 mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				BasePager pager = mPagers.get(position);
				pager.initData();
				
				if(position==0 || position==mPagers.size()-1){
					//首页和设置页要禁用侧边栏
					setSlidingMenuEnable(false);
				}else{
					//其他页面开启侧边栏
					setSlidingMenuEnable(true);
				}
			}
			
			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
			}
			
			@Override
			public void onPageScrollStateChanged(int state) {
				
			}
		});
		 mPagers.get(0).initData();//手动加载第一页数据
		 setSlidingMenuEnable(false);//首页禁用侧边栏
	}

	/**
	 * 开启或禁用侧边栏
	 * @param enable
	 */
	protected void setSlidingMenuEnable(boolean enable) {
		//获取侧边栏对象
		MainActivity mainUI = (MainActivity) mActivity;
		SlidingMenu slidingMenu = mainUI.getSlidingMenu();
		if(enable){
			slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		}else{
			slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		}
		
	}

	class ContentAdapter extends PagerAdapter {

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
			BasePager pager = mPagers.get(position);
			View view = pager.mRootView;// 获取当前页面对象的布局
			container.addView(view);

			/*pager.initData();//初始化数据
			 * viewpager会默认加载下一个页面
			 * 为了节省流量和性能,不要在此处调用初始化数据的方法
			 * */
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

	}
	
	//获取新闻中心的页面
	public NewsCenterPager getNewsCenterPager(){
		NewsCenterPager pager = (NewsCenterPager) mPagers.get(1);
		return pager;
	}

}
