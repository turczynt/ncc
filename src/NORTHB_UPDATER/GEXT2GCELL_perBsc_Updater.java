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
public class GEXT2GCELL_perBsc_Updater extends Updater_parrent
{
     public GEXT2GCELL_perBsc_Updater(String identyfikator,String bscName,int typOperacji,Logger loger,mysqlpackage.DataSource DOA,NewFile sprzF)
    {
	super(identyfikator,bscName,typOperacji,loger,DOA,sprzF);
    }

    String bsc_id;
    String bsc_index;
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
		String bsc_name=rnc.getValue("Rnc_Bsc_Name", 0);
		 bsc_id=rnc.getValue("Rnc_Bsc_Id",0);
		bsc_index=rnc.getValue("Rnc_Bsc_Index", 0);
		String m2000_Index=rnc.getValue("M2000_Index", 0);
		north=new nbipackage.NorthB(m2000_ip, "U-boot", "utranek098",null);
		this.errorInfo=this.errorInfo+"; connect with "+m2000_ip+" by NorthB";
		lstBts=north.make(this.kontrolerName, "LST GEXT2GCELL:;");

		if(lstBts!=null&&lstBts.contains("RETCODE = 0"))
		{
		    //sukces = true;
		    System.out.println(this.identyfikator+" LST GEXT2GCELL: wykonane");
		    this.errorInfo=this.errorInfo+"; POBRANE LST GEXT2GCELL:";
		}
		else
		{
		    System.out.println(this.identyfikator+" LST GEXT2GCELL: ERROR");

		    this.errorInfo=this.errorInfo+"; BLAD W POBIERANIU POBRANE LST G2GNCELL:";
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
			System.err.println("WYJEBALO przy NPack LST PTPBVC:;");
			loger.log(Level.FINEST, "["+this.identyfikator+"]ERROR ", ewqr);
                         sukces=false;
		    }
		    if(relacje2g2g!=null)
		    {


			for(int u=0;u<relacje2g2g.size();u++)
			{
			    Paczka relacja=relacje2g2g.get(u);
			    if(relacja!=null&&relacja.getWartosc("Cell Index")!=null&&relacja.getWartosc("Cell Index").length()>0)
			    {
				//	`external_rnc_bsc_index_FK` INT(11) NULL DEFAULT NULL,
                                //`external_Cell_Name` VARCHAR(50) NULL DEFAULT NULL,
	//`external_Cell_Index` INT(11) NULL DEFAULT NULL,
	//`lac_dec` INT(11) NULL DEFAULT NULL,
	//`Ci_dec` INT(11) NULL DEFAULT NULL,
	//`MCC` INT(11) NULL DEFAULT NULL,
	//`MNC` INT(11) NULL DEFAULT NULL,
	//`NCC` INT(11) NULL DEFAULT NULL,
	//`BCC` INT(11) NULL DEFAULT NULL,
	//`BCCH_FD` INT(11) NULL DEFAULT NULL,
	//`oryginal_gcell_bsc_id` INT(11) NULL DEFAULT NULL COMMENT 'wartosc "bsc index" LST GEXT2GCELL:',
	//`last_update` DATETIME NULL DEFAULT NULL,
	//`oryginal_gcell_rnc_bsc_index_FK` INT(11) NOT NULL COMMENT 'index from oncall.konfiguracja_aktualna_rnc_bsc',
	//`oryginal_gcell__index_FK` VARCHAR(50) NULL DEFAULT NULL COMMENT 'cell_index from oncall.konfiguracja_aktualna_gcell'
                                
                                String external_rnc_bsc_index_FK=bsc_index;
                                String external_Cell_Index=relacja.getWartosc("Cell Index");
                                String external_Cell_Name="'"+relacja.getWartosc("Cell Name")+"'";
                                String lac_dec=relacja.getWartosc("Cell LAC");
                                String Ci_dec=relacja.getWartosc("Cell CI");
                                
                                
                                try
                                {
                                    if (lac_dec.contains("("))
                                    {
                                        lac_dec =  lac_dec.substring(lac_dec.indexOf("(") + 1, lac_dec.indexOf(")"));

                                    }
                                    if (Ci_dec.contains("("))
                                    {
                                        Ci_dec = Ci_dec.substring(Ci_dec.indexOf("(") + 1, Ci_dec.indexOf(")"));

                                    }
                                }
                                catch (Exception e)
                                {
                                    loger.log(Level.FINEST, "[" + this.identyfikator + "]ERROR:", e);
                                }

                                
                                String MCC=relacja.getWartosc("MCC");
                                String MNC=relacja.getWartosc("MNC");
                                String NCC=relacja.getWartosc("NCC");
                                String BCC=relacja.getWartosc("BCC");
                                String BCCH_FD=relacja.getWartosc("BCCH FD");
                                String oryginal_gcell_bsc_id="'"+relacja.getWartosc("BSC Index")+"'";
                                String oryginal_gcell_index_FK="(select g.Cell_Index from oncall.konfiguracja_aktualna_gcell g where g.Ci_dec="+Ci_dec+" and g.Lac_dec="+lac_dec+" limit 1)";
                                String oryginal_gcell_rnc_bsc_index_FK="(select b.Rnc_Bsc_Index from oncall.konfiguracja_aktualna_bts b where b.Bts_Index=(select g.Bts_Index from oncall.konfiguracja_aktualna_gcell g where g.Ci_dec="+Ci_dec+" and g.Lac_dec="+lac_dec+" limit 1))";
                                
                                
                                 String insert="INSERT INTO `raport_konfiguracja_aktualna`.`GEXT2GCELL_perBsc` "
                                         + "("
                                            + "external_rnc_bsc_index_FK,"
                                            + "external_Cell_Index,"
                                            + "external_Cell_Name,"
                                            + "lac_dec,"
                                            + "Ci_dec,"
                                            + "MCC,"
                                            + "MNC,"
                                            + "NCC,"                                        
                                            + "BCC,"                                        
                                            + "BCCH_FD,"                                        
                                            + "oryginal_gcell_bsc_id,"                                        
                                            + "oryginal_gcell_index_FK,"                                        
                                            + "oryginal_gcell_rnc_bsc_index_FK,"                                        
                                            + "last_update"
                                         + ") VALUES"
                                         + "("
                                            + external_rnc_bsc_index_FK+","
                                            + external_Cell_Index+","
                                            + external_Cell_Name+","
                                            + lac_dec+","
                                            + Ci_dec+","
                                            + MCC+","
                                            + MNC+","
                                            + NCC+","
                                            + BCC+","
                                            + BCCH_FD+","
                                            + oryginal_gcell_bsc_id+","
                                            + oryginal_gcell_index_FK+","
                                            + oryginal_gcell_rnc_bsc_index_FK+","
                                            + obecnyDzienCzas+""
                                         + ")"
                                         + " ON DUPLICATE KEY UPDATE "
                                            + "external_Cell_Name="+external_Cell_Name+","
                                            + "lac_dec="+lac_dec+","
                                            + "Ci_dec="+Ci_dec+","
                                            + "MCC="+MCC+","
                                            + "MNC="+MNC+","
                                            + "NCC="+NCC+","
                                            + "BCC="+BCC+","
                                            + "BCCH_FD="+BCCH_FD+","
                                            + "oryginal_gcell_bsc_id="+oryginal_gcell_bsc_id+","
                                            + "oryginal_gcell_index_FK="+oryginal_gcell_index_FK+","
                                            + "oryginal_gcell_rnc_bsc_index_FK="+oryginal_gcell_rnc_bsc_index_FK+","
                                            +"last_update="+obecnyDzienCzas+";";
                                 
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
             String query="delete from raport_konfiguracja_aktualna.GEXT2GCELL_perBsc where last_update_date<"+obecnyDzien+" and external_rnc_bsc_index_FK="+bsc_index+";";
             
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
	    String req="select * from `raport_konfiguracja_aktualna`.`GEXT2GCELL_perBsc` bt where(bt.last_update<"+obecnyDzien+" and bt.external_rnc_bsc_index_FK='"+bsc_index+"');";
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
