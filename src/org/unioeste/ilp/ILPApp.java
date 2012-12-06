/*
 * "Copyright 2012 Lucas André de Alencar"
 * 
 * This file is part of "Intelligent Lock Pattern".
 * 
 * "Intelligent Lock Pattern" is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 * 
 * "Intelligent Lock Pattern" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 * GNU General Public License for more details. 
 * 
 * You should have received a copy of the GNU General Public License 
 * along with "Intelligent Lock Pattern".  If not, see <http://www.gnu.org/licenses/>.
 */

package org.unioeste.ilp;

import java.util.List;

import org.unioeste.ilp.db.DBHelper;
import org.unioeste.ilp.models.Attempt;
import org.unioeste.ilp.models.AttemptDao;
import org.unioeste.ilp.models.DaoSession;
import org.unioeste.ilp.models.Experience;
import org.unioeste.ilp.models.ExperienceDao;
import org.unioeste.ilp.models.Pattern;
import org.unioeste.ilp.models.PatternDao;
import org.unioeste.ilp.models.Sample;
import org.unioeste.ilp.models.SampleDao;
import org.unioeste.ilp.models.User;
import org.unioeste.ilp.models.UserDao;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Methods used on every activity on the app.
 * 
 * @author Lucas André de Alencar
 *
 */
public class ILPApp extends Application {

	private static final String TAG = ILPApp.class.getSimpleName();
	
	DBHelper dbHelper;
	
	UserDao userDao;
	PatternDao patternDao;
	ExperienceDao experienceDao;
	AttemptDao attemptDao;
	SampleDao sampleDao;
	
	SharedPreferences prefs;
	
	@Override
	public void onCreate() {
		super.onCreate();
		dbHelper = new DBHelper(this);
		instantiateDaos(dbHelper.getDaoSession()); // Instantiate Daos for database access
		initPrefs(); // Gets the preferences of the app
		Log.i(TAG, "ILPApp created.");
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
		dbHelper.close();
		Log.i(TAG, "ILPApp terminated.");
	}
	
	private void initPrefs () {
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
	}
	
	private void instantiateDaos (DaoSession daoSession) {
		userDao = daoSession.getUserDao();
		patternDao = daoSession.getPatternDao();
		experienceDao = daoSession.getExperienceDao();
		attemptDao = daoSession.getAttemptDao();
		sampleDao = daoSession.getSampleDao();
	}
	
	public void insertUser (User user) {
		userDao.insert(user);
		Log.d(TAG, "Inserted user " + user.getName() + " with id " + user.getId());
	}
	
	public void insertAttempt (Attempt attempt) {
		attemptDao.insert(attempt);
		Log.d(TAG, "Inserted attempt with id " + attempt.getId());
	}
	
	public void insertSample (Sample sample) {
		sampleDao.insert(sample);
		Log.d(TAG, "Inserted sample with id " + sample.getId());
	}
	
	public void insertPattern (Pattern pattern) {
		patternDao.insert(pattern);
		Log.d(TAG, "Inserted pattern with id " + pattern.getId());
	}
	
	public void insertExperience (Experience experience) {
		experienceDao.insert(experience);
		Log.d(TAG, "Inserted experience with id " + experience.getId());
	}
	
	public void updateExperience (Experience experience) {
		experienceDao.update(experience);
		Log.d(TAG, "Updated experience with id " + experience.getId());
	}
	
	public Pattern rafflePattern () {
		List<Pattern> patterns = patternDao.loadAll();
		int rand = (int) (Math.random() * (patterns.size()-1));
		return patterns.get(rand);
	}
}
