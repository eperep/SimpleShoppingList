package at.mkweb.android.simpleshoppinglist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Item extends LinearLayout implements OnClickListener, OnLongClickListener {

	private Context context;
	SQLiteDatabase db;
	
	TextView textView;
	
	String name;
	boolean active;
	
	public Item(Context context) {
		super(context);
		
		this.context = context;
		this.db = ((SQLiteDatabase) Registry.get(Registry.DATABASE));

		setOrientation(LinearLayout.HORIZONTAL);
	    
	    setClickable(true);
	    setOnLongClickListener(this);
	    
	    setOnClickListener(this);
	}

	public void setName(String name) {
		
		this.name = name;
	}
	
	public void setActive(boolean active) {
		
		this.active = active;
	}
	
	public void create() {
	
		removeAllViews();
		
		addViews();
	}
	
	private void addViews() {
		
		textView = new TextView(context);
	    
		textView.setText(name);
		textView.setId(1000000 + getId());
		textView.setTextColor(Color.BLACK);
		textView.setTextSize(26);
	    //textView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
	    
	    if(active) {
	    
	    	textView.setPaintFlags(textView.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
	    } else {
	    	
	    	textView.setTextColor(Color.GRAY);
	    	textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
	    	textView.setPadding(4, 0, 0, 0);
	    	
	    	Button removeButton = new Button(context);
	    	removeButton.setText("x");
	    	removeButton.setTextSize(8);
	    	removeButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_add));
	    	removeButton.setPaintFlags(removeButton.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
	    	removeButton.setWidth(40);
	    	removeButton.setWidth(40);
	    	removeButton.setOnClickListener(this);
			
			addView(removeButton);
	    }
	    
	    addView(textView);
	}

	@Override
	public void onClick(View v) {
		
		if(v.getClass() == Button.class) {
			
			showRemoveDialog();
		} else {
		
			removeAllViews();
			
			if(active) {
				db.execSQL("UPDATE items SET active = 0 WHERE id = '" + getId() + "';");
				active = false;
				addViews();
			} else {
				db.execSQL("UPDATE items SET active = 1 WHERE id = '" + getId() + "';");
				active = true;
				addViews();
			}
		}
	}
	
	private void showRemoveDialog() {
		
		AlertDialog.Builder b = new AlertDialog.Builder(context);
		b.setPositiveButton(((SimpleShoppingList) context).getText(R.string.button_ok), new AlertDialog.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				db.execSQL("DELETE FROM items WHERE id = '" + getId() + "';");
				((SimpleShoppingList) context).updateList();
				dialog.cancel();
			}
		});
		b.setNegativeButton(((SimpleShoppingList) context).getText(R.string.button_cancel), new AlertDialog.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				dialog.cancel();
			}
		});
		
		AlertDialog d = b.create();
		d.setTitle(((SimpleShoppingList) context).getText(R.string.dialog_remove_title));
		d.setMessage(((SimpleShoppingList) context).getText(R.string.dialog_remove_message));
		
		d.show();
	}

	@Override
	public boolean onLongClick(View v) {
		
		if(v.getClass() == getClass()) {
			
			showRemoveDialog();
			
			Vibrator vr = (Vibrator) ((SimpleShoppingList) context).getSystemService(Context.VIBRATOR_SERVICE);
			vr.vibrate(30);
		}
		return true;
	}
}
