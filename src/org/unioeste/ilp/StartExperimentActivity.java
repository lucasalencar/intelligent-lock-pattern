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

import org.unioeste.ilp.adapters.ExperiencesUserAdapter;
import org.unioeste.ilp.models.Experience;
import org.unioeste.ilp.models.User;
import org.unioeste.ilp.models.UserDao.Properties;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity responsible for:
 * 	- Insert new users
 * 	- Permits selection of experiments already started but not ended
 * 	- Users that don't have already started a experiment, can start a new one.
 * 
 * @author Lucas André de Alencar
 *
 */
public class StartExperimentActivity extends BaseActivity {
	
	ExperiencesUserAdapter adapter;
	ListView list;
	TextView user_name;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_user);
		
		user_name = (EditText) findViewById(R.id.user_name);
		list = (ListView) findViewById(R.id.users_list_view);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		user_name.setText(""); // Cleans the user name field
		updateUsersList();
		
		// Set the action when User is selected on list
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectedListItemAction(position);
			}

		});
	}
	
	private void updateUsersList () {
		List<User> users = ilp.userDao.queryBuilder().orderDesc(Properties.Id).list();
		adapter = new ExperiencesUserAdapter(this, users, CollectSamplesActivity.defaultNumberOfTries);
		list.setAdapter(adapter);
	}
	
	public void selectedListItemAction (int position) {
		User user = (User) list.getAdapter().getItem(position);
		List<Experience> experiences = user.getExperiences();
		
		if (experiences.size() > 0)
			gotoCollectSamplesActivity(user, experiences.get(experiences.size() - 1));
		else
			gotoShowPatternsActivity(user);
	}
	
	public void continueButtonAction (View view) {		
		if (!user_name.getText().toString().equals("")) {
			User user = new User(null, user_name.getText().toString());
			addUser(user);
			gotoShowPatternsActivity(user);
		} else {
			Toast.makeText(this, R.string.insert_user_name_error, Toast.LENGTH_SHORT).show();
		}
	}
	
	public void newExperimentButtonAction (View view) {
		int position = list.getPositionForView((View) (view.getParent()));
		User user = (User) list.getAdapter().getItem(position);
		gotoShowPatternsActivity(user);
	}
	
	private void addUser (User user) {
		try {
			ilp.insertUser(user);
			updateUsersList();
		} catch (SQLiteConstraintException e) {
			Toast.makeText(this, R.string.user_exist, Toast.LENGTH_SHORT).show();
		}
	}
	
	private void gotoCollectSamplesActivity (User user, Experience experience) {
		Intent i = new Intent(this, CollectSamplesActivity.class);
		
		if (experience != null)
			i.putExtra(CollectSamplesActivity.experience_id, experience.getId());
		
		if (user != null)
			i.putExtra(CollectSamplesActivity.user_id, user.getId());
		
		startActivity(i);
	}
	
	private void gotoShowPatternsActivity (User user) {
		Intent i = new Intent(this, ShowPatternsActivity.class);
		
		if (user != null)
			i.putExtra(ShowPatternsActivity.user_id, user.getId());
		
		startActivity(i);
	}
}
