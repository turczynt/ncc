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
public class NodeB_Type_perRnc_Updater extends Updater_parrent	//extends Thread{
{

    
    public NodeB_Type_perRnc_Updater(String identyfikator,String rncName,int typOperacji,Logger loger,mysqlpackage.DataSource DOA,NewFile sprzF)
    {
	super(identyfikator,rncName,typOperacji,loger,DOA,sprzF);
	
	//this.DOA=DOA;
    }

 
    @Override
    public boolean add() throws java.sql.SQLException
    {
	String obecnyDzienCzas="'"+super.sdf.format(super.DataDzisiaj)+"'";
	try
	{	    
	    this.errorInfo=this.errorInfo+";"+"DOA AND STATEMENT CONNECTED";
	    String req="select r.Rnc_Bsc_Index, r.Rnc_Bsc_Id, r.Rnc_Bsc_Name, m.M2000_Ip, r.M2000_Index from oncall.konfiguracja_aktualna_rnc_bsc r,  oncall.konfiguracja_aktualna_m2000 m where(r.m2000_index=m.M2000_Index and r.Rnc_Bsc_Name like '%"+this.kontrolerName+"%') LIMIT 1";
	    ResultSet res=testStatement.executeQuery(req);
	    OdpowiedzSQL rnc=Baza.createAnswer(res);
	    this.errorInfo=this.errorInfo+";"+"RNC M2000 data got";
	    //System.out.println(rnc);

	    //System.out.println(rnc.toString()+"\r\n\r\n");
	    int fine=0;
	    if(rnc.rowCount()>0)
	    {
		java.util.Hashtable<String,Paczka> NBfromNorth=new java.util.Hashtable<String, Paczka>();
		String m2000_ip=rnc.getValue("M2000_Ip", 0);
		String rnc_name=rnc.getValue("Rnc_Bsc_Name", 0);
		String rnc_id=rnc.getValue("Rnc_Bsc_Id",0);
		String rnc_index=rnc.getValue("Rnc_Bsc_Index", 0);
		String m2000_Index=rnc.getValue("M2000_Index", 0);
		north=new nbipackage.NorthB(m2000_ip, "U-boot", "utranek098",null);
		this.errorInfo=this.errorInfo+";"+"NorthB Connection initiated";
		String NE_LST=north.make2("LST NE:");
		if(NE_LST.contains("Login or Register needed"))
		{
		    NE_LST=north.make2("LST NEBYOMC:");
		}
		this.errorInfo=this.errorInfo+";"+"LST NE GETED";
		String[] inNE=NE_LST.split("\n");
		this.errorInfo=this.errorInfo+";"+"LST NE SPLITED";
		
		//Hashtable<String,String> nePerSite=new Hashtable<String,String>();

		Hashtable<String,String> Ne_Name_per_Ip=new Hashtable<String,String>();
		for(int w=0;w<inNE.length;w++)
		{

		    /*if(inNE[w].contains("BTS3900NE"))
		    {
			String ne_name=NewFile.getTokens(inNE[w], "2", "2");
			String site=NewFile.getTokens(ne_name, "2", "2","_");
		
			nePerSite.put(site, ne_name);
		    }*/
		    String ne_name=NewFile.getTokens(inNE[w], "2", "2").trim();
		    String Ip=NewFile.getTokens(inNE[w], "3", "3").trim();
		    Ne_Name_per_Ip.put(Ip, ne_name);

		}
		this.errorInfo=this.errorInfo+";"+"HASH MAP NE_NAME_PER_IP CREATED";
		
		String Unodeb_ip_LST=north.make(this.kontrolerName,"LST UNODEBIP:");
		this.errorInfo=this.errorInfo+";"+"LST UNODEBIP ANSW GOT";
		NPack nnP=new NPack(Unodeb_ip_LST);

		java.util.ArrayList<Paczka> unodeIps=nnP.getAllPacks();
		//Hashtable<String,String> nePerSite=new Hashtable<String,String>();
		this.errorInfo=this.errorInfo+";"+"LST UNODEBIP --> list";
		Hashtable<String,String> Ip_per_Id=new Hashtable<String,String>();
		for(int w=0;w<unodeIps.size();w++)
		{
		    
		    /*if(inNE[w].contains("BTS3900NE"))
		    {
			String ne_name=NewFile.getTokens(inNE[w], "2", "2");
			String site=NewFile.getTokens(ne_name, "2", "2","_");
		
			nePerSite.put(site, ne_name);
		    }*/
		    String ne_Id=unodeIps.get(w).getWartosc("NodeB ID").trim();
		    String Ip=unodeIps.get(w).getWartosc("NodeB IP_TRANS IP address").trim();
		    Ip_per_Id.put(ne_Id, Ip);

		}

		this.errorInfo=this.errorInfo+";"+"LST UNODEBIP list--> HashMap";

		String inNodeb=north.make(this.kontrolerName, "LST UNODEB:LSTFORMAT=HORIZONTAL;");
		//System.out.println("NORTH OUT::P:"+this.kontrolerName+" LST UNODEB:LSTFORMAT=HORIZONTAL; " +inNodeb);
		this.errorInfo=this.errorInfo+";"+"LST UNODEB ANSW GOT";
		if(inNodeb==null||!inNodeb.contains("RETCODE = 0"))
		{
		    System.err.println(this.identyfikator+" BLAD POBIERANIA DANYCH"+inNodeb);
		    sukces=false;
		    this.errorInfo=this.errorInfo+";"+"THROW BLEDNA ODP NORTH";
    		    throw new java.sql.SQLWarning(this.identyfikator+" BLEDNA ODP NORTHB:\r\n"+inNodeb);
		}
		else
		{
		    //System.out.println(inNodeb);
		    NPack nn=new NPack(inNodeb);
		    java.util.ArrayList<Paczka> listaNodow=nn.getAllPacks();
		    java.util.ArrayList<String> idikiNod=new java.util.ArrayList<String>();
		    this.errorInfo=this.errorInfo+";"+"LST NODEB TO list and start iteration";
		    for(int n=0;n<listaNodow.size();n++)
		    {
			String nodebName="";
			try
			{
			    nodebName=listaNodow.get(n).getWartosc("NodeB Name");
			    String nodebId=listaNodow.get(n).getWartosc("NODEB ID");
			    String nodebIndex=rnc_index+"|"+nodebId;
			    if(Ip_per_Id.containsKey(nodebId))
			    {
				String IP=Ip_per_Id.get(nodebId);
				//System.out.println("\t\t nodeIP="+IP);
				if(Ne_Name_per_Ip.containsKey(IP))
				{
				    if(!nodebName.equalsIgnoreCase(Ne_Name_per_Ip.get(IP).trim()))
					System.out.println(nodebName+" ---->"+Ne_Name_per_Ip.get(IP).trim());
				    nodebName=Ne_Name_per_Ip.get(IP).trim();
				}
			    }
			    String siteNB=NewFile.getTokens(nodebName, "2", "2","_");
			    if(!nodebName.equals(""))
			    {
				String softTyp=north.make(nodebName, "LST SOFTWARE:");
				String softVer="";
				if(softTyp.contains("RETCODE = 0"))
				{
				    NPack st=new NPack(softTyp);
				    java.util.ArrayList<Paczka> softy=st.getAllPacks();
				    for(int w=0;w<softy.size();w++)
				    {
					String stArea=softy.get(w).getWartosc("Storage Area");
					if(stArea.contains("Main Area"))
					    softVer=softy.get(w).getWartosc("Software Version");
				    }
				    if(softVer.equals(""))
				    {
					String line=NewFile.getFirstLine(new String[]{"Main Area"}, softTyp);
					if(line!=null&&!line.equals(""))
					{
					    softVer=NewFile.getTokens(line, "3", "3");
					}
				    }
				}
				else
				{
				}
				if(softVer.equals(""))
				    softVer=NewFile.getFirstLine(new String[]{"RETCODE"}, softTyp);
				if(softVer.length()>40)
				    softVer=softVer.substring(0, 39);
				if(softVer.equals(""))
				    softVer=softTyp;
				if(softVer.length()>40)
				    softVer=softVer.substring(0, 39);
				if(softVer.equals(""))
				{
				    softVer = "#NOT_CONNECTED";
				    String connTest=north.make2("REG NE:NAME=\""+nodebName+"\"");
				    if(connTest.contains("NE does not Connection"))
					softVer="NE_NOT_CONNECTED";
				    if(connTest.contains("Can't Found NE"))
					softVer="NE_NOT_EXIST";
				}
				String region="";
				region=nodebName.substring(0, 1);
				if(!region.matches("[1-4]"))
				{
				    if(rnc_name.contains("WAW"))
					region="1";
				    else if(rnc_name.contains("KAT"))
					region="2";
				    else if(rnc_name.contains("POZ"))
					region="3";
				    else if(rnc_name.contains("GDA"))
					region="4";
				}
				if(nodebId!=null&&!nodebId.equals(""))
				{
				    String query="";
				    if(softVer.contains("3900")||softVer.contains("3800")||softVer.contains("3812"))
					query="insert into oncall.konfiguracja_aktualna_NodebType(NodebName, NodebType, Last_update,Ne_index,Region,Status) VALUES('"+nodebName+"','"+softVer+"',"+obecnyDzienCzas+",'"+nodebIndex+"',"+region+",'NEW')ON DUPLICATE KEY UPDATE Last_Update="+obecnyDzienCzas+",NodebName='"+nodebName+"', NodebType='"+softVer+"', Ne_index='"+nodebIndex+"',Status='OK'";
				    else
					query="insert into oncall.konfiguracja_aktualna_NodebType(NodebName, NodebType, Last_update,Ne_index,Region,Status) VALUES('"+nodebName+"','"+softVer+"',"+obecnyDzienCzas+",'"+nodebIndex+"',"+region+",'NEW')ON DUPLICATE KEY UPDATE Last_Update="+obecnyDzienCzas+",NodebName='"+nodebName+"', Status='"+softVer+"', Ne_index='"+nodebIndex+"'";
				    //queryToExecute="UPDATE  oncall.konfiguracja_aktualna_nodeb SET Last_Update="+obecnyDzienCzas+", Update_Status='"+new_update_status+"',Status_ready_for_coa="+new_ready_for_coa+",U2100="+u2100+",U900="+u900+",U900_readyFor_coa="+u900Coa+",U2100_readyFor_coa="+u2100Coa+",NodebType='"+nodebType+"',Nodeb_Name='"+nodebName+"' WHERE Nodeb_Index='"+befor_index+"';";
				    testStatement.execute(query);
				}
				fine++;
			    }
			    
			}
			catch(NBIAnsException nb)
			{
			    try
			    {
				north.closeBuffor();
				north=new nbipackage.NorthB(m2000_ip, "U-boot", "utranek098",null);
			    }
			    catch(Exception ee)
			    {

			    }
			}
			catch(Exception ee)
			{
			    ee.printStackTrace();
			    loger.throwing(nodebName, "["+nodebName+"]ERROR:"+ee.getMessage(), ee);
			    this.errorInfo=this.errorInfo+";WYWALILO NA "+nodebName;
			}

		    }
		    this.errorInfo=this.errorInfo+";"+"NODE END ITERATION";
		    this.answer=" POPRAWNIE ZAKTUALIZOWANYCH NODEB= "+fine+" ("+listaNodow.size()+") ";
		    sukces=true;
		}
		
	    }
	    else
	    {
		loger.info("NIE ZNALEZIONO RNC o nazwie pasujacej do wzorca: "+this.kontrolerName);
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

  
}