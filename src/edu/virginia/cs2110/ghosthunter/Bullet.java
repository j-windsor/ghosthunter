package edu.virginia.cs2110.ghosthunter;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Bullet {

	public Bitmap bitmap;
	int x;
	int y;
	int dx;
	int dy;
	int pointValue;

	public Bullet(Bitmap bitmap, int touchX, int touchY, int charX, int charY) {
		this.bitmap = bitmap;
		this.x = charX + 40;
		this.y = charY + 40;
		this.dx = (int) ((charX - touchX)
				/ (Math.sqrt((charX - touchX) * (charX - touchX)
						+ (charY - touchY) * (charY - touchY))) * 25);
		this.dy = (int) ((charY - touchY)
				/ (Math.sqrt((charX - touchX) * (charX - touchX)
						+ (charY - touchY) * (charY - touchY))) * 25);
	}

	public Rect bounds() {
		return new Rect(this.x, this.y, this.x + bitmap.getWidth(), this.y
				+ bitmap.getHeight());
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public void move() {
		this.y -= this.dy;
		this.x -= this.dx;
	}

	public void draw(Canvas c) {
		c.drawBitmap(bitmap, x - (bitmap.getWidth() / 2),
				y - (bitmap.getHeight() / 2), null);
		move();
	}
}
