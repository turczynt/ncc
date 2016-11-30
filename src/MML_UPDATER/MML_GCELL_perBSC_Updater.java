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
public class MML_GCELL_perBSC_Updater extends MML_Updater_parrent
{

    public MML_GCELL_perBSC_Updater(String identyfikator, String rncName, Logger loger, DataSource DOA,String mmlDir, NewFile sprzF)
    {
	super(identyfikator,rncName,loger,DOA,mmlDir,sprzF);
    }
    String rncId;
    HashMap<String,String> komedny_row_id;
    HashMap<String,String> parametry_row_id;
    HashMap<String,String> cellid_Btsid;  //key Cell_Id  value BtsId
    HashMap<String,String> cellid_Btsid_GcellrowId;//key Cell_Id;BtsId  gcellRowId
    HashMap<String,java.util.ArrayList<String>> komendyUcell;
   
    String typ="2";
    java.util.ArrayList<String>komendsToUpdate;
    //NewFile nn;
    
    private HashMap<String, String> joinBtsCellIds(String[] cellLines, String[] bindLines)
    {
        
        if (cellLines != null && bindLines != null)
        {
            HashMap<String, String> temp=new HashMap<String, String> ();
            for (int z = 0; z < cellLines.length; z++)
            {
                String cellId = null;
                String btsId = null;
                nbipackage.MmlKommand mmlKom = new nbipackage.MmlKommand(cellLines[z]);
                cellId = mmlKom.getValue("CELLID");
                if (cellId != null)
                {
                    for (int b = 0; b < bindLines.length && btsId == null; b++)
                    {
                        nbipackage.MmlKommand mmlBindKom = new nbipackage.MmlKommand(bindLines[b]);
                        String cellIdFromBind = mmlBindKom.getValue("CELLID");
                        if (cellIdFromBind.equals(cellId))
                        {
                            btsId=mmlBindKom.getValue("BTSID");
                            temp.put(cellId, btsId);
                        }
                    }
                }
            }
            return temp;
        }
        return null;
    }
    private HashMap<String,String> getGcell_row_ids() throws SQLException
    {
        HashMap<String, String> temp=new HashMap<String, String> ();
       
        String selectAll="select g.row_id as gcell_rowId, b.row_id as bts_rowId,g.gcell_id,b.bts_id from raport_konfiguracja_aktualna.mml_bsc_gcell_ident g left join raport_konfiguracja_aktualna.mml_bsc_bts_ident b on (g.bts_row_id=b.row_id) where b.bsc_id='"+this.rncId+"';";
         OdpowiedzSQL Idiki=Baza.createAnswer(testStatement.executeQuery(selectAll));
           
         for(int a=0;Idiki!=null&&a<Idiki.rowCount();a++)
         {
                String gcellRowId=Idiki.getValue("gcell_rowId", a);
                String cellId=Idiki.getValue("gcell_id", a);
                String btsId=Idiki.getValue("bts_id", a);
                
                temp.put(cellId+";"+btsId, gcellRowId);
         }
       
        return temp;
    }
    
    private HashMap<String,java.util.ArrayList<String>> sortToHashByCellId(java.util.ArrayList<String> source)
    {
        HashMap<String,java.util.ArrayList<String>>  tmp=new HashMap<String,java.util.ArrayList<String>> ();
        for(int z=0;z<source.size();z++)
        {
             nbipackage.MmlKommand mmlKom=new nbipackage.MmlKommand(source.get(z));
                String CellID=mmlKom.getValue("CELLID");
               java.util.ArrayList<String> lista;
               if(tmp.containsKey(CellID))
               {
                   lista=tmp.get(CellID);
               }
               else
                   lista=new java.util.ArrayList<String>();
               lista.add(source.get(z));
               tmp.put(CellID, lista);
        }
        return tmp;
    }
    
    private void setKomendy_parametry_ids() throws SQLException
    {
        String selectKomParam="select p.row_id as parametr_id, k.row_id,k.nazwa as nazwa_komenda,p.nazwa as nazwa_parametr from raport_konfiguracja_aktualna.mml_bsc_parametry p right join raport_konfiguracja_aktualna.mml_bsc_komendy k on (p.komenda_row_id=k.row_id) where k.typ=2;";
        komedny_row_id=new HashMap<String,String>();
        parametry_row_id=new HashMap<String,String>();
	OdpowiedzSQL Idiki=Baza.createAnswer(testStatement.executeQuery(selectKomParam));
	for(int a=0;a<Idiki.rowCount();a++)		  
        {
            String comm_name=Idiki.getValue("nazwa_komenda", a);
            String param_name=Idiki.getValue("nazwa_parametr", a);
            String comm_index=Idiki.getValue("komenda_row_id", a);
            String param_index=Idiki.getValue("parametr_id", a);
            if(!komedny_row_id.containsKey(comm_name))
                komedny_row_id.put(comm_name, comm_index);
            if(!parametry_row_id.containsKey(comm_name+";"+param_name))
            {
                parametry_row_id.put(comm_name+";"+param_name, param_index);
            }
        }
    }
    
    private void setKomendy_parametry_ids(String comm_name, String param_name, String typ_kom) throws SQLException
    {
        /* String selectKomParam="select p.row_id as parametr_id, p.komenda_row_id,k.nazwa as nazwa_komenda,p.nazwa as nazwa_parametr from raport_konfiguracja_aktualna.mml_bsc_parametry p left join raport_konfiguracja_aktualna.mml_bsc_komendy k on (p.komenda_row_id=k.row_id) where k.typ=2;";
         komedny_row_id=new HashMap<String,String>();
         parametry_row_id=new HashMap<String,String>();
         OdpowiedzSQL Idiki=Baza.createAnswer(testStatement.executeQuery(selectKomParam));
         for(int a=0;a<Idiki.rowCount();a++)		  
         {
         String comm_name=Idiki.getValue("nazwa_komenda", a);
         String param_name=Idiki.getValue("nazwa_parametr", a);
         String comm_index=Idiki.getValue("komenda_row_id", a);
         String param_index=Idiki.getValue("parametr_id", a);
         if(!komedny_row_id.containsKey(comm_name))
         komedny_row_id.put(comm_name, comm_index);
         if(!parametry_row_id.containsKey(comm_name+";"+param_name))
         {
         parametry_row_id.put(comm_name+";"+param_name, param_index);
         }
         }*/

          loger.log(Level.INFO,  "["+this.identyfikator+"] NOWY PARAMETR:"+comm_name+" "+param_name);
        
        if (!parametry_row_id.containsKey(comm_name + ";" + param_name))
        {
         
            if (!komedny_row_id.containsKey(comm_name))
            {
                String insert = "insert into raport_konfiguracja_aktualna.mml_bsc_komendy(typ,nazwa,typ_kom,create_date) values ('2','" + comm_name + "','" + typ_kom + "',now())";
                testStatement.executeUpdate(insert);
                String selectKomParam = "select k.row_id,k.nazwa as nazwa_komenda from raport_konfiguracja_aktualna.mml_bsc_komendy k where k.typ=2  and k.typ_kom='" + typ_kom + "' and k.nazwa='" + comm_name + "';";
            OdpowiedzSQL Idiki = Baza.createAnswer(testStatement.executeQuery(selectKomParam));
            for (int a = 0; a < Idiki.rowCount(); a++)
            {
                //String comm_name=Idiki.getValue("nazwa_komenda", a);
                //String param_name=Idiki.getValue("nazwa_parametr", a);
                String comm_index = Idiki.getValue("komenda_row_id", a);
               
               
                if (!komedny_row_id.containsKey(comm_name))
                    komedny_row_id.put(comm_name, comm_index);
            }
             
               
            }
            if (!parametry_row_id.containsKey(comm_name + ";" + param_name))
            {
                String insert = "insert into raport_konfiguracja_aktualna.mml_bsc_parametry(komenda_row_id,nazwa,create_date) values( (  select row_id from raport_konfiguracja_aktualna.mml_bsc_komendy where typ=2 and typ_kom='" + typ_kom + "' and nazwa='" + comm_name + "' ),'" + param_name + "',now())";
                testStatement.executeUpdate(insert);
            }
            //parametry_row_id=new HashMap<String,String>();
            String selectKomParam = "select p.row_id as parametr_id, k.row_id,k.nazwa as nazwa_komenda,p.nazwa as nazwa_parametr from raport_konfiguracja_aktualna.mml_bsc_parametry p left join raport_konfiguracja_aktualna.mml_bsc_komendy k on (p.komenda_row_id=k.row_id) where k.typ=2  and k.typ_kom='" + typ_kom + "' and k.nazwa='" + comm_name + "' and p.nazwa='" + param_name + "' ;";
            OdpowiedzSQL Idiki = Baza.createAnswer(testStatement.executeQuery(selectKomParam));
            for (int a = 0; a < Idiki.rowCount(); a++)
            {
                //String comm_name=Idiki.getValue("nazwa_komenda", a);
                //String param_name=Idiki.getValue("nazwa_parametr", a);
                String comm_index = Idiki.getValue("komenda_row_id", a);
                String param_index = Idiki.getValue("parametr_id", a);
               
                if (!parametry_row_id.containsKey(comm_name + ";" + param_name))
                {
                    parametry_row_id.put(comm_name + ";" + param_name, param_index);
                }
            }
            
        }
    }
            
    public boolean add() throws java.sql.SQLException
    {
	boolean udane=false;
	String obecnyDzienCzas="'"+sdf.format(DataDzisiaj)+"'";
         komendsToUpdate=new java.util.ArrayList<String>();
//        nn=new NewFile(this.mmlDirPath+"/GCELL_INSERT_"+this.kontrolerName+".sql");
	try
	{
	    //komedny_row_id=new java.util.Hashtable<String, String>();
	    //parametry_row_id=new java.util.Hashtable<String, String>();
//	    nn.openStream();
	  setKomendy_parametry_ids();


	    String req="select r.Rnc_Bsc_Id from oncall.konfiguracja_aktualna_rnc_bsc r where r.Rnc_Bsc_Name ='"+this.kontrolerName+"' LIMIT 1";
	    System.out.println(req);
	    
	    OdpowiedzSQL rnc= selectSql(req);
	    this.rncId=rnc.getValue("Rnc_Bsc_Id", 0);
	


	    String[] cellLines=super.mml.getLinia("ADD GCELL:");
            loger.log(Level.INFO,  "["+this.identyfikator+"] GET CELL LINE FROM MML:"+cellLines.length);
            
            
            
            
            String[] bindcellToBtsLines=super.mml.getLinia("ADD CELLBIND2BTS:");
               loger.log(Level.INFO,  "["+this.identyfikator+"] GET ADD CELLBIND2BTS:");
               
            cellid_Btsid=this.joinBtsCellIds(cellLines, bindcellToBtsLines);
              loger.log(Level.INFO,  "["+this.identyfikator+"] JOIN BTSID WITH GCELL ID");
              
            for ( int n=0;n<cellLines.length;n++)
	    {
               
                nbipackage.MmlKommand mmlKom=new nbipackage.MmlKommand(cellLines[n]);
                String CellID=mmlKom.getValue("CELLID");
                String CellName=mmlKom.getValue("CELLNAME");
                String btsId=cellid_Btsid.get(CellID);
                
                String query="INSERT INTO `raport_konfiguracja_aktualna`.`mml_bsc_gcell_ident` (`gcell_id`, `bsc_id`, `name`,`bts_row_id`, `create_date`, `last_update`) VALUES ("+CellID+",'"+rncId+"', '"+CellName+"',(select row_id from raport_konfiguracja_aktualna.mml_bsc_bts_ident where bts_id='"+btsId+"' and bsc_id='"+this.rncId+"' LIMIT 1), "+obecnyDzienCzas+", "+obecnyDzienCzas+") ON DUPLICATE KEY UPDATE last_update="+obecnyDzienCzas+",name='"+CellName+"'";
                 try{
                testStatement.executeUpdate(query);
                loger.log(Level.INFO,  "["+this.identyfikator+"] dodawania identyfikatorow gcell "+n+"/"+cellLines.length+" CELLNAME="+CellName);
                }
                catch(SQLException e)
                        {
                              loger.throwing(this.getClass().toString(), "["+this.identyfikator+"] ERROR przy dodawaniu identyfikatorow gcell "+n+"/"+cellLines.length+" CELLNAME="+CellName+"\r\n"+query, e);
                             loger.log(Level.INFO,  "["+this.identyfikator+"] dodawania identyfikatorow gcell "+n+"/"+cellLines.length+" CELLNAME="+CellName);
                        }
            }
           this.cellid_Btsid_GcellrowId=getGcell_row_ids();
              loger.log(Level.INFO,  "["+this.identyfikator+"] JOIN CELLID WITH GCELL_ROW_ID");
            java.util.ArrayList<String> linesWithGcell=super.mml.findIN("[\\s:]{1}CELLID=");
            
            this.komendyUcell=sortToHashByCellId(linesWithGcell);
	    for ( int n=0;n<cellLines.length;n++)
	    {
                String CellName=null;
               String insertNodebParam="";
               StringBuffer komendaGcell=new StringBuffer("INSERT IGNORE INTO raport_konfiguracja_aktualna.mml_bsc_Gcell_param (`gcell_row_id`, `param_ident`, `value`, `value_dictionary_id`, `last_update`, `line_id`) VALUES\r\n");
		try
                {
                   
                    nbipackage.MmlKommand mmlKom=new nbipackage.MmlKommand(cellLines[n]);
                    String CellID=mmlKom.getValue("CELLID");
                    CellName=mmlKom.getValue("CELLNAME");
                    
                    String btsId=cellid_Btsid.get(CellID);
                    
                     
                    java.util.ArrayList<String> cellParametrizations=komendyUcell.get(CellID);
                     loger.log(Level.INFO,  "["+this.identyfikator+"] "+n+"/"+cellLines.length+" CELLNAME="+CellName);
                    
                     
                    boolean pierwszyU=true;
		
                    
                    //System.out.println(""+n+"/"+cellLines.length+" CELLNAME="+CellName+" "+this.kontrolerName);
                       



                    if(CellID!=null&&!CellID.equals("")&&btsId!=null&&!btsId.equals(""))
                    {
                        //DODANIE IDENTYFIKATORA GCELL
                       
                        
                        
                        String gcellRowId="(select gcell_row_id from `raport_konfiguracja_aktualna`.`mml_bsc_gcell_ident` where bts_row_id=(select row_id from raport_konfiguracja_aktualna.mml_bsc_bts_ident where bts_id='"+btsId+"' and bsc_id='"+this.rncId+"' LIMIT 1) and bsc_id='"+this.rncId+"' and gcell_id="+CellID+" )";
                        if(this.cellid_Btsid_GcellrowId.containsKey(CellID+";"+btsId))
                            gcellRowId=this.cellid_Btsid_GcellrowId.get(CellID+";"+btsId);
                        
                        for(int z=0;z<cellParametrizations.size();z++)
                        {
                           nbipackage.MmlKommand mmlLine=new nbipackage.MmlKommand(cellParametrizations.get(z));
                           String comm_name=mmlLine.getCommandName();
                           String comm_type=mmlLine.getCommandType();
                           java.util.ArrayList<String> params=mmlLine.getParamNames();
                           
                           for(int p=0;p<params.size();p++)
                           {
                               String param_name=params.get(p);
                               if (!komedny_row_id.containsKey(comm_name) ||!parametry_row_id.containsKey(comm_name + ";" + param_name))
                               {  
                                   setKomendy_parametry_ids(comm_name,param_name,comm_type);
                               }
                               String comm_id=komedny_row_id.get(comm_name);
                               String param_id=parametry_row_id.get(comm_name + ";" + param_name);
                               String paramVal_token="'"+mmlLine.getValue(param_name)+"'";
                               String param_dict_id="null";
                               
                               
                               
                               insertNodebParam="\t("+
                                     ""+gcellRowId+", "
                                    + ""+param_id+", "
				    //+ "(select r.row_id from raport_konfiguracja_aktualna.mml_rnc_parametry r where r.komenda_row_id='"+komenda_row_id+"' and r.nazwa='"+Pname+"' LIMIT 1 ),"
                                    + ""+paramVal_token+", "
				    + ""+param_dict_id+", "
				    
				    + ""+obecnyDzienCzas+", "
                                             + ""+n+""
				    + ")";
                                    if(pierwszyU)
                                    {
                                        komendaGcell.append(insertNodebParam);
                                       // nn.dopiszStream(insertNodebParam);
                                        pierwszyU=false;
                                      }
                                      else
                                          komendaGcell.append(",\r\n"+insertNodebParam);
                               
                               
                                   
                           }
                                    // 
                        }
                        
                    }
                     testStatement.executeUpdate(komendaGcell.toString()+";");
		}
                catch(SQLException e)
                {
                     loger.throwing(this.getClass().toString(), "["+this.identyfikator+"] "+CellName+" ERROR:"+e.getMessage()+"\r\n"+komendaGcell, e);
                    super.testStatement.close();
                    super.connection.close();
                    super.connection=super.DOA.getConnection();
                    super.testStatement=super.connection.createStatement();
                   
                }
		catch(Exception ee)
		{
		    loger.throwing(this.getClass().toString(), "["+this.identyfikator+"] "+CellName+" ERROR:"+ee.getMessage(), ee);
                    
		}   
                //nn.dopiszStream(komendaGcell+";\r\n\r\n");
                        
                       
	    }
            
           
            udane=true;
        }
	catch(Exception ee)
	{
	     loger.throwing(this.getClass().toString(), "["+this.identyfikator+"] ERROR:"+ee.getMessage(), ee);
	}
        finally
        {
            //nn.closeStream();
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
    
   
  
    private OdpowiedzSQL selectSql(String query) throws SQLException
    {
	     ResultSet resS=testStatement.executeQuery(query);

	     OdpowiedzSQL odp=Baza.createAnswer(resS);
	     resS.close();
	     return odp;
     }

}
