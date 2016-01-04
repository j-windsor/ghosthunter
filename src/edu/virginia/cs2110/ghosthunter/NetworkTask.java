package edu.virginia.cs2110.ghosthunter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import org.json.JSONException;

//import android.app.Notification;
//import android.app.NotificationManager;
//import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class NetworkTask extends AsyncTask<String, Void, String> {

	private static final int REGISTRATION_TIMEOUT = 3 * 1000;
	private static final int WAIT_TIMEOUT = 30 * 1000;
	private final HttpClient httpclient = new DefaultHttpClient();

	final HttpParams params = httpclient.getParams();
	HttpResponse response;
	private String content = null;
	private onNetworkTaskComplete listener;
	private boolean error = false;

	public NetworkTask(onNetworkTaskComplete listener) {
		this.listener = listener;
	}

	protected void onPreExecute() {
	}

	protected String doInBackground(String... urls) {

		String URL = null;
		String param1 = "abc";
		String param2 = "xyz";

		try {

			// URL passed to the AsyncTask
			URL = urls[0];
			HttpConnectionParams.setConnectionTimeout(params,
					REGISTRATION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(params, WAIT_TIMEOUT);
			ConnManagerParams.setTimeout(params, WAIT_TIMEOUT);

			HttpPost httpPost = new HttpPost(URL);

			// Any other parameters you would like to set
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("param1", param1));
			nameValuePairs.add(new BasicNameValuePair("param2", param2));
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Response from the Http Request
			response = httpclient.execute(httpPost);
			Log.v("Async", "HEYYYYYYY");

			StatusLine statusLine = response.getStatusLine();
			// Check the Http Request for success
			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				content = out.toString();
			} else {
				// Closes the connection.
				Log.w("HTTP1:", statusLine.getReasonPhrase());
				response.getEntity().getContent().close();
				throw new IOException(statusLine.getReasonPhrase());
			}

		} catch (ClientProtocolException e) {
			Log.w("HTTP2:", e);
			content = e.getMessage();
			error = true;
			cancel(true);
		} catch (IOException e) {
			Log.w("HTTP3:", e);
			content = e.getMessage();
			error = true;
			cancel(true);
		} catch (Exception e) {
			Log.w("HTTP4:", e);
			content = e.getMessage();
			error = true;
			cancel(true);
		}

		return content;
	}

	protected void onCancelled() {

	}

	protected void onPostExecute(String content) {
		try {
			listener.onTaskCompleted(content);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}