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
public class NodebMultiCellGr_perRnc_Updater extends Updater_parrent
{
    String rnc_index;
    public NodebMultiCellGr_perRnc_Updater(String identyfikator,String kontroler,int typOperacji,Logger loger,mysqlpackage.DataSource DOA,NewFile sprzF)
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
				
				String lst=north.make(NE_Name, "LST NODEBMULTICELLGRP:");
				//System.err.println(lst);
				NPack npack=null;
				if(lst.contains("Number of results = 1)"))
				{
				    npack=new NPack(lst.split("\n"),new String[]{"LST NODEBMULTICELLGRP:"},new String[]{"----------"},new String[]{"---    END"});
				}
				else
				    npack=new NPack(lst);

				java.util.ArrayList<Paczka> mcgrLst=npack.getAllPacks();
                                if(lst.contains("RETCODE = 0"))
                                {
				for(int m=0;mcgrLst!=null&&m<mcgrLst.size();m++)
				{
				    String mcType=mcgrLst.get(m).getWartosc("Multiple Carrier Cell Group Type");
				    String mcGrNo=mcgrLst.get(m).getWartosc("Multiple Carrier Cell Group ID");
				    String lstMC=north.make(NE_Name, "LST NODEBMULTICELLGRPITEM:MULTICELLGRPID="+mcGrNo);
				    //System.out.println(lstMC);
				    String locell1="";
				    String locell2="";
				    String locell3="";
				    NPack npackll2=new NPack(lstMC);
                                    
                                    if(lstMC.contains("Number of results = 1)"))
                                    {
                                        npackll2=new NPack(lstMC.split("\n"),new String[]{"LST NODEBMULTICELLGRPITEM:"},new String[]{"----------"},new String[]{"---    END"});
                                    }
				
                                    
                                    
				    java.util.ArrayList<Paczka> mcgrLstS=npackll2.getAllPacks();
				    for(int c=0;mcgrLstS!=null&&c<mcgrLstS.size();c++)
				    {
					String locellNr= mcgrLstS.get(c).getWartosc("Local Cell ID");
					if(locellNr!=null&&locellNr.length()>0)
					{
					    if(c==0)
						locell1=locellNr;
					    if(c==1)
						locell2=locellNr;
					    if(c==2)
						locell3=locellNr;

					}
				    }
				    String gr_index=neIndex+"|"+mcGrNo;

				    if(locell1.equals(""))
					locell1="NULL";
				    if(locell2.equals(""))
					locell2="NULL";
				    if(locell3.equals(""))
					locell3="NULL";
				    String insert="insert into raport_konfiguracja_aktualna.nodeb_multicell_group (gr_index,nodeb_index,mc_gr_id,mc_gr_type,locell_1,locell_2,locell_3,Last_update,Update_Status) values ('"+gr_index+"','"+neIndex+"',"+mcGrNo+",'"+mcType+"',"+locell1+","+locell2+","+locell3+","+obecnyDzienCzas+",'NEW')ON DUPLICATE KEY UPDATE Last_Update="+obecnyDzienCzas+",Update_Status='OK', locell_1="+locell1+", locell_2="+locell2+", locell_3="+locell3;
				   // System.out.println(insertw
				    testStatement.execute(insert);

				}
                                }
			    }
			    else if(nodebType.contains("3800")||nodebType.contains("3812"))
			    {
				String lst=north.make(NE_Name, "LST NODEBMULTICELLGRP:");
                                if(lst.contains("RETCODE = 0"))
                                {
				NPack npack=new NPack(lst);
				if(lst.contains("Number of results = 1)"))
				{
				    npack=new NPack(lst.split("\n"),new String[]{"LST NODEBMULTICELLGRP:"},new String[]{"----------"},new String[]{"---    END"});
				}

				java.util.ArrayList<Paczka> mcgrLst=npack.getAllPacks();
				for(int m=0;mcgrLst!=null&&m<mcgrLst.size();m++)
				{
				    //MC Group Type  MC Group No     First LocalCell     Second LocalCell     Third LocalCell

				    String mcType=mcgrLst.get(m).getWartosc("MC Group Type");
				    String mcGrNo=mcgrLst.get(m).getWartosc("MC Group No");
				    String locell1=mcgrLst.get(m).getWartosc("First LocalCell").trim();
				    String locell2=mcgrLst.get(m).getWartosc("Second LocalCell").trim();
				    String locell3=mcgrLst.get(m).getWartosc("Third LocalCell").trim();
				    String gr_index=neIndex+"|"+mcGrNo;

				    if(locell1.equals(""))
					locell1="NULL";
				    if(locell2.equals(""))
					locell2="NULL";
				    if(locell3.equals(""))
					locell3="NULL";
				    String insert="insert into raport_konfiguracja_aktualna.nodeb_multicell_group (gr_index,nodeb_index,mc_gr_id,mc_gr_type,locell_1,locell_2,locell_3,Last_update,Update_Status) values ('"+gr_index+"','"+neIndex+"',"+mcGrNo+",'"+mcType+"',"+locell1+","+locell2+","+locell3+","+obecnyDzienCzas+",'NEW')ON DUPLICATE KEY UPDATE Last_Update="+obecnyDzienCzas+",Update_Status='OK', locell_1="+locell1+", locell_2="+locell2+", locell_3="+locell3;
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
		    String gr_index=komorki.getValue("gr_index", p);

		    niekomercyjneClean.add("delete from raport_konfiguracja_aktualna.nodeb_multicell_group where gr_index='"+gr_index+"';");
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
	    String req="select u.* from raport_konfiguracja_aktualna.nodeb_multicell_group u  where ( u.Last_update<"+obecnyDzien+" and u.gr_index like '"+rnc_index+"|%');";
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
