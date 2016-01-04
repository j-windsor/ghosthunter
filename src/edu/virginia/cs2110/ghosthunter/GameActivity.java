package edu.virginia.cs2110.ghosthunter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.SyncStateContract.Constants;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class GameActivity extends Activity implements onNetworkTaskComplete,
		SensorEventListener {
	ImageView character;
	private GameView game;
	private SensorManager sensorManager;
	private Sensor accelerometer;
	private MediaPlayer mPlayer;
	public String user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_game);
		user = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString(
				"user", "");

		// Grab savegame if exists
		Intent intent = getIntent();
		if (intent.getBooleanExtra("resume", false)) {
			startGameFromUser(user);
		}

		game = new GameView(this);
		// character = (ImageView) findViewById(R.id.char_sprite);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(game);

		// Setup Accelerometer
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		accelerometer = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	protected void onResume() {
		// App Opens Again and Reregister - Saves Battery
		super.onResume();
		sensorManager.registerListener(this, accelerometer,
				SensorManager.SENSOR_DELAY_GAME);
		mPlayer = MediaPlayer.create(GameActivity.this, R.raw.creepymusic);
		mPlayer.setLooping(true);
		mPlayer.start();
	}

	@Override
	protected void onPause() {
		// App Paused and unregister - Saves Battery
		super.onPause();
		sensorManager.unregisterListener(this);
		mPlayer.stop();
		String savefile;
		try {
			savefile = game.stopAndSave();
			NetworkTask startTask = new NetworkTask(this);
			String query = URLEncoder.encode(savefile, "utf-8");
			// startTask.execute(URLEncoder.encode("http://ghost.llamasatbrunch.com/update.php?user="+user+"&savefile="+savefile,
			// "UTF-8"));
			if (game.thread.gameOver) {
				startTask
						.execute("http://ghost.llamasatbrunch.com/reset.php?user="
								+ user);
			} else
				startTask
						.execute("http://ghost.llamasatbrunch.com/update.php?user="
								+ user + "&savefile=" + query);
		} catch (JSONException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finish();
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// Intentionally Not Implemented - Jay
		// TODO Auto-generated method stub

	}

	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			game.updateCharacterAcceleration((int) event.values[0],
					(int) event.values[1]);

		}
	}

	// //////////////////////////////////// API HELPER METHODS
	@Override
	public void onTaskCompleted(String output) throws JSONException {
		if (output.equals("Done") || output.equals("Failure")) {
			Context context = getApplicationContext();
			CharSequence text = output;
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		} else {
			game = new GameView(this, new JSONObject(output));
			setContentView(game);
			NetworkTask startTask = new NetworkTask(this);
			startTask.execute("http://ghost.llamasatbrunch.com/reset.php?user="
					+ user);
		}

	}

	public void startGameFromUser(String user) {
		NetworkTask startTask = new NetworkTask(this);
		startTask.execute("http://ghost.llamasatbrunch.com/get.php?user="
				+ user);
	}

}
