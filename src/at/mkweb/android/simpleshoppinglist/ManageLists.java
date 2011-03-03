package at.mkweb.android.simpleshoppinglist;

import android.app.Activity;
import android.widget.EditText;
import android.widget.HorizontalScrollView;

public class ManageLists extends Activity {

	public void onCreate() {
		
		HorizontalScrollView view = new HorizontalScrollView(this);
		view.addView(new EditText(this));
		
		setContentView(view);
	}
}
