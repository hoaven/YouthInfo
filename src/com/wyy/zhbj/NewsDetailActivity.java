package com.wyy.zhbj;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.TextSize;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

/**
 * 新闻详情页
 * 
 * @author WANGYOUYU
 * 
 */
public class NewsDetailActivity extends Activity implements OnClickListener{
	public static String tag = "NewsDetailActivity";

	@ViewInject(R.id.ll_control)
	private LinearLayout llControl;

	@ViewInject(R.id.btn_back)
	private ImageButton btnBack;

	@ViewInject(R.id.btn_textsize)
	private ImageButton btnTextSize;

	@ViewInject(R.id.btn_share)
	private ImageButton btnShare;

	@ViewInject(R.id.btn_menu)
	private ImageButton btnMenu;

	@ViewInject(R.id.wv_news_detail)
	private WebView mWebView;
	
	@ViewInject(R.id.pb_loading)
	private ProgressBar pbLoading;

	private String mUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_news_detail);
		ViewUtils.inject(this);

		llControl.setVisibility(View.VISIBLE);
		btnBack.setVisibility(View.VISIBLE);
		btnMenu.setVisibility(View.GONE);
		
		btnBack.setOnClickListener(this);
		btnTextSize.setOnClickListener(this);
		btnShare.setOnClickListener(this);
		
		mUrl = getIntent().getStringExtra("url");

//		mWebView.loadUrl("http://www.baidu.com");//用于测试
		mWebView.loadUrl(mUrl);

		WebSettings settings = mWebView.getSettings();
		settings.setBuiltInZoomControls(true);// 显示缩放按钮(wap网页不支持)
		settings.setUseWideViewPort(true);// 支持双击缩放(wap网页不支持)
		settings.setJavaScriptEnabled(true);// 支持js功能

		mWebView.setWebViewClient(new WebViewClient() {
			// 开始加载网页
			public void onPageStarted(WebView view, String url,
					android.graphics.Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				Log.i(tag, "开始加载网页啦");
				pbLoading.setVisibility(View.VISIBLE);
			}
			
			//网页加载结束
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				Log.i(tag, "网页加载啦");
			}
			
			//所有链接跳转会走此方法
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Log.i(tag, "跳转路径:"+ url);
				view.loadUrl(url); //在跳转链接时强制在当前webview中加载
				return true;
			}
		});
//		mWebView.goBack();//跳到上个页面
//		mWebView.goForward();//跳到下个页面
		
		mWebView.setWebChromeClient(new WebChromeClient(){
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				//进度条发生变化
				Log.i(tag, "进度:"+ newProgress);	
			}
			
			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
				//网页标题
				Log.i(tag, "网页标题:"+ title);
			}
		});

	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_back:
			finish();
			break;
			
		case R.id.btn_textsize:
			//修改网页字体大小
			showChooseDialog();
			
			break;

			
		case R.id.btn_share:
			break;

		}
		
	}
	
	private int mTempWhich;// 记录临时选择的字体大小(点击确定之前)
	private int mCurrenWhich = 2;// 记录当前选中的字体大小(点击确定之后), 默认正常字体
	/**
	 * 展示选择字体大小的弹框
	 */
	private void showChooseDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("字体设置");
		
		String[] items = new String[] { "超大号字体", "大号字体", "正常字体", "小号字体",
		"超小号字体" };

		builder.setSingleChoiceItems(items,mCurrenWhich,new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mTempWhich = which;
				
			}
		});
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//根据选择的字体来修改网页字体大小
				
				WebSettings settings = mWebView.getSettings();
				
				switch (mTempWhich) {
				case 0:
					// 超大字体
					settings.setTextSize(TextSize.LARGEST);
					// settings.setTextZoom(22);//自己定义字体大小
					break;
				case 1:
					// 大字体
					settings.setTextSize(TextSize.LARGER);
					break;
				case 2:
					// 正常字体
					settings.setTextSize(TextSize.NORMAL);
					break;
				case 3:
					// 小字体
					settings.setTextSize(TextSize.SMALLER);
					break;
				case 4:
					// 超小字体
					settings.setTextSize(TextSize.SMALLEST);
					break;

				default:
					break;
				}
				mCurrenWhich = mTempWhich;
			}
		});
		
		builder.setNegativeButton("取消", null);
		builder.show();
	}

}
