package edu.virginia.cs2110.ghosthunter;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

	private static final String TAG = GameView.class.getSimpleName();

	public MainThread thread;
	private Bitmap background;
	private boolean shoot;
	private int shootIterator;

	public GameView(Context context) {
		super(context);

		// adding the callback (this) to the surface holder to intercept events
		getHolder().addCallback(this);

		// create the game loop thread
		thread = new MainThread(getHolder(), this);

		// make the GamePanel focusable so it can handle events
		setFocusable(true);

		// Default Background Setup
		background = BitmapFactory.decodeResource(getResources(),
				R.drawable.brickbg);
		thread.setBackground(background);

		// Character Image Setup
		thread.setCharacterImage(BitmapFactory.decodeResource(getResources(),
				R.drawable.character));

		shoot = true;
		shootIterator = 0;

	}

	public GameView(Context context, JSONObject json) {
		super(context);

		// adding the callback (this) to the surface holder to intercept events
		getHolder().addCallback(this);

		// create the game loop thread
		thread = new MainThread(getHolder(), this, json);

		// make the GamePanel focusable so it can handle events
		setFocusable(true);

		// Default Background Setup
		background = BitmapFactory.decodeResource(getResources(),
				R.drawable.brickbg);
		thread.setBackground(background);

		// Character Image Setup
		thread.setCharacterImage(BitmapFactory.decodeResource(getResources(),
				R.drawable.character));

		shoot = true;
		shootIterator = 0;

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// Setup Background
		Bitmap toscale = BitmapFactory.decodeResource(getResources(),
				R.drawable.brickbg);
		Bitmap scaleVignette = BitmapFactory.decodeResource(getResources(),
				R.drawable.vignette);
		int dim;
		if (width > height)
			dim = width;
		else
			dim = height;
		Log.d(TAG, "DIM: " + dim);
		background = Bitmap.createScaledBitmap(toscale, dim, dim, true);
		// scaleVignette = Bitmap.createScaledBitmap(scaleVignette, width,
		// height, true);
		thread.setBackground(background);
		thread.setVignette(scaleVignette);
		thread.setScreenDimensions(width, height);
	}

	public void updateCharacterAcceleration(int x, int y) {
		thread.updateCharacterAcceleration(x, y);
	}

	public Bitmap getCoinBitmap() {
		return Bitmap.createScaledBitmap(
				BitmapFactory.decodeResource(getResources(), R.drawable.coin),
				50, 50, true);
	}

	public Bitmap getKitBitmap() {
		return Bitmap
				.createScaledBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.health), 50, 50, true);
	}

	public Bitmap getGhostBitmap() {
		int random = (int) (Math.floor(Math.random() * 3));
		switch (random) {
		case 0:
			return BitmapFactory.decodeResource(getResources(), R.drawable.g1);
		case 1:
			return BitmapFactory.decodeResource(getResources(), R.drawable.g2);
		case 2:
			return BitmapFactory.decodeResource(getResources(), R.drawable.g3);
		case 3:
			return BitmapFactory.decodeResource(getResources(), R.drawable.g4);
		default:
			return BitmapFactory.decodeResource(getResources(), R.drawable.g1);

		}
	}

	public Bitmap getGameOverBitmap() {
		Bitmap b = BitmapFactory.decodeResource(getResources(),
				R.drawable.gameover);
		return b;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// at this point the surface is created and
		// we can safely start the game loop
		thread.setRunning(true);
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, "Surface is being destroyed");
		// tell the thread to shut down and wait for it to finish
		// this is a clean shutdown
		boolean retry = true;
		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
				// try again shutting down the thread
				// Log.wtf("Help", "It failed");
			}
		}
		Log.d(TAG, "Thread was shut down cleanly");
	}

	public String stopAndSave() throws JSONException {
		thread.running = false;
		String savejson = thread.savegame();
		return savejson;
	}

	public void close() {
		synchronized (getHolder()) {
			((Activity) getContext()).finish();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (shoot) {
			thread.sendBullet(BitmapFactory.decodeResource(getResources(),
					R.drawable.bullet), (int) event.getX(), (int) event.getY());
			shoot = false;
		}

		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		shootIterator++;
		if (shootIterator > 50) {
			shoot = true;
			shootIterator = 0;
		}

	}
	// canvas.drawBitmap(BitmapFactory.decodeResource(getResources(),
	// R.drawable.vignette), 0, 0, null);

}
