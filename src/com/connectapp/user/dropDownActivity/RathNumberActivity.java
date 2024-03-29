package com.connectapp.user.dropDownActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.connectapp.user.R;
import com.connectapp.user.adapter.RathNumberListArrayAdapter;
import com.connectapp.user.data.Rath;
import com.connectapp.user.data.State;

public class RathNumberActivity extends Activity {

	private ListView listView_rath;
	public static String RATH = "rath";
	private String[] countrynames, countrycodes;
	private TypedArray imgs;
	private List<Rath> rathLIst;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dropdown_layout);
		listView_rath = (ListView) findViewById(R.id.item_listView_dropDown);
		populateCountryList();
		RathNumberListArrayAdapter adapter = new RathNumberListArrayAdapter(RathNumberActivity.this,
				rathLIst);

		listView_rath.setAdapter(adapter);

		listView_rath.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Rath c = rathLIst.get(position);
				Intent returnIntent = new Intent();
				returnIntent.putExtra(RATH, c.getName());
				setResult(RESULT_OK, returnIntent);
				//imgs.recycle();
				// recycle images
				finish();
			}
		});

	}

	private void populateCountryList() {
		rathLIst = new ArrayList<Rath>();
		countrynames = getResources().getStringArray(R.array.rath_number_array);
		countrycodes = getResources().getStringArray(R.array.rath_number_array);
		//imgs = getResources().obtainTypedArray(R.array.country_flags);
		for (int i = 0; i < countrynames.length; i++) {
			rathLIst.add(new Rath(countrynames[i], countrycodes[i]));
		}

	}

	private String fetchPhoneCodeFromName(String countryName, List<State> countryList) {
		System.out.println("Country Name: " + countryName);
		HashMap<String, String> countryCodeMap = new HashMap<String, String>();
		for (int i = 0; i < countryList.size(); i++) {
			String[] countryArray = countryList.get(i).getName().split("\\,");
			//String countryNamefromMap = countryArray[1];

			//countryCodeMap.put(countryNamefromMap, countryList.get(i).getCode());

		}
		Log.v("Hashmap", "" + countryCodeMap.toString());
		System.out.println("" + countryCodeMap.get(countryName));
		return countryCodeMap.get(countryName).toString();
	}

}
