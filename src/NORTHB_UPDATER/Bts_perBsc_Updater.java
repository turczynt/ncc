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
public class Bts_perBsc_Updater extends Updater_parrent		//extends Thread{
{
    public Bts_perBsc_Updater(String identyfikator,String bscName,int typOperacji,Logger loger,mysqlpackage.DataSource DOA,NewFile sprzF)
    {
	super(identyfikator,bscName,typOperacji,loger,DOA,sprzF);
    }

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
	    ResultSet allBts=testStatement.executeQuery("select * from oncall.konfiguracja_aktualna_bts where oncall.konfiguracja_aktualna_bts.Rnc_Bsc_Index = (select Rnc_Bsc_Index from oncall.konfiguracja_aktualna_rnc_bsc where Rnc_Bsc_Name like '%"+this.kontrolerName+"%' LIMIT 1) ");
	    OdpowiedzSQL btsy=Baza.createAnswer(allBts);
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
		lstBts=north.make(this.kontrolerName, "LST BTS:");

		if(lstBts!=null&&lstBts.contains("RETCODE = 0"))
		{
		    //sukces = true;
		    System.out.println(this.identyfikator+" LST BTS wykonane");
		    this.errorInfo=this.errorInfo+"; POBRANE LST BTS";
		}
		else
		{
		    System.out.println(this.identyfikator+" LST BTS ERROR");

		    this.errorInfo=this.errorInfo+"; BLAD W POBIERANIU POBRANE LST BTS:";
		    sukces=false;

		}
		north.closeBuffor();
		north=null;
		this.errorInfo=this.errorInfo+"; ZAMKNIECIE NORTHB";
		this.errorInfo=this.errorInfo+"; POBRANIE KOMOREK Z DB";
		java.util.ArrayList<String> Bts_id_in_db=new java.util.ArrayList<String>();
		if(btsy!=null)
		{
		    for(int c=0;c<btsy.rowCount();c++)
		    {
			String bts_id=btsy.getValue("Bts_Id", c);
			if(bts_id!=null&&!bts_id.equals("")&&!Bts_id_in_db.contains(bts_id))
			    Bts_id_in_db.add(bts_id);
		    }
		}
		this.errorInfo=this.errorInfo+"; UTWORZENIE Bts_id_in_db";

		if(lstBts.contains("RETCODE = 0"))
		{
		    java.util.ArrayList<Paczka> bts=new java.util.ArrayList<Paczka>();
		    try
		    {
			NPack nn=new NPack(lstBts);
			bts=nn.getAllPacks();
			this.errorInfo=this.errorInfo+"; Konwersja LST BTS -->List<Paczka>";
		    }
		    catch(Exception ewqr)
		    {
			System.err.println("WYJEBALO przy NPack LST BTS");
			loger.log(Level.FINEST, "["+this.identyfikator+"]ERROR ", ewqr);
		    }
		    if(bts!=null)
		    {

			this.errorInfo=this.errorInfo+"; START for(int u=0;u<bts.size();u++)";
			for(int u=0;u<bts.size();u++)
			{
			    Paczka komorka=bts.get(u);
			    if(komorka!=null)
			    {
				String Bts_name=komorka.getWartosc("BTS Name");
				String Bts_Id=komorka.getWartosc("BTS Index");
				if(Bts_Id!=null&&!Bts_Id.equals("")&&Bts_name!=null&&!Bts_name.equals(""))
				{
				    String Bts_Index=bsc_index+"|"+Bts_Id;
				    String siteName="";
				    if(Bts_name.contains("_"))
					siteName=Bts_name.split("_")[1];
				    else
				    {
					if(Bts_name.length()>8)
					    siteName=Bts_name.substring(0, 8);
					else
					    siteName=Bts_name;
				    }

				    String region="";
				    region=Bts_name.substring(0, 1);
				    if(!region.matches("[1-4]"))
				    {
					if(bsc_name.contains("WAW"))
					    region="1";
					else if(bsc_name.contains("KAT"))
					    region="2";
					else if(bsc_name.contains("POZ"))
					    region="3";
					else if(bsc_name.contains("GDA"))
					    region="4";
				    }
				    if(!region.matches("[1-4]"))
					region="0";

				    String update_stat="NEW";
				    testStatement.execute("insert into oncall.konfiguracja_aktualna_site(Site_Index, M2000_Index, Site_Name, Last_Update, Update_Status) VALUES(NULL,'"+m2000_Index+"','"+siteName+"',"+obecnyDzienCzas+",'"+update_stat+"') ON DUPLICATE KEY UPDATE Last_Update="+obecnyDzienCzas+", Update_Status='OK', M2000_Index='"+m2000_Index+"';");
				    String insert="insert into oncall.konfiguracja_aktualna_bts (Bts_Index, Rnc_Bsc_Index, Site_Index,Bts_Id,Bts_Name,gsm,dcs,System,Last_Update,Update_Status,Region) values('"+Bts_Index+"', '"+bsc_index+"', (select Site_Index from oncall.konfiguracja_aktualna_site where Site_Name like '%"+siteName+"%' LIMIT 1),"+Bts_Id+",'"+Bts_name+"', (select count((select gc.cell_id from oncall.konfiguracja_aktualna_gcell gc where(gc.Bts_Index='"+Bts_Index+"' and gc.System like '%GSM%') LIMIT 1))),(select count((select gc.cell_id from oncall.konfiguracja_aktualna_gcell gc where(gc.Bts_Index='"+Bts_Index+"' and gc.System like '%DCS%') LIMIT 1))),'System',"+obecnyDzienCzas+",'NEW',"+region+") ON DUPLICATE KEY UPDATE Last_Update="+obecnyDzienCzas+",Bts_Name='"+Bts_name+"',Update_Status='UPDATE',gsm=(select count((select gc.cell_id from oncall.konfiguracja_aktualna_gcell gc where(gc.Bts_Index='"+Bts_Index+"' and gc.System like '%GSM%') LIMIT 1))),dcs=(select count((select gc.cell_id from oncall.konfiguracja_aktualna_gcell gc where(gc.Bts_Index='"+Bts_Index+"' and gc.System like '%DCS%') LIMIT 1)))";

				    try
				    {
					insertyAll.append(insert+";\r\n");
					if(Bts_Id!=null&&Bts_id_in_db.contains(Bts_Id))
					    Bts_id_in_db.remove(Bts_Id);
				    }
				    catch(Exception eq)
				    {
					loger.log(Level.FINEST, "["+this.identyfikator+"]ERROR ", eq);
		
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
	     java.util.ArrayList<String> idKomer=new java.util.ArrayList<String>();
	     ResultSet  res=testStatement.executeQuery("select Bts_Index from oncall.komercyjnosc_komorek_gcell;");
	     OdpowiedzSQL komercyjne=Baza.createAnswer(res);
	     for(int k=0;k<komercyjne.rowCount();k++)
		 if(!idKomer.contains(komercyjne.getValue("Bts_Index", k)))
		     idKomer.add(komercyjne.getValue("Bts_Index", k));

	    for(int p=0;p<btsy.rowCount();p++)
	    {
		//String delSt="DELETE FROM oncall.konfiguracja_aktualna_plyty_3G WHERE oncall.konfiguracja_aktualna_plyty_3G.index = '"+plyty.getValue("index", p)+"'";
		//testStatement.executeUpdate(delSt);
		//polecenia.append(delSt+";\r\n");
		String nameBef=btsy.getValue("Bts_Name", p);
		//System.out.println("Check:"+nameDoZm);
		String indexBef=btsy.getValue("Bts_Index", p);
		String updateBef=btsy.getValue("Last_update", p);
		String idBef=btsy.getValue("Bts_Id", p);
		String site=NewFile.getTokens(nameBef, "2", "2", "_");
		String duplicateReq="select bt.* from oncall.konfiguracja_aktualna_bts bt where"
			+ "("
			    + "("
				+ "(	bt.Bts_Name='"+nameBef+"')"
			    + ")"
			    + " and bt.Bts_Index!='"+indexBef+"'"
			+ " );";
		//System.out.println("REQUEST DUP:"+duplicateReq);
		res=testStatement.executeQuery(duplicateReq);
		OdpowiedzSQL duplikaty=Baza.createAnswer(res);
		if(duplikaty.rowCount()>0)
		{
		    for(int d=0;d<duplikaty.rowCount();d++)
		    {
			System.out.println("duplikaty dla: "+nameBef+" "+indexBef+" "+updateBef+"\r\n"+duplikaty.toString());
			String indexNew=duplikaty.getValue("Bts_Index", d);
			String nameNew=duplikaty.getValue("Bts_Name", d);
			String idNew=duplikaty.getValue("Bts_Id", d);


		        String updateReq="UPDATE  IGNORE oncall.konfiguracja_aktualna_bts SET Update_status='UPDATE:join "+indexBef+"' ,Last_Update="+obecnyDzien+" ";

			updateReq=updateReq+"where(Bts_index='"+indexNew+"')";

			String rmvOldReq="DELETE FROM oncall.konfiguracja_aktualna_bts where (Bts_Index='"+indexBef+"')";
			//String decomerOldReq="UPDATE IGNORE oncall.komercyjnosc_komorek_gcell SET Status_komercyjnosci='0',Last_Update="+obecnyDzien+" where (Bts_Index='"+indexBef+"')";
			java.util.ArrayList<String> checkDownKomm=checkDownBts(indexBef, indexNew,obecnyDzien);
			if(idKomer.contains(indexBef))
			{
			    poleceniaNK.add(updateReq+";");
			    poleceniaNK.add(rmvOldReq+";");
			    
			    poleceniaNK.add("###CHECK_DOWN_BTS");
			    poleceniaNK.addAll(checkDownKomm);
			    poleceniaNK.add("\r\n");

			}
			else
			{
			    poleceniaNK.add(updateReq+";");
			    poleceniaNK.add(rmvOldReq+";\r\n");
			    poleceniaNK.add("###CHECK_DOWN_BTS");
			    poleceniaNK.addAll(checkDownKomm);
			    poleceniaNK.add("\r\n");
			}
			
		    }
		}
		else
		{
		    String rmvOldReq="DELETE FROM oncall.konfiguracja_aktualna_bts where (Bts_Index='"+indexBef+"')";
		    String decomerOldReq="UPDATE IGNORE oncall.komercyjnosc_komorek_gcell SET Status_komercyjnosci='0',Last_Update="+obecnyDzien+" where (Bts_Index='"+indexBef+"')";
		    if(idKomer.contains(indexBef))
		    {
			poleceniaK.add(rmvOldReq+";");
			poleceniaK.add(decomerOldReq+";\r\n");
		    }
		    else
		    {
			poleceniaNK.add(rmvOldReq+";\r\n");
		    }
		  //checkDown(nodebIndexDoZm, indexNew);
		}

	    }
	    komercyjneClean.addAll(poleceniaK);
	    //komercyjneClean.add("NIEKOMERCYJNE(TESTOWO W KOMERCYJNYCH)");
	    niekomercyjneClean.addAll(poleceniaNK);


	    /*for(int p=0;p<doPoprawki.rowCount();p++)
	    {
		String delSt="DELETE FROM oncall.konfiguracja_aktualna_plyty_2G WHERE oncall.konfiguracja_aktualna_plyty_2G.index = '"+doPoprawki.getValue("index", p)+"'";
		//testStatement.executeUpdate(delSt);
		niekomercyjneClean.add(delSt+";");
	    }*/
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
	  //  testStatement = connection.createStatement();
	    //String req="select pl.* from oncall.konfiguracja_aktualna_plyty_2G pl where pl.last_update<"+obecnyDzien;
	    String req="select bt.* from oncall.konfiguracja_aktualna_bts bt left join oncall.konfiguracja_aktualna_rnc_bsc r on (r.Rnc_Bsc_Index=bt.Rnc_Bsc_Index) where(r.Rnc_Bsc_Name like '"+kontroler+"' and  bt.last_update<"+obecnyDzien+");";
	    //select bt.* from oncall.konfiguracja_aktualna_bts bt where ( bt.Last_update<'"+obecnyDzien+"');
	    ResultSet res=testStatement.executeQuery(req);
	    OdpowiedzSQL rnc=Baza.createAnswer(res);
	    //System.out.println(rnc);
	    return rnc;
	}
	catch(Exception ee)
	{
	    loger.log(Level.FINEST,"["+this.identyfikator+"] ERROR:" , ee);
	    return null;
	}

    }

    public java.util.ArrayList<String> checkDownBts(String oldNeIndex, String newNeIndex,String data)
    {
	java.util.ArrayList<String> tmpKomm=new java.util.ArrayList<String>();
	String updateKomer="UPDATE IGNORE oncall.komercyjnosc_komorek_gcell SET Bts_Index='"+newNeIndex+"', Cell_Index=replace(Cell_Index,'"+oldNeIndex+"|','"+newNeIndex+"|'), Update_Status='CHANGE NE_ID',Last_Update="+data+" where Bts_Index='"+oldNeIndex+"'";
	//String rmvOldReq="DELETE FROM oncall.konfiguracja_aktualna_NodebType where (Ne_Index='"+nodebIndexDoZm+"')";
	String updateAktCell="UPDATE IGNORE oncall.konfiguracja_aktualna_gcell SET Bts_Index='"+newNeIndex+"', Cell_Index=replace(Cell_Index,'"+oldNeIndex+"|','"+newNeIndex+"|'), Update_Status='CHANGE NE_ID',Last_Update="+data+" where Bts_Index='"+oldNeIndex+"'";
	//String updateUnodeB="UPDATE oncall.konfiguracja_aktualna_nodeb SET Nodeb_Index='"+newNeIndex+"' , Update_Status='CHANGE NE_ID',Last_Update="+aktTime+" where Nodeb_Index='"+oldNeIndex+"'";
	String updatePlyty2G="UPDATE IGNORE oncall.konfiguracja_aktualna_plyty_2G SET ne_Index='"+newNeIndex+"' , Update_Status='CHANGE NE_ID',Last_Update="+data+", oncall.konfiguracja_aktualna_plyty_2G.index=replace(oncall.konfiguracja_aktualna_plyty_2G.index,'"+oldNeIndex+"|','"+newNeIndex+"|') where ne_Index='"+oldNeIndex+"'";
	String update_cell_on_air_stat="UPDATE IGNORE raport_konfiguracja_aktualna.cell_on_air_stat SET  raport_konfiguracja_aktualna.cell_on_air_stat.Cell_Index=replace(Cell_Index,'"+oldNeIndex+"|','"+newNeIndex+"|')  where Cell_Index like '"+oldNeIndex+"|%'";

	 tmpKomm.add(updateKomer+";");
	 tmpKomm.add(updateAktCell+";");
	 tmpKomm.add(updatePlyty2G+";");
	 tmpKomm.add(update_cell_on_air_stat+";");


	return tmpKomm;
    }
}