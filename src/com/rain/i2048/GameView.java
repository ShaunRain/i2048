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

	private Card[][] cardsMap = new Card[Config.LINES][Config.LINES]; // 保存每个card的矩阵
	private List<Point> emptyPoints = new ArrayList<Point>(); // 用以保存数值为空的card

	public GameView(Context context) {
		super(context);

		initGameView();
	}

	private void initGameView() { // 游戏布局初始化方法
		// TODO Auto-generated method stub
		setOrientation(LinearLayout.VERTICAL); // 以vertical
												// linear为主体，以一行为单位addview
		setBackgroundColor(0xffbbada0);

		setOnTouchListener(new OnTouchListener() { // 触摸响应

			private float fromX, fromY, offsetX, offsetY; // 四个变量：初始位置的x，y
															// 增量的x，y

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

					if (Math.abs(offsetX) > Math.abs(offsetY)) {// x方向增量大
						if (offsetX < -10)
							swipeLeft();
						else if (offsetX > 10)
							swipeRight();
					} else if (Math.abs(offsetX) < Math.abs(offsetY)) {// y方向增量大
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
	 * 根据屏幕尺寸改变改变card尺寸，强制了屏幕竖直，所以只在GameView第一次载入调用
	 * 
	 * @see android.view.View#onSizeChanged(int, int, int, int)
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);

		Config.CARD_WIDTH = (Math.min(w, h) - 10) / Config.LINES;// 设置card尺寸
		addCards(Config.CARD_WIDTH, Config.CARD_WIDTH);// 初始化所有card
		startGame();
	}

	public void addCards(int cardWidth, int cardHeight) { // 向布局框架里加入cards
		Card c;
		LinearLayout ll;
		LinearLayout.LayoutParams lp;

		for (int y = 0; y < Config.LINES; y++) {
			ll = new LinearLayout(getContext());
			lp = new LinearLayout.LayoutParams(-1, cardHeight);// 每一行的布局，横向填充，纵向card长度
			addView(ll, lp);
			for (int x = 0; x < Config.LINES; x++) {
				c = new Card(getContext());
				ll.addView(c, cardWidth, cardHeight);
				// 初始化cards数组，初始值均为0（不显示数字）
				cardsMap[x][y] = c;
				cardsMap[x][y].setNum(0);
			}
		}

	}

	/*
	 * 开始游戏
	 */
	public void startGame() {
		MainActivity main = MainActivity.getMainActivity();

		main.clearScore();// 清空计分板
		main.showBestScore(main.getBestScore());// 从sp中读取最高纪录，设置记录板
		main.showCompleted(main.getCompleted());// 从sp中读取完成次数,设置

		for (int y = 0; y < Config.LINES; y++) {
			for (int x = 0; x < Config.LINES; x++) {
				cardsMap[x][y].setNum(0);
			}
		}

		// 随机添加两个card
		addRandomCard();
		addRandomCard();
	}

	public void addRandomCard() {

		emptyPoints.clear();

		// 初始化空值表
		for (int y = 0; y < Config.LINES; y++) {
			for (int x = 0; x < Config.LINES; x++) {
				if (cardsMap[x][y].getNum() <= 0) {
					emptyPoints.add(new Point(x, y));
				}
			}
		}

		if (!emptyPoints.isEmpty()) { // 若有空卡片存在
			Point p = emptyPoints.remove((int) (Math.random() * emptyPoints
					.size())); // 随机取出一个
			cardsMap[p.x][p.y].setNum(Math.random() >= 0.1 ? 2 : 4); // 随机设置数字2?4

			// 为新添加的card 设置缩放“登场”动画
			MainActivity.getMainActivity().getAnimLayer()
					.createScaleTo1(cardsMap[p.x][p.y]);
		}

	}

	public void swipeLeft() {
		boolean merge = false;// 是否发生合并

		for (int y = 0; y < Config.LINES; y++) {
			for (int x = 0; x < Config.LINES; x++) {// 遍历保存card的矩阵数组
				for (int x1 = x + 1; x1 < Config.LINES; x1++) {// 从当前card的右边开始
					if (cardsMap[x1][y].getNum() > 0) {// 存在不为空的卡时
						if (cardsMap[x][y].getNum() <= 0) {// 如果当前card为空,直接移动至当前card
							MainActivity
									.getMainActivity()
									.getAnimLayer()
									.createMoveAnim(cardsMap[x1][y],
											cardsMap[x][y], x1, y, x, y);// 移动动画
							cardsMap[x][y].setNum(cardsMap[x1][y].getNum());// 数值设置到当前card
							cardsMap[x1][y].setNum(0);// 移动card置空

							x--;// 这种“合并”只是一个card的移动，继续判断（直到发生一次合并为止）
							merge = true;// 发生了“合并”
						} else if (cardsMap[x1][y].equals(cardsMap[x][y])) {// 如果当前card不为空且与移动card数值相同
							MainActivity
									.getMainActivity()
									.getAnimLayer()
									.createMoveAnim(cardsMap[x1][y],
											cardsMap[x][y], x1, y, x, y);
							cardsMap[x][y].setNum(cardsMap[x][y].getNum() * 2);// 当前card数值乘二
							cardsMap[x1][y].setNum(0);// 移动card置空

							MainActivity.getMainActivity().addScore(
									cardsMap[x][y].getNum());// 分数增加
							merge = true;// 发生合并
						}
						break;
					}
				}
			}
		}
		if (merge) {// 如发生了合并，移动。会生成新card，判断游戏是否结束
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
	 * 检查游戏是否结束
	 */
	public void checkComplete() {
		boolean complete = true;// 判断是否失完成
		boolean win_flag = false;// 判断是否失败

		// 遍历card矩阵数组
		ALL: for (int y = 0; y < Config.LINES; y++) {
			for (int x = 0; x < Config.LINES; x++) {
				// 1.有空卡 2.有两个相邻card数值相等. 还未结束
				if (cardsMap[x][y].getNum() == 0
						|| (x > 0 && cardsMap[x][y].equals(cardsMap[x - 1][y]))
						|| (x < Config.LINES - 1 && cardsMap[x][y]
								.equals(cardsMap[x + 1][y]))
						|| (y > 0 && cardsMap[x][y].equals(cardsMap[x][y - 1]))
						|| (y < Config.LINES - 1 && cardsMap[x][y]
								.equals(cardsMap[x][y + 1]))) {
					complete = false;
					break ALL;
				} else if (cardsMap[x][y].getNum() == 2048) {// 存在2048，游戏结束，获胜
					win_flag = true;
					break ALL;

				}
			}

		}

		if (complete && win_flag) {// 结束且获胜
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
									startGame();// 重开

								}
							}).show();
		} else if (complete && !win_flag) {// 结束未获胜
			new AlertDialog.Builder(getContext())
					.setCancelable(false)
					.setTitle("很遗憾")
					.setMessage("Game Over!")
					.setPositiveButton("Confirm",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									startGame();// 重开
								}
							}).show();
		}

	}
}
