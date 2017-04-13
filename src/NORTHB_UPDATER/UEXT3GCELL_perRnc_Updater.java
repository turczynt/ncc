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
public class UEXT3GCELL_perRnc_Updater extends Updater_parrent
{
     public UEXT3GCELL_perRnc_Updater(String identyfikator,String rncName,int typOperacji,Logger loger,mysqlpackage.DataSource DOA,NewFile sprzF)
    {
	super(identyfikator,rncName,typOperacji,loger,DOA,sprzF);
    }

    String rnc_id;
    String rnc_index;
     @Override
    public boolean add() throws java.sql.SQLException
    {
	//String checkStr=this.identyfikator;
	String obecnyDzienCzas="'"+sdf.format(DataDzisiaj)+"'";
	String lstBts="";
	StringBuffer insertyAll=new StringBuffer();
         sukces=true;
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
		String rnc_name=rnc.getValue("Rnc_Bsc_Name", 0);
		 rnc_id=rnc.getValue("Rnc_Bsc_Id",0);
		rnc_index=rnc.getValue("Rnc_Bsc_Index", 0);
		String m2000_Index=rnc.getValue("M2000_Index", 0);
		north=new nbipackage.NorthB(m2000_ip, "U-boot", "utranek098",null);
		this.errorInfo=this.errorInfo+"; connect with "+m2000_ip+" by NorthB";
		lstBts=north.make(this.kontrolerName, "LST UEXT3GCELL:LSTFORMAT=HORIZONTAL;");

		if(lstBts!=null&&lstBts.contains("RETCODE = 0"))
		{
		    //sukces = true;
		    System.out.println(this.identyfikator+" LST UEXT3GCELL:LSTFORMAT=HORIZONTAL: wykonane");
		    this.errorInfo=this.errorInfo+"; POBRANE LST UEXT3GCELL:LSTFORMAT=HORIZONTAL";
		}
		else
		{
		    System.out.println(this.identyfikator+" LST UEXT3GCELL:LSTFORMAT=HORIZONTAL ERROR");

		    this.errorInfo=this.errorInfo+"; BLAD W POBIERANIU POBRANE LST UEXT2GCELL:LSTFORMAT=HORIZONTAL";
		    sukces=false;

		}
		
		this.errorInfo=this.errorInfo+"; ZAMKNIECIE NORTHB";
		this.errorInfo=this.errorInfo+"; POBRANIE KOMOREK Z DB";

		if(lstBts.contains("RETCODE = 0"))
		{
		    java.util.ArrayList<Paczka> relacje2g2g=new java.util.ArrayList<Paczka>();
		    try
		    {
			NPack nn=new NPack(lstBts);
			relacje2g2g=nn.getAllPacks();

		    }
		    catch(Exception ewqr)
		    {
			
			loger.log(Level.FINEST, "["+this.identyfikator+"]ERROR ", ewqr);
                         sukces=false;
		    }
		    if(relacje2g2g!=null)
		    {


			for(int u=0;u<relacje2g2g.size();u++)
			{
			    Paczka relacja=relacje2g2g.get(u);
			    if(relacja!=null&&relacja.getWartosc("Cell ID of Neighboring RNC")!=null&&relacja.getWartosc("Cell ID of Neighboring RNC").length()>0)
			    {

				
                                /*
                                `external_rnc_bsc_index_FK` INT(11) NOT NULL,
                                `external_Cell_Name` VARCHAR(50) NULL DEFAULT NULL,
                                `external_Cell_Index` INT(11) NOT NULL,
                                `lac_dec` INT(11) NULL DEFAULT NULL,
                                `neigh_rnc_id` INT(11) NULL DEFAULT NULL,
                                `Scrambling_code` INT(11) NULL DEFAULT NULL,
                                `Route_Area` INT(11) NULL DEFAULT NULL,
                                `UARFCN` INT(11) NULL DEFAULT NULL,
                                `oryginal_ucell_rnc_id` INT(11) NULL DEFAULT NULL COMMENT 'wartosc "RNC ID" LST UEXT3GCELL:',
                                `last_update` DATETIME NULL DEFAULT NULL,
                                `oryginal_ucell_rnc_rnc_index_FK` INT(11) NULL DEFAULT NULL COMMENT 'index from oncall.konfiguracja_aktualna_rnc_bsc',
                                `oryginal_ucell_index_FK` VARCHAR(50) NULL DEFAULT NULL COMMENT 'cell_index from oncall.konfiguracja_aktualna_ucell',
                                 */                                
                                String external_rnc_bsc_index_FK=rnc_index;
                                String external_Cell_Index=relacja.getWartosc("Cell ID of Neighboring RNC");
                                String external_Cell_Name="'"+relacja.getWartosc("Cell Name")+"'";
                                String lac_dec=relacja.getWartosc("Location Area Code");
                                String neigh_rnc_id=relacja.getWartosc("Neighboring RNC ID");
                                String rac_dec="'"+relacja.getWartosc("Routing area code")+"'";
                                try
                                {
                                    if (lac_dec.contains("("))
                                    {
                                        lac_dec =  lac_dec.substring(lac_dec.indexOf("(") + 1, lac_dec.indexOf(")"));
                                    }
                                   
                                    if (rac_dec.contains("("))
                                    {
                                        rac_dec = rac_dec.substring(rac_dec.indexOf("(") + 1, rac_dec.indexOf(")"));
                                    }
                                }catch (Exception e){
                                    loger.log(Level.FINEST, "[" + this.identyfikator + "]ERROR:", e);
                                }
                                String Scrambling_code=relacja.getWartosc("DL Primary Scrambling Code");
                                String UARFCN=relacja.getWartosc("Uplink UARFCN");
                               
                                //String oryginal_ucell_rnc_id="'"+relacja.getWartosc("RNC ID")+"'";
                                String oryginal_ucell_index_FK="(select g.Cell_Index from oncall.konfiguracja_aktualna_ucell g where g.Cell_Id="+external_Cell_Index+" and g.Lac_dec="+lac_dec+" limit 1)";
                                String oryginal_ucell_rnc_rnc_index_FK="(select b.Rnc_Bsc_Index from oncall.konfiguracja_aktualna_nodeb b where b.Nodeb_Index=(select g.Nodeb_Index from oncall.konfiguracja_aktualna_ucell g where  g.Cell_Id="+external_Cell_Index+" and g.Lac_dec="+lac_dec+" limit 1))";
                                
                             
                                
                                
                                 String insert="INSERT INTO `raport_konfiguracja_aktualna`.`UEXT3GCELL_perRnc` "
                                         + "("
                                            + "external_rnc_bsc_index_FK,"
                                            + "external_Cell_Index,"
                                            + "external_Cell_Name,"
                                            + "lac_dec,"
                                            + "neigh_rnc_id,"
                                            + "Scrambling_code,"
                                            + "UARFCN,"
                                            + "rac_dec,"                                        
                                            + "oryginal_ucell_index_FK,"                                        
                                            + "oryginal_ucell_rnc_rnc_index_FK,"                                        
                                            + "last_update"
                                         + ") VALUES"
                                         + "("
                                            + external_rnc_bsc_index_FK+","
                                            + external_Cell_Index+","
                                            + external_Cell_Name+","
                                            + lac_dec+","
                                            + neigh_rnc_id+","
                                            + Scrambling_code+","
                                            + UARFCN+","
                                            + rac_dec+","
                                            + oryginal_ucell_index_FK+","
                                            + oryginal_ucell_rnc_rnc_index_FK+","
                                            + obecnyDzienCzas+""
                                         + ")"
                                         + " ON DUPLICATE KEY UPDATE "
                                            + "external_Cell_Name="+external_Cell_Name+","
                                            + "lac_dec="+lac_dec+","
                                            + "neigh_rnc_id="+neigh_rnc_id+","
                                            + "Scrambling_code="+Scrambling_code+","
                                            + "UARFCN="+UARFCN+","
                                            + "rac_dec="+rac_dec+","
                                            + "oryginal_ucell_index_FK="+oryginal_ucell_index_FK+","
                                            + "oryginal_ucell_rnc_rnc_index_FK="+oryginal_ucell_rnc_rnc_index_FK+","
                                            + "last_update="+obecnyDzienCzas+";";
                                 
                                 //  System.out.println(insert);
                                 try
                                 {
                                     testStatement.execute(insert);
                                 }
                                 catch(java.sql.SQLException ss )
                                 {
                                     loger.throwing(this.getClass().toString(), this.identyfikator+"["+insert+"]", ss);		    //System.err.println("BLAD PRZY ZAMYKANIU NORTHB");
                                 }
                                

			    }
			}
			this.errorInfo=this.errorInfo+"; END for(int u=0;u<gcell.size();u++)";
                       
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
  
    @Override
     public boolean clearNotUpdatet()
    {
        
	 try
	 {
	     java.util.ArrayList<String> poleceniaK=new java.util.ArrayList<String>();
	     java.util.ArrayList<String> poleceniaNK=new java.util.ArrayList<String>();
	     String obecnyDzien="'"+sdf.format(DataDzisiaj)+"'";
             String query="delete from raport_konfiguracja_aktualna.UEXT3GCELL_perRnc where last_update<"+obecnyDzien+" and external_rnc_bsc_index_FK="+rnc_index+";";
             
            testStatement.execute(query);
	
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
       
	String obecnyDzien="'"+sdf.format(dateBefore)+"'";
	try
	{
	    String req="select * from `raport_konfiguracja_aktualna`.`UEXT3GCELL_perRnc` bt where(bt.last_update<"+obecnyDzien+" and bt.external_rnc_bsc_index_FK='"+rnc_index+"');";
	    ResultSet res=testStatement.executeQuery(req);
	    OdpowiedzSQL rnc=Baza.createAnswer(res);
	    return rnc;
	}
	catch(Exception ee)
	{
	    loger.log(Level.FINEST,"["+this.identyfikator+"] ERROR:" , ee);
	    return null;
            
	}
    }


}
