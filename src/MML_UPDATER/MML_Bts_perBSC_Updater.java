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
import java.util.HashMap;
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
public class MML_Bts_perBSC_Updater extends MML_Updater_parrent
{

    public MML_Bts_perBSC_Updater(String identyfikator, String rncName, Logger loger, DataSource DOA,String mmlDir, NewFile sprzF)
    {
	super(identyfikator,rncName,loger,DOA,mmlDir,sprzF);
    }
    String bscId;
    
    java.util.Hashtable<String,String> paramIds;////  key=komm_name;param_name  value=komm_ident;param_ident;
    
    public boolean add() throws java.sql.SQLException
    {
	boolean udane=false;
	String obecnyDzienCzas="'"+sdf.format(DataDzisiaj)+"'";
	HashMap <String,String> btsID_per_btsRowIdent=new HashMap<String,String>();
	try
	{
	    String req="select r.Rnc_Bsc_Id from oncall.konfiguracja_aktualna_rnc_bsc r where r.Rnc_Bsc_Name ='"+this.kontrolerName+"' LIMIT 1";
	    System.out.println(req);
	    ResultSet res=testStatement.executeQuery(req);
	    OdpowiedzSQL rnc=Baza.createAnswer(res);
	    this.bscId=rnc.getValue("Rnc_Bsc_Id", 0);
	    String[] btsLines=super.mml.getLinia("ADD BTS:");
	    for ( int n=0;n<btsLines.length;n++)
	    {
		try
                {
                    nbipackage.MmlKommand mmlKom=new nbipackage.MmlKommand(btsLines[n]);

                    String BtsName=mmlKom.getValue("BTSNAME");
                    //System.out.println(""+n+"/"+nodebLines.length+" NODEBNAME="+NodebName);
                    if(BtsName!=null&&!BtsName.equals(""))
                    {
                        String BtsId=mmlKom.getValue("BTSID");
                        String query="INSERT INTO `raport_konfiguracja_aktualna`.`mml_bsc_bts_ident` (`bts_id`, `bsc_id`, `name`, `create_date`, `last_update`) VALUES ("+BtsId+",'"+bscId+"', '"+BtsName+"', "+obecnyDzienCzas+", "+obecnyDzienCzas+") ON DUPLICATE KEY UPDATE last_update="+obecnyDzienCzas+",name='"+BtsName+"'";
                       // System.out.println(query);
                        testStatement.executeUpdate(query);
                        if(!btsID_per_btsRowIdent.containsKey(BtsId))
                        {
                            String reqNodebIdent="select r.row_id from raport_konfiguracja_aktualna.mml_bsc_bts_ident r where r.bts_id="+BtsId+" and r.bsc_id='"+bscId+"';";
                            //System.out.println(reqKomName);
                            ResultSet resNodebIdent=testStatement.executeQuery(reqNodebIdent);
                            OdpowiedzSQL komNodebIdent=Baza.createAnswer(resNodebIdent);
                            btsID_per_btsRowIdent.put(BtsId, komNodebIdent.getValue("row_id", 0));
			
                        }
                    }
		}
		catch(Exception ee)
		{
		    ee.printStackTrace();
		}
	    }
	    String[] nodebParamLines=super.mml.getLinia("BTSID=");
            
            java.util.ArrayList<String> allBtsIdLines=super.mml.getLiniaList("BTSID=");
            HashMap<String,java.util.ArrayList<String>> komendyBts=this.sortToHashByBtsllId(allBtsIdLines);
            
            
	    String typ="1";
            
	    for ( int n=0;n<nodebParamLines.length;n++)
	    {
		try
		{
		    nbipackage.MmlKommand mmlKom=new nbipackage.MmlKommand(nodebParamLines[n]);
		    String NodebId=mmlKom.getValue("BTSID");
		    
		    if(NodebId!=null&&!NodebId.equals(""))
		    {
			String typ_kom=mmlKom.getCommandType();
			String komName=mmlKom.getCommandName();
			//System.out.println(""+n+"/"+nodebParamLines.length+" "+komName);
			java.util.ArrayList<String> paramNames=mmlKom.getParamNames();

			String query="INSERT INTO `raport_konfiguracja_aktualna`.`mml_bsc_komendy` (`typ`, `nazwa`, `typ_kom`, `create_date`) VALUES ("+typ+",'"+komName+"', '"+typ_kom+"',"+obecnyDzienCzas+") ON DUPLICATE KEY UPDATE create_date="+obecnyDzienCzas+",typ_kom='"+typ_kom+"'";
			//System.out.println(query);
			testStatement.executeUpdate(query);
			String reqKomName="select r.row_id from raport_konfiguracja_aktualna.mml_bsc_komendy r where r.typ="+typ+" and r.nazwa='"+komName+"';";
			//System.out.println(reqKomName);
			ResultSet resKomName=testStatement.executeQuery(reqKomName);
			OdpowiedzSQL komN=Baza.createAnswer(resKomName);


			String komenda_row_id=komN.getValue("row_id", 0);
			String bts_ident=btsID_per_btsRowIdent.get(NodebId);
			for(int z=0;z<paramNames.size();z++)
			{
			    String insertNodebParam="";
			    try
                            {
                                String Pname=paramNames.get(z);
                                String PVal=mmlKom.getValue(Pname);
                                String insert="INSERT INTO `raport_konfiguracja_aktualna`.`mml_bsc_parametry` (`komenda_row_id`, `nazwa`,`create_date`) VALUES ('"+komenda_row_id+"','"+Pname+"',"+obecnyDzienCzas+") ON DUPLICATE KEY UPDATE create_date="+obecnyDzienCzas+";";
                                //System.out.println(insert);
                                testStatement.executeUpdate(insert);
                                insertNodebParam="INSERT INTO raport_konfiguracja_aktualna.mml_bsc_bts_param(bts_ident,bsc_id,param_ident,komm_ident,value,last_update,line_id) VALUES("
				    + bts_ident+","
				    +"'"+bscId+"',"
				    + "(select r.row_id from raport_konfiguracja_aktualna.mml_bsc_parametry r where r.komenda_row_id='"+komenda_row_id+"' and r.nazwa='"+Pname+"' LIMIT 1 ),"
				    + ""+komenda_row_id+","
				    + "'"+PVal+"',"
				    + ""+obecnyDzienCzas+","
                                    +n+""
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
                catch(com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException ee)
                {
                     connection = DOA.getConnection();
                     testStatement = connection.createStatement();
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
	
         String obecnyDzien="'"+sdf.format(DataDzisiaj)+"'";
	     query="delete from raport_konfiguracja_aktualna.mml_bsc_bts_param where last_update<"+obecnyDzien+" and bsc_id='"+this.bscId+"'";
	    testStatement.executeUpdate(query);
             query="delete from raport_konfiguracja_aktualna.mml_bsc_bts_ident where last_update<"+obecnyDzien+" and bsc_id='"+this.bscId+"'";
	    testStatement.executeUpdate(query);
	
	 }
	catch(Exception ee)
	{
	     loger.throwing(this.getClass().toString(), "["+this.identyfikator+" CLEANER] ERROR:query="+query, ee);
	}
	return true;
    }
    
    private void createParamIds()
    {
        this.paramIds=new java.util.Hashtable<String,String>();
        
    }
    
     private HashMap<String,java.util.ArrayList<String>> sortToHashByBtsllId(java.util.ArrayList<String> source)
    {
        HashMap<String,java.util.ArrayList<String>>  tmp=new HashMap<String,java.util.ArrayList<String>> ();
        for(int z=0;z<source.size();z++)
        {
             nbipackage.MmlKommand mmlKom=new nbipackage.MmlKommand(source.get(z));
                String CellID=mmlKom.getValue("BTSID");
               java.util.ArrayList<String> lista;
               if(tmp.containsKey(CellID))
               {
                   lista=tmp.get(CellID);
               }
               else
                   lista=new java.util.ArrayList<String>();
               lista.add(source.get(z));
               tmp.put(CellID, lista);
        }
        return tmp;
    }
    
}
