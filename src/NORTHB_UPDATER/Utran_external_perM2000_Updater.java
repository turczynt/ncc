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
import java.util.logging.Logger;
/**
 * @author turczyt
 */
public class Utran_external_perM2000_Updater extends Updater_parrent	//extends Thread{
{

    public Utran_external_perM2000_Updater(String identyfikator,String M2000_Name,int typOperacji,Logger loger,mysqlpackage.DataSource DOA,NewFile sprzF)
    {
	super(identyfikator,M2000_Name,typOperacji,loger,DOA,sprzF);
    }


    @Override
    public boolean add() throws java.sql.SQLException
    {
	sukces=true;
	//String obecnyDzienCzas="'"+sdf.format(DataDzisiaj)+"'";
	try
	{
	    String allEnodeReq="select m.M2000_Ip,m.M2000_Index,en.Enodeb_Index,en.Enodeb_Name from oncall.konfiguracja_aktualna_m2000 m, oncall.konfiguracja_aktualna_enodeb en where( m.M2000_Name like '%"+this.kontrolerName+"%' and m.M2000_Index= en.M2000_index)";

	    ResultSet res=testStatement.executeQuery(allEnodeReq);
	    OdpowiedzSQL enody=Baza.createAnswer(res);
	    String m2000_ip="";
	    String m2000_index="";
	    if(enody.rowCount()>0)
	    {
		m2000_ip=enody.getValue("M2000_Ip", 0);
		m2000_index=enody.getValue("M2000_Index", 0);
		north=new NorthB(m2000_ip,"U-boot","utranek098",null);
		for(int n=0;n<enody.rowCount();n++)
		{
		     String enodeName=enody.getValue("Enodeb_Name", n);
		     String query="";
		     
		     try
		     {
			 String enode_Index=enody.getValue("Enodeb_Index", n);
			 String tmp=north.make(enodeName, "LST UTRANEXTERNALCELL:");
			 String retcode="";
			 if(tmp.contains("RETCODE"))
			     retcode=NewFile.getFirstLine(new String[]{"RETCODE"}, tmp);
			 //if(enodeName.contains("POZ0017A"))
			 //System.out.println(tmp);
			 if(retcode.contains("RETCODE = 0")) ///poprawna odpowiedz
			 {
			     NPack nn=new NPack(tmp);
			     java.util.ArrayList<Paczka> utrcells=nn.getAllPacks();

			     loger.log(Level.FINEST, "["+this.identyfikator+"]"+n+"/"+enody.rowCount()+" "+enodeName+" ilosc="+utrcells.size());
			     for(int e=0;e<utrcells.size();e++)
			     {
				 /*String cellId=ecellki.get(e).getWartosc("Cell ID");
				  String cellIndex=enode_Index+"|"+cellId;
				  String LoId=ecellki.get(e).getWartosc("Local Cell ID");
				  String cellName=ecellki.get(e).getWartosc("Cell Name");*/
				 String updateStat="NEW";
				 boolean dopas=false;
				 String indet="";
				 String Ecell_Index="";
				 String Ucell_Index="";
				 String Enodeb_Index="";
				 String rncid=utrcells.get(e).getWartosc("RNC ID");
				 String Ucell_Id=utrcells.get(e).getWartosc("RNC cell ID");
				 String ncc=utrcells.get(e).getWartosc("Mobile network code");
				 //String query="insert into oncall.konfiguracja_aktualna_ecell (Cell_Index, Cell_Id, Cell_Name, Enodeb_Index,System,Status_Ubl,Status_Act,Status_Barr,Status_Reserv,Last_Update,Update_Status) VALUES('"+cellIndex+"','"+cellId+"','"+cellName+"','"+enode_Index+"','LTE','"+blkStat+"','"+actStat+"','"+barrStat+"','"+reservStat+"',"+obecnyDzienCzas+",'"+updateStat+"') ON DUPLICATE KEY UPDATE Status_Ubl='"+blkStat+"',Status_Act='"+actStat+"',Status_Barr='"+barrStat+"',Cell_Name='"+cellName+"',Status_Reserv='"+reservStat+"',Last_Update="+obecnyDzienCzas+", Update_Status='OK'";
				 query=   "set @.enode_index =(select en.Enodeb_Index from oncall.konfiguracja_aktualna_enodeb en where en.Enodeb_Name= '"+enodeName+"' LIMIT 1	);"+
					    "set @.rnc_index=(select concat((select r.Rnc_Bsc_Index from oncall.konfiguracja_aktualna_rnc_bsc r where r.Rnc_Bsc_Id='"+rncid+"' LIMIT 1),'|%'));"+
					    "set @.ucell_index=(select uc.Cell_Index  from oncall.konfiguracja_aktualna_ucell uc where (uc.Cell_id='"+Ucell_Id+"'  )LIMIT 1);"+
					    "insert into raport_konfiguracja_aktualna.utranexternalncell (ident,Enodeb_Index,Ucell_Index,ncc,rncid,Ucell_id,Last_Updete,Update_Status) Values("+
						"(SELECT CONCAT(@.enode_index ,(SELECT CONCAT('&' ,@.ucell_index)))),"+
						"@.enode_index,"+
						"@.ucell_index,"+
						"'"+ncc+"',"+
						"'"+rncid+"',"+
						"'"+Ucell_Id+"',"+
						"now(),"+
						"'NEW'"+
					    ")"+
					    "ON DUPLICATE KEY UPDATE "+
						"Ucell_Index=@.ucell_index,"+
						"Enodeb_Index=@.enode_index,"+
						"rncid='"+rncid+"',"+
						"Ucell_id='"+Ucell_Id+"',"+
						"ncc='"+ncc+"',"+
						"Last_Updete=now(),"+
						"Update_Status='OK'"+
					    ";";
				 String[] comm= query.split(";");
				 for(int c=0;c<comm.length;c++)
				 {
				     testStatement.addBatch(comm[c]);
				 }
				 try{
				 int[] batchOut=testStatement.executeBatch();
				 testStatement.clearBatch();
				 }
				 catch(Exception er)
				 {
				    loger.log(Level.FINEST, "["+this.identyfikator+"]ERROR:"+query, er);
				 }
				 //System.out.println("WYkonanie:"+batchOut);
				 //testStatement.execute(query);
				 //System.out.println("\t"+cellId);
			     }
			     //System.out.println(":OK");
			 }
			 else// bledna odpowiedz -- zmiana w 'Update_Stat' wszystkich komorek o danym enodeb_index
			 {
			     String new_update_status="NOT_FOUND";
			     String info=north.make2("REG NE:NAME=\""+enody.getValue("Enodeb_Name", n)+"\"").replaceAll("'", "");
			     if(info.contains("RETCODE"))
				 info=NewFile.getFirstLine(new String[]{"RETCODE"}, info);
			     if(info.length()>40)
				 info=info.substring(0, 39);
			     if(info.contains("Cant Found"))
				 new_update_status="NOT_FOUND";
			     if(info.contains("NE does not Connection"))
				 new_update_status="NOT_CONNECTED";
			     query="update raport_konfiguracja_aktualna.utranexternalncell SET Last_Updete=now(), Update_Status='"+new_update_status+"' WHERE Enodeb_Index='"+enode_Index+"'";
			     try{
			     testStatement.execute(query);
			     }
			     catch(Exception er)
			     {
				loger.log(Level.FINEST, "["+this.identyfikator+"]ERROR:"+query, er);
			     }
			 }
		     }
		     catch(Exception ee)
		     {
			loger.log(Level.FINEST, "["+this.identyfikator+"]ERROR:", ee);
		    }
		}
	    }
	    //System.out.println("["+this.identyfikator+"]ZAKONCZENIE WATKU");
	}
	catch(Exception e)
	{
	    loger.log(Level.FINEST, "["+this.identyfikator+"]ERROR ", e);
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
		    connection.close();
	    }
	    catch(Exception ewe)
	    {
		loger.throwing(this.getClass().toString(), this.identyfikator+".CloseDBConnection", ewe);
	    }
	    if(!sukces)
		this.errorInfo="["+this.identyfikator+"] ERROR "+this.errorInfo+"ANS ZAKONCZENIE WATKU "+sukces;//" "+checkStr+"\r\n");//+insertyAll);//+" LST_UCELL=="+inLocell+" LST_ACCES=="+inAcces);
	    return sukces;
	}
    }
}