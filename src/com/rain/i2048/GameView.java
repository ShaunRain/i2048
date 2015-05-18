package com.rain.i2048;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

@SuppressLint("ClickableViewAccessibility")
public class GameView extends LinearLayout {

	private Card[][] cardsMap = new Card[Config.LINES][Config.LINES]; // ����ÿ��card�ľ���
	private List<Point> emptyPoints = new ArrayList<Point>(); // ���Ա�����ֵΪ�յ�card

	public GameView(Context context) {
		super(context);

		initGameView();
	}

	private void initGameView() { // ��Ϸ���ֳ�ʼ������
		// TODO Auto-generated method stub
		setOrientation(LinearLayout.VERTICAL); // ��vertical
												// linearΪ���壬��һ��Ϊ��λaddview
		setBackgroundColor(0xffbbada0);

		setOnTouchListener(new OnTouchListener() { // ������Ӧ

			private float fromX, fromY, offsetX, offsetY; // �ĸ���������ʼλ�õ�x��y
															// ������x��y

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					fromX = event.getX();
					fromY = event.getY();
					break;
				case MotionEvent.ACTION_UP:
					offsetX = event.getX() - fromX;
					offsetY = event.getY() - fromY;

					if (Math.abs(offsetX) > Math.abs(offsetY)) {// x����������
						if (offsetX < -10)
							swipeLeft();
						else if (offsetX > 10)
							swipeRight();
					} else if (Math.abs(offsetX) < Math.abs(offsetY)) {// y����������
						if (offsetY < -10)
							swipeUp();
						else if (offsetY > 10)
							swipeDown();
					}
					break;
				}

				return true;
			}
		});

	}

	/*
	 * ������Ļ�ߴ�ı�ı�card�ߴ磬ǿ������Ļ��ֱ������ֻ��GameView��һ���������
	 * 
	 * @see android.view.View#onSizeChanged(int, int, int, int)
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);

		Config.CARD_WIDTH = (Math.min(w, h) - 10) / Config.LINES;// ����card�ߴ�
		addCards(Config.CARD_WIDTH, Config.CARD_WIDTH);// ��ʼ������card
		startGame();
	}

	public void addCards(int cardWidth, int cardHeight) { // �򲼾ֿ�������cards
		Card c;
		LinearLayout ll;
		LinearLayout.LayoutParams lp;

		for (int y = 0; y < Config.LINES; y++) {
			ll = new LinearLayout(getContext());
			lp = new LinearLayout.LayoutParams(-1, cardHeight);// ÿһ�еĲ��֣�������䣬����card����
			addView(ll, lp);
			for (int x = 0; x < Config.LINES; x++) {
				c = new Card(getContext());
				ll.addView(c, cardWidth, cardHeight);
				// ��ʼ��cards���飬��ʼֵ��Ϊ0������ʾ���֣�
				cardsMap[x][y] = c;
				cardsMap[x][y].setNum(0);
			}
		}

	}

	/*
	 * ��ʼ��Ϸ
	 */
	public void startGame() {
		MainActivity main = MainActivity.getMainActivity();

		main.clearScore();// ��ռƷְ�
		main.showBestScore(main.getBestScore());// ��sp�ж�ȡ��߼�¼�����ü�¼��
		main.showCompleted(main.getCompleted());// ��sp�ж�ȡ��ɴ���,����

		for (int y = 0; y < Config.LINES; y++) {
			for (int x = 0; x < Config.LINES; x++) {
				cardsMap[x][y].setNum(0);
			}
		}

		// ����������card
		addRandomCard();
		addRandomCard();
	}

	public void addRandomCard() {

		emptyPoints.clear();

		// ��ʼ����ֵ��
		for (int y = 0; y < Config.LINES; y++) {
			for (int x = 0; x < Config.LINES; x++) {
				if (cardsMap[x][y].getNum() <= 0) {
					emptyPoints.add(new Point(x, y));
				}
			}
		}

		if (!emptyPoints.isEmpty()) { // ���пտ�Ƭ����
			Point p = emptyPoints.remove((int) (Math.random() * emptyPoints
					.size())); // ���ȡ��һ��
			cardsMap[p.x][p.y].setNum(Math.random() >= 0.1 ? 2 : 4); // �����������2?4

			// Ϊ����ӵ�card �������š��ǳ�������
			MainActivity.getMainActivity().getAnimLayer()
					.createScaleTo1(cardsMap[p.x][p.y]);
		}

	}

	public void swipeLeft() {
		boolean merge = false;// �Ƿ����ϲ�

		for (int y = 0; y < Config.LINES; y++) {
			for (int x = 0; x < Config.LINES; x++) {// ��������card�ľ�������
				for (int x1 = x + 1; x1 < Config.LINES; x1++) {// �ӵ�ǰcard���ұ߿�ʼ
					if (cardsMap[x1][y].getNum() > 0) {// ���ڲ�Ϊ�յĿ�ʱ
						if (cardsMap[x][y].getNum() <= 0) {// �����ǰcardΪ��,ֱ���ƶ�����ǰcard
							MainActivity
									.getMainActivity()
									.getAnimLayer()
									.createMoveAnim(cardsMap[x1][y],
											cardsMap[x][y], x1, y, x, y);// �ƶ�����
							cardsMap[x][y].setNum(cardsMap[x1][y].getNum());// ��ֵ���õ���ǰcard
							cardsMap[x1][y].setNum(0);// �ƶ�card�ÿ�

							x--;// ���֡��ϲ���ֻ��һ��card���ƶ��������жϣ�ֱ������һ�κϲ�Ϊֹ��
							merge = true;// �����ˡ��ϲ���
						} else if (cardsMap[x1][y].equals(cardsMap[x][y])) {// �����ǰcard��Ϊ�������ƶ�card��ֵ��ͬ
							MainActivity
									.getMainActivity()
									.getAnimLayer()
									.createMoveAnim(cardsMap[x1][y],
											cardsMap[x][y], x1, y, x, y);
							cardsMap[x][y].setNum(cardsMap[x][y].getNum() * 2);// ��ǰcard��ֵ�˶�
							cardsMap[x1][y].setNum(0);// �ƶ�card�ÿ�

							MainActivity.getMainActivity().addScore(
									cardsMap[x][y].getNum());// ��������
							merge = true;// �����ϲ�
						}
						break;
					}
				}
			}
		}
		if (merge) {// �緢���˺ϲ����ƶ�����������card���ж���Ϸ�Ƿ����
			addRandomCard();
			checkComplete();
		}
	}

	public void swipeRight() {
		boolean merge = false;

		for (int y = 0; y < Config.LINES; y++) {
			for (int x = Config.LINES - 1; x >= 0; x--) {
				for (int x1 = x - 1; x1 >= 0; x1--) {
					if (cardsMap[x1][y].getNum() > 0) {
						if (cardsMap[x][y].getNum() <= 0) {
							MainActivity
									.getMainActivity()
									.getAnimLayer()
									.createMoveAnim(cardsMap[x1][y],
											cardsMap[x][y], x1, y, x, y);
							cardsMap[x][y].setNum(cardsMap[x1][y].getNum());
							cardsMap[x1][y].setNum(0);

							x++;
							merge = true;
						} else if (cardsMap[x1][y].equals(cardsMap[x][y])) {
							MainActivity
									.getMainActivity()
									.getAnimLayer()
									.createMoveAnim(cardsMap[x1][y],
											cardsMap[x][y], x1, y, x, y);
							cardsMap[x][y].setNum(cardsMap[x][y].getNum() * 2);
							cardsMap[x1][y].setNum(0);

							MainActivity.getMainActivity().addScore(
									cardsMap[x][y].getNum());
							merge = true;
						}
						break;
					}
				}
			}
		}
		if (merge) {
			addRandomCard();
			checkComplete();
		}
	}

	public void swipeUp() {
		boolean merge = false;

		for (int x = 0; x < Config.LINES; x++) {
			for (int y = 0; y < Config.LINES; y++) {
				for (int y1 = y + 1; y1 < Config.LINES; y1++) {
					if (cardsMap[x][y1].getNum() > 0) {
						if (cardsMap[x][y].getNum() <= 0) {
							MainActivity
									.getMainActivity()
									.getAnimLayer()
									.createMoveAnim(cardsMap[x][y1],
											cardsMap[x][y], x, y1, x, y);
							cardsMap[x][y].setNum(cardsMap[x][y1].getNum());
							cardsMap[x][y1].setNum(0);

							y--;
							merge = true;
						} else if (cardsMap[x][y1].equals(cardsMap[x][y])) {
							MainActivity
									.getMainActivity()
									.getAnimLayer()
									.createMoveAnim(cardsMap[x][y1],
											cardsMap[x][y], x, y1, x, y);
							cardsMap[x][y].setNum(cardsMap[x][y].getNum() * 2);
							cardsMap[x][y1].setNum(0);

							MainActivity.getMainActivity().addScore(
									cardsMap[x][y].getNum());
							merge = true;
						}
						break;
					}
				}
			}
		}
		if (merge) {
			addRandomCard();
			checkComplete();
		}
	}

	public void swipeDown() {
		boolean merge = false;

		for (int x = 0; x < Config.LINES; x++) {
			for (int y = Config.LINES - 1; y >= 0; y--) {

				for (int y1 = y - 1; y1 >= 0; y1--) {
					if (cardsMap[x][y1].getNum() > 0) {

						if (cardsMap[x][y].getNum() <= 0) {
							MainActivity
									.getMainActivity()
									.getAnimLayer()
									.createMoveAnim(cardsMap[x][y1],
											cardsMap[x][y], x, y1, x, y);
							cardsMap[x][y].setNum(cardsMap[x][y1].getNum());
							cardsMap[x][y1].setNum(0);

							y++;
							merge = true;
						} else if (cardsMap[x][y1].equals(cardsMap[x][y])) {
							MainActivity
									.getMainActivity()
									.getAnimLayer()
									.createMoveAnim(cardsMap[x][y1],
											cardsMap[x][y], x, y1, x, y);
							cardsMap[x][y].setNum(cardsMap[x][y].getNum() * 2);
							cardsMap[x][y1].setNum(0);
							MainActivity.getMainActivity().addScore(
									cardsMap[x][y].getNum());
							merge = true;
						}

						break;
					}
				}
			}
		}

		if (merge) {
			addRandomCard();
			checkComplete();
		}
	}

	/*
	 * �����Ϸ�Ƿ����
	 */
	public void checkComplete() {
		boolean complete = true;// �ж��Ƿ�ʧ���
		boolean win_flag = false;// �ж��Ƿ�ʧ��

		// ����card��������
		ALL: for (int y = 0; y < Config.LINES; y++) {
			for (int x = 0; x < Config.LINES; x++) {
				// 1.�пտ� 2.����������card��ֵ���. ��δ����
				if (cardsMap[x][y].getNum() == 0
						|| (x > 0 && cardsMap[x][y].equals(cardsMap[x - 1][y]))
						|| (x < Config.LINES - 1 && cardsMap[x][y]
								.equals(cardsMap[x + 1][y]))
						|| (y > 0 && cardsMap[x][y].equals(cardsMap[x][y - 1]))
						|| (y < Config.LINES - 1 && cardsMap[x][y]
								.equals(cardsMap[x][y + 1]))) {
					complete = false;
					break ALL;
				} else if (cardsMap[x][y].getNum() == 2048) {// ����2048����Ϸ��������ʤ
					win_flag = true;
					break ALL;

				}
			}

		}

		if (complete && win_flag) {// �����һ�ʤ
			new AlertDialog.Builder(getContext())
					.setCancelable(false)
					.setTitle("You Win!")
					.setMessage(
							"Final Score:"
									+ MainActivity.getMainActivity()
											.getBestScore())
					.setPositiveButton("Confirm",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									int completed = MainActivity
											.getMainActivity().getCompleted();
									MainActivity.getMainActivity()
											.saveCompleted(++completed);
									startGame();// �ؿ�

								}
							}).show();
		} else if (complete && !win_flag) {// ����δ��ʤ
			new AlertDialog.Builder(getContext())
					.setCancelable(false)
					.setTitle("���ź�")
					.setMessage("Game Over!")
					.setPositiveButton("Confirm",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									startGame();// �ؿ�
								}
							}).show();
		}

	}
}
