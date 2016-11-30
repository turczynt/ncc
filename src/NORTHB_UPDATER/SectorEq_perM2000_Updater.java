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

public class SectorEq_perM2000_Updater extends Updater_parrent	//extends Thread{
{
   
    public SectorEq_perM2000_Updater(String identyfikator,String M2000,int typOperacji,Logger loger,mysqlpackage.DataSource DOA,NewFile sprzF)
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
                        
                        
                        
                        if((z % 100)==0)
                        {
                            loger.log(Level.FINEST,"["+this.identyfikator+"] EXECUTED:"+z+"/"+ NeNames.size());
                        }
                        /*
LST SECTOR:;%%
RETCODE = 0  Operation succeeded.

List Sector Configuration
-------------------------
Sector ID  Sector Name       Location Name  User Label  Antenna Azimuth(0.1degree)

0          RRU80_U2100       NULL           NULL        750                       
1          RRU82_U2100       NULL           NULL        2200                      
2          RRU84_U2100       NULL           NULL        3300                      
3          RRU70_LTE         NULL           NULL        750                       
4          RRU72_LTE         NULL           NULL        2200                      
5          RRU74_LTE         NULL           NULL        3300                      
11         RRU60_U900        NULL           NULL        750                       
13         RRU62_U900        NULL           NULL        2200                      
15         RRU64_U900        NULL           NULL        3300                      
90         L800_90_Sektor_1  NULL           NULL        750                       
92         L800_92_Sektor_2  NULL           NULL        2200                      
94         L800_94_Sektor_3  NULL           NULL        3300                      
(Number of results = 12)


---    END
* 
* NODEB: ----->
* LST SECTOREQM:;%%
RETCODE = 0  Operation succeeded.

List Sector Equipment Configuration
-----------------------------------
Sector Equipment ID  Sector ID

0                    0        
1                    0        
2                    1        
3                    1        
4                    2        
5                    2        
6                    3        
7                    4        
8                    5        
11                   11       
13                   13       
15                   15       
20                   0        
21                   1        
22                   2        
900                  90       
920                  92       
940                  94       
(Number of results = 18)


---    END
* 
* 
* ENODEB:---->
* LST SECTOREQM:;%%
RETCODE = 0  Operation succeeded.

List Sector Equipment Configuration
-----------------------------------
Sector Equipment ID  Sector ID  Antenna Config Mode  RRU Cabinet No.  RRU Subrack No.  RRU Slot No.  Beam Shape  Beam Layer Spilt  Beam Azimuth Offset

0                    0          ANTENNAPORT          NULL             NULL             NULL          NULL        NULL              NULL
1                    1          ANTENNAPORT          NULL             NULL             NULL          NULL        NULL              NULL
2                    2          ANTENNAPORT          NULL             NULL             NULL          NULL        NULL              NULL
3                    3          ANTENNAPORT          NULL             NULL             NULL          NULL        NULL              NULL
8                    8          ANTENNAPORT          NULL             NULL             NULL          NULL        NULL              NULL
(Number of results = 5)


---    END


LST ENODEBFUNCTION:;%%
RETCODE = 0  Operation succeeded.

List eNodeB Function Configuration
----------------------------------
 eNodeB Function Name  =  14_WAR8020A_1_STAD_NARODOWY
Application Reference  =  1
            eNodeB ID  =  5264
           User Label  =  NULL
         NeRM Version  =  LTEDATAV100R011C10SPC100
      Product Version  =  BTS3900 V100R011C10SPC100
(Number of results = 1)


---    END

* 
* %LST ENODEBFUNCTION:;%%
RETCODE = 1  Invalid command,it is inexecutable.

Invalid command,it is inexecutable.

---    END

* 
* 
* LST NODEBFUNCTION:;%%
RETCODE = 1  Invalid command,it is inexecutable.

Invalid command,it is inexecutable.

---    END
* 
* 
* LST NODEBFUNCTION:;%%
RETCODE = 0  Operation succeeded.

List NodeB
----------
  NodeB Function Name  =  14_WAR8021A_50250U_WOLOSKA_12
Application Reference  =  2
         NeRM Version  =  UMTSDATAV200R018C10SPC100
           User Label  =  NULL
     NodeB Identifier  =  0
      Product Version  =  BTS3900 V100R011C10SPC100
(Number of results = 1)


---    END


                         */
                        
                        
                        
                        
                    String lstSector=north.make(NeNames.get(z), "LST SECTOR:");
                    String lstSectorEq=north.make(NeNames.get(z), "LST SECTOREQM:");
                    
                    
                 

		
                    
                    if(lstSector.contains("RETCODE = 0")&&lstSectorEq.contains("RETCODE = 0"))
                    {
                        java.util.ArrayList<Paczka> Sectors=(new NPack(lstSector,NPack.FORMAT_POZIOMY)).getAllPacks();
                        if(lstSector.contains("Number of results = 1)"))
                            Sectors=(new NPack(lstSector,NPack.FORMAT_PIONOWY)).getAllPacks();
                        java.util.ArrayList<Paczka> SectorsEq=(new NPack(lstSectorEq,NPack.FORMAT_POZIOMY)).getAllPacks();
                        
                        String nodebId="NULL";
                        String nodebName="NULL";
                        String enodebId="NULL";
                        String enodebName="NULL";
                        
                        
                        String lstEnodebFunction=north.make(NeNames.get(z), "LST ENODEBFUNCTION:");
                        if(lstEnodebFunction.contains("RETCODE = 0"))
                        {
                            try{
                            Paczka enodebPack=(new NPack(lstEnodebFunction,NPack.FORMAT_PIONOWY)).getAllPacks().get(0);
                            enodebName="'"+enodebPack.getWartosc("eNodeB Function Name")+"'";
                            enodebId="'"+enodebPack.getWartosc("eNodeB ID")+"'";
                            }
                            catch(Exception ee)
                            {
                                ;
                            }
                        }
                        
                        String lstNodebFunction=north.make(NeNames.get(z), "LST NODEBFUNCTION:");
                        if(lstNodebFunction.contains("RETCODE = 0"))
                        {
                            try{
                            Paczka nodebPack=(new NPack(lstNodebFunction,NPack.FORMAT_PIONOWY)).getAllPacks().get(0);
                            nodebName="'"+nodebPack.getWartosc("NodeB Function Name")+"'";
                            nodebId="(select Nodeb_Id from oncall.konfiguracja_aktualna_nodeb n where n.Nodeb_Name="+nodebName+" limit 1)";
                            }
                            catch(Exception ee)
                            {
                                ;
                            }
                            
                        }
                        
                        for(int e=0;e<SectorsEq.size();e++)
                        {
                            String secId=SectorsEq.get(e).getWartosc("Sector ID");
                            String eqId="'"+SectorsEq.get(e).getWartosc("Sector Equipment ID")+"'";
                            String azymut="null";
                            String sectorName="null";
                            for(int s=0;s<Sectors.size();s++)
                            {
                                if(Sectors.get(s).getWartosc("Sector ID").equals(secId))
                                {
                                    azymut="'"+Sectors.get(s).getWartosc("Antenna Azimuth(0.1degree)")+"'";
                                                                          
                                    sectorName="'"+Sectors.get(s).getWartosc("Sector Name")+"'";
                                }
                            }
                            
                            secId="'"+secId+"'";
                            if(azymut.equals("null"))
                                loger.log(Level.FINEST,"["+this.identyfikator+"]"+NeNames.get(z)+" secSize="+Sectors.size()+":"+lstSector+"\r\n"+lstSectorEq);
                            String insert="INSERT INTO `raport_konfiguracja_aktualna`.`sector_eq` ( `NeName`, `NodebId`, `M2000Index`, `EnodebId`, `sectorId`, `sectorEq`, `azymut`, `sector_name`, `create_date`, `last_update`, `NodebName`, `EnodebName`, `update_status`) VALUES ( '"+NeNames.get(z)+"', "+nodebId+",'"+m2000_Index+"', "+enodebId+","+secId+","+eqId+","+azymut+", "+sectorName+", "+obecnyDzienCzas+", "+obecnyDzienCzas+","+nodebName+","+enodebName+", 'CREATE')  ON DUPLICATE KEY UPDATE `NodebId`="+nodebId+", `M2000Index`='"+m2000_Index+"', `EnodebId`="+enodebId+", `azymut`="+azymut+",  `sector_name`="+sectorName+", `last_update`="+obecnyDzienCzas+", `NodebName`="+nodebName+", `EnodebName`="+enodebName+", `update_status`='Update'";
                           // System.out.println(insert);
                            
                          
                            
                           
                            Statement testStatementIns = connection.prepareStatement(insert,  Statement.RETURN_GENERATED_KEYS);
                          testStatementIns.executeUpdate(insert,  Statement.RETURN_GENERATED_KEYS);
                           int sectorEq_rowInd=-1;
                           
                           try (ResultSet generatedKeys = testStatementIns.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                sectorEq_rowInd=generatedKeys.getInt(1);
                            }
            
                             }
                           
                           
                           String eqDet=north.make(NeNames.get(z), "LST SECTOREQM:SECTOREQMID="+SectorsEq.get(e).getWartosc("Sector Equipment ID"));
                           if(eqDet.contains("RETCODE = 0"))
                           {
                               
                              
                                java.util.ArrayList<Paczka> antenap=(new NPack(eqDet,NPack.FORMAT_POZIOMY)).getAllPacks();
                                if(eqDet.substring(eqDet.indexOf("List Sector Equipment Antenna Configuration")).contains("(Number of results = 1)"))
                                    antenap=(new NPack(eqDet,NPack.FORMAT_PIONOWY)).getAllPacks();
                                for(int a=0;a<antenap.size();a++)
                                {
                                    if(antenap.get(a).getWartosc("SUBRACK NO.").length()>0&&antenap.get(a).getWartosc("ANTENNA CHANNEL NO.").length()>0)
                                    {
                                    String cn="'"+antenap.get(a).getWartosc("Cabinet No.")+"'";
                                    String srn="'"+antenap.get(a).getWartosc("SUBRACK NO.")+"'";
                                    String sn="'"+antenap.get(a).getWartosc("SLOT NO.")+"'";
                                    String antNo="'"+antenap.get(a).getWartosc("ANTENNA CHANNEL NO.")+"'";
                                    String TxRx_mode="'"+antenap.get(a).getWartosc("ANTENNA RX/TX MODE")+"'";
                                    String MasterSlave_mode="'"+antenap.get(a).getWartosc("TX ANTENNA MASTER/SLAVE MODE")+"'";
                                    String insertAnt="INSERT INTO `raport_konfiguracja_aktualna`.`antenaPortInfo`(sectorEq_rowInd,cn,srn,sn,antNo,TxRx_mode,MasterSlave_mode, M2000Index,`create_date`, `last_update`,`update_status`) VALUES ("+sectorEq_rowInd+","+cn+","+srn+","+sn+","+antNo+","+TxRx_mode+","+MasterSlave_mode+",'"+m2000_Index+"', "+obecnyDzienCzas+", "+obecnyDzienCzas+",'CREATE')   ON DUPLICATE KEY UPDATE cn="+cn+",TxRx_mode="+TxRx_mode+",MasterSlave_mode="+MasterSlave_mode+", `last_update`= "+obecnyDzienCzas+",`update_status`='UPDATE',M2000Index='"+m2000_Index+"'";
                                 //  System.out.println(NeNames.get(z)+"|"+sectorEq_rowInd+"|"+insertAnt);
                                    if(antenap.get(a).getWartosc("SUBRACK NO.")!=null&&!antenap.get(a).getWartosc("SUBRACK NO.").equals(""))
                                        testStatement.executeUpdate(insertAnt);
                                    }
                                    
                                }
                                
                                try{
                                    String ecellPerEq=north.make(NeNames.get(z), "LST EUCELLSECTOREQM:SECTOREQMID="+SectorsEq.get(e).getWartosc("Sector Equipment ID"));
                                    String insetrt;
                                    String lte="0";
                                    String umts="0";
                                    java.util.ArrayList<Paczka> komorki=null;
                                    if(ecellPerEq.contains("Number of results ="))
                                    {
                                        if(ecellPerEq.contains("Number of results = 1"))
                                        {
                                            komorki=(new NPack(ecellPerEq,NPack.FORMAT_PIONOWY)).getAllPacks();
                                        }
                                        else
                                        {
                                            komorki=(new NPack(ecellPerEq,NPack.FORMAT_POZIOMY)).getAllPacks();
                                        }
                                        lte="1";
                                    }
                                    else
                                    {
                                        String ucellPerEq=north.make(NeNames.get(z), "LST ULOCELL:MODE=SECTOREQM,SECTOREQMID="+SectorsEq.get(e).getWartosc("Sector Equipment ID"));
                                        if(ucellPerEq.contains("Number of results ="))
                                        {
                                            if(ucellPerEq.contains("Number of results = 1"))
                                        {
                                            komorki=(new NPack(ucellPerEq,NPack.FORMAT_PIONOWY)).getAllPacks();
                                        }
                                        else
                                        {
                                            komorki=(new NPack(ucellPerEq,NPack.FORMAT_POZIOMY)).getAllPacks();
                                        }
                                        umts="1";
                                        }
                                    }
                                    
                                    if(umts.equals("1")||lte.equals("1"))
                                    {
                                         for(int a=0;a<komorki.size();a++)
                                        {
                                            if(komorki.get(a).getWartosc("Local Cell ID").length()>0)
                                            {
                                                String locellId=komorki.get(a).getWartosc("Local Cell ID");
                                                String insertAnt="INSERT INTO `raport_konfiguracja_aktualna`.`cellsPerEqupment`(sectorEq_rowInd,locellId,lte,umts,M2000Index, `create_date`, `last_update`,`update_status`) VALUES ("+sectorEq_rowInd+","+locellId+","+lte+","+umts+",'"+m2000_Index+"', "+obecnyDzienCzas+", "+obecnyDzienCzas+",'CREATE')   ON DUPLICATE KEY UPDATE lte="+lte+",umts="+umts+", `last_update`= "+obecnyDzienCzas+",`update_status`='UPDATE',M2000Index='"+m2000_Index+"'";
                                             //  System.out.println(NeNames.get(z)+"|"+sectorEq_rowInd+"|"+insertAnt);
                                                testStatement.executeUpdate(insertAnt);
                                            }
                                    
                                        }
                                    }
                                }
                                catch(Exception ee)
                                {
                                    ;
                                }
                                
                                
                                /*
                                 * 
                                 * +++    14_BIA1005A_1_CIESZYNSKA        2016-04-29 07:48:30 DST
O&M    #808454723
%% LST EUCELLSECTOREQM:SECTOREQMID=10;%%
RETCODE = 0  Operation succeeded.

List Cell Sector Equipment
--------------------------
                       Local cell ID  =  0
                 Sector equipment ID  =  10
      Reference signal power(0.1dBm)  =  32767
               Baseband equipment ID  =  255
Reference Signal Power Margin(0.1dB)  =  0
             Sector CPRI Compression  =  Invalid
(Number of results = 1)


---    END

LST ULOCELL:MODE=SECTOREQM,SECTOREQMID=0;
+++    14_BIA1005A_1_CIESZYNSKA        2016-04-29 07:50:10 DST
O&M    #538969812
%% LST ULOCELL:MODE=SECTOREQM,SECTOREQMID=0;%%
RETCODE = 0  Operation succeeded.

List Local Cell Configuration
-----------------------------
                      Local Cell ID  =  15071111
                    Local Cell Type  =  NORMAL_CELL
              Cell Scale Indication  =  Macro Cell
                            Cell ID  =  14041
           UL Baseband Equipment ID  =  0
           DL Baseband Equipment ID  =  0
               Local Cell Radius(m)  =  29000
Local Cell Inner Handover Radius(m)  =  0
                         Two Tx Way  =  FALSE
                      Reserved Cell  =  FALSE
(Number of results = 1)


---    END

                                 */
                           }
                           
                        }
                    }
                    else
                    {
                        String info=north.make2("REG NE:NAME=\""+NeNames.get(z)+"\"").replaceAll("'", "");
                        String update_stat="";
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
					
                          String updateSQL="UPDATE `raport_konfiguracja_aktualna`.`sector_eq` set last_update="+obecnyDzienCzas+", update_status='"+update_stat+"' where NeName='"+NeNames.get(z)+"'";
                          
                          testStatement.execute(updateSQL);
                          String updateAntena="Update raport_konfiguracja_aktualna.antenaPortInfo set last_update="+obecnyDzienCzas+", update_status='"+update_stat+"' where sectorEq_rowInd in ( select rowAuto_inc from raport_konfiguracja_aktualna.sector_eq eq where eq.NeName='"+NeNames.get(z)+"')";
                          testStatement.execute(updateAntena);
                          
                          
                           String updateCellPerEq="Update raport_konfiguracja_aktualna.cellsPerEqupment set last_update="+obecnyDzienCzas+", update_status='"+update_stat+"' where sectorEq_rowInd in ( select rowAuto_inc from raport_konfiguracja_aktualna.sector_eq eq where eq.NeName='"+NeNames.get(z)+"')";
                          testStatement.execute(updateCellPerEq);
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

		    niekomercyjneClean.add("delete from raport_konfiguracja_aktualna.sector_eq where rowAuto_inc='"+gr_index+"';");
		}
		catch(Exception ee)
		{
		    loger.log(Level.FINEST,"["+this.identyfikator+" CLEANER] ERROR:" , ee);
		}
	    }
            
            niekomercyjneClean.add("delete from raport_konfiguracja_aktualna.cellsPerEqupment  where ( last_update<"+obecnyDzien+" and M2000Index ='"+m2000_Index+"' and update_status!='NOT_CONNECTED')");
            niekomercyjneClean.add("delete from raport_konfiguracja_aktualna.antenaPortInfo   where ( last_update<"+obecnyDzien+" and M2000Index ='"+m2000_Index+"' and update_status!='NOT_CONNECTED')");

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
	    String req="select u.* from raport_konfiguracja_aktualna.sector_eq u  where ( u.last_update<"+obecnyDzien+" and u.M2000Index like '"+m2000_Index+"' and update_status!='NOT_CONNECTED')";
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