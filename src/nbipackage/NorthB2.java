/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nbipackage;

/**
 *
 * @author turczyt
 */

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;
import java.net.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class NorthB2 {
String login;
String passwd;
String hostIp;
String serwer ;
Socket echoSocket ;
PrintWriter out ;
InputStream in;

    public NorthB2(String login, String passwd, String hostIp) throws UnknownHostException, IOException
    {
	this.login = login;
	this.passwd = passwd;
	this.hostIp = hostIp;
	this.echoSocket = new Socket(hostIp, 31114);
	this.out = new PrintWriter(echoSocket.getOutputStream(), true);
        this.in = echoSocket.getInputStream();
	String userInput="LGI:OP=\""+login+"\",PWD=\""+passwd+"\""+";";
        this.out.println(userInput);
	//while(in.available()>0)
	int sign=0;

	while((sign=in.read())>0)
	{
	    

	    System.out.println("|"+sign+" "+((char)sign));
	}
    }
    public static void main(String[] args)
    {
	try{
	NorthB2 north=new NorthB2("U-boot","utranek098","172.16.35.150");
	}
	catch(Exception ee)
	{
	    ee.printStackTrace();
	}
    }


}
