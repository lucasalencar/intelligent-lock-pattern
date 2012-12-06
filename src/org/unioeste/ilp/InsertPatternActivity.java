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

import java.util.ArrayList;
import java.util.List;

import org.unioeste.ilp.models.Pattern;

import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Inserts new patterns.
 * 
 * @author Lucas André de Alencar
 *
 */
public class InsertPatternActivity extends LockPatternActivity {

    private Button confirmButton;
    
    private List<Cell> lastPattern;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.insert_pattern);
    	
    	setLockTextInfo(R.id.lock_text_info);
    	setLockPatternView(R.id.lock_pattern_view);
    	confirmButton = (Button) findViewById(R.id.insert_pattern_continue);
    	
    	lockTextInfo.setText(R.string.draw_pattern_and_press_continue);
    	lockPatternView.setOnPatternListener(mPatternViewListener);
    }
    
    private void doCreatePattern(List<Cell> pattern) {
    	if (pattern.size() < 4) {
            lockPatternView.setDisplayMode(DisplayMode.Wrong);
            lockTextInfo.setText(R.string.connect_4dots_message);
            return;
        }
    	
    	if (lastPattern == null) {
            lastPattern = new ArrayList<LockPatternView.Cell>();
            lastPattern.addAll(pattern);
            lockTextInfo.setText(R.string.pattern_record_message);
            confirmButton.setEnabled(true);
        } else {
            if (LockPatternUtils.patternToSha1(lastPattern).equals(LockPatternUtils.patternToSha1(pattern))) {
                lockTextInfo.setText(R.string.your_new_unlock_pattern_message);
                confirmButton.setEnabled(true);
            } else {
                lockTextInfo.setText(R.string.redraw_pattern_to_confirm_message);
                confirmButton.setEnabled(false);
                lockPatternView.setDisplayMode(DisplayMode.Wrong);
            }
        }
    }
    
    private final LockPatternView.OnPatternListener mPatternViewListener = new LockPatternView.OnPatternListener() {
		
		@Override
		public void onPatternStart() {
            lockPatternView.setDisplayMode(DisplayMode.Correct);

            lockTextInfo.setText(R.string.release_finger_when_done_message);
            confirmButton.setEnabled(false);
            if (getString(R.string.continue_to_next).equals(confirmButton.getText()))
            	lastPattern = null;
		}
		
		@Override
		public void onPatternDetected(List<Cell> pattern) {
			doCreatePattern(pattern);
		}
		
		@Override
		public void onPatternCleared() {
			// ignore it
		}
		
		@Override
		public void onPatternCellAdded(List<Cell> pattern, MotionEvent last_event) {
			// ignore it
		}
		
		@Override
		public void onPatternCellAdded(List<Cell> pattern) {
			// ignore it
		}
	};
	
	public void confirmButtonAction (View view) {
		if (getString(R.string.continue_to_next).equals(confirmButton.getText())) {
            lockPatternView.clearPattern();
            lockTextInfo.setText(R.string.redraw_pattern_to_confirm_message);
            confirmButton.setText(R.string.confirm);
            confirmButton.setEnabled(false);
        } else {
        	String pattern_sha1 = LockPatternUtils.patternToSha1(lastPattern);
        	String pattern_string = LockPatternUtils.patternToString(lastPattern);
        	Pattern pattern = new Pattern(null, pattern_sha1, pattern_string);
        	try {
        		ilp.insertPattern(pattern);
        	} catch (SQLiteConstraintException e) {
        		Toast.makeText(this, R.string.pattern_exist, Toast.LENGTH_SHORT).show();
        	}
            finish();
        }
	}
	
	public void cancelButtonAction (View view) {
		finish();
	}
}
