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

import group.pals.android.lib.ui.lockpattern.widget.LockPatternView;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;

/**
 * General class used by activities that requires the LockPattern.
 * 
 * Contains the text info and LockPattern View attributes, considered
 * common to all activities that use LockPattern.
 * 
 * @author Lucas André de Alencar
 *
 */
public class LockPatternActivity extends BaseActivity {

	public static final String _ClassName = LockPatternActivity.class.getName();
	
	protected TextView lockTextInfo;
    protected LockPatternView lockPatternView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    }
    
    protected void initTactileFeedback () {
		// haptic feedback
        boolean hapticFeedbackEnabled = false;
        try {
            hapticFeedbackEnabled = Settings.System.getInt(getContentResolver(),
                    Settings.System.HAPTIC_FEEDBACK_ENABLED, 0) != 0;
        } catch (Throwable t) {
            // ignore it
        }
        lockPatternView.setTactileFeedbackEnabled(hapticFeedbackEnabled);
	}
    
    protected void setLockTextInfo (int rvalue) {
    	lockTextInfo = (TextView) findViewById(rvalue);
    }
    
    protected void setLockPatternView (int rvalue) {
    	lockPatternView = (LockPatternView) findViewById(rvalue);
    	lockPatternView.setTactileFeedbackEnabled(false);
    }
}
