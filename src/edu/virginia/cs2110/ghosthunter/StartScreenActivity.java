package edu.virginia.cs2110.ghosthunter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class StartScreenActivity extends Activity implements
		onNetworkTaskComplete {
	Button resumeButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_screen);
		Button b1 = (Button) findViewById(R.id.start_button);
		resumeButton = (Button) findViewById(R.id.resume_button);

		// First Run Registration
		boolean firstrun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
				.getBoolean("firstrun", true);
		if (firstrun) {
			if (isConnected()) {
				AlertDialog.Builder alert = new AlertDialog.Builder(this);

				alert.setTitle("Welcome to Ghosthunter!");
				alert.setMessage("Please enter a username! This username will allow you to play on other devices. If you have already registered on another device, typing the same username will link this device.");

				// Set an EditText view to get user input
				final EditText input = new EditText(this);
				alert.setView(input);

				alert.setPositiveButton("Save",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								Editable value = input.getText();
								registerUser(value);

							}
						});

				alert.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// Canceled.
							}
						});

				alert.show();

				// Save the first state
				getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
						.putBoolean("firstrun", false).commit();
			} else {
				Context context = getApplicationContext();
				CharSequence text = "Unable to register user at this time.";
				int duration = Toast.LENGTH_SHORT;

				Toast toast = Toast.makeText(context, text, duration);
				toast.show();

			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		String user = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
				.getString("user", "");
		if (isConnected()) {
			checkDefault(user);
		} else {
			resumeButton.setEnabled(false);
			resumeButton.setVisibility(View.INVISIBLE);
		}
	}

	protected void onPause() {
		super.onPause();

	}

	// check network connection
	public boolean isConnected() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected())
			return true;
		else
			return false;
	}

	public void onClickStart(View view) {
		Intent intent = new Intent(this, GameActivity.class);
		startActivity(intent);
	}

	public void onClickResume(View view) {
		Intent intent = new Intent(this, GameActivity.class);
		intent.putExtra("resume", true);
		startActivity(intent);
	}

	// //////////////////////////////////////////// API HELPER METHODS
	public void onTaskCompleted(String output) {
		// Web API Return Handler
		if (output.equals("1") || output.equals("0")) {
			int isdefault = Integer.parseInt(output);
			Log.d("HI", "hi: " + isdefault);
			if (isdefault == 1) {
				resumeButton.setEnabled(false);
				resumeButton.setVisibility(View.INVISIBLE);
				Log.d("HI", "here");
			} else if (isdefault == 0) {
				resumeButton.setEnabled(true);
				resumeButton.setVisibility(View.VISIBLE);
				Log.d("HI", "or here");
			}
		} else {
			Context context = getApplicationContext();
			CharSequence text = output;
			int duration = Toast.LENGTH_SHORT;

			checkDefault(getSharedPreferences("PREFERENCE", MODE_PRIVATE)
					.getString("user", ""));

			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}
	}

	public void registerUser(Editable value) {
		NetworkTask startTask = new NetworkTask(this);
		startTask.execute("http://ghost.llamasatbrunch.com/register.php?user="
				+ value.toString());

		getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
				.putString("user", value.toString()).commit();
	}

	public void checkDefault(String user) {
		NetworkTask startTask = new NetworkTask(this);
		startTask
				.execute("http://ghost.llamasatbrunch.com/checkdefault.php?user="
						+ user);
	}

}
