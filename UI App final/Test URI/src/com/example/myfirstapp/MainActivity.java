package com.example.myfirstapp;

import java.io.File;
import java.io.IOException;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.aviary.android.feather.Constants;
import com.aviary.android.feather.FeatherActivity;
import com.aviary.android.feather.library.utils.StringUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
//import android.graphics.drawable.AnimationDrawable;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
//import android.view.Menu;
import android.speech.RecognizerIntent;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
//import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myfirstapp.AlbumStorageDirFactory;
import com.example.myfirstapp.BaseAlbumDirFactory;
import com.example.myfirstapp.FroyoAlbumDirFactory;

import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.example.myfirstapp.AlbumStorageDirFactory;
import com.example.myfirstapp.BaseAlbumDirFactory;
import com.example.myfirstapp.FroyoAlbumDirFactory;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView; 


public class MainActivity extends Activity {
	private static final int SELECT_PICTURE = 1;
	private static final int ACTION_TAKE_PHOTO_B = 1;
	private static final int ACTION_CAMERA_SPEAK = 2;
	private String selectedImagePath;
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
	private static final int ACTION_REQUEST_FEATHER = 100;
	
	/** Folder name on the sdcard where the images will be saved **/
	private static final String FOLDER_NAME = "picfix";
	
	/** apikey is required http://developers.aviary.com/ */
	private static final String API_KEY = "xxxxx";
	
	// variables needed to access the image gallery
	public static Uri[] mUrls = null;
	private static String[] strUrls = null;
	private String[] mNames = null;
	private Cursor cc = null;
	private ProgressDialog myProgressDialog = null;
	private GridView gridview = null;
	private int currentView = 2;
	private File mGalleryFolder;
	private boolean timeToUpdate = false;
	String mOutputFilePath;
	private static Button buttonImageCapture = null;
	private static Button buttonSpeak = null;
	private static Button buttonSetting = null;
	//private static Button buttonView = null;
	//private static ImageView paddingImage = null;
	private String mCurrentPhotoPath;
	private ImageView mImageView;
	private Bitmap mImageBitmap;

	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
	
	//private Button btnMoreInfo = null;
	//private GridView gridview = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);
	
		mGalleryFolder = createFolders();
	    
	    // BUTTON ---------------------------------------------------------------------
        // Button enter the screen - animate
        buttonImageCapture = (Button) findViewById(R.id.Button1);
        Animation testAnim = AnimationUtils.loadAnimation(this, R.anim.push_down);
        buttonImageCapture.startAnimation(testAnim);
        
        //buttonImage = (Button) findViewById(R.id.Button2);
        //testAnim = AnimationUtils.loadAnimation(this, R.anim.push_down);
        //buttonImage.startAnimation(testAnim);
        
        //buttonImage = (Button) findViewById(R.id.Button3);
        //testAnim = AnimationUtils.loadAnimation(this, R.anim.push_down);
        //buttonImage.startAnimation(testAnim);
        
        buttonSetting = (Button) findViewById(R.id.Button4);
        testAnim = AnimationUtils.loadAnimation(this, R.anim.push_down);
        buttonSetting.startAnimation(testAnim);
        
        buttonSpeak = (Button) findViewById(R.id.btSpeak);
        testAnim = AnimationUtils.loadAnimation(this, R.anim.push_down);
        buttonSpeak.startAnimation(testAnim);
        
        //buttonView = (Button) findViewById(R.id.Button2);
        //paddingImage = (ImageView) findViewById(R.id.imageView1);
        
	    // BUTTON ---------------------------------------------------------------------

	    // GRID VIEW ------------------------------------------------------------------
	    cc = this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);

	    // File[] files=f.listFiles();
	    if (cc != null) {
	        myProgressDialog = new ProgressDialog(MainActivity.this);
	        myProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	        myProgressDialog.setMessage(getResources().getString(R.string.pls_wait_txt));
	        myProgressDialog.show();
	        //myProgressDialog.setIcon(R.drawable.blind);

	        new Thread() {
	            public void run() {
	                try {
	                    cc.moveToFirst();
	                    int imageLoad = cc.getCount() - 1;
	                    mUrls = new Uri[cc.getCount()];
	                    strUrls = new String[cc.getCount()];
	                    mNames = new String[cc.getCount()];
	                    for (int i = 0; i < cc.getCount(); i++){
	                        cc.moveToPosition(imageLoad);
	                        mUrls[i] = Uri.parse(cc.getString(1));
	                        strUrls[i] = cc.getString(1);
	                        mNames[i] = cc.getString(3);
	                    	imageLoad--;
	                        //Log.e("mNames[i]",mNames[i]+":"+cc.getColumnCount()+ " : " +cc.getString(3));
	                    }

	                } catch (Exception e) {
	                	
	                }
	                
	                myProgressDialog.dismiss();
	            }
	        }.start();
	    
	        // create a gridView to display images
	        gridview = (GridView) findViewById(R.id.grid);
	        gridview.setAdapter(new ImageAdapter(this));
	        
	        // handing the implicit intent (file from facebook)
	        Uri fromFacebook = null;
	        Bundle status = getIntent().getExtras();
	        if(status != null){
	        	fromFacebook = ((Uri) getIntent().getExtras().get(Intent.EXTRA_STREAM));
	        	startFeather(fromFacebook);
	        } 
	        
	        // display the image on EditActivity when selected
	        gridview.setOnItemClickListener(new OnItemClickListener() {
	            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            	startFeather(mUrls[position]);
	        	//****//	Intent mInDisplay = new Intent(MainActivity.this, EditActivity.class);
	        	//****//    mInDisplay.putExtra("Index", position);
	        	//****//    startActivity(mInDisplay);
	        	}
	        });
	        
	       
	        
	        
	    }
	    // GRID VIEW ------------------------------------------------------------------
	    
	    /*
	    // NOTE - Gallery view for Gallery button
	    ((Button) findViewById(R.id.Button2)).setOnClickListener(new OnClickListener() {
	    	public void onClick(View arg0) {
	            // in onCreate or any event where your want the user to
	            // select a file
	            Intent intent = new Intent();
	            intent.setType("image/*");
	            intent.setAction(Intent.ACTION_GET_CONTENT);
	            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
	    	}
	    });
	   	*/
	}
    
    /** Called when the user clicks the Gallery button */
    public void changeView(View view) {
    	if(currentView == 2){
    		gridview.setNumColumns(1);
    		gridview.setColumnWidth(180);
    		gridview.setAdapter(new LargeImageAdapter(this));

    		//ImageButton ib = (ImageButton) findViewById(R.id.imageButton);
    		//int i1Btn = R.drawable.two_col_white;
    		//ib.setImageResource(i1Btn);
    		currentView = 1;
    	}else{
    		gridview.setNumColumns(2);
    		gridview.setColumnWidth(90);
    		gridview.setAdapter(new ImageAdapter(this));
    	
    		//ImageButton ib = (ImageButton) findViewById(R.id.imageButton);
    		//int i1Btn = R.drawable.single_col_white;
    		//ib.setImageResource(i1Btn);
    		currentView = 2;
    	}
    	//Intent intent = new Intent(this, GalleryActivity.class);
    	//startActivity(intent);
    }
    

    /* Photo album for this application */
	private String getAlbumName() {
		return getString(R.string.album_name);
	}

    private File getAlbumDir() {
		File storageDir = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			
			storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

			if (storageDir != null) {
				if (! storageDir.mkdirs()) {
					if (! storageDir.exists()){
						Log.d("CameraSample", "failed to create directory");
						return null;
					}
				}
			}
			
		} else {
			Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
		}
		
		return storageDir;
	}

    private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
		File albumF = getAlbumDir();
		File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
		return imageF;
	}

    private File setUpPhotoFile() throws IOException {
		
		File f = createImageFile();
		mCurrentPhotoPath = f.getAbsolutePath();
		
		return f;
	}

	private void setPic() {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
		int targetW = mImageView.getWidth();
		int targetH = mImageView.getHeight();

		/* Get the size of the image */
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;
		
		/* Figure out which way needs to be reduced less */
		int scaleFactor = 1;
		if ((targetW > 0) || (targetH > 0)) {
			scaleFactor = Math.min(photoW/targetW, photoH/targetH);	
		}

		/* Set bitmap options to scale the image decode target */
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
		Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		
		/* Associate the Bitmap to the ImageView */
		mImageView.setImageBitmap(bitmap);
		mImageView.setVisibility(View.VISIBLE);
	}

	private void galleryAddPic() {
		    Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
			File f = new File(mCurrentPhotoPath);
		    Uri contentUri = Uri.fromFile(f);
		    mediaScanIntent.setData(contentUri);
		    this.sendBroadcast(mediaScanIntent);
	}

	

	private void handleBigCameraPhoto() {

		if (mCurrentPhotoPath != null) {
			setPic();
			galleryAddPic();
			mCurrentPhotoPath = null;
		}

	}

    private void dispatchTakePictureIntent(int actionCode) {

		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		switch(actionCode) {
		case ACTION_TAKE_PHOTO_B:
			File f = null;
			
			try {
				f = setUpPhotoFile();
				mCurrentPhotoPath = f.getAbsolutePath();
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
			} catch (IOException e) {
				e.printStackTrace();
				f = null;
				mCurrentPhotoPath = null;
			}
			break;

		default:
			break;			
		} // switch

		startActivityForResult(takePictureIntent, actionCode);
	}
    
 
    Button.OnClickListener mTakePicOnClickListener = 
    		new Button.OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
    		}
    	};
    
    	public static boolean isIntentAvailable(Context context, String action) {
    		final PackageManager packageManager = context.getPackageManager();
    		final Intent intent = new Intent(action);
    		List<ResolveInfo> list =
    			packageManager.queryIntentActivities(intent,
    					PackageManager.MATCH_DEFAULT_ONLY);
    		return list.size() > 0;
    	}
    	private void setBtnListenerOrDisable( 
    			Button btn, 
    			Button.OnClickListener onClickListener,
    			String intentName
    	) {
    		if (isIntentAvailable(this, intentName)) {
    			Log.d("if","if part of button Listener");
    			btn.setOnClickListener(onClickListener);        	
    		} else {
    			Log.d("else","else part of button Listener");
    			btn.setText( 
    				getText(R.string.cannot).toString() + " " + btn.getText());
    			btn.setClickable(false);
    		}
    	}	
    /** Called when the user clicks the Capture button */
    public void captureIsPressed(View view) {
        //gridview.setNumColumns(1);
    	//Button picBtn = (Button) findViewById(R.id.Button1);
    	
   	 	Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
   	 	startActivityForResult( cameraIntent, ACTION_CAMERA_SPEAK );
    	
   	 	/*
    	Log.d("beforecapturePressed","before main function part of button Pressed");
		setBtnListenerOrDisable( 
				buttonImageCapture, 
				mTakePicOnClickListener,
				MediaStore.ACTION_IMAGE_CAPTURE
		);
		
		Log.d("aftercapturePressed","after main function part of button Pressed");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
		} else {
			mAlbumStorageDirFactory = new BaseAlbumDirFactory();
		}
		*/
    }
    
    
    
    /** Called when the user clicks the Refresh button */
    public void refreshIsPressed(View view) {
    	myRefreshGrid();
    }
    
    /** Called when the user clicks the Speak button */
    public void speak(View view) {
    	  Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    	 
    	  // Specify the calling package to identify your application
    	  intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
    	    .getPackage().getName());
    	 
    	  //Start the Voice recognizer activity for the result.
    	  startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    	 }
    
    
    /*
    // Called when the user clicks the Gallery button
    public void galleryIsPressed(View view) {
        //gridview.setNumColumns(1);
    	//Intent intent = new Intent(this, GalleryActivity.class);
    	//startActivity(intent);
    }
    
    // Called when the user clicks the Facebook button
    public void facebookIsPressed(View view) {
        //gridview.setNumColumns(2);
    	Intent intent = new Intent(this, SettingActivity.class);
    	startActivity(intent);
    }
    */
    
    /** Called when the user clicks the Setting button 
     *  Not yet implemented - go to facebook activity !!*/
    public void settingIsPressed(View view) {
    	// hiding all the current button
    	/*
    	buttonImageCapture.setVisibility(View.INVISIBLE);
    	buttonSetting.setVisibility(View.INVISIBLE);
    	buttonSpeak.setVisibility(View.INVISIBLE);
    	paddingImage.setVisibility(View.GONE);
    	
        Animation testAnim = AnimationUtils.loadAnimation(this, R.anim.push_down);
        buttonView.startAnimation(testAnim);
    	buttonView.setVisibility(View.VISIBLE);
    	*/
    	
    	/*
        Animation testAnim = AnimationUtils.loadAnimation(this, R.anim.push_down);
        buttonImageCapture.startAnimation(testAnim);
        
        testAnim = AnimationUtils.loadAnimation(this, R.anim.push_down);
        buttonSetting.startAnimation(testAnim);
        
        testAnim = AnimationUtils.loadAnimation(this, R.anim.push_down);
        buttonSpeak.startAnimation(testAnim);
        */
    	
        //gridview.setNumColumns(2);
    	
    	// go to setting activity
    	//Intent intent = new Intent(this, SettingActivity.class);
    	//startActivity(intent);
    }
    
    public class MyCustomGridView extends GridView {
    	public MyCustomGridView(Context context, AttributeSet attrs) {
    	    super(context, attrs);
    	}

    	@Override
    	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	    super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    	    int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
    	    int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
    	    this.setMeasuredDimension(parentWidth / 2, parentHeight);
    	}
    	
    	/*
    	@Override
    	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	    int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
    	    int myWidth = (int) (parentHeight * 0.5);
    	    super.onMeasure(MeasureSpec.makeMeasureSpec(myWidth, MeasureSpec.EXACTLY), heightMeasureSpec);
    	}
    	*/
    }  
    
    public class ImageAdapter extends BaseAdapter {
        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return cc.getCount();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

/*
        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.activity_main, null);

            try {

                ImageView imageView = (ImageView) v.findViewById(R.id.ImageView01);
                //imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                // imageView.setPadding(8, 8, 8, 8);
                Bitmap bmp = decodeURI(mUrls[position].getPath());
                //BitmapFactory.decodeFile(mUrls[position].getPath());
                imageView.setImageBitmap(bmp);
                //bmp.
                TextView txtName = (TextView) v.findViewById(R.id.TextView01);
                txtName.setText(mNames[position]);
            } catch (Exception e) {

            }
            return v;
        }
*/
    	
    	
    	
    	
    	/*private void handleCameraVideo(Intent intent) {
    	
    		mImageBitmap = null;
    		mImageView.setVisibility(View.INVISIBLE);
    	}*/

    	
        
        
        
        	
        	
        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {  // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(90, 90));
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                //imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

            // OPTION 1
            //imageView.setImageURI(mUrls[position]);
            
            // OPTION 2
            Bitmap bmp = decodeURI(mUrls[position].getPath());
            imageView.setImageBitmap(bmp);
            
            //imageView.setImageResource(mThumbIds[position]);
            return imageView;
        }

        /*
        // references to our images
        private Integer[] mThumbIds = {
                R.drawable.sample_2, R.drawable.sample_3,
                R.drawable.sample_4, R.drawable.sample_5,
                R.drawable.sample_6, R.drawable.sample_7,
                R.drawable.sample_0, R.drawable.sample_1,
                R.drawable.sample_2, R.drawable.sample_3,
                R.drawable.sample_4, R.drawable.sample_5,
                R.drawable.sample_6, R.drawable.sample_7,
                R.drawable.sample_0, R.drawable.sample_1,
                R.drawable.sample_2, R.drawable.sample_3,
                R.drawable.sample_4, R.drawable.sample_5,
                R.drawable.sample_6, R.drawable.sample_7
        };
        */
    }
    
    public class LargeImageAdapter extends BaseAdapter {
        private Context mContext;

        public LargeImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return cc.getCount();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {  // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(180, 160));
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                //imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

            // OPTION 1
            //imageView.setImageURI(mUrls[position]);
            
            // OPTION 2
            Bitmap bmp = decodeURI(mUrls[position].getPath());
            imageView.setImageBitmap(bmp);
            
            //imageView.setImageResource(mThumbIds[position]);
            return imageView;
        }
    }
    
    public Bitmap decodeURI(String filePath){
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Only scale if we need to 
        // (16384 buffer for img processing)
        Boolean scaleByHeight = Math.abs(options.outHeight - 100) >= Math.abs(options.outWidth - 100);
        if(options.outHeight * options.outWidth * 2 >= 16384){
            // Load, scaling to smallest power of 2 that'll get it <= desired dimensions
            double sampleSize = scaleByHeight
                ? options.outHeight / 100
                : options.outWidth / 100;
            options.inSampleSize = 
                (int)Math.pow(2d, Math.floor(
                Math.log(sampleSize)/Math.log(2d)));
        }

        // Do the actual decoding
        options.inJustDecodeBounds = false;
        options.inTempStorage = new byte[512];  
        Bitmap output = BitmapFactory.decodeFile(filePath, options);

        return output;
    }
    
    public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}
    
    /*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (resultCode == RESULT_OK) {
	        if (requestCode == SELECT_PICTURE) {
	            Uri selectedImageUri = data.getData();
	            selectedImagePath = getPath(selectedImageUri);
	            
	            //TextView tv = (TextView) findViewById(R.id.textView1);
	            //tv.setText(selectedImagePath);
	            
	            ImageView iv = (ImageView) findViewById(R.id.imageView1);
	            iv.setImageURI(selectedImageUri);
	        }
	    }
	}
	*/
    
    @SuppressWarnings("deprecation")
	public void onActivityResult( int requestCode, int resultCode, Intent data ) {
    	if (requestCode == VOICE_RECOGNITION_REQUEST_CODE)
    		 
    		   //If Voice recognition is successful then it returns RESULT_OK
    		   if(resultCode == RESULT_OK) {
    		 
    		    ArrayList<String> textMatchList = data
    		    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
    		 
    		    if (!textMatchList.isEmpty()) {
    		     // If first Match contains the 'search' word
    		     // Then start web search.
    		     if (textMatchList.get(0).contains("capture")) {
    		    	 //captureIsPressed(null);
    		    	 Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
    		    	 startActivityForResult( cameraIntent, ACTION_CAMERA_SPEAK );		

    		    	 //Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
    		    	 //startActivity(cameraIntent);
    		     }
    		     else if (textMatchList.get(0).contains("change view")) {
    		    	 changeView(null);
    		    	 //Intent intent = new Intent(this, SettingActivity.class);
    		    	 //startActivity(intent);
    		     }
    		     else {
                     AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                     alertDialog.setMessage("Please say 'capture' or 'change view'.");
                     alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                           // here you can add functions
                           speak(null);

                        }
                     });
                     alertDialog.show();
                 }
    		 
    		    }
    		   //Result code for various error.
    		   }else if(resultCode == RecognizerIntent.RESULT_AUDIO_ERROR){
    		    showToastMessage("Audio Error");
    		   }else if(resultCode == RecognizerIntent.RESULT_CLIENT_ERROR){
    		    showToastMessage("Client Error");
    		   }else if(resultCode == RecognizerIntent.RESULT_NETWORK_ERROR){
    		    showToastMessage("Network Error");
    		   }else if(resultCode == RecognizerIntent.RESULT_NO_MATCH){
    		    showToastMessage("No Match");
    		   }else if(resultCode == RecognizerIntent.RESULT_SERVER_ERROR){
    		    showToastMessage("Server Error");
    		   }
    	
		if ( resultCode == RESULT_OK ) {
			switch ( requestCode ) {
				/*
				case ACTION_REQUEST_GALLERY:
					// user chose an image from the gallery
					loadAsync( data.getData() );
					break;
				*/

				case ACTION_REQUEST_FEATHER:
					/*
					// send a notification to the media scanner
					updateMedia( mOutputFilePath );

					// update the preview with the result
					loadAsync( data.getData() );
					onSaveCompleted( mOutputFilePath );
					*/
					
					// Update the URI list - HANS
					updateMedia( mOutputFilePath );
					timeToUpdate = true;
					try {
						Thread.sleep(3000);
						myRefreshGrid();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					break;
				case ACTION_CAMERA_SPEAK:
					// Update the URI list - HANS
					timeToUpdate = true;
					try {
						Thread.sleep(3000);
						myRefreshGrid();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					break;
				case ACTION_TAKE_PHOTO_B: {
					if (resultCode == RESULT_OK) {
						handleBigCameraPhoto();
					}
					break;
				} // ACTION_TAKE_PHOTO_B
					
				
			}
		} else if ( resultCode == RESULT_CANCELED ) {
			switch ( requestCode ) {
				case ACTION_REQUEST_FEATHER:

					// feather was cancelled without saving.
					// we need to delete the entire session
					//if ( null != mSessionId ) deleteSession( mSessionId );

					// delete the result file, if exists
					if ( mOutputFilePath != null ) {
						//deleteFileNoThrow( mOutputFilePath );
						mOutputFilePath = null;
					}
					
					break;
			}
		}
 		
		
		// refresh the grid view
		//sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
	}
    
    /**
     * Helper method to show the toast message
     **/
     void showToastMessage(String message){
      Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
     }
    
    private void myRefreshGrid(){
    	// HANS - Update grid - START
    	cc = this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
    	//cc.requery();
    	
    	// File[] files=f.listFiles();
    	if (cc != null) {
    		myProgressDialog = new ProgressDialog(MainActivity.this);
    		myProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    		myProgressDialog.setMessage(getResources().getString(R.string.pls_wait_txt));
    		myProgressDialog.show();
			// myProgressDialog.setIcon(R.drawable.blind);

			try {
				cc.moveToFirst();
				int imageLoad = cc.getCount() - 1;
				mUrls = new Uri[cc.getCount()];
				strUrls = new String[cc.getCount()];
				mNames = new String[cc.getCount()];
				for (int i = 0; i < cc.getCount(); i++) {
					cc.moveToPosition(imageLoad);
					mUrls[i] = Uri.parse(cc.getString(1));
					strUrls[i] = cc.getString(1);
					mNames[i] = cc.getString(3);
					imageLoad--;
					// Log.e("mNames[i]",mNames[i]+":"+cc.getColumnCount()+
					// " : " +cc.getString(3));
				}

			} catch (Exception e) {

			}

			myProgressDialog.dismiss();
		}

		// ImageAdapter.notifyDataChanged();
		// grid.setAdapter(adapter);

		// ImageAdapter.notifyDataChanged();
		// grid.invalidateViews();

		// update the grid - HANS
		gridview.setNumColumns(2);
		gridview.setColumnWidth(90);
		gridview.setAdapter(new ImageAdapter(this));
		gridview.invalidateViews();
		ImageButton ib = (ImageButton) findViewById(R.id.imageButton);
		int i1Btn = R.drawable.single_col_white;
		ib.setImageResource(i1Btn);
		currentView = 2;
		// HANS - Update grid - END
	}
    
    private void updateMedia(String filepath) {
		// TODO Auto-generated method stub
		MediaScannerConnection.scanFile( getApplicationContext(), new String[] { filepath }, null, null );
	}

	/**
	 * Once you've chosen an image you can start the feather activity
	 * 
	 * @param uri
	 */
	private void startFeather( Uri uri ) {

		// Log.d( LOG_TAG, "uri: " + uri );

		// first check the external storage availability
		// if ( !isExternalStorageAvilable() ) {
		//	showDialog( EXTERNAL_STORAGE_UNAVAILABLE );
		//	return;
		// }

		// create a temporary file where to store the resulting image
		File file = getNextFileName();
		if ( null != file ) {
			mOutputFilePath = file.getAbsolutePath();
		} else {
			new AlertDialog.Builder( this ).setTitle( android.R.string.dialog_alert_title ).setMessage( "Failed to create a new File" )
					.show();
			return;
		}

		// Create the intent needed to start feather
		Intent newIntent = new Intent( this, FeatherActivity.class );

		// set the source image uri
		newIntent.setData( uri );

		// pass the required api_key and secret ( see
		// http://developers.aviary.com/ )
		newIntent.putExtra( "API_KEY", API_KEY );

		// pass the uri of the destination image file (optional)
		// This will be the same uri you will receive in the onActivityResult
		newIntent.putExtra( "output", Uri.parse( "file://" + mOutputFilePath ) );

		// format of the destination image (optional)
		newIntent.putExtra( Constants.EXTRA_OUTPUT_FORMAT, Bitmap.CompressFormat.JPEG.name() );

		// output format quality (optional)
		newIntent.putExtra( Constants.EXTRA_OUTPUT_QUALITY, 90 );

		// If you want to disable the external effects
		// newIntent.putExtra( Constants.EXTRA_EFFECTS_ENABLE_EXTERNAL_PACKS, false );

		// If you want to disable the external effects
		// newIntent.putExtra( Constants.EXTRA_STICKERS_ENABLE_EXTERNAL_PACKS, false );
		
		// enable fast rendering preview
		// newIntent.putExtra( Constants.EXTRA_EFFECTS_ENABLE_FAST_PREVIEW, true );

		// you can force feather to display only a certain ( see FilterLoaderFactory#Filters )
		// you can omit this if you just wanto to display the default tools

		/*
		 * newIntent.putExtra( "tools-list", new String[] { FilterLoaderFactory.Filters.ENHANCE.name(),
		 * FilterLoaderFactory.Filters.EFFECTS.name(), FilterLoaderFactory.Filters.STICKERS.name(),
		 * FilterLoaderFactory.Filters.ADJUST.name(), FilterLoaderFactory.Filters.CROP.name(),
		 * FilterLoaderFactory.Filters.BRIGHTNESS.name(), FilterLoaderFactory.Filters.CONTRAST.name(),
		 * FilterLoaderFactory.Filters.SATURATION.name(), FilterLoaderFactory.Filters.SHARPNESS.name(),
		 * FilterLoaderFactory.Filters.DRAWING.name(), FilterLoaderFactory.Filters.TEXT.name(),
		 * FilterLoaderFactory.Filters.MEME.name(), FilterLoaderFactory.Filters.RED_EYE.name(),
		 * FilterLoaderFactory.Filters.WHITEN.name(), FilterLoaderFactory.Filters.BLEMISH.name(),
		 * FilterLoaderFactory.Filters.COLORTEMP.name(), } );
		 */

		// you want the result bitmap inline. (optional)
		// newIntent.putExtra( Constants.EXTRA_RETURN_DATA, true );

		// you want to hide the exit alert dialog shown when back is pressed
		// without saving image first
		// newIntent.putExtra( Constants.EXTRA_HIDE_EXIT_UNSAVE_CONFIRMATION, true );

		// -- VIBRATION --
		// Some aviary tools use the device vibration in order to give a better experience
		// to the final user. But if you want to disable this feature, just pass
		// any value with the key "tools-vibration-disabled" in the calling intent.
		// This option has been added to version 2.1.5 of the Aviary SDK
		newIntent.putExtra( Constants.EXTRA_TOOLS_DISABLE_VIBRATION, true );
		
		final DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics( metrics );
		int max_size = Math.min( metrics.widthPixels, metrics.heightPixels );

		// you can pass the maximum allowed image size, otherwise feather will determine
		// the max size based on the device memory
		// Here we're passing the current display size as max image size because after
		// the execution of Aviary we're saving the HI-RES image so we don't need a big
		// image for the preview
		max_size = (int) ( (double) max_size / 0.8 );
		//****//Log.d( LOG_TAG, "max-image-size: " + max_size );
		newIntent.putExtra( "max-image-size", max_size );

		// Enable/disable the default borders for the effects
		newIntent.putExtra( "effect-enable-borders", true );

		// You need to generate a new session id key to pass to Aviary feather
		// this is the key used to operate with the hi-res image ( and must be unique for every new instance of Feather )
		// The session-id key must be 64 char length

		//***//mSessionId = StringUtils.getSha256( System.currentTimeMillis() + API_KEY );
		//***//Log.d( LOG_TAG, "session: " + mSessionId + ", size: " + mSessionId.length() );
		//***//newIntent.putExtra( "output-hires-session-id", mSessionId );

		// ..and start feather
		startActivityForResult( newIntent, ACTION_REQUEST_FEATHER );		
	}
    
	/**
	 * Return a new image file. Name is based on the current time. Parent folder will be the one created with createFolders
	 * 
	 * @return
	 * @see #createFolders()
	 */
	private File getNextFileName() {
		if ( mGalleryFolder != null ) {
			if ( mGalleryFolder.exists() ) {
				File file = new File( mGalleryFolder, "picfix_" + System.currentTimeMillis() + ".jpg" );
				return file;
			}
		}
		return null;
	}
	
	/**
	 * Try to create the required folder on the sdcard where images will be saved to.
	 * 
	 * @return
	 */
	private File createFolders() {

		File baseDir;

		if ( android.os.Build.VERSION.SDK_INT < 8 ) {
			baseDir = Environment.getExternalStorageDirectory();
		} else {
			baseDir = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_PICTURES );
		}

		if ( baseDir == null ) return Environment.getExternalStorageDirectory();

		//****//Log.d( LOG_TAG, "Pictures folder: " + baseDir.getAbsolutePath() );
		File aviaryFolder = new File( baseDir, FOLDER_NAME );

		if ( aviaryFolder.exists() ) return aviaryFolder;
		if ( aviaryFolder.mkdirs() ) return aviaryFolder;

		return Environment.getExternalStorageDirectory();
	}
}