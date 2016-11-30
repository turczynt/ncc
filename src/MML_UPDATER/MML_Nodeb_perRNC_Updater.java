/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package MML_UPDATER;

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
import java.util.logging.Logger;

/**
 *
 * @author turczyt
 */
public class MML_Nodeb_perRNC_Updater extends MML_Updater_parrent
{

    public MML_Nodeb_perRNC_Updater(String identyfikator, String rncName, Logger loger, DataSource DOA,String mmlDir, NewFile sprzF)
    {
	super(identyfikator,rncName,loger,DOA,mmlDir,sprzF);
    }
    String rncId;
    public boolean add() throws java.sql.SQLException
    {
	boolean udane=false;
	String obecnyDzienCzas="'"+sdf.format(DataDzisiaj)+"'";
	
	try
	{
	    String req="select r.Rnc_Bsc_Id from oncall.konfiguracja_aktualna_rnc_bsc r where r.Rnc_Bsc_Name ='"+this.kontrolerName+"' LIMIT 1";
	    System.out.println(req);
	    ResultSet res=testStatement.executeQuery(req);
	    OdpowiedzSQL rnc=Baza.createAnswer(res);
	    this.rncId=rnc.getValue("Rnc_Bsc_Id", 0);
	    String[] nodebLines=super.mml.getLinia("ADD UNODEB:");
	    for ( int n=0;n<nodebLines.length;n++)
	    {
		try{
		nbipackage.MmlKommand mmlKom=new nbipackage.MmlKommand(nodebLines[n]);

		String NodebName=mmlKom.getValue("NODEBNAME");
		//System.out.println(""+n+"/"+nodebLines.length+" NODEBNAME="+NodebName);
		if(NodebName!=null&&!NodebName.equals(""))
		{
		    String NodebId=mmlKom.getValue("nodebid");
		    String query="INSERT INTO `raport_konfiguracja_aktualna`.`mml_rnc_Nodeb_ident` (`nodeb_id`, `rnc_id`, `name`, `create_date`, `last_update`) VALUES ("+NodebId+",'"+rncId+"', '"+NodebName+"', "+obecnyDzienCzas+", "+obecnyDzienCzas+") ON DUPLICATE KEY UPDATE last_update="+obecnyDzienCzas+",name='"+NodebName+"'";
		   // System.out.println(query);
		    testStatement.executeUpdate(query);
		}
		}
		catch(Exception ee)
		{
		    ee.printStackTrace();
		}
	    }
	    String[] nodebParamLines=super.mml.getLinia("NODEBID=");
	    String typ="1";
	    for ( int n=0;n<nodebParamLines.length;n++)
	    {
		try
		{
		    nbipackage.MmlKommand mmlKom=new nbipackage.MmlKommand(nodebParamLines[n]);
		    String NodebId=mmlKom.getValue("nodebid");
		    
		    if(NodebId!=null&&!NodebId.equals(""))
		    {
			String typ_kom=mmlKom.getCommandType();
			String komName=mmlKom.getCommandName();
			//System.out.println(""+n+"/"+nodebParamLines.length+" "+komName);
			java.util.ArrayList<String> paramNames=mmlKom.getParamNames();

			String query="INSERT INTO `raport_konfiguracja_aktualna`.`mml_rnc_komendy` (`typ`, `nazwa`, `typ_kom`, `create_date`) VALUES ("+typ+",'"+komName+"', '"+typ_kom+"',"+obecnyDzienCzas+") ON DUPLICATE KEY UPDATE create_date="+obecnyDzienCzas+",typ_kom='"+typ_kom+"'";
			//System.out.println(query);
			testStatement.executeUpdate(query);
			String reqKomName="select r.row_id from raport_konfiguracja_aktualna.mml_rnc_komendy r where r.typ="+typ+" and r.nazwa='"+komName+"';";
			//System.out.println(reqKomName);
			ResultSet resKomName=testStatement.executeQuery(reqKomName);
			OdpowiedzSQL komN=Baza.createAnswer(resKomName);


			String komenda_row_id=komN.getValue("row_id", 0);
			String reqNodebIdent="select r.row_id from raport_konfiguracja_aktualna.mml_rnc_Nodeb_ident r where r.nodeb_id="+NodebId+" and r.rnc_id='"+rncId+"';";
			//System.out.println(reqKomName);
			ResultSet resNodebIdent=testStatement.executeQuery(reqNodebIdent);
			OdpowiedzSQL komNodebIdent=Baza.createAnswer(resNodebIdent);
			String nodeb_ident=komNodebIdent.getValue("row_id", 0);
			for(int z=0;z<paramNames.size();z++)
			{
			    String insertNodebParam="";
			    try{
			    String Pname=paramNames.get(z);
			    String PVal=mmlKom.getValue(Pname);
			    String insert="INSERT INTO `raport_konfiguracja_aktualna`.`mml_rnc_parametry` (`komenda_row_id`, `nazwa`,`create_date`) VALUES ('"+komenda_row_id+"','"+Pname+"',"+obecnyDzienCzas+") ON DUPLICATE KEY UPDATE create_date="+obecnyDzienCzas+";";

			    //System.out.println(insert);
			    testStatement.executeUpdate(insert);
			    insertNodebParam="INSERT INTO raport_konfiguracja_aktualna.mml_rnc_Nodeb_param(nodeb_ident,rnc_id,param_ident,komm_ident,value,last_update) VALUES("
				    + nodeb_ident+","
				    +"'"+rncId+"',"
				    + "(select r.row_id from raport_konfiguracja_aktualna.mml_rnc_parametry r where r.komenda_row_id='"+komenda_row_id+"' and r.nazwa='"+Pname+"' LIMIT 1 ),"
				    + ""+komenda_row_id+","
				    + "'"+PVal+"',"
				    + ""+obecnyDzienCzas+""
				    + ")";
			    //System.out.println(insertNodebParam);
			    testStatement.executeUpdate(insertNodebParam);
			    }
			    catch(Exception ee)
			    {
				 loger.throwing(this.getClass().toString(), "["+this.identyfikator+"] ERROR:", ee);
			    }

			}
			
		    }
		}
		catch(Exception ee)
		{
		     loger.throwing(this.getClass().toString(), "["+this.identyfikator+"] ERROR:", ee);
		}
	    }


	    udane=true;

	}
	catch(Exception ee)
	{
	     loger.throwing(this.getClass().toString(), "["+this.identyfikator+"] ERROR:", ee);
	}

	return udane ;
    }

    public boolean clearNotUpdatet()
    {
	String query="";
	try{
	 OdpowiedzSQL nody=getNotUpdateCandidates(DataDzisiaj, this.kontrolerName);
	 for(int z=0;nody!=null&&z<nody.rowCount();z++)
	 {
	     String row_id=nody.getValue("row_id", z);
	     //System.out.println(z+"/"+nody.rowCount()+" row_id="+row_id);
	     query="delete from raport_konfiguracja_aktualna.mml_rnc_Nodeb_param where row_id='"+row_id+"'";
	    testStatement.executeUpdate(query);
	 }
	 }
	catch(Exception ee)
	{
	     loger.throwing(this.getClass().toString(), "["+this.identyfikator+" CLEANER] ERROR:query="+query, ee);
	}
	return true;
    }
    public OdpowiedzSQL getNotUpdateCandidates(java.util.Date dateBefore, String kontroler)
    {
	String reqNodebIdent="";
	try{
	String obecnyDzien="'"+sdf.format(dateBefore)+"'";

	reqNodebIdent="select n.row_id from raport_konfiguracja_aktualna.mml_rnc_Nodeb_param n where n.last_update<"+obecnyDzien+" and n.rnc_id='"+this.rncId+"';";
	System.out.println(reqNodebIdent);
			ResultSet resNodebIdent=testStatement.executeQuery(reqNodebIdent);
			OdpowiedzSQL komNodebIdent=Baza.createAnswer(resNodebIdent);

	return komNodebIdent;
	}
	catch(Exception ee)
	{
	     loger.throwing(this.getClass().toString(), "["+this.identyfikator+" CLEANER] ERROR:req="+reqNodebIdent+"\r\n", ee);
	}
	return null;
    }

}
