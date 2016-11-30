/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package NORTHB_UPDATER;

import NORTHB_UPDATER.Updater_parrent;
import java.sql.ResultSet;
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
public class Locell_details_perRnc_Updater extends Updater_parrent
{
    String rnc_index;
    public Locell_details_perRnc_Updater(String identyfikator,String kontroler,int typOperacji,Logger loger,mysqlpackage.DataSource DOA,NewFile sprzF)
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
                int il=0;
                for(int i=0;i<nody.rowCount();i++)
                {
                    String NE_Name=nody.getValue("NE_Name", i);
                    System.out.println("["+this.identyfikator+"]"+i+"/"+nody.rowCount()+" "+NE_Name);
                        
                    String nodebType=nody.getValue("NodebType", i);
                    String neIndex=nody.getValue("Ne_index", i);
                    Hashtable<String,Paczka> machsparaPerLocellId=new Hashtable<String,Paczka>();
                     String LST_MACHSPARA=null;
                    try{
                       
                        if(nodebType.contains("3900"))
                        {   LST_MACHSPARA=north.make(NE_Name, "LST ULOCELLMACHSPARA:");
                        
                        }
                        else
                        {
                            LST_MACHSPARA=north.make(NE_Name, "LST MACHSPARA:");
                        }
                        if(LST_MACHSPARA!=null&&LST_MACHSPARA.contains("RETCODE = 0"))
                        {
                            NPack npack=new NPack(LST_MACHSPARA);
                            java.util.ArrayList<Paczka> machsLst=npack.getAllPacks();
                            for(int m=0;m<machsLst.size();m++)
                            {
                                String LoId=machsLst.get(m).getWartosc("Local Cell ID");
                                if(LoId!=null&&!LoId.equals(""))
                                {
                                    machsparaPerLocellId.put(LoId, machsLst.get(m));
                                }
                            }
                        }
                                                
                    }
                    catch(Exception ee)
                    {
                     loger.log(Level.FINEST, "["+this.identyfikator+"] ERROR{"+NE_Name+"}:"+LST_MACHSPARA+" \r\n", ee);
                      ee.printStackTrace();   
                    }
                    try
                    {
                        
                        String lst="";
                        if(nodebType.contains("3900"))
                            lst=north.make(NE_Name, "LST ULOCELL:MODE=ALLLOCALCELL");
                        else
                            lst=north.make(NE_Name, "LST LOCELL:");
                        if(lst.contains("RETCODE = 0"))
                        {
                            NPack npack=new NPack(lst);
                            java.util.ArrayList<Paczka> locellkiLst=npack.getAllPacks();
                            for(int n=0;n<locellkiLst.size();n++)
                            {
                                String locellDet="";
                                try
                                {
                                    String locellId=locellkiLst.get(n).getWartosc("Local Cell ID");
                                    Paczka LoPack=null;
                                    if(nodebType.contains("3900"))
                                    {
                                        locellDet=north.make(NE_Name, "LST ULOCELL:MODE=LOCALCELL,ULOCELLID="+locellId);
                                        if(locellDet.contains("RETCODE = 0"))
                                        {
                                            NPack det= new NPack(locellDet.split("\n"), new String[]
                                            {
                                                "LST ULOCELL:"
                                                }, new String[]
                                                {
                                                    "--------"
                                                }, new String[]
                                                {
                                                    "---    END"
                                                });
                                                LoPack=det.getAllPacks().get(0);
                                        }
                                    }
                                    else
                                    {
                                        locellDet=north.make(NE_Name, "LST LOCELL: MODE=LOCALCELL, LOCELL="+locellId);
                                        if(locellDet.contains("RETCODE = 0"))
                                        {
                                            NPack det= new NPack(locellDet.split("\n"), new String[]{"LST LOCELL:"}, new String[]{"-----------------"}, new String[]{"---    END"});
                                            LoPack=det.getAllPacks().get(0);
                                        }
                                    }
                                    if(LoPack==null)
                                    {
                                        System.out.println("BRAK locellDet dla stacji:"+NE_Name);
                                    }
                                    else
                                    {
                                        String eqNr="''";
                                        String equipLine=NewFile.getFirstLine(new String[]{"Sector Equipment ID"}, locellDet);
                                        try
                                        {
                                           eqNr="'"+equipLine.split("=")[1].trim()+"'";
                                        }
                                        catch(Exception ee)
                                        {
                                            
                                        }
                                        String cellId=LoPack.getWartosc("Cell ID");
                                        String dl64qam=LoPack.getWartosc("DL64QAM");
                                        String cell_radius=LoPack.getWartosc("Local Cell Radius(m)");
                                        String cell_inner_ho_radius=LoPack.getWartosc("Local Cell Inner Handover Radius(m)");
                                        String max_pow=LoPack.getWartosc("Max Output Power(0.1dBm)");
                                        String dl_bb_resource_allocation_mode=LoPack.getWartosc("Dl BB Resource Allocation Mode");
                                        String Nband_interf_suppression="###";
                                        String ul_baseband_equipment_id=null;
                                        String dl_baseband_equipment_id=null;
                                        String  IC=null;
                                        String se_hsscch_dtx_switch=null;
                                        String power_margin=null;
                                        String resource_allocate_method=null;
                                       /* String SE Hsscch DTX Switch  
                                                Power Margin(%)  
                                                *  Resource Allocate Method  
                                         */   
                                        if(locellId!=null&&!locellId.equals(""))
                                        {
                                            if(machsparaPerLocellId.containsKey(locellId))
                                            {
                                                Paczka machPack=machsparaPerLocellId.get(locellId);
                                                resource_allocate_method=machPack.getWartosc("Resource Allocate Method"); 
                                                se_hsscch_dtx_switch=machPack.getWartosc("SE Hsscch DTX Switch");
                                                power_margin=machPack.getWartosc("Power Margin(%)");
                                            }
                                        }
                                         
                                        if(nodebType.contains("3900"))
                                        {
                                            IC=LoPack.getWartosc("IC ");
                                            ul_baseband_equipment_id=LoPack.getWartosc("UL Baseband Equipment ID");
                                            dl_baseband_equipment_id=LoPack.getWartosc("DL Baseband Equipment ID");
                                            Nband_interf_suppression=LoPack.getWartosc("Narrowband Interference Suppression");
                                           
                                        }
                                        else
                                        {
                                            IC=LoPack.getWartosc("IC MODE");
                                            ul_baseband_equipment_id=LoPack.getWartosc("UL BB Resource Group No");
                                            dl_baseband_equipment_id=LoPack.getWartosc("DL BB Resource Group No");
                                        }
                                        String insert="insert into raport_konfiguracja_aktualna.locell_details (rowAuto_inc,nodeb_index, locell_id,cell_id,ul_baseband_equipment_id,dl_baseband_equipment_id,DL64QAM, IC,cell_radius,cell_inner_ho_radius,max_pow,dl_bb_resource_allocation_mode,Nband_interf_suppression   ,create_date,last_update_date,update_status,resource_allocate_method,se_hsscch_dtx_switch,power_margin,sectorEquipment) values (null,'"+neIndex+"', '"+locellId+"','"+cellId+"','"+ul_baseband_equipment_id+"','"+dl_baseband_equipment_id+"','"+dl64qam+"','"+IC+"','"+cell_radius+"','"+cell_inner_ho_radius+"','"+max_pow+"','"+dl_bb_resource_allocation_mode+"','"+Nband_interf_suppression+"',"+obecnyDzienCzas+","+obecnyDzienCzas+",'NEW','"+resource_allocate_method+"','"+se_hsscch_dtx_switch+"',"+power_margin+","+eqNr+")ON DUPLICATE KEY UPDATE last_update_date="+obecnyDzienCzas+",update_status='UPDATE', cell_id='"+cellId+"',ul_baseband_equipment_id='"+ul_baseband_equipment_id+"',dl_baseband_equipment_id='"+dl_baseband_equipment_id+"',DL64QAM='"+dl64qam+"',IC='"+IC+"',cell_radius='"+cell_radius+"',cell_inner_ho_radius='"+cell_inner_ho_radius+"',max_pow='"+max_pow+"',dl_bb_resource_allocation_mode='"+dl_bb_resource_allocation_mode+"',Nband_interf_suppression='"+Nband_interf_suppression+"',resource_allocate_method='"+resource_allocate_method+"',se_hsscch_dtx_switch='"+se_hsscch_dtx_switch+"',power_margin="+power_margin+",sectorEquipment="+eqNr;
                                        testStatement.execute(insert);
                                    }
                                }
                                catch(Exception ee)
                                {
                                    loger.log(Level.FINEST, "["+this.identyfikator+"] ERROR:"+locellDet+" \r\n", ee);
                                    //System.out.println("###########\r\n"+locellDet+"\r\n#########\r\n");
                                    ee.printStackTrace();
                                }
                            }
                        }
                        else
                            System.out.println(lst);
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
		    String gr_index=komorki.getValue("rowAuto_inc", p);

		    niekomercyjneClean.add("delete from raport_konfiguracja_aktualna.locell_details where rowAuto_inc='"+gr_index+"';");
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
	    String req="select u.* from raport_konfiguracja_aktualna.locell_details u  where ( u.last_update_date<"+obecnyDzien+" and u.nodeb_index like '"+rnc_index+"|%');";
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
