package com.connectapp.user.syncadapter;

public interface DBConstants {

	public static final int DB_VERSION = 2;// DB version changed on 30-3-2107

	public static final String DB_NAME = "Offline.db";

	//public static final String DB_NAME = Environment.getExternalStorageDirectory() + "/Offline.db";

	public static final String _ID = "_id";

	final String CREATE_TABLE_BASE = "CREATE TABLE IF NOT EXISTS ";

	final String ON = " ON ";

	final String PRIMARY_KEY = " PRIMARY KEY";

	final String INTEGER = " Integer";

	final String TEXT = " TEXT";

	final String DATE_TIME = " DATETIME";

	final String BLOB = " BLOB";

	final String AUTO_ICNREMENT = " AUTOINCREMENT";

	final String UNIQUE = "UNIQUE";

	final String START_COLUMN = " ( ";

	final String FINISH_COLUMN = " ) ";

	final String COMMA = ",";

	final String ON_CONFLICT_REPLACE = "ON CONFLICT REPLACE";

	// OFFLINE Table
	public static final String OFFLINE_TABLE = " offlineTable";

	public static final String MU_ID = "muID";

	public static final String THREAD_ID = "threadId";

	public static final String IMAGE = "image";

	public static final String LATITUDE = "latitude";

	public static final String LONGITUDE = "longitude";

	public static final String PICTURE_CATEGORY = "comments";

	public static final String KEYWORDS = "keywords";

	public static final String ADDRESS = "address";

	public static final String DATE = "date";

	public static final String TIME = "time";

	public static final String SCHOOL_CODE = "schoolCode";

	public static final String RATH_NUMBER = "rathNumber";

	public static final String VILLAGE_NAME = "villageName";

	public static final String OTHER_DATA = "otherData";

}
