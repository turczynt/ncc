/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package MML_UPDATER;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import poolThread.UpdaterInterface;
import nbipackage.*;
import mysqlpackage.*;
import poolThread.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author turczyt
 */
public class MML_Updater_parrent implements UpdaterInterface{

    String kontrolerName;
    int typOperacji;
    String identyfikator;
    mysqlpackage.DataSource DOA;
    Logger loger;
    boolean sukces;
    Statement testStatement;
    
    Connection connection;
    String DATE_FORMAT_NOW;
    java.text.SimpleDateFormat sdf;
    java.text.SimpleDateFormat withoutTimeFormat;
    java.util.Date DataDzisiaj;
    
    String answer;
    String errorInfo;
    java.util.ArrayList<String> komercyjneClean;
    java.util.ArrayList<String> niekomercyjneClean;
    NewFile sprzF;
    String mmlDirPath;
    NewFile mml;

    public MML_Updater_parrent(String identyfikator,String kontroler,Logger loger,mysqlpackage.DataSource DOA,String mmlDirPath,NewFile sprzF)
    {
	try{
	    this.sprzF=sprzF;
	    this.mmlDirPath=mmlDirPath;
	    this.mml=new NewFile(mmlDirPath+"/"+kontroler+".mml");
	    this.identyfikator=identyfikator;
	    //loger.log(Level.FINER,"CREATE "+this.identyfikator);
	    this.DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
	    this.kontrolerName=kontroler;
	    this.DOA=DOA;
            
	    this.typOperacji=typOperacji;
            
	    this.loger=loger;
	    this.answer="";
	    this.errorInfo="";
	    this.sukces=false;
	    this.sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    this.withoutTimeFormat= new java.text.SimpleDateFormat("yyyy-MM-dd");
	    this.DataDzisiaj=java.util.Calendar.getInstance().getTime();

	}
	catch(Exception ee)
	{
	     loger.log(Level.FINER,"["+this.identyfikator+"] ERROR ", ee);
	}
    }

    public void run()
    {
	komercyjneClean=new java.util.ArrayList<String>();
	niekomercyjneClean=new java.util.ArrayList<String> ();
	boolean udane=false;
	try
	{
	    this.answer="";
	    this.errorInfo="";

	    this.connection = DOA.getConnection();
            
            
	   // this.connection.setAutoCommit(false);
	    
	    this.testStatement = connection.createStatement();



	    if (this.typOperacji == this.ADD)
	    {
		loger.log(Level.INFO, "["+this.identyfikator+"] START");

		//boolean ok=add();
		int prob=0;
		//boolean ok=add();

		while((udane=add())==false&&prob<5)
		{
		   // System.out.println( "["+this.identyfikator+"] SUKCES="+udane+" TRY AGAIN "+prob);
		    prob++;

		   loger.log(Level.INFO, "["+this.identyfikator+"] SUKCES="+udane+" TRY AGAIN ("+(prob)+") AFTER 30s "+odp());
		   try{
		    Thread.sleep(30000);
		    }
		   catch(Exception ee)
		   {
		      loger.throwing(this.getClass().getName()+"."+this.identyfikator,"run", ee);
		   }
		    loger.log(Level.INFO, "["+this.identyfikator+"] START ");
		    this.answer="";
		    this.errorInfo="";
		    
		    try
		    {
			try
			{
			    this.testStatement.close();
			    this.connection.close();
			    
			}
			catch(Exception ee)
			{
			   loger.throwing(this.getClass().getName()+"."+this.identyfikator,"CLOSE CONNECTION", ee);
			}
			this.connection = DOA.getConnection();
			this.testStatement = connection.createStatement();

		    }
		    catch(Exception ee)
		    {
                        loger.throwing(this.getClass().getName()+"."+this.identyfikator,"CLOSE CONNECTION", ee);
//			ee.printStackTrace();
		    }



		    //this.sukces=false;

		}
		if(udane)
		{
		    try
		    {
			loger.log(Level.FINEST, "["+this.identyfikator+" CLEANER]START");
			//this.connection = DOA.getConnection();
			//this.testStatement = connection.createStatement();
			loger.log(Level.FINEST, "["+this.identyfikator+" CLEANER]CHECK DEL");
			clearNotUpdatet();
			loger.log(Level.FINEST, "["+this.identyfikator+" CLEANER]END");
		    }
		    catch(Exception ee)
		    {
			loger.log(Level.FINEST, "["+this.identyfikator+" CLEANER] ERROR", ee);
		    }
		    finally
		    {
			try
			{
			    this.testStatement.close();
			    this.connection.close();
			}
			catch(Exception eer)
			{
			    loger.log(Level.FINEST, "["+this.identyfikator+" CLEANER CLOSE DB CONN] ERROR", eer);
			}
		    }
		}
		//loger.log(Level.INFO, odp());
	    }
	}
	catch (SQLException ex)
	{
	    //loger.log(Level.SEVERE, "["+this.identyfikator+"]ERROR:", ex);
	    //loger.log(Level.SEVERE, "["+this.identyfikator+"] END NOT_SUCCED");
	    loger.throwing(this.getClass().getName()+"."+this.identyfikator,"run", ex);
	}
	catch(Exception ew)
	{
	    loger.throwing(this.getClass().getName()+"."+this.identyfikator,"run", ew);
	}
	finally
	{

	    try
	    {
		if(this.testStatement!=null)
		{
		    this.testStatement.close();
		    this.testStatement=null;
		}
	    }
	    catch(Exception ee)
	    {
		loger.throwing(this.getClass().getName()+"."+this.identyfikator,"finally_close_DB_testStatement", ee);
	    }
	    try
	    {
		if(this.connection!=null)
		{
		    this.connection.close();
		    this.connection=null;
		}
	    }
	    catch(Exception ee)
	    {
		loger.throwing(this.getClass().getName()+"."+this.identyfikator,"finally_close_DB_connection", ee);
	    }
	    if(udane)
		loger.log(Level.INFO, "["+this.identyfikator+"] END SUCCED="+udane+" "+odp());
	    else
		loger.log(Level.INFO, "["+this.identyfikator+"] END ERROR SUCCED="+udane+" "+odp());
	}
    }

    public void truncate() throws java.sql.SQLException
    {

	//mysql.executeQuery("truncate table webmirror.nodeb");
	Connection connection = DOA.getConnection();
        Statement testStatement = null;
        try
	{
	    testStatement = connection.createStatement();
           ;// testStatement.execute("truncate table webmirror.nodeb");
	}
	catch (SQLException e)
	{
            throw e;
        }
	finally
	{
	    testStatement.close();
	    connection.close();
	}
    }

    public boolean add() throws SQLException
    {
	this.answer="";
	this.errorInfo="";
	return true;

    }

    public String odp()
    {
	;//throw new UnsupportedOperationException("Not supported yet.");
	if(!sukces)
	    return this.answer+" "+this.errorInfo;
	else
	    return this.answer;
    }

    public boolean isDone() throws SQLException
    {
	return this.sukces;
    }

    public void setDOA(DataSource DOA)
    {
	;//throw new UnsupportedOperationException("Not supported yet.");
	this.DOA=DOA;
    }
    public void clean()
    {
	;
    }

     public boolean clearNotUpdatet()
    {
	return true;
    }
    public boolean executCleanCommends()
    {
	sprzF.dopisz("############################"+this.identyfikator+" NIEKOMERCYJNE(WYKONANE AUTOMATYCZNIE)########################\r\n");
	for(int z=0;z<this.niekomercyjneClean.size();z++)
	{
	    try
	    {
		if(!(this.niekomercyjneClean.get(z).trim()).equals(""))
		{
		testStatement.executeUpdate(this.niekomercyjneClean.get(z).trim());
		sprzF.dopisz(this.niekomercyjneClean.get(z).trim());
		sprzF.dopisz(";EXECUTED\r\n");
		}
		else
		    sprzF.dopisz("\r\n");
	    }
	    catch(Exception ee)
	    {
		sprzF.dopisz(this.niekomercyjneClean.get(z).trim());
		sprzF.dopisz(";ERROR\r\n");
		ee.printStackTrace();
		loger.log(Level.FINEST, "["+this.identyfikator+" CLEAN] ERROR", ee);
	    }
	}
	sprzF.dopisz("############################"+this.identyfikator+" POWIAZANE Z KOMERCYJNYMI(DO WERYFIKACJI)########################\r\n");
	for(int z=0;z<this.komercyjneClean.size();z++)
	{
	    sprzF.dopisz(this.komercyjneClean.get(z).trim()+"\r\n");
	}
	sprzF.dopisz("\r\n\r\n");
	return true;
    }

     public OdpowiedzSQL getNotUpdateCandidates(java.util.Date dateBefore,String kontroler)
    {
	 return null;
     }

}
