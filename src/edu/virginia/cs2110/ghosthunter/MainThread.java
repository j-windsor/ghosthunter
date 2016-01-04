package edu.virginia.cs2110.ghosthunter;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;

public class MainThread extends Thread {

	private static final String TAG = MainThread.class.getSimpleName();

	private SurfaceHolder surfaceHolder;
	private GameView gamePanel;
	public boolean running;
	private Bitmap bgimage;
	private Bitmap vignette;
	private Bitmap characterimage;
	private int charX;
	private int charY;
	private int screenX = 1200;
	private int screenY = 1920;
	private ArrayList<Coin> coinsOnScreen;
	private ArrayList<Ghost> ghostsOnScreen;
	private ArrayList<Bullet> bulletsOnScreen;
	private ArrayList<Coin> kitsOnScreen;
	private Rect charRect;
	private Paint scorePaint;
	private int score;
	private int bullets;
	public boolean gameOver;
	private long tickCount;
	private int diff;

	// private boolean gameOver;

	public void setRunning(boolean running) {
		this.running = running;
	}

	public MainThread(SurfaceHolder surfaceHolder, GameView gamePanel) {
		super();
		this.gameOver = false;
		this.surfaceHolder = surfaceHolder;
		this.gamePanel = gamePanel;
		coinsOnScreen = new ArrayList<Coin>();
		ghostsOnScreen = new ArrayList<Ghost>();
		bulletsOnScreen = new ArrayList<Bullet>();
		kitsOnScreen = new ArrayList<Coin>();
		charRect = new Rect();
		scorePaint = new Paint();
		scorePaint.setColor(Color.GREEN);
		scorePaint.setTextSize(40);
		score = 100;
		bullets = 7;
		charX = 350;
		charY = 500;
		diff = 100;
		this.tickCount = 0L;
	}

	public MainThread(SurfaceHolder surfaceHolder, GameView gamePanel,
			JSONObject main) {
		super();
		this.surfaceHolder = surfaceHolder;
		this.gamePanel = gamePanel;
		coinsOnScreen = new ArrayList<Coin>();
		ghostsOnScreen = new ArrayList<Ghost>();
		bulletsOnScreen = new ArrayList<Bullet>();
		kitsOnScreen = new ArrayList<Coin>();
		charRect = new Rect();
		scorePaint = new Paint();
		scorePaint.setColor(Color.GREEN);
		scorePaint.setTextSize(40);
		score = 100;
		bullets = 7;
		charX = 0;
		charY = 0;
		diff = 100;
		this.tickCount = 0L;

		try {
			JSONObject charOb = main.getJSONObject("character");
			charX = charOb.getInt("x");
			Log.d("dadf", "JSON CHARX is: " + charX);
			charY = charOb.getInt("y");
			score = main.getInt("score");
			bullets = main.getInt("bullets");
			tickCount = main.getLong("tickCount");
			Log.d("DJDJDJDJDJD", "TickCount is: " + tickCount);
			JSONArray ghosts = main.getJSONArray("ghost");
			for (int i = 0; i < ghosts.length(); i++) {
				JSONObject obj = ghosts.getJSONObject(i);
				ghostsOnScreen.add(new Ghost(gamePanel.getGhostBitmap(), obj
						.getInt("x"), obj.getInt("y"), obj.getInt("dx"), obj
						.getInt("dy"), obj.getBoolean("hit")));
			}
			JSONArray coins = main.getJSONArray("coin");
			for (int i = 0; i < coins.length(); i++) {
				JSONObject obj = coins.getJSONObject(i);
				coinsOnScreen.add(new Coin(gamePanel.getCoinBitmap(), obj
						.getInt("x"), obj.getInt("y"), false));
			}
			JSONArray kits = main.getJSONArray("kit");
			for (int i = 0; i < kits.length(); i++) {
				JSONObject obj = kits.getJSONObject(i);
				kitsOnScreen.add(new Coin(gamePanel.getKitBitmap(), obj
						.getInt("x"), obj.getInt("y"), false));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void setBackground(Bitmap image) {
		this.bgimage = image;
	}

	public void setCharacterImage(Bitmap image) {
		this.characterimage = Bitmap.createScaledBitmap(image, 100, 100, true);
	}

	public void setVignette(Bitmap image) {
		this.vignette = image;
	}

	public void setScreenDimensions(int x, int y) {
		this.screenX = x - 75;
		this.screenY = y - 75;
		Log.d("ACCELEROMETER", "X: " + screenX + "Y: " + screenY);
	}

	public void updateCharacterAcceleration(int x, int y) {
		this.charX -= x * 2;
		this.charY += y * 2;
		if (charX >= screenX)
			charX = screenX;
		if (charX <= 0)
			charX = 0;
		if (charY >= screenY)
			charY = screenY;
		if (charY <= 0)
			charY = 0;
		charRect.set(charX, charY, charX + characterimage.getWidth(), charY
				+ characterimage.getHeight());
		// Log.d("ACCELEROMETER", "X: "+ charX + "Y: " + charY);
	}

	public void sendBullet(Bitmap bitmap, int touchX, int touchY) {
		if (bullets != 0) {
			bulletsOnScreen
					.add(new Bullet(bitmap, touchX, touchY, charX, charY));
			bullets--;
		}
		if (gameOver) {
			gamePanel.close();
		}
	}

	private void gameOver(Canvas c) {
		gameOver = true;
		coinsOnScreen.clear();
		ghostsOnScreen.clear();
		bulletsOnScreen.clear();
		kitsOnScreen.clear();
		Bitmap b = gamePanel.getGameOverBitmap();
		int dim;
		if (screenX > screenY)
			dim = screenX;
		else
			dim = screenY;
		Log.d(TAG, "DIM: " + dim);
		b = Bitmap.createScaledBitmap(b, screenX + 80, screenY + 125, true);
		c.drawBitmap(b, 0, 0, null);
		this.running = false;

	}

	public String savegame() throws JSONException {
		JSONObject main = new JSONObject();
		JSONArray g = new JSONArray();
		for (Ghost c : ghostsOnScreen) {
			JSONObject pnObj = new JSONObject();
			pnObj.put("x", c.getX());
			pnObj.put("y", c.getY());
			pnObj.put("dx", c.getDX());
			pnObj.put("dy", c.getDY());
			pnObj.put("hit", c.getHit());
			g.put(pnObj);
		}
		main.put("ghost", g);
		JSONArray coins = new JSONArray();
		for (Coin c : coinsOnScreen) {
			JSONObject pnObj = new JSONObject();
			pnObj.put("x", c.getX());
			pnObj.put("y", c.getY());
			coins.put(pnObj);
		}
		main.put("coin", coins);
		JSONArray kits = new JSONArray();
		for (Coin c : kitsOnScreen) {
			JSONObject pnObj = new JSONObject();
			pnObj.put("x", c.getX());
			pnObj.put("y", c.getY());
			kits.put(pnObj);
		}
		main.put("kit", kits);
		JSONObject pnObj = new JSONObject();
		pnObj.put("x", charX);
		pnObj.put("y", charY);
		main.put("character", pnObj);
		main.put("score", score);
		main.put("bullets", bullets);
		main.put("tickCount", tickCount);

		return main.toString();

	}

	@SuppressLint("WrongCall")
	@Override
	public void run() {
		// tickCount = 0L;
		Log.d(TAG, "Starting game loop");
		Canvas canvas;
		while (running) {
			while (running) {
				tickCount++;
				canvas = null;
				// PROCESSING DONE HERE
				if (tickCount % 700 == 0)
					coinsOnScreen.add(new Coin(gamePanel.getCoinBitmap(),
							screenX, screenY));
				if (tickCount % (int) (100 - .007 * tickCount) == 0)
					ghostsOnScreen.add(new Ghost(gamePanel.getGhostBitmap(),
							screenX, screenY, charX, charY));
				// if(tickCount%100 == 0) ghostsOnScreen.add(new
				// Ghost(gamePanel.getGhostBitmap(), screenX, screenY, charX,
				// charY));

				// try locking the canvas for exclusive pixel editing on the
				// surface
				try {
					canvas = this.surfaceHolder.lockCanvas();
					synchronized (surfaceHolder) {
						// update game state
						// draws the canvas on the panel
						canvas.drawBitmap(bgimage, 0, 0, null); // draw the
																// background
						for (int i = 0; i < bulletsOnScreen.size(); i++) {
							Bullet c = bulletsOnScreen.get(i);
							c.draw(canvas);
							if (c.getX() < -200 || c.getX() > screenX + 200
									|| c.getY() < -200
									|| c.getY() > screenY + 200) {
								bulletsOnScreen.remove(c);
							}
						}
						canvas.drawBitmap(characterimage, charX, charY, null); // draw
																				// the
																				// character
						for (int i = 0; i < coinsOnScreen.size(); i++) {
							Coin c = coinsOnScreen.get(i);
							c.draw(canvas);
							if (c.bounds().intersect(charRect)) {
								coinsOnScreen.remove(c);
								bullets += 5;
							}
						}

						for (int i = 0; i < kitsOnScreen.size(); i++) {
							Coin c = kitsOnScreen.get(i);
							c.draw(canvas);
							if (c.bounds().intersect(charRect)) {
								kitsOnScreen.remove(c);
								score += 3;
							}
						}

						for (int i = 0; i < ghostsOnScreen.size(); i++) {
							Ghost c = ghostsOnScreen.get(i);
							c.draw(canvas);
							// canvas.drawRect(c.bounds(), new Paint());
							for (int j = 0; j < bulletsOnScreen.size(); j++) {
								if (bulletsOnScreen.get(j).bounds()
										.intersect(c.bounds())) {
									if (Math.random() > .7) {
										kitsOnScreen.add(new Coin(gamePanel
												.getKitBitmap(), screenX,
												screenY, (int) c.getX() + 100,
												(int) c.getY() + 100));
									}
									ghostsOnScreen.remove(c);
								}
							}
							if (c.bounds().intersect(charRect)) {
								c.setHit(false);
								score -= 5;
							}
							// Remove offscreen ghosts
							if (c.getX() < -200 || c.getX() > screenX + 200
									|| c.getY() < -200
									|| c.getY() > screenY + 200) {
								ghostsOnScreen.remove(c);
							}
						}
						// canvas.drawBitmap(vignette, 0, 0, null); // draw the
						// vignette
						canvas.drawText("Health: " + score, screenX - 150, 50,
								scorePaint);
						canvas.drawText("Shots: " + bullets, screenX - 150,
								100, scorePaint);

						this.gamePanel.onDraw(canvas);
						if (score <= 0) {
							gameOver(canvas);
						}
					}
				} finally {
					// in case of an exception the surface is not left in
					// an inconsistent state
					if (canvas != null) {
						surfaceHolder.unlockCanvasAndPost(canvas);
					}
				} // end finally
			}
		}
		Log.d(TAG, "Game loop executed " + tickCount + " times");
	}
}
