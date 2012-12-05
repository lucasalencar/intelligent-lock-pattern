package org.unioeste.ilp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * The class BaseActivity is responsible for encapsulate some
 * common attributes that activities on this application have.
 * 
 * Implements listener onSharedPreferenceChanged that watches
 * for changes on the DropboxService configuration, either it's
 * starting or stopping. 
 * 
 * All the activities that inherents from this class, when 
 * implementing specific actions to SharedPreferecesListener,
 * its important to call the super method before any action.  
 * 
 * Encapsulates the ILPApp, so that all the activities have
 * access to the same object.
 * 
 * Responsible for showing the menu when MENU button is pressed
 * and the actions performed when some of the items is selected.
 * 
 * @author Lucas André de Alencar
 *
 */
public abstract class BaseActivity extends Activity implements OnSharedPreferenceChangeListener {
	
	private static final String TAG = "BaseActivity";
	
	protected ILPApp ilp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ilp = (ILPApp) getApplication();
		ilp.prefs.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemPrefs	:
			startActivity(new Intent(this, PrefsActivity.class));
			break;
		case R.id.itemExportDb:
			startActivity(new Intent(this, DatabaseExporterActivity.class));
			break;
		default:
			Log.w(TAG, "Opção " + item.getItemId() + " inexistente!");
			break;
		}
		return true;
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		Log.i(TAG, "Shared Preference Key: " + key);
	}
}
