/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package MML_UPDATER;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import poolThread.UpdaterInterface;
import nbipackage.*;
import mysqlpackage.*;
import poolThread.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author turczyt
 */
public class MML_UCELL_perRNC_Updater extends MML_Updater_parrent
{

    public MML_UCELL_perRNC_Updater(String identyfikator, String rncName, Logger loger, DataSource DOA,String mmlDir, NewFile sprzF)
    {
	super(identyfikator,rncName,loger,DOA,mmlDir,sprzF);
    }
    String rncId;
    Hashtable<String,String> komedny_row_id;
    Hashtable<String,String> parametry_row_id;
     Hashtable<String,String> nodeb_row_ids;
     HashMap<String,java.util.ArrayList<nbipackage.MmlKommand>> komendyUcell;
    String typ="2";
    java.util.ArrayList<String>komendsToUpdate;
    public boolean add() throws java.sql.SQLException
    {
	boolean udane=false;
	String obecnyDzienCzas="'"+sdf.format(DataDzisiaj)+"'";
         komendsToUpdate=new java.util.ArrayList<String>();
        
	try
	{
	    komedny_row_id=new java.util.Hashtable<String, String>();
	    parametry_row_id=new java.util.Hashtable<String, String>();
	    nodeb_row_ids=new java.util.Hashtable<String, String>();
	  


	    String req="select r.Rnc_Bsc_Id from oncall.konfiguracja_aktualna_rnc_bsc r where r.Rnc_Bsc_Name ='"+this.kontrolerName+"' LIMIT 1";
	    System.out.println(req);
	    
	    OdpowiedzSQL rnc= selectSql(req);
	    this.rncId=rnc.getValue("Rnc_Bsc_Id", 0);
	


	    String[] cellLines=super.mml.getLinia("ADD UCELLSETUP:");
	      getCellKomends();
	    get_nodeb_row_id();

	  //  BSC_TH.join();
            
	    for ( int n=0;n<cellLines.length;n++)
	    {
                String CellName=null;
		try{
		nbipackage.MmlKommand mmlKom=new nbipackage.MmlKommand(cellLines[n]);
		//ADD UCELLSETUP:CELLID=23824, CELLNAME="22382411_CHR1007D_O_SLASKA_64A", LOCELL=22382411,
				//BANDIND=Band1, UARFCNUPLINKIND=TRUE, UARFCNUPLINK=9886, UARFCNDOWNLINK=10836, CFGRACIND=REQUIRE,
				//RAC=H'15, CNOPGRPINDEX=0, MAXTXPOWER=430, TCELL=CHIP0, NINSYNCIND=5, NOUTSYNCIND=50, TRLFAILURE=50,
				//DLTPCPATTERN01COUNT=10, PSCRAMBCODE=144, TXDIVERSITYIND=FALSE, SPGID=1, LAC=H'00CF, SAC=H'5D10, CIO=0,
				//NODEBNAME="223824_CHR1007D_SLASKA_64A", SRN=0, SN=16, SSN=3, VPLIMITIND=FALSE, DSSFLAG=FALSE,
				//DSSSMALLCOVMAXTXPOWER=430, CCHCNOPINDEX=255, DPGID=255, CELLHETFLAG=FALSE;

		String NodebName=mmlKom.getValue("NODEBNAME");
		CellName=mmlKom.getValue("CELLNAME");
		String CellID=mmlKom.getValue("CELLID");
		loger.log(Level.INFO,  "["+this.identyfikator+"] "+n+"/"+cellLines.length+" CELLNAME="+CellName);
		//System.out.println(""+n+"/"+cellLines.length+" CELLNAME="+CellName+" "+this.kontrolerName);
		String nodeb_row_id="";
		if(nodeb_row_ids.containsKey(rncId+";"+CellID))
		{
		    nodeb_row_id=nodeb_row_ids.get(rncId+";"+CellID);
		}
		else
		{
		    String Nreq="select row_id from raport_konfiguracja_aktualna.mml_rnc_Nodeb_ident where name='"+NodebName+"'";
		    OdpowiedzSQL node=selectSql(Nreq);
		    nodeb_row_id=node.getValue("row_id", 0);
		    get_nodeb_row_id();
		}



		if(CellName!=null&&!CellName.equals("")&&nodeb_row_id!=null&&!nodeb_row_id.equals(""))
		{
		    //String NodebId=mmlKom.getValue("nodebid");
		    String query="INSERT INTO `raport_konfiguracja_aktualna`.`mml_rnc_Ucell_ident` (`cell_id`, `rnc_id`, `name`,`nodeb_row_id`, `create_date`, `last_update`) VALUES ("+CellID+",'"+rncId+"', '"+CellName+"','"+nodeb_row_id+"', "+obecnyDzienCzas+", "+obecnyDzienCzas+") ON DUPLICATE KEY UPDATE last_update="+obecnyDzienCzas+",name='"+CellName+"',nodeb_row_id='"+nodeb_row_id+"'";
		   // System.out.println(query);
		    testStatement.executeUpdate(query);
		    String UCreq="select row_id from raport_konfiguracja_aktualna.mml_rnc_Ucell_ident where cell_id='"+CellID+"' and rnc_id='"+rncId+"' LIMIT 1";
		    OdpowiedzSQL cell=selectSql(UCreq);
		    
		    // ResultSet UCres=testStatement.executeQuery(UCreq);
		   // OdpowiedzSQL cell=Baza.createAnswer(UCres);
		    String cell_row_id=cell.getValue("row_id", 0);

		    cellParametrization(rncId,nodeb_row_id,cell_row_id,CellID,obecnyDzienCzas);
		}
		}
		catch(Exception ee)
		{
		    loger.throwing(this.getClass().toString(), "["+this.identyfikator+"] "+CellName+" ERROR:"+ee.getMessage(), ee);
		}   
	    }
           
            udane=true;
        }
	catch(Exception ee)
	{
	     loger.throwing(this.getClass().toString(), "["+this.identyfikator+"] ERROR:"+ee.getMessage(), ee);
	}

	return udane ;
    }

    public boolean clearNotUpdatet()
    {
	String reqNodebIdent="";
	try
	{
	    String obecnyDzienCzas="'"+sdf.format(DataDzisiaj)+"'";
	    reqNodebIdent="delete from raport_konfiguracja_aktualna.mml_rnc_Ucell_ident  where last_update<"+obecnyDzienCzas+" and rnc_id='"+this.rncId+"';";
	    testStatement.executeUpdate(reqNodebIdent);
	}
	catch(Exception ee)
	{
	     loger.throwing(this.getClass().toString(), "["+this.identyfikator+" CLEANER] ERROR:req="+reqNodebIdent+"\r\n"+ee.getMessage(), ee);
	}
	return true;
    }
    
    private boolean cellParametrization(String rncId,String nodeb_row_id, String cell_row_id,String CellID,String obecnyDzienCzas)throws java.sql.SQLException
    {


       
	java.util.ArrayList<nbipackage.MmlKommand> cellParamLines=this.komendyUcell.get(CellID);
	this.komendyUcell.remove(CellID);
	    String komendaUcell="INSERT IGNORE INTO raport_konfiguracja_aktualna.mml_rnc_Ucell_param (`cell_row_id`, `param_ident`, `value`, `value_dictionary_id`, `last_update`, `line_id`) VALUES\r\n";
            
            String komendaNUcell="INSERT IGNORE INTO raport_konfiguracja_aktualna.mml_rnc_Ucell_param_with_ncell (cell_row_id,ncell_id  ,param_ident,value,value_dictionary_id,`line_id`,last_update) VALUES";
           boolean pierwszyU=true;
           boolean pierwszyN=true;
	    for ( int n=0;n<cellParamLines.size();n++)
	    {
		try
		{
		    //nbipackage.MmlKommand mmlKom=cellParamLines.get(n);
		    //String cellId=mmlKom.getValue("cellid");

		   //if(cellId!=null&&!cellId.equals(""))
		    //{
			String typ_kom=cellParamLines.get(n).getCommandType();
			String komName=cellParamLines.get(n).getCommandName();
			//System.out.println(""+n+"/"+cellParamLines.length+" "+komName);
			java.util.ArrayList<String> paramNames=cellParamLines.get(n).getParamNames();
			boolean ncellKomm=false;
			String NCELLID=null;
			if(cellParamLines.get(n).ifContainsParam("NCELLID"))
			{
			    ncellKomm=true;
			    NCELLID=cellParamLines.get(n).getValue("NCELLID");
			}

			//parametry_row_id.put(kom_name+";"+param_name,kom_row_id+";"+param_row_id) ;
			//komedny_row_id.put(kom_name, kom_row_id) ;
			String komenda_row_id=null;
			if(komedny_row_id.containsKey(typ_kom+";"+komName)&&!komedny_row_id.get(typ_kom+";"+komName).equals(""))
			{
			    komenda_row_id=komedny_row_id.get(typ_kom+";"+komName);
			}
			else
			{
			    String query="INSERT INTO `raport_konfiguracja_aktualna`.`mml_rnc_komendy` (`typ`, `nazwa`, `typ_kom`, `create_date`) VALUES ("+typ+",'"+komName+"', '"+typ_kom+"',"+obecnyDzienCzas+") ON DUPLICATE KEY UPDATE create_date="+obecnyDzienCzas+",typ_kom='"+typ_kom+"'";
			    System.out.println("####"+query);
			    testStatement.executeUpdate(query);
			    String reqKomName="select r.row_id from raport_konfiguracja_aktualna.mml_rnc_komendy r where r.typ="+typ+" and r.nazwa='"+komName+"' and r.typ_kom='"+typ_kom+"';";
			   
			    ResultSet resKomName=testStatement.executeQuery(reqKomName);
			    OdpowiedzSQL komN=Baza.createAnswer(resKomName);
			  
                            param_komm_ids();
			   komenda_row_id=komN.getValue("row_id", 0);
			   komedny_row_id.put(typ_kom+";"+komName, komenda_row_id);

			}
                        
			String nodeb_ident=nodeb_row_id;
			String ucell_ident=cell_row_id;
			for(int z=0;z<paramNames.size();z++)
			{
			    String insertNodebParam="";
			    try{
			    String Pname=paramNames.get(z);
			    String PVal=cellParamLines.get(n).getValue(Pname);
			    PVal=PVal.replaceAll("'", "\\\\'");
			    
			    String param_row_id=null;
			    if(parametry_row_id.containsKey(typ_kom+";"+komName+";"+Pname))
			    {
				String[] paramIdTmp=parametry_row_id.get(typ_kom+";"+komName+";"+Pname).split("[;]");
				param_row_id=paramIdTmp[1];
			    }
			    else
			    {
				//parametry_row_id.put(kom_name+";"+param_name,kom_row_id+";"+param_row_id) ;


				String insert="INSERT INTO `raport_konfiguracja_aktualna`.`mml_rnc_parametry` (`komenda_row_id`, `nazwa`,`create_date`) VALUES ('"+komenda_row_id+"','"+Pname+"',"+obecnyDzienCzas+") ON DUPLICATE KEY UPDATE create_date="+obecnyDzienCzas+";";

				//System.out.println(insert);

				testStatement.executeUpdate(insert);

				param_komm_ids();
				if(parametry_row_id.containsKey(typ_kom+";"+komName+";"+Pname))
				{
				    String[] paramIdTmp=parametry_row_id.get(typ_kom+";"+komName+";"+Pname).split("[;]");
				    param_row_id=paramIdTmp[1];
				}
			    }
			    if(param_row_id!=null)
			    {
				String paramVal_token="NULL";
				String param_dict_id="NULL";

				if(PVal.endsWith(";"))
				    PVal=PVal.replace(";", "");
				if(PVal.length()<50)
				    paramVal_token="'"+PVal+"'";
				else
				{

				    String dictIdSel="select * from raport_konfiguracja_aktualna.mml_rnc_value_dictionary where param_row_id="+param_row_id+" and param_value='"+PVal+"';";
				    ResultSet Komres=testStatement.executeQuery(dictIdSel);
				    OdpowiedzSQL KomSql=Baza.createAnswer(Komres);
				    if(KomSql!=null&&KomSql.rowCount()>0)
				    {
					param_dict_id="'"+KomSql.getValue("row_id", 0)+"'";
				    }
				    else
				    {
					String insDir="insert into raport_konfiguracja_aktualna.mml_rnc_value_dictionary(param_row_id,param_value,value_length) values('"+param_row_id+"','"+PVal+"','"+PVal.length()+"')";
					//System.out.println(insDir);
					testStatement.executeUpdate(insDir);

				    }

				    Komres=testStatement.executeQuery(dictIdSel);
				    KomSql=Baza.createAnswer(Komres);
				    if(KomSql!=null&&KomSql.rowCount()>0)
				    {
					param_dict_id="'"+KomSql.getValue("row_id", 0)+"'";
				    }
				}
                                komendsToUpdate.contains(typ_kom+";"+komName);
                                {
				if(ncellKomm)
				{
				 /*insertNodebParam="INSERT INTO raport_konfiguracja_aktualna.mml_rnc_Ucell_param_with_ncell(cell_row_id,rnc_id, nodeb_row_id,ncell_id  ,param_ident,komm_ident,value,value_dictionary_id,`line_id`,last_update) VALUES("
				    +"'"+ucell_ident+"',"
				    +"'"+rncId+"',"
				    + "'"+nodeb_ident+"',"
				    +"'"+NCELLID+"',"
				    + "'"+param_row_id+"',"
				    //+ "(select r.row_id from raport_konfiguracja_aktualna.mml_rnc_parametry r where r.komenda_row_id='"+komenda_row_id+"' and r.nazwa='"+Pname+"' LIMIT 1 ),"
				    + "'"+komenda_row_id+"',"
				    + ""+paramVal_token+","
				    + ""+param_dict_id+","
				    + "'"+n+"',"
				    + ""+obecnyDzienCzas+""
				    + ")";*/
                                    //insertNodebParam="INSERT INTO raport_konfiguracja_aktualna.mml_rnc_Ucell_param_with_ncell(cell_row_id,ncell_id  ,param_ident,value,value_dictionary_id,`line_id`,last_update) VALUES"+
                                      insertNodebParam="("
				    +"'"+ucell_ident+"',"
				    +"'"+NCELLID+"',"
				    + "'"+param_row_id+"',"
				    //+ "(select r.row_id from raport_konfiguracja_aktualna.mml_rnc_parametry r where r.komenda_row_id='"+komenda_row_id+"' and r.nazwa='"+Pname+"' LIMIT 1 ),"
				    + ""+paramVal_token+","
				    + ""+param_dict_id+","
				    + "'"+n+"',"
				    + ""+obecnyDzienCzas+""
				    + ")";
                                      if(pierwszyN)
                                      {  komendaNUcell=komendaNUcell+insertNodebParam;
                                        pierwszyN=false;
                                      }
                                      else
                                          komendaNUcell=komendaNUcell+",\r\n"+insertNodebParam;
                                      
				}
				else
				{
				    /* insertNodebParam="INSERT INTO raport_konfiguracja_aktualna.mml_rnc_Ucell_param(cell_row_id,rnc_id, nodeb_row_id  ,param_ident,komm_ident,value,value_dictionary_id,`line_id`,last_update) VALUES("
				    +"'"+ucell_ident+"',"
				    +"'"+rncId+"',"
				    + "'"+nodeb_ident+"',"
				    + "'"+param_row_id+"',"
				    //+ "(select r.row_id from raport_konfiguracja_aktualna.mml_rnc_parametry r where r.komenda_row_id='"+komenda_row_id+"' and r.nazwa='"+Pname+"' LIMIT 1 ),"
				    + "'"+komenda_row_id+"',"
				    + ""+paramVal_token+","
				    + ""+param_dict_id+","
				    + "'"+n+"',"
				    + ""+obecnyDzienCzas+""
				    + ")";*/
                                    // insertNodebParam="INSERT INTO raport_konfiguracja_aktualna.mml_rnc_Ucell_param(cell_row_id,param_ident,value,value_dictionary_id,`line_id`,last_update) VALUES("+
				     insertNodebParam="\t("+
                                     ""+ucell_ident+", "
                                    + ""+param_row_id+", "
				    //+ "(select r.row_id from raport_konfiguracja_aktualna.mml_rnc_parametry r where r.komenda_row_id='"+komenda_row_id+"' and r.nazwa='"+Pname+"' LIMIT 1 ),"
                                    + ""+paramVal_token+", "
				    + ""+param_dict_id+", "
				    
				    + ""+obecnyDzienCzas+", "
                                             + ""+n+""
				    + ")";
                                    if(pierwszyU)
                                    {
                                        komendaUcell=komendaUcell+insertNodebParam;
                                       // nn.dopiszStream(insertNodebParam);
                                        pierwszyU=false;
                                      }
                                      else
                                          komendaUcell=komendaUcell+",\r\n"+insertNodebParam;
                                                                  
                                     // nn.dopiszStream("INSERT IGNORE INTO `mml_rnc_Ucell_param` (`cell_row_id`, `param_ident`, `value`, `value_dictionary_id`, `last_update`, `line_id`) VALUES\n");
				}
			    // System.out.println(insertNodebParam);
			    //testStatement.addBatch(insertNodebParam);
                                }
                            
			    //this.sprzF.dopisz(insertNodebParam+"\r\n");
 
			    }
			    }
			    catch(Exception ee)
			    {
				 loger.throwing(this.getClass().toString(), "["+this.identyfikator+"] ERROR:"+ee.getMessage()+"INS="+insertNodebParam, ee);
			    }
                        }
                }
		catch(Exception ee)
		{
		   
		     loger.throwing(this.getClass().toString(), "["+this.identyfikator+"] ERROR:"+ee.getMessage(), ee);
		}
		
	    }
            
           try
           {
               //testStatement.executeBatch();            
                
               testStatement.executeUpdate(komendaUcell+";");
                          
               testStatement.executeUpdate(komendaNUcell+";");
              

           }
           catch(java.sql.BatchUpdateException e)
           {
               loger.throwing(this.getClass().toString(), "["+this.identyfikator+"] ERROR:"+e.getMessage(), e);
           }
           catch(java.sql.SQLException e)
           {
               //System.out.println(komendaUcell+";");
                 // System.out.println(komendaNUcell+";");
               loger.throwing(this.getClass().toString(), "["+this.identyfikator+"] ERROR:"+e.getMessage()+"\r\n["+this.identyfikator+"]"+komendaUcell+";\r\n["+this.identyfikator+"]"+komendaNUcell, e);
               
           }
           finally
           {
               // testStatement.clearBatch();
           }
            
            
	//this.connection.commit();
	return true;
    }

    public boolean param_komm_ids()
    {
	try{
	String Komreq="select k.nazwa as komenda_nazwa, k.typ_kom,p.komenda_row_id,p.row_id, p.nazwa as parametr_nazwa,k.use_in_update   from raport_konfiguracja_aktualna.mml_rnc_komendy k left join raport_konfiguracja_aktualna.mml_rnc_parametry p on( p.komenda_row_id=k.row_id ) where k.typ="+typ+"";
	   
	    OdpowiedzSQL KomSql=selectSql(Komreq);
	    for(int z=0;z<KomSql.rowCount();z++)
	    {
		String kom_row_id=KomSql.getValue("komenda_row_id", z);
		String param_row_id=KomSql.getValue("row_id", z);
		String param_name=KomSql.getValue("parametr_nazwa", z);
		String kom_name=KomSql.getValue("komenda_nazwa", z);
		String kom_typ=KomSql.getValue("typ_kom", z);
		if(!komedny_row_id.containsKey(kom_typ+";"+kom_name))
		   komedny_row_id.put(kom_typ+";"+kom_name, kom_row_id) ;
                
		if(!parametry_row_id.containsKey(kom_typ+";"+kom_name+";"+param_name))
		   parametry_row_id.put(kom_typ+";"+kom_name+";"+param_name, kom_row_id+";"+param_row_id) ;
		if(KomSql.getValue("use_in_update", z).equals("1"))
                {
                    komendsToUpdate.add(kom_typ+";"+kom_name);
                }

		//komedny_row_id
		//parametry_row_id
	    }
	}
	catch(Exception ee)
	{
	    //ee.printStackTrace();
            loger.throwing(this.getClass().toString(), "["+this.identyfikator+"] ERROR:", ee);
	}
	return true;
    }
    public boolean get_nodeb_row_id()
    {
	try{
	String Komreq="select r.cell_id,n.row_id from raport_konfiguracja_aktualna.mml_rnc_Ucell_ident r left join raport_konfiguracja_aktualna.mml_rnc_Nodeb_ident n on(r.nodeb_row_id=n.row_id) where r.rnc_id='"+rncId+"';";
	  
	    OdpowiedzSQL KomSql=selectSql(Komreq);

	    for(int z=0;z<KomSql.rowCount();z++)
	    {
		String Cell_id=KomSql.getValue("cell_id", z);
		String nodeb_row_id=KomSql.getValue("row_id", z);
                if(nodeb_row_id!=null&&!nodeb_row_id.equals("")&&!nodeb_row_id.equalsIgnoreCase("NULL"))
		if(!nodeb_row_ids.containsKey(rncId+";"+Cell_id))
		    nodeb_row_ids.put(rncId+";"+Cell_id, nodeb_row_id);



		//komedny_row_id
		//parametry_row_id
	    }
	}
	catch(Exception ee)
	{
	   // ee.printStackTrace();
            loger.throwing(this.getClass().toString(), "["+this.identyfikator+"] ERROR:", ee);
	}
	return true;
    }

    private OdpowiedzSQL selectSql(String query) throws SQLException
    {
	     ResultSet resS=testStatement.executeQuery(query);

	     OdpowiedzSQL odp=Baza.createAnswer(resS);
	     resS.close();
	     return odp;
     }

    private  boolean getCellKomends()
    {
	System.out.println("START PARSING MML COMMANDS["+this.identyfikator+"]");
	this.komendyUcell=new HashMap<String,java.util.ArrayList<nbipackage.MmlKommand>>();
	 java.util.ArrayList<String> cellLines=super.mml.getLiniaList("CELLID=");

	 int wys=0;
	 while(cellLines.size()>0)
	 {
	    wys++;

	    if(wys==1000)
	    {
		     System.out.println(""+this.identyfikator+" "+cellLines.size());
		     wys=0;
	    }
            
		nbipackage.MmlKommand mmlKom=new nbipackage.MmlKommand(cellLines.get(cellLines.size()-1));
                cellLines.remove(cellLines.size()-1);
                
		//ADD UCELLSETUP:CELLID=23824, CELLNAME="22382411_CHR1007D_O_SLASKA_64A", LOCELL=22382411,
				//BANDIND=Band1, UARFCNUPLINKIND=TRUE, UARFCNUPLINK=9886, UARFCNDOWNLINK=10836, CFGRACIND=REQUIRE,
				//RAC=H'15, CNOPGRPINDEX=0, MAXTXPOWER=430, TCELL=CHIP0, NINSYNCIND=5, NOUTSYNCIND=50, TRLFAILURE=50,
				//DLTPCPATTERN01COUNT=10, PSCRAMBCODE=144, TXDIVERSITYIND=FALSE, SPGID=1, LAC=H'00CF, SAC=H'5D10, CIO=0,
				//NODEBNAME="223824_CHR1007D_SLASKA_64A", SRN=0, SN=16, SSN=3, VPLIMITIND=FALSE, DSSFLAG=FALSE,
				//DSSSMALLCOVMAXTXPOWER=430, CCHCNOPINDEX=255, DPGID=255, CELLHETFLAG=FALSE;

		String CellID=mmlKom.getValue("CELLID");
		if(CellID!=null)
		{
                    if(this.komendyUcell.containsKey(CellID))
                    {
                        //System.out.println("CELLID="+CellID+" KOMEND AKTUALNIE="+this.komendyUcell.get(CellID).size());
                        this.komendyUcell.get(CellID).add(mmlKom);
                    }
                    else
                    { 
                        java.util.ArrayList<nbipackage.MmlKommand> cellKomendsLst=new java.util.ArrayList<nbipackage.MmlKommand>();
                        cellKomendsLst.add(mmlKom);
                        this.komendyUcell.put(CellID, cellKomendsLst);
                        
                    }
                }
                

		
	}
	 System.out.println("END PARSING MML COMMANDS["+this.identyfikator+"]");
	return true;
    }
}
