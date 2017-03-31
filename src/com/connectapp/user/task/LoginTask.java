package com.connectapp.user.task;
/*package com.raisahab.task;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONObject;

import com.raisahab.fts.callback.LoginListener;
import com.raisahab.fts.constant.Consts;
import com.raisahab.fts.data.UserClass;
import com.raisahab.fts.util.Util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class LoginTask extends AsyncTask<String, Void, String> {

	private Context mContext;
	private ProgressDialog mProgressDialog;
	private String TAG = "";
	public LoginListener mListener;
	private UserClass userClass = new UserClass();

	public LoginTask(Context mContext) {

		this.mContext = mContext;
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setMessage("Loading...");

		TAG = mContext.getClass().getSimpleName();
		Log.d("tag", TAG);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		showProgressDialog();

	}

	@Override
	protected String doInBackground(String... arg0) {
		String response = "";
		try {
			response = Util.postMethodWay_json(Consts.LOGIN_URL, arg0[0]);
			Log.i(TAG, "Response: " + response);
			parseData(response);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		mListener.getLoginState(userClass);
		hideProgressDialog();
	}

	public void showProgressDialog() {
		if (!mProgressDialog.isShowing())
			mProgressDialog.show();
	}

	public void hideProgressDialog() {
		if (mProgressDialog.isShowing())
			mProgressDialog.dismiss();
	}

	private void parseData(String response) {

		UserClass userClass = new UserClass();
		try {

			JSONArray array = new JSONArray(response);
			for (int i = 0; i < array.length(); i++) {
				JSONObject jsonObject = array.optJSONObject(i);
				userClass.setMessage(jsonObject.optString("msg"));
				if (jsonObject.optString("code").equals("200")) {
					JSONArray jsonArray = jsonObject.optJSONArray("userData");
					for (int j = 0; j < jsonArray.length(); j++) {
						JSONObject object = jsonArray.optJSONObject(i);
						userClass.setUserId(object.optString("userid"));
						userClass.setName(object.optString("name"));
						userClass.setEmail(object.optString("email"));
						userClass.setPhone(object.optString("phone"));
						userClass.setStatus(object.optString("status"));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			userClass.setMessage("Login Failed! Please try again.");
		}
		this.userClass = userClass;
	}
}
*/