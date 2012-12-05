package org.unioeste.ilp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.unioeste.ilp.db.DBHelper;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * Activity responsible for exporting the database
 * of the application to an external directory on the
 * sd card to data extraction.
 * 
 * @author Lucas André de Alencar
 *
 */
public class DatabaseExporterActivity extends BaseActivity {

	private ExportDatabaseFileTask task;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		task = new ExportDatabaseFileTask();
	}

	@Override
	protected void onStart() {
		super.onStart();
		task.execute("");
		finish();
	}

	private class ExportDatabaseFileTask extends AsyncTask<String, Void, Boolean> {

		private final ProgressDialog dialog = new ProgressDialog(DatabaseExporterActivity.this);

		// can use UI thread here
		protected void onPreExecute() {
			this.dialog.setMessage(ilp.getText(R.string.export_db_executing));
			this.dialog.show();
		}

		// automatically done on worker thread (separate from UI thread)
		protected Boolean doInBackground(final String... args) {
			File dbFile = ilp.getDatabasePath(DBHelper.DB_NAME);
			File exportDir = ilp.getExternalFilesDir("IntelligentLockPattern");
			Log.i("ExportDatabaseFileTask", "Exporting db file to " + exportDir.getAbsolutePath());
			
			if (!exportDir.exists())
				exportDir.mkdirs();
			
			File file = new File(exportDir, dbFile.getName());

			try {
				file.createNewFile();
				this.copyFile(dbFile, file);
				return true;
			} catch (IOException e) {
				Log.e("ExportDatabaseFileTask", e.getMessage(), e);
				return false;
			}
		}

		// can use UI thread here
		protected void onPostExecute(final Boolean success) {
			if (this.dialog.isShowing())
				try {
					this.dialog.dismiss();
				} catch (IllegalArgumentException e) {
					Log.i("ExportDatabseFileTask", "Something went wrong, but I'll just ignore it! :)");
				}
				
			if (success)
				Toast.makeText(DatabaseExporterActivity.this, R.string.export_db_success, Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(DatabaseExporterActivity.this, R.string.export_db_fail, Toast.LENGTH_SHORT).show();
		}

		private void copyFile(File src, File dst) throws IOException {
			FileChannel inChannel = null;
			FileChannel outChannel = null;
			try {
				inChannel = new FileInputStream(src).getChannel();
				outChannel = new FileOutputStream(dst).getChannel();
				inChannel.transferTo(0, inChannel.size(), outChannel);
			} finally {
				if (inChannel != null)
					inChannel.close();
				if (outChannel != null)
					outChannel.close();
			}
		}
	}
}
