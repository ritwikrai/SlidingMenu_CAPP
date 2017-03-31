package com.connectapp.user.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.connectapp.user.R;
import com.connectapp.user.data.Data;
import com.connectapp.user.data.Threads;
import com.connectapp.user.data.UserClass;
import com.connectapp.user.font.RobotoTextView;

/**
 * 
 * @author raisahab.ritwik
 * 
 */
@SuppressWarnings("unused")
public class Util {

	private static String USERCLASS = "USERCLASS";
	private static String OFFLINEKEYWORDS = "OFFLINEKEYWORDS";
	private static String OFFLINEDATA = "OFFLINEDATA";

	private static int CONNECTIONTIMEOUT = 8000;
	private static StringBuilder sb = null;
	private static final String PREF_NAME = "FTSPrefs";
	private static final String PREF_NAME_KEYWORDS = "KeywordsFTSPrefs";
	private static final String PREF_NAME_DATA = "DataFTSPrefs";

	// To check Internet Connection
	public static boolean isInternetAvailable(Context context) {

		ConnectivityManager conManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo i = conManager.getActiveNetworkInfo();
		if ((i == null) || (!i.isConnected()) || (!i.isAvailable())) {

			return false;
		}
		return true;
	}

	/** Hide Soft Keyboard **/
	public static void hideSoftKeyboard(Context context, View view) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
	}

	/** Hide Soft Keyboard **/
	public static void showSoftKeyboard(Context context, View view) {
		InputMethodManager inputMethodManager = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
		;
	}

	public static void showMessageWithOk(final Activity mContext, final String message) {
		((Activity) mContext).runOnUiThread(new Runnable() {

			public void run() {
				final Dialog customDialog = new Dialog(mContext);
				customDialog.getWindow().setBackgroundDrawable(
						new ColorDrawable(android.graphics.Color.TRANSPARENT));

				LayoutInflater layoutInflater = (LayoutInflater) mContext.getApplicationContext()
						.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);

				View view = layoutInflater.inflate(R.layout.dialog_universal_info, null);
				RobotoTextView dialog_universal_info_text = (RobotoTextView) view
						.findViewById(R.id.dialog_universal_info_text);
				RobotoTextView dialog_universal_info_ok = (RobotoTextView) view
						.findViewById(R.id.dialog_universal_info_ok);

				dialog_universal_info_text.setText(message);

				dialog_universal_info_ok.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						customDialog.dismiss();
					}
				});
				customDialog.setContentView(view);

				customDialog.show();
				/*final AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
				LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
				alert.setContentView(inflater.inflate(R.layout.dialog_universal_info, null));
				alert.setTitle(R.string.app_name);

				alert.setMessage(message);
				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

					}
				});
				alert.show();*/
			}
		});
	}

	public static void showMessageWithOk_success(final Context mContext, final String message,
			final String title) {
		((Activity) mContext).runOnUiThread(new Runnable() {

			public void run() {
				final AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
				alert.setTitle(title);
				alert.setIcon(R.drawable.success);
				alert.setMessage(message);
				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

					}
				});
				alert.show();
			}
		});
	}

	public static void showCallBackMessageWithOkCallback_success(final Context mContext,
			final String message, final AlertDialogCallBack callBack) {
		((Activity) mContext).runOnUiThread(new Runnable() {

			public void run() {

				final Dialog customDialog = new Dialog(mContext);
				customDialog.getWindow().setBackgroundDrawable(
						new ColorDrawable(android.graphics.Color.TRANSPARENT));

				LayoutInflater layoutInflater = (LayoutInflater) mContext.getApplicationContext()
						.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);

				View view = layoutInflater.inflate(R.layout.dialog_universal_info, null);
				RobotoTextView dialog_universal_info_text = (RobotoTextView) view
						.findViewById(R.id.dialog_universal_info_text);
				RobotoTextView dialog_universal_info_ok = (RobotoTextView) view
						.findViewById(R.id.dialog_universal_info_ok);

				dialog_universal_info_text.setText(message);

				dialog_universal_info_ok.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						callBack.onSubmit();
					}
				});
				customDialog.setContentView(view);

				customDialog.show();
				/*final AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
				alert.setTitle(R.string.app_name);
				alert.setCancelable(false);
				alert.setIcon(R.drawable.success);
				alert.setMessage(message);
				alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						callBack.onSubmit();
					}
				});

				alert.show();*/
			}
		});
	}

	public static void showCallBackMessageWithOkCancel(final Context mContext, final String message,
			final AlertDialogCallBack callBack) {
		((Activity) mContext).runOnUiThread(new Runnable() {

			public void run() {

				final Dialog customDialog = new Dialog(mContext);
				customDialog.getWindow().setBackgroundDrawable(
						new ColorDrawable(android.graphics.Color.TRANSPARENT));

				LayoutInflater layoutInflater = (LayoutInflater) mContext.getApplicationContext()
						.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);

				View view = layoutInflater.inflate(R.layout.dialog_universal_warning, null);
				RobotoTextView dialog_universal_warning_text = (RobotoTextView) view
						.findViewById(R.id.dialog_universal_warning_text);
				RobotoTextView dialog_universal_warning_ok = (RobotoTextView) view
						.findViewById(R.id.dialog_universal_warning_ok);
				RobotoTextView dialog_universal_warning_cancel = (RobotoTextView) view
						.findViewById(R.id.dialog_universal_warning_cancel);

				dialog_universal_warning_text.setText(message);

				dialog_universal_warning_ok.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						callBack.onSubmit();
					}
				});
				
				dialog_universal_warning_cancel.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						callBack.onCancel();
					}
				});
				customDialog.setContentView(view);

				customDialog.show();

				/*final AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
				alert.setTitle(R.string.app_name);
				alert.setCancelable(false);
				alert.setMessage(message);
				alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						callBack.onSubmit();
					}
				});
				alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						callBack.onCancel();
					}
				});
				alert.show();*/
			}
		});
	}

	public static void showCallBackMessageWithOkCallback(final Context mContext, final String message,
			final AlertDialogCallBack callBack) {
		((Activity) mContext).runOnUiThread(new Runnable() {

			public void run() {

				final Dialog customDialog = new Dialog(mContext);
				customDialog.getWindow().setBackgroundDrawable(
						new ColorDrawable(android.graphics.Color.TRANSPARENT));

				LayoutInflater layoutInflater = (LayoutInflater) mContext.getApplicationContext()
						.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);

				View view = layoutInflater.inflate(R.layout.dialog_universal_info, null);
				RobotoTextView dialog_universal_info_text = (RobotoTextView) view
						.findViewById(R.id.dialog_universal_info_text);
				RobotoTextView dialog_universal_info_ok = (RobotoTextView) view
						.findViewById(R.id.dialog_universal_info_ok);

				dialog_universal_info_text.setText(message);

				dialog_universal_info_ok.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						callBack.onSubmit();
					}
				});
				customDialog.setContentView(view);

				customDialog.show();
				/*
				final AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
				alert.setTitle(R.string.app_name);
				alert.setCancelable(false);
				alert.setMessage(message);
				alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						callBack.onSubmit();
					}
				});

				alert.show();*/
			}
		});
	}

	public static void showCallBackMessageWithOkCallback_success(final Context mContext,
			final String message, final AlertDialogCallBack callBack, final String title) {

		((Activity) mContext).runOnUiThread(new Runnable() {

			public void run() {

				final Dialog customDialog = new Dialog(mContext);
				customDialog.getWindow().setBackgroundDrawable(
						new ColorDrawable(android.graphics.Color.TRANSPARENT));

				LayoutInflater layoutInflater = (LayoutInflater) mContext.getApplicationContext()
						.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);

				View view = layoutInflater.inflate(R.layout.dialog_universal_info, null);
				RobotoTextView dialog_universal_info_text = (RobotoTextView) view
						.findViewById(R.id.dialog_universal_info_text);
				RobotoTextView dialog_universal_info_ok = (RobotoTextView) view
						.findViewById(R.id.dialog_universal_info_ok);

				dialog_universal_info_text.setText(message);

				dialog_universal_info_ok.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						callBack.onSubmit();
					}
				});
				customDialog.setContentView(view);

				customDialog.show();

				/*final AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
				alert.setTitle(title);
				alert.setIcon(R.drawable.success);
				alert.setCancelable(false);
				alert.setMessage(message);
				alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						callBack.onSubmit();
					}
				});

				alert.show();*/
			}
		});
	}

	// Saving UserClass details
	public static void saveUserClass(final Context mContext, UserClass userClass) {
		SharedPreferences LastMilePrefs = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor prefsEditor = LastMilePrefs.edit();
		try {
			prefsEditor.putString(USERCLASS, ObjectSerializer.serialize(userClass));
		} catch (IOException e) {
			e.printStackTrace();
		}
		prefsEditor.commit();
	}

	// Fetching UserClass details
	public static UserClass fetchUserClass(final Context mContext) {
		SharedPreferences LastMilePrefs = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		UserClass userClass = null;
		String serializeOrg = LastMilePrefs.getString(USERCLASS, null);
		try {
			if (serializeOrg != null) {
				userClass = (UserClass) ObjectSerializer.deserialize(serializeOrg);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return userClass;
	}

	// Saving threads
	public static void saveOfflineKeywordsThreads(final Context mContext, Threads threads) {
		SharedPreferences LastMilePrefs = mContext.getSharedPreferences(PREF_NAME_KEYWORDS,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor prefsEditor = LastMilePrefs.edit();
		try {
			prefsEditor.putString(OFFLINEKEYWORDS, ObjectSerializer.serialize(threads));
		} catch (IOException e) {
			e.printStackTrace();
		}
		prefsEditor.commit();
	}

	// Fetching threads
	public static Threads fetchOfflineKeywordsThreads(final Context mContext) {
		SharedPreferences LastMilePrefs = mContext.getSharedPreferences(PREF_NAME_KEYWORDS,
				Context.MODE_PRIVATE);
		Threads thread = null;
		String serializeOrg = LastMilePrefs.getString(OFFLINEKEYWORDS, null);
		try {
			if (serializeOrg != null) {
				thread = (Threads) ObjectSerializer.deserialize(serializeOrg);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return thread;
	}

	// Saving Data details
	public static void saveOfflineData(final Context mContext, Data data) {
		SharedPreferences LastMilePrefs = mContext.getSharedPreferences(PREF_NAME_DATA, Context.MODE_PRIVATE);
		SharedPreferences.Editor prefsEditor = LastMilePrefs.edit();
		try {
			prefsEditor.putString(OFFLINEDATA, ObjectSerializer.serialize(data));
		} catch (IOException e) {
			e.printStackTrace();
		}
		prefsEditor.commit();
	}

	// Fetching Keywords details
	public static Data fetchOfflineData(final Context mContext) {
		SharedPreferences LastMilePrefs = mContext.getSharedPreferences(PREF_NAME_DATA, Context.MODE_PRIVATE);
		Data data = null;
		String serializeOrg = LastMilePrefs.getString(OFFLINEDATA, null);
		try {
			if (serializeOrg != null) {
				data = (Data) ObjectSerializer.deserialize(serializeOrg);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return data;
	}

	/** Get the current date **/
	@SuppressLint("SimpleDateFormat")
	public static String getDate() {
		String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		return date;
	}

	/** Get the current Time **/
	@SuppressLint("SimpleDateFormat")
	public static String getTime() {
		String date = new SimpleDateFormat("HH:mm:ss").format(new Date());
		return date;
	}

	/**
	 * show global alert message
	 * 
	 * @param context
	 *            application context
	 * @param title
	 *            alert title
	 * @param btn_title
	 *            alert click button name
	 * @param msg
	 *            alert message
	 */
	public static void alertMessage(Context context, String title, String btn_title, String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(msg).setCancelable(false)
				.setPositiveButton(btn_title, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						dialog.dismiss();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * Show log
	 * 
	 * @param type
	 *            of log 0 for info,1 error,2 for verbosa
	 * @param logtitle
	 * @param logcontent
	 * @return
	 */
	public static void printLog(int type, String logtitle, String logcontent) {

		switch (type) {
		case 0:
			Log.i(logtitle, logcontent + "");
			break;
		case 1:
			Log.e(logtitle, logcontent + "");
			break;
		case 2:
			Log.v(logtitle, logcontent + "");
			break;

		default:
			Log.i(logtitle, logcontent + "");
			break;
		}
	}

	//	public static String postMethodWay_json(String hostName, String data) throws ClientProtocolException, IOException {
	//
	//		sb = new StringBuilder();
	//		HttpClient client = new DefaultHttpClient();
	//		HttpConnectionParams.setConnectionTimeout(client.getParams(), CONNECTIONTIMEOUT);
	//		HttpConnectionParams.setSoTimeout(client.getParams(), CONNECTIONTIMEOUT);
	//		HttpResponse response;
	//
	//		try {
	//			HttpPost post = new HttpPost(hostName);
	//
	//			StringEntity se = new StringEntity(data);
	//			se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
	//			post.setEntity(se);
	//			response = client.execute(post);
	//			int statuscode = response.getStatusLine().getStatusCode();
	//
	//			/* Checking response */
	//			if (statuscode == 200) {
	//
	//				if (response != null) {
	//					InputStream in = response.getEntity().getContent();
	//					// InputStream in = /* your InputStream */;
	//					InputStreamReader is = new InputStreamReader(in);
	//
	//					BufferedReader br = new BufferedReader(is);
	//					String read = br.readLine();
	//
	//					while (read != null) {
	//						// System.out.println(read);
	//						sb.append(read);
	//						read = br.readLine();
	//
	//					}
	//					// the entity
	//					Log.i("tAG", "" + sb.toString());
	//					return sb.toString();
	//				}
	//			} else {
	//				// the entity
	//				System.out.println("Response code is:: " + response.getStatusLine().getStatusCode());
	//				sb.append(statuscode);
	//				return sb.toString();
	//			}
	//
	//		} catch (Exception e) {
	//			e.printStackTrace();
	//			sb.append(e.getLocalizedMessage());
	//			Log.i("connection", "Cannot Estabilish Connection! " + e.getLocalizedMessage());
	//		}
	//
	//		return sb.toString();
	//	}

	public static String getBase64StringFromBitmap(Bitmap mBitmap) {
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bao);
		byte[] ba = bao.toByteArray();
		return (Base64.encodeToString(ba, Base64.DEFAULT));
	}

	public static Bitmap getBitmapBase64FromString(String encodedImage) {
		byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
		Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
		return (decodedByte);
	}

	public static AlertDialog showSettingsAlert(final Context applicationContext,
			AlertDialog systemAlertDialog) {
		Log.v("calling showSettingsAlert()", "true");

		AlertDialog.Builder builder = new AlertDialog.Builder(applicationContext);
		builder.setTitle("GPS Disabled");
		builder.setIcon(R.drawable.warning);
		builder.setCancelable(false);
		builder.setMessage("Your GPS seems to be disabled, tap Ok to enable it?");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// mContext.startActivity(new
				// Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
				Intent viewIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				viewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // <-- Newly
																	// added
																	// line
				applicationContext.startActivity(viewIntent);
				dialog.dismiss();
			}
		});
		//		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
		//			public void onClick(DialogInterface dialog, int whichButton) {
		//				dialog.dismiss();
		//			}
		//		});
		systemAlertDialog = builder.create();
		systemAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		systemAlertDialog.show();
		return systemAlertDialog;
	}
}
