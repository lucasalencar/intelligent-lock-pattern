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

package org.unioeste.ilp.adapters;

import java.util.List;

import org.unioeste.ilp.R;
import org.unioeste.ilp.models.Experience;
import org.unioeste.ilp.models.User;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

/**
 * Adapter responsible for inflating the elements
 * of interface used on the users list on 
 * activity StartExperimentActivity.
 * 
 * @author Lucas André de Alencar
 *
 */
public class ExperiencesUserAdapter extends BaseAdapter {

	List<User> users;
	int maxAttempts;
	LayoutInflater mInflater;
	ViewHolder holder;
	
	private static class ViewHolder {
		private TextView userName;
		private TextView userAttempts;
		private Button newAttemptButton;
	}
	
	public ExperiencesUserAdapter (Context context, List<User> users, int maxAttempts) {
		mInflater = LayoutInflater.from(context);
		this.users = users;
		this.maxAttempts = maxAttempts;
	}
	
	@Override
	public int getCount() {
		return users.size();
	}

	@Override
	public Object getItem(int position) {
		return users.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.experiences_list_item, null);
			holder = new ViewHolder();
			
			holder.userName = (TextView) convertView.findViewById(R.id.user_name_text);
			holder.userAttempts = (TextView) convertView.findViewById(R.id.user_attempts);
			holder.newAttemptButton = (Button) convertView.findViewById(R.id.new_experiment_button);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		fillFields(users.get(position), holder);
		
		return convertView;
	}
	
	private void fillFields (User user, ViewHolder holder) {
		holder.userName.setText(user.getName());
		
		String userAttempts = getUserAttempts(user);
		setAttemptsVisibility(userAttempts);
	}
	
	private void setAttemptsVisibility (String userAttempts) {
		holder.userAttempts.setText(userAttempts + "/" + maxAttempts);
		
		if (!userAttempts.equals("")) {
			holder.userAttempts.setVisibility(View.VISIBLE);
			holder.newAttemptButton.setVisibility(View.GONE);
		} else {
			holder.userAttempts.setVisibility(View.GONE);
			holder.newAttemptButton.setVisibility(View.VISIBLE);
		}
	}
	
	private String getUserAttempts (User user) {
		List<Experience> experiences = user.getExperiences();
		if (experiences.size() <= 0) return "";
		if (experiences.get(experiences.size() - 1).getDone()) return "";
		Integer attempts = experiences.get(experiences.size() - 1).getAttempts().size();
		return attempts.toString();
	}
}
