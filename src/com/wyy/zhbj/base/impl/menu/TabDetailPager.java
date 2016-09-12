package com.wyy.zhbj.base.impl.menu;

import java.util.ArrayList;
import android.os.Handler;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.viewpagerindicator.CirclePageIndicator;
import com.wyy.zhbj.NewsDetailActivity;
import com.wyy.zhbj.R;
import com.wyy.zhbj.base.BaseMenuDetailPager;
import com.wyy.zhbj.domain.NewsMenu.NewsTabData;
import com.wyy.zhbj.domain.NewsTabBean;
import com.wyy.zhbj.domain.NewsTabBean.NewsData;
import com.wyy.zhbj.domain.NewsTabBean.TopNews;
import com.wyy.zhbj.global.GlobalConstants;
import com.wyy.zhbj.utils.CacheUtils;
import com.wyy.zhbj.utils.PrefUtils;
import com.wyy.zhbj.view.PullToRefreshListView;
import com.wyy.zhbj.view.PullToRefreshListView.OnRefreshListener;
import com.wyy.zhbj.view.TopNewsViewPager;

/**
 * 页签页面对象
 * 
 * @author WANGYOUYU
 * 
 */
public class TabDetailPager extends BaseMenuDetailPager {
	public static String tag = "TabDetailPager";

	private NewsTabData mTabData; // 单个页签的网络数据
	// private TextView view;

	@ViewInject(R.id.vp_top_news)
	private TopNewsViewPager mViewPager;

	@ViewInject(R.id.indicator)
	private CirclePageIndicator mIndicator;

	@ViewInject(R.id.tv_title)
	private TextView tvTitle;

	@ViewInject(R.id.lv_list)
	private PullToRefreshListView lvList;

	private String mUrl;
	private ArrayList<TopNews> mTopnews;
	private ArrayList<NewsData> mNewsList;

	private NewsAdapter mNewsAdapter;
	private String mMoreUrl;// 下一页数据链接

	private Handler mHandler;

	public TabDetailPager(Activity activity, NewsTabData newsTabData) {
		super(activity);
		mTabData = newsTabData;

		mUrl = GlobalConstants.SERVER_URL + mTabData.url;
	}

	@Override
	public View initView() {
		// // 给帧布局填充布局对象
		// view = new TextView(mActivity);
		//
		// // view.setText(mTabData.title);///此处空指针
		//
		// view.setTextColor(Color.RED);
		// view.setTextSize(22);
		// view.setGravity(Gravity.CENTER);
		View view = View.inflate(mActivity, R.layout.pager_tab_datail, null);
		ViewUtils.inject(this, view);

		// 给listView添加头布局
		View mHeaderView = View.inflate(mActivity, R.layout.list_item_header,
				null);
		ViewUtils.inject(this, mHeaderView);// 此处必须将头布局也注入
		lvList.addHeaderView(mHeaderView);

		// 5.前端界面设置回调
		lvList.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				// 刷新数据
				getDataFromFromServer();
			}

			@Override
			public void onLoadMore() {
				// 判断是否有下一页数据
				if (mMoreUrl != null) {
					// 有下一页
					getMoreDataFromServer();
				} else {
					// 没有下一页
					Toast.makeText(mActivity, "没有更多数据啦", Toast.LENGTH_SHORT)
							.show();
					// 没有数据时也要收起控件
					lvList.onRefreshComplete(true);
				}
			}
		});

		lvList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int headerViewsCount = lvList.getHeaderViewsCount();// 获取头布局数量
				position = position - headerViewsCount;// 需要减去头布局的占位
				Log.i(tag, "第" + position + "个被点击啦");

				NewsData news = mNewsList.get(position);

				String readIds = PrefUtils.getString(mActivity, "read_ids", "");
				if (!readIds.contains(news.id + "")) {
					// 只有不包含当前id，才追加，避免重复添加同一个id
					readIds = readIds + news.id + ",";
					PrefUtils.setString(mActivity, "read_ids", readIds);
				}

				// 需要将被点击的item的文字颜色改为灰色，局部刷新，view对象就是当前被点击的对象
				TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
				tvTitle.setTextColor(Color.GRAY);
				// mNewsAdapter.notifyDataSetChanged();//全局刷新,浪费性能

				// 跳到新闻详情页面
				Intent intent = new Intent(mActivity, NewsDetailActivity.class);
				intent.putExtra("url", news.url);
				mActivity.startActivity(intent);
			}
		});

		return view;
	}

	@Override
	public void initData() {
		// view.setText(mTabData.title);
		String cache = CacheUtils.getCache(mUrl, mActivity);
		if (!TextUtils.isEmpty(cache)) {
			processedData(cache, false);
		}

		getDataFromFromServer();
	}

	private void getDataFromFromServer() {
		HttpUtils utils = new HttpUtils();
		utils.send(HttpMethod.GET, mUrl, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String result = responseInfo.result;
				processedData(result, false);

				CacheUtils.setCache(mUrl, result, mActivity);

				// 网络请求结束后收起下拉控件
				lvList.onRefreshComplete(true);

			}

			@Override
			public void onFailure(HttpException error, String msg) {
				// 请求失败
				error.printStackTrace();
				Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();

				// 网络请求失败后收起下拉控件
				lvList.onRefreshComplete(false);
			}
		});
	}

	/**
	 * 加载下一页数据
	 */
	protected void getMoreDataFromServer() {
		HttpUtils utils = new HttpUtils();
		utils.send(HttpMethod.GET, mMoreUrl, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String result = responseInfo.result;
				processedData(result, true);

				// 网络请求结束后收起下拉控件
				lvList.onRefreshComplete(true);

			}

			@Override
			public void onFailure(HttpException error, String msg) {
				// 请求失败
				error.printStackTrace();
				Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();

				// 网络请求失败后收起下拉控件
				lvList.onRefreshComplete(false);
			}
		});

	}

	protected void processedData(String result, boolean isMore) {
		Gson gson = new Gson();
		NewsTabBean newsTabBean = gson.fromJson(result, NewsTabBean.class);

		String moreUrl = newsTabBean.data.more;
		if (!TextUtils.isEmpty(moreUrl)) {
			mMoreUrl = GlobalConstants.SERVER_URL + moreUrl;
		} else {
			mMoreUrl = null;
		}

		if (!isMore) {
			// 头条新闻填充数据
			mTopnews = newsTabBean.data.topnews;
			if (mTopnews != null) {
				mViewPager.setAdapter(new TopNewsAdapter());
				mIndicator.setViewPager(mViewPager);
				mIndicator.setSnap(true);// 快照方式展示

				// 事件要设置给Indicator
				mIndicator.setOnPageChangeListener(new OnPageChangeListener() {

					@Override
					public void onPageSelected(int position) {
						// 更新头条新闻标题
						TopNews topNews = mTopnews.get(position);
						tvTitle.setText(topNews.title);
					}

					@Override
					public void onPageScrolled(int position,
							float positionOffset, int positionOffsetPixels) {
					}

					@Override
					public void onPageScrollStateChanged(int state) {
					}
				});

				// 更新第一个头天新闻标题
				tvTitle.setText(mTopnews.get(0).title);

				// 默认第一个选中(解决页面销毁后重新初始化时,Indicator仍然保留上次圆点位置的bug)
				mIndicator.onPageSelected(0);
			}

			// 列表新闻
			mNewsList = newsTabBean.data.news;
			if (mNewsList != null) {
				mNewsAdapter = new NewsAdapter();
				lvList.setAdapter(mNewsAdapter);
			}

			// 自动轮播
			if (mHandler == null) {
				mHandler = new Handler() {
					public void handleMessage(android.os.Message msg) {
						int currentItem = mViewPager.getCurrentItem();
						currentItem++;

						if (currentItem > mTopnews.size() - 1) {
							currentItem = 0;// 如果已经到了最后一个页面,跳到第一页
						}

						mViewPager.setCurrentItem(currentItem);
						mHandler.sendEmptyMessageDelayed(0, 3000);// 继续发送延时3秒的消息,形成内循环
					};
				};
				// 保证启动自动轮播逻辑只执行一次
				mHandler.sendEmptyMessageDelayed(0, 3000);// 发送延时3秒的消息

				mViewPager.setOnTouchListener(new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							// 停止广告自动轮播
							// 删除handler的所有消息
							mHandler.removeCallbacksAndMessages(null);
							// mHandler.post(new Runnable() {
							// @Override
							// public void run() {
							// // 在主线程运行
							// }
							// });
							break;
						case MotionEvent.ACTION_CANCEL://取消事件
							// 当按下viewpager后,直接滑动listview,导致抬起事件无法响应,但会走此事件
							//启动广告
							mHandler.sendEmptyMessageDelayed(0, 3000);
							break;
						case MotionEvent.ACTION_UP:
							//启动广告
							mHandler.sendEmptyMessageDelayed(0, 3000);
							break;
						default:
							break;
						}
						return false;
					}
				});
			}
		} else {
			// 加载更多数据
			ArrayList<NewsData> moreNews = newsTabBean.data.news;
			mNewsList.addAll(moreNews);// 将数据追加在原来的集合中
			// 刷新listview
			mNewsAdapter.notifyDataSetChanged();
		}
	}

	// 头条新闻适配器
	class TopNewsAdapter extends PagerAdapter {

		private BitmapUtils mBitmapUtils;

		public TopNewsAdapter() {
			mBitmapUtils = new BitmapUtils(mActivity);

			// 设置加载中的默认图片
			mBitmapUtils
					.configDefaultLoadingImage(R.drawable.topnews_item_default);
		}

		@Override
		public int getCount() {

			return mTopnews.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView view = new ImageView(mActivity);
			// view.setImageResource(R.drawable.topnews_item_default);
			view.setScaleType(ScaleType.FIT_XY); // 设置图片缩放方式，宽高填充父控件

			String imageUrl = mTopnews.get(position).topimage;// 图片下载链接

			// 下载图片 ——— 将图片设置给imageView ——— 避免内存溢出 ——— 缓存
			// BitmapUtils—XUtils
			mBitmapUtils.display(view, imageUrl);

			container.addView(view);
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

	}

	class NewsAdapter extends BaseAdapter {
		private BitmapUtils mBitmapUtils;

		public NewsAdapter() {
			mBitmapUtils = new BitmapUtils(mActivity);
			mBitmapUtils.configDefaultLoadingImage(R.drawable.news_pic_default);
		}

		@Override
		public int getCount() {
			return mNewsList.size();
		}

		@Override
		public NewsData getItem(int position) {
			return mNewsList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(mActivity, R.layout.list_item_news,
						null);
				holder = new ViewHolder();
				holder.ivIcon = (ImageView) convertView
						.findViewById(R.id.iv_icon);
				holder.tvTitle = (TextView) convertView
						.findViewById(R.id.tv_title);
				holder.tvDate = (TextView) convertView
						.findViewById(R.id.tv_date);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			NewsData news = getItem(position);
			holder.tvTitle.setText(news.title);
			holder.tvDate.setText(news.pubdate);

			// 根据本地记录来标记已读未读
			String readIds = PrefUtils.getString(mActivity, "read_ids", "");
			if (readIds.contains(news.id + "")) {
				holder.tvTitle.setTextColor(Color.GRAY);
			} else {
				holder.tvTitle.setTextColor(Color.BLACK);
			}

			mBitmapUtils.display(holder.ivIcon, news.listimage);
			return convertView;
		}

	}

	static class ViewHolder {
		public ImageView ivIcon;
		public TextView tvTitle;
		public TextView tvDate;
	}

}
