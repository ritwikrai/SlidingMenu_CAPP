package com.connectapp.user.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MembersDB implements DBConstants {
	private static MembersDB obj = null;


	public synchronized static MembersDB obj() {

		if (obj == null)
			obj = new MembersDB();
		return obj;

	}

	public Boolean saveHistoryData(Context context, ContentValues cv) {

		System.out.println(" ----------  SAVING MEMBER --------- ");
		SQLiteDatabase mdb = ConnectAppDBHelper.getInstance(context).getWritableDatabase();
		mdb.beginTransaction();
		try {
			mdb.insert(MEMBERS_DIRECTORY_TABLE, null, cv);
			mdb.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			mdb.endTransaction();
			return true;
		}

	}

	private Boolean isDatabaseEmpty(Cursor mCursor) {

		if (mCursor.moveToFirst()) {
			// NOT EMPTY
			return false;

		} else {
			// IS EMPTY
			return true;
		}

	}
}
