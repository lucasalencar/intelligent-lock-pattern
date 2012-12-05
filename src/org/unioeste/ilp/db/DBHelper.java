package org.unioeste.ilp.db;

import org.unioeste.ilp.models.DaoMaster;
import org.unioeste.ilp.models.DaoSession;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Responsible for establishing a session with the database. 
 * 
 * @author Lucas André de Alencar
 *
 */
public class DBHelper {

	private static final String TAG = "DBHelper";
	public static final String DB_NAME = "ilp.db";
	
	final DaoMaster.DevOpenHelper helper;
	
	public DBHelper (Context context) {
		helper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
		Log.i(TAG, "DBHelper created.");
	}
	
	public DaoSession getDaoSession () {
		SQLiteDatabase db = helper.getWritableDatabase();
		DaoMaster daoMaster = new DaoMaster(db);
		DaoSession daoSession = daoMaster.newSession();
		return daoSession;
	}
	
	public void close() {
		helper.close();
	}
	
}
