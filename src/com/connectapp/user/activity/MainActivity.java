package com.connectapp.user.activity;

import java.util.ArrayList;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.connectapp.user.R;
import com.connectapp.user.adapter.DrawerAdapter;
import com.connectapp.user.constant.Consts;
import com.connectapp.user.data.OfflineSubmission;
import com.connectapp.user.data.UserClass;
import com.connectapp.user.db.HistoryDB;
import com.connectapp.user.font.RobotoTextView;
import com.connectapp.user.fragment.DashboardFragment;
import com.connectapp.user.syncadapter.Constant;
import com.connectapp.user.syncadapter.DBConstants;
import com.connectapp.user.syncadapter.OfflineDB;
import com.connectapp.user.util.ImageUtil;
import com.connectapp.user.util.Util;
import com.connectapp.user.volley.VolleyTaskManager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MainActivity extends ActionBarActivity implements DBConstants {

	private ListView mDrawerList;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;

	private ArrayList<OfflineSubmission> offlineSubmissions = new ArrayList<OfflineSubmission>();
	private Context mContext;
	private Dialog customDialog;

	private VolleyTaskManager volleyTaskManager;

	private int dataCount = 0;
	private boolean isPostOfflineData = false;
	private UserClass userClass;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard_raga);
		mContext = MainActivity.this;

		userClass = Util.fetchUserClass(mContext);

		ImageLoader imageLoader = ImageLoader.getInstance();
		if (!imageLoader.isInited()) {
			imageLoader.init(ImageLoaderConfiguration.createDefault(this));
		}
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle("ConnectApp");
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mTitle = mDrawerTitle = getTitle();
		mDrawerList = (ListView) findViewById(R.id.list_view);

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		View headerView = getLayoutInflater().inflate(R.layout.header_navigation_drawer_social, mDrawerList, false);
		RobotoTextView robotoTextView = (RobotoTextView) headerView.findViewById(R.id.tv_userName);
		if (userClass != null)
			robotoTextView.setText(userClass.getName());
		ImageView iv = (ImageView) headerView.findViewById(R.id.image);
		//		ImageUtil.displayRoundImage(iv, "http://pengaja.com/uiapptemplate/newphotos/profileimages/0.jpg",
		//				null);
		ImageUtil.displayRoundImage(iv, "", null);
		/*ImageView iv = (ImageView) headerView.findViewById(R.id.image);
		iv.setImageDrawable(getResources().getDrawable(R.drawable.default_image));*/

		mDrawerList.addHeaderView(headerView);// Add header before adapter (for
												// pre-KitKat)
		mDrawerList.setAdapter(new DrawerAdapter(this));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		int color = getResources().getColor(R.color.material_grey_100);
		color = Color.argb(0xCD, Color.red(color), Color.green(color), Color.blue(color));
		mDrawerList.setBackgroundColor(color);
		mDrawerList.getLayoutParams().width = (int) getResources().getDimension(R.dimen.drawer_width_social);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				getSupportActionBar().setTitle(mTitle);
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			displayView(1);
		}

	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			displayView(position);

			mDrawerLayout.closeDrawer(mDrawerList);
		}
	}


	@Override
	public void setTitle(int titleId) {
		setTitle(getString(titleId));
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	private void displayView(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;
		switch (position) {

		case 1:
			fragment = new DashboardFragment();
			//Util.showMessageWithOk(mContext, "HOME");
			break;

		case 2:

			// UNSYNCED DATA
			onDataSettingClick();
			break;

		case 3:

			onHistoryClick();
			break;
		case 4:
			String url = Consts.PRIVACY_POLICY_URL;
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
			break;
		case 5:
			onLogoutClick();
			break;
		default:
			break;
		}

		if (fragment != null) {
			replaceFragment(fragment, position);
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
	}

	private void replaceFragment(Fragment fragment, int position) {

		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

		// update selected item and title, then close the drawer
		mDrawerList.setItemChecked(position, true);
		mDrawerList.setSelection(position);
		mDrawerLayout.closeDrawer(mDrawerList);

	}

	/** Un-synced Data option */
	private void onDataSettingClick() {

		customDialog = new Dialog(mContext);

		customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

		LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.dialog_offline_data_status, null);

		ImageButton btn_close = (ImageButton) view.findViewById(R.id.btn_close);

		btn_close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				customDialog.dismiss();

			}
		});

		TextView tv_unsyncedData = (TextView) view.findViewById(R.id.tv_unsyncedData);

		offlineSubmissions.clear();
		try {
			fetchOfflineRows();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tv_unsyncedData.setText("Unsynced Data: (" + offlineSubmissions.size() + ")");

		}

		RelativeLayout rl_unsyncedDataCountLayout = (RelativeLayout) view.findViewById(R.id.rl_unsyncedDataCount);

		rl_unsyncedDataCountLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (offlineSubmissions.size() > 0) {
					customDialog.dismiss();
					startActivity(new Intent(mContext, UnSyncedDataActivity.class));
				}

			}
		});

		/*Button btn_submit = (Button) view.findViewById(R.id.btn_sync_now);
		btn_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Util.isInternetAvailable(mContext)) {
					if (offlineSubmissions.size() > 0) {
						System.out.println("Offline Data size: " + offlineSubmissions.size());
						postOfflineData();
					} else {
						Toast.makeText(mContext, "You have no offline data", Toast.LENGTH_SHORT).show();
					}
				} else {
					Util.showMessageWithOk(mContext, "No Internet Access.");
				}
			}
		});*/
		customDialog.setCancelable(false);
		customDialog.setContentView(view);
		customDialog.setCanceledOnTouchOutside(false);
		// Start AlertDialog
		customDialog.show();
	}

	private void onLogoutClick() {

		startActivity(new Intent(mContext, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
		UserClass userClass = Util.fetchUserClass(mContext);

		if (userClass != null) {
			userClass.setIsLoggedin(false);
			Util.saveUserClass(mContext, userClass);
		}
		finish();
	}

	private void fetchOfflineRows() {
		OfflineDB mDb = new OfflineDB(mContext);
		SQLiteDatabase database = mDb.getReadableDatabase();

		try {
			Cursor cur = database.query(OFFLINE_TABLE, null, null, null, null, null, null);
			System.out.println("Count: " + cur.getCount());
			if (cur != null) {
				while (cur.moveToNext()) {
					offlineSubmissions.add(Constant.getOfflineSubmissionFromCursor(cur));
				}
				cur.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void onHistoryClick() {

		ArrayList<OfflineSubmission> offlineSubmissions = new ArrayList<OfflineSubmission>();
		offlineSubmissions = new HistoryDB().getHistory(mContext);
		if (offlineSubmissions.size() > 0)
			startActivity(new Intent(mContext, HistoryActivity.class));
		else
			Util.showMessageWithOk(MainActivity.this, "You have no submission History!");

	}
}
