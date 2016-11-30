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
public class Ucell_perRnc_Updater extends Updater_parrent	//extends Thread{
{
    public Ucell_perRnc_Updater(String identyfikator,String rncName,int typOperacji,Logger loger,mysqlpackage.DataSource DOA,NewFile sprzF)
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
		java.util.Hashtable<String,Paczka> NBfromNorth=new java.util.Hashtable<String, Paczka>();
		String m2000_ip=rnc.getValue("M2000_Ip", 0);
		String rnc_name=rnc.getValue("Rnc_Bsc_Name", 0);
		String rnc_id=rnc.getValue("Rnc_Bsc_Id",0);
		String rnc_index=rnc.getValue("Rnc_Bsc_Index", 0);
		String m2000_Index=rnc.getValue("M2000_Index", 0);
		north=new nbipackage.NorthB(m2000_ip, "U-boot", "utranek098",null);
		this.errorInfo=this.errorInfo+"; connect with "+m2000_ip+" by NorthB";
		inLocell=north.make(this.kontrolerName, "LST UCELL:");
		inAcces=north.make(this.kontrolerName, "LST UCELLACCESSSTRICT:LSTFORMAT=HORIZONTAL");
		if(inLocell!=null&&inLocell.contains("RETCODE = 0")&&inAcces!=null&&inAcces.contains("RETCODE = 0"))
		{
		    //sukces = true;
		    System.out.println(this.identyfikator+" LST UCELL and LST UCELLACCESS wykonane");
		    this.errorInfo=this.errorInfo+"; POBRANE LST UCELL i UCELLACCESS";
		}
		else
		{
		    System.out.println(this.identyfikator+" LST UCELL and LST UCELLACCESS ERROR");
		    this.errorInfo=this.errorInfo+"; BLAD W POBIERANIU POBRANE LST UCELL i UCELLACCESS:";
		    //north.closeBuffor();
		    //north=null;
		    //connection.close();
		    sukces=false;
		}
		north.closeBuffor();
		north=null;
		this.errorInfo=this.errorInfo+"; ZAMKNIECIE NORTHB";
		String allCellReq="select u.*,n.Nodeb_Name,r.Rnc_Bsc_Name from oncall.konfiguracja_aktualna_ucell u,oncall.konfiguracja_aktualna_nodeb n, oncall.konfiguracja_aktualna_rnc_bsc r where ( u.Nodeb_Index=n.Nodeb_Index and n.Rnc_Bsc_Index="+rnc_index+" and r.Rnc_Bsc_Index="+rnc_index+")";
		ResultSet allU=testStatement.executeQuery(allCellReq);
		OdpowiedzSQL komorki=Baza.createAnswer(allU);
		this.errorInfo=this.errorInfo+"; POBRANIE KOMOREK Z DB";
		//System.out.println(komorki);


		    //1 utworzenie listy istniejacyc Cell_Index
		    //2 iteracja po cellkach z NOrthB dodanie nowej lub update jezeli istnieje(usuniecie z listy istniejacych)
		    //3 iteracja po pozostalych na liscie istniejacych( Update_Stat =Not Found

		    /*AD1) Utworzenie listy istniejacych
		     *
		     */
		    java.util.ArrayList<String> CI_in_db=new java.util.ArrayList<String>();
		    if(komorki!=null)
		    {
			for(int c=0;c<komorki.rowCount();c++)
			{
			    String cell_id=komorki.getValue("Cell_Id", c);
			    if(cell_id!=null&&!cell_id.equals("")&&!CI_in_db.contains(cell_id))
				CI_in_db.add(cell_id);
			}
		    }
		    this.errorInfo=this.errorInfo+"; UTWORZENIE CI_in_db";


		    /*AD 2)
		     Dodanie nowej lub update jezeli istnieje
		     *
		     * Odjecie z listy istniejacych
		     */


		    if(inLocell.contains("RETCODE = 0"))
		    {
			java.util.ArrayList<Paczka> ucell=new java.util.ArrayList<Paczka>();
			java.util.ArrayList<Paczka> uac=new java.util.ArrayList<Paczka>();
			java.util.Hashtable<String,Paczka> accHash=new java.util.Hashtable<String, Paczka>();

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

			try
			{
			    NPack na=new NPack(inAcces);
			    uac=na.getAllPacks();
			    this.errorInfo=this.errorInfo+"; Konwersja UCELLLACCESS -->List<Paczka>";
			}
			catch(Exception ewqr)
			{
			    loger.log(Level.FINEST, "["+this.identyfikator+"] ERROR: ", ewqr);
			}
			
			//if(false)
			if(uac!=null)
			{
			    for(int a=0;a<uac.size();a++)
			    {
			       String cellId=uac.get(a).getWartosc("Cell ID");
			       if(cellId!=null&&!cellId.equals("")&&!accHash.containsKey(cellId))
				   accHash.put(cellId, uac.get(a));
			    }
			}
			this.errorInfo=this.errorInfo+"; Konwersja UCELLLACCESS -->Hashtable<Klucz,Paczka>";
			if(ucell!=null)
			{
			    this.errorInfo=this.errorInfo+"; START for(int u=0;u<ucell.size();u++)";
			    for(int u=0;u<ucell.size();u++)
			    {
				Paczka komorka=ucell.get(u);
				if(komorka!=null)
				{
				String Cell_name=komorka.getWartosc("Cell Name");
				String locellId=komorka.getWartosc("Local Cell ID");
				String Cell_id=komorka.getWartosc("Cell Id");
				String Nodeb_Name=komorka.getWartosc("NodeB Name");

				String lac=komorka.getWartosc("Location Area Code");
				String sac=komorka.getWartosc("Service Area Code");
				String rac=komorka.getWartosc("Routing Area Code");
				String timeOffset=komorka.getWartosc("Time Offset");
				String scrCode=komorka.getWartosc("DL Primary Scrambling Code");
				String UlFreq=komorka.getWartosc("Uplink UARFCN");
				String DlFreq=komorka.getWartosc("Downlink UARFCN");
				int lacInt=-1;
				int sacInt=-1;
				int racInt=-1;
				try
				{
				    if(lac.contains("("))
				    {
					String tmp=lac.substring(lac.indexOf("(")+1,lac.indexOf(")"));
					lacInt=Integer.parseInt(tmp);



				    }
				    if(sac.contains("("))
				    {
					String tmp=sac.substring(sac.indexOf("(")+1,sac.indexOf(")"));
					sacInt=Integer.parseInt(tmp);



				    }
				    if(rac.contains("("))
				    {
					String tmp=rac.substring(rac.indexOf("(")+1,rac.indexOf(")"));
					racInt=Integer.parseInt(tmp);



				    }
				}
				catch(Exception e)
				{
				   loger.log(Level.FINEST, "["+this.identyfikator+"] ERROR: ", e);
				}
				
				if(Nodeb_Name!=null&&!Nodeb_Name.equals("")&&Cell_id!=null&&Cell_name!=null&&!Cell_name.equals(""))
				{
				    String siteName=NewFile.getTokens(Nodeb_Name, "2", "2","_");
				    String Band=ucell.get(u).getWartosc("Band Indicator");
				    String blkStat=ucell.get(u).getWartosc("Cell administrative state");
				    String actStat=ucell.get(u).getWartosc("Validation indication");
				    String barrStat="";
				    String reservStat="";
				    String updateStat="UPDATE";

				    if(Cell_id!=null&&accHash.containsKey(Cell_id))
				    {
					if(accHash.get(Cell_id)!=null)
					{
					    barrStat=accHash.get(Cell_id).getWartosc("Cell barred indicator for SIB3");
					    reservStat=accHash.get(Cell_id).getWartosc("Cell reserved for operator use");
					}
				    }
				    String insert="insert into oncall.konfiguracja_aktualna_ucell (Cell_Index, Cell_Id, Cell_Name, System, Nodeb_Index,Status_blk,Status_barr,Status_act,Status_reserv,Last_Update,Update_status,LocellId,Lac_dec,Sac_dec,Rac_dec,TimeOffset,Scr_code,Dl_freq) values((SELECT CONCAT((select nn.Nodeb_Index from oncall.konfiguracja_aktualna_nodeb nn where nn.Nodeb_Name like '%"+Nodeb_Name+"%' and nn.Rnc_Bsc_Index='"+rnc_index+"' LIMIT 1),'|"+Cell_id+"') ),"+Cell_id+",'"+Cell_name+"', '"+Band+"',(select nn.Nodeb_Index from oncall.konfiguracja_aktualna_nodeb nn where nn.Nodeb_Name like '%"+Nodeb_Name+"%' and nn.Rnc_Bsc_Index='"+rnc_index+"' LIMIT 1),'"+blkStat+"','"+barrStat+"','"+actStat+"','"+reservStat+"',"+obecnyDzienCzas+",'NEW',"+locellId+","+lacInt+","+sacInt+","+racInt+",'"+timeOffset+"','"+scrCode+"','"+DlFreq+"' )  ON DUPLICATE KEY UPDATE  Status_blk='"+blkStat+"',Status_barr='"+barrStat+"',Status_act='"+actStat+"',Cell_Name='"+Cell_name+"',Status_reserv='"+reservStat+"',Last_Update="+obecnyDzienCzas+",Update_status='"+updateStat+"',LocellId="+locellId+",Lac_dec="+lacInt+",Sysye='"+Band+"',Sac_dec="+sacInt+",Rac_dec="+racInt+",TimeOffset='"+timeOffset+"',Scr_code='"+scrCode+"',Dl_freq='"+DlFreq+"'";
				    String onAir_ins="insert into raport_konfiguracja_aktualna.cell_on_air_stat (Cell_Index,Actual_stat_act,Actual_stat_blk,Act_Update,System) values( (SELECT CONCAT((select nn.Nodeb_Index from oncall.konfiguracja_aktualna_nodeb nn where nn.Nodeb_Name like '%"+Nodeb_Name+"%' and nn.Rnc_Bsc_Index='"+rnc_index+"' LIMIT 1),'|"+Cell_id+"') ),'"+actStat+"','"+blkStat+"',now(),3)ON DUPLICATE KEY UPDATE "+
						    "Last_stat_blk=if(Act_Update<DATE_SUB(now(), INTERVAL 15 HOUR) or Last_Update is null ,Actual_stat_blk,Last_stat_blk) ,"+
						    "Last_stat_act=if(Act_Update<DATE_SUB(now(), INTERVAL 15 HOUR) or Last_Update is null,Actual_stat_act,Last_stat_act) ,"+
						    "Last_Update=if(Act_Update<DATE_SUB(now(), INTERVAL 15 HOUR) or Last_Update is null,Act_Update,Last_Update),"+
						    "Actual_stat_act='"+actStat+"',"+
						    "Actual_stat_blk='"+blkStat+"',"+
						    "Act_Update=now(),System=3";

				    
				    

				    //System.out.println(this.identyfikator+" "+u+"/"+ucell.size()+" "+insert);
				    try
				    {
					insertyAll.append(insert+";\r\n"+onAir_ins+";\r\n");
					//testStatement.execute(insert);
					
					if(Cell_id!=null&&CI_in_db.contains(Cell_id))
					    CI_in_db.remove(Cell_id);
				    }
				    catch(Exception eq)
				    {
					loger.log(Level.FINEST, "["+this.identyfikator+"] ERROR: ", eq);
				    }
				}
				//usuniecie z updatewanej komorki z listy db(po zakonczeniu pozostana na liscie tylko nie modyfikowane-prawdopodobnie juz nie istniejace
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
			for(int k=0;k<komendy.length;k++)
			{
			    try
			    {
				if(komendy[k]!=null&&!komendy[k].trim().equals(""))
				{

				    testStatement.executeUpdate(komendy[k]);
				  //  System.out.println(komendy[k]);

				}
				if(bathRoz==1000||k==(komendy.length-1))
				{
					//int[] wyniki=testStatement.executeBatch();
					//testStatement.clearBatch();
				       loger.log(Level.FINEST, "["+this.identyfikator+"] wykonanno "+k+"/"+komendy.length);
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

    public boolean clearNotUpdatet()
    {
	 try
	 {
	     java.util.ArrayList<String> poleceniaK=new java.util.ArrayList<String>();
	     java.util.ArrayList<String> poleceniaNK=new java.util.ArrayList<String>();
	     String obecnyDzien="'"+sdf.format(DataDzisiaj)+"'";
	     OdpowiedzSQL komorki=getNotUpdateCandidates(DataDzisiaj, this.kontrolerName);
	     java.util.ArrayList<String> idKomer=new java.util.ArrayList<String>();
	     ResultSet  res=testStatement.executeQuery("select Cell_Index from oncall.komercyjnosc_komorek_ucell;");
	    OdpowiedzSQL komercyjne=Baza.createAnswer(res);
	    for(int k=0;k<komercyjne.rowCount();k++)
		if(!idKomer.contains(komercyjne.getValue("Cell_Index", k)))
		    idKomer.add(komercyjne.getValue("Cell_Index", k));




	    for(int p=0;komorki!=null&&p<komorki.rowCount();p++)
	    {
		try{
		//String delSt="DELETE FROM oncall.konfiguracja_aktualna_plyty_3G WHERE oncall.konfiguracja_aktualna_plyty_3G.index = '"+plyty.getValue("index", p)+"'";
		//testStatement.executeUpdate(delSt);
		//polecenia.append(delSt+";\r\n");
		String nameDoZm=komorki.getValue("Cell_Name", p);
		String site=NewFile.getTokens(nameDoZm, "2", "2", "_");
		String cellIdDoZm=komorki.getValue("Cell_Id", p);
		String nodebIndex=komorki.getValue("Nodeb_Index", p);
		String cellIndexBef=komorki.getValue("Cell_Index", p);
		String systemBef=komorki.getValue("System", p);
		String blkBef=komorki.getValue("Status_blk", p);
		String actBef=komorki.getValue("Status_act", p);
		String barrBef=komorki.getValue("Status_barr", p);
		String resBef=komorki.getValue("Status_reserv", p);
		String updateBef=komorki.getValue("Last_Update", p);

		String duplicateReq="select u.* from oncall.konfiguracja_aktualna_ucell u where"
			+ "("
			    + "("
				+ "(	u.Cell_Id='"+cellIdDoZm+"' and u.Cell_Name='"+nameDoZm+"') or"
				+ "(	u.Cell_Id='"+cellIdDoZm+"' and u.Cell_Index='"+cellIndexBef+"') or"
				+ "(	u.Cell_Index='"+cellIndexBef+"' and u.Cell_Name='"+nameDoZm+"') or"
				+ "(	u.Cell_Id='"+cellIdDoZm+"' and u.Cell_Name like '%"+site+"%') or"
				+ "(	u.Nodeb_Index='"+nodebIndex+"' and u.Cell_Name like '%"+nameDoZm+"%')"
			    + ") and"
			    + " u.Cell_Index!='"+cellIndexBef+"' and "
			    + " u.Last_Update>'"+updateBef+"'"
			+ " );";
		res=testStatement.executeQuery(duplicateReq);
		OdpowiedzSQL duplikaty=Baza.createAnswer(res);
		//System.out.println("DUPLIKATY REKORDU:"+cellIndexBef+"("+nameDoZm+","+cellIdDoZm+","+nodebIndexDoZm+")");
		//System.out.println(duplikaty.toString()+"\r\n\r\n\\\\\\\\\\\\\\\\\\\\\\\\\\"+p);
		if(duplikaty.rowCount()>0)
		{
		    for(int d=0;d<duplikaty.rowCount();d++)
		    {
			String nameDD=duplikaty.getValue("Cell_Name", d);
			String cellIdDD=duplikaty.getValue("Cell_Id", d);
			String nodebIndexDD=duplikaty.getValue("Nodeb_Index", d);
			String cellIndexDD=duplikaty.getValue("Cell_Index", d);
			String systemDD=duplikaty.getValue("System", d);
			String blkDD=duplikaty.getValue("Status_blk", d);
			String actDD=duplikaty.getValue("Status_act", d);
			String barrDD=duplikaty.getValue("Status_barr", d);
			String resDD=duplikaty.getValue("Status_reserv", d);

			String updateReq="UPDATE IGNORE oncall.konfiguracja_aktualna_ucell SET Update_status='UPDATE:join "+cellIndexBef+"' ,Last_Update="+obecnyDzien+" where(Cell_Index='"+cellIndexDD+"');";


		       // String updateCellOnAirStat="##ON_AIR##UPDATE IGNORE raport_konfiguracja_aktualna.cell_on_air_stat SET Cell_Index='"+cellIndexDD+"' where(Cell_Index='"+cellIndexBef+"')";

			String updateCellOnAirStat="##ON_AIR##UPDATE IGNORE raport_konfiguracja_aktualna.cell_on_air_stat  as a inner join ("+
								    "select * from raport_konfiguracja_aktualna.cell_on_air_stat"+
									"where Cell_Index='"+cellIndexBef+"') as b on a.Cell_Index='"+cellIndexDD+"' and b.Cell_Index='"+cellIndexBef+"' set a.Last_stat_blk:=b.Actual_stat_blk, a.Last_Update:=b.Act_Update;";

			String rmvOldCellOnAirStat="##ON_AIR##DELETE FROM raport_konfiguracja_aktualna.cell_on_air_stat where (Cell_Index='"+cellIndexBef+"');";
			String rmvOldReq="DELETE FROM oncall.konfiguracja_aktualna_ucell where (Cell_Index='"+cellIndexBef+"');";
			String updateKomer="UPDATE IGNORE oncall.komercyjnosc_komorek_ucell SET Cell_Id='"+cellIdDD+"', Cell_Index='"+cellIndexDD+"', Cell_Name='"+nameDD+"', Nodeb_Index='"+nodebIndexDD+"', Update_Status='CHANGE' where Cell_Index='"+cellIndexBef+"';";
			String updatePlyty3G="UPDATE IGNORE oncall.konfiguracja_aktualna_plyty_3G p SET p.ne_Index='"+nodebIndexDD+"' , p.Update_Status='CHANGE NE_ID',p.Last_Update="+obecnyDzien+", p.index=replace(p.index,'"+cellIndexBef+"|','"+nodebIndexDD+"|') where p.ne_Index='"+cellIndexBef+"';";
			if(idKomer.contains(cellIndexBef))
			{
			    poleceniaNK.add("##########SCALENIE KOMERCYJNEGO DUPLIKATU############");
			    poleceniaNK.add(updateReq);
			    poleceniaNK.add(rmvOldReq);
			    poleceniaNK.add(updateKomer);
			    poleceniaNK.add(updatePlyty3G);
			    poleceniaNK.add(updateCellOnAirStat);
			    poleceniaNK.add(rmvOldCellOnAirStat);
			    poleceniaNK.add("\r\n");
			}
			else
			{
			    poleceniaNK.add(updateReq);
			    poleceniaNK.add(rmvOldReq);
			    poleceniaNK.add(updatePlyty3G);
			    poleceniaNK.add(updateCellOnAirStat);
			    poleceniaNK.add(rmvOldCellOnAirStat);
			    poleceniaNK.add("\r\n");
			    //	polecenia.append(updateReq+";\r\n"+rmvOldReq+";\r\n"+updatePlyty3G+";\r\n"+updateCellOnAirStat+";\r\n"+rmvOldCellOnAirStat+";\r\n");
			}
		    }
		}
		else
		{
		    String komerToHist="insert into  raport_komercyjnosc_komorek.`raport_komercyjnosci_komorek.historyczne_komorki`(CellName,System,Band,Data_komercjalizacji,Data_dekomercjalizacji) values((select u.Cell_Name from oncall.komercyjnosc_komorek_ucell u where u.Cell_Index='"+cellIndexBef+"'),'3G',(select u.System from oncall.komercyjnosc_komorek_ucell u where u.Cell_Index='"+cellIndexBef+"'),(select u.Last_Update from oncall.komercyjnosc_komorek_ucell u where u.Cell_Index='"+cellIndexBef+"'),now() );";
		    String dekomerReq="DELETE FROM oncall.komercyjnosc_komorek_ucell where (Cell_Index='"+cellIndexBef+"');";
		    String rmvOldReq="DELETE FROM oncall.konfiguracja_aktualna_ucell where (Cell_Index='"+cellIndexBef+"');";
		    if(idKomer.contains(cellIndexBef))
		    {
			poleceniaK.add(komerToHist);
			poleceniaK.add(dekomerReq);
			poleceniaK.add(rmvOldReq);
			poleceniaK.add("\r\n");

		    }
		    else
		    {
			poleceniaNK.add(rmvOldReq);
			poleceniaNK.add("\r\n");
		    }
		}
	     }
	    catch(Exception ee)
	    {
		loger.log(Level.FINEST,"["+this.identyfikator+" CLEANER] ERROR:" , ee);
	    }
	    }
	    komercyjneClean.addAll(poleceniaK);
	   // komercyjneClean.add("#####NIEKOMERCYJNE(TESTOWO W KOMERCYJNYCH)");
	    niekomercyjneClean.addAll(poleceniaNK);

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
	    //testStatement = connection.createStatement();
	    //String req="select pl.* from oncall.konfiguracja_aktualna_plyty_2G pl where pl.last_update<"+obecnyDzien;
	    //String req="select pl.* from oncall.konfiguracja_aktualna_plyty_2G pl left join oncall.konfiguracja_aktualna_bts b on(b.Bts_Index=pl.ne_index) left join oncall.konfiguracja_aktualna_rnc_bsc r on (r.Rnc_Bsc_Index=b.Rnc_Bsc_Index) where(r.Rnc_Bsc_Name like '"+kontroler+"' and  pl.last_update<"+obecnyDzien+");";
	    //String req="select u.* from oncall.konfiguracja_aktualna_ucell u left join oncall.konfiguracja_aktualna_nodeb b on(b.Nodeb_Index=u.Nodeb_Index ) left join oncall.konfiguracja_aktualna_rnc_bsc r on( r.Rnc_Bsc_Index=b.Rnc_Bsc_Index ) where ( r.Rnc_Bsc_Name ='"+kontroler+"' and (  u.Last_update<"+obecnyDzien+"));";
	    String req="select u.* from oncall.konfiguracja_aktualna_ucell u  where (  (  u.Last_update<"+obecnyDzien+")  and u.Cell_Index like (select concat((select r.Rnc_Bsc_Index from oncall.konfiguracja_aktualna_rnc_bsc r where r.Rnc_Bsc_Name='"+kontroler+"' LIMIT 1),'|%'))  );";
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

    /*public String getReadyForCoaStat(NorthB north, String node_name)
    {
	String status="";
	try
	{
	    String odp=north.make(node_name, "DSP BRD:");
	    //System.out.println(odp);
	    if(odp.contains("RETCODE = 0"))
	    {
		NPack nn = new NPack(odp);
		java.util.ArrayList<Paczka> plyty=nn.getAllPacks();
		boolean lbbp=false;
		for(int p=0;p<plyty.size();p++)
		{
		    if(plyty.get(p).getWartosc("Config Type").equalsIgnoreCase("LBBP"))
		    {
			String OpStat=plyty.get(p).getWartosc("Operational State").trim();
			String AvStat=plyty.get(p).getWartosc("Availability Status").trim();

			String klucz=plyty.get(p).getWartosc("Cabinet No.")+","+plyty.get(p).getWartosc("Subrack No.")+","+plyty.get(p).getWartosc("Slot No.")+";"+OpStat+","+AvStat;
			if(OpStat.equalsIgnoreCase("Enabled")&&AvStat.equalsIgnoreCase("Normal"))
			{

			    return "T:"+klucz;
			}
			else
			{
			    status=status+klucz+"|";
			}
			lbbp=true;
		    }
		}
		if(lbbp)
		    return "F:"+status;
		else
		    return "F:BRAK_LBBP";
	    }
	    else
	    {
		System.err.println(odp);
		if(odp.contains("RETCODE"))
		    return "F:"+NewFile.getLinia("RETCODE", odp)[0];
		else
		    return "E:NBI_ERROR";
	    }
	}
	catch(Exception ee)
	{
	    System.out.println("["+this.identyfikator+"]"+ee.toString());
	    ee.printStackTrace();
	    return "E:"+ee.toString();
	}

    }*/
}