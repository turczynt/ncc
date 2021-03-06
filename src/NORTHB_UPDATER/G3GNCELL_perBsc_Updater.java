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
public class G3GNCELL_perBsc_Updater extends Updater_parrent
{
     public G3GNCELL_perBsc_Updater(String identyfikator,String bscName,int typOperacji,Logger loger,mysqlpackage.DataSource DOA,NewFile sprzF)
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
		lstBts=north.make(this.kontrolerName, "LST G3GNCELL:;");

		if(lstBts!=null&&lstBts.contains("RETCODE = 0"))
		{
		    //sukces = true;
		    System.out.println(this.identyfikator+" LST G3GNCELL: wykonane");
		    this.errorInfo=this.errorInfo+"; POBRANE LST G3GNCELL:";
		}
		else
		{
		    System.out.println(this.identyfikator+" LST G3GNCELL: ERROR");

		    this.errorInfo=this.errorInfo+"; BLAD W POBIERANIU POBRANE LST G3GNCELL:";
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
			    if(relacja!=null&&relacja.getWartosc("Source Cell Index")!=null&&relacja.getWartosc("Source Cell Index").length()>0&&relacja.getWartosc("Neighbor Cell Index")!=null&&relacja.getWartosc("Neighbor Cell Index").length()>0)
			    {
				//  Source Cell Index  Source Cell Name                  Neighbor Cell Index  Neighbor Cell Name                     RSCP Offset  Ec/No Offset  TDD 3G Better Cell HO Watch Time  TDD 3G Better Cell HO Valid Time  FDD 3G Better Cell HO Watch Time  FDD 3G Better Cell HO Valid Time  Neighboring Cell Priority  Directional of Neighboring Cell


                             
                                // `source_rnc_bsc_index_FK` INT(11) NOT NULL,
                                //`source_cell_index` INT(11) NOT NULL,
                                //`neighbor_cell_index` INT(11) NOT NULL,
                                //`source_cell_name` VARCHAR(50) NULL DEFAULT NULL,
                                //`neighbor_cell_name` VARCHAR(50) NULL DEFAULT NULL,
                                //`source_bts_index_FK` VARCHAR(50) NULL DEFAULT NULL,
                                //`source_gcell_index_FK` VARCHAR(50) NULL DEFAULT NULL,
                                //`neighbor_ucell_index_FK` VARCHAR(50) NULL DEFAULT NULL,
                                //`last_update` DATETIME NULL DEFAULT NULL,
                                
                                String source_rnc_bsc_index_FK=bsc_index;
                                String source_cell_index=relacja.getWartosc("Source Cell Index");
                                String neighbor_cell_index=relacja.getWartosc("Neighbor Cell Index");
                                String source_cell_name="'"+relacja.getWartosc("Source Cell Name")+"'";
                                String neighbor_cell_name="'"+relacja.getWartosc("Neighbor Cell Name")+"'";
                                String source_bts_index_FK="(select g.Bts_Index from oncall.konfiguracja_aktualna_gcell g  where g.Cell_Id='"+source_cell_index+"' and g.Bts_Index like '"+bsc_index+"|%' limit 1)";
                                String source_gcell_index_FK="(select g.Cell_Index from oncall.konfiguracja_aktualna_gcell g  where g.Cell_Id='"+source_cell_index+"' and g.Bts_Index like '"+bsc_index+"|%' limit 1)";
                                String neighbor_ucell_index_FK=//"(select indexy.Cell_Index from  (select g.Cell_Index as Cell_Index from oncall.konfiguracja_aktualna_gcell g  where g.Cell_Id='"+neighbor_cell_index+"' and g.Bts_Index like '"+bsc_index+"|%' limit 1 union select e.oryginal_gcell_index_FK as Cell_Index from raport_konfiguracja_aktualna.GEXT2GCELL_perBsc e where e.external_rnc_bsc_index_FK='"+bsc_index+"' and e.external_Cell_Index='"+neighbor_cell_index+"' limit 1) as indexy limit 1)";     
                                //"(select g.Cell_Index from oncall.konfiguracja_aktualna_gcell g  where g.Cell_Id='"+neighbor_cell_index+"' and g.Bts_Index like '"+bsc_index+"|%' limit 1)";
                               //
                                "(SELECT COALESCE(	(select g.Cell_Index as Cell_Index from oncall.konfiguracja_aktualna_ucell g  where g.Cell_Id='"+neighbor_cell_index+"' and g.Nodeb_Index like '"+bsc_index+"|%' limit 1),	(select e.oryginal_ucell_index_FK as Cell_Index from raport_konfiguracja_aktualna.GEXT3GCELL_perBsc e where e.external_rnc_bsc_index_FK='"+bsc_index+"' and e.external_Cell_Index='"+neighbor_cell_index+"' limit 1)) as Cell_Index)";
                                
                                
                                
                                String insert="INSERT INTO `raport_konfiguracja_aktualna`.`G3GNCELL_perBsc` "
                                         + "("
                                            + "source_rnc_bsc_index_FK,"
                                            + "source_cell_index,"
                                            + "neighbor_cell_index,"
                                            + "source_cell_name,"
                                            + "neighbor_cell_name,"
                                            + "source_bts_index_FK,"
                                            + "source_gcell_index_FK,"
                                            + "neighbor_ucell_index_FK,"                                         
                                            + "last_update"
                                         + ") VALUES"
                                         + "("
                                            + source_rnc_bsc_index_FK+","
                                            + source_cell_index+","
                                            + neighbor_cell_index+","
                                            + source_cell_name+","
                                            + neighbor_cell_name+","
                                            + source_bts_index_FK+","
                                            + source_gcell_index_FK+","
                                            + neighbor_ucell_index_FK+","
                                            + obecnyDzienCzas+""
                                         + ")"
                                         + " ON DUPLICATE KEY UPDATE "
                                            + "source_cell_name="+source_cell_name+","
                                            + "neighbor_cell_name="+neighbor_cell_name+","
                                            + "neighbor_ucell_index_FK=(	 	IF( neighbor_ucell_index_FK IS NULL,(SELECT COALESCE( (select g.Cell_Index as Cell_Index from oncall.konfiguracja_aktualna_ucell g  where g.Cell_Id='"+neighbor_cell_index+"' and g.Nodeb_Index like '"+bsc_index+"|%' limit 1),	(select e.oryginal_ucell_index_FK as Cell_Index from raport_konfiguracja_aktualna.GEXT3GCELL_perBsc e where e.external_rnc_bsc_index_FK='"+bsc_index+"' and e.external_Cell_Index='"+neighbor_cell_index+"' limit 1)			)			),			neighbor_ucell_index_FK 		)	 ),"
                                            +"last_update="+obecnyDzienCzas+";";
                                 
                                  // System.out.println(insert);
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
             String query="delete from raport_konfiguracja_aktualna.G3GNCELL_perBsc where last_update<"+obecnyDzien+" and source_rnc_bsc_index_FK='"+bsc_index+"';";
             
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
	    String req="select * from `raport_konfiguracja_aktualna`.`G3GNCELL_perBsc` bt where(bt.last_update<"+obecnyDzien+" and bt.source_rnc_bsc_index_FK='"+bsc_index+"');";
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
