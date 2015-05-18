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

	private List<Card> cards = new ArrayList<Card>();// һ��card���������治�õ�card���󣬽�ʡ�ظ���������Դ�˷�

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
	 * �ƶ����� ��������ʼ����ֹcard�����Լ�������
	 */
	public void createMoveAnim(final Card from, final Card to, int fromX,
			int fromY, int toX, int toY) {
		final Card c = getCard(from.getNum());// ����һ����card����ֵ��fromcard��ͬ
		LayoutParams lp = new LayoutParams(Config.CARD_WIDTH, Config.CARD_WIDTH);// ���ֲ�������
		// fromCard��߽�ľ���
		lp.leftMargin = fromX * Config.CARD_WIDTH;
		lp.topMargin = fromY * Config.CARD_WIDTH;
		// Ϊ��card����ͬ���Ĳ��ֲ���
		c.setLayoutParams(lp);

		if (to.getNum() <= 0) {// �Ƿ���ʾtocard
			to.getLabel().setVisibility(View.INVISIBLE);
		}
		// λ�ƶ���,����λ�Ʋ��� deltax deltay
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
				recycleCard(c); // ������ʱcard
			}
		});
		c.startAnimation(trans);

	}
	//��card������ȡ
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
	 * ���Ŷ���
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
