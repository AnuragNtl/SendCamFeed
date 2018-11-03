package com.clay.sendcamfeed;
import java.io.*;
import java.net.*;
public class SendFeed
{
private String host;
private byte fn=1;
private int port;
private int psize=8192;
	private InetAddress addr1;
private int fdelay=0;
	DatagramSocket ds1;
public SendFeed(InetAddress in4,int port,int psize)
{
this.host=host;
this.port=port;
this.psize=psize;
try
{
	ds1=new DatagramSocket();
addr1=in4;
}
catch(IOException ioexcepn){}
}
byte eInfo[]=new byte[3];
public void setFrameDelay(int fdly)
{
	fdelay=fdly;
}
private void send(byte bsend[],int n)
{
	try
	{
DatagramPacket dp1=new DatagramPacket(bsend,n,addr1,port);
//port(dp1!=null)
ds1.send(dp1);
}
catch(IOException ioexcepn){}
/*System.out.println("--------------------------------------------------");
for(int i=0;i<n;i++)
	System.out.println(bsend[i]+" "+(bsend[i]>=32?(char)(bsend[i]):' '));
System.out.println("--------------------------------------------------");*/
}
public void present(byte b1[])
{
	int len=b1.length;
	if(fn==10)
		fn=1;
	int pnum=1;
eInfo[0]=fn;
eInfo[1]=0;
if(len%psize==0)
	eInfo[2]=(byte)(len/psize);
else
	eInfo[2]=(byte)((len/psize)+1);
send(eInfo,3);
int nop=eInfo[2];
int pos=0;
for(int i=pnum;i<=nop;i++)
{
	ByteArrayOutputStream out1=new ByteArrayOutputStream();
	eInfo[1]=(byte)pnum;
	out1.write(eInfo,0,2);
	if(pos+psize>len)
		out1.write(b1,pos,len%psize);
	else
	out1.write(b1,pos,psize);
	byte bs1[]=out1.toByteArray();
	send(bs1,bs1.length);
	pnum++;
	pos+=psize;
}
	fn++;	
	try
	{
		Thread.sleep(fdelay*50);
	}
	catch(InterruptedException intrrpdexcepn){}
}
//////////////
};
