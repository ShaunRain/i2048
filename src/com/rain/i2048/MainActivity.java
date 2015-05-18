package com.rain.i2048;

import com.rain.i2048.R;

import android.app.Activity;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

	private int score = 0; // ��ǰ����
	private TextView tvScore, tvBestScore, tvComplete; // �Ʒְ�,��¼��,��ɰ�
	private LinearLayout root = null; //
	private Button btnNewGame; // start��ť
	private GameView gameView; // ��Ϸ���
	private AnimLayer animLayer = null; // �������

	private static MainActivity mainActivity = null; // Ϊ�ⲿ�ṩ�����ݽ����ӿ�

	public MainActivity() {
		mainActivity = this;
	}

	public static MainActivity getMainActivity() {
		return mainActivity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		root = (LinearLayout) findViewById(R.id.container);
		root.setBackgroundColor(0xfffaf8ef);

		tvScore = (TextView) findViewById(R.id.tvScore);
		tvBestScore = (TextView) findViewById(R.id.tvBestScore);
		tvComplete = (TextView) findViewById(R.id.tvComplete);

		gameView = (GameView) findViewById(R.id.gameView);

		btnNewGame = (Button) findViewById(R.id.btnNewGame);
		btnNewGame.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				gameView.startGame();
			}
		});

		animLayer = (AnimLayer) findViewById(R.id.animLayer);
	}

	public void clearScore() {
		score = 0;
		showScore();
	}

	public void showScore() {
		tvScore.setText(score + "");
	}

	public void addScore(int s) {
		score += s;
		showScore();

		int maxScore = getBestScore();
		if (score >= maxScore)// �÷ֳ�����¼��ʱ����¼�����
			maxScore = score;
		saveBestScore(maxScore);
		showBestScore(maxScore);
	}

	public void saveBestScore(int s) {
		Editor editor = getPreferences(MODE_PRIVATE).edit();
		editor.putInt("BestScore", s);
		editor.commit();
	}

	public int getBestScore() {
		return getPreferences(MODE_PRIVATE).getInt("BestScore", 0);
	}

	public void showBestScore(int s) {
		tvBestScore.setText(s + "");
	}

	public void saveCompleted(int t) {
		Editor editor = getPreferences(MODE_PRIVATE).edit();
		editor.putInt("Completed", t);
		editor.commit();
	}

	public int getCompleted() {
		return getPreferences(MODE_PRIVATE).getInt("Completed", 0);
	}

	public void showCompleted(int t) {
		tvComplete.setText(t + "");
	}

	public AnimLayer getAnimLayer() {
		return animLayer;
	}
}
