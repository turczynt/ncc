/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NORTHB_UPDATER;

import java.sql.ResultSet;
import java.sql.SQLException;
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
public class GEXT2GCELL_perBsc_updater extends Updater_parrent
{
     public GEXT2GCELL_perBsc_updater(String identyfikator,String bscName,int typOperacji,Logger loger,mysqlpackage.DataSource DOA,NewFile sprzF)
    {
	super(identyfikator,bscName,typOperacji,loger,DOA,sprzF);
    }

    String bsc_id;
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


	    this.errorInfo=this.errorInfo+"; get M2000 info for bsc="+this.kontrolerName;
	    if(rnc.rowCount()>0)
	    {

		String m2000_ip=rnc.getValue("M2000_Ip", 0);
		String bsc_name=rnc.getValue("Rnc_Bsc_Name", 0);
		 bsc_id=rnc.getValue("Rnc_Bsc_Id",0);
		String bsc_index=rnc.getValue("Rnc_Bsc_Index", 0);
		String m2000_Index=rnc.getValue("M2000_Index", 0);
		north=new nbipackage.NorthB(m2000_ip, "U-boot", "utranek098",null);
		this.errorInfo=this.errorInfo+"; connect with "+m2000_ip+" by NorthB";
		lstBts=north.make(this.kontrolerName, "LST BTSETHPORT:");

		if(lstBts!=null&&lstBts.contains("RETCODE = 0"))
		{
		    //sukces = true;
		    System.out.println(this.identyfikator+" LST BTSETHPORT:: wykonane");
		    this.errorInfo=this.errorInfo+"; POBRANE LST BTSETHPORT::";
		}
		else
		{
		    System.out.println(this.identyfikator+" LST BTSETHPORT: ERROR");

		    this.errorInfo=this.errorInfo+"; BLAD W POBIERANIU POBRANE LST BTSETHPORT:";
		    sukces=false;

		}
		
		this.errorInfo=this.errorInfo+"; ZAMKNIECIE NORTHB";
		this.errorInfo=this.errorInfo+"; POBRANIE KOMOREK Z DB";

		if(lstBts.contains("RETCODE = 0"))
		{
		    java.util.ArrayList<Paczka> ethPort=new java.util.ArrayList<Paczka>();
		    try
		    {
			NPack nn=new NPack(lstBts);
			ethPort=nn.getAllPacks();

		    }
		    catch(Exception ewqr)
		    {
			System.err.println("WYJEBALO przy NPack LST PTPBVC:;");
			loger.log(Level.FINEST, "["+this.identyfikator+"]ERROR ", ewqr);
		    }
		    if(ethPort!=null)
		    {


			for(int u=0;u<ethPort.size();u++)
			{
			    Paczka komorka=ethPort.get(u);
			    if(komorka!=null)
			    {
				//
                                
                                
                                String btsIndex=komorka.getWartosc("BTS Index");
                                String sn=komorka.getWartosc("Port Slot No.");
                                String srn=komorka.getWartosc("Port Subrack No.");
                                String cn=komorka.getWartosc("Port Cabinet No.");
                                String odpDet=north.make(this.kontrolerName, "DSP BTSETHPORT: IDTYPE=BYID,BTSID="+btsIndex+",SN="+sn+",SRN="+srn+",CN="+cn+",LSTFORMAT=VERTICAL;");
                                NPack npackDet=new NPack(odpDet,NPack.FORMAT_PIONOWY);
                                java.util.ArrayList<Paczka> pp=npackDet.getAllPacks();
                               
                               
                                if(pp!=null&&pp.size()>0)
                                    for(Paczka ethDet:pp)
                                {
                                    //Paczka ethDet=pp.get(0);
                                     String pn=ethDet.getWartosc("Port No.");
                                    String PortAttribute=ethDet.getWartosc("Port Attribute");
                                    String MaxTransmitUnit=ethDet.getWartosc("Max Transmit Unit(byte)");
                                    String MACAddress=ethDet.getWartosc("MAC Address");
                                    String WorkMode=ethDet.getWartosc("Work Mode");
                                    String OAM3AHFlag=ethDet.getWartosc("Ethernet OAM 3AH Flag");
                                    String ReceivePackages=ethDet.getWartosc("Receive Packages");
                                    String NumberBytesReceived=ethDet.getWartosc("Number of Bytes Received");
                                    String ReceiveCRCErrorPackages=ethDet.getWartosc("Receive CRC Error Packages");
                                    String TransmitPackages=ethDet.getWartosc("Transmit Packages");
                                    String NumberBytesSent=ethDet.getWartosc("Number of Bytes Sent");
                                    
                                    String Speed=ethDet.getWartosc("Speed");
                                    String ArpAgent=ethDet.getWartosc("ARP Agent");
                                    String FlowControl=ethDet.getWartosc("Flow Control");
                                    String PortState=ethDet.getWartosc("Port State");
                                    String LoopbackStatus=ethDet.getWartosc("Loopback Status");
                                    String RXTraffic=ethDet.getWartosc("RX Traffic");
                                    String TXTraffic=ethDet.getWartosc("TX Traffic");
                                    String InLoopbackModeOrNot=ethDet.getWartosc("In Loopback Mode or Not");
                                    String PhysicalLayerStatus=ethDet.getWartosc("Physical Layer Status");
                                    String LocalConfigurationNegotiationMode=ethDet.getWartosc("Local Configuration Negotiation Mode");
                                    String LocalActualNegotiationMode=ethDet.getWartosc("Local Actual Negotiation Mode");
                                    String PeerActualNegotiationMode=ethDet.getWartosc("Peer Actual Negotiation Mode");
                                    String PeerSpeed=ethDet.getWartosc("Peer Speed");
                                    String PeerDuplex=ethDet.getWartosc("Peer Duplex");
                                    String NumberOfIPs=ethDet.getWartosc("Number of IPs");
                                    String IPAddressList=ethDet.getWartosc("IP Address List");
                           

                                      String insert="INSERT INTO `raport_konfiguracja_aktualna`.`dspbtseth_per_bts` (bsc_id,bts_id,sn,cn,srn,pn,PortAttribute,MaxTransmitUnit,MACAddress,WorkMode,OAM3AHFlag,ReceivePackages,NumberBytesReceived,ReceiveCRCErrorPackages,TransmitPackages,NumberBytesSent,Speed,ArpAgent,FlowControl,PortState,LoopbackStatus,RXTraffic,TXTraffic,InLoopbackModeOrNot,PhysicalLayerStatus,LocalConfigurationNegotiationMode,LocalActualNegotiationMode,PeerActualNegotiationMode,PeerSpeed,PeerDuplex,NumberOfIPs,IPAddressList ,create_date,last_update_date,update_state) VALUES"
                                             + " ('"+bsc_id+"','"+btsIndex+"','"+sn+"','"+cn+"','"+srn+"','"+pn+"','"+PortAttribute+"','"+MaxTransmitUnit+"','"+MACAddress+"','"+WorkMode+"','"+OAM3AHFlag+"','"+ReceivePackages+"','"+NumberBytesReceived+"','"+ReceiveCRCErrorPackages+"','"+TransmitPackages+"','"+NumberBytesSent+"','"+Speed+"','"+ArpAgent+"','"+FlowControl+"','"+PortState+"','"+LoopbackStatus+"','"+RXTraffic+"','"+TXTraffic+"','"+InLoopbackModeOrNot+"','"+PhysicalLayerStatus+"','"+LocalConfigurationNegotiationMode+"','"+LocalActualNegotiationMode+"','"+PeerActualNegotiationMode+"','"+PeerSpeed+"','"+PeerDuplex+"','"+NumberOfIPs+"','"+IPAddressList+"',(now()),"+obecnyDzienCzas+",'NEW')  ON DUPLICATE KEY UPDATE PortAttribute='"+PortAttribute+"',MaxTransmitUnit='"+MaxTransmitUnit+"',MACAddress='"+MACAddress+"',WorkMode='"+WorkMode+"',OAM3AHFlag='"+OAM3AHFlag+"',ReceivePackages='"+ReceivePackages+"',NumberBytesReceived='"+NumberBytesReceived+"',ReceiveCRCErrorPackages='"+ReceiveCRCErrorPackages+"',TransmitPackages='"+TransmitPackages+"',NumberBytesSent='"+NumberBytesSent+"',Speed='"+Speed+"',ArpAgent='"+ArpAgent+"',FlowControl='"+FlowControl+"',PortState='"+PortState+"',LoopbackStatus='"+LoopbackStatus+"',RXTraffic='"+RXTraffic+"',TXTraffic='"+TXTraffic+"',InLoopbackModeOrNot='"+InLoopbackModeOrNot+"',PhysicalLayerStatus='"+PhysicalLayerStatus+"',LocalConfigurationNegotiationMode='"+LocalConfigurationNegotiationMode+"',LocalActualNegotiationMode='"+LocalActualNegotiationMode+"',PeerActualNegotiationMode='"+PeerActualNegotiationMode+"',PeerSpeed='"+PeerSpeed+"',PeerDuplex='"+PeerDuplex+"',NumberOfIPs='"+NumberOfIPs+"',IPAddressList='"+IPAddressList+"' ,last_update_date="+obecnyDzienCzas+",update_state='UPDATE' ";
                                   //System.out.println(insert);
                                     testStatement.execute(insert);
                                    
                                }
//				 String insert="insert into oncall.konfiguracja_aktualna_bts (Bts_Index, Rnc_Bsc_Index, Site_Index,Bts_Id,Bts_Name,gsm,dcs,System,Last_Update,Update_Status,Region) values('"+Bts_Index+"', '"+bsc_index+"', (select Site_Index from oncall.konfiguracja_aktualna_site where Site_Name like '%"+siteName+"%' LIMIT 1),"+Bts_Id+",'"+Bts_name+"', (select count((select gc.cell_id from oncall.konfiguracja_aktualna_gcell gc where(gc.Bts_Index='"+Bts_Index+"' and gc.System like '%GSM%') LIMIT 1))),(select count((select gc.cell_id from oncall.konfiguracja_aktualna_gcell gc where(gc.Bts_Index='"+Bts_Index+"' and gc.System like '%DCS%') LIMIT 1))),'System',"+obecnyDzienCzas+",'NEW',"+region+") ON DUPLICATE KEY UPDATE Last_Update="+obecnyDzienCzas+",Bts_Name='"+Bts_name+"',Update_Status='UPDATE',gsm=(select count((select gc.cell_id from oncall.konfiguracja_aktualna_gcell gc where(gc.Bts_Index='"+Bts_Index+"' and gc.System like '%GSM%') LIMIT 1))),dcs=(select count((select gc.cell_id from oncall.konfiguracja_aktualna_gcell gc where(gc.Bts_Index='"+Bts_Index+"' and gc.System like '%DCS%') LIMIT 1)))";

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
     
     
     
    /*public boolean add() throws java.sql.SQLException
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


	    this.errorInfo=this.errorInfo+"; get M2000 info for bsc="+this.kontrolerName;
	    if(rnc.rowCount()>0)
	    {

		String m2000_ip=rnc.getValue("M2000_Ip", 0);
		String bsc_name=rnc.getValue("Rnc_Bsc_Name", 0);
		 bsc_id=rnc.getValue("Rnc_Bsc_Id",0);
		String bsc_index=rnc.getValue("Rnc_Bsc_Index", 0);
		String m2000_Index=rnc.getValue("M2000_Index", 0);
		north=new nbipackage.NorthB(m2000_ip, "U-boot", "utranek098",null);
		this.errorInfo=this.errorInfo+"; connect with "+m2000_ip+" by NorthB";
		lstBts=north.make(this.kontrolerName, "LST BTSETHPORT:");

		if(lstBts!=null&&lstBts.contains("RETCODE = 0"))
		{
		    //sukces = true;
		    System.out.println(this.identyfikator+" LST BTSETHPORT:: wykonane");
		    this.errorInfo=this.errorInfo+"; POBRANE LST BTSETHPORT::";
		}
		else
		{
		    System.out.println(this.identyfikator+" LST BTSETHPORT: ERROR");

		    this.errorInfo=this.errorInfo+"; BLAD W POBIERANIU POBRANE LST BTSETHPORT:";
		    sukces=false;

		}
		
		this.errorInfo=this.errorInfo+"; ZAMKNIECIE NORTHB";
		this.errorInfo=this.errorInfo+"; POBRANIE KOMOREK Z DB";

		if(lstBts.contains("RETCODE = 0"))
		{
		    java.util.ArrayList<Paczka> ethPort=new java.util.ArrayList<Paczka>();
		    try
		    {
			NPack nn=new NPack(lstBts);
			ethPort=nn.getAllPacks();

		    }
		    catch(Exception ewqr)
		    {
			System.err.println("WYJEBALO przy NPack LST PTPBVC:;");
			loger.log(Level.FINEST, "["+this.identyfikator+"]ERROR ", ewqr);
		    }
		    if(ethPort!=null)
		    {


			for(int u=0;u<ethPort.size();u++)
			{
			    Paczka komorka=ethPort.get(u);
			    if(komorka!=null)
			    {
				//
                                
                                
                                String btsIndex=komorka.getWartosc("BTS Index");
                                String sn=komorka.getWartosc("Port Slot No.");
                                String srn=komorka.getWartosc("Port Subrack No.");
                                String cn=komorka.getWartosc("Port Cabinet No.");
                                String odpDet=north.make(this.kontrolerName, "DSP BTSETHPORT: IDTYPE=BYID,BTSID="+btsIndex+",SN="+sn+",SRN="+srn+",CN="+cn+",LSTFORMAT=VERTICAL;");
                                NPack npackDet=new NPack(odpDet,NPack.FORMAT_PIONOWY);
                                java.util.ArrayList<Paczka> pp=npackDet.getAllPacks();
                               
                               
                                if(pp!=null&&pp.size()>0)
                                    for(Paczka ethDet:pp)
                                {
                                    //Paczka ethDet=pp.get(0);
                                     String pn=ethDet.getWartosc("Port No.");
                                    String PortAttribute=ethDet.getWartosc("Port Attribute");
                                    String MaxTransmitUnit=ethDet.getWartosc("Max Transmit Unit(byte)");
                                    String MACAddress=ethDet.getWartosc("MAC Address");
                                    String WorkMode=ethDet.getWartosc("Work Mode");
                                    String OAM3AHFlag=ethDet.getWartosc("Ethernet OAM 3AH Flag");
                                    String ReceivePackages=ethDet.getWartosc("Receive Packages");
                                    String NumberBytesReceived=ethDet.getWartosc("Number of Bytes Received");
                                    String ReceiveCRCErrorPackages=ethDet.getWartosc("Receive CRC Error Packages");
                                    String TransmitPackages=ethDet.getWartosc("Transmit Packages");
                                    String NumberBytesSent=ethDet.getWartosc("Number of Bytes Sent");
                                    
                                    String Speed=ethDet.getWartosc("Speed");
                                    String ArpAgent=ethDet.getWartosc("ARP Agent");
                                    String FlowControl=ethDet.getWartosc("Flow Control");
                                    String PortState=ethDet.getWartosc("Port State");
                                    String LoopbackStatus=ethDet.getWartosc("Loopback Status");
                                    String RXTraffic=ethDet.getWartosc("RX Traffic");
                                    String TXTraffic=ethDet.getWartosc("TX Traffic");
                                    String InLoopbackModeOrNot=ethDet.getWartosc("In Loopback Mode or Not");
                                    String PhysicalLayerStatus=ethDet.getWartosc("Physical Layer Status");
                                    String LocalConfigurationNegotiationMode=ethDet.getWartosc("Local Configuration Negotiation Mode");
                                    String LocalActualNegotiationMode=ethDet.getWartosc("Local Actual Negotiation Mode");
                                    String PeerActualNegotiationMode=ethDet.getWartosc("Peer Actual Negotiation Mode");
                                    String PeerSpeed=ethDet.getWartosc("Peer Speed");
                                    String PeerDuplex=ethDet.getWartosc("Peer Duplex");
                                    String NumberOfIPs=ethDet.getWartosc("Number of IPs");
                                    String IPAddressList=ethDet.getWartosc("IP Address List");
                           

                                      String insert="INSERT INTO `raport_konfiguracja_aktualna`.`dspbtseth_per_bts` (bsc_id,bts_id,sn,cn,srn,pn,PortAttribute,MaxTransmitUnit,MACAddress,WorkMode,OAM3AHFlag,ReceivePackages,NumberBytesReceived,ReceiveCRCErrorPackages,TransmitPackages,NumberBytesSent,Speed,ArpAgent,FlowControl,PortState,LoopbackStatus,RXTraffic,TXTraffic,InLoopbackModeOrNot,PhysicalLayerStatus,LocalConfigurationNegotiationMode,LocalActualNegotiationMode,PeerActualNegotiationMode,PeerSpeed,PeerDuplex,NumberOfIPs,IPAddressList ,create_date,last_update_date,update_state) VALUES"
                                             + " ('"+bsc_id+"','"+btsIndex+"','"+sn+"','"+cn+"','"+srn+"','"+pn+"','"+PortAttribute+"','"+MaxTransmitUnit+"','"+MACAddress+"','"+WorkMode+"','"+OAM3AHFlag+"','"+ReceivePackages+"','"+NumberBytesReceived+"','"+ReceiveCRCErrorPackages+"','"+TransmitPackages+"','"+NumberBytesSent+"','"+Speed+"','"+ArpAgent+"','"+FlowControl+"','"+PortState+"','"+LoopbackStatus+"','"+RXTraffic+"','"+TXTraffic+"','"+InLoopbackModeOrNot+"','"+PhysicalLayerStatus+"','"+LocalConfigurationNegotiationMode+"','"+LocalActualNegotiationMode+"','"+PeerActualNegotiationMode+"','"+PeerSpeed+"','"+PeerDuplex+"','"+NumberOfIPs+"','"+IPAddressList+"',(now()),"+obecnyDzienCzas+",'NEW')  ON DUPLICATE KEY UPDATE PortAttribute='"+PortAttribute+"',MaxTransmitUnit='"+MaxTransmitUnit+"',MACAddress='"+MACAddress+"',WorkMode='"+WorkMode+"',OAM3AHFlag='"+OAM3AHFlag+"',ReceivePackages='"+ReceivePackages+"',NumberBytesReceived='"+NumberBytesReceived+"',ReceiveCRCErrorPackages='"+ReceiveCRCErrorPackages+"',TransmitPackages='"+TransmitPackages+"',NumberBytesSent='"+NumberBytesSent+"',Speed='"+Speed+"',ArpAgent='"+ArpAgent+"',FlowControl='"+FlowControl+"',PortState='"+PortState+"',LoopbackStatus='"+LoopbackStatus+"',RXTraffic='"+RXTraffic+"',TXTraffic='"+TXTraffic+"',InLoopbackModeOrNot='"+InLoopbackModeOrNot+"',PhysicalLayerStatus='"+PhysicalLayerStatus+"',LocalConfigurationNegotiationMode='"+LocalConfigurationNegotiationMode+"',LocalActualNegotiationMode='"+LocalActualNegotiationMode+"',PeerActualNegotiationMode='"+PeerActualNegotiationMode+"',PeerSpeed='"+PeerSpeed+"',PeerDuplex='"+PeerDuplex+"',NumberOfIPs='"+NumberOfIPs+"',IPAddressList='"+IPAddressList+"' ,last_update_date="+obecnyDzienCzas+",update_state='UPDATE' ";
                                   //System.out.println(insert);
                                     testStatement.execute(insert);
                                    
                                }
//				 String insert="insert into oncall.konfiguracja_aktualna_bts (Bts_Index, Rnc_Bsc_Index, Site_Index,Bts_Id,Bts_Name,gsm,dcs,System,Last_Update,Update_Status,Region) values('"+Bts_Index+"', '"+bsc_index+"', (select Site_Index from oncall.konfiguracja_aktualna_site where Site_Name like '%"+siteName+"%' LIMIT 1),"+Bts_Id+",'"+Bts_name+"', (select count((select gc.cell_id from oncall.konfiguracja_aktualna_gcell gc where(gc.Bts_Index='"+Bts_Index+"' and gc.System like '%GSM%') LIMIT 1))),(select count((select gc.cell_id from oncall.konfiguracja_aktualna_gcell gc where(gc.Bts_Index='"+Bts_Index+"' and gc.System like '%DCS%') LIMIT 1))),'System',"+obecnyDzienCzas+",'NEW',"+region+") ON DUPLICATE KEY UPDATE Last_Update="+obecnyDzienCzas+",Bts_Name='"+Bts_name+"',Update_Status='UPDATE',gsm=(select count((select gc.cell_id from oncall.konfiguracja_aktualna_gcell gc where(gc.Bts_Index='"+Bts_Index+"' and gc.System like '%GSM%') LIMIT 1))),dcs=(select count((select gc.cell_id from oncall.konfiguracja_aktualna_gcell gc where(gc.Bts_Index='"+Bts_Index+"' and gc.System like '%DCS%') LIMIT 1)))";

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
    }*/

    @Override
     public boolean clearNotUpdatet()
    {
        return true;
	/* try
	 {
	     java.util.ArrayList<String> poleceniaK=new java.util.ArrayList<String>();
	     java.util.ArrayList<String> poleceniaNK=new java.util.ArrayList<String>();
	     String obecnyDzien="'"+sdf.format(DataDzisiaj)+"'";
             String query="delete from raport_konfiguracja_aktualna.dspbtseth_per_bts where last_update_date<curdate() and bsc_id='"+bsc_id+"';";
             
            testStatement.execute(query);
	
	     return true;
	}
	catch(Exception ee)
	{
            loger.log(Level.FINEST,"["+this.identyfikator+" CLEANER] ERROR:" , ee);
            return false;
	}*/
    }


    @Override
    public OdpowiedzSQL getNotUpdateCandidates(java.util.Date dateBefore, String kontroler)
    {
        return null;
	/*String obecnyDzien="'"+sdf.format(dateBefore)+"'";
	try
	{
	    String req="select bt.* from raport_konfiguracja_aktualna.dspbtseth_per_bts bt where(bt.last_update_date<(curdate()) and bt.bsc_id='"+bsc_id+"');";
	    ResultSet res=testStatement.executeQuery(req);
	    OdpowiedzSQL rnc=Baza.createAnswer(res);
	    return rnc;
	}
	catch(Exception ee)
	{
	    loger.log(Level.FINEST,"["+this.identyfikator+"] ERROR:" , ee);
	    return null;
	}*/
    }


}
