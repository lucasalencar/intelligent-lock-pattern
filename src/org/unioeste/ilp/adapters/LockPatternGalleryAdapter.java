package org.unioeste.ilp.adapters;

import group.pals.android.lib.ui.lockpattern.widget.LockPatternUtils;
import group.pals.android.lib.ui.lockpattern.widget.LockPatternView;
import group.pals.android.lib.ui.lockpattern.widget.LockPatternView.DisplayMode;

import java.util.List;

import org.unioeste.ilp.R;
import org.unioeste.ilp.models.Pattern;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Responsible for inflating interface elements
 * on the gallery on activities that show
 * the lock pattern.
 * 
 * @author Lucas André de Alencar
 *
 */
public class LockPatternGalleryAdapter extends BaseAdapter {
	
	List<Pattern> lockPatterns;
	LayoutInflater mInflater;
	ViewHolder holder;
	
	private static class ViewHolder {
		private LockPatternView lockPattern; 
	}

	public LockPatternGalleryAdapter(Context context, List<Pattern> lockPatterns) {
		mInflater = LayoutInflater.from(context);
		this.lockPatterns = lockPatterns;
	}
	
	@Override
	public int getCount() {
		return lockPatterns.size();
	}

	@Override
	public Object getItem(int position) {
		return lockPatterns.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.gallery_pattern_item, null);
			holder = new ViewHolder();
			
			holder.lockPattern = (LockPatternView) convertView.findViewById(R.id.gallery_pattern_item);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		fillFields(lockPatterns.get(position), holder);
		
		return convertView;
	}
	
	private void fillFields (Pattern lockPattern, ViewHolder holder) {
		holder.lockPattern.setPattern(DisplayMode.Animate, LockPatternUtils.stringToPattern(lockPattern.getPattern_string()));
		holder.lockPattern.disableInput();
	}	

}
