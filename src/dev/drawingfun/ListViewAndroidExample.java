package dev.drawingfun;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ListViewAndroidExample extends Activity {
	ListView listView ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_view_android_example);
		
		listView = (ListView) findViewById(R.id.list);
		String[] values = new String[] {"Rectangle", "Square", "Circle", "Line", "Smooth Line", "Triangle"};

		// Define a new Adapter
		// First parameter - Context
		// Second parameter - Layout for the row
		// Third parameter - ID of the TextView to which the data is written
		// Forth - the Array of data
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);

		// Assign adapter to ListView
		listView.setAdapter(adapter); 
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			  @Override
			  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                  // ListView Clicked item index
                  int itemPosition     = position;
                  // ListView Clicked item value
                  String  itemValue    = (String) listView.getItemAtPosition(position);
                  // Show Alert 
                  Toast.makeText(getApplicationContext(), "Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG).show();
			  }
			}); 
	}
}
