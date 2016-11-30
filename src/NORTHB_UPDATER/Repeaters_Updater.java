package NORTHB_UPDATER;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import NORTHB_UPDATER.Updater_parrent;
import java.sql.Connection;
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
import java.io.File;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author turczyt
 */
public class Repeaters_Updater extends Updater_parrent	//extends Thread{
{

    public Repeaters_Updater(String identyfikator,String path,int typOperacji,Logger loger,mysqlpackage.DataSource DOA,NewFile sprzF)
    {
	super(identyfikator,path,typOperacji,loger,DOA,sprzF);
	
	


    }
    public boolean add() throws java.sql.SQLException
    {


	
	java.util.Hashtable<String,Integer> NodeNameBand=new java.util.Hashtable<String, Integer>();
	
	 java.util.ArrayList<String> lstExist=new java.util.ArrayList<String>();
	
        String obecnyDzienCzas="'"+super.sdf.format(super.DataDzisiaj)+"'";
	System.out.println("start "+this.identyfikator+" "+sdf.format(DataDzisiaj));
	StringBuffer insertyAll=new StringBuffer();
	
	//loger.info("["+this.identyfikator+"] START WATKU");

	try
	{
	    //connection = DOA.getConnection();

	    //testStatement = connection.createStatement();
	    File stocks = new File(this.kontrolerName);
	    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    Document doc = dBuilder.parse(stocks);
	    doc.getDocumentElement().normalize();

	    System.out.println("root of xml file" + doc.getDocumentElement().getNodeName());
	    NodeList typy=doc.getElementsByTagName("Repeaters").item(0).getChildNodes();

	    //String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
	    //java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT_NOW);
	    //java.util.Date DataDzisiaj=java.util.Calendar.getInstance().getTime();
	    //String obecnyDzienCzas="'"+sdf.format(DataDzisiaj)+"'";


	    for (int t = 0; t < typy.getLength(); t++)
	    {

		if (typy.item(t) instanceof Element)
		{
		    Element systemNode = (Element) typy.item(t);
		    String sys=systemNode.getTagName();
		    //System.out.println("System:"+sys);

		    NodeList repeaters=systemNode.getChildNodes();
		    for(int r=0;r<repeaters.getLength();r++)
		    {
			if (repeaters.item(r) instanceof Element)
			{
			    Element repeater=(Element)repeaters.item(r);
			    Node rCount=repeater.getElementsByTagName("Count").item(0);

			    boolean count=rCount.getTextContent().contains("yes");

				String CountVal="0";
				if(count)
				    CountVal="1";


				String rType=repeater.getElementsByTagName("REPTYPE").item(0).getTextContent();
				String rRegion=repeater.getElementsByTagName("RegionID").item(0).getTextContent();
				String rCellId=repeater.getElementsByTagName("RepeaterCellID1").item(0).getTextContent();
				String rName=repeater.getElementsByTagName("RepeaterName").item(0).getTextContent();
				
				String siteName=rName;
				if(rName.contains("-"))
				{
				    siteName=rName.substring(0, rName.indexOf("-"));
				    siteName=siteName.trim();
				}

				if(rName.length()>199)
				    rName=rName.substring(0, 199);
				String regionId="-1";
				if(rRegion.contains("WAW"))
				    regionId="1";
				if(rRegion.contains("KAT"))
				    regionId="2";
				if(rRegion.contains("POZ"))
				    regionId="3";
				if(rRegion.contains("GDA"))
				    regionId="4";

				String primKey=sys+"|"+siteName;
				System.out.println("###"+primKey);
				testStatement.executeUpdate("insert into raport_konfiguracja_aktualna.repeaters(primKey, name,system,type,region,cellId,count,last_update) values('"+primKey+"','"+rName+"','"+sys+"','"+rType+"',"+regionId+",'"+rCellId+"',"+CountVal+","+obecnyDzienCzas+") ON DUPLICATE KEY UPDATE count="+CountVal+",last_update="+obecnyDzienCzas+";");
				 

			}
		    }
		}
	    }
	    sukces=true;
	    //loger.info( "["+this.identyfikator+"]END OK");
	}
	catch(Exception e)
	{
	    //e.printStackTrace();
	    loger.throwing(this.getClass().toString(), this.identyfikator+".add", e);
	   // loger.l
	    sukces=false;
	}
	finally
	{
	    if(north!=null)
	    {
		try
		{
		    north.closeBuffor();
		    north=null;
		}
		catch(Exception ewe)
		{
		    loger.throwing(this.getClass().toString(), this.identyfikator+".CloseNorthB", ewe);		    //System.err.println("BLAD PRZY ZAMYKANIU NORTHB");
		}
	    }
	    try
	    {
		if(connection!=null)
		{
		    connection.close();
		    connection=null;
		}
	    }
	    catch(Exception ewe)
	    {
		loger.throwing(this.getClass().toString(), this.identyfikator+".CloseDBConnection", ewe);
	    }
	    if(sukces)
		this.answer=this.answer+"["+this.identyfikator+"]ZAKONCZENIE WATKU "+sukces+" NIEMODYFIKOWANE_Z_DB"+lstExist;//+insertyAll);
	    else
		this.errorInfo=this.errorInfo+"ERROR["+this.identyfikator+"]ZAKONCZENIE WATKU "+sukces;//" "+checkStr+"\r\n");//+insertyAll);//+" LST_UCELL=="+inLocell+" LST_ACCES=="+inAcces);
	    return sukces;
	}
    }

}