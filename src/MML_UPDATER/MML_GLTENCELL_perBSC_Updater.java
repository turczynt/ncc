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
public class MML_GLTENCELL_perBSC_Updater extends MML_Updater_parrent
{

    public MML_GLTENCELL_perBSC_Updater(String identyfikator, String rncName, Logger loger, DataSource DOA,String mmlDir, NewFile sprzF)
    {
	super(identyfikator,rncName,loger,DOA,mmlDir,sprzF);
    }
    String bscIndex;
    
    java.util.Hashtable<String,String> paramIds;////  key=komm_name;param_name  value=komm_ident;param_ident;
    
    public boolean add() throws java.sql.SQLException
    {
	boolean udane=false;
	String obecnyDzienCzas="'"+sdf.format(DataDzisiaj)+"'";
	HashMap <String,String> btsID_per_btsRowIdent=new HashMap<String,String>();
	try
	{
	    String req="select r.Rnc_Bsc_Index from oncall.konfiguracja_aktualna_rnc_bsc r where r.Rnc_Bsc_Name ='"+this.kontrolerName+"' LIMIT 1";
	    System.out.println(req);
	    ResultSet res=testStatement.executeQuery(req);
	    OdpowiedzSQL rnc=Baza.createAnswer(res);
	    this.bscIndex=rnc.getValue("Rnc_Bsc_Index", 0);
	    String[] btsLines=super.mml.getLinia("ADD GLTENCELL:");
	    for ( int n=0;n<btsLines.length;n++)
	    {
		try
                {
                    nbipackage.MmlKommand mmlKom=new nbipackage.MmlKommand(btsLines[n]);
                    String SRCLTENCELLID=mmlKom.getValue("SRCLTENCELLID");
                    String NBRLTENCELLID=mmlKom.getValue("NBRLTENCELLID");
                    String SPTRESEL=mmlKom.getValue("SPTRESEL");
                    String SPTBLINDHO=mmlKom.getValue("SPTBLINDHO");
                    String SPTRAPIDSEL=mmlKom.getValue("SPTRAPIDSEL");
                    String NCELLPRI=mmlKom.getValue("NCELLPRI");
                    
                   
                    
                    String query="INSERT INTO raport_konfiguracja_aktualna.GLTENCELL_perBSC (bsc_index,SRCLTENCELLID, NBRLTENCELLID, SPTRESEL, SPTBLINDHO,SPTRAPIDSEL,NCELLPRI,last_update) VALUES ("+this.bscIndex+","+SRCLTENCELLID+",'"+NBRLTENCELLID+"','"+SPTRESEL+"','"+SPTBLINDHO+"','"+SPTRAPIDSEL+"','"+NCELLPRI+"', now()) ON DUPLICATE KEY UPDATE last_update=(now()),SPTRESEL='"+SPTRESEL+"', SPTBLINDHO='"+SPTBLINDHO+"',SPTRAPIDSEL='"+SPTRAPIDSEL+"',NCELLPRI='"+NCELLPRI+"'";
                    testStatement.executeUpdate(query);
                 
		}
		catch(Exception ee)
		{
		    ee.printStackTrace();
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
	     query="delete from raport_konfiguracja_aktualna.GLTENCELL_perBSC where last_update<"+obecnyDzien+" and bsc_index='"+this.bscIndex+"'";
	    testStatement.executeUpdate(query);
	
	 }
	catch(Exception ee)
	{
	     loger.throwing(this.getClass().toString(), "["+this.identyfikator+" CLEANER] ERROR:query="+query, ee);
	}
	return true;
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
