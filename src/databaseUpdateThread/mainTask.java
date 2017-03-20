/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseUpdateThread;


import NORTHB_UPDATER.Utran_external_perM2000_Updater;
import NORTHB_UPDATER.Utran_ncell_perM2000_Updater;
import NORTHB_UPDATER.NodebMultiCellGr_perRnc_Updater;
import NORTHB_UPDATER.UlteCell_perRnc_Updater;
import NORTHB_UPDATER.NodebRXATTEN_perRnc_Updater;  
import NORTHB_UPDATER.UcellAlgoSwitch_perRnc_Updater;
import NORTHB_UPDATER.Btsbindlocgr_perBsc_Updater;
import NORTHB_UPDATER.gtrxdev_perBsc_Updater;
import MML_UPDATER.MML_ADJNODE_perRNC_Updater;
import MML_UPDATER.MML_Bts_perBSC_Updater;
import MML_UPDATER.MML_GCELL_perBSC_Updater;
import MML_UPDATER.MML_GEXTLTECELL_perBSC_Updater;
import MML_UPDATER.MML_GLTENCELL_perBSC_Updater;
import MML_UPDATER.MML_Nodeb_perRNC_Updater;
import MML_UPDATER.MML_UCELL_perRNC_Updater;
import NORTHB_UPDATER.DevipNe_perM200_Updater;
import NORTHB_UPDATER.EthPort_perM2000_Updater;
import NORTHB_UPDATER.G2GNCELL_perBsc_Updater;
import NORTHB_UPDATER.IpClk_pt_perRnc_Updater;
import NORTHB_UPDATER.Locell_details_perRnc_Updater;
import NORTHB_UPDATER.NodebCMCTRL_perRnc_Updater;
import NORTHB_UPDATER.NodebIsdSwitch_perRnc_Updater;
import NORTHB_UPDATER.NodebMntMode_perRnc_Updater;
import NORTHB_UPDATER.NodebNTPC_perRnc_Updater;
import NORTHB_UPDATER.Nodeb_adaRetSwitch_perRnc_Updater;
import NORTHB_UPDATER.Nodeb_dlflowcontrolpara_perRnc_Updater;
import NORTHB_UPDATER.Nodeb_rscgrp_perRnc_Updater;
import NORTHB_UPDATER.PtpBvc_perBsc_Updater;
import NORTHB_UPDATER.Rruchain_perM2000_Updater;
import NORTHB_UPDATER.SectorEq_perM2000_Updater;
import NORTHB_UPDATER.btsEthport_perBsc_Updater;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import poolThread.UpdaterInterface;
import poolThread.GrupaZadan;
import poolThread.Pool;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import mysqlpackage.*;
import nbipackage.NewFile;
import java.util.logging.*;
import java.util.Locale;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import nbipackage.Emailer;

/**
 *
 * @author turczyt
 */
public class mainTask
{
    /**
     * @param args the command line arguments
     */
    static Pool pool;
    static Pool poolMML;
    static java.util.logging.Logger logger;
    public static void main(String[] args)
    {
	System.out.println("STARTTTTT");
        logger = Logger.getAnonymousLogger();
	logger.log(Level.FINEST, "###START####");
        //KATALOG DO TOOL'a
	String katalogPath="/usr/samba/utran/PP/Logs/NCC_DB/";
	String mmlDir="/usr/samba/utran/PP/TMP/Printouts/";
        
        //KATALOG LOKALNY/WINDOWS
        
       /*
        * 
        * String katalogPath="";
        * String mmlDir="C:\\Printouts\\";
	*/
        
        
        
        
	
        boolean wybraneModuly=false;
	boolean LTEUCELL=false;
	boolean MULTIGR=false;
	boolean BINDLOC=false;
	boolean gtrxdev=false;
	boolean RXATTEN=false;
        
	boolean MML_NODE=false;
	boolean MML_UCELL=false;
        boolean MML_GCELL=false;
	boolean MML_ANI=false;
        boolean MML_GEXTLTECELL=false;
        boolean MML_GLTENCELL=false;
        
	boolean UN_CELL_ON_ENODEB=false;
	boolean UEXTERNAL_ON_ENODEB=false;
	boolean ALGO_SWITCH_ON_RNC=false;
	boolean NTPC=false;
        boolean PTP_BVC_ON_BSC=false;
        boolean LODET=false;
        boolean ISD_SWITCH=false;
        boolean ADA_RE_SWITCH=false;
        boolean NE_STATUS=false;
        boolean CMCTRL_STATE=false;
        boolean IPCLK_PT=false;
        boolean FLOWCTRLPARA=false;
        boolean DEVIP=false;
        boolean ETHPORT=false;
        boolean ETHPORTBSC=false;
        boolean RRUCHAIN=false;
        boolean SEQTOR_EQ=false;
        boolean RSCGRP=false;
        boolean G2GNCELL=false;
   
        
        boolean MML_BTS=false;
        boolean ALL_MML=false;
        boolean ALL_NORTHB=false;
        
        
        String wzorzec="";
	if(args.length>0)
	{
	    if( args[0].equals("-h"))
	    {
                System.out.println("Dostepne parametry:");
		System.out.println("\t-h legenda");
		System.out.println("\t-moduly 'm1;m2;m3' ...[opcjonalnie region]\r\n\r\n");
		System.out.println("\tDostepne moduly:");
		System.out.println("\t\t LTEUCELL (dane z odp NorthB z komendy LST ULTECELL:)");
		System.out.println("\t\t MULTIGR (dane z odp NorthB z komendy LST NODEBMULTICELLGRP:)");
		System.out.println("\t\t BINDLOC (dane z odp NorthB z komendy LST BTSBINDLOCGRP:)");
		System.out.println("\t\t gtrxdev (dane z odp NorthB z komendy LST GTRXDEV:)");
		System.out.println("\t\t RXATTEN (dane z odp NorthB z komendy LST RXBRANCH:/LST RXATTEN:)");
		System.out.println("\t\t MML_NODE (parametryzacja nodeb z plikow MML'owych:komendy zawierajace param NodebId)");
		System.out.println("\t\t MML_ADJNODE (parametryzacja ADJNODE z plikow MML'owych:komendy zawierajace param ANI)");
		System.out.println("\t\t MML_UCELL (parametryzacja komorek z plikow MML'owych:komendy zawierajace param CellId)");
                System.out.println("\t\t MML_BTS (parametryzacja nodeb z plikow MML'owych:komendy zawierajace param BtsId)");
                System.out.println("\t\t MML_GCELL (parametryzacja komorek z plikow MML'owych:komendy zawierajace param GellId)");
		System.out.println("\t\t MML_GEXTLTECELL (parametryzacja externali 4G wykreowanych na BSC)");
                System.out.println("\t\t MML_GLTENCELL (parametryzacja relacji 2G--->4G)");
                System.out.println("\t\t UN_CELL_ON_ENODEB (parametryzacja relacje 4G--->3G )");
		System.out.println("\t\t UEXTERNAL_ON_ENODEB(parametryzacja externali 3G wykreowanych na EnodeB)");
		System.out.println("\t\t ALGO_SWITCH_ON_RNC (parametryzacja ucell_algoswitch na RNC)");
                System.out.println("\t\t PTP_BVC_ON_BSC (parametryzacja LST PTPBVC na BSC)");
		System.out.println("\t\t NTPC (parametryzacja NTPC na NODEB)");
                System.out.println("\t\t ISD_SWITCH (wartosc 'NodeB ISD Switch'/'Intelligent Shutdown Switch' po stronie NodeB)");
                System.out.println("\t\t ADA_RE_SWITCH(wartosc 'Adaptive Retransmission Control Switch'/'HSUPA AdaRetransSwitch' po stronie NodeB)");
                System.out.println("\t\t LODET (parametryzacja LOCELL(szczegoly) oraz LST ULOCELLMACHSPARA po stronie NodeB)");
                System.out.println("\t\t NE_STATUS (wartosc 'Ne Status'/'Maintenance Mode' po stronie NodeB)");
                System.out.println("\t\t CMCTRL_STATE (wartosc 'CM Control State' po stronie NodeB)");
                System.out.println("\t\t IPCLK_PT( wartosc 'Protocol Type' z LST IPCLKLNK/LST IPCLKLINK)");
                System.out.println("\t\t FLOWCTRLPARA(wartosc parametrow 'Hsdpa Switch'/'Flow Control Switch' oraz Time Delay(5ms) z komendy LST DLFLOWCTRLPARA/HSDPAFLOWCTRLPARA po stronie NodeB)");
                System.out.println("\t\t DEVIP(parametryzacja LST DEVIP: na kazdym L/U/UL)");
                System.out.println("\t\t ETHPORT(wybrane parametry z DSP ETHPORT: na kazdym NE: L/U/UL)");
                System.out.println("\t\t ETHPORTBSC(wybrane parametry z DSP BTSETHPORT: na kazdym BTS)");
                System.out.println("\t\t G2GNCELL(relacje 2G-->2G LST G2GNCELL:; na kazdym BSC)");
                System.out.println("\t\t RRUCHAIN(wybrane parametry z LST RRU/RRUCHAIN na kazdym NE: L/U/UL)");
                System.out.println("\t\t RSCGRP(parametryzacja LST RSCGRP: na kazdym U/UL");
                System.out.println("\t\t SEQTOR_EQ(parametryzacja LST SECTOR\\SECTOREQUIPMENT na kazdym NE");
                System.out.println("\t\t ALL_MML( wszystkie moduly pobierajace dane z *.mml)");
                System.out.println("\t\t ALL_NORTHB( wszystkie moduly pobierajace dane przy pomocy NorthB)");
 		System.exit(0);
	    }
	    else if(args[0].contains("moduly"))
	    {
		wybraneModuly=true;
		if(args.length>1)
		{
		    String[] modulTok=args[1].split("[;]");
		    for(int a=0;a<modulTok.length;a++)
		    {
			System.out.println("MODUL"+a+" ="+modulTok[a]);
			if(modulTok[a].equalsIgnoreCase("LTEUCELL"))
			    LTEUCELL=true;
			if(modulTok[a].equalsIgnoreCase("MULTIGR"))
			    MULTIGR=true;
			if(modulTok[a].equalsIgnoreCase("BINDLOC"))
			    BINDLOC=true;
			if(modulTok[a].equalsIgnoreCase("gtrxdev"))
			    gtrxdev=true;
			if(modulTok[a].equalsIgnoreCase("RXATTEN"))
			    RXATTEN=true;
			if(modulTok[a].equalsIgnoreCase("NTPC"))
			    NTPC=true;
			if(modulTok[a].equalsIgnoreCase("MML_NODE"))
			    MML_NODE=true;
                        if(modulTok[a].equalsIgnoreCase("MML_UCELL"))
			    MML_UCELL=true;
                        if(modulTok[a].equalsIgnoreCase("MML_ADJNODE"))
                        MML_ANI=true;
			if(modulTok[a].equalsIgnoreCase("MML_ANI"))
			    MML_ANI=true;
                        if(modulTok[a].equalsIgnoreCase("MML_BTS"))
                            MML_BTS=true;
                        if(modulTok[a].equalsIgnoreCase("MML_GCELL"))
			    MML_GCELL=true;
                        if(modulTok[a].equalsIgnoreCase("MML_GEXTLTECELL"))
                            MML_GEXTLTECELL=true;
                        if(modulTok[a].equalsIgnoreCase("MML_GLTENCELL"))
                            MML_GLTENCELL=true;
			if(modulTok[a].equalsIgnoreCase("UN_CELL_ON_ENODEB"))
			    UN_CELL_ON_ENODEB=true;
			if(modulTok[a].equalsIgnoreCase("UEXTERNAL_ON_ENODEB"))
			    UEXTERNAL_ON_ENODEB=true;
			if(modulTok[a].equalsIgnoreCase("ALGO_SWITCH_ON_RNC"))
			    ALGO_SWITCH_ON_RNC=true;
                        if(modulTok[a].equalsIgnoreCase("PTP_BVC_ON_BSC"))
                            PTP_BVC_ON_BSC=true;
                        if(modulTok[a].equalsIgnoreCase("LODET"))
                            LODET=true;                        
                        if(modulTok[a].equalsIgnoreCase("ISD_SWITCH"))
                            ISD_SWITCH=true;
                         if(modulTok[a].equalsIgnoreCase("ADA_RE_SWITCH"))
                            ADA_RE_SWITCH=true;
                        if(modulTok[a].equalsIgnoreCase("NE_STATUS"))
                            NE_STATUS=true;
                        if(modulTok[a].equalsIgnoreCase("CMCTRL_STATE"))
                            CMCTRL_STATE=true;
                        if(modulTok[a].equalsIgnoreCase("IPCLK_PT"))
                           IPCLK_PT=true;
                        if(modulTok[a].equalsIgnoreCase("FLOWCTRLPARA"))
                            FLOWCTRLPARA=true;
                        if(modulTok[a].equalsIgnoreCase("DEVIP"))
                            DEVIP=true;
                        
                        if(modulTok[a].equalsIgnoreCase("SEQTOR_EQ"))
                            SEQTOR_EQ=true;
                        if(modulTok[a].equalsIgnoreCase("ETHPORT"))
                            ETHPORT=true;
                        
                        if(modulTok[a].equalsIgnoreCase("ETHPORTBSC"))
                            ETHPORTBSC=true;
                        if(modulTok[a].equalsIgnoreCase("G2GNCELL"))
                            G2GNCELL=true;
                        
                        if(modulTok[a].equalsIgnoreCase("RRUCHAIN"))
                            RRUCHAIN=true;
                        if(modulTok[a].equalsIgnoreCase("RSCGRP"))
                            RSCGRP=true;
                        if(modulTok[a].equalsIgnoreCase("ALL_MML"))
                            ALL_MML=true;
                        if(modulTok[a].equalsIgnoreCase("ALL_NORTHB"))
                            ALL_NORTHB=true;
                        
		    }
                    if(ALL_MML)
                    {
                        MML_NODE=true;
                        MML_UCELL=true;
                        MML_GCELL=true;
                        MML_ANI=true;
                        MML_BTS=true;
                        MML_GEXTLTECELL=true;
                        MML_GLTENCELL=true;
                    }
                    if(ALL_NORTHB)
                    {
                        UN_CELL_ON_ENODEB=true;
                        UEXTERNAL_ON_ENODEB=true;
                        ALGO_SWITCH_ON_RNC=true;
                        NTPC=true;
                        PTP_BVC_ON_BSC=true;
                        LODET=true;
                        ISD_SWITCH=true;
                        ADA_RE_SWITCH=true;
                        NE_STATUS=true;
                        CMCTRL_STATE=true;
                        IPCLK_PT=true;
                        LTEUCELL=true;
                        MULTIGR=true;
                        BINDLOC=true;
                        gtrxdev=true;
                        RXATTEN=true;
                        FLOWCTRLPARA=true;
                        DEVIP=true;
                        ETHPORT=true;
                        ETHPORTBSC=true;
                        G2GNCELL=true;
                        RRUCHAIN=true;
                        RSCGRP=true;
                        SEQTOR_EQ=true;
                    }
                    if(args.length>2)
                    {
                        wzorzec=args[2];
                    }
                }
		else
		{
		    System.out.println("Brak modulow do wykonania. Przykladowe wywolanie:\r\n java -jar NCC_DB.jar -moduly 'LTEUCELL;MML_NODE'");
		    System.exit(0);
		}
	    }
	    else
	    {
		System.out.println("BLEDNE WYWOLANIE APLIKACJI:\r\n\r\n aby wyswietlic pomoc uruchom z parametrem -h");
		System.exit(0);
	    }
	}

        
	try
	{
           java.util.Date DataDzisiaj=java.util.Calendar.getInstance().getTime();
	   String DATE_FORMAT_NOW = "yyyy_MM_dd";
	   java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT_NOW);
	   String obecnyDzienCzas=sdf.format(DataDzisiaj);
	   NewFile sprzTEST=null;
	   sprzTEST=new NewFile(katalogPath+"SPRZ_NCC_DB_"+obecnyDzienCzas+".txt");
	    //NewFile sqlKom=new NewFile(katalogPath+"SQL_KOM_"+obecnyDzienCzas+".txt");
	   String loggerPath=katalogPath+"LOGS_NCC_DB_"+obecnyDzienCzas+".txt";
	   sprzTEST.dopisz("###################"+DataDzisiaj+"###################\r\n");
	   FileHandler fh= new FileHandler(loggerPath);

	   fh.setLevel(Level.ALL);
	   ConsoleHandler handler = new ConsoleHandler();
	   handler.setLevel(Level.ALL);

	   LogFormater formatter = new LogFormater();
	   fh.setFormatter(formatter);
	   handler.setFormatter(formatter);
	   logger.addHandler(fh);
	   logger.addHandler(handler);
	   logger.setLevel(Level.ALL);
	   //logger.info("START  WERSJA Z 25-03-2014");

           mysqlpackage.DataSource DOA=DataSource.getInstance();
           pool=new Pool(DOA,logger);
           poolMML=new Pool(DOA,logger);

           String reqNodebIdentDels="";
           Connection connection=null;
           Statement testStatement=null;
           OdpowiedzSQL RNCIDENT=null;
           OdpowiedzSQL BSCIDENT=null;
           try
           {
               connection = DOA.getConnection();
               testStatement = connection.createStatement();
               String req="select  r.Rnc_Bsc_Id, r.Rnc_Bsc_Name from oncall.konfiguracja_aktualna_rnc_bsc r where r.Rnc_Bsc_Name like '%"+wzorzec+"RNC%' ORDER BY r.hash_id";
               ResultSet res=testStatement.executeQuery(req);
               RNCIDENT=Baza.createAnswer(res);
               
               req="select  r.Rnc_Bsc_Id, r.Rnc_Bsc_Name from oncall.konfiguracja_aktualna_rnc_bsc r where r.Rnc_Bsc_Name like '%"+wzorzec+"BSC%' ORDER BY r.hash_id";
               res=testStatement.executeQuery(req);
               BSCIDENT=Baza.createAnswer(res);
               
               
               if(MML_GCELL||!wybraneModuly)
               {
                   String obecnyDzienCzasDel="'"+sdf.format(DataDzisiaj)+"'";
                   reqNodebIdentDels="truncate table raport_konfiguracja_aktualna.mml_bsc_Gcell_param;";
                   testStatement.executeUpdate(reqNodebIdentDels);
                   // testStatement.execute("ALTER TABLE konfiguracja_aktualna.mml_rnc_Ucell_param AUTO_INCREMENT = 1");
               }
               
               if(MML_UCELL||!wybraneModuly)
               {
                   String obecnyDzienCzasDel="'"+sdf.format(DataDzisiaj)+"'";
                   reqNodebIdentDels="truncate table raport_konfiguracja_aktualna.mml_rnc_Ucell_param;";
                   
                   
                   testStatement.executeUpdate(reqNodebIdentDels);
                   // testStatement.execute("ALTER TABLE konfiguracja_aktualna.mml_rnc_Ucell_param AUTO_INCREMENT = 1");
                   reqNodebIdentDels="truncate table raport_konfiguracja_aktualna.mml_rnc_Ucell_param_with_ncell;";
                  
                   testStatement.executeUpdate(reqNodebIdentDels);
                   
                   /*String[] delIndex1=new String[]
                   {
                       "ALTER TABLE `raport_konfiguracja_aktualna`.`mml_rnc_Ucell_param` DROP INDEX `btree_mml_ucell_cellIdent`",
                       "ALTER TABLE `raport_konfiguracja_aktualna`.`mml_rnc_Ucell_param` DROP INDEX `btree_mml_ucell_paramIdent`",
                       
                       "ALTER TABLE `raport_konfiguracja_aktualna`.`mml_rnc_Ucell_param_with_ncell` DROP INDEX `btree_mml_uncell_cellIdent`",
                       "ALTER TABLE `raport_konfiguracja_aktualna`.`mml_rnc_Ucell_param_with_ncell` DROP INDEX `btree_mml_uncell_ncellIdent`",
                       "ALTER TABLE `raport_konfiguracja_aktualna`.`mml_rnc_Ucell_param_with_ncell` DROP INDEX `btree_mml_uncell_paramIdent`"    
                   };
                   for(int d=0;d<delIndex1.length;d++)
                   {
                       try{
                           System.out.println(delIndex1[d]);
                            testStatement.execute(delIndex1[d]);
                       }
                       catch(Exception ee)
                       {
                           ee.printStackTrace();
                       }
                   }*/
                   
                   
                   //testStatement.execute("ALTER TABLE raport_konfiguracja_aktualna.mml_rnc_Ucell_param_with_ncell AUTO_INCREMENT = 1");
                   
                   //testStatement.execute("ALTER TABLE raport_konfiguracja_aktualna.mml_rnc_Ucell_param AUTO_INCREMENT=1");
                   //testStatement.execute("ALTER TABLE raport_konfiguracja_aktualna.mml_rnc_Ucell_param_with_ncell AUTO_INCREMENT=1");
                   

                   logger.info("[CLEANER CELL PARAM DELETE END]");
               }
          
           }
           catch(Exception ee)
           {
               logger.throwing(null, "[CLEANER CELL PARAM DELETE]", ee);
           }
           finally
           {
               try
               {
                   testStatement.close();
                   connection.close();
               }
               catch(Exception ee)
               {
                   logger.throwing(null, "[CLEANER CELL CONNECTION CLOSER", ee);
               }
           }
           
           //////////////////////////////////////////////////////
	   //////////////////// RNCUPDATER //////////////////////
           //////////////////////////////////////////////////////
           
	    GrupaZadan uLtecell_update=new GrupaZadan(3,"UPDATE UCELLS",logger);
            GrupaZadan MultiGrNode=new GrupaZadan(3,"MULTICELL_GR_ON_NODE",logger);
            GrupaZadan AttenNode=new GrupaZadan(4,"RXATTEN_NODE",logger);
	    GrupaZadan NTPCNode=new GrupaZadan(4,"NTPC_NODE",logger);
            GrupaZadan ISD_SWITCH_Node=new GrupaZadan(4,"ISD_SWITCH_NODE",logger);
            GrupaZadan NE_STSTUS=new GrupaZadan(4,"NE_STATUS",logger);
            GrupaZadan ADA_RE_SWITCH_Node=new GrupaZadan(4,"ADA_RE_SWITCH_NODE",logger);
	    GrupaZadan LODETNode=new GrupaZadan(4,"LOCELL_DET UPDATE_GR",logger);
            GrupaZadan MMLNode=new GrupaZadan(4,"MML_NODEB",logger);
            
	    GrupaZadan MMLUcell=new GrupaZadan(2,"MML_Ucell",logger);
            GrupaZadan MML_Adjnode=new GrupaZadan(4,"MML_Adjnode",logger);
            GrupaZadan MMLBts=new GrupaZadan(2,"MML_BTS",logger);
            GrupaZadan MMLGcell=new GrupaZadan(2,"MML_Gcell",logger);
            GrupaZadan MMLGextlte=new GrupaZadan(2,"MML_GEXTLTECELL",logger);
            GrupaZadan MMLGlteCell=new GrupaZadan(2,"MML_GLTENCELL",logger);

            
            GrupaZadan algoSwitch=new GrupaZadan(4,"ALGOSWITCH",logger);
            GrupaZadan CMCTRL_GR=new GrupaZadan(4,"CMCTRL_STATE",logger);
            GrupaZadan IPCLK_GR=new GrupaZadan(4,"IPCLK_PT",logger);
            GrupaZadan FLOWCTRLPARA_GR=new GrupaZadan(4,"FLOWCTRLPARA_NODE",logger);
            GrupaZadan RSCGRP_GR=new GrupaZadan(4,"RSCGRP",logger);
            
            //RSCGRP
            	    
            for(int r=0;RNCIDENT!=null&&r<RNCIDENT.rowCount();r++)
            {
                uLtecell_update.add(new UlteCell_perRnc_Updater("LTEUCELL_"+RNCIDENT.getValue("Rnc_Bsc_Name", r),RNCIDENT.getValue("Rnc_Bsc_Name", r),UpdaterInterface.ADD,logger,DOA,sprzTEST));
                MultiGrNode.add(new NodebMultiCellGr_perRnc_Updater("MULTIGR_"+RNCIDENT.getValue("Rnc_Bsc_Name", r),RNCIDENT.getValue("Rnc_Bsc_Name", r),UpdaterInterface.ADD,logger,DOA,sprzTEST));
                AttenNode.add(new NodebRXATTEN_perRnc_Updater("RXATTEN_"+RNCIDENT.getValue("Rnc_Bsc_Name", r),RNCIDENT.getValue("Rnc_Bsc_Name", r),UpdaterInterface.ADD,logger,DOA,sprzTEST));
                NTPCNode.add(new NodebNTPC_perRnc_Updater("NTPC_"+RNCIDENT.getValue("Rnc_Bsc_Name", r),RNCIDENT.getValue("Rnc_Bsc_Name", r),UpdaterInterface.ADD,logger,DOA,sprzTEST));
                ISD_SWITCH_Node.add(new NodebIsdSwitch_perRnc_Updater("ISD_SWITCH_"+RNCIDENT.getValue("Rnc_Bsc_Name", r),RNCIDENT.getValue("Rnc_Bsc_Name", r),UpdaterInterface.ADD,logger,DOA,sprzTEST));
                NE_STSTUS.add(new NodebMntMode_perRnc_Updater("NE_STSTUS_"+RNCIDENT.getValue("Rnc_Bsc_Name", r),RNCIDENT.getValue("Rnc_Bsc_Name", r),UpdaterInterface.ADD,logger,DOA,sprzTEST));
                ADA_RE_SWITCH_Node.add(new Nodeb_adaRetSwitch_perRnc_Updater("ADA_RE_SWITCH_"+RNCIDENT.getValue("Rnc_Bsc_Name", r),RNCIDENT.getValue("Rnc_Bsc_Name", r),UpdaterInterface.ADD,logger,DOA,sprzTEST));
                LODETNode.add(new Locell_details_perRnc_Updater("LOCELL_DET_"+RNCIDENT.getValue("Rnc_Bsc_Name", r),RNCIDENT.getValue("Rnc_Bsc_Name", r),UpdaterInterface.ADD,logger,DOA,sprzTEST));
                MMLNode.add(new MML_Nodeb_perRNC_Updater("MML_NODE_"+RNCIDENT.getValue("Rnc_Bsc_Name", r),RNCIDENT.getValue("Rnc_Bsc_Name", r),logger, DOA,mmlDir,sprzTEST));
                MMLUcell.add(new MML_UCELL_perRNC_Updater("MML_UCELL_"+RNCIDENT.getValue("Rnc_Bsc_Name", r), RNCIDENT.getValue("Rnc_Bsc_Name", r),logger, DOA,mmlDir,sprzTEST));
                MML_Adjnode.add(new MML_ADJNODE_perRNC_Updater("MML_ADJNODE_"+RNCIDENT.getValue("Rnc_Bsc_Name", r),RNCIDENT.getValue("Rnc_Bsc_Name", r),logger, DOA,mmlDir,sprzTEST));
                algoSwitch.add(new UcellAlgoSwitch_perRnc_Updater("ALGOSWITCH_"+RNCIDENT.getValue("Rnc_Bsc_Name", r),RNCIDENT.getValue("Rnc_Bsc_Name", r),UpdaterInterface.ADD,logger,DOA,sprzTEST));
                CMCTRL_GR.add(new NodebCMCTRL_perRnc_Updater("CMCTRL_STATE_"+RNCIDENT.getValue("Rnc_Bsc_Name", r),RNCIDENT.getValue("Rnc_Bsc_Name", r),UpdaterInterface.ADD,logger,DOA,sprzTEST));
                IPCLK_GR.add(new IpClk_pt_perRnc_Updater("IPCLK_"+RNCIDENT.getValue("Rnc_Bsc_Name", r),RNCIDENT.getValue("Rnc_Bsc_Name", r),UpdaterInterface.ADD,logger,DOA,sprzTEST));
                FLOWCTRLPARA_GR.add(new Nodeb_dlflowcontrolpara_perRnc_Updater("FLOWCTRLPARA_"+RNCIDENT.getValue("Rnc_Bsc_Name", r),RNCIDENT.getValue("Rnc_Bsc_Name", r),UpdaterInterface.ADD,logger,DOA,sprzTEST));
                
                RSCGRP_GR.add(new Nodeb_rscgrp_perRnc_Updater("RSCGRP_"+RNCIDENT.getValue("Rnc_Bsc_Name", r),RNCIDENT.getValue("Rnc_Bsc_Name", r),UpdaterInterface.ADD,logger,DOA,sprzTEST));
            }   
            
            ///////////////////////////////////////////////////////
            /////////////////// BSC UPDATER ///////////////////////
            ///////////////////////////////////////////////////////
            
            GrupaZadan btsbindLocGroup=new GrupaZadan(4,"CLEAN NODE",logger);
            GrupaZadan gtrxdevGroup=new GrupaZadan(4,"CLEAN NODE",logger);
            GrupaZadan PtpBvcGroup=new GrupaZadan(4,"PtpBvc",logger);
            GrupaZadan BtsEthPortGroup=new GrupaZadan(4,"BtsEthPort",logger);
            GrupaZadan G2GNCELLGroup=new GrupaZadan(4,"G2GNCELL",logger);
            for(int r=0;BSCIDENT!=null&&r<BSCIDENT.rowCount();r++)
            {
                btsbindLocGroup.add(new Btsbindlocgr_perBsc_Updater("BINDLOC_GR_"+BSCIDENT.getValue("Rnc_Bsc_Name", r),BSCIDENT.getValue("Rnc_Bsc_Name", r),UpdaterInterface.ADD,logger,DOA,sprzTEST));
                gtrxdevGroup.add(new gtrxdev_perBsc_Updater("GTRXDEV_"+BSCIDENT.getValue("Rnc_Bsc_Name", r),BSCIDENT.getValue("Rnc_Bsc_Name", r),UpdaterInterface.ADD,logger,DOA,sprzTEST));
                PtpBvcGroup.add(new PtpBvc_perBsc_Updater("PTPBVC_"+BSCIDENT.getValue("Rnc_Bsc_Name", r),BSCIDENT.getValue("Rnc_Bsc_Name", r),UpdaterInterface.ADD,logger,DOA,sprzTEST));
                MMLBts.add(new MML_Bts_perBSC_Updater("MML_BTS_"+BSCIDENT.getValue("Rnc_Bsc_Name", r),BSCIDENT.getValue("Rnc_Bsc_Name", r),logger, DOA,mmlDir,sprzTEST));
                MMLGcell.add(new MML_GCELL_perBSC_Updater("MML_GCELL_"+BSCIDENT.getValue("Rnc_Bsc_Name", r), BSCIDENT.getValue("Rnc_Bsc_Name", r),logger, DOA,mmlDir,sprzTEST));
                MMLGextlte.add(new MML_GEXTLTECELL_perBSC_Updater("MML_GEXTLTECELL_"+BSCIDENT.getValue("Rnc_Bsc_Name", r), BSCIDENT.getValue("Rnc_Bsc_Name", r),logger, DOA,mmlDir,sprzTEST));
                MMLGlteCell.add(new MML_GLTENCELL_perBSC_Updater("MML_GLTENCELL_"+BSCIDENT.getValue("Rnc_Bsc_Name", r), BSCIDENT.getValue("Rnc_Bsc_Name", r),logger, DOA,mmlDir,sprzTEST));
                BtsEthPortGroup.add(new btsEthport_perBsc_Updater("BTSETHPORT_"+BSCIDENT.getValue("Rnc_Bsc_Name", r),BSCIDENT.getValue("Rnc_Bsc_Name", r),UpdaterInterface.ADD,logger,DOA,sprzTEST));
                G2GNCELLGroup.add(new G2GNCELL_perBsc_Updater("G2GNCELL"+BSCIDENT.getValue("Rnc_Bsc_Name", r),BSCIDENT.getValue("Rnc_Bsc_Name", r),UpdaterInterface.ADD,logger,DOA,sprzTEST));
                //G2GNCELL
            }
            
            ////////////////////////////////////////////////
            ////////////// LTE UPDATER /////////////////////
            ////////////////////////////////////////////////
            
            
	    GrupaZadan uncell_update=new GrupaZadan(4,"UPDATE UN_CELLL",logger);
            if(wzorzec.equals("")||wzorzec.equalsIgnoreCase("WAW"))
	    uncell_update.add(new Utran_ncell_perM2000_Updater("WAW_UNCELL","WAW",UpdaterInterface.ADD,logger,DOA,sprzTEST));
            if(wzorzec.equals("")||wzorzec.equalsIgnoreCase("GDA"))
	    uncell_update.add(new Utran_ncell_perM2000_Updater("GDA_UNCELL","GDA",UpdaterInterface.ADD,logger,DOA,sprzTEST));
	    if(wzorzec.equals("")||wzorzec.equalsIgnoreCase("POZ"))
            uncell_update.add(new Utran_ncell_perM2000_Updater("POZ_UNCELL","POZ",UpdaterInterface.ADD,logger,DOA,sprzTEST));
            if(wzorzec.equals("")||wzorzec.equalsIgnoreCase("KAT"))
	    uncell_update.add(new Utran_ncell_perM2000_Updater("KAT_UNCELL","KAT",UpdaterInterface.ADD,logger,DOA,sprzTEST));

	    GrupaZadan utran_extncell_update=new GrupaZadan(4,"UPDATE UEXT_CELL",logger);
	    if(wzorzec.equals("")||wzorzec.equalsIgnoreCase("WAW"))
            utran_extncell_update.add(new Utran_external_perM2000_Updater("WAW_UTRAN_EXTERNCELL","WAW",UpdaterInterface.ADD,logger,DOA,sprzTEST));
	    if(wzorzec.equals("")||wzorzec.equalsIgnoreCase("GDA"))
            utran_extncell_update.add(new Utran_external_perM2000_Updater("GDA_UTRAN_EXTERNCELL","GDA",UpdaterInterface.ADD,logger,DOA,sprzTEST));
	    if(wzorzec.equals("")||wzorzec.equalsIgnoreCase("POZ"))
            utran_extncell_update.add(new Utran_external_perM2000_Updater("POZ_UTRAN_EXTERNCELL","POZ",UpdaterInterface.ADD,logger,DOA,sprzTEST));
	    if(wzorzec.equals("")||wzorzec.equalsIgnoreCase("KAT"))
            utran_extncell_update.add(new Utran_external_perM2000_Updater("KAT_UTRAN_EXTERNCELL","KAT",UpdaterInterface.ADD,logger,DOA,sprzTEST));

            
            GrupaZadan deviceIp_update=new GrupaZadan(4,"DEVICE_IP_UPDATER",logger);
	    if(wzorzec.equals("")||wzorzec.equalsIgnoreCase("WAW"))
            deviceIp_update.add(new DevipNe_perM200_Updater("WAW_DEVIP","WAW",UpdaterInterface.ADD,logger,DOA,sprzTEST));
	    if(wzorzec.equals("")||wzorzec.equalsIgnoreCase("GDA"))
            deviceIp_update.add(new DevipNe_perM200_Updater("GDA_DEVIP","GDA",UpdaterInterface.ADD,logger,DOA,sprzTEST));
	    if(wzorzec.equals("")||wzorzec.equalsIgnoreCase("POZ"))
            deviceIp_update.add(new DevipNe_perM200_Updater("POZ_DEVIP","POZ",UpdaterInterface.ADD,logger,DOA,sprzTEST));
	    if(wzorzec.equals("")||wzorzec.equalsIgnoreCase("KAT"))
            deviceIp_update.add(new DevipNe_perM200_Updater("KAT_DEVIP","KAT",UpdaterInterface.ADD,logger,DOA,sprzTEST));

            
            GrupaZadan ethPort_update=new GrupaZadan(4,"ETHPORT_PER_M2000_UPDATER",logger);
	    if(wzorzec.equals("")||wzorzec.equalsIgnoreCase("WAW"))
            ethPort_update.add(new EthPort_perM2000_Updater("WAW_ETHPORT","WAW",UpdaterInterface.ADD,logger,DOA,sprzTEST));
	    if(wzorzec.equals("")||wzorzec.equalsIgnoreCase("GDA"))
            ethPort_update.add(new EthPort_perM2000_Updater("GDA_ETHPORT","GDA",UpdaterInterface.ADD,logger,DOA,sprzTEST));
	    if(wzorzec.equals("")||wzorzec.equalsIgnoreCase("POZ"))
            ethPort_update.add(new EthPort_perM2000_Updater("POZ_ETHPORT","POZ",UpdaterInterface.ADD,logger,DOA,sprzTEST));
	    if(wzorzec.equals("")||wzorzec.equalsIgnoreCase("KAT"))
            ethPort_update.add(new EthPort_perM2000_Updater("KAT_ETHPORT","KAT",UpdaterInterface.ADD,logger,DOA,sprzTEST));
            
            
            GrupaZadan Rruchain_update=new GrupaZadan(4,"RRUCHAIN_PER_M2000_UPDATER",logger);
	    if(wzorzec.equals("")||wzorzec.equalsIgnoreCase("WAW"))
            Rruchain_update.add(new Rruchain_perM2000_Updater("WAW_RRUCHAIN","WAW",UpdaterInterface.ADD,logger,DOA,sprzTEST));
	    if(wzorzec.equals("")||wzorzec.equalsIgnoreCase("GDA"))
            Rruchain_update.add(new Rruchain_perM2000_Updater("GDA_RRUCHAIN","GDA",UpdaterInterface.ADD,logger,DOA,sprzTEST));
	    if(wzorzec.equals("")||wzorzec.equalsIgnoreCase("POZ"))
            Rruchain_update.add(new Rruchain_perM2000_Updater("POZ_RRUCHAIN","POZ",UpdaterInterface.ADD,logger,DOA,sprzTEST));
	    if(wzorzec.equals("")||wzorzec.equalsIgnoreCase("KAT"))
            Rruchain_update.add(new Rruchain_perM2000_Updater("KAT_RRUCHAIN","KAT",UpdaterInterface.ADD,logger,DOA,sprzTEST));
            
            
            
            GrupaZadan SEQTOR_EQ_GR=new GrupaZadan(4,"SEQTOR_EQ",logger);
	    if(wzorzec.equals("")||wzorzec.equalsIgnoreCase("WAW"))
            SEQTOR_EQ_GR.add(new SectorEq_perM2000_Updater("WAW_SEQTOR_EQ","WAW",UpdaterInterface.ADD,logger,DOA,sprzTEST));
	    if(wzorzec.equals("")||wzorzec.equalsIgnoreCase("GDA"))
            SEQTOR_EQ_GR.add(new SectorEq_perM2000_Updater("GDA_SEQTOR_EQ","GDA",UpdaterInterface.ADD,logger,DOA,sprzTEST));
	    if(wzorzec.equals("")||wzorzec.equalsIgnoreCase("POZ"))
            SEQTOR_EQ_GR.add(new SectorEq_perM2000_Updater("POZ_SEQTOR_EQ","POZ",UpdaterInterface.ADD,logger,DOA,sprzTEST));
	    if(wzorzec.equals("")||wzorzec.equalsIgnoreCase("KAT"))
            SEQTOR_EQ_GR.add(new SectorEq_perM2000_Updater("KAT_SEQTOR_EQ","KAT",UpdaterInterface.ADD,logger,DOA,sprzTEST));
            
            ////////////////////////////////////////////////
            ////////////// PER M2000 UPDATER ///////////////
            ////////////////////////////////////////////////
 
            if(MML_NODE||!wybraneModuly)
		poolMML.add2KolejkaGrup(MMLNode);
            if(MML_ANI||!wybraneModuly)
		poolMML.add2KolejkaGrup(MML_Adjnode);
	    if(MML_UCELL||!wybraneModuly)
		poolMML.add2KolejkaGrup(MMLUcell);
            if(MML_BTS||!wybraneModuly)
                poolMML.add2KolejkaGrup(MMLBts);
            if(MML_GCELL||!wybraneModuly)
		poolMML.add2KolejkaGrup(MMLGcell);
            if(MML_GEXTLTECELL||!wybraneModuly)
                poolMML.add2KolejkaGrup(MMLGextlte);
            if(MML_GLTENCELL||!wybraneModuly)
                poolMML.add2KolejkaGrup(MMLGlteCell);            
            if(RXATTEN||!wybraneModuly)
		pool.add2KolejkaGrup(AttenNode);
	    if(gtrxdev||!wybraneModuly)
                pool.add2KolejkaGrup(gtrxdevGroup);
             if(ALGO_SWITCH_ON_RNC||!wybraneModuly)
                pool.add2KolejkaGrup(algoSwitch);
            if(UN_CELL_ON_ENODEB)
		pool.add2KolejkaGrup(uncell_update);
	    if(UEXTERNAL_ON_ENODEB)
		pool.add2KolejkaGrup(utran_extncell_update);
	    if(NTPC||!wybraneModuly)
		pool.add2KolejkaGrup(NTPCNode);
	    if(PTP_BVC_ON_BSC||!wybraneModuly)
                pool.add2KolejkaGrup(PtpBvcGroup);
            if(ISD_SWITCH||!wybraneModuly)
                pool.add2KolejkaGrup(ISD_SWITCH_Node);
            if(ADA_RE_SWITCH||!wybraneModuly)
                pool.add2KolejkaGrup(ADA_RE_SWITCH_Node);
            if(NE_STATUS||!wybraneModuly)
                pool.add2KolejkaGrup(NE_STSTUS);
            if(CMCTRL_STATE||!wybraneModuly)
                pool.add2KolejkaGrup(CMCTRL_GR);
            if(IPCLK_PT||!wybraneModuly)
                pool.add2KolejkaGrup(IPCLK_GR);
            if(FLOWCTRLPARA||!wybraneModuly)
                pool.add2KolejkaGrup(FLOWCTRLPARA_GR);
             if(LODET||!wybraneModuly)
                pool.add2KolejkaGrup(LODETNode);
            if(ETHPORT||!wybraneModuly)
                pool.add2KolejkaGrup(ethPort_update);
            if(ETHPORTBSC||!wybraneModuly)
                pool.add2KolejkaGrup(BtsEthPortGroup);
            if(G2GNCELL||!wybraneModuly)
                pool.add2KolejkaGrup(G2GNCELLGroup);
            if(RRUCHAIN||!wybraneModuly)
                pool.add2KolejkaGrup(Rruchain_update);
            
            if(SEQTOR_EQ||!wybraneModuly)
                pool.add2KolejkaGrup(SEQTOR_EQ_GR);            
            if(DEVIP||!wybraneModuly)
                pool.add2KolejkaGrup(deviceIp_update);
            if(RSCGRP||!wybraneModuly)
               pool.add2KolejkaGrup(RSCGRP_GR);
            if(MULTIGR||!wybraneModuly)
		pool.add2KolejkaGrup(MultiGrNode);	
	    if(BINDLOC||!wybraneModuly)
		pool.add2KolejkaGrup(btsbindLocGroup);
            if(LTEUCELL||!wybraneModuly)
		pool.add2KolejkaGrup(uLtecell_update);
            
	    logger.info("UPDATER ADDED TO POOL");
	    logger.info("START POOL");
            
            Thread sq = new Thread(new Runnable()
            {
                public void run() 
                {
                    try
                    {
                        logger.info("NORTH_UPDATES_START");
                        pool.start();
                        pool.join();
                        logger.info("NORTH_UPDATES_END");
                    }
                    catch (InterruptedException ex)
                    {
                         logger.log(Level.FINEST, "BLAD W GLOWNYM WATKU", ex);
                    }
                }
            });
            
            
            
              Thread sqMML = new Thread(new Runnable()
            {
            public void run() 
            {
                try
                {
                    logger.info("MML_UPDATES_START");    
                    poolMML.start();
                    poolMML.join();
                    logger.info("MML_UPDATES_END");
                }
                catch (InterruptedException ex)
                {
                    logger.log(Level.FINEST, "BLAD W GLOWNYM WATKU", ex);
                }
                
            }
        });
              
              
            sqMML.start();
            sq.start();
            
            sq.join();
            
            sqMML.join();
            

	    String reqNodebIdentDel="";
	    try
            {
                connection = DOA.getConnection();
                testStatement = connection.createStatement();
                
                logger.info("[CLEANER NodeB/AdjNode Ident]START");
                reqNodebIdentDel = "delete from raport_konfiguracja_aktualna.mml_rnc_Nodeb_ident  where last_update<curdate();";

                testStatement.executeUpdate(reqNodebIdentDel);

                reqNodebIdentDel = "delete from raport_konfiguracja_aktualna.mml_rnc_Adjnode_ident  where last_update<curdate();";
                testStatement.executeUpdate(reqNodebIdentDel);
                
                if(MML_UCELL||!wybraneModuly)
                {
                /*String[] delIndex1=new String[]
                   {
                       "CREATE INDEX `btree_mml_ucell_cellIdent` USING BTREE ON `raport_konfiguracja_aktualna`.`mml_rnc_Ucell_param`(`cell_row_id`)",
                       "CREATE INDEX `btree_mml_ucell_paramIdent` USING BTREE ON `raport_konfiguracja_aktualna`.`mml_rnc_Ucell_param`(`param_ident`)",
                       
                       "CREATE INDEX `btree_mml_uncell_cellIdent` USING BTREE ON `raport_konfiguracja_aktualna`.`mml_rnc_Ucell_param_with_ncell`(`cell_row_id`)",
                       "CREATE INDEX `btree_mml_uncell_ncellIdent` USING BTREE ON `raport_konfiguracja_aktualna`.`mml_rnc_Ucell_param_with_ncell`(`param_ident`)",
                       "CREATE INDEX `btree_mml_uncell_paramIdent` USING BTREE ON `raport_konfiguracja_aktualna`.`mml_rnc_Ucell_param_with_ncell`(`ncell_id`)"
                       
                   };
                   for(int d=0;d<delIndex1.length;d++)
                   {
                       try{
                            System.out.println(delIndex1[d]);
                            testStatement.execute(delIndex1[d]);
                       }
                       catch(Exception ee)
                       {
                           ee.printStackTrace();
                       }
                   }*/
               }
            }
            catch (Exception ee)
            {
                logger.throwing(null, "[CLEANER NodeB/AdjNode Ident] query=" + reqNodebIdentDel, ee);

            }
            finally
            {
                try
                {
                    logger.info("[CLEANER NodeB/AdjNode Ident]END");
                    testStatement.close();
                    connection.close();
                }
                catch(Exception ee)
                {
                    
                }
            }
            logger.info("UPDATE DB END");
            logger.log(Level.OFF,"POMYSLNE ZAKONCZENIE DZIALANIA APLIKACJI");
	    System.exit(0);
	}
	catch(Exception ee)
	{
	    ee.printStackTrace();
	    logger.log(Level.FINEST, "BLAD W GLOWNYM WATKU", ee);
	}
    }
}
