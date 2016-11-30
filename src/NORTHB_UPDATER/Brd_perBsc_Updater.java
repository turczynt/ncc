package NORTHB_UPDATER;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.sql.Connection;
import java.sql.Date;
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
public class Brd_perBsc_Updater extends Updater_parrent	//extends Thread{
{
    StringBuffer insertyAll;
   

    public Brd_perBsc_Updater(String identyfikator,String bscName,int typOperacji,Logger loger,mysqlpackage.DataSource DOA,NewFile sprzF)
    {
	//
	super(identyfikator,bscName,typOperacji,loger,DOA,sprzF);
	this.insertyAll=new StringBuffer();
    }

    @Override
    
    public boolean add() throws java.sql.SQLException
    {
	java.util.ArrayList<String> lstExist=new java.util.ArrayList<String>();
	String obecnyDzienCzas="'"+sdf.format(DataDzisiaj)+"'";
	try
	{
	  //  testStatement = connection.createStatement();
	    String req="select r.Rnc_Bsc_Index, r.Rnc_Bsc_Id, r.Rnc_Bsc_Name, m.M2000_Ip, r.M2000_Index from oncall.konfiguracja_aktualna_rnc_bsc r,  oncall.konfiguracja_aktualna_m2000 m where(r.m2000_index=m.M2000_Index and r.Rnc_Bsc_Name like '%"+this.kontrolerName+"%') LIMIT 1";
	    ResultSet res=testStatement.executeQuery(req);
	    OdpowiedzSQL rnc=Baza.createAnswer(res);
	    String requets="select p.index,b.Bts_Name, r.rnc_bsc_name from oncall.konfiguracja_aktualna_plyty_2G p,oncall.konfiguracja_aktualna_bts b, oncall.konfiguracja_aktualna_rnc_bsc r where ( p.ne_index=b.bts_index and b.rnc_bsc_index=r.rnc_bsc_index)";
	    ResultSet brdTmP=testStatement.executeQuery(requets);
	    OdpowiedzSQL brdExist=Baza.createAnswer(brdTmP);
   
	    for(int w=0;w<brdExist.rowCount();w++)
	    {
		String IndexBrd=brdExist.getValue("index", w).trim();
		if(IndexBrd!=null&&!IndexBrd.equals(""))
		    lstExist.add(IndexBrd);
	    }
	    String m2000_ip=rnc.getValue("M2000_Ip", 0);
	    String bsc_name=rnc.getValue("Rnc_Bsc_Name", 0);
	    String bsc_id=rnc.getValue("Rnc_Bsc_Id",0);
	    String bsc_index=rnc.getValue("Rnc_Bsc_Index", 0);
	    String m2000_Index=rnc.getValue("M2000_Index", 0);
	    north=new nbipackage.NorthB(m2000_ip, "U-boot", "utranek098",null);

	    java.util.ArrayList<Paczka> btsy=new java.util.ArrayList<Paczka>();
	    String lstBts=north.make(this.kontrolerName, "LST BTS:");

	    if(lstBts.contains("RETCODE = 0"))
	    {
		NPack np=new NPack(lstBts);
		btsy=np.getAllPacks();
	    }
	    int licznik=0;
	    for(int w=0;w<btsy.size();w++)
	    {


		String BtsName=btsy.get(w).getWartosc("BTS Name").trim();
		String BtsId=btsy.get(w).getWartosc("BTS Index").trim();
		String bts_indexDB=bsc_index+"|"+BtsId;
		if(!BtsName.equals("")&&!BtsId.equals(""))
		{
		String dspBRD=north.make(this.kontrolerName, "DSP BTSBRD: INFOTYPE=INPOSBRD, IDTYPE=BYNAME, BTSNAME=\""+BtsName+"\"");
		if(licznik==50||w==(btsy.size()-1))
		{
		    System.out.println(this.identyfikator+" "+w+"/"+btsy.size()+" "+java.util.Calendar.getInstance().getTime());
		    licznik=0;
		}
		licznik++;
		if(dspBRD.contains("RETCODE = 0"))
		{
		    NPack nn=new NPack(dspBRD);
		    java.util.ArrayList<Paczka> listBrd=nn.getAllPacks();
		    for(int d=0;d<listBrd.size();d++)
		    {
			String cn=listBrd.get(d).getWartosc("Cabinet No.").trim();
			String srn=listBrd.get(d).getWartosc("Subrack No.").trim();
			String sn=listBrd.get(d).getWartosc("Slot No.").trim();
			String conf_typ=listBrd.get(d).getWartosc("Configuration Board Type").trim();
			String physical_conf_typ=listBrd.get(d).getWartosc("Physical Board Type").trim();
			String band=listBrd.get(d).getWartosc("Frequency Band").trim();
			String barCode=listBrd.get(d).getWartosc("Bar Code").trim();
			String klucz=cn+"|"+srn+"|"+sn;
			String plytaIndex=bts_indexDB+"|"+klucz;


			if(cn!=null&&cn.matches("[0-9]*")&&sn!=null&&sn.matches("[0-9]*")&&srn!=null&&srn.matches("[0-9]*"))
			{
			    String ins="INSERT INTO `oncall`.`konfiguracja_aktualna_plyty_2G` (`ne_index`, `cn`, `srn`, `sn`, `config_type`, `rfu_working_mode`, `mfrinfo_serial_nr`, `last_update`, `update_status`, `index`) values ('"+bts_indexDB+"',"+cn+","+srn+","+sn+",'"+conf_typ+"','"+band+"','"+barCode+"',"+obecnyDzienCzas+",'NEW','"+plytaIndex+"') ON DUPLICATE KEY UPDATE `config_type`='"+conf_typ+"',`rfu_working_mode`='"+band+"',`mfrinfo_serial_nr`='"+barCode+"',`last_update`="+obecnyDzienCzas+",`update_status`='OK'";
			    //System.out.println(ins);
			    //testStatement.execute(ins);
			    insertyAll.append(ins+";\r\n");
			    if(plytaIndex!=null&&!plytaIndex.equals("")&&lstExist.contains(plytaIndex))
				lstExist.remove(plytaIndex);
			}
			
		    }																												  //index, ne_index, cn,srn,sn,config_type,availability_status,operational_status,rfu_working_mode,mfrinfo_type,mfrinfo_description,last_update,update_status

		}
		else if(dspBRD.contains("The OML of BTS is faulty"))
		{
		    String upd="UPDATE oncall.konfiguracja_aktualna_plyty_2G set `last_update`="+obecnyDzienCzas+", update_status='OML_BTS_FAULTY' where ne_index='"+bts_indexDB+"'";
		    insertyAll.append(upd+";\r\n");
		}

		}
	    }
	    //System.out.println("POZOSTALO NIE ZMODYFIKOWANYCH:"+lstExist);
	    //sukces=true;
	    try{

			String[] komendy=insertyAll.toString().split(";");
			int bathRoz=0;
			for(int k=0;k<komendy.length;k++)
			{
			    //testStatement.addBatch(komendy[k]);
			    if(komendy[k]!=null&&!komendy[k].trim().equals(""))
			    {
				try{
				testStatement.executeUpdate(komendy[k]);
				}
				catch (SQLException ex)
				{
				    loger.log(Level.FINER,"["+this.identyfikator+"]"+komendy[k], ex);
				    
				    ex.printStackTrace();

				}
				catch (Exception ex)
				{
				    loger.log(Level.FINER,"["+this.identyfikator+"]"+komendy[k], ex);
				    ex.printStackTrace();

				}

			    }
			    if(bathRoz==1000||k==(komendy.length-1))
			    {
				    //int[] wyniki=testStatement.executeBatch();
				    //testStatement.clearBatch();
				    System.out.println(this.identyfikator+" wykonanno "+k+"/"+komendy.length);
				    bathRoz=0;
			    }
			    bathRoz++;
			}
			//int[] wyniki=testStatement.executeBatch();
			//testStatement.clearBatch();
			sukces=true;

			}
			catch(Exception batchExc)
			{
			   loger.throwing(this.getClass().getName()+"."+this.identyfikator,"batchExc", batchExc);
			    sukces=false;
			}
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
	OdpowiedzSQL doPoprawki=getNotUpdateCandidates(DataDzisiaj, this.kontrolerName);
	for(int p=0;p<doPoprawki.rowCount();p++)
	{
		String delSt="DELETE FROM oncall.konfiguracja_aktualna_plyty_2G WHERE oncall.konfiguracja_aktualna_plyty_2G.index = '"+doPoprawki.getValue("index", p)+"'";
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
	    String req="select pl.* from oncall.konfiguracja_aktualna_plyty_2G pl left join oncall.konfiguracja_aktualna_bts b on(b.Bts_Index=pl.ne_index) left join oncall.konfiguracja_aktualna_rnc_bsc r on (r.Rnc_Bsc_Index=b.Rnc_Bsc_Index) where(r.Rnc_Bsc_Name like '"+kontroler+"' and  pl.last_update<"+obecnyDzien+");";

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


    /*public String odp()
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
    }*/

    /*private String readyForCoaStat(NorthB north,String nodeName,String system,String nodebType)
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
    }*/

    //boolean done;
}