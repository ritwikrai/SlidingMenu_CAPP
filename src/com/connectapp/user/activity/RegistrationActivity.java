package com.connectapp.user.activity;

import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.connectapp.user.R;
import com.connectapp.user.util.AlertDialogCallBack;
import com.connectapp.user.util.Util;
import com.connectapp.user.volley.ServerResponseCallback;
import com.connectapp.user.volley.VolleyTaskManager;

public class RegistrationActivity extends Activity implements ServerResponseCallback {

	private Context mContext;
	private EditText et_name, et_phone, et_organizationId;
	private VolleyTaskManager volleyTaskManager;
	private TextView tv_login;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
		//Log.v(null, null);
		initView();

	}

	private void initView() {

		et_name = (EditText) findViewById(R.id.et_name);
		et_phone = (EditText) findViewById(R.id.et_phone);
		et_organizationId = (EditText) findViewById(R.id.et_organizationId);
		tv_login = (TextView) findViewById(R.id.tv_login);
		mContext = RegistrationActivity.this;
		volleyTaskManager = new VolleyTaskManager(mContext);
		tv_login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(mContext, LoginActivity.class));
				finish();
			}
		});

	}

	public void onRegisterClick(View v) {

		Log.d("Device Model", "" + getDeviceModel());
		Log.d("IMEI", "" + getImeiNumber());
		String name = et_name.getText().toString().trim();
		String phone = et_phone.getText().toString().trim();

		if (name.isEmpty())
			Toast.makeText(mContext, "Please enter a name.", Toast.LENGTH_SHORT).show();

		else if (phone.isEmpty())
			Toast.makeText(mContext, "Please enter a phone number.", Toast.LENGTH_SHORT).show();

		else if (phone.length() != 10)
			Toast.makeText(mContext, "Please enter a correct phone number.", Toast.LENGTH_SHORT).show();

		else if (et_organizationId.getText().toString().trim().isEmpty())
			Toast.makeText(mContext, "Please enter an organization code.", Toast.LENGTH_SHORT).show();

		else if (!et_organizationId.getText().toString().trim().equalsIgnoreCase("FTS"))
			Toast.makeText(mContext, "Please enter the correct organization code.", Toast.LENGTH_SHORT)
					.show();

		else {

			HashMap<String, String> map = new HashMap<String, String>();
			map.put("name", name);
			map.put("phone", phone);
			map.put("email", "");
			map.put("deviceModel", getDeviceModel());
			map.put("imei", getImeiNumber());
			map.put("otp", "11111111");
			map.put("orgCode", "FTS2016");
			volleyTaskManager.doRegistration(map, true);

		}

	}

	private String getDeviceModel() {
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		if (model.startsWith(manufacturer)) {
			return capitalize(model);
		}
		return capitalize(manufacturer) + " " + model;

	}

	private String capitalize(String str) {
		if (TextUtils.isEmpty(str)) {
			return str;
		}
		char[] arr = str.toCharArray();
		boolean capitalizeNext = true;

		//	        String phrase = "";
		StringBuilder phrase = new StringBuilder();
		for (char c : arr) {
			if (capitalizeNext && Character.isLetter(c)) {
				//	                phrase += Character.toUpperCase(c);
				phrase.append(Character.toUpperCase(c));
				capitalizeNext = false;
				continue;
			} else if (Character.isWhitespace(c)) {
				capitalizeNext = true;
			}
			//	            phrase += c;
			phrase.append(c);
		}

		return phrase.toString();
	}

	private String getImeiNumber() {
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();
	}

	@Override
	public void onSuccess(JSONObject resultJsonObject) {
		Log.d("Response", "" + resultJsonObject);
		String statusCode = resultJsonObject.optString("code");
		String message = resultJsonObject.optString("msg");
		if (statusCode.trim().equalsIgnoreCase("200")) {
			Util.showCallBackMessageWithOkCallback(mContext,
					"You are successfully registered. Please login with your phone number.",
					new AlertDialogCallBack() {

						@Override
						public void onSubmit() {
							startActivity(new Intent(mContext, LoginActivity.class));
							finish();

						}

						@Override
						public void onCancel() {
							// TODO Auto-generated method stub

						}
					});

		} else
			Util.showMessageWithOk(RegistrationActivity.this, "" + message);
	}

	@Override
	public void onError() {

	}
}
