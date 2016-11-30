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
public class EnodeB_perM2000_Updater extends Updater_parrent	//extends Thread{
{
  
    public EnodeB_perM2000_Updater(String identyfikator,String M2000_Name,int typOperacji,Logger loger,mysqlpackage.DataSource DOA,NewFile sprzF)
    {
	super(identyfikator,M2000_Name,typOperacji,loger,DOA,sprzF);
    }

    @Override
    public boolean add() throws java.sql.SQLException
    {
	//boolean sukces=false;
	sukces=true;

	String obecnyDzienCzas="'"+sdf.format(DataDzisiaj)+"'";
	try
	{
	    String req="select m.M2000_Index, m.M2000_Name, m.M2000_Ip from oncall.konfiguracja_aktualna_m2000 m where( m.M2000_Name like '%"+this.kontrolerName+"%')";
	    ResultSet res=testStatement.executeQuery(req);
	    OdpowiedzSQL mki=Baza.createAnswer(res);

	    ResultSet allENode=testStatement.executeQuery("select * from oncall.konfiguracja_aktualna_enodeb where oncall.konfiguracja_aktualna_enodeb.M2000_Index = (select M2000_Index from oncall.konfiguracja_aktualna_m2000 where M2000_Name like '%"+this.kontrolerName+"%' LIMIT 1) ");
	    OdpowiedzSQL enody=Baza.createAnswer(allENode);

	    if(mki.rowCount()>0)
	    {
		String M_ip=mki.getValue("M2000_Ip", 0);
		String M_name=mki.getValue("M2000_Name", 0);
		String M_index=mki.getValue("M2000_Index", 0);
		java.util.Hashtable<String,Paczka> ENBfromNorth=new java.util.Hashtable<String, Paczka>();
		try
		{
		    north=new nbipackage.NorthB(M_ip,"U-boot","utranek098",null);
		    String lst_ne=north.make2("LST NE:");

		    if(lst_ne.contains("Login or Register needed"))
		    {
			lst_ne=north.make2("LST NEBYOMC:");
		    }
		    if(!lst_ne.contains("RETCODE = 0"))
		    {
			loger.log(Level.FINEST, "["+this.identyfikator+"]BLAD POBRANIA LST NE:"+lst_ne);
			sukces=false;
			throw new java.sql.SQLWarning(this.identyfikator+" BLEDNA ODP NORTHB:\r\n"+lst_ne);
		    }
		    else
		    {
			java.util.ArrayList<String> lte_lines=nbipackage.NewFile.getLinesFromText(new String[]{"eNodeBNE"}, lst_ne);
			lte_lines.addAll(nbipackage.NewFile.getLinesFromText(new String[]{"BTS3900NE"}, lst_ne));
			java.util.ArrayList<String> idikiNod=new java.util.ArrayList<String>();
			for(int l=0;l<lte_lines.size();l++)
			{
			    try
			    {
				String enodeb_name =nbipackage.NewFile.getTokens(lte_lines.get(l), "2", "2");
				Paczka epack=getEnodeIdPack(north,enodeb_name);
				if(epack!=null)
				{
				    String nodebIndex=M_index+"|"+epack.getWartosc("eNodeB ID");
				    {
					ENBfromNorth.put(nodebIndex,epack);
					idikiNod.add(nodebIndex);
				    }
				}
			    }
			    catch(Exception ee)
			    {
				loger.log(Level.FINEST, "["+this.identyfikator+"]ERROR ", ee);
	
			    }
			}
			for(int n=0;n<enody.rowCount();n++)
			{
			    String nn=enody.getValue("Enodeb_Name", n);
			    try
			    {
				String befor_index=enody.getValue("Enodeb_Index", n);
				String befor_ready_for_coa=enody.getValue("Status_ready_for_coa", n);
				String befor_update_status=enody.getValue("Update_Status", n);
				String queryToExecute="";
				if(ENBfromNorth.containsKey(befor_index))
				{
				    Paczka nod=ENBfromNorth.get(befor_index);
				    String Ne_Node_Name=nod.getWartosc("ENODEB NAME");
				    String readyFromNorth=getReadyForCoaStat(north,Ne_Node_Name).replaceAll("'", "");
				    if(readyFromNorth.length()>40)
					readyFromNorth=readyFromNorth.substring(0, 39);
				    String new_update_status="OK";
				    String new_ready_for_coa="3";////okreslic status
				    if(readyFromNorth.contains("E:"))
				    {
					new_update_status="ERROR";
					String info="";
					if(readyFromNorth.contains("RETCODE"))
					    info=readyFromNorth;
					else
					    info= north.make2("REG NE:NAME=\""+Ne_Node_Name+"\"").replaceAll("'", "");
					if(info.contains("RETCODE"))
					    info=NewFile.getFirstLine(new String[]{"RETCODE"}, info);
					if(info.length()>40)
					    info=info.substring(0, 39);
					if(info.contains("Can't Found NE"))
					    new_update_status="NOT_FOUND";
					if(info.contains("Cant Found NE"))
					    new_update_status="NOT_FOUND";
					else if(info.contains("NE does not Connection"))
					    new_update_status="NOT_CONNECTED";
					else
					    new_update_status=readyFromNorth;
				    }
				    else if(readyFromNorth.contains("F:"))
				    {
					new_ready_for_coa="0";
				    }
				    else if(readyFromNorth.contains("T:"))
				    {
					new_ready_for_coa="1";
				    }
				    queryToExecute="UPDATE  oncall.konfiguracja_aktualna_enodeb SET Last_Update="+obecnyDzienCzas+", Update_Status='"+new_update_status+"',Status_ready_for_coa="+new_ready_for_coa+", ready_for_coa_info='"+readyFromNorth+"', Enodeb_Name='"+Ne_Node_Name+"' WHERE Enodeb_Index='"+befor_index+"';";
				    testStatement.execute(queryToExecute);
				    if(!new_ready_for_coa.equals(befor_ready_for_coa))//&&!befor_update_status.equals("NOT_FOUND"))
				    {
					queryToExecute="Insert into oncall.konfiguracja_aktualna_zmiany_ne (zmiany_index,ne_index,Data_Modyfikacji,Typ_Modyfikacji) Values(NULL,'"+befor_index+"',"+obecnyDzienCzas+",'"+new_ready_for_coa+"');";
					testStatement.execute(queryToExecute);
				    }
				    if(!new_update_status.equals(befor_update_status))//&&!befor_update_status.equals("NOT_FOUND"))
				    {
					if(new_update_status.equals("OK"))
					    new_update_status=new_ready_for_coa;
					queryToExecute="Insert into oncall.konfiguracja_aktualna_zmiany_ne (zmiany_index,ne_index,Data_Modyfikacji,Typ_Modyfikacji) Values(NULL,'"+befor_index+"',"+obecnyDzienCzas+",'"+new_update_status+"');";
					testStatement.execute(queryToExecute);
				    }
				    idikiNod.remove(befor_index);
				}
				else
				{
				    String new_update_status="#";
				    String info=north.make2("REG NE:NAME=\""+enody.getValue("Enodeb_Name", n)+"\"").replaceAll("'", "");
				    if(info.contains("RETCODE"))
					info=NewFile.getFirstLine(new String[]{"RETCODE"}, info);
				    if(info.length()>40)
					info=info.substring(0, 39);
				    if(info.contains("Can't Found NE"))
					new_update_status="NOT_FOUND";
				    if(info.contains("Cant Found NE"))
					new_update_status="NOT_FOUND";
				    if(info.contains("NE does not Connection"))
					new_update_status="NOT_CONNECTED";
				    String tmp=north.make(enody.getValue("Enodeb_Name", n), "LST ENODEBFUNCTION:");
				    if(tmp.contains("No matching"))
					new_update_status="NOT_FOUND EFUN";
				    
				    queryToExecute="UPDATE  oncall.konfiguracja_aktualna_enodeb SET Last_Update="+obecnyDzienCzas+", Update_Status='"+new_update_status+"', ready_for_coa_info='"+info+"' WHERE Enodeb_Index='"+befor_index+"'";
				    testStatement.execute(queryToExecute);
				    if(!new_update_status.equals(befor_update_status))
				    {
					queryToExecute = "Insert into oncall.konfiguracja_aktualna_zmiany_ne (zmiany_index,ne_index,Data_Modyfikacji,Typ_Modyfikacji) Values(NULL,'" + befor_index + "'," + obecnyDzienCzas + ",'" + new_update_status + "');";
					testStatement.execute(queryToExecute);
				    }
				}
			    }
			    catch(Exception ee)
			    {
				loger.log(Level.FINEST, "["+this.identyfikator+"]ERROR ", ee);
				if(north!=null)
				{
				    try{
					north.closeBuffor();
					north=null;
				    }
				    catch(Exception ewe)
				    {
					loger.log(Level.FINEST, "["+this.identyfikator+"]ERROR ", ewe);
				    }
				    
				}
				north=new nbipackage.NorthB(M_ip,"U-boot","utranek098",null);
			    }
			}

			for(int n=0;n<idikiNod.size();n++)//dodanie nowych NODOW
			{
			    try
			    {
				Paczka node=ENBfromNorth.get(idikiNod.get(n));
				String nodebName=node.getWartosc("ENODEB NAME");
				String siteName="";
				if(nodebName.contains("_"))
				    siteName=nodebName.split("_")[1];
				else
				{
				    if(nodebName.length()>8)
					siteName=nodebName.substring(0, 8);
				    else
					siteName=nodebName;
				}
				String nodebId=node.getWartosc("eNodeB ID");
				String region="";
				region=nodebName.substring(0, 1);
				if(!region.matches("[1-4]"))
				{
				    if(M_name.contains("WAW"))
					region="1";
				    else if(M_name.contains("KAT"))
					region="2";
				    else if(M_name.contains("POZ"))
					region="3";
				    else if(M_name.contains("GDA"))
					region="4";
				}
				if(!region.matches("[1-4]"))
				    region="0";
				if(nodebId!=null&&nodebId.length()>0)
				{
				    String nodebIndex=M_index+"|"+nodebId;
				    String status_ready_for_coa="2";//stala wartosc 2 oznaczajaca nowy wpis
				    String update_stat="NEW";
				    String readyFromNorth=getReadyForCoaStat(north,nodebName).replaceAll("'", "");
				    if(readyFromNorth.length()>40)
					readyFromNorth=readyFromNorth.substring(0, 39);
				    if(readyFromNorth.contains("E:"))
				    {
					String info=north.make2("REG NE:NAME=\""+enody.getValue("Enodeb_Name", n)+"\"").replaceAll("'", "");
					if(info.contains("RETCODE"))
					    info=NewFile.getFirstLine(new String[]{"RETCODE"}, info);
					if(info.length()>40)
					    info=info.substring(0, 39);
					if(info.contains("Can't Found NE"))
					    update_stat="NOT_FOUND";
					else if(info.contains("Cant Found NE"))
					    update_stat="NOT_FOUND";
					else if(info.contains("NE does not Connection"))
					    update_stat="NOT_CONNECTED";
					else
					    update_stat=readyFromNorth;
				    }
				    else if(readyFromNorth.contains("F:"))
				    {
					status_ready_for_coa="0";
				    }
				    else if(readyFromNorth.contains("T:"))
				    {
					status_ready_for_coa="1";
				    }
				    testStatement.execute("insert into oncall.konfiguracja_aktualna_site(Site_Index, M2000_Index, Site_Name, Last_Update, Update_Status) VALUES(NULL,'"+M_index+"','"+siteName+"',"+obecnyDzienCzas+",'"+update_stat+"') ON DUPLICATE KEY UPDATE Last_Update="+obecnyDzienCzas+", Update_Status='OK', M2000_Index='"+M_index+"';");
				    String query="insert into oncall.konfiguracja_aktualna_enodeb(Enodeb_Index, M2000_Index, Site_Index, Enodeb_Id, ENodeb_Name, Status_ready_for_coa, Last_Update, Update_Status,ready_for_coa_info,Region) VALUES('"+nodebIndex+"',"+M_index+",(select Site_Index from oncall.konfiguracja_aktualna_site where Site_Name like '%"+siteName+"%' LIMIT 1),"+nodebId+",'"+nodebName+"',"+status_ready_for_coa+","+obecnyDzienCzas+",'"+update_stat+"','"+readyFromNorth+"',"+region+");";
				    String query1="Insert into oncall.konfiguracja_aktualna_zmiany_ne (zmiany_index,ne_index,Data_Modyfikacji,Typ_Modyfikacji) Values(NULL,'"+nodebIndex+"',"+obecnyDzienCzas+",'NEW');";
				    testStatement.execute(query);
				    testStatement.execute(query1);
				}
			    }
			    catch(Exception ee)
			    {
				loger.log(Level.FINEST, "["+this.identyfikator+"]ERROR ", ee);
				if(north!=null)
				{
				    try{
					north.closeBuffor();
					north=null;
				    }
				    catch(Exception ewe)
				    {
					loger.log(Level.FINEST, "["+this.identyfikator+"]ERROR ", ewe);
				    }
				    
				}
				north=new nbipackage.NorthB(M_ip,"U-boot","utranek098",null);
			    }
			}
		    }
		    if(north!=null)
		    {
			try
			{
			    north.closeBuffor();
			}
			catch(Exception ewe)
			{
			    loger.log(Level.FINEST, "["+this.identyfikator+"]ERROR North Close", ewe);
			}
		    }
		}
		catch(Exception ee)
		{
		    sukces=false;
		    loger.log(Level.FINEST, "["+this.identyfikator+"]ERROR North Close", ee);
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
		{
		    connection.close();
		    connection=null;
		}
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

    public String getReadyForCoaStat(NorthB north, String node_name)
    {
	String status="";
	try
	{
	    String odp=north.make(node_name, "DSP BRD:");
	    //System.out.println(odp);


	     int powtorka=0;
	     int sleepTime=5000;
	     while(!odp.contains("RETCODE = 0")&&powtorka<2)
	     {

		 System.err.println("NorthB error. sleep "+sleepTime+" ms");
		 Thread.sleep(sleepTime);
		 odp=north.make(node_name, "DSP BRD:");
		 powtorka++;
	    }



	    if(odp.contains("RETCODE = 0"))
	    {
		NPack nn = new NPack(odp);
		java.util.ArrayList<Paczka> plyty=nn.getAllPacks();
		boolean lbbp=false;
		for(int p=0;plyty!=null&&p<plyty.size();p++)
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

		if(odp.contains("RETCODE")&&!odp.contains("RETCODE = 1"))
		    return "F:"+NewFile.getLinia("RETCODE", odp)[0];
		else
		{
		    String info=north.make2("REG NE:NAME=\""+node_name+"\"").replaceAll("'", "");
		    if(info.contains("RETCODE"))
			info=NewFile.getFirstLine(new String[]{"RETCODE"}, info);
		    if(info.length()>40)
			info=info.substring(0, 39);
		    if(info.contains("RETCODE = 0"))
			return "F:"+info;
		    else
			return "E:"+info;
		}
	    }
	}
	catch(Exception ee)
	{

	    loger.log(Level.FINEST, "["+this.identyfikator+"]ERROR ", ee);
	    ee.printStackTrace();
	    return "E:"+ee.toString();
	}
	
    }
    private Paczka getEnodeIdPack(NorthB north,String enodeb_name)
    {
	Paczka epack=null;
	try
	{
	     String tmp=north.make(enodeb_name, "LST ENODEBFUNCTION:");
	     int powtorka=0;
	     int sleepTime=5000;
	     while(!tmp.contains("RETCODE = 0")&&!tmp.contains("Invalid Command")&&powtorka<5)
	     {

		 System.err.println("NorthB error. sleep "+sleepTime+" ms");
		 Thread.sleep(sleepTime);
		 tmp=north.make(enodeb_name, "LST ENODEBFUNCTION:");
		 powtorka++;
	    }
	    if(tmp.contains("RETCODE = 0"))
	    {
		nbipackage.NPack enodeFNPack=new NPack(tmp.split("\n"), new String[]{"LST ENODEBFUNCTION:"}, new String[]{"-------"}, new String[]{"---    END"});
		if(enodeFNPack.getAllPacks().size()>0)
		{
		    epack=enodeFNPack.getAllPacks().get(0);
		    epack.dodaj("ENODEB NAME", enodeb_name);
		}
	    }
	}
	catch(Exception e)
	{
	    loger.log(Level.FINEST, "["+this.identyfikator+"]ERROR ", e);
	    e.printStackTrace();
	}
	return epack;
    }

    public boolean clearNotUpdatet()
    {
	 try
	 {
	     java.util.ArrayList<String> poleceniaK=new java.util.ArrayList<String>();
	     java.util.ArrayList<String> poleceniaNK=new java.util.ArrayList<String>();
	     String obecnyDzien="'"+super.withoutTimeFormat.format(DataDzisiaj)+"'";
	     OdpowiedzSQL nody=getNotUpdateCandidates(DataDzisiaj, this.kontrolerName);
	     java.util.ArrayList<String> idKomer=new java.util.ArrayList<String>();
	     ResultSet  res=testStatement.executeQuery("select Nodeb_Index from oncall.komercyjnosc_komorek_ecell;");
	     OdpowiedzSQL komercyjne=Baza.createAnswer(res);
	     for(int k=0;k<komercyjne.rowCount();k++)
		 if(!idKomer.contains(komercyjne.getValue("Nodeb_Index", k)))
		     idKomer.add(komercyjne.getValue("Nodeb_Index", k));
	    for(int p=0;p<nody.rowCount();p++)
	    {

		//String delSt="DELETE FROM oncall.konfiguracja_aktualna_plyty_3G WHERE oncall.konfiguracja_aktualna_plyty_3G.index = '"+plyty.getValue("index", p)+"'";
		//testStatement.executeUpdate(delSt);
		//polecenia.append(delSt+";\r\n");
		String nameDoZm=nody.getValue("NodebName", p);
		//System.out.println("Check:"+nameDoZm);
		String nodebIndexDoZm=nody.getValue("Ne_index", p);
		if(nameDoZm!=null&&!nameDoZm.equals("")&&nameDoZm.contains("_"))
		{
		String idNode=nodebIndexDoZm;
		if(nodebIndexDoZm.contains("|"))
		{
		    String [] splity=nodebIndexDoZm.split("[|]");
		    if(splity!=null&&splity.length>1&&splity[1].length()>0)
		    idNode="|"+splity[1];
		}
		String updateBef=nody.getValue("Last_update", p);
		String typBef=nody.getValue("NodebType", p);
		String site=NewFile.getTokens(nameDoZm, "2", "2", "_");
		String duplicateReq="select nt.* from oncall.konfiguracja_aktualna_NodebType nt where"
			+ "("
			    +"("
				+ "(nt.Ne_index='"+nodebIndexDoZm+"' and nt.NodebName='"+nameDoZm+"') or"
				+ "(nt.Ne_index='"+nodebIndexDoZm+"' and nt.NodebName like '%"+site+"%') or"
				+ "(nt.Ne_index like '%"+idNode+"' and nt.NodebName like '%"+nameDoZm+"%')"
			    +")"
			    + " and (nt.NodebName!='"+nameDoZm+"' or nt.Ne_index!='"+nodebIndexDoZm+"')"+ "and nt.Last_Update>'"+updateBef+"'"
			+ " );";
		System.out.println("REQUEST DUP:"+duplicateReq);
		res=testStatement.executeQuery(duplicateReq);
		OdpowiedzSQL duplikaty=Baza.createAnswer(res);
		if(duplikaty.rowCount()>0)
		{
		    for(int d=0;d<duplikaty.rowCount();d++)
		    {
			System.out.println("duplikaty dla: "+nameDoZm+" "+nodebIndexDoZm+" "+updateBef+"\r\n"+duplikaty.toString());
			String indexNew=duplikaty.getValue("Ne_index", d);
			String nameNew=duplikaty.getValue("NodebName", d);
			String typNew=duplikaty.getValue("NodebType", d);

			boolean oldPerId=false;
			boolean oldPerName=false;
			if(!nodebIndexDoZm.equals(indexNew))
			{
			    oldPerId=true;
			}
			else if(!nameNew.equals(nameDoZm))
			{
			    oldPerName=true;
			}

			String updateReq="UPDATE  IGNORE oncall.konfiguracja_aktualna_NodebType  SET Status='UPDATE:join:"+nameDoZm+","+nodebIndexDoZm+"' ,Last_Update="+obecnyDzien+" ";
			if(typNew.contains("3900")||typNew.contains("3800")||typNew.contains("3812"))
			    ;
			else
			{
			    if(typBef.contains("3900")||typBef.contains("3800")||typBef.contains("3812"))
				updateReq=updateReq+",NodebType="+typBef+" ";

			}
			if(oldPerId)
			    updateReq=updateReq+"where(Ne_index='"+indexNew+"');";
			else if(oldPerName)
			    updateReq=updateReq+"where(NodebName='"+nameNew+"');";

			String rmvOldReq="DELETE FROM oncall.konfiguracja_aktualna_NodebType where (Ne_Index='"+nodebIndexDoZm+"' and NodebName='"+nameDoZm+"');";

			java.util.ArrayList<String> kommDownTmp=checkDownNodeB(nodebIndexDoZm, indexNew,obecnyDzien);
			if(idKomer.contains(nodebIndexDoZm))
			{
			    poleceniaK.add(updateReq);
			    poleceniaK.add(rmvOldReq);

			    poleceniaK.add("#####CHECK DOWN####");
			    poleceniaK.addAll(kommDownTmp);
			    poleceniaK.add("\r\n");
			}
			else
			{
			    poleceniaNK.add(updateReq);
			    poleceniaNK.add(rmvOldReq);
			    poleceniaNK.add("#####CHECK DOWN####");
			    poleceniaNK.addAll(kommDownTmp);
			    poleceniaNK.add("\r\n");
			}

		    }
		}
		else
		{
		    String rmvOldReq="DELETE FROM oncall.konfiguracja_aktualna_NodebType where (Ne_Index='"+nodebIndexDoZm+"');";
		    if(idKomer.contains(nodebIndexDoZm))
		    {
			//poleceniaKomer.append(rmvOldReq+";\r\n");
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
	    }
	    komercyjneClean.addAll(poleceniaK);
	    komercyjneClean.add("#####NIEKOMERCYJNE(TESTOWO W KOMERCYJNYCH)");
	    komercyjneClean.addAll(poleceniaNK);


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
	    return false;
	}
    }
    @Override
    public OdpowiedzSQL getNotUpdateCandidates(java.util.Date dateBefore, String kontroler)
    {
	String obecnyDzien="'"+super.withoutTimeFormat.format(dateBefore)+"'";
	try
	{
	    //testStatement = connection.createStatement();
	    //String req="select pl.* from oncall.konfiguracja_aktualna_plyty_2G pl where pl.last_update<"+obecnyDzien;
	    //String req="select pl.* from oncall.konfiguracja_aktualna_plyty_2G pl left join oncall.konfiguracja_aktualna_bts b on(b.Bts_Index=pl.ne_index) left join oncall.konfiguracja_aktualna_rnc_bsc r on (r.Rnc_Bsc_Index=b.Rnc_Bsc_Index) where(r.Rnc_Bsc_Name like '"+kontroler+"' and  pl.last_update<"+obecnyDzien+");";
	    //select * from oncall.konfiguracja_aktualna_enodeb e where e.Update_Status='NOT_FOUND' or e.ready_for_coa_info like '%Cant Found%';
	    String req="select b.* from oncall.konfiguracja_aktualna_enodeb b  left join oncall.konfiguracja_aktualna_m2000 nt on(b.M2000_Index=nt.M2000_Index) where ( nt.M2000_Name like '%"+kontroler+"%' and ( (b.`Update_Status` like 'NOT_FOUND' or b.`Update_Status`='#') or( b.Last_update<"+obecnyDzien+")) and b.`Update_Status`  not like 'NE_NOT_CONNECTED');";
		
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

    public java.util.ArrayList<String> checkDownNodeB(String oldNeIndex, String newNeIndex,String aktTime)
    {
	java.util.ArrayList<String> tmpKom=new java.util.ArrayList<String>();
	if(!oldNeIndex.equals(newNeIndex))
	{
	    tmpKom.add("UPDATE IGNORE oncall.komercyjnosc_komorek_ucell SET Nodeb_Index='"+newNeIndex+"', Cell_Index=replace(Cell_Index,'"+oldNeIndex+"|','"+newNeIndex+"|'), Update_Status='CHANGE NE_ID',Last_Update="+aktTime+" where Nodeb_Index='"+oldNeIndex+"';");
	    tmpKom.add("UPDATE IGNORE oncall.konfiguracja_aktualna_ucell SET Nodeb_Index='"+newNeIndex+"', oncall.konfiguracja_aktualna_ucell.Cell_Index=replace(Cell_Index,'"+oldNeIndex+"|','"+newNeIndex+"|'), Update_Status='CHANGE NE_ID',Last_Update="+aktTime+" where Nodeb_Index='"+oldNeIndex+"';");
	    tmpKom.add("UPDATE IGNORE raport_konfiguracja_aktualna.cell_on_air_stat SET  raport_konfiguracja_aktualna.cell_on_air_stat.Cell_Index=replace(Cell_Index,'"+oldNeIndex+"|','"+newNeIndex+"|')  where Cell_Index like '"+oldNeIndex+"|%';");
	    tmpKom.add("UPDATE IGNORE oncall.konfiguracja_aktualna_nodeb SET Update_Status='UPDATE:"+oldNeIndex+"',Last_Update="+aktTime+" where Nodeb_Index='"+newNeIndex+"';");
	    tmpKom.add("DELETE FROM   oncall.konfiguracja_aktualna_nodeb where Nodeb_Index='"+oldNeIndex+"';");
	    tmpKom.add("UPDATE IGNORE oncall.konfiguracja_aktualna_plyty_3G SET ne_Index='"+newNeIndex+"' , Update_Status='CHANGE NE_ID',Last_Update="+aktTime+", oncall.konfiguracja_aktualna_plyty_3G.index=replace(oncall.konfiguracja_aktualna_plyty_3G.index,'"+oldNeIndex+"|','"+newNeIndex+"|') where ne_Index='"+oldNeIndex+"';");
	}
	return tmpKom;
    }
}
