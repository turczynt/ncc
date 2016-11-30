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
public class Brd_perRnc_Updater extends Updater_parrent	//extends Thread{
{
    public Brd_perRnc_Updater(String identyfikator,String rncName,int typOperacji,Logger loger,mysqlpackage.DataSource DOA,NewFile sprzF)
    {
	super(identyfikator,rncName,typOperacji,loger,DOA,sprzF);
    }

    @Override
    public boolean add() throws java.sql.SQLException
    {

	java.util.Hashtable<String,Integer> NodeNameBand=new java.util.Hashtable<String, Integer>();
	String obecnyDzienCzas="'"+sdf.format(DataDzisiaj)+"'";
	try
	{
	   

	    String req="select r.Rnc_Bsc_Index, r.Rnc_Bsc_Id, r.Rnc_Bsc_Name, m.M2000_Ip, r.M2000_Index from oncall.konfiguracja_aktualna_rnc_bsc r,  oncall.konfiguracja_aktualna_m2000 m where(r.m2000_index=m.M2000_Index and r.Rnc_Bsc_Name like '%"+this.kontrolerName+"%') LIMIT 1";
	    ResultSet res=testStatement.executeQuery(req);
	    OdpowiedzSQL rnc=Baza.createAnswer(res);
	    String requets="select t.NodebName,t.NodebType,t.Ne_Index,n.Site_Index,n.Nodeb_Id,n.Rnc_Bsc_Index,n.Nodeb_Index from oncall.konfiguracja_aktualna_nodeb n, oncall.konfiguracja_aktualna_NodebType t where ( t.Ne_Index=n.Nodeb_Index and n.Rnc_Bsc_Index = (select Rnc_Bsc_Index from oncall.konfiguracja_aktualna_rnc_bsc where Rnc_Bsc_Name like '%"+this.kontrolerName+"%' LIMIT 1) )";
	    ResultSet allNode=testStatement.executeQuery(requets);
	    OdpowiedzSQL nody=Baza.createAnswer(allNode);
	    String m2000_ip=rnc.getValue("M2000_Ip", 0);
	    String rnc_name=rnc.getValue("Rnc_Bsc_Name", 0);
	    String rnc_id=rnc.getValue("Rnc_Bsc_Id",0);
	    String rnc_index=rnc.getValue("Rnc_Bsc_Index", 0);
	    String m2000_Index=rnc.getValue("M2000_Index", 0);
	    north=new nbipackage.NorthB(m2000_ip, "U-boot", "utranek098",null);
	    for(int w=0;w<nody.rowCount();w++)
	    {
		String nodeName=nody.getValue("NodebName", w);
		try
		{
		    String nodeIndex=nody.getValue("Nodeb_Index", w);
		    String nodebType=nody.getValue("NodebType", w);
		    String dspBRD=north.make(nodeName, "DSP BRD:");
		    boolean dbs3900=false;
		    boolean dbs3800=false;
		    if(nodebType.contains("3900"))
			dbs3900=true;
		    if(nodebType.contains("3800"))
			dbs3800=true;
		    if(dspBRD.contains("RETCODE = 0"))
		    {
			NPack nn=new NPack(dspBRD);
			java.util.ArrayList<Paczka> listBrd=nn.getAllPacks();
			java.util.Hashtable<String,Paczka> RRU=new java.util.Hashtable<String,Paczka>();
			java.util.Hashtable<String,Paczka> BRDMRF=new java.util.Hashtable<String,Paczka>();
			if(dbs3900)
			{
			    String rru=north.make(nodeName, "LST RRU:");
			    if(rru.contains("RETCODE = 0"))
			    {
				NPack np=new NPack(rru);
				java.util.ArrayList<Paczka> rruL=np.getAllPacks();
				if(rru.contains("(Number of results = 1)"))
				{
				    np = new NPack(rru.split("\n"),new String[]{"LST RRU:"},new String[]{"-----"},new String[]{"---    END"});
				    rruL=np.getAllPacks();
				}
				for(int z=0;z<rruL.size();z++)
				{
				    String cnRRU=rruL.get(z).getWartosc("Cabinet No.");
				    String srnRRU=rruL.get(z).getWartosc("Subrack No.");
				    String snRRU=rruL.get(z).getWartosc("Slot No.");
				    String klucz=cnRRU+"|"+srnRRU+"|"+snRRU;
				    RRU.put(klucz, rruL.get(z));
				    String mfr=north.make(nodeName,"DSP BRDMFRINFO:CN="+cnRRU+",SRN="+srnRRU+",SN="+snRRU);
				    /*BRDMFRINFO TYLKO DLA PLYT RRU*/
				    if(mfr.contains("RETCODE = 0"))
				    {
					//System.out.println("KLUCZ ADD="+nodeName+";"+klucz+";");
					NPack npm=new NPack(mfr.split("\n"),new String[]{"DSP BRDMFRINFO:"},new String[]{"-----"},new String[]{"---    END"});
					java.util.ArrayList<Paczka> mfrL=npm.getAllPacks();
					if(mfrL!=null&&mfrL.size()>0)
					{
					    BRDMRF.put(klucz, mfrL.get(0));
					}
				    }
				    else if(mfr.contains("This board cannot be operated"))
				    {
					Paczka mf=new Paczka();
					mf.dodaj("ERROR", "This board cannot be operated");
					BRDMRF.put(klucz, mf);
				    }
				}
			    }
			    /*BRDMFRINFO DLA WSZYSTKICH PLYT
			    for(int d=0;d<listBrd.size();d++)
			    {
				String cn=listBrd.get(d).getWartosc("Cabinet No.");
				String srn=listBrd.get(d).getWartosc("Subrack No.");
				String sn=listBrd.get(d).getWartosc("Slot No.");
				String mfr=north.make(nodeName,"DSP BRDMFRINFO:CN="+cn+",SRN="+srn+",SN="+sn);
				String klucz=cn+"|"+srn+"|"+sn;
					if(mfr.contains("RETCODE = 0"))
					{
					    NPack npm=new NPack(mfr.split("\n"),new String[]{"DSP BRDMFRINFO:"},new String[]{"-----"},new String[]{"---    END"});
					    java.util.ArrayList<Paczka> mfrL=npm.getAllPacks();
					    if(mfrL!=null&&mfrL.size()>0)
					    {
						BRDMRF.put(klucz, mfrL.get(0));
					    }
					}
			    }*/
			}
			for(int d=0;d<listBrd.size();d++)
			{
			    String cn=listBrd.get(d).getWartosc("Cabinet No.");
			    String srn=listBrd.get(d).getWartosc("Subrack No.");
			    String sn=listBrd.get(d).getWartosc("Slot No.");
			    String conf_typ="";
			    //if(dbs3900)
			    conf_typ=listBrd.get(d).getWartosc("Config Type").trim();
			    if(conf_typ.equals(""))
				conf_typ=listBrd.get(d).getWartosc("Board Type");

			    String avabStat=listBrd.get(d).getWartosc("Availability Status");
			    if(avabStat.length()>30)
				avabStat=avabStat.substring(0, 29);
			    String operStat="";

			    operStat=listBrd.get(d).getWartosc("Operational State").trim();
			    if(operStat.equals(""))
				operStat=listBrd.get(d).getWartosc("Operate State");

			    if(conf_typ.equals("")||avabStat.equals("")||operStat.equals(""))
				System.err.println("BLEDNE DANE "+dspBRD);

			    String klucz=cn+"|"+srn+"|"+sn;
			    String rfu="";
			    String plytaIndex=nodeIndex+"|"+klucz;
			    if(RRU.containsKey(klucz))
			    {
				rfu=RRU.get(klucz).getWartosc("RF Unit Working Mode");
			    }
			    String mfrinfo_type="";
			    String mfrinfo_serial_nr="";
			    String mfrinfo_description="";
			    String updateStat="OK";
			    if(BRDMRF.containsKey(klucz))
			    {
				String err=BRDMRF.get(klucz).getWartosc("ERROR");
				mfrinfo_type=BRDMRF.get(klucz).getWartosc("Type");
				mfrinfo_serial_nr=BRDMRF.get(klucz).getWartosc("Serial Number");
				mfrinfo_description=BRDMRF.get(klucz).getWartosc("Description");
				if(mfrinfo_description.length()>=150)
				    mfrinfo_description=mfrinfo_description.substring(0, 149);
				if(err!=null&&!err.equals(""))
				    updateStat=err;
			    }
			    String ins="INSERT INTO `oncall`.`konfiguracja_aktualna_plyty_3G` (`ne_index`, `cn`, `srn`, `sn`, `config_type`, `availability_status`, `operational_status`, `rfu_working_mode`, `mfrinfo_type`, `mfrinfo_serial_nr`, `mfrinfo_description`, `last_update`, `update_status`, `index`) values ('"+nodeIndex+"',"+cn+","+srn+","+sn+",'"+conf_typ+"','"+avabStat+"','"+operStat+"','"+rfu+"','"+mfrinfo_type+"','"+mfrinfo_serial_nr+"','"+mfrinfo_description+"',"+obecnyDzienCzas+",'"+updateStat+"','"+plytaIndex+"') ON DUPLICATE KEY UPDATE `config_type`='"+conf_typ+"',`availability_status`='"+avabStat+"',`operational_status`='"+operStat+"',`rfu_working_mode`='"+rfu+"',`mfrinfo_type`='"+mfrinfo_type+"',`mfrinfo_serial_nr`='"+mfrinfo_serial_nr+"',`mfrinfo_description`='"+mfrinfo_description+"',`last_update`="+obecnyDzienCzas+",`update_status`='"+updateStat+"'";
			    testStatement.execute(ins);
			}
		    }
		    else
		    {
			String upd="";
			System.err.println("NIE UDALO SIE LIST PLYT DLA STACJI("+nodeIndex+")"+nodeName+" "+nodebType);
			if(nodebType.contains("NE_NOT"))
			{
			    upd="UPDATE `oncall`.`konfiguracja_aktualna_plyty_3G` SET `last_update`="+obecnyDzienCzas+",`update_status`='"+nodebType+"'  WHERE ne_index='"+nodeIndex+"';";
			}
			else
			{
			    String connTest=north.make2("REG NE:NAME=\""+nodeName+"\"");
			    String upSt="";
			    if(connTest.contains("NE does not Connection"))
				upSt="NE_NOT_CONNECTED";
			    else if(connTest.contains("Can't Found NE"))
				upSt="NE_NOT_EXIST";
			    else
				upSt="ERROR";
			    upd="UPDATE `oncall`.`konfiguracja_aktualna_plyty_3G` SET `last_update`="+obecnyDzienCzas+",`update_status`='"+upSt+"'  WHERE ne_index='"+nodeIndex+"';";
			}
			if(!upd.equals(""))
			    testStatement.executeUpdate(upd);
		    }
		}
		catch(Exception ee)
		{
		    loger.throwing(this.identyfikator, "["+nodeName+"]ERROR", ee);
		    try
		    {
			north.closeBuffor();
			north=null;
			north=north=new nbipackage.NorthB(m2000_ip, "U-boot", "utranek098",null);
		    }
		    catch(Exception es)
		    {
			loger.throwing(this.identyfikator, "["+nodeName+"]ERROR", es);
		    }
		}
	    }
	    sukces=true;
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	    loger.throwing(this.getClass().getName()+"."+this.identyfikator,"add", e);
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
	    if(connection!=null)
	    try
	    {
		
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
	OdpowiedzSQL doPoprawki=getNotUpdateCandidates(DataDzisiaj, this.kontrolerName);
	for(int p=0;p<doPoprawki.rowCount();p++)
	{
		String delSt="DELETE FROM oncall.konfiguracja_aktualna_plyty_3G WHERE oncall.konfiguracja_aktualna_plyty_3G.index = '"+doPoprawki.getValue("index", p)+"'";
		//testStatement.executeUpdate(delSt);
		niekomercyjneClean.add(delSt+";");
	    }
	super.executCleanCommends();
	return true;
    }

    @Override
    public OdpowiedzSQL getNotUpdateCandidates(java.util.Date dateBefore, String kontroler)
    {
	String obecnyDzien="'"+sdf.format(dateBefore)+"'";

	try
	{
	  //  testStatement = connection.createStatement();
	    //String req="select pl.* from oncall.konfiguracja_aktualna_plyty_2G pl where pl.last_update<"+obecnyDzien;
	    //String req="select pl.* from oncall.konfiguracja_aktualna_plyty_2G pl left join oncall.konfiguracja_aktualna_bts b on(b.Bts_Index=pl.ne_index) left join oncall.konfiguracja_aktualna_rnc_bsc r on (r.Rnc_Bsc_Index=b.Rnc_Bsc_Index) where(r.Rnc_Bsc_Name like '"+kontroler+"' and  pl.last_update<"+obecnyDzien+");";
	    String req="select pl.* from oncall.konfiguracja_aktualna_plyty_3G pl left join oncall.konfiguracja_aktualna_nodeb b on(b.Nodeb_Index=pl.ne_index) left join oncall.konfiguracja_aktualna_rnc_bsc r on (r.Rnc_Bsc_Index=b.Rnc_Bsc_Index) where(r.Rnc_Bsc_Name like '"+kontroler+"' and  pl.last_update<"+obecnyDzien+");";
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

}


/*
 *  private String readyForCoaStat(NorthB north,String nodeName,String system,String nodebType)
    {
	//readyForCoa

	//0-nie gotowe;

	//1-U2100-gotowe

	//8-u900-gotowe

	//9- u900 oraz u2100 ready_for_coa
	//System.out.println("CHECK READ COA:"+nodeName+" "+system+" "+nodebType);
	String outReady="NOT_READY";
	if(nodebType.contains("3900"))
	    outReady=readyForCoaStat3900(north,nodeName,system);
	if(nodebType.contains("3800"))
	    outReady=readyForCoaStat3800(north,nodeName,system);



	return outReady;
    }

    private String readyForCoaStat3900(NorthB north,String nodeName,String system)
    {
	//readyForCoa

	//0-nie gotowe;

	//1-U2100-gotowe

	//8-u900-gotowe

	//9- u900 oraz u2100 ready_for_coa




	return "NOT_READY";
    }
    private String readyForCoaStat3800(NorthB north,String node_name,String system)
    {
	String status="";
	try
	{
	    String odp=north.make(node_name, "DSP BRD:");
	    //System.out.println(odp);


	     int powtorka=0;
	     int sleepTime=5000;
	     while(!odp.contains("RETCODE = 0")&&powtorka<5)
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
		boolean mrru=false;
		for(int p=0;p<plyty.size();p++)
		{
		    if(plyty.get(p).getWartosc("Board Type").equalsIgnoreCase("MRRU"))
		    {
			//
			String OpStat=plyty.get(p).getWartosc("Operate State").trim();
			String AvStat=plyty.get(p).getWartosc("Availability Status").trim();

			String klucz=plyty.get(p).getWartosc("Cabinet No.")+","+plyty.get(p).getWartosc("Subrack No.")+","+plyty.get(p).getWartosc("Slot No.")+";"+OpStat+","+AvStat;
			if(OpStat.equalsIgnoreCase("Enabled")&&AvStat.equalsIgnoreCase("Normal"))
			{

			    return "T2100:"+klucz;
			}
			else
			{
			    status=status+klucz+"|";
			}
			mrru=true;
		    }

		}
		if(mrru)
		    return "F2100:"+status;
		else
		    return "F2100:BRAK_MRRU";
	    }
	    else
	    {
		System.err.println(odp);

		if(odp.contains("RETCODE")&&!odp.contains("RETCODE = 1"))
		    return "F2100:"+NewFile.getLinia("RETCODE", odp)[0];
		else
		{
		    String info=north.make2("REG NE:NAME=\""+node_name+"\"").replaceAll("'", "");
		    if(info.contains("RETCODE"))
			info=NewFile.getFirstLine(new String[]{"RETCODE"}, info);
		    if(info.length()>40)
			info=info.substring(0, 39);
		    if(info.contains("RETCODE = 0"))
			return "F2100:"+info;
		    else
			return "E2100:"+info;
		}
	    }
	}
	catch(Exception ee)
	{
	    ee.printStackTrace();
	    return "E2100:"+ee.toString();
	}

    }
    private String readyForCoaStat3812(NorthB north,String nodeName,String system)
    {
	//readyForCoa

	//0-nie gotowe;

	//1-U2100-gotowe

	//8-u900-gotowe

	//9- u900 oraz u2100 ready_for_coa
	
	return "NOT_READY";
    }

    boolean done;
 */