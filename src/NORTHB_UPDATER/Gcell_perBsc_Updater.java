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
public class Gcell_perBsc_Updater extends Updater_parrent	//extends Thread{
{
    public Gcell_perBsc_Updater(String identyfikator,String bscName,int typOperacji,Logger loger,mysqlpackage.DataSource DOA,NewFile sprzF)
    {
	super(identyfikator,bscName,typOperacji,loger,DOA,sprzF);
    }

    @Override
    public boolean add() throws java.sql.SQLException
    {
	sukces=false;
	String obecnyDzienCzas="'"+sdf.format(DataDzisiaj)+"'";
	String lstGcell="";
	String lstIdl="";
	StringBuffer insertyAll=new StringBuffer();
	try
	{
	
	    this.errorInfo=this.errorInfo+"; DOA.connected,createStatement";
	    String req="select r.Rnc_Bsc_Index, r.Rnc_Bsc_Id, r.Rnc_Bsc_Name, m.M2000_Ip, r.M2000_Index from oncall.konfiguracja_aktualna_rnc_bsc r,  oncall.konfiguracja_aktualna_m2000 m where(r.m2000_index=m.M2000_Index and r.Rnc_Bsc_Name like '%"+this.kontrolerName+"%') LIMIT 1";
	    ResultSet res=testStatement.executeQuery(req);
	    OdpowiedzSQL rnc=Baza.createAnswer(res);
	    ResultSet allBts=testStatement.executeQuery("select * from oncall.konfiguracja_aktualna_bts where oncall.konfiguracja_aktualna_bts.Rnc_Bsc_Index = (select Rnc_Bsc_Index from oncall.konfiguracja_aktualna_rnc_bsc where Rnc_Bsc_Name like '%"+this.kontrolerName+"%' LIMIT 1) ");
	    OdpowiedzSQL btsy=Baza.createAnswer(allBts);
	    System.out.println("BEGIN "+this.identyfikator);

	    this.errorInfo=this.errorInfo+"; get M2000 info for bsc="+this.kontrolerName;
	    if(rnc.rowCount()>0)
	    {
		java.util.Hashtable<String,Paczka> BtsfromNorth=new java.util.Hashtable<String, Paczka>();
		String m2000_ip=rnc.getValue("M2000_Ip", 0);
		String bsc_name=rnc.getValue("Rnc_Bsc_Name", 0);
		String bsc_id=rnc.getValue("Rnc_Bsc_Id",0);
		String bsc_index=rnc.getValue("Rnc_Bsc_Index", 0);
		String m2000_Index=rnc.getValue("M2000_Index", 0);
		north=new nbipackage.NorthB(m2000_ip, "U-boot", "utranek098",null);
		this.errorInfo=this.errorInfo+"; connect with "+m2000_ip+" by NorthB";
		lstGcell=north.make(this.kontrolerName, "LST GCELL:");
		lstIdl=north.make(this.kontrolerName, "LST GCELLIDLEBASIC:");
		if(lstGcell!=null&&lstGcell.contains("RETCODE = 0")&&lstIdl!=null&&lstIdl.contains("RETCODE = 0"))
		{
		    System.out.println(this.identyfikator+" LST GCELL and LST GCELIDL wykonane");
		    this.errorInfo=this.errorInfo+"; POBRANE LST gCELL i GCELLIDL";
		}
		else
		{
		    this.errorInfo=this.errorInfo+"; BLAD W POBIERANIU POBRANE LST GCELL i GCELLIDL:";
		    north.closeBuffor();
		    north=null;
		    sukces=false;
		}
		north.closeBuffor();
		north=null;
		this.errorInfo=this.errorInfo+"; ZAMKNIECIE NORTHB";
		String allCellReq="select g.*,b.Bts_Name,r.Rnc_Bsc_Name from oncall.konfiguracja_aktualna_gcell g,oncall.konfiguracja_aktualna_bts b, oncall.konfiguracja_aktualna_rnc_bsc r where ( g.Bts_Index=b.Bts_Index and b.Rnc_Bsc_Index="+bsc_index+" and r.Rnc_Bsc_Index="+bsc_index+")";
		ResultSet allU=testStatement.executeQuery(allCellReq);
		OdpowiedzSQL komorki=Baza.createAnswer(allU);
		this.errorInfo=this.errorInfo+"; POBRANIE KOMOREK Z DB";

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

		if(lstGcell.contains("RETCODE = 0"))
		{
		    java.util.ArrayList<Paczka> gcell=new java.util.ArrayList<Paczka>();
		    java.util.ArrayList<Paczka> gidl=new java.util.ArrayList<Paczka>();
		    java.util.Hashtable<String,Paczka> gidlHash=new java.util.Hashtable<String, Paczka>();
		    try
		    {
			NPack nn=new NPack(lstGcell);
			gcell=nn.getAllPacks();
			this.errorInfo=this.errorInfo+"; Konwersja GCELL -->List<Paczka>";
		    }
		    catch(Exception ewqr)
		    {
			loger.log(Level.FINEST, "["+this.identyfikator+"] ERROR", ewqr);
		    }
		    try
		    {
			NPack na=new NPack(lstIdl);
			gidl=na.getAllPacks();
			this.errorInfo=this.errorInfo+"; Konwersja GCELLIDL -->List<Paczka>";
		    }
		    catch(Exception ewqr)
		    {
			loger.log(Level.FINEST, "["+this.identyfikator+"] ERROR", ewqr);
		    }
		    if(gidl!=null)
		    {
			for(int a=0;a<gidl.size();a++)
			{
			    String cellId=gidl.get(a).getWartosc("Cell Index");
			    if(cellId!=null&&!cellId.equals("")&&!gidlHash.containsKey(cellId))
				gidlHash.put(cellId, gidl.get(a));
			}
		    }
		    this.errorInfo=this.errorInfo+"; Konwersja GCELLIDL -->Hashtable<Klucz,Paczka>";
		    if(gcell!=null)
		    {
			this.errorInfo=this.errorInfo+"; START for(int u=0;u<gcell.size();u++)";
			for(int u=0;u<gcell.size();u++)
			{
			    Paczka komorka=gcell.get(u);
			    if(komorka!=null)
			    {
				String Cell_name=komorka.getWartosc("Cell Name");
				String Cell_id=komorka.getWartosc("Cell Index");
				String Bts_Id=komorka.getWartosc("BTS Index");
				if(Bts_Id!=null&&!Bts_Id.equals("")&&Cell_id!=null&&Cell_name!=null&&!Cell_name.equals(""))
				{
				    String Band=gcell.get(u).getWartosc("Freq. Band");
				    String blkStat=gcell.get(u).getWartosc("Administrative State");
				    String actStat=gcell.get(u).getWartosc("active status");
				    String barrStat="";
				    String reservStat="NOT_USED";
				    String updateStat="UPDATE";
				    String Bts_Index=bsc_index+"|"+Bts_Id;
				    String cell_index=Bts_Index+"|"+Cell_id;

				    String lac=gcell.get(u).getWartosc("Cell LAC");
				    String cellCi=gcell.get(u).getWartosc("Cell CI");

				    int lacInt=-1;
				    int cellCiInt=-1;
				    int racInt=-1;
				    try
				    {
					if(lac.contains("("))
					{
					    String tmp=lac.substring(lac.indexOf("(")+1,lac.indexOf(")"));
					    lacInt=Integer.parseInt(tmp);
					}
					if(cellCi.contains("("))
					{
					    String tmp=cellCi.substring(cellCi.indexOf("(")+1,cellCi.indexOf(")"));
					    cellCiInt=Integer.parseInt(tmp);
					}
				    }
				    catch(Exception e)
				    {
					loger.log(Level.FINEST, "["+this.identyfikator+"]ERROR:", e);
				    }


				    if(Cell_id!=null&&gidlHash.containsKey(Cell_id))
				    {
					if(gidlHash.get(Cell_id)!=null)
					{
					    barrStat=gidlHash.get(Cell_id).getWartosc("Cell Bar Access");
					}
				    }
				    String insert="insert into oncall.konfiguracja_aktualna_gcell (Cell_Index, Cell_Id, Cell_Name, System, Bts_Index,Status_blk,Status_barr,Status_act,Status_reserv,Last_Update,Update_status,Lac_dec,Ci_dec) values('"+cell_index+"',"+Cell_id+",'"+Cell_name+"', '"+Band+"','"+Bts_Index+"','"+blkStat+"','"+barrStat+"','"+actStat+"','"+reservStat+"',"+obecnyDzienCzas+",'NEW',"+lacInt+","+cellCiInt+" )  ON DUPLICATE KEY UPDATE  Status_blk='"+blkStat+"',Status_barr='"+barrStat+"',Status_act='"+actStat+"',Status_reserv='"+reservStat+"',Last_Update="+obecnyDzienCzas+",Update_status='"+updateStat+"',Cell_Name='"+Cell_name+"',Lac_dec="+lacInt+",Ci_dec="+cellCiInt+",System='"+Band+"'";
				    String onAir_ins="insert into raport_konfiguracja_aktualna.cell_on_air_stat  (Cell_Index,Actual_stat_act,Actual_stat_blk,Act_Update,System) values( '"+cell_index+"','"+actStat+"','"+blkStat+"',now(),2)ON DUPLICATE KEY UPDATE "+
							"Last_stat_blk=if(Act_Update<DATE_SUB(now(), INTERVAL 15 HOUR) or Last_Update is null ,Actual_stat_blk,Last_stat_blk) ,"+
							"Last_stat_act=if(Act_Update<DATE_SUB(now(), INTERVAL 15 HOUR) or Last_Update is null,Actual_stat_act,Last_stat_act) ,"+
							"Last_Update=if(Act_Update<DATE_SUB(now(), INTERVAL 15 HOUR) or Last_Update is null,Act_Update,Last_Update),"+
							"Actual_stat_act='"+actStat+"',"+
							"Actual_stat_blk='"+blkStat+"',"+
							"Act_Update=now(),System=2";
				    /*
				     *		    insert into raport_konfiguracja_aktualna.cell_on_air_stat  (Cell_Index,Actual_stat_act,Actual_stat_blk,Act_Update,System) values( '7|256|1559','testActStatN2X#X','testBLKN2X#X',now(),2)ON DUPLICATE KEY UPDATE
							Last_stat_blk=if(Last_Update<DATE_SUB(now(), INTERVAL 23 HOUR) or Last_Update is null ,Actual_stat_blk,Last_stat_blk) ,
							Last_stat_act=if(Last_Update<DATE_SUB(now(), INTERVAL 23 HOUR) or Last_Update is null,Actual_stat_act,Last_stat_act) ,
							Last_Update=if(Last_Update<DATE_SUB(now(), INTERVAL 23 HOUR) or Last_Update is null,Act_Update,Last_Update),
							Actual_stat_act='testActStatN2X#X',
							Actual_stat_blk='testBLKN2X#X',
							Act_Update=now(),System=2;

				     */
				    try
				    {
					insertyAll.append(insert+";\r\n"+onAir_ins+";\r\n");

					if(Cell_id!=null&&CI_in_db.contains(Cell_id))
					    CI_in_db.remove(Cell_id);
				    }
				    catch(Exception eq)
				    {
					loger.log(Level.FINEST, "["+this.identyfikator+"]ERROR:", eq);
				    }
				}
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
				    loger.log(Level.FINEST, "["+this.identyfikator+"]ERROR:"+komendy[k]+" ", ex);
				}
				catch (Exception ex)
				{
				    loger.log(Level.FINEST, "["+this.identyfikator+"]ERROR:"+komendy[k]+" ", ex);
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
			loger.log(Level.FINEST, "["+this.identyfikator+"]ERROR:", batchExc);
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
	     OdpowiedzSQL komorki=getNotUpdateCandidates(DataDzisiaj, this.kontrolerName);
	     java.util.ArrayList<String> idKomer=new java.util.ArrayList<String>();
	     ResultSet  res=testStatement.executeQuery("select Cell_Index from oncall.komercyjnosc_komorek_gcell;");
	     OdpowiedzSQL komercyjne=Baza.createAnswer(res);
	     for(int k=0;k<komercyjne.rowCount();k++)
		 if(!idKomer.contains(komercyjne.getValue("Cell_Index", k)))
		     idKomer.add(komercyjne.getValue("Cell_Index", k));

	     for(int p=0;p<komorki.rowCount();p++)
	     {
		 //String delSt="DELETE FROM oncall.konfiguracja_aktualna_plyty_3G WHERE oncall.konfiguracja_aktualna_plyty_3G.index = '"+plyty.getValue("index", p)+"'";
		 //testStatement.executeUpdate(delSt);
		 //polecenia.append(delSt+";\r\n");
		 String nameBef=komorki.getValue("Cell_Name", p);
		 String cellIdBef=komorki.getValue("Cell_Id", p);
		 String btsIndexBef=komorki.getValue("Bts_Index", p);
		 String cellIndexBef=komorki.getValue("Cell_Index", p);
		 String systemBef=komorki.getValue("System", p);
		 String blkBef=komorki.getValue("Status_blk", p);
		 String actBef=komorki.getValue("Status_act", p);
		 String barrBef=komorki.getValue("Status_barr", p);
		 String resBef=komorki.getValue("Status_reserv", p);
		 String updateBef=komorki.getValue("Last_Update", p);
		 String site=NewFile.getTokens(nameBef, "2", "2", "_");
		 if(site.contains(""))
		    site=nameBef;

		String duplicateReq="select g.* from oncall.konfiguracja_aktualna_gcell g where"
			+ "("
			    + "("
				+ "(	g.Cell_Id='"+cellIdBef+"' and g.Cell_Name like '%"+site+"%') or"
				+ "(	g.Cell_Name like '%"+nameBef+"%') "
			    + ") and"
			    + " g.Cell_Index!='"+cellIndexBef+"' and "
			    + " g.Last_Update>'"+updateBef+"'"
			+ " );";


		res=testStatement.executeQuery(duplicateReq);
		OdpowiedzSQL duplikaty=Baza.createAnswer(res);
		System.out.println("DUPLIKATY REKORDU:"+cellIndexBef+"("+nameBef+","+cellIdBef+","+btsIndexBef+")\r\n"+duplikaty);
		//System.out.println(duplikaty.toString()+"\r\n\r\n\\\\\\\\\\\\\\\\\\\\\\\\\\"+p);
		if(duplikaty.rowCount()>0)
		{
		    for(int d=0;d<duplikaty.rowCount();d++)
		    {
			String nameNew=duplikaty.getValue("Cell_Name", d);
			String cellIdNew=duplikaty.getValue("Cell_Id", d);
			String btsIndexNew=duplikaty.getValue("Bts_Index", d);
			String cellIndexNew=duplikaty.getValue("Cell_Index", d);
			String systemNew=duplikaty.getValue("System", d);
			String blkNew=duplikaty.getValue("Status_blk", d);
			String actNew=duplikaty.getValue("Status_act", d);
			String barrNew=duplikaty.getValue("Status_barr", d);
			String resNew=duplikaty.getValue("Status_reserv", d);

			String updateReq="UPDATE  IGNORE oncall.konfiguracja_aktualna_gcell SET Update_status='UPDATE:join "+cellIndexBef+"' ,Last_Update="+obecnyDzien+" where(Cell_Index='"+cellIndexNew+"');";
			String rmvOldReq="DELETE FROM oncall.konfiguracja_aktualna_gcell where (Cell_Index='"+cellIndexBef+"');";

			String updateKomer="UPDATE IGNORE oncall.komercyjnosc_komorek_gcell SET Cell_Id='"+cellIdNew+"', Cell_Index='"+cellIndexNew+"', Cell_Name='"+nameNew+"', Bts_Index='"+btsIndexNew+"', Update_Status='CHANGE' where Cell_Index='"+cellIndexBef+"';";
			String updatePlyty2G="UPDATE   IGNORE oncall.konfiguracja_aktualna_plyty_2G SET ne_Index='"+btsIndexNew+"' , Update_Status='CHANGE NE_ID',Last_Update="+obecnyDzien+", oncall.konfiguracja_aktualna_plyty_2G.index=replace(oncall.konfiguracja_aktualna_plyty_2G.index,'"+btsIndexBef+"|','"+btsIndexNew+"|') where ne_Index='"+btsIndexBef+"';";
			if(idKomer.contains(cellIndexBef))
			{
			    poleceniaNK.add("#########SCALENIE KOMERCYJNEGO DUPLIKATU#######");
			    poleceniaNK.add(updateReq);
			    poleceniaNK.add(rmvOldReq);
			    poleceniaNK.add(updateKomer);
			    poleceniaNK.add(updatePlyty2G);
			    poleceniaNK.add("\r\n");

			    //poleceniaKomer.append(+";\r\n"+rmvOldReq+";\r\n"+updateKomer+";\r\n"+updatePlyty2G+";\r\n\r\n");

			}
			else
			{
			    poleceniaNK.add(updateReq);
			    poleceniaNK.add(rmvOldReq);
			    poleceniaNK.add(updatePlyty2G);
			    poleceniaNK.add("\r\n");
			   // polecenia.append(updateReq+";\r\n"+rmvOldReq+";\r\n"+updatePlyty2G+";\r\n\r\n");//+updateKomer+";\r\n");
			}
		    }
		}
		else
		{
		    String rmvOldReq="DELETE FROM oncall.konfiguracja_aktualna_gcell where (Cell_Index='"+cellIndexBef+"');";
		    String komer2Hist="insert into  raport_komercyjnosc_komorek.`raport_komercyjnosci_komorek.historyczne_komorki`(CellName,System,Band,Data_komercjalizacji,Data_dekomercjalizacji) values((select g.Cell_Name from oncall.komercyjnosc_komorek_gcell g where g.Cell_Index='"+cellIndexBef+"'),'2G',(select g.System from oncall.komercyjnosc_komorek_gcell g where g.Cell_Index='"+cellIndexBef+"'),(select g.Last_Update from oncall.komercyjnosc_komorek_gcell g where g.Cell_Index='"+cellIndexBef+"'),now() );";
		    String rmvOldKomer="DELETE FROM oncall.komercyjnosc_komorek_gcell where (Cell_Index='"+cellIndexBef+"');";
		    if(idKomer.contains(cellIndexBef))
		    {
			poleceniaK.add(komer2Hist);
			poleceniaK.add(rmvOldKomer);
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
	    komercyjneClean.addAll(poleceniaK);
	    //komercyjneClean.add("NIEKOMERCYJNE(TESTOWO W KOMERCYJNYCH)");
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
	    String req="select g.* from oncall.konfiguracja_aktualna_gcell g left join oncall.konfiguracja_aktualna_bts bt on (g.Bts_Index=bt.Bts_Index) left join oncall.konfiguracja_aktualna_rnc_bsc r on (r.Rnc_Bsc_Index=bt.Rnc_Bsc_Index) where(r.Rnc_Bsc_Name like '"+kontroler+"' and  g.last_update<"+obecnyDzien+");";
	    loger.log(Level.FINEST, "["+this.identyfikator+" CLEANER REQUEST]"+req);
	    
	    ResultSet res=testStatement.executeQuery(req);
	    OdpowiedzSQL rnc=Baza.createAnswer(res);
	    return rnc;
	}
	catch(Exception ee)
	{
	    loger.log(Level.FINEST,"["+this.identyfikator+" CLEANER] ERROR:" , ee);
	    return null;
	}
    }
}

  /*  public String getReadyForCoaStat(NorthB north, String node_name)
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

    }

    public String odp()
    {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isDone()
    {
	return this.done;
    }

    public void setDOA(mysqlpackage.DataSource DOA)
    {
	this.DOA=DOA;
    }

    private String readyForCoaStat(NorthB north,String nodeName)
    {
	return "NOT_READY";
    }

    boolean done;*/