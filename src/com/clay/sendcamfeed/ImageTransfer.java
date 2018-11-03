package com.clay.sendcamfeed;
import java.io.*;
import java.net.*;
public abstract class ImageTransfer
{
private String boundary="#next.img#";
private byte img[];   
private ServerSocket s1;
private Socket sc1;
private OutputStream o1;
private int port=80;
private boolean sending=false;
private SendFeed sf1;
public boolean isSending()
{
	return sending;
}
private Runnable rnnbl1=new Runnable()
{
	public void run()
	{
	if(sending)
			return;
	if(img==null)
		return;
		sending=true;
		//onLog("Sending feed");
/*		try
		{
			if(img==null)
				return;
			DataOutputStream d1=new DataOutputStream(o1);
			d1.write(img,0,img.length);
			d1.write(boundary.getBytes());
			d1.flush();
		}
		catch(IOException ioexcepn){}*/
		PrintWriter pw1=new PrintWriter(new BufferedWriter(new OutputStreamWriter(o1)));
		try
		{
			pw1.println("Sending Feed");
			sf1.present(img);
		pw1.println("Sent");
		}
		catch(Throwable t)
		{
			//try
			{
				pw1.println(t.toString()+t.getMessage());
				pw1.close();
			}
			//catch(IOException e){}
			}
		sending=false;
		//onLog("Feed Sent");
	}
};
private int remotePort=2234;
public ImageTransfer(int port)
{
this.port=port;
}
private boolean connected=false;
public void connect()
{
	Runnable r1=new Runnable()
	{
		public void run()
		{
			try
			{
	s1=new ServerSocket(port);
	sc1=s1.accept();
	o1=sc1.getOutputStream();
String addr1=sc1.getInetAddress().getHostAddress();
		sf1=new SendFeed(InetAddress.getByName(addr1),remotePort,8192);
		connected=true;
		onLog("Connected to "+addr1);
		Thread thrd2=new Thread(r2);
	thrd2.start();
			}
			catch(IOException ioexcepn){}
			}
	};
	Thread thrd1=new Thread(r1);
	thrd1.start();
		}
private Runnable r2=new Runnable()
{
	public void run()
	{
		try
		{
		DataInputStream d1=new DataInputStream(sc1.getInputStream());
		while(true)
		{
			try
			{
			byte rd1[]=new byte[1];
			d1.read(rd1,0,1);
			onLog("BYte Read="+rd1[0]);
			if(rd1[0]==3)
			{
			d1.read(rd1,0,1);
				onParameterChange(3,rd1[0]);
			}
			else if(rd1[0]==4)
			{
				d1.read(rd1,0,1);
				onParameterChange(4,rd1[0]);
			}
			}
			catch(Exception e){}
			}
	}
	catch(IOException ioexcepn){}
	}
		};
public boolean isConnected()
{
	return connected;
}
public void setFrameDelay(int fdly)
{
	sf1.setFrameDelay(fdly);
}
public abstract void onParameterChange(int v,int q);
public void send(byte img1[])
{
	img=img1;
	Thread thrd1=new Thread(rnnbl1);
	thrd1.start();
}
public abstract void onLog(String s1);
};
