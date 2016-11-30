/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NORTHB_UPDATER;

import java.io.IOException;
import nbipackage.NBIAnsException;
import nbipackage.NPack;
import nbipackage.NorthB;
import nbipackage.Paczka;

/**
 *
 * @author turczyt
 */
public class test
{

    /**
     * @param args the command line arguments
     */
    
    
    
    public static void main(String[] args)
    {
    
          java.util.Date DataDzisiaj=java.util.Calendar.getInstance().getTime();
           String DATE_FORMAT_NOW = "yyyy-MM-dd hh:mm:ss"; 
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT_NOW);
           String obecnyDzienCzas=sdf.format(DataDzisiaj);
           
    
    
    }
  /*  public static void main(String[] args) throws NBIAnsException, IOException
    {
				NorthB north=new nbipackage.NorthB("172.17.38.10", "U-boot", "utranek098",null);
                                String NE_Name="224937_STR2002A_KRAKOWSKA_71";
                                String neIndex="11|24937";
				String lst=north.make(NE_Name, "LST NODEBMULTICELLGRP:");
				System.err.println(lst);
				NPack npack=null;
				if(lst.contains("Number of results = 1)"))
				{
				    npack=new NPack(lst.split("\n"),new String[]{"LST NODEBMULTICELLGRP:"},new String[]{"----------"},new String[]{"---    END"});
				}
				else
				    npack=new NPack(lst);

				java.util.ArrayList<Paczka> mcgrLst=npack.getAllPacks();
                                if(lst.contains("RETCODE = 0"))
                                {
				for(int m=0;mcgrLst!=null&&m<mcgrLst.size();m++)
				{
				    String mcType=mcgrLst.get(m).getWartosc("Multiple Carrier Cell Group Type");
				    String mcGrNo=mcgrLst.get(m).getWartosc("Multiple Carrier Cell Group ID");
				    String lstMC=north.make(NE_Name, "LST NODEBMULTICELLGRPITEM:MULTICELLGRPID="+mcGrNo);
				    System.out.println(lstMC);
				    String locell1="";
				    String locell2="";
				    String locell3="";
				    NPack npackll2=new NPack(lstMC);
                                    
                                    if(lstMC.contains("Number of results = 1)"))
                                    {
                                        npackll2=new NPack(lstMC.split("\n"),new String[]{"LST NODEBMULTICELLGRPITEM:"},new String[]{"----------"},new String[]{"---    END"});
                                    }
                                    
                                    java.util.ArrayList<Paczka> mcgrLstS=npackll2.getAllPacks();
				    for(int c=0;mcgrLstS!=null&&c<mcgrLstS.size();c++)
				    {
					String locellNr= mcgrLstS.get(c).getWartosc("Local Cell ID");
					if(locellNr!=null&&locellNr.length()>0)
					{
					    if(c==0)
						locell1=locellNr;
					    if(c==1)
						locell2=locellNr;
					    if(c==2)
						locell3=locellNr;

					}
				    }
				    String gr_index=neIndex+"|"+mcGrNo;

				    if(locell1.equals(""))
					locell1="NULL";
				    if(locell2.equals(""))
					locell2="NULL";
				    if(locell3.equals(""))
					locell3="NULL";
				    String insert="insert into raport_konfiguracja_aktualna.nodeb_multicell_group (gr_index,nodeb_index,mc_gr_id,mc_gr_type,locell_1,locell_2,locell_3,Last_update,Update_Status) values ('"+gr_index+"','"+neIndex+"',"+mcGrNo+",'"+mcType+"',"+locell1+","+locell2+","+locell3+",OBECNYDZIENCZAS,'NEW')ON DUPLICATE KEY UPDATE Last_Update=OBECNYDZIENCZAS,Update_Status='OK', locell_1="+locell1+", locell_2="+locell2+", locell_3="+locell3;
				    System.out.println(insert);
				    //testStatement.execute(insert);

				}
                                }
			    }*/
    
}
