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

public class EthPort_perM2000_Updater extends Updater_parrent	//extends Thread{
{
   
    public EthPort_perM2000_Updater(String identyfikator,String M2000,int typOperacji,Logger loger,mysqlpackage.DataSource DOA,NewFile sprzF)
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
                    String inNodeb=north.make(NeNames.get(z), "DSP ETHPORT:");
                    if(inNodeb.contains("Invalid board"))
                    {
                        inNodeb=north.make(NeNames.get(z), "DSP ETHPORT: SRN=1");
                    }
                    if(inNodeb.contains("RETCODE = 0"))
                    {
                        String update_stat=null;
                        java.util.ArrayList<Paczka> devices=null;
                        if(inNodeb.contains("Number of results = 1)"))//pojedynczy devip,formatowanie pionowe
                        {
                            NPack npack = new NPack(inNodeb.split("\n"), new String[]{"DSP ETHPORT:"}, new String[]{"----------"}, new String[]{"---    END"});
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
                                String Ip_address_list=devices.get(d).getWartosc("IP Address List");
                                String local_duplex=devices.get(d).getWartosc("Local Duplex");
                                String local_speed=devices.get(d).getWartosc("Local Speed");
                                String max_trans_unit=devices.get(d).getWartosc("Maximum Transmission Unit(byte)");
                                String physical_layer=devices.get(d).getWartosc("Physical Layer Status");
                                
                                String port_status=devices.get(d).getWartosc("Port Status");
                                
                                String board_type=devices.get(d).getWartosc("Subboard Type");
                                String mac_addr=devices.get(d).getWartosc("MAC Address");
                                String srn=devices.get(d).getWartosc("Subrack No.");
                                String sn=devices.get(d).getWartosc("Slot No.");
                                String cn=devices.get(d).getWartosc("Cabinet No.");
                                String pn=devices.get(d).getWartosc("Port No.");
                              
                                update_stat="'OK'";
                                String insert=null;
                                
                                
                                String port_attribute=devices.get(d).getWartosc("Port Attribute");
                                String flow_control=devices.get(d).getWartosc("Flow Control");
                                String loopback_status=devices.get(d).getWartosc("Loopback Status");
                                String in_loopback_mode_or_not=devices.get(d).getWartosc("In Loopback Mode or Not");
                                String ethernet_flag=devices.get(d).getWartosc("Ethernet OAM 3AH Flag");
                                String number_of_rx_packets=devices.get(d).getWartosc("Number of RX Packets(packet)");
                                String number_of_error_rx_packets=devices.get(d).getWartosc("Number of RX CRC Error Packets(packet)");
                                String rx_traffic=devices.get(d).getWartosc("RX Traffic(byte/s)");
                                String number_of_tx_packets=devices.get(d).getWartosc("Number of TX Packets(packet)");
                                String tx_traffic=devices.get(d).getWartosc("TX Traffic(byte/s)");
                                String local_config_negotiation_mode=devices.get(d).getWartosc("Local Configuration Negotiation Mode");
                                String local_actual_negotiation_mode=devices.get(d).getWartosc("Local Actual Negotiation Mode");
                                
                                
                                String peer_actual_negotiation_mode=devices.get(d).getWartosc("Peer Actual Negotiation Mode");
                                String peer_speed=devices.get(d).getWartosc("Peer Speed");
                                String peer_duplex=devices.get(d).getWartosc("Peer Duplex");      

                                
                                
                                
                                try
                                {
                                    insert="insert into raport_konfiguracja_aktualna.ethport_per_ne (rowAuto_inc,ne_name,m2000_index,srn,sn,cn,pn,board_type, mac_addr,port_status,physical_layer,max_trans_unit,local_speed,local_duplex,Ip_address_list, port_attribute,flow_control,loopback_status,in_loopback_mode_or_not,OAM_3AH_ethernet_flag,number_of_rx_packets,number_of_CRC_error_rx_packets,rx_traffic,number_of_tx_packets,tx_traffic,local_config_negotiation_mode,local_actual_negotiation_mode,peer_actual_negotiation_mode,peer_speed,peer_duplex,     create_date,last_update_date,Update_Status) values (null,'"+NeNames.get(z)+"','"+m2000_Index+"','"+srn+"','"+sn+"','"+cn+"','"+pn+"','"+board_type+"', '"+mac_addr+"','"+port_status+"','"+physical_layer+"','"+max_trans_unit+"','"+local_speed+"','"+local_duplex+"','"+Ip_address_list+"', '"+port_attribute+"','"+flow_control+"','"+loopback_status+"','"+in_loopback_mode_or_not+"','"+ethernet_flag+"','"+number_of_rx_packets+"','"+number_of_error_rx_packets+"','"+rx_traffic+"','"+number_of_tx_packets+"','"+tx_traffic+"','"+local_config_negotiation_mode+"','"+local_actual_negotiation_mode+"','"+peer_actual_negotiation_mode+"','"+peer_speed+"','"+peer_duplex+"',"+obecnyDzienCzas+","+obecnyDzienCzas+","+update_stat+")ON DUPLICATE KEY UPDATE last_update_date="+obecnyDzienCzas+",Update_Status="+update_stat+",board_type='"+board_type+"', mac_addr='"+mac_addr+"',port_status='"+port_status+"',physical_layer='"+physical_layer+"',max_trans_unit='"+max_trans_unit+"',local_speed='"+local_speed+"',local_duplex='"+local_duplex+"',Ip_address_list='"+Ip_address_list+"',port_attribute='"+port_attribute+"',flow_control='"+flow_control+"',loopback_status='"+loopback_status+"',in_loopback_mode_or_not='"+in_loopback_mode_or_not+"',OAM_3AH_ethernet_flag='"+ethernet_flag+"',number_of_rx_packets='"+number_of_rx_packets+"',number_of_CRC_error_rx_packets='"+number_of_error_rx_packets+"',rx_traffic='"+rx_traffic+"',number_of_tx_packets='"+number_of_tx_packets+"',tx_traffic='"+tx_traffic+"',local_config_negotiation_mode='"+local_config_negotiation_mode+"',local_actual_negotiation_mode='"+local_actual_negotiation_mode+"',peer_actual_negotiation_mode='"+peer_actual_negotiation_mode+"',peer_speed='"+peer_speed+"',peer_duplex='"+peer_duplex+"'";
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
                               update="update raport_konfiguracja_aktualna.ethport_per_ne set Update_Status='"+update_stat+"', last_update_date="+obecnyDzienCzas+" where ne_name='"+NeNames.get(z)+"'";
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

		    niekomercyjneClean.add("delete from raport_konfiguracja_aktualna.ethport_per_ne where rowAuto_inc='"+gr_index+"';");
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
	    String req="select u.* from raport_konfiguracja_aktualna.ethport_per_ne u  where ( u.last_update_date<"+obecnyDzien+" and u.m2000_index like '"+m2000_Index+"');";
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