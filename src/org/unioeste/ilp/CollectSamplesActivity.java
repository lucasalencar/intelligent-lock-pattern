package org.unioeste.ilp;

import group.pals.android.lib.ui.lockpattern.widget.LockPatternUtils;
import group.pals.android.lib.ui.lockpattern.widget.LockPatternView;
import group.pals.android.lib.ui.lockpattern.widget.LockPatternView.Cell;
import group.pals.android.lib.ui.lockpattern.widget.LockPatternView.DisplayMode;

import java.util.ArrayList;
import java.util.List;

import org.unioeste.ilp.models.Experience;
import org.unioeste.ilp.models.Pattern;
import org.unioeste.ilp.models.User;
import org.unioeste.ilp.services.SamplesCollectorService;
import org.unioeste.ilp.services.SamplesCollectorService.SamplesCollectorBinder;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;

/**
 * Collects the samples from user and persists it.
 * 
 * @author Lucas André de Alencar
 *
 */
public class CollectSamplesActivity extends LockPatternActivity {
	
	/**
	 * Constants to standardize the parameters
	 */
    public static final String _PaternSha1 = _ClassName + ".pattern_sha1";
    public static final String user_id = _ClassName + ".user_id";
    public static final String pattern_id = _ClassName + ".pattern_id";
    public static final String experience_id = _ClassName + ".experience_id";
    public static final String numberOfTries = _ClassName + ".positive_entries";
    
    /** If param positive_entries are not specified, this value is used. */
    public static final int defaultNumberOfTries = 100;
    
	int numberOfAttempts;
	
	User user;
	Experience experience;
	
	private List<Cell> lastPattern;
	
	SamplesCollectorService samples_collector;
	boolean boundFlag;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getIntentExtras();
		
		if (haveExperience()) {
			user = experience.getUser();
			numberOfAttempts = getNumberOfAttemptsRemaining();
		} else {
			createExperience();
		}
		
		setContentView(R.layout.collect_samples);
		
		setLockTextInfo(R.id.text_info);
		setLockPatternView(R.id.samples_lock_pattern);
		
		updateNumberOfTries(numberOfAttempts);
		lockPatternView.setOnPatternListener(mPatternViewListener);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		bindSamplesCollectorService();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		if (boundFlag) {
			unbindService(serviceConnection);
			boundFlag = false;
		}
	}
	
	private void getIntentExtras () {
		numberOfAttempts = getIntent().getIntExtra(numberOfTries, defaultNumberOfTries);
		
		if (haveExperience())
			experience = ilp.experienceDao.load(getIntent().getLongExtra(experience_id, 0));
		else if (getIntent().getLongExtra(user_id, 0) != 0)
			user = ilp.userDao.load(getIntent().getLongExtra(user_id, 0));
		else
			finish();
	}
	
	private int getNumberOfAttemptsRemaining () {
		return numberOfAttempts - experience.getAttempts().size();
	}
	
	private boolean haveExperience () {
		return getIntent().getLongExtra(experience_id, 0) != 0;
	}
	
	private void createExperience () {
		Pattern pattern = getPattern();
		if (pattern != null) {
			experience = new Experience(null, false, user.getId(), pattern.getId());
			ilp.insertExperience(experience);
		} else {
			finish();
		}
	}
	
	private Pattern getPattern () {
		if (getIntent().getLongExtra(pattern_id, 0) != 0)
			return ilp.patternDao.load(getIntent().getLongExtra(pattern_id, 0));
		else
			return null;
	}
	
	private void bindSamplesCollectorService () {
		Intent i = new Intent(this, SamplesCollectorService.class);
		bindService(i, serviceConnection, Context.BIND_AUTO_CREATE);
	}
	
	private void updateNumberOfTries (int numberOfAttempts) {
		java.util.Formatter formatter = new java.util.Formatter();
		lockTextInfo.setText(formatter.format(getString(R.string.number_of_tries), numberOfAttempts).toString());
		formatter.close();
	}
	
	private void doComparePattern (List<Cell> pattern) {
		if (pattern == null) return;
		
		lastPattern = new ArrayList<LockPatternView.Cell>();
        lastPattern.addAll(pattern);
        
        if (LockPatternUtils.patternToSha1(pattern).equals(experience.getPattern().getPattern_sha1())) {
            samples_collector.persistSamples(experience);
        	if (--numberOfAttempts == 0) finishExperiment();
        	updateNumberOfTries(numberOfAttempts);
        	lockPatternView.clearPattern();
        } else {
        	samples_collector.cleanSamples();
        	lockPatternView.setDisplayMode(DisplayMode.Wrong);
        }
	}
	
	private void finishExperiment () {
		experience.setDone(true);
		ilp.updateExperience(experience);
		finish();
	}
	
	private final LockPatternView.OnPatternListener mPatternViewListener = new LockPatternView.OnPatternListener() {
		
		@Override
		public void onPatternStart() {
			lockPatternView.setDisplayMode(DisplayMode.Correct);
		}
		
		@Override
		public void onPatternDetected(List<Cell> pattern) {
			doComparePattern(pattern);
		}
		
		@Override
		public void onPatternCleared() {
			lockPatternView.setDisplayMode(DisplayMode.Correct);
		}
		
		@Override
		public void onPatternCellAdded(List<Cell> pattern, MotionEvent event) {
			samples_collector.addSample(pattern.get(pattern.size() - 1), event);
		}

		@Override
		public void onPatternCellAdded(List<Cell> pattern) {
			// ignore it
		}
	};
	
	private ServiceConnection serviceConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			boundFlag = false;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			SamplesCollectorBinder binder = (SamplesCollectorBinder) service;
			samples_collector = binder.getService();
			boundFlag = true;
		}
	};
}
