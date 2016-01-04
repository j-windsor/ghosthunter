package edu.virginia.cs2110.ghosthunter;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.DisplayMetrics;

public class Coin {

	public Bitmap bitmap;
	int x;
	int y;
	int pointValue;

	public Coin(Bitmap bitmap, int ScreenX, int ScreenY) {
		this.bitmap = bitmap;
		Random ranGen = new Random();
		this.x = ranGen.nextInt(ScreenX);
		this.y = ranGen.nextInt(ScreenY);
	}

	public Coin(Bitmap bitmap, int ScreenX, int ScreenY, int x, int y) {
		this.bitmap = bitmap;
		// Random ranGen = new Random();
		this.x = x;
		this.y = y;
	}

	public Coin(Bitmap bitmap, int x, int y, boolean whatever) {
		this.bitmap = bitmap;
		Random ranGen = new Random();
		this.x = x;
		this.y = y;
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

	public void draw(Canvas c) {
		c.drawBitmap(bitmap, x - (bitmap.getWidth() / 2),
				y - (bitmap.getHeight() / 2), null);
	}

}
