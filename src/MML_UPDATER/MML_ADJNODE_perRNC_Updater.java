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
public class MML_ADJNODE_perRNC_Updater extends MML_Updater_parrent
{

    public MML_ADJNODE_perRNC_Updater(String identyfikator, String rncName, Logger loger, DataSource DOA,String mmlDir, NewFile sprzF)
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
	    //System.out.println(req);
	    ResultSet res=testStatement.executeQuery(req);
	    OdpowiedzSQL rnc=Baza.createAnswer(res);
	    this.rncId=rnc.getValue("Rnc_Bsc_Id", 0);
	    String[] nodebLines=super.mml.getLinia("ADD ADJNODE:");
	    for ( int n=0;n<nodebLines.length;n++)
	    {
		try{
		nbipackage.MmlKommand mmlKom=new nbipackage.MmlKommand(nodebLines[n]);

		String adjnodeName=mmlKom.getValue("NAME");
		String nodeb_id=mmlKom.getValue("NODEBID");
		//System.out.println(""+n+"/"+nodebLines.length+" NODEBNAME="+NodebName);
		if(adjnodeName!=null&&!adjnodeName.equals(""))
		{
		    String ani=mmlKom.getValue("ani");
		    String query="INSERT INTO `raport_konfiguracja_aktualna`.`mml_rnc_Adjnode_ident` (`ani`, `rnc_id`, `name`,`nodeb_id`, `create_date`, `last_update`) VALUES ("+ani+",'"+rncId+"', '"+adjnodeName+"',"+nodeb_id+", "+obecnyDzienCzas+", "+obecnyDzienCzas+") ON DUPLICATE KEY UPDATE last_update="+obecnyDzienCzas+",name='"+adjnodeName+"',nodeb_id='"+nodeb_id+"'";
		   // System.out.println(query);
		    testStatement.executeUpdate(query);
		}
		}
		catch(Exception ee)
		{
		    ee.printStackTrace();
		}
	    }
	    String[] nodebParamLines=super.mml.getLinia("ANI=");
	    String typ="3";
	    for ( int n=0;n<nodebParamLines.length;n++)
	    {
		try
		{
		    nbipackage.MmlKommand mmlKom=new nbipackage.MmlKommand(nodebParamLines[n]);
		    String ani=mmlKom.getValue("ANI");

		    if(ani!=null&&!ani.equals(""))
		    {
			String typ_kom=mmlKom.getCommandType();
			String komName=mmlKom.getCommandName();
			System.out.println(""+n+"/"+nodebParamLines.length+" "+komName);
			java.util.ArrayList<String> paramNames=mmlKom.getParamNames();

			String query="INSERT INTO `raport_konfiguracja_aktualna`.`mml_rnc_komendy` (`typ`, `nazwa`, `typ_kom`, `create_date`) VALUES ("+typ+",'"+komName+"', '"+typ_kom+"',"+obecnyDzienCzas+") ON DUPLICATE KEY UPDATE create_date="+obecnyDzienCzas+",typ_kom='"+typ_kom+"'";
			//System.out.println(query);
			testStatement.executeUpdate(query);
			String reqKomName="select r.row_id from raport_konfiguracja_aktualna.mml_rnc_komendy r where r.typ="+typ+" and r.nazwa='"+komName+"';";
			//System.out.println(reqKomName);
			ResultSet resKomName=testStatement.executeQuery(reqKomName);
			OdpowiedzSQL komN=Baza.createAnswer(resKomName);


			String komenda_row_id=komN.getValue("row_id", 0);
			String reqAdjnodeIdent="select r.row_id from raport_konfiguracja_aktualna.mml_rnc_Adjnode_ident r where r.ani="+ani+" and r.rnc_id='"+rncId+"';";
			//System.out.println(reqKomName);
			ResultSet resNodebIdent=testStatement.executeQuery(reqAdjnodeIdent);
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

			    String reqParam_row_id="select r.row_id from raport_konfiguracja_aktualna.mml_rnc_parametry r where r.komenda_row_id='"+komenda_row_id+"' and r.nazwa='"+Pname+"' LIMIT 1 ";
			   // System.out.println(reqParam_row_id);
			    ResultSet PAR_ROW=testStatement.executeQuery(reqParam_row_id);
			    OdpowiedzSQL PAR_ROWSql=Baza.createAnswer(PAR_ROW);
			   String  param_row_id=PAR_ROWSql.getValue("row_id", 0);
			    String paramVal_token="null";
				String param_dict_id="null";
				if(PVal.length()<50)
				    paramVal_token="'"+PVal+"'";
				else
				{

				    String dictIdSel="select * from raport_konfiguracja_aktualna.mml_rnc_value_dictionary where param_row_id="+param_row_id+" and param_value='"+PVal+"';";
				    ResultSet Komres=testStatement.executeQuery(dictIdSel);
				    OdpowiedzSQL KomSql=Baza.createAnswer(Komres);
				    if(KomSql!=null&&KomSql.rowCount()>0)
				    {
					param_dict_id="'"+KomSql.getValue("row_id", 0)+"'";
				    }
				    else
				    {
					String insDir="insert into raport_konfiguracja_aktualna.mml_rnc_value_dictionary(param_row_id,param_value,value_length) values('"+param_row_id+"','"+PVal+"','"+PVal.length()+"')";
					//System.out.println(insDir);
					testStatement.executeUpdate(insDir);

				    }

				    Komres=testStatement.executeQuery(dictIdSel);
				    KomSql=Baza.createAnswer(Komres);
				    if(KomSql!=null&&KomSql.rowCount()>0)
				    {
					param_dict_id="'"+KomSql.getValue("row_id", 0)+"'";
				    }
				}







			    
			    insertNodebParam="INSERT INTO raport_konfiguracja_aktualna.mml_rnc_Adjnode_param(adjnode_ident,rnc_id,param_ident,komm_ident,value,value_dictionary_id,line_id,last_update) VALUES("
				    + nodeb_ident+","
				    +"'"+rncId+"',"
				    + param_row_id+","
				    + ""+komenda_row_id+","
				    + ""+paramVal_token+","
				    +param_dict_id+","
				    +"'"+n+"',"
				    + ""+obecnyDzienCzas+""
				    + ")";
			   // System.out.println(insertNodebParam);
			    testStatement.executeUpdate(insertNodebParam);

			    /*
			     * String paramVal_token="null";
				String param_dict_id="null";
				if(PVal.length()<50)
				    paramVal_token="'"+PVal+"'";
				else
				{

				    String dictIdSel="select * from raport_konfiguracja_aktualna.mml_rnc_value_dictionary where param_row_id="+param_row_id+" and param_value='"+PVal+"';";
				    ResultSet Komres=testStatement.executeQuery(dictIdSel);
				    OdpowiedzSQL KomSql=Baza.createAnswer(Komres);
				    if(KomSql!=null&&KomSql.rowCount()>0)
				    {
					param_dict_id="'"+KomSql.getValue("row_id", 0)+"'";
				    }
				    else
				    {
					String insDir="insert into raport_konfiguracja_aktualna.mml_rnc_value_dictionary(param_row_id,param_value,value_length) values('"+param_row_id+"','"+PVal+"','"+PVal.length()+"')";
					//System.out.println(insDir);
					testStatement.executeUpdate(insDir);

				    }

				    Komres=testStatement.executeQuery(dictIdSel);
				    KomSql=Baza.createAnswer(Komres);
				    if(KomSql!=null&&KomSql.rowCount()>0)
				    {
					param_dict_id="'"+KomSql.getValue("row_id", 0)+"'";
				    }
				}

			    insertNodebParam="INSERT INTO raport_konfiguracja_aktualna.mml_rnc_Ucell_param(cell_row_id,rnc_id, nodeb_row_id  ,param_ident,komm_ident,value,value_dictionary_id,`line_id`,last_update) VALUES("
				    +"'"+ucell_ident+"',"
				    +"'"+rncId+"',"
				    + "'"+nodeb_ident+"',"
				    + "'"+param_row_id+"',"
				    //+ "(select r.row_id from raport_konfiguracja_aktualna.mml_rnc_parametry r where r.komenda_row_id='"+komenda_row_id+"' and r.nazwa='"+Pname+"' LIMIT 1 ),"
				    + "'"+komenda_row_id+"',"
				    + ""+paramVal_token+","
				    + ""+param_dict_id+","
				    + "'"+n+"',"
				    + ""+obecnyDzienCzas+""
				    + ");";
			     */










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
            
	 //OdpowiedzSQL nody=getNotUpdateCandidates(DataDzisiaj, this.kontrolerName);
            String obecnyDzien="'"+sdf.format(DataDzisiaj)+"'";
	
	     query="delete from raport_konfiguracja_aktualna.mml_rnc_Adjnode_param where last_update<"+obecnyDzien+" and rnc_id='"+this.rncId+"';";
             loger.log(Level.FINEST, "["+this.identyfikator+" CLEANER]START:"+query);
	    testStatement.executeUpdate(query);
            
            query="delete from raport_konfiguracja_aktualna.mml_rnc_Adjnode_ident where last_update<"+obecnyDzien+" and rnc_id='"+this.rncId+"';";
              loger.log(Level.FINEST, "["+this.identyfikator+" CLEANER]START:"+query);
	    testStatement.executeUpdate(query);
	 }
	catch(Exception ee)
	{
	     loger.throwing(this.getClass().toString(), "["+this.identyfikator+" CLEANER] ERROR:query="+query, ee);
	}
	return true;
    }
    
    public OdpowiedzSQL getNotUpdateCandidatesParams(java.util.Date dateBefore, String kontroler)
    {
	String reqNodebIdent="";
	try{
	String obecnyDzien="'"+sdf.format(dateBefore)+"'";

	reqNodebIdent="select n.row_id from raport_konfiguracja_aktualna.mml_rnc_Adjnode_param n where n.last_update<"+obecnyDzien+" and n.rnc_id='"+this.rncId+"';";
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
     public OdpowiedzSQL getNotUpdateCandidates(java.util.Date dateBefore, String kontroler)
    {
	String reqNodebIdent="";
	try{
	String obecnyDzien="'"+sdf.format(dateBefore)+"'";

	reqNodebIdent="select n.row_id from raport_konfiguracja_aktualna.mml_rnc_Adjnode_ident n where n.last_update<"+obecnyDzien+" and n.rnc_id='"+this.rncId+"';";
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
