package com.example.vordisplay;

import java.util.Random;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener{
	
	private ImageView RotateVor;
	private ImageView To;
	private ImageView From;
	private ImageView Arrow;
	private Bitmap dialBitmap;
	private Bitmap arrowBitmap;
	private Matrix dialMatrix;
	private Matrix arrowMatrix;
	private Button vorButton;
	private Button radioButton;
    private int angle;
    private Handler mHandler;
    private TextView Radial;
    private TextView SpeedDisp;
    private int Rand;
    private String Morse;
    private int StationRadial;
    private AlertDialog.Builder radioDialog;
    private float mLastX, mLastY, mLastZ;
    private boolean mInitialized; 
    private SensorManager mSensorManager; 
    private Sensor mAccelerometer; 
    private final float NOISE = (float) 2.0;
    private int SensorAngle;
    private int ArrowAngle;
    private SeekBar SetSpeed;
    private int speed;
    private MediaPlayer mp;
    private int PlaneMoving;
    private long Time, Time1,Time2;
    private long Traveled, Distance,PrevDist,DistChange;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.vor_main);
		
		//initialize variables
		RotateVor = (ImageView) findViewById(R.id.rotatingVOR);
		To = (ImageView) findViewById(R.id.toVOR);
		From = (ImageView) findViewById(R.id.fromVOR);
		Arrow = (ImageView) findViewById(R.id.arrow);
		Radial = (TextView) findViewById(R.id.radialDisp);
		vorButton = (Button) findViewById(R.id.OBS);
		angle = 0;
		ArrowAngle = 0;
		speed = 0;
		Traveled = 0;
		radioButton = (Button) findViewById(R.id.radio);
		SetSpeed = (SeekBar) findViewById(R.id.speed);
	    mp = MediaPlayer.create(getApplicationContext(), R.raw.jetpass);
		
	    mp.setLooping(true);
	    
	    
	    //initialize speed control variables
		SetSpeed.setMax(300);
		SetSpeed.setProgress(50);
		
		//initialize accelerometer variables
		mInitialized = false;
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		
		//create radio station parameters
		RadioStation radio = new RadioStation();
		Morse = radio.CreateMorse();
		StationRadial = radio.CreateRadial();
		Distance = radio.CreateDistance();
		
		//display radial from OBS
		Radial.setText((360 - angle) + "  " + ":" + "  " + SensorAngle + "\n" + speed + "mph "+ "\n" + Distance*.0006 + "mi");
        Radial.setTextColor(0xff00ff00);
		Radial.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
		Radial.setTextSize(TypedValue.COMPLEX_UNIT_PX, 80);
		Radial.setBackgroundColor(0xff000000);
		
		
		//initialize bitmap and matrix for rotation
		dialBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dial4);
		arrowBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.arrow);
		
		dialMatrix = new Matrix();
		arrowMatrix = new Matrix();
		dialMatrix = RotateVor.getImageMatrix();
		arrowMatrix = Arrow.getImageMatrix();
		rotateDial(0);
		
		//call buttons
		setOBSButton();
		
		//set To arrow
		setTo();
	}
	
	//this function rotates the the image when called
	public void rotateDial(int amount){

		angle++;
		dialMatrix.postRotate(amount);
		
		Bitmap rotated = Bitmap.createBitmap(dialBitmap, 0, 0, dialBitmap.getWidth(), dialBitmap.getHeight(),
		        dialMatrix, true);

		RotateVor.setImageBitmap(rotated);
	}
	
    //This function will rotate the image when the OBS button is pressed or held down
	public void setOBSButton(){
 
		vorButton.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				
				switch(event.getAction()) {
		        case MotionEvent.ACTION_DOWN:
		            if (mHandler != null) return true;
		            
		            //update the radial display
		    		Radial.setText((360 - angle) + "  " + ":" + "  " + SensorAngle + "\n" + speed + "mph "+ "\n" + Distance*.0006 + "mi");
		            Radial.setTextColor(0xff00ff00);
		    		Radial.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
		    		Radial.setTextSize(TypedValue.COMPLEX_UNIT_PX, 80);
		    		Radial.setBackgroundColor(0xff000000);
		    		
		            mHandler = new Handler();
		            mHandler.postDelayed(mAction, 1);
		            break;
		        case MotionEvent.ACTION_UP:
		            if (mHandler == null) return true;
		            
		            //update the radial display
		    		Radial.setText((360 - angle) + "  " + ":" + "  " + SensorAngle + "\n" + speed + "mph "+ "\n" + Distance*.0006 + "mi");
		            Radial.setTextColor(0xff00ff00);
		    		Radial.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
		    		Radial.setTextSize(TypedValue.COMPLEX_UNIT_PX, 80);
		    		Radial.setBackgroundColor(0xff000000);

		            mHandler.removeCallbacks(mAction);
		            mHandler = null;
		            break;
		        }
				return false;
			} 
			Runnable mAction = new Runnable() {
		        @Override public void run() {
		        	rotateDial(1);
		            mHandler.postDelayed(this, 1);
		        }
		    }; 
		}); 
		
		radioButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DisplayRadio();
			}
		});
		
		SetSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				Time1 = (System.currentTimeMillis()/1000);
				Time = Time1 - Time2;
				Time2 = Time1;
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				speed = progress;
				Radial.setText((360 - angle) + "  " + ":" + "  " + SensorAngle + "\n" + speed + "mph "+ "\n" + Distance*.0006 + "mi");
				if(speed <= 0) mp.stop();
				else mp.start();
			}
		});
		
		
	}
	
	public void DisplayRadio(){
		
		radioDialog = new AlertDialog.Builder(this);
		radioDialog.setTitle("             Radio Station");
		radioDialog.setMessage("Radio ID: " + Morse + "\n" 
				+ "Radial: " + StationRadial + "\n" + "Distance: "
				+ Distance);
		
		radioDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}
		}); 
		AlertDialog radioD = radioDialog.create();
		radioD.show(); 
	}
	
	//flips display to show "To" arrow
	public void setTo(){
		From.setVisibility(View.INVISIBLE);
		To.setVisibility(View.VISIBLE);
		Arrow.setVisibility(View.VISIBLE);
		RotateVor.setVisibility(View.VISIBLE);
	}
	
	//flips display to show "From" arrow
	public void setFrom(){
		To.setVisibility(View.INVISIBLE);
		From.setVisibility(View.VISIBLE);
		Arrow.setVisibility(View.VISIBLE);
		RotateVor.setVisibility(View.VISIBLE);
	}
	
	//rotates arrow 2 degrees left
	public void rotateArrowLeft(int amount){
		
		arrowMatrix.postRotate(amount);
		
		Bitmap rotated = Bitmap.createBitmap(arrowBitmap, 0, 0, arrowBitmap.getWidth(), arrowBitmap.getHeight(),
		        arrowMatrix, true);

		Arrow.setImageBitmap(rotated);
	}
	
	//rotates arrow 2 degrees right
	public void rotateArrowRight(int amount){
		
		arrowMatrix.postRotate(-amount);
		
		Bitmap rotated = Bitmap.createBitmap(arrowBitmap, 0, 0, arrowBitmap.getWidth(), arrowBitmap.getHeight(),
		        arrowMatrix, true);

		Arrow.setImageBitmap(rotated);
	}

	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	}

	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	//used to access accelerometer information to show the position of the phone
	@Override
	public void onSensorChanged(SensorEvent event) {
	
	float x = event.values[0];
	float y = event.values[1];
	float z = event.values[2];
	Calculations obj = new Calculations();
	
	
	if (!mInitialized) {
		mLastX = x;
		mLastY = y;
		mLastZ = z;
		mInitialized = true;
		
	} else {
		float deltaX = Math.abs(mLastX - x);
		float deltaY = Math.abs(mLastY - y);
		float deltaZ = Math.abs(mLastZ - z);
		
		if (deltaX < NOISE) deltaX = (float)0.0;
		if (deltaY < NOISE) deltaY = (float)0.0;
		if (deltaZ < NOISE) deltaZ = (float)0.0;
		
		
		mLastX = x;
		mLastY = y;
		mLastZ = z;
		
		if(x < 0 && y < 0){
			x = Math.abs(x);
			y = Math.abs(y);
			SensorAngle = (int) obj.GetAngle(x, y);
			SensorAngle =  180 + SensorAngle;
		}
		else if(x < 0 && y > 0){
			x = Math.abs(x);
			SensorAngle = (int) obj.GetAngle(x, y);
			SensorAngle = 180 - SensorAngle;
		}else if (y < 0 && x > 0){
			y = Math.abs(y);
			SensorAngle = (int) obj.GetAngle(x, y);
			SensorAngle =  360 - SensorAngle;
		}
		else{
			SensorAngle = (int) obj.GetAngle(x, y);
		}
	}
	
		if(SensorAngle < (360 - angle) && ArrowAngle < 25){
			ArrowAngle++;
			rotateArrowRight(1);
		}
		else if(SensorAngle > (360 - angle) && ArrowAngle > -25){
			ArrowAngle--;
			rotateArrowLeft(1);
		}
		
		if(speed > 0){
			Time1 = (System.currentTimeMillis()/1000);
			Time = Time1 - Time2;
			Time2 = Time1;
			PrevDist = Distance;
			Distance  = obj.LawOfCos(Time*speed, Distance, 360 - angle - SensorAngle) ;
			DistChange = Distance - PrevDist;
			if(DistChange > 0){
				setFrom();
			}
			else if(DistChange < 0)
				setTo();
			
			if(Distance*0.0006 < .5 || (360 - angle - SensorAngle > 85 && 360 - angle - SensorAngle < 95) || 
					(306 - angle - SensorAngle > 270 && 360 - angle - SensorAngle < 275) || 
					(360 - angle - SensorAngle < -85 && 360 - angle - SensorAngle > -95) ||
					(360 - angle - SensorAngle < -270 && 360 - angle - SensorAngle > -275)){
				Radial.setText("Signal Lost");
	            Radial.setTextColor(0xff000000);
	    		Radial.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
	    		Radial.setTextSize(TypedValue.COMPLEX_UNIT_PX, 80);
	    		Radial.setBackgroundColor(0xffff0000);
			}
			else{
				Radial.setText((360 - angle) + "  " + ":" + "  " + SensorAngle + "\n" + speed + "mph "+ "\n" + Distance*.0006 + "mi");
		        Radial.setTextColor(0xff00ff00);
				Radial.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
				Radial.setTextSize(TypedValue.COMPLEX_UNIT_PX, 80);
				Radial.setBackgroundColor(0xff000000);
			}
		}
		
		
	}
}
