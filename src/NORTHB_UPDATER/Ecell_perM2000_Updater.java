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
public class Ecell_perM2000_Updater  extends Updater_parrent		//extends Thread{
{
    /*
    String M2000_Name;
    int typOperacji;
    private String identyfikator;
    private mysqlpackage.DataSource DOA;
    Logger loger;
    */
    
    public Ecell_perM2000_Updater(String identyfikator,String M2000_Name,int typOperacji,Logger loger,mysqlpackage.DataSource DOA,NewFile sprzF)
    {
	super(identyfikator,M2000_Name,typOperacji,loger,DOA,sprzF);
    }
    @Override
    public boolean add() throws java.sql.SQLException
    {
	sukces=false;
	String obecnyDzienCzas="'"+sdf.format(DataDzisiaj)+"'";
	try
	{
	
	    String allEnodeReq="select m.M2000_Ip,m.M2000_Index,en.Enodeb_Index,en.Enodeb_Name from oncall.konfiguracja_aktualna_m2000 m, oncall.konfiguracja_aktualna_enodeb en where( m.M2000_Name like '%"+this.kontrolerName+"%' and m.M2000_Index= en.M2000_index and( en.Update_Status like 'OK' or en.Update_Status like 'NEW' or en.Update_Status like 'NOT_CONNECTED'))";
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
		    try
		    {
			String enodeName=enody.getValue("Enodeb_Name", n);
			String enode_Index=enody.getValue("Enodeb_Index", n);
			String tmp=north.make(enodeName, "LST CELL:");
			String retcode="";
			if(tmp.contains("RETCODE"))
			    retcode=NewFile.getFirstLine(new String[]{"RETCODE"}, tmp);
			if(retcode.contains("RETCODE = 0")) ///poprawna odpowiedz
			{
			    NPack nn=new NPack(tmp);
			    if(tmp.contains("Number of results = 1"))
				nn=new NPack(tmp.split("\n"),new String[]{"LST CELL:"},new String[]{"---------"},new String[]{"---    END"});
			    java.util.ArrayList<Paczka> ecellki=nn.getAllPacks();
			    String lstAcces=north.make(enodeName, "LST CELLACCESS:");
			    java.util.ArrayList<Paczka> listaAcc=new java.util.ArrayList<Paczka>();
			    if(lstAcces.contains("Number of results = 1"))
			    {
				NPack npack=new NPack(lstAcces.split("\n"), new String[]{"LST CELLACCESS:"}, new String[]{"----------"}, new String[]{"---    END"});
				listaAcc=npack.getAllPacks();
			    }
			    else
			    {
				NPack npack=new NPack(lstAcces);
				listaAcc=npack.getAllPacks();
			    }
			    String lstOPC=north.make(enodeName, "LST CELLOP:");
			    java.util.ArrayList<Paczka> listaOpc=new java.util.ArrayList<Paczka>();
			    if(lstAcces.contains("Number of results = 1"))
			    {
				NPack npack=new NPack(lstOPC.split("\n"), new String[]{"LST CELLOP:"}, new String[]{"----------"}, new String[]{"---    END"});
				listaOpc=npack.getAllPacks();
			    }
			    else
			    {
				NPack npack=new NPack(lstOPC);
				listaOpc=npack.getAllPacks();
			    }
			    for(int e=0;e<ecellki.size();e++)
			    {
				String cellId=ecellki.get(e).getWartosc("Cell ID");
				String cellIndex=enode_Index+"|"+cellId;
				String LoId=ecellki.get(e).getWartosc("Local Cell ID");
				String cellName=ecellki.get(e).getWartosc("Cell Name");
				String actStat=ecellki.get(e).getWartosc("Cell active state");
				String blkStat=ecellki.get(e).getWartosc("Cell admin state");
				String barrStat="";
				String reservStat="NOT_FOUND";
				String updateStat="NEW";
				String trc="#";
				boolean dopas=false;
				for(int a=0;!dopas&&listaAcc!=null&&a<listaAcc.size();a++)
				{
				    String locellId=listaAcc.get(a).getWartosc("Local cell ID");
				    if(LoId.equals(locellId))
				    {
					barrStat=listaAcc.get(a).getWartosc("Cell barring state");
					dopas=true;
				    }
				}
				dopas=false;
				for(int a=0;!dopas&&listaOpc!=null&&a<listaOpc.size();a++)
				{
				    String locellId=listaOpc .get(a).getWartosc("Local cell ID");
				    if(LoId.equals(locellId))
				    {
					reservStat=listaOpc.get(a).getWartosc("Cell reserved for operator");
					trc=listaOpc.get(a).getWartosc("Local tracking area ID");
					dopas=true;
				    }
				}
				if(barrStat.length()>10)
				    barrStat=barrStat.substring(0, 10);
				if(actStat.length()>10)
				    actStat=actStat.substring(0, 10);
				if(blkStat.length()>10)
				    blkStat=blkStat.substring(0, 10);
				if(reservStat.length()>10)
				    reservStat=reservStat.substring(0, 10);
				String query="insert into oncall.konfiguracja_aktualna_ecell (Cell_Index, Cell_Id, Cell_Name, Enodeb_Index,System,Status_Ubl,Status_Act,Status_Barr,Status_Reserv,Last_Update,Update_Status,Locell_Id,trc) VALUES('"+cellIndex+"','"+cellId+"','"+cellName+"','"+enode_Index+"','LTE','"+blkStat+"','"+actStat+"','"+barrStat+"','"+reservStat+"',"+obecnyDzienCzas+",'"+updateStat+"','"+LoId+"','"+trc+"') ON DUPLICATE KEY UPDATE Status_Ubl='"+blkStat+"',Status_Act='"+actStat+"',Status_Barr='"+barrStat+"',Cell_Name='"+cellName+"',Status_Reserv='"+reservStat+"',Last_Update="+obecnyDzienCzas+", Update_Status='OK',Locell_Id='"+LoId+"',trc='"+trc+"'";
				testStatement.execute(query);
			    }
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
			    String query="update oncall.konfiguracja_aktualna_ecell SET Last_Update="+obecnyDzienCzas+", Update_Status='"+new_update_status+"' WHERE Enodeb_Index='"+enode_Index+"'";
			    testStatement.execute(query);
			}
		    }
		    catch(NBIAnsException ee)
		    {
			loger.log(Level.FINEST, "["+this.identyfikator+"]ERROR ", ee);
			try
			{
			    north.closeBuffor();
			    north=null;
			    north=new NorthB(m2000_ip,"U-boot","utranek098",null);
			}
			catch(Exception eee)
			{
			    loger.log(Level.FINEST, "["+this.identyfikator+"]ERROR ", eee);
			}
		    }
		}

	    }
	    //System.out.println("["+this.identyfikator+"]ZAKONCZENIE WATKU");
	    sukces=true;
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

/*
    public String getReadyForCoaStat(NorthB north, String node_name)
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