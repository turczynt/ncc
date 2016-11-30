/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package NORTHB_UPDATER;

import NORTHB_UPDATER.Updater_parrent;
import java.sql.ResultSet;
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
public class NodebRXATTEN_perRnc_Updater extends Updater_parrent
{
    String rnc_index;
    public NodebRXATTEN_perRnc_Updater(String identyfikator,String kontroler,int typOperacji,Logger loger,mysqlpackage.DataSource DOA,NewFile sprzF)
    {
	super(identyfikator,kontroler,typOperacji,loger,DOA,sprzF);
    }
    @Override
    public boolean add() throws java.sql.SQLException
    {
	String obecnyDzienCzas="'"+sdf.format(DataDzisiaj)+"'";
	sukces=true;
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
		    rnc_index=rnc.getValue("Rnc_Bsc_Index", 0);
		    String m2000_Index=rnc.getValue("M2000_Index", 0);
		    north=new nbipackage.NorthB(m2000_ip, "U-boot", "utranek098",null);
		    String nodReq="select nt.NodebName as NE_Name,nt.Ne_index, nn.Nodeb_Name as Nodeb_Name_On_Rnc, nn.NodebType, r.Rnc_Bsc_Name,m.M2000_Ip  from oncall.konfiguracja_aktualna_NodebType nt, oncall.konfiguracja_aktualna_nodeb nn, oncall.konfiguracja_aktualna_m2000 m, oncall.konfiguracja_aktualna_rnc_bsc r where (nn.Rnc_Bsc_Index='"+rnc_index+"' and nn.Nodeb_Index=nt.Ne_index and nn.Rnc_Bsc_Index=r.Rnc_Bsc_Index and m.M2000_Index =r.M2000_Index)";
		    ResultSet resN=testStatement.executeQuery(nodReq);
		    OdpowiedzSQL nody=Baza.createAnswer(resN);
		    //System.out.println(nody);
		    int il=0;
		    for(int i=0;i<nody.rowCount();i++)
		    {
			try
			{
			    il++;
			    if(il==500||i==(nody.rowCount()-1))
			    {
				System.out.println("["+this.identyfikator+"]"+i+"/"+nody.rowCount());
				il=0;

			    }
			    String NE_Name=nody.getValue("NE_Name", i);
			    String nodebType=nody.getValue("NodebType", i);
			    String neIndex=nody.getValue("Ne_index", i);
			  //  System.out.println("NE_Name="+NE_Name);
			    if(nodebType.contains("3900"))
			    {

				String lst=north.make(NE_Name, "LST RXBRANCH:");
				//System.err.println(lst);
				NPack npack=new NPack(lst);

				java.util.ArrayList<Paczka> mcgrLst=npack.getAllPacks();
				for(int m=0;mcgrLst!=null&&m<mcgrLst.size();m++)
				{
				    //Cabinet No.  Subrack No.  Slot No.  RX Channel No. Attenuation(0.5dB)
				    String cn=mcgrLst.get(m).getWartosc("Cabinet No.");
				    String srn=mcgrLst.get(m).getWartosc("Subrack No.");
				    String sn=mcgrLst.get(m).getWartosc("Slot No.");

				    String rxChan=mcgrLst.get(m).getWartosc("RX Channel No.");
				    String attenValue=mcgrLst.get(m).getWartosc("Attenuation(0.5dB)");
				    String fail=mcgrLst.get(m).getWartosc("Failure Cause");
    				    String update_stat="NEW";
				    if(fail==null||fail.equals(""))
					update_stat=fail;
				    if(cn.equals("")||srn.equals("")||sn.equals(""))
					    ;
				    else if (rxChan.equals(""))
				    {
					String insert="Update `raport_konfiguracja_aktualna`.`nodeb_rxattenuation` set last_update_date="+obecnyDzienCzas+",update_state='"+fail+"' where nodeb_index='"+neIndex+"' and cn="+cn+" and sn="+sn+" and srn="+srn;
									    testStatement.execute(insert);
				    }
				    else
				    {
				    String insert="INSERT INTO `raport_konfiguracja_aktualna`.`nodeb_rxattenuation` (nodeb_index,cn,srn,sn,rx_chan_no,rx_attenuation,create_date,last_update_date,update_state) VALUES ('"+neIndex+"','"+cn+"','"+srn+"','"+sn+"','"+rxChan+"','"+attenValue+"',(now()),"+obecnyDzienCzas+",'"+update_stat+"')  ON DUPLICATE KEY UPDATE rx_attenuation='"+attenValue+"',last_update_date="+obecnyDzienCzas+",update_state='UPDATE' ";

				   // System.out.println(insert);
				    testStatement.execute(insert);
				    }

				}
			    }
			    else if(nodebType.contains("3800")||nodebType.contains("3812"))
			    {
				String lst=north.make(NE_Name, "LST RXATTEN:");
				//System.err.println(lst);
				NPack npack=new NPack(lst);

				java.util.ArrayList<Paczka> mcgrLst=npack.getAllPacks();
				for(int m=0;mcgrLst!=null&&m<mcgrLst.size();m++)
				{
				    //Cabinet No.  Subrack No.  Slot No.  Rx Channel No.  Attenuation of Rx Channel(dB)
				    String cn=mcgrLst.get(m).getWartosc("Cabinet No.");
				    String srn=mcgrLst.get(m).getWartosc("Subrack No.");
				    String sn=mcgrLst.get(m).getWartosc("Slot No.");

				     String rxChan=mcgrLst.get(m).getWartosc("Rx Channel No.");
				    String attenValue=mcgrLst.get(m).getWartosc("Attenuation of Rx Channel(dB)");

				 String fail=mcgrLst.get(m).getWartosc("Failure Cause");
				    String update_stat="NEW";
				    if(fail==null||fail.equals(""))
					update_stat=fail;

				    if(cn.equals("")||srn.equals("")||sn.equals(""))
					    ;
				    else if (rxChan.equals(""))
				    {
					String insert="Update `raport_konfiguracja_aktualna`.`nodeb_rxattenuation` set last_update_date="+obecnyDzienCzas+",update_state='"+fail+"' where nodeb_index='"+neIndex+"' and cn="+cn+" and sn="+sn+" and srn="+srn;
									    testStatement.execute(insert);
				    }
				    else
				    {
				    String insert="INSERT INTO `raport_konfiguracja_aktualna`.`nodeb_rxattenuation` (nodeb_index,cn,srn,sn,rx_chan_no,rx_attenuation,create_date,last_update_date,update_state) VALUES ('"+neIndex+"','"+cn+"','"+srn+"','"+sn+"','"+rxChan+"','"+attenValue+"',(now()),"+obecnyDzienCzas+",'"+update_stat+"')  ON DUPLICATE KEY UPDATE rx_attenuation='"+attenValue+"',last_update_date="+obecnyDzienCzas+",update_state='UPDATE' ";

				    //System.out.println(insert);
				    testStatement.execute(insert);
				    }

				}
			    }
			    else
			    {
				//loger.log(Level.WARNING, "NIEROZPOZNANY TYP:"+NE_Name+" "+nodebType);

			    }
			}
			catch(Exception ee)
			{
			    loger.log(Level.FINEST, "["+this.identyfikator+"] ERROR:", ee);
			    ee.printStackTrace();
			}
		    }
		}
	    }
	    catch(Exception e)
	    {
		e.printStackTrace();
		loger.throwing(this.getClass().toString(), this.identyfikator+".add", e);
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


     public boolean clearNotUpdatet()
    {
	 try
	 {

	     java.util.ArrayList<String> poleceniaNK=new java.util.ArrayList<String>();
	     String obecnyDzien="'"+sdf.format(DataDzisiaj)+"'";
	     OdpowiedzSQL komorki=getNotUpdateCandidates(DataDzisiaj, this.kontrolerName);


	    for(int p=0;komorki!=null&&p<komorki.rowCount();p++)
	    {
		try
		{
		    String rowAuto_inc=komorki.getValue("rowAuto_inc", p);
		    niekomercyjneClean.add("delete from `raport_konfiguracja_aktualna`.`nodeb_rxattenuation` where rowAuto_inc='"+rowAuto_inc+"';");
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
    }

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
	    String req="select u.* from  `raport_konfiguracja_aktualna`.`nodeb_rxattenuation` u  where ( u.Last_update_date<"+obecnyDzien+" and u.nodeb_index like '"+rnc_index+"|%');";
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
