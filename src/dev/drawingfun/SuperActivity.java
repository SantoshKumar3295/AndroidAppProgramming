package dev.drawingfun;

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

// this superclass subclass problem got solved the other way around
//public class SuperActivity extends FragmentActivity implements OnAmbilWarnaListener, View.OnClickListener {
public class SuperActivity extends FragmentActivity implements OnAmbilWarnaListener {
    //public ImageButton colorBtn;  // I may not need this one here
    
    public static int mColor = 0; // mColor, colorBtn
    @Override
    public void onCancel(AmbilWarnaDialogFragment dialogFragment)  {
        Log.d("TAG", "onCancel()");
    }

    @Override
    public void onOk(AmbilWarnaDialogFragment dialogFragment, int color)  {
        Log.d("TAG", "onOk(). Color: " + color);

        //drawView.setErase(false);
        //drawView.setBrushSize(drawView.getLastBrushSize());

        // uer chosen color
        if (color != mColor) {
            // update color
            //MainActivity.this.mColor = color;
            SuperActivity.this.mColor = color;
            //drawView.setColor(mColor);
        }
    }
}
