package com.clay.sendcamfeed;

import android.os.Bundle;
import android.app.Activity;
import android.view.*;
import android.widget.*;
import android.view.Menu;
import android.content.*;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.*;
import java.io.*;
import android.graphics.YuvImage;
import android.os.*;
public class MainActivity extends Activity implements SurfaceHolder.Callback
{

	private SurfaceView sv1;
	private SurfaceHolder sh1;
	private Camera cam1;
	private boolean p1=false;
	private int fdelay=0;
	private Handler hn1=new Handler()
	{
		public void handleMessage(Message msg1)
		{
			Bundle data=msg1.getData();
			int type=data.getInt("type");
			if(type==0)
			Toast.makeText(MainActivity.this,data.getString("msg1"),6000).show();
			else
			{
				int q=data.getInt("q"),v=data.getInt("v");
				Toast.makeText(MainActivity.this,"Received Update:q="+q+" v="+v,6000).show();
				switch(v)
			{
			case 3:quality=q;
			break;
			case 4:fdelay=q;
			break;
			}
			setCameraProperties();
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		getWindow().setFormat(PixelFormat.UNKNOWN);
		sv1=(SurfaceView)findViewById(R.id.sv1);
		sh1=sv1.getHolder();
		sh1.addCallback(this);
        sh1.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);	
        	}
	private byte b11[];
	private ImageTransfer imgt1;
	private int w,h;
	private YuvImage ym1;
	private int quality=35;
	private int height=480,width=640;
	private void start()
	{

		Toast.makeText(MainActivity.this,"Taking Pic",6000).show();
		new Thread(new Runnable(){
			public void run(){
				imgt1=new ImageTransfer(2234)
				{
					public void onParameterChange(int v,int q)
					{
						onLog("Changing Parameters");
						Message msg1=new Message();
						Bundle bn1=new Bundle();
						bn1.putInt("type",1);
						bn1.putInt("q",q);
						bn1.putInt("v",v);
						msg1.setData(bn1);
						hn1.sendMessage(msg1);
					}
					public void onLog(String s1)
					{
						Message msg1=new Message();
						Bundle bn1=new Bundle();
						bn1.putInt("type",0);
						bn1.putString("msg1",s1);
						msg1.setData(bn1);
						hn1.sendMessage(msg1);
					}
				};
				imgt1.connect();
				while(!imgt1.isConnected()){}
	
				}
			}).start();
		cam1.setPreviewCallback(new Camera.PreviewCallback()
        {
        	public void onPreviewFrame(byte b1[],Camera cam1)
        	{
        	
if(imgt1==null)
	return;
        		if(!imgt1.isConnected())
	return;
		Camera.Parameters pm1=cam1.getParameters();
		w=pm1.getPreviewSize().width;
		h=pm1.getPreviewSize().height;
ym1=new YuvImage(b1,pm1.getPreviewFormat(),w,h,null);
	ByteArrayOutputStream bs1=new ByteArrayOutputStream();
	ym1.compressToJpeg(new android.graphics.Rect(0,0,ym1.getWidth(),ym1.getHeight()),quality,bs1);
	b11=bs1.toByteArray();
        		new Thread(new Runnable(){public void run(){try{
	if(!imgt1.isSending())
	{
		imgt1.send(b11);
	}
		}
		catch(Exception e){Toast.makeText(MainActivity.this,e.toString(),6000).show();}//surfaceChanged(null,0,0,0);	
		}}).start();
	    	}
        });

	}
	
public void surfaceDestroyed(SurfaceHolder s)
{
	cam1.stopPreview();
	cam1.release();
	cam1=null;
	p1=false;
}

public void surfaceChanged(SurfaceHolder s,int format,int width,int height)
{
	if(p1)
	{
		cam1.stopPreview();
		p1=false;
	}
	if(cam1!=null)
	{try{
		cam1.setPreviewDisplay(sh1);
		cam1.startPreview();
		p1=true;
	}catch(IOException ioexcepn){}}
}
public void surfaceCreated(SurfaceHolder s)
{
	cam1=Camera.open();
	setCameraProperties();
	}
private void setCameraProperties()
{
	Camera.Parameters pm1=cam1.getParameters();
	w=pm1.getPreviewSize().width;
	h=pm1.getPreviewSize().height;
pm1.setPictureSize(MainActivity.this.width,MainActivity.this.height);
pm1.setJpegQuality(quality);
pm1.set("jpeg-quality", quality);
pm1.setPictureFormat(PixelFormat.JPEG);
pm1.setPreviewSize(width, height);
if(imgt1!=null)
imgt1.setFrameDelay(fdelay);
cam1.setParameters(pm1);
}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
			start();	// TODO Auto-generated method stub
				return false;
			}
		});
		return true;
	}

}
