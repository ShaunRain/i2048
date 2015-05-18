package com.rain.i2048;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

public class AnimLayer extends FrameLayout {

	private List<Card> cards = new ArrayList<Card>();// 一个card容器，缓存不用的card对象，节省重复创建的资源浪费

	public AnimLayer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}

	public AnimLayer(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public AnimLayer(Context context) {
		super(context);

	}

	/*
	 * 移动动画 参数：起始和终止card对象以及其坐标
	 */
	public void createMoveAnim(final Card from, final Card to, int fromX,
			int fromY, int toX, int toY) {
		final Card c = getCard(from.getNum());// 创建一个新card，数值与fromcard相同
		LayoutParams lp = new LayoutParams(Config.CARD_WIDTH, Config.CARD_WIDTH);// 布局参数对象
		// fromCard与边界的距离
		lp.leftMargin = fromX * Config.CARD_WIDTH;
		lp.topMargin = fromY * Config.CARD_WIDTH;
		// 为新card设置同样的布局参数
		c.setLayoutParams(lp);

		if (to.getNum() <= 0) {// 是否显示tocard
			to.getLabel().setVisibility(View.INVISIBLE);
		}
		// 位移动画,设置位移参数 deltax deltay
		TranslateAnimation trans = new TranslateAnimation(0, Config.CARD_WIDTH
				* (toX - fromX), 0, Config.CARD_WIDTH * (toY - fromY));
		trans.setDuration(150);
		trans.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				to.getLabel().setVisibility(View.VISIBLE);
				recycleCard(c); // 回收临时card
			}
		});
		c.startAnimation(trans);

	}
	//从card容器中取
	private Card getCard(int num) {
		Card c;
		if (cards.size() > 0) {
			c = cards.remove(0);
		} else {
			c = new Card(getContext());
			addView(c);
		}
		c.setVisibility(VISIBLE);
		c.setNum(num);
		return c;
	}

	private void recycleCard(Card c) {
		c.setVisibility(INVISIBLE);
		c.setAnimation(null);
		c.setNum(0);
		cards.add(c);
	}

	/*
	 * 缩放动画
	 */
	public void createScaleTo1(Card target) {
		ScaleAnimation scale = new ScaleAnimation(0.1f, 1, 0.1f, 1,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		scale.setDuration(150);
		target.setAnimation(null);
		target.getLabel().startAnimation(scale);
	}
}
