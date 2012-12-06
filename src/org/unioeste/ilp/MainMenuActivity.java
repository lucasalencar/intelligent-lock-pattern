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

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Main menu of the application.
 * 
 * Some elements have permitions to be showed.
 * 
 * @author Lucas André de Alencar
 *
 */
public class MainMenuActivity extends BaseActivity {
	
	Button buttonInsertPattern;
	Button buttonShowPatterns;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        buttonInsertPattern = (Button) findViewById(R.id.button_insert_pattern);
        buttonShowPatterns = (Button) findViewById(R.id.button_show_patterns);
        
        setPermitions();
    }
    
    private void setPermitions () {
    	if (ilp.prefs.getBoolean("admin_mode", false)) {
    		buttonInsertPattern.setVisibility(View.VISIBLE);
    		buttonShowPatterns.setVisibility(View.VISIBLE);
    	} else {
    		buttonInsertPattern.setVisibility(View.GONE);
    		buttonShowPatterns.setVisibility(View.GONE);
    	}
    }
    
    public void gotoStartExperimentActivity (View view) {
    	Intent i = new Intent(this, StartExperimentActivity.class);
    	startActivity(i);
    }
    
    public void gotoInsertPatternActivity (View view) {
    	Intent i = new Intent(this, InsertPatternActivity.class);
    	startActivity(i);
    }
    
    public void gotoShowPatternsActivity (View view) {
    	Intent i = new Intent(this, ShowPatternsActivity.class);
    	startActivity(i);
    }
    
    public void gotoTestAuthActivity(View view) {
    	Intent i = new Intent(this, ShowPatternsActivity.class);
    	i.putExtra(ShowPatternsActivity.activity_flux, ShowPatternsActivity.TEST_AUTH_FLUX);
    	startActivity(i);
    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		super.onSharedPreferenceChanged(sharedPreferences, key);
		if (key.equals("admin_mode"))
			setPermitions();
	}
}