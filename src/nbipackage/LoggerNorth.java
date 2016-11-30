/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nbipackage;

/**
 *
 * @author turczyt
 */
public class LoggerNorth
{
    String systemUser;
    String[] args;
    private String login;
    private String passwd;
    String logInfDir;

    public LoggerNorth(String logInfDir,String systemUser, String[] args)
    {
	this.logInfDir=logInfDir;
	this.systemUser = systemUser;
	this.args = args;
	this.login=null;
	this.passwd=null;
	for(int i=0;i<args.length;i++)
	{
	    
	    if(args[i].equals("-u")&&args.length>(i+1))
	    {
		this.login=args[i+1];
	    }
	    if(args[i].equals("-p")&&args.length>(i+1))
	    {
		this.passwd=args[i+1];
	    }
	}
	if(login!=null&&passwd!=null)
	{
	    
	    NewFile logInfo=new NewFile(logInfDir+systemUser+"/logInfo.conf");
	    logInfo.setReadOnlyForOwner();
	    if(logInfo.istnieje())
	    {
		String loginFile=logInfo.getParamValue("login");
		String passwdFile=logInfo.getParamValue("passwd");
		if(loginFile==null||passwdFile==null)
		{
		    logInfo.clear();
		    logInfo.dopisz("login="+login+"\n");
		    logInfo.dopisz("passwd="+passwd+"\n");
		}
	    }
	    else
	    {
	
		    logInfo=new NewFile(logInfDir+systemUser+"/logInfo.conf");
		    logInfo.setReadOnlyForOwner();
		    logInfo.dopisz("login="+login+"\n");
		    logInfo.dopisz("passwd="+passwd+"\n");

	    }
	}
	if(login==null||passwd==null)
	{
	    NewFile logInfo=new NewFile(logInfDir+systemUser+"/logInfo.conf");
	    logInfo.setReadOnlyForOwner();
	    if(logInfo.istnieje())
	    {
		this.login=logInfo.getParamValue("login");
		this.passwd=logInfo.getParamValue("passwd");
	    }
	}
    }

    public LoggerNorth(String[] args)
    {
	this.args = args;
	this.login=null;
	this.passwd=null;
	for(int i=0;i<args.length;i++)
	{

	    if(args[i].equals("-u")&&args.length>(i+1))
	    {
		this.login=args[i+1];
	    }
	    if(args[i].equals("-p")&&args.length>(i+1))
	    {
		this.passwd=args[i+1];
	    }
	}
    }

    public String getLogin()
    {
	return login;
    }

    public String getPasswd()
    {
	return passwd;
    }
}