package com.connectapp.user.activity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images.Media;
import android.support.v4.view.ViewPager;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.connectapp.user.R;
import com.connectapp.user.adapter.ImageAdapter;
import com.connectapp.user.constant.StaticConstants;
import com.connectapp.user.data.ImageClass;
import com.connectapp.user.data.Thread;
import com.connectapp.user.db.HistoryDB;
import com.connectapp.user.dropDownActivity.PictureCategoryActivity;
import com.connectapp.user.dropDownActivity.StateCodeActivity;
import com.connectapp.user.location.FetchCordinates;
import com.connectapp.user.location.FusedLocationCallback;
import com.connectapp.user.location.LocationCallback;
import com.connectapp.user.syncadapter.Constant;
import com.connectapp.user.syncadapter.DBConstants;
import com.connectapp.user.util.AlertDialogCallBack;
import com.connectapp.user.util.Util;
import com.connectapp.user.volley.ServerResponseCallback;
import com.connectapp.user.volley.VolleyTaskManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.drive.events.CompletionEvent;
import com.google.android.gms.games.quest.Quest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.plus.PlusShare;

public class SchoolFormActivity extends Activity implements LocationListener, ConnectionCallbacks, OnConnectionFailedListener,
		ServerResponseCallback, FusedLocationCallback, LocationCallback, OnClickListener {
	private static final int CAMERA_PIC_REQUEST = 1337;
	private static final int PICTURE_GALLERY_REQUEST = 2572;
	private static Uri mCapturedImageURI;
	private String TAG;
	private TextView dropDownActivity_pictureCategory;
	private int dynamicEtId_schoolId;
	private int dynamicEtId_villageName;
	private EditText et_anchal;
	private EditText et_comments;
	private EditText et_sanch;
	private EditText et_sankul;
	private EditText et_upsanch;
	private EditText et_village;
	private FusedLocationProviderApi fusedLocationProviderApi;
	private String geoAddress;
	private ContentValues historyCV;
	private int imageCount;
	private ArrayList<ImageClass> imagesList;
	private LinearLayout ll_dynamicField;
	private Context mContext;
	private Location mCurrentLocation;
	private GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;
	private ProgressDialog mProgressDialog;
	private AlertDialog systemAlertDialog;
	private Thread thread;
	private TextView tvCountryCode;
	private TextView tv_imageProgress;
	private TextView tv_stateCode;
	private View v_swipeLeft;
	private View v_swipeRight;
	private VolleyTaskManager volleyTaskManager;
	private ViewPager vp_selectedImages;


	/* AlertDialog Callback saved Off-line*/
	class SavedOffline implements AlertDialogCallBack {
		SavedOffline() {
		}

		public void onSubmit() {
			SchoolFormActivity.this.finish();
			SchoolFormActivity.this.mCurrentLocation = null;
		}

		public void onCancel() {
		}
	}

	/* AlertDialog Callback Submission Complete */
	class SubmissionComplete implements AlertDialogCallBack {
		SubmissionComplete() {
		}

		public void onSubmit() {
			SchoolFormActivity.this.clearForm();
			SchoolFormActivity.this.finish();
			SchoolFormActivity.this.mCurrentLocation = null;
		}

		public void onCancel() {
		}
	}


	public SchoolFormActivity() {
		this.imageCount = 0;
		this.geoAddress = "";
		this.dynamicEtId_schoolId = 556987098;
		this.dynamicEtId_villageName = 987684756;
		this.TAG = getClass().getSimpleName();
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_form);
		this.mContext = this;
		this.dropDownActivity_pictureCategory = (TextView) findViewById(R.id.dropDownActivity_pictureCategory);
		this.dropDownActivity_pictureCategory.setOnClickListener(this);
		this.volleyTaskManager = new VolleyTaskManager(this.mContext);
		this.vp_selectedImages = (ViewPager) findViewById(R.id.vp_selectedImages);
		this.tv_imageProgress = (TextView) findViewById(R.id.tv_imageProgress);
		this.v_swipeLeft = findViewById(R.id.v_swipeLeft);
		this.v_swipeRight = findViewById(R.id.v_swipeRight);
		this.imagesList = new ArrayList<ImageClass>();
		this.mProgressDialog = new ProgressDialog(this.mContext);
		this.mProgressDialog.setProgressStyle(0);
		this.mProgressDialog.setCancelable(false);
		this.mProgressDialog.setCanceledOnTouchOutside(false);
		this.thread = new Thread();
		this.thread = (Thread) getIntent().getExtras().get("thread");
		initView();
	}

	private void initView() {
		this.ll_dynamicField = (LinearLayout) findViewById(R.id.ll_dynamicField);
		if (this.thread.getThreadID().equalsIgnoreCase("6")) {
			this.ll_dynamicField.setVisibility(0);
			this.et_comments = (EditText) findViewById(R.id.et_comments);
			this.tvCountryCode = (TextView) findViewById(R.id.tvCountryCode);
			this.tv_stateCode = (TextView) findViewById(R.id.tv_stateCode);
			this.et_anchal = (EditText) findViewById(R.id.et_anchal);
			this.et_sankul = (EditText) findViewById(R.id.et_sankul);
			this.et_sanch = (EditText) findViewById(R.id.et_sanch);
			this.et_upsanch = (EditText) findViewById(R.id.et_upsanch);
			this.et_village = (EditText) findViewById(R.id.et_village);
			this.tv_stateCode.setFocusable(true);
			this.tv_stateCode.requestFocus();
			this.tv_stateCode.setCursorVisible(true);
			this.tv_stateCode.setOnClickListener(this);
			setTextWatcher();
		}
		this.mCurrentLocation = null;
		checkingLocation();
	}

	private void setTextWatcher() {
		et_anchal.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				Log.d(TAG, "andchal on text changed count " + s.length());
				if (s.length() == 2)
					et_sankul.requestFocus();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		et_sankul.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() == 1)
					et_sanch.requestFocus();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		et_sanch.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() == 1)
					et_upsanch.requestFocus();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		et_upsanch.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() == 1)
					et_village.requestFocus();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		et_village.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() == 2) {
					et_village.clearFocus();
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					Util.hideSoftKeyboard(mContext, et_village);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

	}

	@SuppressLint({ "InflateParams" })
	public void onPictureClick(View v) {
		if (this.imagesList.size() < 1) {
			this.mProgressDialog.setMessage("Please wait...");
			this.mProgressDialog.setCancelable(true);
			showProgressDialog();
			cameraSelectedPic();
			return;
		}
		Util.showMessageWithOk(SchoolFormActivity.this, "Maximum number of images has already been selected!");
	}

	private void showProgressDialog() {
		if (!this.mProgressDialog.isShowing()) {
			this.mProgressDialog.show();
		}
	}

	private void hideProgressDialog() {
		if (this.mProgressDialog.isShowing()) {
			this.mProgressDialog.dismiss();
		}
	}

	protected void populatingSelectedPic() {
		Log.v(this.TAG, "selected from gallery");
		Intent albumIntent = new Intent("android.intent.action.PICK", Media.EXTERNAL_CONTENT_URI);
		albumIntent.setType("image/*");
		startActivityForResult(albumIntent, PICTURE_GALLERY_REQUEST);
	}

	protected void cameraSelectedPic() {
		Log.i(this.TAG, "selected from camera");
		ContentValues values = new ContentValues();
		values.put(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_TITLE, "Image File name");
		mCapturedImageURI = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, values);
		System.out.println("Action image capture uri: " + mCapturedImageURI.getPath());
		Intent intentPicture = new Intent("android.media.action.IMAGE_CAPTURE");
		intentPicture.putExtra("output", mCapturedImageURI);
		startActivityForResult(intentPicture, CAMERA_PIC_REQUEST);
	}

	private void processImagePath(String picturePath) {
		Options opt = new Options();
		opt.inScaled = true;
		int bitWidth = BitmapFactory.decodeFile(picturePath).getWidth();
		int bitHeight = BitmapFactory.decodeFile(picturePath).getHeight();
		System.out.println("width : " + bitWidth + " bitHeight : " + bitHeight);
		if (bitWidth <= AccessibilityNodeInfoCompat.ACTION_PREVIOUS_HTML_ELEMENT || bitHeight <= 1536) {
			opt.inSampleSize = 4;
		} else if ((bitHeight <= 1536 || bitHeight > 1944)
				&& (bitWidth <= AccessibilityNodeInfoCompat.ACTION_PREVIOUS_HTML_ELEMENT || bitWidth > 2592)) {
			opt.inSampleSize = 8;
		} else {
			opt.inSampleSize = 6;
		}
		Bitmap bitmap = BitmapFactory.decodeFile(picturePath, opt);
		if (bitmap != null) {
			try {
				int orientation = new ExifInterface(picturePath).getAttributeInt("Orientation", 1);
				Log.e("orientation", new StringBuilder(String.valueOf(orientation)).append("<<<").toString());
				Matrix matrix = new Matrix();
				switch (orientation) {
				case CompletionEvent.STATUS_FAILURE /*1*/:
					Log.v("Case:", "1");
					break;
				case CompletionEvent.STATUS_CONFLICT /*2*/:
					Log.v("Case:", "2");
					break;
				case CompletionEvent.STATUS_CANCELED /*3*/:
					Log.v("Case:", "3");
					matrix.postRotate(BitmapDescriptorFactory.HUE_CYAN);
					break;
				case GeofencingRequest.INITIAL_TRIGGER_DWELL /*4*/:
					Log.v("Case:", "4");
					break;
				case DetectedActivity.TILTING /*5*/:
					Log.v("Case:", "5");
					break;
				case Quest.STATE_FAILED /*6*/:
					Log.v("Case:", "6");
					matrix.postRotate(90.0f);
					break;
				case DetectedActivity.WALKING /*7*/:
					Log.v("Case:", "7");
					break;
				case DetectedActivity.RUNNING /*8*/:
					Log.v("Case:", "8");
					matrix.postRotate(-90.0f);
					break;
				}
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			} catch (IOException e) {
				e.printStackTrace();
			}
			ImageClass imageClass = new ImageClass();
			imageClass.setBase64value(Util.getBase64StringFromBitmap(bitmap));
			imageClass.setImageCount(this.imageCount + 1);
			this.imagesList.add(imageClass);
			this.imageCount++;
		} else {
			Toast.makeText(this, new StringBuilder(String.valueOf(picturePath)).append("not found").toString(), 1).show();
		}
		imageUpdateOnView();
	}

	private void imageUpdateOnView() {
		this.vp_selectedImages.setAdapter(new ImageAdapter(this, this.imagesList));
		if (this.imagesList.size() == 0) {
			this.vp_selectedImages.setBackgroundResource(R.drawable.default_empty);
		} else {
			this.vp_selectedImages.setBackgroundColor(Color.parseColor("#D7D7D7"));
			this.vp_selectedImages.setCurrentItem(this.imagesList.size() - 1);
		}
		if (this.imagesList.size() <= 1) {
			this.tv_imageProgress.setText("[Image added " + this.imagesList.size() + "/1]");
			this.v_swipeRight.setVisibility(4);
			this.v_swipeLeft.setVisibility(4);
			return;
		}
		this.tv_imageProgress.setText("Slide to view other images\n[Images added " + this.imagesList.size() + "/1]");
		this.v_swipeRight.setVisibility(0);
		this.v_swipeLeft.setVisibility(0);
	}

	private void generateNoteOnSD(Context context, String sFileName, String sBody) {
		try {
			File root = new File(Environment.getExternalStorageDirectory(), "Notes");
			if (!root.exists()) {
				root.mkdirs();
			}
			FileWriter writer = new FileWriter(new File(root, sFileName));
			writer.append(sBody);
			writer.flush();
			writer.close();
			Toast.makeText(context, "Saved", 0).show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void checkingLocation() {
		if (isGooglePlayServicesAvailable()) {
			LocationManager locationManager = (LocationManager) getSystemService("location");
			if (locationManager.isProviderEnabled("gps") || locationManager.isProviderEnabled("network")) {
				Log.v("GPS Connection Found:", "true");
				if (this.mCurrentLocation == null) {
					this.mProgressDialog.setMessage("Fetching present location...");
					this.mProgressDialog.setCancelable(true);
					createLocationRequest();
					return;
				}
				return;
			}
			Log.e(this.TAG, "NO LOCATION FOUND!");
			if (this.systemAlertDialog == null) {
				this.systemAlertDialog = Util.showSettingsAlert(getApplicationContext(), this.systemAlertDialog);
				return;
			} else if (!this.systemAlertDialog.isShowing()) {
				this.systemAlertDialog = Util.showSettingsAlert(getApplicationContext(), this.systemAlertDialog);
				return;
			} else {
				return;
			}
		}
		GooglePlayServicesUtil.getErrorDialog(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this), this, 10).show();
	}

	private boolean isGooglePlayServicesAvailable() {
		if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == 0) {
			return true;
		}
		return false;
	}

	protected void createLocationRequest() {
		/*if (Util.isInternetAvailable(this.mContext)) {
			System.out.println("Fused Location called!");
			this.mProgressDialog.setMessage("Fetching present location...");
			this.mProgressDialog.setCancelable(true);
			showProgressDialog();
			this.mLocationRequest = LocationRequest.create();
			this.mLocationRequest.setPriority(100);
			this.mLocationRequest.setNumUpdates(1);
			this.mLocationRequest.setInterval(5000);
			this.mLocationRequest.setFastestInterval(1000);
			this.fusedLocationProviderApi = LocationServices.FusedLocationApi;
			if (this.mGoogleApiClient == null) {
				this.mGoogleApiClient = new Builder(this).addApi(LocationServices.API)
						.addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
				this.mGoogleApiClient.connect();
				return;
			}
			return;
		}*/
		FetchCordinates mtask = new FetchCordinates(this.mContext);
		mtask.mListener = this;
		mtask.execute(new String[0]);
	}

	public void getLocation(Location mLocation) {
		this.mCurrentLocation = mLocation;
		Log.d("Latitude", "" + mLocation.getLatitude());
		Log.d("Longitude", "" + mLocation.getLongitude());
	}

	public void onConnectionFailed(ConnectionResult connectionResult) {
		Log.d(this.TAG, "Connection failed: " + connectionResult.toString());
		Toast.makeText(this, "Connection failed: " + connectionResult.toString(), 1).show();
		hideProgressDialog();
	}

	public void onConnected(Bundle arg0) {
		Log.d(this.TAG, "onConnected - isConnected ...............: " + this.mGoogleApiClient.isConnected());
		startLocationUpdates();
	}

	protected void startLocationUpdates() {
		this.fusedLocationProviderApi.requestLocationUpdates(this.mGoogleApiClient, this.mLocationRequest,
				(LocationListener) this);
		Log.d(this.TAG, "Location update started ..............: ");
	}

	public void onConnectionSuspended(int arg0) {
	}

	public void onLocationChanged(Location location) {
		Log.d(this.TAG, "Firing onLocationChanged..............................................");
		Log.d(this.TAG, "Lat: " + location.getLatitude());
		Log.d(this.TAG, "Lon: " + location.getLongitude());
		Log.d(this.TAG, "Accuray: " + location.getAccuracy());
		hideProgressDialog();
		this.mCurrentLocation = location;
		Log.v("onLocationChanged", "Geo Address: " + this.geoAddress);
	}

	protected void onResume() {
		super.onResume();
		System.out.println("------>> ON RESUME CALLED  >>---------------");
		if (this.mCurrentLocation == null) {
			checkingLocation();
		}
	}

	public void onSubmitClick(View v) {
		String countryCode = "";
		String villageName = "";
		String stateCode = this.tv_stateCode.getText().toString().trim();
		String anchal = this.et_anchal.getText().toString().trim();
		String sankul = this.et_sankul.getText().toString().trim();
		String sanch = this.et_sanch.getText().toString().trim();
		String upsanch = this.et_upsanch.getText().toString().trim();
		String village = this.et_village.getText().toString().trim();
		String completeSchoolCode = "";
		if (this.ll_dynamicField.getVisibility() == 0) {
			countryCode = this.tvCountryCode.getText().toString().trim();
			villageName = this.et_comments.getText().toString().trim();
		}
		if (this.imagesList.size() < 1) {
			Util.showMessageWithOk(SchoolFormActivity.this, "Please take a picture first.");
		} else if (stateCode.isEmpty() || stateCode.equalsIgnoreCase("-")) {
			Util.showMessageWithOk(SchoolFormActivity.this, "Please enter the State Code.");
		} else if (anchal.isEmpty()) {
			Util.showMessageWithOk(SchoolFormActivity.this, "Please enter the Anchal.");
		} else if (sankul.isEmpty()) {
			Util.showMessageWithOk(SchoolFormActivity.this, "Please enter the Sankul.");
		} else if (sanch.isEmpty()) {
			Util.showMessageWithOk(SchoolFormActivity.this, "Please enter the Sanch.");
		} else if (upsanch.isEmpty()) {
			Util.showMessageWithOk(SchoolFormActivity.this, "Please enter the Up-Sanch.");
		} else if (village.isEmpty()) {
			Util.showMessageWithOk(SchoolFormActivity.this, "Please enter the Village.");
		} else if (anchal.length() < 2) {
			Util.showMessageWithOk(SchoolFormActivity.this, "Please enter the correct Anchal.");
		} else if (village.length() < 2) {
			Util.showMessageWithOk(SchoolFormActivity.this, "Please enter the correct Village.");
		} else if (this.dropDownActivity_pictureCategory.getText().toString().trim().isEmpty()) {
			Util.showMessageWithOk(SchoolFormActivity.this, "Please enter a comment.");
		} else if (Util.isInternetAvailable(this.mContext)) {
			completeSchoolCode = new StringBuilder(String.valueOf(countryCode)).append(stateCode).append(anchal).append(sankul)
					.append(sanch).append(upsanch).append(village).toString();
			HashMap<String, String> formDataMap = new HashMap();
			formDataMap.put(DBConstants.MU_ID, Util.fetchUserClass(this.mContext).getUserId());
			formDataMap.put(com.connectapp.user.db.DBConstants.THREAD_ID, this.thread.getThreadID());
			formDataMap.put(DBConstants.IMAGE, ((ImageClass) this.imagesList.get(0)).getBase64value());
			formDataMap.put("lat", "" + this.mCurrentLocation.getLatitude());
			formDataMap.put("long", "" + this.mCurrentLocation.getLongitude());
			formDataMap.put("piccat", this.dropDownActivity_pictureCategory.getText().toString().trim());
			formDataMap.put(DBConstants.DATE, Util.getDate());
			formDataMap.put(DBConstants.TIME, Util.getTime());
			if (this.thread.getThreadID().equalsIgnoreCase("6")) {
				formDataMap.put("sCode", completeSchoolCode);
				formDataMap.put(DBConstants.PICTURE_CATEGORY, villageName);
			} else {
				formDataMap.put("keyWords", "12,13,14");
			}
			this.historyCV = new ContentValues();
			this.historyCV.put(DBConstants.MU_ID, Util.fetchUserClass(this.mContext).getUserId());
			this.historyCV.put(DBConstants.THREAD_ID, this.thread.getThreadID());
			this.historyCV.put(DBConstants.IMAGE, ((ImageClass) this.imagesList.get(0)).getBase64value());
			this.historyCV.put(DBConstants.LATITUDE, this.mCurrentLocation.getLatitude());
			this.historyCV.put(DBConstants.LONGITUDE, this.mCurrentLocation.getLongitude());
			this.historyCV.put(DBConstants.PICTURE_CATEGORY, this.dropDownActivity_pictureCategory.getText().toString().trim());
			this.historyCV.put(DBConstants.KEYWORDS, "12,13,14");
			this.historyCV.put(DBConstants.ADDRESS, this.geoAddress);
			this.historyCV.put(DBConstants.DATE, Util.getDate());
			this.historyCV.put(DBConstants.TIME, Util.getTime());
			this.historyCV.put(DBConstants.SCHOOL_CODE, completeSchoolCode);
			this.historyCV.put(DBConstants.RATH_NUMBER, StaticConstants.RATH_NUMBER_DEFAULT);
			this.historyCV.put(DBConstants.VILLAGE_NAME, villageName);
			this.historyCV.put(DBConstants.OTHER_DATA, "{\"sCode\":\"" + countryCode + "\",\"village\":\"" + villageName + "\"}");
			this.volleyTaskManager.doPostFormData(formDataMap, true);
		} else {
			completeSchoolCode = new StringBuilder(String.valueOf(countryCode)).append(stateCode).append(anchal).append(sankul)
					.append(sanch).append(upsanch).append(village).toString();
			Util.showCallBackMessageWithOkCallback_success(this.mContext,
					"The data has been saved. It will be uploaded whenever Internet is available.", new SavedOffline(),
					"No Internet");
			ContentValues cv = new ContentValues();
			cv.put(DBConstants.MU_ID, Util.fetchUserClass(this.mContext).getUserId());
			cv.put(DBConstants.THREAD_ID, this.thread.getThreadID());
			cv.put(DBConstants.IMAGE, ((ImageClass) this.imagesList.get(0)).getBase64value());
			cv.put(DBConstants.LATITUDE, this.mCurrentLocation.getLatitude());
			cv.put(DBConstants.LONGITUDE, this.mCurrentLocation.getLongitude());
			cv.put(DBConstants.PICTURE_CATEGORY, this.dropDownActivity_pictureCategory.getText().toString().trim());
			cv.put(DBConstants.KEYWORDS, "12,13,14");
			cv.put(DBConstants.ADDRESS, this.geoAddress);
			cv.put(DBConstants.DATE, Util.getDate());
			cv.put(DBConstants.TIME, Util.getTime());
			cv.put(DBConstants.SCHOOL_CODE, completeSchoolCode);
			cv.put(DBConstants.RATH_NUMBER, StaticConstants.RATH_NUMBER_DEFAULT);
			cv.put(DBConstants.VILLAGE_NAME, villageName);
			cv.put(DBConstants.OTHER_DATA, "{\"sCode\":\"" + countryCode + "\",\"village\":\"" + villageName + "\"}");
			getContentResolver().insert(Constant.CONTENT_URI, cv);
			clearForm();
		}
	}

	public void onSuccess(JSONObject resultJsonObject) {
		Log.v(this.TAG, "" + resultJsonObject);
		if (resultJsonObject.toString() == null || resultJsonObject.toString().trim().isEmpty()) {
			Toast.makeText(this.mContext, " Request failed. Please try again.", 0).show();
			return;
		}
		try {
			String result = "";
			String message = "";
			result = resultJsonObject.optString("code");
			message = resultJsonObject.optString("msg");
			Log.v(this.TAG, result);
			if (result.equalsIgnoreCase("200")) {
				new HistoryDB().saveHistoryData(this.mContext, this.historyCV);
				Util.showCallBackMessageWithOkCallback(this.mContext, "Submision Complete", new SubmissionComplete());
				return;
			}
			Toast.makeText(this.mContext, " " + message, 0).show();
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this.mContext, " Request failed. Please try again.", 0).show();
		}
	}

	public void onError() {
	}

	private void clearForm() {
		this.imageCount = 0;
		this.dropDownActivity_pictureCategory.setText("");
		this.tv_imageProgress.setText("[Image added 0/1]");
		this.imagesList.clear();
		if (this.thread.getThreadID().equalsIgnoreCase("6")) {
			this.et_comments.setText("");
			this.tvCountryCode.setText("");
		}
		imageUpdateOnView();
		onPause();
	}

	public void onBackPressed() {
		super.onBackPressed();
	}

	protected void onDestroy() {
		super.onDestroy();
		System.out.println("On destroy");
		if (this.mGoogleApiClient != null) {
			this.mGoogleApiClient.disconnect();
		}
	}

	public void getLocationAGPS(Location location) {
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dropDownActivity_pictureCategory:
			startActivityForResult(new Intent(this.mContext, PictureCategoryActivity.class), 12);
			break;
		case R.id.tv_stateCode:
			startActivityForResult(new Intent(this.mContext, StateCodeActivity.class), 11);
			break;
		default:
			break;
		}
	}

	public synchronized void onActivityResult(int requestCode, int resultCode, Intent data) {
		System.out.println("------>> onActivityResult CALLED  >>---------------");
		if (requestCode == 11 && resultCode == Activity.RESULT_OK) {
			this.tv_stateCode.setText(data.getStringExtra(StateCodeActivity.RESULT_STATECODE));
			this.et_anchal.requestFocus();
			Util.showSoftKeyboard(this.mContext, this.et_anchal);
		} else if (requestCode == 12 && resultCode == Activity.RESULT_OK) {
			this.dropDownActivity_pictureCategory.setText(data.getStringExtra(PictureCategoryActivity.PICTURE_CATEGORY));
			this.et_comments.requestFocus();
		} else {
			hideProgressDialog();
			if (resultCode == Activity.RESULT_OK) {
				Uri selectedUri = null;
				switch (requestCode) {
				case CAMERA_PIC_REQUEST /*1337*/:
					selectedUri = mCapturedImageURI;
					break;
				case PICTURE_GALLERY_REQUEST /*2572*/:
					selectedUri = data.getData();
					break;
				}
				String[] filePathColumn = new String[] { "_data" };
				Cursor cursor = getContentResolver().query(selectedUri, filePathColumn, null, null, null);
				cursor.moveToFirst();
				String picturePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
				cursor.close();
				Log.v(this.TAG, "Picture path: " + picturePath);
				processImagePath(picturePath);
			} else if (!(requestCode == 11 || requestCode == 12)) {
				Log.w("DialogChoosePicture", "Warning: activity result not ok");
				Toast.makeText(this.mContext, "No image selected", Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.d("onSaveInstanceState", "onSaveInstanceState");
		Log.d("onSaveInstanceState", "Captured Uri" + mCapturedImageURI);
		System.out.println("------------------------------------\n");
		outState.putString("URI", "" + mCapturedImageURI);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {

		super.onRestoreInstanceState(savedInstanceState);
		System.out.println("------------------------------------\n");
		Log.d("onRestoreInstanceState", "onRestoreInstanceState");
		Log.d("onRestoreInstanceState", "Captured Uri " + mCapturedImageURI);
		System.out.println("------------------------------------\n");

		System.out.println("Restored URI " + savedInstanceState.getString("URI"));

	}
}
