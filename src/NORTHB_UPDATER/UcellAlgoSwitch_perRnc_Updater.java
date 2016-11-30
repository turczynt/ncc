/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package NORTHB_UPDATER;

import NORTHB_UPDATER.Updater_parrent;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import mysqlpackage.Baza;
import mysqlpackage.OdpowiedzSQL;
import nbipackage.NPack;
import nbipackage.NewFile;
import nbipackage.Paczka;

/**
 *
 * @author turczyt
 */
public class UcellAlgoSwitch_perRnc_Updater extends Updater_parrent {
String obecnyDzienCzas;
Hashtable<String,String> idSwitch;
    public UcellAlgoSwitch_perRnc_Updater(String identyfikator,String kontroler,int typOperacji,Logger loger,mysqlpackage.DataSource DOA,NewFile sprzF)
    {
	super(identyfikator,kontroler,typOperacji,loger,DOA,sprzF);
    }
    

    public boolean add() throws java.sql.SQLException
    {
	obecnyDzienCzas="'"+sdf.format(DataDzisiaj)+"'";
	idSwitch=new java.util.Hashtable<String, String>();
	sukces=false;
	try
	{
	    String req="select r.Rnc_Bsc_Index, r.Rnc_Bsc_Id, r.Rnc_Bsc_Name, m.M2000_Ip, r.M2000_Index from oncall.konfiguracja_aktualna_rnc_bsc r,  oncall.konfiguracja_aktualna_m2000 m where(r.m2000_index=m.M2000_Index and r.Rnc_Bsc_Name like '%"+this.kontrolerName+"%') LIMIT 1";
	    ResultSet res=testStatement.executeQuery(req);
	    OdpowiedzSQL rnc=Baza.createAnswer(res);
	    if(rnc.rowCount()>0)
	    {
		String m2000_ip=rnc.getValue("M2000_Ip", 0);
		String rnc_name=rnc.getValue("Rnc_Bsc_Name", 0);
		String rnc_id=rnc.getValue("Rnc_Bsc_Id",0);
		String rnc_index=rnc.getValue("Rnc_Bsc_Index", 0);
		String m2000_Index=rnc.getValue("M2000_Index", 0);
		north=new nbipackage.NorthB(m2000_ip, "U-boot", "utranek098",null);
		String lst=north.make(rnc_name, "LST UCELLALGOSWITCH:LSTFORMAT=HORIZONTAL");
		north.closeBuffor();
		north=null;
		NPack npack=new NPack(lst);
		java.util.ArrayList<Paczka> algoLst=npack.getAllPacks();
		    //System.out.println(lst);
		    for(int z=0;algoLst!=null&&z<algoLst.size();z++)
		    {
			/*
			 Cell CAC algorithm switch ;
			 Uplink CAC algorithm switch;
			 Downlink CAC algorithm switch;
			 Switch for Cell Load Control
			Mac-hs Reset algorithm switch;
			 Cell Hspa Plus function switch;
			 Cell Hspa Enhanced function switch;
			Cell Capability Auto Handle Switch
			Inter-freq Handover Select User algorithm switch;
			Load Based Inter-Rat Handover Select User algorithm switch;
			Offload Switch;
			BE rate reduction switch based on fairness;
			R99 SC LDR User Selection;
			Power CAC Choice for RRC;
			CS RAB Setup Resource Allocation Switch
			Demarcation Preemption Switch;
			Credit and Code CAC Choice for RRC;
			Cell-level Call Shock Switch;
			Cell Downlink Code Admission Optimization Switch
			 *
			 */
			String cellId=algoLst.get(z).getWartosc("Cell ID");
			String cellName=algoLst.get(z).getWartosc("Cell Name");
			System.out.println("############ ["+this.identyfikator+"]"+z+"/"+algoLst.size()+" "+cellName+"############");

			if(!cellName.equals(""))
			{
			String cellIndex="";
			String selCellInd="select Cell_Index from oncall.konfiguracja_aktualna_ucell where Cell_Name='"+cellName+"' LIMIT 1";
			ResultSet resC=testStatement.executeQuery(selCellInd);
			OdpowiedzSQL cellIndexSQL=Baza.createAnswer(resC);
			if(cellIndexSQL.rowCount()>0)
			    cellIndex=cellIndexSQL.getValue("Cell_Index", 0);
			if(!cellIndex.equals(""))
			{

			String CacAlgos=algoLst.get(z).getWartosc("Cell CAC algorithm switch");
			String UplinkCacAlgos=algoLst.get(z).getWartosc("Uplink CAC algorithm switch");
			String DownlinkCacAlgos=algoLst.get(z).getWartosc("Downlink CAC algorithm switch");
			String CellLoadContrAlgos=algoLst.get(z).getWartosc("Switch for Cell Load Control");
			String MacHsAlgos=algoLst.get(z).getWartosc("Mac-hs Reset algorithm switch");
			String HspaPlusFunctionAlgos=algoLst.get(z).getWartosc("Cell Hspa Plus function switch");
			String HspaEnhancedAlgos=algoLst.get(z).getWartosc("Cell Hspa Enhanced function switch");

			String capabAutoHandlAlgos=algoLst.get(z).getWartosc("Cell Capability Auto Handle Switch");
			String InderFreqHandAlgos=algoLst.get(z).getWartosc("Inter-freq Handover Select User algorithm switch");
			String LoadBasedInterHandAlgos=algoLst.get(z).getWartosc("Load Based Inter-Rat Handover Select User algorithm switch");
			String OffloadAlgos=algoLst.get(z).getWartosc("Offload Switch");
			String BeAlgos=algoLst.get(z).getWartosc("BE rate reduction switch based on fairness");
			String R99Algos=algoLst.get(z).getWartosc("R99 SC LDR User Selection");
			String PowerCasForRCCAlgos=algoLst.get(z).getWartosc("Power CAC Choice for RRC");
			String CsRabAlgos=algoLst.get(z).getWartosc("CS RAB Setup Resource Allocation Switch");
			String DeamrcationAlgos=algoLst.get(z).getWartosc("Demarcation Preemption Switch");
			String CreditAndCodeCacAlgos=algoLst.get(z).getWartosc("Credit and Code CAC Choice for RRC");
			String CellLevelCallAlgos=algoLst.get(z).getWartosc("Cell-level Call Shock Switch");
			String CellDownlinkCodeAlgos=algoLst.get(z).getWartosc("Cell Downlink Code Admission Optimization Switch");


			//String CacAlgos=algoLst.get(z).getWartosc("Cell CAC algorithm switch");
			addSwitch(rnc_index,cellId,cellIndex,cellName,"Cell CAC algorithm switch",CacAlgos);
			addSwitch(rnc_index,cellId,cellIndex,cellName,"Uplink CAC algorithm switch",UplinkCacAlgos);
			addSwitch(rnc_index,cellId,cellIndex,cellName,"Downlink CAC algorithm switch",DownlinkCacAlgos);
			addSwitch(rnc_index,cellId,cellIndex,cellName,"Switch for Cell Load Control",CellLoadContrAlgos);
			addSwitch(rnc_index,cellId,cellIndex,cellName,"Mac-hs Reset algorithm switch",MacHsAlgos);
			addSwitch(rnc_index,cellId,cellIndex,cellName,"Cell Hspa Plus function switch",HspaPlusFunctionAlgos);
			addSwitch(rnc_index,cellId,cellIndex,cellName,"Cell Hspa Enhanced function switch",HspaEnhancedAlgos);

			addSwitch(rnc_index,cellId,cellIndex,cellName,"Cell Capability Auto Handle Switch",capabAutoHandlAlgos);
			addSwitch(rnc_index,cellId,cellIndex,cellName,"Inter-freq Handover Select User algorithm switch",InderFreqHandAlgos);
			addSwitch(rnc_index,cellId,cellIndex,cellName,"Load Based Inter-Rat Handover Select User algorithm switch",LoadBasedInterHandAlgos);
			addSwitch(rnc_index,cellId,cellIndex,cellName,"Offload Switch",OffloadAlgos);
			addSwitch(rnc_index,cellId,cellIndex,cellName,"BE rate reduction switch based on fairnes",BeAlgos);
			addSwitch(rnc_index,cellId,cellIndex,cellName,"R99 SC LDR User Selection",R99Algos);
			addSwitch(rnc_index,cellId,cellIndex,cellName,"Power CAC Choice for RRC",PowerCasForRCCAlgos);
			addSwitch(rnc_index,cellId,cellIndex,cellName,"CS RAB Setup Resource Allocation Switch",CsRabAlgos);
			addSwitch(rnc_index,cellId,cellIndex,cellName,"Demarcation Preemption Switch",DeamrcationAlgos);
			addSwitch(rnc_index,cellId,cellIndex,cellName,"Credit and Code CAC Choice for RRC",CreditAndCodeCacAlgos);
			addSwitch(rnc_index,cellId,cellIndex,cellName,"Cell-level Call Shock Switch",CellLevelCallAlgos);
			addSwitch(rnc_index,cellId,cellIndex,cellName,"Cell Downlink Code Admission Optimization Switch",CellDownlinkCodeAlgos);
			
			    }
			}
		    }
		    sukces=true;
		}
	    }
	    catch(Exception e)
	{
	    //e.printStackTrace();
	    loger.throwing(this.getClass().toString(), this.identyfikator+".add", e);
	   // loger.l
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
	    if(sukces)
		this.answer=this.answer+"["+this.identyfikator+"]ZAKONCZENIE WATKU "+sukces;//+insertyAll);
	    else
		this.errorInfo=this.errorInfo+"ERROR["+this.identyfikator+"]ZAKONCZENIE WATKU "+sukces;//" "+checkStr+"\r\n");//+insertyAll);//+" LST_UCELL=="+inLocell+" LST_ACCES=="+inAcces);
	    return sukces;
	}
    }

private boolean addSwitch(String rncIndex,String CellId,String cellIndex,String cellName,String typ,String switche)
{
    String insert="";
    String insertC="";
    try
    {

	String[] pojSwitche=switche.split("&");
	for(int s=0;pojSwitche!=null&&s<pojSwitche.length;s++)
	{
	    String sName="";
	    String sVal="";
	    if(pojSwitche[s].contains(":"))
	    {
		sName=pojSwitche[s].split(":")[0];
		sVal=pojSwitche[s].split(":")[2];
	    }
	    else
	    {
		sName = typ;
		sVal=pojSwitche[s];
	    }
	    String typName=typ+"|"+sName;
	    //System.out.println("TYP="+typ+"| Name="+sName+"| Value="+sVal);
	    if(!idSwitch.containsKey(typName))
	    {
		insert="insert into raport_konfiguracja_aktualna.AlgoSwitchTypes (switch_type,switch_name,switch_type_name,Last_update) values('"+typ+"','"+sName+"','"+typName+"',"+obecnyDzienCzas+")ON DUPLICATE KEY UPDATE Last_Update="+obecnyDzienCzas+";";
		this.testStatement.executeUpdate(insert);
		String zap="select switch_index from raport_konfiguracja_aktualna.AlgoSwitchTypes s where s.switch_type_name='"+typName+"'";
		ResultSet res=testStatement.executeQuery(zap);
		OdpowiedzSQL rnc=Baza.createAnswer(res);
		if(rnc.rowCount()>0)
		{
		    idSwitch.put(typName, rnc.getValue("switch_index", 0));
		}
	    }

	    if(idSwitch.containsKey(typName))
	    {
		insertC="insert into raport_konfiguracja_aktualna.ucellalgoswitch (ucell_index,switch_index,switch_value,Last_Update,Update_Status) Values("
											+ "'"+cellIndex+"',"
											+ idSwitch.get(typName)+","
											+ "'"+sVal+"',"
											+"now(),"
											+ "'NEW'"
			+ ")ON DUPLICATE KEY UPDATE Last_Update="+obecnyDzienCzas+",Update_Status='OK', switch_value='"+sVal+"'";
		testStatement.executeUpdate(insertC);
	    }
	    
	}
	return true;
    }
    catch(Exception ee)
	    {
	    System.out.println(insert+"\r\n"+insertC);
	ee.printStackTrace();
	return false;
    }

}

public boolean clearNotUpdatet()
    {
	String query="";
	 try
	 {
	     OdpowiedzSQL komorki=getNotUpdateCandidates(DataDzisiaj, this.kontrolerName);
	     for(int k=0;k<komorki.rowCount();k++)
	     {
		query="delete from raport_konfiguracja_aktualna.ucellalgoswitch where ucell_index='"+komorki.getValue("ucell_index", k)+"' and switch_index='"+komorki.getValue("switch_index", k)+"' and Last_Update='"+komorki.getValue("Last_Update", k)+"';";
		niekomercyjneClean.add(query);
	     }
	}
	 catch(Exception ee)
	    {
		loger.log(Level.FINEST,"["+this.identyfikator+" CLEANER] ERROR:"+query , ee);
		return false;
	    }

	  super.executCleanCommends();
	   // komercyjneClean.add("#####NIEKOMERCYJNE(TESTOWO W KOMERCYJNYCH)");
	   return false;
    }

public OdpowiedzSQL getNotUpdateCandidates(java.util.Date dateBefore, String kontroler)
    {
	String obecnyDzien="'"+sdf.format(dateBefore)+"'";

	try
	{
	    //testStatement = connection.createStatement();
	    //String req="select pl.* from oncall.konfiguracja_aktualna_plyty_2G pl where pl.last_update<"+obecnyDzien;
	    //String req="select pl.* from oncall.konfiguracja_aktualna_plyty_2G pl left join oncall.konfiguracja_aktualna_bts b on(b.Bts_Index=pl.ne_index) left join oncall.konfiguracja_aktualna_rnc_bsc r on (r.Rnc_Bsc_Index=b.Rnc_Bsc_Index) where(r.Rnc_Bsc_Name like '"+kontroler+"' and  pl.last_update<"+obecnyDzien+");";
	    //String req="select u.* from oncall.konfiguracja_aktualna_ucell u left join oncall.konfiguracja_aktualna_nodeb b on(b.Nodeb_Index=u.Nodeb_Index ) left join oncall.konfiguracja_aktualna_rnc_bsc r on( r.Rnc_Bsc_Index=b.Rnc_Bsc_Index ) where ( r.Rnc_Bsc_Name ='"+kontroler+"' and (  u.Last_update<"+obecnyDzien+"));";
	    //
	    String req="select a.* from raport_konfiguracja_aktualna.ucellalgoswitch a  left join oncall.konfiguracja_aktualna_ucell u on(u.Cell_Index=a.ucell_index) left join oncall.konfiguracja_aktualna_nodeb n on(n.Nodeb_Index=u.Nodeb_Index) left join oncall.konfiguracja_aktualna_rnc_bsc r on(n.Rnc_Bsc_Index=r.Rnc_Bsc_Index) where a.Last_Update<CURDATE()  and r.Rnc_Bsc_Name='"+kontroler+"';";
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
