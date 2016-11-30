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

public class DevipNe_perM200_Updater extends Updater_parrent	//extends Thread{
{
   
    public DevipNe_perM200_Updater(String identyfikator,String M2000,int typOperacji,Logger loger,mysqlpackage.DataSource DOA,NewFile sprzF)
    {
	super(identyfikator,M2000,typOperacji,loger,DOA,sprzF);
    }
    String m2000_Index;
    @Override
   public boolean add() throws java.sql.SQLException
    {
	
	this.errorInfo=this.identyfikator;
	java.util.Hashtable<String,Integer> NodeNameBand=new java.util.Hashtable<String, Integer>();
	sukces=false;

	String obecnyDzienCzas="'"+sdf.format(DataDzisiaj)+"'";

	try
	{
	    this.errorInfo=this.errorInfo+"; DOA.connected,createStatement";
	    String req="select m.M2000_Name, m.M2000_Ip, m.M2000_Index from  oncall.konfiguracja_aktualna_m2000 m where(m.M2000_Name like '%"+this.kontrolerName+"%') LIMIT 1";
	    ResultSet res=testStatement.executeQuery(req);
	    OdpowiedzSQL m2000Info=Baza.createAnswer(res);
	    
            this.errorInfo=this.errorInfo+"; get M2000 info for name="+this.kontrolerName;
	    if(m2000Info.rowCount()>0)
	    {
		java.util.Hashtable<String,Paczka> NBfromNorth=new java.util.Hashtable<String, Paczka>();
		String m2000_ip=m2000Info.getValue("M2000_Ip", 0);
		String rnc_name=m2000Info.getValue("M2000_Name", 0);
		m2000_Index=m2000Info.getValue("M2000_Index", 0);
		north=new nbipackage.NorthB(m2000_ip, "U-boot", "utranek098",null);
		this.errorInfo=this.errorInfo+"; connect with "+m2000_ip+" by NorthB";
		String NE_LST=north.make2("LST NE:");
		if(NE_LST.contains("Login or Register needed"))
		{
		    NE_LST=north.make2("LST NEBYOMC:");
		}
                String[] inNE=NE_LST.split("\n");
                java.util.ArrayList<String> NeNames=new java.util.ArrayList<String>();
		for(int w=0;w<inNE.length;w++)
		{
		    if(inNE[w].contains("BTS3900NE")||inNE[w].contains("eNodeBNE")||inNE[w].contains("NodeBNE"))
		    {
			String ne_name=NewFile.getTokens(inNE[w], "2", "2").trim();
			if(!NeNames.contains(ne_name))
                            NeNames.add(ne_name);
		    }
                }
                
                for(int z=0;z<NeNames.size();z++)
                {
                    String NE=NeNames.get(z);
                    try{
                    String inNodeb=north.make(NeNames.get(z), "LST DEVIP:");
                    
                    if(inNodeb.contains("RETCODE = 0"))
                    {
                        String update_stat=null;
                        java.util.ArrayList<Paczka> devices=null;
                        if(inNodeb.contains("Number of results = 1)"))//pojedynczy devip,formatowanie pionowe
                        {
                            NPack npack = new NPack(inNodeb.split("\n"), new String[]{"LST DEVIP:"}, new String[]{"----------"}, new String[]{"---    END"});
                            devices=npack.getAllPacks();
                        }
                        else //wiecej niz jeden devip,formatowanie poziome
                        {
                            NPack nn=new NPack(inNodeb);
                            devices=nn.getAllPacks();
                        }
                        if(devices!=null&&devices.size()>0)
                        {
                            for(int d=0;d<devices.size();d++)
                            {
                                String ip_adres=devices.get(d).getWartosc("IP Address");
                                String mask=devices.get(d).getWartosc("Mask");
                                String port_type=devices.get(d).getWartosc("Port Type");
                                String board_type=devices.get(d).getWartosc("Subboard Type");
                                String srn=devices.get(d).getWartosc("Subrack No.");
                                String sn=devices.get(d).getWartosc("Slot No.");
                                String cn=devices.get(d).getWartosc("Cabinet No.");
                                String pn=devices.get(d).getWartosc("Port No.");
                                String vrf=devices.get(d).getWartosc("VRF Index");
                                update_stat="'OK'";
                                String insert=null;
                                try{
                                insert="insert into raport_konfiguracja_aktualna.devip_per_ne (rowAuto_inc,ne_name,ip_adres,m2000_index,mask,port_type,board_type,srn,sn,cn,pn,vrf,create_date,last_update_date,Update_Status) values (null,'"+NeNames.get(z)+"','"+ip_adres+"','"+m2000_Index+"','"+mask+"','"+port_type+"','"+board_type+"',srn,sn,cn,pn,vrf,"+obecnyDzienCzas+","+obecnyDzienCzas+","+update_stat+")ON DUPLICATE KEY UPDATE last_update_date="+obecnyDzienCzas+",Update_Status="+update_stat+",mask='"+mask+"',port_type='"+port_type+"',board_type='"+board_type+"',srn="+srn+",sn="+sn+",cn="+cn+",pn="+pn+",vrf="+vrf ;
				//System.out.println(insert);
				testStatement.execute(insert);
                                }
                                catch(java.sql.SQLException ee)
                                {
                                    loger.throwing(this.getClass().toString(), "["+this.identyfikator+"] sqlExecError:"+insert, ee);
                                }
                            }
                        }
                    }
                    else
                    {
                        String update_stat=null;
                         String info=north.make2("REG NE:NAME=\""+NeNames.get(z)+"\"").replaceAll("'", "");
			    if(info.contains("RETCODE"))
			        update_stat=NewFile.getFirstLine(new String[]{"RETCODE"}, info);
			    if(update_stat.length()>40)
			        update_stat=update_stat.substring(0, 39);
			    if(info.contains("not Connection"))
			        update_stat="NOT_CONNECTED";
                            System.out.println(info);
                            String update=null;
                            try
                            {
                               update="update raport_konfiguracja_aktualna.devip_per_ne set Update_Status='"+update_stat+"', last_update_date="+obecnyDzienCzas+" where ne_name='"+NeNames.get(z)+"'";
                                // System.out.println(update);
                                testStatement.execute(update);
                            }
                            catch(java.sql.SQLException ee)
                            {
                                loger.throwing(this.getClass().toString(), "["+this.identyfikator+"] sqlExecError:"+update, ee);
                            }
                    }
                    }
                    catch(Exception ee)
                    {
                        loger.throwing(this.getClass().toString(), "["+this.identyfikator+"] while processing NE:\""+NE+"\"", ee);
                    }
                }
               	sukces=true;
	    }
	    else
	    {
		System.out.println("NIE ZNALEZIONO RNC o nazwie pasujacej do wzorca: "+this.kontrolerName);
		sukces=false;
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
		    String gr_index=komorki.getValue("rowAuto_inc", p);

		    niekomercyjneClean.add("delete from raport_konfiguracja_aktualna.devip_per_ne where rowAuto_inc='"+gr_index+"';");
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
	    String req="select u.* from raport_konfiguracja_aktualna.devip_per_ne u  where ( u.last_update_date<"+obecnyDzien+" and u.m2000_index like '"+m2000_Index+"');";
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