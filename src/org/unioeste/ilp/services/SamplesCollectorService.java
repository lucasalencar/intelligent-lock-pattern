package org.unioeste.ilp.services;

import group.pals.android.lib.ui.lockpattern.widget.LockPatternView.Cell;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.unioeste.ilp.ILPApp;
import org.unioeste.ilp.models.Attempt;
import org.unioeste.ilp.models.Experience;
import org.unioeste.ilp.models.Sample;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Stores the samples when user is drawing a pattern on order
 * and persist all the samples relating it with a attempt.
 * 
 * Works through binding to activities.
 * 
 * Manages the Attempts and Samples of a Experience.
 * 
 * @author Lucas André de Alencar
 *
 */
public class SamplesCollectorService extends Service {
	
	private static final String TAG = "SamplesCollectorService";
	
	public static final String pattern_id = TAG + ".pattern_id";

	private final IBinder binder = new SamplesCollectorBinder();
	
	ILPApp ilp;
	
	Attempt attempt;
	List<Sample> samples;
	
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		ilp = (ILPApp) getApplication();
		samples = new ArrayList<Sample>();
	}
	
	public void addSample (Cell cell, MotionEvent event) {
		Log.d(TAG, "Adding sample to cell " + cell);
		Sample s = new Sample(null, event.getEventTime(), event.getPressure(), event.getSize(), null);
		samples.add(s);
	}
	
	public void persistSamples (Experience experience) {
		if (samples == null) throw new IllegalStateException("No samples to persist");
		newAttempt(experience);
		
		Iterator<Sample> it = samples.iterator();
		while (it.hasNext()) {
			Sample sample = it.next();
			sample.setAttempt(attempt);
			ilp.insertSample(sample);
		}
		cleanSamples();
	}
	
	public double[] getAttempt() {
		double [] s = new double[samples.size() * 3 - 1];
		Iterator<Sample> it = samples.iterator();
		int i = 0;
		double last_event_time = 0;
		
		while (it.hasNext()) {
			Sample sample = (Sample) it.next();
			if (last_event_time != 0)
				s[i++] = sample.getEvent_time() - last_event_time;
			
			s[i++] = sample.getPressure();
			s[i++] = sample.getPressure_area();
			
			last_event_time = sample.getEvent_time();
		}
		return s;
	}
	
	private void newAttempt (Experience experience) {
		attempt = new Attempt(null, experience.getId());
		ilp.insertAttempt(attempt);
	}
	
	public void cleanSamples () {
		samples.clear();
		Log.d(TAG, "Cleaned samples");
	}
	
	public class SamplesCollectorBinder extends Binder {
		public SamplesCollectorService getService () {
			return SamplesCollectorService.this;
		}
	}
}
