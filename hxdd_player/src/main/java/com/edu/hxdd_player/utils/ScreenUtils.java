package com.edu.hxdd_player.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * 获得屏幕相关的辅助类
 * 
 * @author kang
 * 
 */
public class ScreenUtils
{
	/**
	 * 获得屏幕宽度
	 */
	public static int getScreenWidth(Context context)
	{
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.widthPixels;
	}

	/**
	 * 获得屏幕高度
	 */
	public static int getScreenHeight(Context context)
	{
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.heightPixels;
	}

	/**
	 * 获得状态栏的高度
	 */
	public static int getStatusHeight(Context context)
	{
		int statusHeight = -1;
		try
		{
			Class<?> clazz = Class.forName("com.android.internal.R$dimen");
			Object object = clazz.newInstance();
			int height = Integer.parseInt(clazz.getField("status_bar_height")
					.get(object).toString());
			statusHeight = context.getResources().getDimensionPixelSize(height);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return statusHeight;
	}

	/**
	 * 获取当前屏幕截图，包含状态栏
	 * 
	 * @param activity 需要截图的activity
	 * @return 截图bitmap
	 */
	public static Bitmap snapShotWithStatusBar(Activity activity)
	{
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bmp = view.getDrawingCache();
		int width = getScreenWidth(activity);
		int height = getScreenHeight(activity);
		Bitmap bp = null;
		bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
		view.destroyDrawingCache();
		return bp;

	}

	/**
	 * 获取当前屏幕截图，不包含状态栏
	 * 
	 * @param activity
	 * @return Bitmap
	 */
	public static Bitmap snapShotWithoutStatusBar(Activity activity)
	{
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bmp = view.getDrawingCache();
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;

		int width = getScreenWidth(activity);
		int height = getScreenHeight(activity);
		Bitmap bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height
				- statusBarHeight);
		view.destroyDrawingCache();
		return bp;
	}

	/**
	 * 设置view高度
	 * @param view 设置的view
	 * @param proportion 屏幕高度的几分之一
     */
	public static void resetHeight(View view, float proportion) {
		resetHeight(view, proportion, 0);
	}

	/**
	 * 设置view高度
	 * @param view 设置的view
	 * @param proportion 屏幕高度的几分之一
	 * @param offset 便宜修正
	 */
	public static void resetHeight(View view, float proportion, int offset) {
		ViewGroup.LayoutParams lp = view.getLayoutParams();
		lp.height = (int)(getScreenHeight(view.getContext()) / proportion) + offset;
		view.setLayoutParams(lp);
	}

	/**
	 * 设置view宽
	 * @param view 设置的view
	 * @param proportion 屏幕宽度的几分之一
	 */
	public static void resetWidth(View view, float proportion ) {
		ViewGroup.LayoutParams lp =  view.getLayoutParams();
		lp.width = (int)(getScreenWidth(view.getContext()) / proportion);
		view.setLayoutParams(lp);
	}
}
