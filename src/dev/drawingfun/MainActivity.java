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
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import java.util.UUID;
import android.util.Log;
import android.graphics.Color;
import android.support.v4.app.*;
import android.provider.MediaStore;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.widget.Toast;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Hashtable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.database.Cursor;
import android.os.AsyncTask;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import java.io.InputStream;
import java.net.URL;
import android.os.AsyncTask;

public class MainActivity extends SuperActivity implements View.OnClickListener {
    private DrawingView drawView;
    private ImageButton currPaint, drawBtn, eraseBtn, newBtn, openBtn, saveBtn, colorBtn, undoBtn, redoBtn, shapeBtn;
    private ImageButton fillBtn, recteraseBtn;
    private float smallBrush, mediumBrush, largeBrush;
    private static int mColor = 0; // mColor, colorBtn
    private static String mShape;
    public static String FILEPATH = "filePath";
    public static String FILENAME = "fileName";
    private int selectedItem = 3;  // smoothline
    ImageView imgView;
    Bitmap bitmap;
    ProgressDialog pDialog;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // drawview
        drawView = (DrawingView)findViewById(R.id.drawing);
        drawBtn = (ImageButton)findViewById(R.id.draw_btn);
        drawBtn.setOnClickListener(this);
        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);
        drawView.setBrushSize(mediumBrush);

        eraseBtn = (ImageButton)findViewById(R.id.erase_btn);
        eraseBtn.setOnClickListener(this);
        newBtn = (ImageButton)findViewById(R.id.new_btn);
        newBtn.setOnClickListener(this);
        saveBtn = (ImageButton)findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);

        recteraseBtn = (ImageButton)findViewById(R.id.fill_btn);
        recteraseBtn.setOnClickListener(this);
        fillBtn = (ImageButton)findViewById(R.id.fill_btn);
        fillBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setmCurrentShape(6);
                }
            });

        openBtn = (ImageButton)findViewById(R.id.open_btn);  // not fully what I want
        imgView = (ImageView)findViewById(R.id.img_view);  // img_view
        openBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    // TODO auto-generated method stub
                    new LoadImage().execute("http://www.learn2crack.com/wp-content/uploads/2014/04/node-cover-720x340.png");  //working
                }
            });

        // for colorBtn
        colorBtn = (ImageButton)findViewById(R.id.color_btn);
        colorBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {                
                    showColorPicker();
                }
            });
        // when activity is re-created, we need to set OnAmbilWarnaListen
        if (savedInstanceState != null) {
            AmbilWarnaDialogFragment fragment = (AmbilWarnaDialogFragment)getSupportFragmentManager().findFragmentByTag("color_picker_dialog");
            if (fragment != null) {
                fragment.setOnAmbilWarnaListener(this);
            }
        }
        
        // for undoBtn
        undoBtn = (ImageButton)findViewById(R.id.undo_btn);
        undoBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {                
                    drawView.onClickUndo();
                }
            });
        // for redoBtn
        redoBtn = (ImageButton)findViewById(R.id.redo_btn);
        redoBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {                
                    drawView.onClickRedo();
                }
            });

        // for shapeBtn
        shapeBtn = (ImageButton)findViewById(R.id.shape_btn);
        shapeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> values = GetCity();
                showSudokuListDialog(values);
            }
        });
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
            protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Loading image ....");
            pDialog.show();
        }
        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }
        protected void onPostExecute(Bitmap image) {
            if (image != null) {
                imgView.setImageBitmap(image);
                pDialog.dismiss();
            } else {
                pDialog.dismiss();
                Toast.makeText(MainActivity.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.draw_btn) {
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle("Brush size:");
            brushDialog.setContentView(R.layout.brush_chooser);

            // for small brush size
            ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        drawView.setBrushSize(smallBrush);
                        drawView.setLastBrushSize(smallBrush);
                        brushDialog.dismiss();
                    }
                });
            // for medium brush size
            ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        drawView.setBrushSize(mediumBrush);
                        drawView.setLastBrushSize(mediumBrush);
                        brushDialog.dismiss();
                    }
                });
            // for large brush size
            ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        drawView.setBrushSize(largeBrush);
                        drawView.setLastBrushSize(largeBrush);
                        brushDialog.dismiss();
                    }
                });
            drawView.setErase(false);
            drawView.setColor(mColor);
            brushDialog.show();
        } else if (view.getId() == R.id.erase_btn) {
            // respond to clicks -- for erase button
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle("Erase size:");
            brushDialog.setContentView(R.layout.brush_chooser);

            // for three different size buttons
            ImageButton smallBtn = (ImageButton) brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawView.setErase(true);
                        drawView.setBrushSize(smallBrush);
                        brushDialog.dismiss();
                    }
                });
            ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        drawView.setErase(true);
                        drawView.setBrushSize(mediumBrush);
                        brushDialog.dismiss();
                    }
                });
            ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        drawView.setErase(true);
                        drawView.setBrushSize(largeBrush);
                        brushDialog.dismiss();
                    }
                });
            brushDialog.show();
        } else if (view.getId() == R.id.new_btn) {
            // new button
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("New drawing");
            newDialog.setMessage("Start new drawing (you will lose the current drawing)?");
            newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        //drawView.startNew();
                        drawView.clear();
                        dialog.dismiss();
                    }
                });
            newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
            newDialog.show();
        } else if (view.getId() == R.id.save_btn) {
            drawView.setDrawingCacheEnabled(true);
            // save drawing
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle("Save drawing");
            saveDialog.setMessage("Save drawing to device Gallery?");
            saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        // save drawing
                    }
                });
            saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
            saveDialog.show();

            // attemp to write the image to a file, it is in the "Gallery" already
            String imgSaved = MediaStore.Images.Media.insertImage(getContentResolver(), drawView.getDrawingCache(), UUID.randomUUID().toString()+".png", "drawing");
            // give user feedback
            if (imgSaved != null){
                Toast savedToast = Toast.makeText(getApplicationContext(), "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
                savedToast.show();
            } else {
                Toast unsavedToast = Toast.makeText(getApplicationContext(), "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
                unsavedToast.show();
            }
            // destroy the drawing cache so that any future drawings saved won't use the existing cache
            drawView.destroyDrawingCache();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCancel(AmbilWarnaDialogFragment dialogFragment)  {
        Log.d("TAG", "onCancel()");
    }
    
    @Override
    public void onOk(AmbilWarnaDialogFragment dialogFragment, int color)  {
        Log.d("TAG", "onOk(). Color: " + color);

        drawView.setErase(false);
        drawView.setBrushSize(drawView.getLastBrushSize());
        if (color != mColor) {
            // update color
            //MainActivity.this.mColor = color;
            MainActivity.this.mColor = color;
            drawView.setColor(mColor);
        }
    }

    // for shapeBtn
    public void showSudokuListDialog(ArrayList<String> values) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater layout = LayoutInflater.from(MainActivity.this);
        View  sudokulistView = layout.inflate(R.layout.sudokulist, null);
        final ListView listView = (ListView) sudokulistView.findViewById(R.id.lvSudokuItems);  // change with listView
        builder.setView(sudokulistView);
        builder.setCancelable(false);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);
		listView.setAdapter(adapter);
        
        // set list click event
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @SuppressWarnings("unchecked")
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    selectedItem = position;
                    drawView.setmCurrentShape(selectedItem);
                    //String strPreview = (String)adapter.getItem(position);
                }
            });

        // set list touch event
        listView.setOnTouchListener(new AdapterView.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });

        // set open button getResources().getString(R.string.dialogOpenBtnOK)
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                        field.setAccessible(true);
                        field.setBoolean(dialog, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (selectedItem == -1) {
                        return;
                    } else {
                        try {
                            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                            field.setAccessible(true);
                            field.setBoolean(dialog, true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                }
            });
        
        // set Cancel button
        // getResources().getString(R.string.dialogOpenBtnCancel)
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                        field.setAccessible(true);
                        field.setBoolean(dialog, true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                }
            });
        builder.create().show();
    }

    private ArrayList<String> GetCity() {
        ArrayList<String> values = new ArrayList<String>();
        values.add("Rectangle");
        values.add("Square");
        values.add("Circle");
        values.add("Straight Line");
        values.add("Smooth Line");
        values.add("Triangle");
        //values.add("FloodFill");
        return values;
    }

    // show Color Picker dialog fragment. If color wasn't set previously, set BLUE by default
    public void showColorPicker() {
        int thisColor = super.mColor == 0 ? Color.BLUE : mColor;
        // create new instance of AmbilWarnaDialogFragment and set OnAmbilWarnaListener to it
        // show dialog fragment with some tag value
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        AmbilWarnaDialogFragment fragment = AmbilWarnaDialogFragment.newInstance(thisColor, android.R.style.Theme_Dialog);
        fragment.setOnAmbilWarnaListener(this);
        fragment.show(ft, "color_picker_dialog");
    }
}
