/**
 * at.mkweb.android.simpleshoppinglist.SimpleShoppingList
 * 
 * LICENSE:
 *
 * This file is part of SimpleShoppingList, an Android app to create very simple shopping lists (http://android.mk-web.at/app/simpleshoppinglist.html).
 *
 * SimpleShoppingList is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 *
 * NabDroi is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with software.  If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * @author Mario Klug <mario.klug@mk-web.at>
 * @package at.mkweb.android.simpleshoppinglist
 * 
 * @license http://www.gnu.org/licenses/gpl.html
 */

package at.mkweb.android.simpleshoppinglist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SimpleShoppingList extends Activity implements OnClickListener, OnLongClickListener {
	
	LinearLayout linear;
	SQLiteDatabase db;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.main);
    	
    	((Button) findViewById(R.id.button_add)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				showAddElementDialog();
			}
		});

        loadDatabase();
        updateList();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu){

    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu, menu);
        
    	return true;
    }
    
    private void addElement(String name) {

        try {
        	db.execSQL("INSERT INTO items (id, name, active) VALUES (NULL, '" + name + "', 1);");
        } catch (Exception e) {
        	showToast(e.getMessage());
        }
        
        updateList();
    }
    
    public boolean onOptionsItemSelected (MenuItem item){

    	if(item.getItemId() ==  R.id.menu_add_item) {
    		
    		showAddElementDialog();
    	}
    	
    	if(item.getItemId() == R.id.menu_manage_lists) {
    		
    		Intent manageListsIntent = new Intent(this, ManageLists.class);
    		startActivityForResult(manageListsIntent, 0);
    	}
    	
    	if(item.getItemId() == R.id.menu_clear) {
    		
    		showListClearDialog();
    	}

    	if(item.getItemId() == R.id.menu_exit) {

    		showExitDialog();
    	}

    	return false;
    }
    
    private void showToast(String message) {
    	
    	showToast(message, Toast.LENGTH_SHORT);
    }
    
    private void showToast(String message, int duration) {
    	
    	Toast toast = Toast.makeText(this, message, duration);
    	toast.show();
    }
    
    private void updateList() {
    	
    	if(linear != null) {
    		
    		linear.removeAllViews();
    	} else {
    		
    		linear = (LinearLayout) findViewById(R.id.ContentLinearLayout);
    	}
        
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.paper);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(bmp);
        bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        
        linear.setBackgroundDrawable(bitmapDrawable);
    	
    	Cursor c = db.rawQuery("SELECT id, name, active FROM items;", null);
        
        if(c.getCount() < 1) {
        	
        	showToast(getText(R.string.err_no_items_yet).toString(), Toast.LENGTH_LONG);
        } else {
        	
        	c.moveToFirst();
        	
        	do {
	        	String name = c.getString(c.getColumnIndex("name"));
	        	String active = c.getString(c.getColumnIndex("active"));
	        	
	        	TextView text = new TextView(this);
	        	
	            
	            text.setText(name);
	            text.setId(new Integer(c.getString(c.getColumnIndex("id"))));
	            text.setTextColor(Color.BLACK);
	            text.setTextSize(26);
	            
	            text.setClickable(true);
	            text.setOnClickListener(this);
	            
	            if(active.equals("0")) {
	            
	            	text.setTextColor(Color.GRAY);
	            	text.setPaintFlags(text.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
	            	text.setOnLongClickListener(this);
	            } else {
	            	
	            	text.setPaintFlags(text.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
	            }
	            
	            linear.addView(text);
            
        	} while(c.moveToNext());
        }
    }

	@Override
	public void onClick(View v) {
			
		Cursor c = db.rawQuery("SELECT active FROM items WHERE id = " + v.getId(), null);
		
		if(c.getCount() == 1) {
		
			c.moveToFirst();
			String active = c.getString(c.getColumnIndex("active"));
			
			if(active.equals("1")) {
				db.execSQL("UPDATE items SET active = 0 WHERE id = '" + v.getId() + "';");
				
				((TextView) v).setTextColor(Color.GRAY);
				((TextView) v).setPaintFlags(((TextView) v).getPaintFlags() ^ Paint.FAKE_BOLD_TEXT_FLAG | Paint.STRIKE_THRU_TEXT_FLAG);
				((TextView) v).setOnLongClickListener(this);
			} else {
				db.execSQL("UPDATE items SET active = 1 WHERE id = '" + v.getId() + "';");
				
				((TextView) v).setTextColor(Color.BLACK);
				((TextView) v).setPaintFlags(((TextView) v).getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
				((TextView) v).setPaintFlags(((TextView) v).getPaintFlags()	^ Paint.STRIKE_THRU_TEXT_FLAG);
				((TextView) v).setOnLongClickListener(null);
			}
		}
	}

	@Override
	public boolean onLongClick(View v) {
		
		final View tv = v;
		
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setPositiveButton(getText(R.string.button_ok), new AlertDialog.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				db.execSQL("DELETE FROM items WHERE id = '" + tv.getId() + "';");
				updateList();
				dialog.cancel();
			}
		});
		b.setNegativeButton(getText(R.string.button_cancel), new AlertDialog.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				dialog.cancel();
			}
		});
		
		AlertDialog d = b.create();
		d.setTitle(getText(R.string.dialog_remove_title));
		d.setMessage(getText(R.string.dialog_remove_message));
		
		Vibrator vr = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vr.vibrate(30);
		
		d.show();
		return false;
	}
	

    public void showAddElementDialog() {
    	
    	final EditText et = new EditText(this);
		
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setView(et);
		b.setPositiveButton(getText(R.string.button_ok), new AlertDialog.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				String value = et.getText().toString().trim();
				
				if(value.length() > 0) {
					
					showToast(getText(R.string.added) + ": " + value);
					addElement(value);
				} else {
					
					showToast(getText(R.string.err_elem_not_entered).toString());
				}
				dialog.cancel();
			}
		});
		
		b.setNegativeButton(getText(R.string.button_cancel), new AlertDialog.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				dialog.cancel();
			}
		}); 
		
		AlertDialog dialog = b.create();
		
		dialog.setTitle(getText(R.string.dialog_new_item_title));
		dialog.setMessage(getText(R.string.dialog_new_item_message));
		
		dialog.show();
    }
    
    public void showListClearDialog() {
    	
    	AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setPositiveButton(getText(R.string.button_ok), new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				db.execSQL("DELETE FROM items;");
				updateList();
				dialog.cancel();
			}
		});
		b.setNegativeButton(getText(R.string.button_cancel), new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				dialog.cancel();
			}
		});
		
		AlertDialog d = b.create();
		d.setTitle(getText(R.string.dialog_clear_title));
		d.setMessage(getText(R.string.dialog_clear_message));
		
		d.show();
    }
    
    public void showExitDialog() {
    	
    	AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setPositiveButton(getText(R.string.button_ok), new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				dialog.cancel();
				System.exit(0);
			}
		});
		b.setNegativeButton(getText(R.string.button_cancel), new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				dialog.cancel();
			}
		});
		
		AlertDialog d = b.create();
		d.setTitle(getText(R.string.dialog_exit_title));
		d.setMessage(getText(R.string.dialog_exit_message));
		
		d.show();
    }
	
    private void loadDatabase() {
    	
    	db = openOrCreateDatabase("items", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS items (id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(255), active BOOLEAN);");
    }
}