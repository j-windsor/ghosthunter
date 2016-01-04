package edu.virginia.cs2110.ghosthunter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

class Ghost {
	private Bitmap bitmap;
	private int x;
	int y;
	int screenX;
	int screenY;
	double sControl;
	int dy;
	int dx;
	boolean hit;
	Paint ghostPaint;

	public Ghost(Bitmap bitmap, int screenX, int screenY, int charX, int charY) {
		this.bitmap = bitmap;
		sControl = Math.random();
		if (sControl > .5)
			this.x = -150;
		else
			this.x = screenX + 150;
		this.y = (int) (screenY / 3 + Math.random() * screenY / 2);
		this.screenX = screenX;
		this.screenY = screenY;
		this.dx = (int) ((this.x - charX) / 50);
		this.dy = (int) ((this.y - charY) / 50);

		hit = true;
		ghostPaint = new Paint();
		ghostPaint.setAlpha(230);

	}

	public Ghost(Bitmap bitmap, int x, int y, int dx, int dy, boolean hit) {
		this.bitmap = bitmap;
		sControl = 0;
		this.x = x;
		this.y = y;
		this.screenX = 0;
		this.screenY = 0;
		this.dx = dx;
		this.dy = dy;
		this.hit = hit;

		hit = true;
		ghostPaint = new Paint();
		ghostPaint.setAlpha(230);

	}

	public void setHit(boolean hit) {
		this.hit = hit;
	}

	public boolean getHit() {
		return this.hit;
	}

	public int getDX() {
		return dx;
	}

	public int getDY() {
		return dy;
	}

	public void move() {
		this.x -= dx;
		this.y -= dy;
	}

	public Rect bounds() {
		if (hit) {
			return new Rect(this.x + 40, this.y + 40, this.x
					+ bitmap.getWidth() - 40, this.y + bitmap.getHeight() - 40);
		} else {
			ghostPaint.setAlpha(90);
		}
		return new Rect();
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public void draw(Canvas c) {
		c.drawBitmap(bitmap, (int) (x), (int) (y), ghostPaint);
		move();
	}

}
