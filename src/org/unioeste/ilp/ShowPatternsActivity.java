package org.unioeste.ilp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.unioeste.ilp.adapters.LockPatternGalleryAdapter;
import org.unioeste.ilp.models.Experience;
import org.unioeste.ilp.models.Pattern;
import org.unioeste.ilp.models.PatternDao.Properties;
import org.unioeste.ilp.models.User;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.Toast;

/**
 * Shows all the patterns on database.
 * 
 * If user_id specified, displays only patterns that 
 * user didn't made experiments.
 *
 * @author Lucas André de Alencar
 *
 */
@SuppressWarnings("deprecation")
public class ShowPatternsActivity extends LockPatternActivity {

	public static final String user_id = _ClassName + ".user_id";
	public static final String activity_flux = _ClassName + ".activity_flux";
	
	public static final String TEST_AUTH_FLUX = _ClassName + ".test_auth_flux";
	public static final String COLLECT_SAMPLES_FLUX = _ClassName + ".collect_samples_flux";
	
	String flux = COLLECT_SAMPLES_FLUX;
	
	User user;
	
	Gallery lockPatternGallery;
	LockPatternGalleryAdapter adapter;
	Button buttonContinue;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		havePatterns();
		getIntentExtras();
		
		setContentView(R.layout.show_pattern);
		
		setLockTextInfo(R.id.lock_text_info);
		lockPatternGallery = (Gallery) findViewById(R.id.lock_pattern_gallery);
		buttonContinue = (Button) findViewById(R.id.show_pattern_continue);
	}
	
	private void havePatterns () {
		List<Pattern> lockPatterns = ilp.patternDao.loadAll();
		if (lockPatterns.size() == 0) {
			Toast.makeText(this, R.string.no_patterns_found, Toast.LENGTH_SHORT).show();
			finish();
		}
	}
	
	private void getIntentExtras () {
		if (getIntent().getLongExtra(user_id, 0) != 0)
			user = ilp.userDao.load(getIntent().getLongExtra(user_id, 0));
		
		String flux = getIntent().getStringExtra(activity_flux);
		if (flux != null && !flux.equals(""))
			this.flux = flux;
			
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if ((user != null && flux.equals(COLLECT_SAMPLES_FLUX)) || flux.equals(TEST_AUTH_FLUX))
			lockTextInfo.setText(R.string.choose_pattern);
		else {
			lockTextInfo.setText(R.string.inserted_patterns);
			buttonContinue.setVisibility(View.GONE);
		}
		
		updateLockPatternsGallery();
	}
	
	private void updateLockPatternsGallery () {
		List<Pattern> lockPatterns = getPatterns();
		adapter = new LockPatternGalleryAdapter(this, lockPatterns);
		lockPatternGallery.setAdapter(adapter);
	}
	
	private List<Pattern> getPatterns () {
		if (user != null) {
			List<Experience> experiences = ilp.experienceDao.queryBuilder().where(org.unioeste.ilp.models.ExperienceDao.Properties.User_id.eq(user.getId())).list();
			Collection<Long> patternsUsed = extractPatterns(experiences);
			List<Pattern> patterns = ilp.patternDao.queryBuilder().where(Properties.Id.notIn(patternsUsed)).list();
			
			if (patterns.size() <= 0) {
				Toast.makeText(this, R.string.no_more_patterns_to_experiment, Toast.LENGTH_SHORT).show();
				finish();
			}
			return patterns;
		} else {
			return ilp.patternDao.loadAll();
		}
	}
	
	private Collection<Long> extractPatterns (List<Experience> experiences) {
		Collection<Long> patterns = new ArrayList<Long>();
		for (int i = 0; i < experiences.size(); i++) {
			patterns.add(experiences.get(i).getPattern_id());
		}
		return patterns;
 	}
	
	public void backButtonAction (View view) {
		finish();
	}
	
	public void confirmButtonAction (View view) {
		Pattern pattern = (Pattern) adapter.getItem(lockPatternGallery.getSelectedItemPosition());
		
		if (flux.equals(COLLECT_SAMPLES_FLUX))
			gotoCollectSamplesActivity(pattern);
		else if (flux.equals(TEST_AUTH_FLUX))
			gotoTestAuthActivity(pattern);
		else {
			Toast.makeText(this, "Problemas no fluxo da aplicação.", Toast.LENGTH_SHORT).show();
			finish();
		}
	}
	
	private void gotoTestAuthActivity(Pattern pattern) {
		Intent i = new Intent(this, TestAuthActivity.class);
		
		if (pattern != null)
			i.putExtra(TestAuthActivity.pattern_id, pattern.getId());
		
		if (user != null)
			i.putExtra(CollectSamplesActivity.user_id, user.getId());
		
		startActivity(i);
	}
	
	private void gotoCollectSamplesActivity (Pattern pattern) {
		Intent i = new Intent(this, CollectSamplesActivity.class);
		
		if (pattern != null)
			i.putExtra(CollectSamplesActivity.pattern_id, pattern.getId());
		
		if (user != null)
			i.putExtra(CollectSamplesActivity.user_id, user.getId());
		
		startActivity(i);
		finish();
	}
}
