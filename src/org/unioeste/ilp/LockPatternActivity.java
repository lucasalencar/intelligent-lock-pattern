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
