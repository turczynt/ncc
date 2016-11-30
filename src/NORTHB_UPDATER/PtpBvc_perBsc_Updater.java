package NORTHB_UPDATER;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
public class PtpBvc_perBsc_Updater extends Updater_parrent		//extends Thread{
{
    public PtpBvc_perBsc_Updater(String identyfikator,String bscName,int typOperacji,Logger loger,mysqlpackage.DataSource DOA,NewFile sprzF)
    {
	super(identyfikator,bscName,typOperacji,loger,DOA,sprzF);
    }

    String bsc_id;
    @Override
    public boolean add() throws java.sql.SQLException
    {
	//String checkStr=this.identyfikator;
	String obecnyDzienCzas="'"+sdf.format(DataDzisiaj)+"'";
	String lstBts="";
	StringBuffer insertyAll=new StringBuffer();
	try
	{
	    this.errorInfo=this.errorInfo+"; DOA.connected,createStatement";
	    String req="select r.Rnc_Bsc_Index, r.Rnc_Bsc_Id, r.Rnc_Bsc_Name, m.M2000_Ip, r.M2000_Index from oncall.konfiguracja_aktualna_rnc_bsc r,  oncall.konfiguracja_aktualna_m2000 m where(r.m2000_index=m.M2000_Index and r.Rnc_Bsc_Name like '%"+this.kontrolerName+"%') LIMIT 1";
	    ResultSet res=testStatement.executeQuery(req);
	    OdpowiedzSQL rnc=Baza.createAnswer(res);


	    this.errorInfo=this.errorInfo+"; get M2000 info for bsc="+this.kontrolerName;
	    if(rnc.rowCount()>0)
	    {

		String m2000_ip=rnc.getValue("M2000_Ip", 0);
		String bsc_name=rnc.getValue("Rnc_Bsc_Name", 0);
		 bsc_id=rnc.getValue("Rnc_Bsc_Id",0);
		String bsc_index=rnc.getValue("Rnc_Bsc_Index", 0);
		String m2000_Index=rnc.getValue("M2000_Index", 0);
		north=new nbipackage.NorthB(m2000_ip, "U-boot", "utranek098",null);
		this.errorInfo=this.errorInfo+"; connect with "+m2000_ip+" by NorthB";
		lstBts=north.make(this.kontrolerName, "LST PTPBVC:");

		if(lstBts!=null&&lstBts.contains("RETCODE = 0"))
		{
		    //sukces = true;
		    System.out.println(this.identyfikator+" LST PTPBVC: wykonane");
		    this.errorInfo=this.errorInfo+"; POBRANE LST PTPBVC:";
		}
		else
		{
		    System.out.println(this.identyfikator+" LST PTPBVC: ERROR");

		    this.errorInfo=this.errorInfo+"; BLAD W POBIERANIU POBRANE LST PTPBVC:";
		    sukces=false;

		}
		north.closeBuffor();
		north=null;
		this.errorInfo=this.errorInfo+"; ZAMKNIECIE NORTHB";
		this.errorInfo=this.errorInfo+"; POBRANIE KOMOREK Z DB";

		if(lstBts.contains("RETCODE = 0"))
		{
		    java.util.ArrayList<Paczka> trxy=new java.util.ArrayList<Paczka>();
		    try
		    {
			NPack nn=new NPack(lstBts);
			trxy=nn.getAllPacks();

		    }
		    catch(Exception ewqr)
		    {
			System.err.println("WYJEBALO przy NPack LST PTPBVC:;");
			loger.log(Level.FINEST, "["+this.identyfikator+"]ERROR ", ewqr);
		    }
		    if(trxy!=null)
		    {


			for(int u=0;u<trxy.size();u++)
			{
			    Paczka komorka=trxy.get(u);
			    if(komorka!=null)
			    {
				//

				String nse_id=komorka.getWartosc("NSE Identifier");
                                String ptpbvc_id=komorka.getWartosc("PTP BVC Identifier");
				String cell_id=komorka.getWartosc("Cell Index");
                                String adm_state=komorka.getWartosc("Administrative State");
				if(cell_id!=null&&!cell_id.equals("")&&nse_id!=null&&!nse_id.equals("")&&ptpbvc_id!=null&&!ptpbvc_id.equals(""))
				{
					 String update_stat="NEW";
				 String insert="INSERT INTO `raport_konfiguracja_aktualna`.`ptpbvc_on_bsc` ( cell_id,nse_id,bsc_id,ptpbvc_id,adm_state,create_date,last_update_date,update_state) VALUES ('"+cell_id+"','"+nse_id+"','"+bsc_id+"','"+ptpbvc_id+"','"+adm_state+"',(now()),"+obecnyDzienCzas+",'"+update_stat+"')  ON DUPLICATE KEY UPDATE adm_state='"+adm_state+"',last_update_date="+obecnyDzienCzas+",update_state='UPDATE' ";
				 //String insert="insert into oncall.konfiguracja_aktualna_site(Site_Index, M2000_Index, Site_Name, Last_Update, Update_Status) VALUES(NULL,'"+m2000_Index+"','"+siteName+"',"+obecnyDzienCzas+",'"+update_stat+"') ON DUPLICATE KEY UPDATE Last_Update="+obecnyDzienCzas+", Update_Status='OK', M2000_Index='"+m2000_Index+"';"
				//
				 //System.out.println(insert);
				 testStatement.execute(insert);
				}
//				 String insert="insert into oncall.konfiguracja_aktualna_bts (Bts_Index, Rnc_Bsc_Index, Site_Index,Bts_Id,Bts_Name,gsm,dcs,System,Last_Update,Update_Status,Region) values('"+Bts_Index+"', '"+bsc_index+"', (select Site_Index from oncall.konfiguracja_aktualna_site where Site_Name like '%"+siteName+"%' LIMIT 1),"+Bts_Id+",'"+Bts_name+"', (select count((select gc.cell_id from oncall.konfiguracja_aktualna_gcell gc where(gc.Bts_Index='"+Bts_Index+"' and gc.System like '%GSM%') LIMIT 1))),(select count((select gc.cell_id from oncall.konfiguracja_aktualna_gcell gc where(gc.Bts_Index='"+Bts_Index+"' and gc.System like '%DCS%') LIMIT 1))),'System',"+obecnyDzienCzas+",'NEW',"+region+") ON DUPLICATE KEY UPDATE Last_Update="+obecnyDzienCzas+",Bts_Name='"+Bts_name+"',Update_Status='UPDATE',gsm=(select count((select gc.cell_id from oncall.konfiguracja_aktualna_gcell gc where(gc.Bts_Index='"+Bts_Index+"' and gc.System like '%GSM%') LIMIT 1))),dcs=(select count((select gc.cell_id from oncall.konfiguracja_aktualna_gcell gc where(gc.Bts_Index='"+Bts_Index+"' and gc.System like '%DCS%') LIMIT 1)))";

			    }
			}
			this.errorInfo=this.errorInfo+"; END for(int u=0;u<gcell.size();u++)";
		    }
		    try
		    {
			String[] komendy=insertyAll.toString().split(";");
			int bathRoz=0;
			for(int k=0;k<komendy.length;k++)
			{
			    if(komendy[k]!=null&&!komendy[k].trim().equals(""))
			    {
				try
				{
				    testStatement.executeUpdate(komendy[k]);
				}
				catch (SQLException ex)
				{
				    loger.log(Level.FINEST, "["+this.identyfikator+"]ERROR IN:"+komendy[k]+" ", ex);
				}
				catch (Exception ex)
				{
				    loger.log(Level.FINEST, "["+this.identyfikator+"]ERROR IN:"+komendy[k]+" ", ex);
				}
			    }
			    if(bathRoz==1000||k==(komendy.length-1))
			    {
				    loger.info(this.identyfikator+" wykonanno "+k+"/"+komendy.length);
				    bathRoz=0;
			    }
			    bathRoz++;
			}
			sukces=true;
		    }
		    catch(Exception batchExc)
		    {
			loger.log(Level.FINEST, "["+this.identyfikator+"]ERROR ", batchExc);
			sukces=false;
		    }
		}
	    }
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

    @Override
     public boolean clearNotUpdatet()
    {
	 try
	 {
	     java.util.ArrayList<String> poleceniaK=new java.util.ArrayList<String>();
	     java.util.ArrayList<String> poleceniaNK=new java.util.ArrayList<String>();
	     String obecnyDzien="'"+sdf.format(DataDzisiaj)+"'";
	     OdpowiedzSQL btsy=getNotUpdateCandidates(DataDzisiaj, this.kontrolerName);
	    
	    for(int p=0;p<btsy.rowCount();p++)
	    {
		String ident=btsy.getValue("ptpbvc_on_bsc_ident", p);
		String query="delete from raport_konfiguracja_aktualna.ptpbvc_on_bsc where ptpbvc_on_bsc_ident='"+ident+"';";
		niekomercyjneClean.add(query);
	    }

	    super.executCleanCommends();
	     return true;
	}
	catch(Exception ee)
	{
	      loger.log(Level.FINEST,"["+this.identyfikator+" CLEANER] ERROR:" , ee);
	    return false;
	}
    }





       @Override
    public OdpowiedzSQL getNotUpdateCandidates(java.util.Date dateBefore, String kontroler)
    {
	String obecnyDzien="'"+sdf.format(dateBefore)+"'";
	try
	{
	    String req="select bt.* from raport_konfiguracja_aktualna.ptpbvc_on_bsc bt where(bt.last_update_date<"+obecnyDzien+" and bt.bsc_id='"+bsc_id+"');";
	    ResultSet res=testStatement.executeQuery(req);
	    OdpowiedzSQL rnc=Baza.createAnswer(res);
	    return rnc;
	}
	catch(Exception ee)
	{
	    loger.log(Level.FINEST,"["+this.identyfikator+"] ERROR:" , ee);
	    return null;
	}
    }


}