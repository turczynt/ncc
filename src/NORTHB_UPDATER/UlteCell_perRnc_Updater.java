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
public class UlteCell_perRnc_Updater extends Updater_parrent	//extends Thread{
{
    String rnc_id;
    public UlteCell_perRnc_Updater(String identyfikator,String rncName,int typOperacji,Logger loger,mysqlpackage.DataSource DOA,NewFile sprzF)
    {
	super(identyfikator,rncName,typOperacji,loger,DOA,sprzF);
    }

    @Override
    public boolean add() throws java.sql.SQLException
    {

	this.errorInfo=this.identyfikator;
	sukces=false;

        String obecnyDzienCzas="'"+sdf.format(DataDzisiaj)+"'";

	String inLocell="";
	String inAcces="";
	StringBuffer insertyAll=new StringBuffer();
	try
	{
	    this.errorInfo=this.errorInfo+"; DOA.connected,createStatement";
	    String req="select r.Rnc_Bsc_Index, r.Rnc_Bsc_Id, r.Rnc_Bsc_Name, m.M2000_Ip, r.M2000_Index from oncall.konfiguracja_aktualna_rnc_bsc r,  oncall.konfiguracja_aktualna_m2000 m where(r.m2000_index=m.M2000_Index and r.Rnc_Bsc_Name like '%"+this.kontrolerName+"%') LIMIT 1";
	    ResultSet res=testStatement.executeQuery(req);
	    OdpowiedzSQL rnc=Baza.createAnswer(res);

	    this.errorInfo=this.errorInfo+"; get M2000 info for rnc="+this.kontrolerName;
	    if(rnc.rowCount()>0)
	    {

		String m2000_ip=rnc.getValue("M2000_Ip", 0);
		String rnc_name=rnc.getValue("Rnc_Bsc_Name", 0);
		rnc_id=rnc.getValue("Rnc_Bsc_Id",0);
		String rnc_index=rnc.getValue("Rnc_Bsc_Index", 0);
		String m2000_Index=rnc.getValue("M2000_Index", 0);
		north=new nbipackage.NorthB(m2000_ip, "U-boot", "utranek098",null);
		this.errorInfo=this.errorInfo+"; connect with "+m2000_ip+" by NorthB";
		inLocell=north.make(this.kontrolerName, "LST ULTECELL:LSTFORMAT=HORIZONTAL");

		if(inLocell!=null&&inLocell.contains("RETCODE = 0")&&inAcces!=null&&inAcces.contains("RETCODE = 0"))
		{
		    //sukces = true;
		    System.out.println(this.identyfikator+"LST ULTECELL: wykonane");
		    this.errorInfo=this.errorInfo+"; POBRANE LST ULTECELL:";
		}
		else
		{
		    System.out.println(this.identyfikator+" LST ULTECELL: ERROR");
		    this.errorInfo=this.errorInfo+"; BLAD W POBIERANIU LST ULTECELL:";
		    //north.closeBuffor();
		    //north=null;
		    //connection.close();
		    sukces=false;
		}
		north.closeBuffor();
		north=null;
		this.errorInfo=this.errorInfo+"; ZAMKNIECIE NORTHB";

		this.errorInfo=this.errorInfo+"; POBRANIE KOMOREK Z DB";

		if(inLocell.contains("RETCODE = 0"))
		{
			java.util.ArrayList<Paczka> ucell=new java.util.ArrayList<Paczka>();

			try
			{
			    NPack nn=new NPack(inLocell);
			    ucell=nn.getAllPacks();
			    this.errorInfo=this.errorInfo+"; Konwersja UCELL -->List<Paczka>";
			}
			catch(Exception ewqr)
			{
			    loger.log(Level.FINEST, "["+this.identyfikator+"] ERROR: ", ewqr);
			}
			if(ucell!=null)
			{
			    this.errorInfo=this.errorInfo+"; START for(int u=0;u<ucell.size();u++)";
			    for(int u=0;u<ucell.size();u++)
			    {
				Paczka komorka=ucell.get(u);
				if(komorka!=null)
				{
				    String lte_cell_index=komorka.getWartosc("LTE Cell Index");
                                    

				    String lte_cell_name=komorka.getWartosc("LTE Cell Name");
				    String lte_physical_ind=komorka.getWartosc("LTE Physical Cell Identity");
				    if(!lte_cell_name.equals(""))
				    {
					String lte_cell_identity_MML=komorka.getWartosc("LTE Cell Identity");
                                        if(lte_cell_identity_MML==null||lte_cell_identity_MML.equals(""))
                                            lte_cell_identity_MML=komorka.getWartosc("EUTRAN Cell Identity");
                                        if(lte_cell_identity_MML==null||lte_cell_index.equals(""))
                                            lte_cell_identity_MML="null";
                                        else
                                            lte_cell_identity_MML="'"+lte_cell_identity_MML+"'";
					String mcc=komorka.getWartosc("Mobile Country Code");
					String mnc=komorka.getWartosc("Mobile Network Code");
					String tac=komorka.getWartosc("Tracking Area Code");
					String op_gr_ind=komorka.getWartosc("Operator Group Index");
					
					String lte_freq_band=komorka.getWartosc("LTE Cell Frequency Band");
					String lte_down_freq=komorka.getWartosc("LTE Cell Downlink Frequency");




					//String insert="insert into oncall.konfiguracja_aktualna_ucell (Cell_Index, Cell_Id, Cell_Name, System, Nodeb_Index,Status_blk,Status_barr,Status_act,Status_reserv,Last_Update,Update_status,LocellId,Lac_dec,Sac_dec,Rac_dec,TimeOffset,Scr_code,Dl_freq) values((SELECT CONCAT((select nn.Nodeb_Index from oncall.konfiguracja_aktualna_nodeb nn where nn.Nodeb_Name like '%"+Nodeb_Name+"%' and nn.Rnc_Bsc_Index='"+rnc_index+"' LIMIT 1),'|"+Cell_id+"') ),"+Cell_id+",'"+Cell_name+"', '"+Band+"',(select nn.Nodeb_Index from oncall.konfiguracja_aktualna_nodeb nn where nn.Nodeb_Name like '%"+Nodeb_Name+"%' and nn.Rnc_Bsc_Index='"+rnc_index+"' LIMIT 1),'"+blkStat+"','"+barrStat+"','"+actStat+"','"+reservStat+"',"+obecnyDzienCzas+",'NEW',"+locellId+","+lacInt+","+sacInt+","+racInt+",'"+timeOffset+"','"+scrCode+"','"+DlFreq+"' )  ON DUPLICATE KEY UPDATE  Status_blk='"+blkStat+"',Status_barr='"+barrStat+"',Status_act='"+actStat+"',Cell_Name='"+Cell_name+"',Status_reserv='"+reservStat+"',Last_Update="+obecnyDzienCzas+",Update_status='"+updateStat+"',LocellId="+locellId+",Lac_dec="+lacInt+",Sac_dec="+sacInt+",Rac_dec="+racInt+",TimeOffset='"+timeOffset+"',Scr_code='"+scrCode+"',Dl_freq='"+DlFreq+"'";

					String insert="INSERT INTO raport_konfiguracja_aktualna.ulte_cell_on_rnc ( lte_cell_index,lte_cell_name, lte_cell_identity_MML, rnc_id, mcc, mnc, tac, op_gr_ind, lte_physical_ind, lte_freq_band, lte_down_freq, create_date, last_update_date, update_state) VALUES "+
														 "('"+lte_cell_index+"', '"+lte_cell_name+"', "+lte_cell_identity_MML+", '"+rnc_id+"', '"+mcc+"', '"+mnc+"', '"+tac+"', '"+op_gr_ind+"', '"+lte_physical_ind+"', '"+lte_freq_band+"', '"+lte_down_freq+"', (now()), "+obecnyDzienCzas+", 'CREATE')   ON DUPLICATE KEY UPDATE lte_cell_index='"+lte_cell_index+"', lte_cell_identity_MML="+lte_cell_identity_MML+", rnc_id='"+rnc_id+"', mcc='"+mcc+"', mnc='"+mnc+"', tac='"+tac+"', op_gr_ind='"+op_gr_ind+"', lte_physical_ind='"+lte_physical_ind+"', lte_freq_band='"+lte_freq_band+"', lte_down_freq='"+lte_down_freq+"', last_update_date="+obecnyDzienCzas+",update_state='UPDATE' ;";

					try
					{
					    insertyAll.append(insert    );

					}
					catch(Exception eq)
					{
					    loger.log(Level.FINEST, "["+this.identyfikator+"] ERROR: ", eq);
					}
				    }
				}
			    }
			    this.errorInfo=this.errorInfo+"; END for(int u=0;u<ucell.size();u++)";
			}



			try{

			String[] komendy=insertyAll.toString().split(";");
			insertyAll=null;
			this.errorInfo=this.errorInfo+"SPLIT insertALL to TAB;";
			int bathRoz=0;
			this.errorInfo=this.errorInfo+"KOMENDY TAB TO EXECUTE LENGTH="+komendy.length+";";
                        int poprawnych=0;
			for(int k=0;k<komendy.length;k++)
			{
			    //  System.out.println(komendy[k]);
			    try
			    {
				if(komendy[k]!=null&&!komendy[k].trim().equals(""))
				{

				    testStatement.executeUpdate(komendy[k]);
				  //  System.out.println(komendy[k]);
                                  poprawnych++;

				}
				if(bathRoz==1000||k==(komendy.length-1))
				{
					//int[] wyniki=testStatement.executeBatch();
					//testStatement.clearBatch();
				       loger.log(Level.FINEST, "["+this.identyfikator+"] wykonanno "+k+"(udanych="+poprawnych+")/"+komendy.length);
					bathRoz=0;
				}
				bathRoz++;
			    }
			    catch (SQLException ex)
			    {
			        System.err.println("ERROR"+this.identyfikator+" "+komendy[k]);
			        loger.log(Level.FINEST, "["+this.identyfikator+"] ERROR:"+komendy[k]+" ",ex);
			    }
			    catch (Exception ex)
			    {
			        loger.log(Level.FINEST, "["+this.identyfikator+"] ERROR:"+komendy[k]+" ",ex);
			    }
			}
			//int[] wyniki=testStatement.executeBatch();
			//testStatement.clearBatch();
			this.errorInfo=this.errorInfo+"END OF ITERATING BATCH;";
			sukces=true;
			}
			catch(Exception batchExc)
			{
			    loger.log(Level.FINEST, "["+this.identyfikator+"] ERROR:"+" ",batchExc);
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
	 String obecnyDzien="'"+sdf.format(DataDzisiaj)+"'";
	//String obecnyDzien="'"+withoutTimeFormat.format(dateBefore)+"'";

	   String req="delete from raport_konfiguracja_aktualna.ulte_cell_on_rnc   where ( last_update_date<"+obecnyDzien+" and rnc_id='"+rnc_id+"');";
         
	this.sprzF.dopisz(req+"\r\n");
        niekomercyjneClean.add(req);
	super.executCleanCommends();
	return true;
    }
    /*{
	 try
	 {
	    
	     java.util.ArrayList<String> poleceniaNK=new java.util.ArrayList<String>();
	     String obecnyDzien="'"+sdf.format(DataDzisiaj)+"'";
	     OdpowiedzSQL komorki=getNotUpdateCandidates(DataDzisiaj, this.kontrolerName);
	    

	    for(int p=0;komorki!=null&&p<komorki.rowCount();p++)
	    {
		try
		{
		    String row_autoInc=komorki.getValue("row_autoInc", p);
		    String lte_cell_index=komorki.getValue("lte_cell_index", p);
		    niekomercyjneClean.add("delete from raport_konfiguracja_aktualna.ulte_cell_on_rnc  where row_autoInc='"+row_autoInc+"' and lte_cell_index='"+lte_cell_index+"';");
		}
		catch(Exception ee)
		{
		    loger.log(Level.FINEST,"["+this.identyfikator+" CLEANER] ERROR:" , ee);
		}
	    }
	    

	     super.executCleanCommends();
	     return true;
	}
	catch(Exception ee)
	{
	    loger.log(Level.FINEST,"["+this.identyfikator+" CLEANER] ERROR:" , ee);
	    return false;
	}
    }*/

    @Override
    public OdpowiedzSQL getNotUpdateCandidates(java.util.Date dateBefore, String kontroler)
  
    {
	String obecnyDzien="'"+withoutTimeFormat.format(dateBefore)+"'";

	try
	{
	    //testStatement = connection.createStatement();
	    //String req="select pl.* from oncall.konfiguracja_aktualna_plyty_2G pl where pl.last_update<"+obecnyDzien;
	    //String req="select pl.* from oncall.konfiguracja_aktualna_plyty_2G pl left join oncall.konfiguracja_aktualna_bts b on(b.Bts_Index=pl.ne_index) left join oncall.konfiguracja_aktualna_rnc_bsc r on (r.Rnc_Bsc_Index=b.Rnc_Bsc_Index) where(r.Rnc_Bsc_Name like '"+kontroler+"' and  pl.last_update<"+obecnyDzien+");";
	    //String req="select u.* from oncall.konfiguracja_aktualna_ucell u left join oncall.konfiguracja_aktualna_nodeb b on(b.Nodeb_Index=u.Nodeb_Index ) left join oncall.konfiguracja_aktualna_rnc_bsc r on( r.Rnc_Bsc_Index=b.Rnc_Bsc_Index ) where ( r.Rnc_Bsc_Name ='"+kontroler+"' and (  u.Last_update<"+obecnyDzien+"));";
	    String req="select u.* from raport_konfiguracja_aktualna.ulte_cell_on_rnc u  where ( u.last_update_date<curdate() and u.rnc_id='"+rnc_id+"');";
	    loger.log(Level.FINE, "["+this.identyfikator+" CLENEAR REQUEST] "+req);
	    ResultSet res=testStatement.executeQuery(req);
	    OdpowiedzSQL rnc=Baza.createAnswer(res);
	    //System.out.println(rnc);
	    return rnc;
	}
	catch(Exception ee)
	{
	    loger.log(Level.FINEST,"["+this.identyfikator+" CLEANER] ERROR:" , ee);
	    return null;
	}
    }
}