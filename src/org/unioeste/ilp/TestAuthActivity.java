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

import group.pals.android.lib.ui.lockpattern.widget.LockPatternUtils;
import group.pals.android.lib.ui.lockpattern.widget.LockPatternView;
import group.pals.android.lib.ui.lockpattern.widget.LockPatternView.Cell;
import group.pals.android.lib.ui.lockpattern.widget.LockPatternView.DisplayMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.unioeste.ilp.models.Pattern;
import org.unioeste.ilp.models.User;
import org.unioeste.ilp.network.ReplicatorNeuralNetwork;
import org.unioeste.ilp.network.util.DataSetNormalizer;
import org.unioeste.ilp.services.SamplesCollectorService;
import org.unioeste.ilp.services.SamplesCollectorService.SamplesCollectorBinder;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

/**
 * Activity responsible for loading the
 * neural network trained on the computer with the characteristics
 * of a user and allow the user to make tests with the network
 * to see if it really authenticates the user as expected.
 * 
 * @author Lucas André de Alencar
 *
 */
public class TestAuthActivity extends LockPatternActivity {

	public static final String _PaternSha1 = _ClassName + ".pattern_sha1";
    public static final String user_id = _ClassName + ".user_id";
    public static final String pattern_id = _ClassName + ".pattern_id";
    
    User user;
    Pattern pattern;
    ReplicatorNeuralNetwork network;
	
	private List<Cell> lastPattern;
	
	SamplesCollectorService samples_collector;
	boolean boundFlag;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getIntentExtras();
		
		pattern = getPattern();
		if (pattern == null) {
			Log.w("TestAuthActivity", "No pattern specified on the activity.");
			finish();
		}
		
		loadNetwork(pattern.getId(), 80);
		
		setContentView(R.layout.test_auth);
		
		setLockTextInfo(R.id.text_info);
		setLockPatternView(R.id.lock_pattern);
		
		lockTextInfo.setText("Desenhe o padrão gráfico escolhido.");
		
		lockPatternView.setOnPatternListener(mPatternViewListener);
	}
	
	private void loadNetwork(long patternId, int trainingNumber) {
		File extDir = ilp.getExternalFilesDir("IntelligentLockPattern");
		if (!extDir.exists())
			extDir.mkdirs();
		
		File file = new File(extDir, "pattern " + patternId + "/" + trainingNumber);
		try {
			network = new ReplicatorNeuralNetwork(file);
		} catch (Exception e) {
			Log.e("TestAuthActivity", "Problems loading network file.");
			finish();
		}
		Log.i("TestAuthActivity", "Loaded network from file " + file.getAbsolutePath());
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
		if (getIntent().getLongExtra(user_id, 0) != 0)
			user = ilp.userDao.load(getIntent().getLongExtra(user_id, 0));
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
	
	private void doComparePattern (List<Cell> pattern) {
		if (pattern == null) return;
		
		lastPattern = new ArrayList<LockPatternView.Cell>();
        lastPattern.addAll(pattern);
        
        if (LockPatternUtils.patternToSha1(pattern).equals(this.pattern.getPattern_sha1()) && authenticateAttempt(samples_collector.getAttempt())) {
        	Toast.makeText(this, "Autenticado biometricamente!!!", Toast.LENGTH_SHORT).show();
            lockPatternView.clearPattern();
        } else {
	        lockPatternView.setDisplayMode(DisplayMode.Wrong);
        }
        samples_collector.cleanSamples();
	}
	
	private boolean authenticateAttempt(double [] attempt) {
		attempt = DataSetNormalizer.normalize(
				ReplicatorNeuralNetwork.input_low_norm, 
				ReplicatorNeuralNetwork.input_high_norm, 
				ReplicatorNeuralNetwork.output_low_norm, 
				ReplicatorNeuralNetwork.output_high_norm, 
				attempt
		);
		double [] output = network.compute(attempt);
		double error = network.calculateError(attempt, output);
		Log.i("TestAuthActivity", error + " " + network.getTrainError() + " " + (error < network.getTrainError()));
		return error < network.getTrainError();
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
