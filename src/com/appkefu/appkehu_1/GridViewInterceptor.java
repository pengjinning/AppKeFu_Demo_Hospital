package com.appkefu.appkehu_1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

public class GridViewInterceptor extends GridView {
	private DropListener mDropListener;
	private View mDragItem;
	private ImageView mDragView;
	private int mDragPos; // which item is being dragged
	private int mFirstDragPos; // where was the dragged item originally
	private int mDragPointY; // at what offset inside the item did the user grab
								// it
	private int mDragPointX;// xiaochp
	private int mTempY;
	private int mTempX;

	private int initX = 0;
	private int initY = 0;

	private int mCoordOffsetY; // the difference between screen coordinates and
								// coordinates in this view
	private int mCoordOffsetX;// xiaochp
	private Rect mTempRect = new Rect();
	private final int mTouchSlop;
	private int mHeight;
	private int mUpperBound;
	private int mLowerBound;
	private WindowManager mWindowManager;
	private WindowManager.LayoutParams mWindowParams;
	private int dragndropBackgroundColor = 0x00000000;
	private Bitmap mDragBitmap;
	private int mItemHeightHalf = 32;
	private int mItemWidthHalf = 32;// xiaochp
	private int mItemHeightNormal = 64;
	private int mItemWidthNormal = 64;// xiaochp
	private int mItemHeightExpanded = 128;
	private int mItemWidthExpanded = 128;// xiaochp
	private ListMoveHandler mListMoveHandler;

	public GridViewInterceptor(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}

	public GridViewInterceptor(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		mListMoveHandler = new ListMoveHandler();
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		Log.v(">>>>>>>>>>onTouchEvent", ">>>>>>>>>>onTouchEvent");
		if ((mDropListener != null) && mDragView != null) {
			int action = ev.getAction();
			switch (action) {
			case MotionEvent.ACTION_UP:

			case MotionEvent.ACTION_CANCEL:
				Rect r = mTempRect;
				mDragView.getDrawingRect(r);
				stopDragging();
				if (mDropListener != null && mDragPos >= 0
						&& mDragPos < getCount()) {
					mDropListener.drop(mFirstDragPos, mDragPos);
				}
				if (mListMoveHandler.mIsStart) {
					mListMoveHandler.stop();
				}
				unExpandViews(false);
				break;

			case MotionEvent.ACTION_MOVE:
				int x = (int) ev.getX();
				int y = (int) ev.getY();

				{
					mTempX = x;
					mTempY = y;
					Log.v("mDragView>>>", ">>>getTop" + mDragView.getTop()
							+ ">>getBottom=" + mDragView.getBottom());
					if (y - mDragPointY < 0) {
						// out of top.
						// t = -mDragView.getTop();

						if (!mListMoveHandler.mIsStart) {
							mListMoveHandler.start(false);
						} else if (mListMoveHandler.mIsUp) {
							mListMoveHandler.stop();
							mListMoveHandler.start(false);
						}

					} else if ((y - mDragPointY + mItemHeightNormal + mCoordOffsetY) > 480) {
						// out of bottom.
						// t = mDragFrameHeight -
						// mDragView.getBitmapDrawRect().bottom;

						if (!mListMoveHandler.mIsStart) {
							mListMoveHandler.start(true);
						} else if (!mListMoveHandler.mIsUp) {
							mListMoveHandler.stop();
							mListMoveHandler.start(true);
						}

					} else {
						// between top and bottom.
						if (mListMoveHandler.mIsStart) {
							mListMoveHandler.stop();
						}
					}

				}

				dragView(x, y);

				int itemnum = getItemForPosition(x, y);
				if (itemnum >= 0) {
					if (action == MotionEvent.ACTION_DOWN
							|| itemnum != mDragPos) {

						mDragPos = itemnum;

						doExpansion();
						Log.v(">>>doExpansion", ">>>>>>>>>>doExpansion");
					}
				}
				break;
			}
			// return true;
		}
		return super.onTouchEvent(ev);

	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if (mDropListener != null) {
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				int x = (int) ev.getX();
				int y = (int) ev.getY();
				int itemnum = pointToPosition(x, y);
				Log.v("itemnum>>>", ">>>>>>>>" + itemnum);
				if (itemnum == AdapterView.INVALID_POSITION) {
					break;
				}
				View item = getChildAt(itemnum - getFirstVisiblePosition());
				Log.v("itemnum>>>", ">>>>>>>>" + getFirstVisiblePosition()
						+ "-RAW-Y-" + ev.getRawY() + "----" + ev.getY()
						+ "-----" + item.getTop());
				Log.v("itemnum>>>", ">>>>>>>>" + getFirstVisiblePosition()
						+ "-RAW-X-" + ev.getRawX() + "----" + ev.getX()
						+ "-----" + item.getLeft());
				mDragPointY = y - item.getTop();
				mDragPointX = x - item.getLeft();

				mCoordOffsetY = ((int) ev.getRawY()) - y;
				mCoordOffsetX = ((int) ev.getRawX()) - x;
				mDragItem = getChildAt(itemnum - getFirstVisiblePosition());
				// xiaochp View dragger = item.findViewById(R.id.img);
				Rect r = mTempRect;
				// dragger.getDrawingRect(r);

				r.left = mDragItem.getLeft();
				r.right = mDragItem.getRight();
				r.top = mDragItem.getTop();
				r.bottom = mDragItem.getBottom();

				mItemHeightHalf = (r.bottom - r.top) / 2;
				mItemWidthHalf = (r.right - r.left) / 2;// xiaochp
				mItemHeightNormal = r.bottom - r.top;
				mItemWidthNormal = r.right - r.left;// xiaochp
				mItemHeightExpanded = mItemHeightNormal;
				mItemWidthExpanded = mItemWidthNormal;// xiaochp

				if ((r.left < x) && (x < r.right)) {
					item.setDrawingCacheEnabled(true);
					// Create a copy of the drawing cache so that it does not
					// get recycled
					// by the framework when the list tries to clean up memory
					Bitmap bitmap = Bitmap.createBitmap(item.getDrawingCache());
					startDragging(bitmap, x, y);
					mDragPos = itemnum;
					mFirstDragPos = mDragPos;
					mHeight = getHeight();
					int touchSlop = mTouchSlop;
					mUpperBound = Math.min(y - touchSlop, mHeight / 3);
					mLowerBound = Math.max(y + touchSlop, mHeight * 2 / 3);
					return false;
				}
				mDragView = null;
				break;
			}
		}
		return super.onInterceptTouchEvent(ev);
	}

	private void startDragging(Bitmap bm, int x, int y) {
		stopDragging();

		mWindowParams = new WindowManager.LayoutParams();
		mWindowParams.gravity = Gravity.TOP | Gravity.LEFT;
		mWindowParams.x = x - mDragPointX + mCoordOffsetX;
		mWindowParams.y = y - mDragPointY + mCoordOffsetY;

		mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		mWindowParams.format = PixelFormat.TRANSLUCENT;
		mWindowParams.windowAnimations = 0;

		ImageView v = new ImageView(getContext());
		// int backGroundColor =
		// getContext().getResources().getColor(R.color.dragndrop_background);
		v.setBackgroundColor(dragndropBackgroundColor);
		v.setImageBitmap(bm);
		mDragBitmap = bm;

		mWindowManager = (WindowManager) getContext()
				.getSystemService("window");
		mWindowManager.addView(v, mWindowParams);
		mDragView = v;
	}

	private void stopDragging() {
		if (mDragView != null) {
			WindowManager wm = (WindowManager) getContext().getSystemService(
					"window");
			wm.removeView(mDragView);
			mDragView.setImageDrawable(null);
			mDragView = null;
		}
		if (mDragBitmap != null) {
			mDragBitmap.recycle();
			mDragBitmap = null;
		}
	}

	private void dragView(int x, int y) {
		float alpha = 1.0f;
		mWindowParams.alpha = alpha;
		// }
		mWindowParams.y = y - mDragPointY + mCoordOffsetY;
		mWindowParams.x = x - mDragPointX + mCoordOffsetX;
		Log.v("dragView", "mWindowParams" + "x=" + mWindowParams.x + "y="
				+ mWindowParams.y);
		mWindowManager.updateViewLayout(mDragView, mWindowParams);
	}

	// 需要调整以适应新的拖动算法
	private int getItemForPosition(int x, int y) {
		int adjustedy = y - mDragPointY + mItemHeightHalf; // 中心点
		int adjustedx = x - mDragPointX + mItemWidthHalf; // 中心点
		int pos = myPointToPosition(adjustedx, adjustedy);
		// 删除，保证中心点的移动
		// if (pos >= 0) {
		// if (pos <= mFirstDragPos) {
		// pos += 1;
		//
		// }
		// } else if (adjustedy < 0) {
		// pos = 0;
		// }
		Log.v("itempos>>>", ">>>>>>>>" + pos + ">>>>x=" + adjustedx + ">>>>y="
				+ adjustedy);
		return pos;
	}

	// 未调用 作用不明
	private void adjustScrollBounds(int y) {
		if (y >= mHeight / 3) {
			mUpperBound = mHeight / 3;
		}
		if (y <= mHeight * 2 / 3) {
			mLowerBound = mHeight * 2 / 3;
		}
	}

	/*
	 * Restore size and visibility for all listitems
	 */
	private void unExpandViews(boolean deletion) {
		for (int i = 0;; i++) {
			View v = getChildAt(i);
			if (v == null) {
				if (deletion) {
					// HACK force update of mItemCount
					int position = getFirstVisiblePosition();
					int y = getChildAt(0).getTop();
					setAdapter(getAdapter());
					// xiaochp setSelectionFromTop(position, y);
					// end hack
				}
				layoutChildren(); // force children to be recreated where needed
				v = getChildAt(i);
				if (v == null) {
					break;
				}
			}
			ViewGroup.LayoutParams params = v.getLayoutParams();
			params.height = mItemHeightNormal;
			v.setLayoutParams(params);
			v.setVisibility(View.VISIBLE);
		}
	}

	/*
	 * Adjust visibility and size to make it appear as though an item is being
	 * dragged around and other items are making room for it: If dropping the
	 * item would result in it still being in the same place, then make the
	 * dragged listitem's size normal, but make the item invisible. Otherwise,
	 * if the dragged listitem is still on screen, make it as small as possible
	 * and expand the item below the insert point. If the dragged item is not on
	 * screen, only expand the item below the current insertpoint.
	 */
	private void doExpansion() {
		int childnum = mDragPos - getFirstVisiblePosition();
		if (mDragPos > mFirstDragPos) {
			childnum++;
		}

		View first = getChildAt(mFirstDragPos - getFirstVisiblePosition());
		Log.v("vv.equals(mDragItem>>>", ">>first=" + first);
		for (int i = 0;; i++) {
			View vv = getChildAt(i);
			if (vv == null) {
				Log.v("vv.equals(mDragItem>>>", "break>>i=" + i);
				break;
			}
			int height = mItemHeightNormal;
			int visibility = View.VISIBLE;
			// xiaochp 删除 用于单纯换数据
			// if (vv.equals(first)) {
			// // processing the item that is being dragged
			// if (mDragPos == mFirstDragPos) {
			// // hovering over the original location
			// visibility = View.INVISIBLE;
			// } else {
			// // not hovering over it
			// height = 1;
			// }
			// } else if (i == childnum) {
			// if (mDragPos < getCount() - 1) {
			// height = mItemHeightExpanded;
			// }
			// }
			if (vv.equals(mDragItem) && first != null) {
				// processing the item that is being dragged
				Log.v("vv.equals(mDragItem>>>", ">>vv=" + vv + ">>mDragItem="
						+ mDragItem);
				Log.v("vv.equals(mDragItem>>>", ">>i=" + i + ">>mDragPos="
						+ mDragPos + ">mFirstDragPos=" + mFirstDragPos
						+ ">>childnum" + childnum);
				visibility = View.INVISIBLE;
			}
			ViewGroup.LayoutParams params = vv.getLayoutParams();
			params.height = height;
			vv.setLayoutParams(params);
			vv.setVisibility(visibility);
		}
	}

	/*
	 * pointToPosition() doesn't consider invisible views, but we need to, so
	 * implement a slightly different version.
	 */
	private int myPointToPosition(int x, int y) {
		Rect frame = mTempRect;
		final int count = getChildCount();
		for (int i = count - 1; i >= 0; i--) {
			final View child = getChildAt(i);
			child.getHitRect(frame);
			if (frame.contains(x, y)) {
				return getFirstVisiblePosition() + i;
			}
		}
		return INVALID_POSITION;
	}

	public interface DropListener {
		void drop(int from, int to);
	}

	public void setDropListener(DropListener onDrop) {
		// TODO Auto-generated method stub
		mDropListener = onDrop;
	}

	private class ListMoveHandler extends Handler {

		private final int SCROLLDISTANCE = 20;
		private final int SCROLLDURATION = 200;
		private final int MESSAGEWHAT = 111;
		private final int MESSAGEDELAY = 100;

		private boolean mIsStart = false;
		private boolean mIsUp = false;

		public void start(boolean isUp) {
			mIsUp = isUp;
			this.mIsStart = true;
			this.sendEmptyMessageDelayed(MESSAGEWHAT, MESSAGEDELAY);
		}

		public void stop() {
			this.mIsStart = false;
			this.removeMessages(MESSAGEWHAT);
		}

		public void myDragView() {
			GridViewInterceptor.this.dragView(GridViewInterceptor.this.mTempX,
					GridViewInterceptor.this.mTempY);

			int itemnum = getItemForPosition(GridViewInterceptor.this.mTempX,
					GridViewInterceptor.this.mTempY);
			if (itemnum >= 0) {
				if (itemnum != mDragPos) {

					GridViewInterceptor.this.mDragPos = itemnum;

					GridViewInterceptor.this.doExpansion();
					Log.v(">>>doExpansion", ">>>>>>>>>>doExpansion");
				}
			}
		}

		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);

			if (mIsUp) {
				myDragView();
				// GridViewInterceptor.this.smoothScrollBy(SCROLLDISTANCE,
				// SCROLLDURATION);
			} else {
				myDragView();
				// GridViewInterceptor.this.smoothScrollBy(-SCROLLDISTANCE,
				// SCROLLDURATION);
			}

			if (mIsStart) {
				this.sendEmptyMessageDelayed(MESSAGEWHAT, MESSAGEDELAY);
			}
		}
	}
}
