package nbipackage;

import java.io.IOException;

public class NorthTool
{

    NewFile tool;
    NorthB north;
    NewFile bledy;

    public NorthTool(NorthB north, NewFile bledy)
    {
        tool = new NewFile();
        this.north = north;
        this.bledy = bledy;
    }

    public NorthTool()
    {
        tool = new NewFile();
    }

    public java.util.ArrayList<String> getCellIdsList(String RncName, String NodebName) throws NBIAnsException, IOException//LST cellsId for specyfic node
    {
        //north.make("REG NE:NAME="+RncName);
        //String lstUcell=north.make("LST UCELL:NODEBNAME=\""+NodebName+"\",LSTFORMAT=VERTICAL");
        //NPack NPCAK=new NPack(lstUcell.split("\n"),new String[]{"LST UCELL:"},new String[]{"List Cell Basic Information"},new String[]{"---    END"});
        //java.util.ArrayList<pczka> listaTymczasowa=NPCAK.getAllPacks();
        java.util.ArrayList<Paczka> listaTymczasowa = lstUcell(RncName, NodebName);
        java.util.ArrayList<String> CellIdsList = new java.util.ArrayList<String>();
        if (listaTymczasowa != null)
        {
            for (int z = 0; z < listaTymczasowa.size(); z++)
            {
                Paczka paczka = (Paczka) listaTymczasowa.get(z);
                String CellId = paczka.getWartosc("Cell ID").trim();
                CellIdsList.add(CellId);
            }
        }
        return CellIdsList;
    }
    public java.util.ArrayList<String> getGCellNamesList(String BscName, String BtsName) throws NBIAnsException, IOException//LST cellsId for specyfic node
    {
        //north.make("REG NE:NAME="+RncName);
        //String lstUcell=north.make("LST UCELL:NODEBNAME=\""+NodebName+"\",LSTFORMAT=VERTICAL");
        //NPack NPCAK=new NPack(lstUcell.split("\n"),new String[]{"LST UCELL:"},new String[]{"List Cell Basic Information"},new String[]{"---    END"});
        //java.util.ArrayList<pczka> listaTymczasowa=NPCAK.getAllPacks();
        java.util.ArrayList<Paczka> listaTymczasowa = lstGcell(BscName, BtsName);
        java.util.ArrayList<String> CellNamesList = new java.util.ArrayList<String>();
        if (listaTymczasowa != null)
        {
            for (int z = 0; z < listaTymczasowa.size(); z++)
            {
                Paczka paczka = (Paczka) listaTymczasowa.get(z);
                String CellName = paczka.getWartosc("Cell Name").trim();
                CellNamesList.add(CellName);
            }
        }
        return CellNamesList;
    }

    public java.util.ArrayList<Paczka> lstUlgroup(String NodebName) throws NBIAnsException, IOException, IOException//LST ULGROUP
    {
        //north.make("REG NE:NAME="+NodebName);
        String kommOdp = north.make(NodebName, "LST ULGROUP:");
        NPack NPCAK = new NPack(kommOdp.split("\n"), new String[]
                {
                    "LST ULGROUP:"
                }, new String[]
                {
                    "-------"
                }, new String[]
                {
                    "---    END"
                });
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        //System.out.println(kommOdp);
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa;//.get(0);
        }
        else
        {
            NPCAK = new NPack(kommOdp);
            listaTymczasowa = NPCAK.getAllPacks();
            if (listaTymczasowa != null && listaTymczasowa.size() > 0)
            {
                return listaTymczasowa;//.get(0);
            } 
            else
            {
                System.out.println(kommOdp);
                return null;
            }
        }
        //return listaTymczasowa;
    }
    public java.util.ArrayList<Paczka> lstDlgroup(String NodebName)throws NBIAnsException, IOException, IOException										//LST ULGROUP
	{

		String kommOdp=north.make(NodebName,"LST DLGROUP:");
		NPack NPCAK=new NPack(kommOdp.split("\n"),new String[]{"LST DLGROUP:"},new String[]{"-------"},new String[]{"---    END"});
		java.util.ArrayList<Paczka> listaTymczasowa=NPCAK.getAllPacks();

		if(listaTymczasowa==null||listaTymczasowa.size()==0)
		{
			NPCAK=new NPack(kommOdp);
			listaTymczasowa=NPCAK.getAllPacks();
		}
		if(listaTymczasowa!=null&&listaTymczasowa.size()>0)
			return listaTymczasowa;//.get(0);
		else
		{
			System.out.println(kommOdp);
			return null;
		}
		//return listaTymczasowa;
	}
    public java.util.ArrayList<Paczka> lstDualCellGrp(String NodeName)			throws NBIAnsException, IOException, IOException						//LST DUALCELLGRP
	{

		String lst =north.make(NodeName,"LST DUALCELLGRP:");
		NPack NPCAK=new NPack(lst);//.split("\n"),new String[]{"LST DUALCELLGRP:"},new String[]{"DualCell Config Information"},new String[]{"---    END"});
		java.util.ArrayList<Paczka> listaTymczasowa=NPCAK.getAllPacks();
		if(listaTymczasowa!=null&&listaTymczasowa.size()>0)
			return listaTymczasowa;
		else
		{
			System.out.println("*****************\n"+lst);
			return null;
		}
	}
    public java.util.ArrayList<Paczka> dspEthPortOnNode(String NodebName) throws NBIAnsException, IOException, IOException//DSP ETHPORT
    {
        //north.make("REG NE:NAME="+NodebName);
        String kommOdp = north.make(NodebName, "DSP ETHPORT:");//);
        NPack NPCAK = new NPack(kommOdp.split("\n"), new String[]
                {
                    "DSP ETHPORT:"
                }, new String[]
                {
                    "-------"
                }, new String[]
                {
                    "---    END"
                });
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        //System.out.println(kommOdp);
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa;//.get(0);
        } else
        {

            NPCAK = new NPack(kommOdp);
            listaTymczasowa = NPCAK.getAllPacks();
            return listaTymczasowa;
            //System.out.println(kommOdp);

        }
        //return listaTymczasowa;
    }

    public java.util.ArrayList<Paczka> dspBRD(String NodebName) throws NBIAnsException, IOException, IOException//DSP BRD
    {
        //north.make("REG NE:NAME="+NodebName);
        String kommOdp = north.make(NodebName, "DSP BRD:");//);
        NPack NPCAK = new NPack(kommOdp);//.split("\n"),new String[]{"LST ULGROUP:"},new String[]{"-------"},new String[]{"---    END"});
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        //System.out.println(kommOdp);
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa;//.get(0);
        } else
        {
            System.out.println(kommOdp);
            return null;
        }
        //return listaTymczasowa;
    }

    public java.util.ArrayList<Paczka> lstUsccpch(String RncName,String cellid)	throws NBIAnsException, IOException, IOException		//LST USCCPCH:
	{


		String lstUcell=north.make(RncName,"LST USCCPCH:CELLID="+cellid);

		NPack NPCAK=new NPack(lstUcell);//.split("\n"),new String[]{"LST UCELL:"},new String[]{"List Cell Basic Information"},new String[]{"---    END"});
		java.util.ArrayList<Paczka> listaTymczasowa=NPCAK.getAllPacks();
		if(listaTymczasowa!=null&&listaTymczasowa.size()>0)
			return listaTymczasowa;//.get(0);
		else
		{
			System.out.println(lstUcell);
			return null;
		}
		//return listaTymczasowa;
	}
    public java.util.ArrayList<Paczka> lstUcell(String RncName, String NodebName) throws NBIAnsException, IOException//LST UCELL:
    {
        //north.make("REG NE:NAME="+RncName);
        if (NodebName != null)
        {
            NodebName = NodebName.replaceAll("\"", "");
        }
        String lstUcell = north.make(RncName, "LST UCELL:NODEBNAME=\"" + NodebName + "\",LSTFORMAT=HORIZONTAL");

        NPack NPCAK = new NPack(lstUcell);//.split("\n"),new String[]{"LST UCELL:"},new String[]{"List Cell Basic Information"},new String[]{"---    END"});
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa;//.get(0);
        } else
        {
            System.out.println(lstUcell);
            return null;
        }
        //return listaTymczasowa;
    }

     public java.util.ArrayList<Paczka> lstGcell(String BscName, String BtsName) throws NBIAnsException, IOException//LST UCELL:
    {
        //north.make("REG NE:NAME="+RncName);
        if (BtsName != null)
        {
            BtsName = BtsName.replaceAll("\"", "");
        }
        String lstGcell = north.make(BscName, "LST GCELL: IDTYPE=BYNAME, BTSNAME=\"" + BtsName + "\"");

        NPack NPCAK = new NPack(lstGcell);//.split("\n"),new String[]{"LST UCELL:"},new String[]{"List Cell Basic Information"},new String[]{"---    END"});
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa;//.get(0);
        } else
        {
            System.out.println(lstGcell);
            return null;
        }
        //return listaTymczasowa;
    }

    public java.util.ArrayList<Paczka> lstUnodeb(String RncName) throws NBIAnsException, IOException//LST UNODEB:
    {
        //north.make("REG NE:NAME="+RncName);

        String lstUcell = north.make(RncName, "LST UNODEB:LSTFORMAT=HORIZONTAL");

        NPack NPCAK = new NPack(lstUcell);//.split("\n"),new String[]{"LST UCELL:"},new String[]{"List Cell Basic Information"},new String[]{"---    END"});
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa;//.get(0);
        } else
        {
            System.out.println(lstUcell);
            return null;
        }
        //return listaTymczasowa;
    }

    public java.util.ArrayList<Paczka> lstBTSIPPM(String BSCName,String ani) throws NBIAnsException, IOException//LST BTSIPPM: IDTYPE=BYID, BTSID=405
    {
        //north.make("REG NE:NAME="+RncName);

        String lstBtsIppm = north.make(BSCName, "LST BTSIPPM: IDTYPE=BYID, BTSID="+ani);

        NPack NPCAK = new NPack(lstBtsIppm);//.split("\n"),new String[]{"LST IPPM:"},new String[]{"--------"},new String[]{"---    END"});
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa;//.get(0);
        } else
        {
            System.out.println(lstBtsIppm);
            return null;
        }
        //return listaTymczasowa;
    }



    public java.util.ArrayList<Paczka> lstIPPM(String RncName,String ani) throws NBIAnsException, IOException//LST IPPM
    {
        //north.make("REG NE:NAME="+RncName);

        String lstIppm = north.make(RncName, "LST IPPM: ANI="+ani);

        NPack NPCAK = new NPack(lstIppm.split("\n"),new String[]{"LST IPPM:"},new String[]{"--------"},new String[]{"---    END"});
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa;//.get(0);
        }
        else
        {
            System.out.println(lstIppm);
            return null;
        }
        //return listaTymczasowa;
    }
    public Paczka lstSpecUcellByName(String RncName, String cellname) throws NBIAnsException, IOException//LST UCELL: LSTTYPE=BYNAME
    {
        //north.make("REG NE:NAME="+RncName);

        String lstUcell = north.make(RncName, "LST UCELL:LSTTYPE=BYCELLNAME,CELLNAME=\"" + cellname + "\"");
        NPack NPCAK = new NPack(lstUcell);//.split("\n"),new String[]{"LST UCELL:"},new String[]{"List Cell Basic Information"},new String[]{"---    END"});
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();

        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa.get(0);
        }
        else
        {
            System.out.println(lstUcell);
            return null;
        }
    }

    public Paczka lstSpecUcellByCellid(String RncName, String cellid) throws NBIAnsException, IOException//LST UCELL: LSTTYPE=CELLID
    {
        //north.make("REG NE:NAME="+RncName);

        String lstUcell = north.make(RncName, "LST UCELL:LSTTYPE=BYCELLID,CELLID=" + cellid);
        NPack NPCAK = new NPack(lstUcell);//.split("\n"),new String[]{"LST UCELL:"},new String[]{"List Cell Basic Information"},new String[]{"---    END"});
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();

        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa.get(0);
        }
        else
        {
            System.out.println(lstUcell);
            return null;
        }
    }
    //LST UCELLURA:CELLID
    public Paczka lstUcellUra(String RncName, String cellid) throws NBIAnsException, IOException//LST UCELL: LSTTYPE=CELLID
    {
        //north.make("REG NE:NAME="+RncName);

        String lstUcell = north.make(RncName, "LST UCELLURA:CELLID=" + cellid);
        NPack NPCAK = new NPack(lstUcell);//.split("\n"),new String[]{"LST UCELL:"},new String[]{"List Cell Basic Information"},new String[]{"---    END"});
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();

        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa.get(0);
        }
        else
        {
            System.out.println(lstUcell);
            return null;
        }
    }

    public Paczka lstSpecGcellByName(String RncName, String cellname) throws NBIAnsException, IOException//LST GCELL: LSTTYPE=CELLID
    {
        //north.make("REG NE:NAME="+RncName);

        String lstUcell = north.make(RncName, "LST GCELL:IDTYPE=BYNAME,CELLNAME=\"" + cellname + "\"");
        NPack NPCAK = new NPack(lstUcell);//.split("\n"),new String[]{"LST UCELL:"},new String[]{"List Cell Basic Information"},new String[]{"---    END"});
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        //System.out.println(lstUcell);
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa.get(0);
        }
        else
        {
            System.out.println(lstUcell);
            return null;
        }
    }

    public java.util.ArrayList<Paczka> lstGcellGPRS(String RncName, String cellname) throws NBIAnsException, IOException//LST GCELLGPRS: IDTYP=BYNAME
    {
        //north.make("REG NE:NAME="+RncName);

        String lstUcell = north.make(RncName, "LST GCELLGPRS:IDTYPE=BYNAME,CELLNAME=\"" + cellname + "\"");
        NPack NPCAK = new NPack(lstUcell);//.split("\n"),new String[]{"LST UCELL:"},new String[]{"List Cell Basic Information"},new String[]{"---    END"});
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        //System.out.println(lstUcell);
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa;//.get(0);
        }
        else
        {
            System.out.println(lstUcell);
            return null;
        }
    }
    
    /**
     *  NodeB Name|NodeB ID |Subrack No.|Subrack name|Slot No.|Subsystem No.|IUB Trans Bearer Type  IP Trans Apart Ind  IUB Trans Delay  Satellite Trans Ind  NodeB Protocol Version  Resource Management Mode  NodeB Trace Switch  NodeB Host Type  Peer RNC ID  Peer NodeB ID  Sharing Type Of NodeB  Cn Operator Index  DSS NodeB Flag  Administrative state
     * @param RncName
     * @param regex
     * @return
     */
    public java.util.ArrayList<String> findNodeName(String RncName, String regex) throws NBIAnsException, IOException//DSP UCELL:
    {
        //String regName=north.make("REG NE:NAME="+RncName);//DSP UCELL: DSPT=BYCELL, LstFormat=VERTICAL
        String lstUcell = north.make(RncName, "LST UNODEB:LSTFORMAT=HORIZONTAL");
        NPack NPCAK = new NPack(lstUcell);//.split("\n"), new String[]{"DSP UCELL:"}, new String[]{"Cell state information"}, new String[]{"---    END"});

        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        java.util.ArrayList<String> odpowiedz=new java.util.ArrayList<String>();
        if (listaTymczasowa.size() > 0)
        {
            for(int a=0;a<listaTymczasowa.size();a++)
            {
                if(tool.zawiera(listaTymczasowa.get(a).getWartosc("NodeB Name"),regex))
                    odpowiedz.add(listaTymczasowa.get(a).getWartosc("NodeB Name"));
            }
        }
        else
        {
            System.out.println(lstUcell);
        }
        return odpowiedz;
    }

    public java.util.ArrayList<String> findBtsName(String BscName, String regex) throws NBIAnsException, IOException//DSP UCELL:
    {
        //String regName=north.make("REG NE:NAME="+RncName);//DSP UCELL: DSPT=BYCELL, LstFormat=VERTICAL
        String lstUcell = north.make(BscName, "LST BTS:");
        NPack NPCAK = new NPack(lstUcell);//.split("\n"), new String[]{"DSP UCELL:"}, new String[]{"Cell state information"}, new String[]{"---    END"});

        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        java.util.ArrayList<String> odpowiedz=new java.util.ArrayList<String>();
        if (listaTymczasowa!=null&&listaTymczasowa.size() > 0)
        {
            for(int a=0;a<listaTymczasowa.size();a++)
            {
                
                if(tool.zawiera(listaTymczasowa.get(a).getWartosc("BTS Name"),regex))
                    odpowiedz.add(listaTymczasowa.get(a).getWartosc("BTS Name"));
            }
        }
        else
        {
            System.out.println(lstUcell);
        }
        return odpowiedz;
    }


    public Paczka LstUcellAccessstrict(String RncName, String CELLID) throws NBIAnsException, IOException//LST UCELLACCESSSTRICT:
    {
        //north.make("REG NE:NAME="+RncName);
        String lst = north.make(RncName, "LST UCELLACCESSSTRICT:CELLID=" + CELLID + ",LSTFORMAT=VERTICAL");
        NPack NPCAK = new NPack(lst.split("\n"), new String[]
                {
                    "LST UCELLACCESSSTRICT:"
                }, new String[]
                {
                    "List Cell Access Restriction Information"
                }, new String[]
                {
                    "---    END"
                });
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        if (listaTymczasowa.size() > 0)
        {
            return listaTymczasowa.get(0);
        }


        return null;
    }

    public Paczka LstUnodebip(String RncName, String NodeName) throws NBIAnsException, IOException//LST UNODEBIP: LSTTYPE=BYNAME
    {
        //north.make("REG NE:NAME="+RncName);
        String lst = north.make(RncName, "LST UNODEBIP:LSTTYPE=BYNODEBNAME, NODEBNAME=\"" + NodeName + "\"");
        NPack NPCAK = new NPack(lst);//.split("\n"),new String[]{"LST UCELLACCESSSTRICT:"},new String[]{"List Cell Access Restriction Information"},new String[]{"---    END"});
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        if (listaTymczasowa.size() > 0)
        {
            return listaTymczasowa.get(0);
        }
        return null;
    }

    public Paczka LstIPOAPVC(String RncName, String peerIp) throws NBIAnsException, IOException//LST UNODEBIP: LSTTYPE=BYNAME
    {
        //north.make("REG NE:NAME="+RncName);
        String lst = north.make(RncName, "LST IPOAPVC: PEERIPADDR=\"" + peerIp + "\"");
        NPack NPCAK = new NPack(lst);//.split("\n"),new String[]{"LST UCELLACCESSSTRICT:"},new String[]{"List Cell Access Restriction Information"},new String[]{"---    END"});
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        if (listaTymczasowa.size() > 0)
        {
            return listaTymczasowa.get(0);
        }
        return null;
    }

    public java.util.ArrayList<Paczka> lstGext2Gcell(String bscName, String EXT2GCELLNAME)throws NBIAnsException, IOException
    {
        //String regOdp=north.make("REG NE:NAME="+bscName);
        String lst = north.make(bscName, "LST GEXT2GCELL:IDTYPE=BYNAME,EXT2GCELLNAME=\"" + EXT2GCELLNAME + "\"");
        NPack NPCAK = new NPack(lst);
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        //System.out.println(lst);
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa;//.get(0);
        } else
        {
            System.out.println(lst);
            return null;
        }
    }

    public java.util.ArrayList<Paczka> lstGext3Gcell(String RncName, String EXT3GCELLNAME)throws NBIAnsException, IOException
    {
        //String regOdp=north.make("REG NE:NAME="+RncName);
        String lst = north.make(RncName, "LST GEXT3GCELL:IDTYPE=BYNAME,EXT3GCELLNAME=\"" + EXT3GCELLNAME + "\"");
        NPack NPCAK = new NPack(lst);
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        //System.out.println(lst);
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa;//.get(0);
        } else
        {
            System.out.println(lst);
            return null;
        }
    }

    public java.util.ArrayList<Paczka> lstUext2Gcell(String rncName, String EXT2GCELLNAME)throws NBIAnsException, IOException
    {
        //String regOdp=north.make("REG NE:NAME="+rncName);
        String lst = north.make(rncName, "LST UEXT2GCELL:LSTTYPE=BYGSMCELLNAME,GSMCELLNAME=\"" + EXT2GCELLNAME + "\"");
        //LST UEXT2GCELL:LSTTYPE=ByGSMCellName,GSMCELLNAME="4459813G_BYD2102A_O_LOCHOWO";

        NPack NPCAK = new NPack(lst);
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        //System.out.println(lst);
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa;//.get(0);
        } else
        {
            System.out.println(lst);
            return null;
        }
    }

    public java.util.ArrayList<Paczka> lstUext3Gcell(String BscName, String EXT2GCELLNAME)throws NBIAnsException, IOException
    {
        //String regOdp=north.make("REG NE:NAME="+rncName);
        String lst = north.make(BscName, "LST UEXT3GCELL: LSTTYPE=BYCELLID, CELLID=" + EXT2GCELLNAME + ",LSTFORMAT=HORIZONTAL");
        //LST UEXT2GCELL:LSTTYPE=ByGSMCellName,GSMCELLNAME="4459813G_BYD2102A_O_LOCHOWO";

        NPack NPCAK = new NPack(lst);
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        //System.out.println(lst);
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa;//.get(0);
        } else
        {
            System.out.println(lst);
            return null;
        }
    }

    public java.util.ArrayList<Paczka> LstAdjnode(String RncName) throws NBIAnsException, IOException//LST ADJNODE: LSTTYPE=BYID
    {
        //north.make("REG NE:NAME="+RncName);
        String lst = north.make(RncName, "LST ADJNODE: LSTTYPE=BYID,LSTFORMAT=VERTICAL");
        //String lst =north.make("LST ADJNODE: LSTTYPE=BYID");

        NPack NPCAK = new NPack(lst.split("\n"), new String[]
                {
                    "LST ADJNODE:"
                }, new String[]
                {
                    "List Adjacent Node"
                }, new String[]
                {
                    "---    END"
                });
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        if (listaTymczasowa.size() > 0); else
        {
            System.out.println(lst);
        }
        return listaTymczasowa;
    }

    public java.util.ArrayList<Paczka> LstAdjnodeHorizontal(String RncName) throws NBIAnsException, IOException//LST ADJNODE: LSTTYPE=BYID
    {
        //north.make("REG NE:NAME="+RncName);
        String lst = north.make(RncName, "LST ADJNODE: LSTTYPE=BYID,LSTFORMAT=HORIZONTAL");
        //String lst =north.make("LST ADJNODE: LSTTYPE=BYID");

        NPack NPCAK = new NPack(lst);
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        if (listaTymczasowa.size() > 0); else
        {
            System.out.println(lst);
        }
        return listaTymczasowa;
    }
    public java.util.ArrayList<Paczka> LstSctplnk(String NodeName) throws NBIAnsException, IOException//LST SCTPLNK:
    {
        //north.make("REG NE:NAME=\""+NodeName+"\"");
        String lst = north.make(NodeName, "LST SCTPLNK:");
        //String lst =north.make("LST ADJNODE: LSTTYPE=BYID");

        NPack NPCAK = new NPack(lst.split("\n"), new String[]
                {
                    "LST SCTPLNK:"
                }, new String[]
                {
                    "-------"
                }, new String[]
                {
                    "---    END"
                });
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        if (listaTymczasowa.size() > 0); else
        {
            System.out.println(lst);
        }
        return listaTymczasowa;
    }

    public Paczka LstSctplnkOnRNC(String rncName, String sctpNr)throws NBIAnsException, IOException
    {
        //north.make("REG NE:NAME=\""+rncName+"\"");
        String lst = north.make(rncName, "LST SCTPLNK:SCTPLNKN=" + sctpNr);
        //String lst =north.make("LST ADJNODE: LSTTYPE=BYID");

        NPack NPCAK = new NPack(lst);//.split("\n"),new String[]{"LST SCTPLNK:"},new String[]{"-------"},new String[]{"---    END"});
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        if (listaTymczasowa.size() > 0)
        {
            return listaTymczasowa.get(0);
        }
        else
        {
            System.out.println(lst);
        }
        return null;
    }

    public java.util.ArrayList<Paczka> LstAdjnode2(String RncName) throws NBIAnsException, IOException//LST ADJNODE: LSTTYPE=BYID
    {
        ////north.make("REG NE:NAME="+RncName);
        //String lst =north.make("LST ADJNODE: LSTTYPE=BYID,LSTFORMAT=VERTICAL");
        String lst = north.make(RncName, "LST ADJNODE: LSTTYPE=BYID");

        NPack NPCAK = new NPack(lst);//.split("\n"),new String[]{"LST ADJNODE:"},new String[]{"List Adjacent Node"},new String[]{"---    END"});
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        if (listaTymczasowa.size() > 0); else
        {
            NPCAK = new NPack(lst);
            listaTymczasowa = NPCAK.getAllPacks();
        }
        if (listaTymczasowa.size() > 0); else
        {
            System.out.println(lst);
        }

        return listaTymczasowa;
    }

    public java.util.ArrayList<Paczka> LstAdjnodeByName(String RncName, String NodebName)throws NBIAnsException, IOException //LST ADJNODE: LSTTYPE=BYNAME
    {

        //String odpa=north.make("REG NE:NAME="+RncName);
        //System.out.println(odpa);
        //String lst =north.make("LST ADJNODE: LSTTYPE=BYNAME, NAME=\""+NodebName+"\",LSTFORMAT=VERTICAL");
        String lst = north.make(RncName, "LST ADJNODE: LSTTYPE=BYNAME, NAME=\"" + NodebName + "\"");
        //String lst =north.make("LST ADJNODE:NAME=\""+NodebName+"\",LSTFORMAT=VERTICAL");
        //System.out.println(lst);
        NPack NPCAK = new NPack(lst);//.split("\n"),new String[]{"LST ADJNODE:"},new String[]{"List Adjacent Node"},new String[]{"---    END"});
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        if (listaTymczasowa.size() > 0); else
        {
            System.out.println(lst);
        }
        return listaTymczasowa;
    }
    /*#################################################################################*/
    public java.util.ArrayList<Paczka> LstImalink(String NodeName) throws NBIAnsException, IOException//LST IMALNK:
    {
        if (NodeName != null)
        {
            NodeName = NodeName.replaceAll("\"", "");
            //String regodp=north.make("REG NE:NAME=\""+NodeName+"\"");

            String lst = north.make(NodeName, "LST IMALNK:");

            NPack NPCAK = new NPack(lst.split("\n"), new String[]
                    {
                        "LST IMALNK:"
                    }, new String[]
                    {
                        "IMA Link Data"
                    }, new String[]
                    {
                        "---    END"
                    });
            java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
            if (!(listaTymczasowa.size() > 0))
            {
                NPCAK = new NPack(lst.split("\n"), new String[]{"LST IMALNK:"}, new String[]{"List IMA Link Configuration"}, new String[]{"---    END"});
                listaTymczasowa = NPCAK.getAllPacks();
            }

            if (!(listaTymczasowa.size() > 0))
            {
                //lst =north.make("LST IMALNK:");
                NPCAK = new NPack(lst);
                listaTymczasowa = NPCAK.getAllPacks();
                //System.out.println(lst);
            }

            if (listaTymczasowa.size() > 0); else
            {
                System.out.println(lst);
            }
            return listaTymczasowa;
        }
        else
        {
            return null;
        }
    }

    public Paczka lstSaalnkn(String RncName, String srn, String sn, String saalId) throws NBIAnsException, IOException//LST SAALNKN:
    {
        //north.make("REG NE:NAME=\""+ RncName+"\"");
        String lst = north.make(RncName, "LST SAALLNK: SRN=" + srn + ", SN=" + sn + ", SAALLNKN=" + saalId + ",LSTFORMAT=VERTICAL");
        NPack NPCAK = new NPack(lst.split("\n"), new String[]{"LST SAALLNK:"}, new String[]{"List SAAL Link"}, new String[]{"---    END"});
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        //System.out.println(lst);
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa.get(0);
        }
        else
        {
            System.out.println(lst);
            return null;
        }
    }

    public Paczka lstImalnk(String RncName, String srn, String sn, String imalIndex) throws NBIAnsException, IOException//LST IMALNK:
    {
        //north.make("REG NE:NAME=\""+ RncName+"\"");
        String lst = north.make(RncName, "LST IMALNK:SRN=" + srn + ",SN=" + sn + ",IMALNKN=" + imalIndex);
        NPack NPCAK = new NPack(lst);//.split("\n"),new String[]{"LST SAALLNK:"},new String[]{"List SAAL Link"},new String[]{"---    END"});
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        //System.out.println(lst);
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa.get(0);
        }
        else
        {
            System.out.println(lst);
            return null;
        }
    }

    public java.util.ArrayList<Paczka> lstSaalnknOnNode(String NodeName) throws NBIAnsException, IOException//LST SAALNKN: ON NODE SIDE
    {
        //String regOdp=north.make("REG NE:NAME=\""+ NodeName+"\"");
        String lst = north.make(NodeName, "LST SAALLNK:");

        NPack NPCAK = new NPack(lst.split("\n"), new String[]{"LST SAALLNK:"}, new String[]{"SAAL Link Data"}, new String[]{"---    END"               });
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        //System.out.println(lst);
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa;
        } else
        {
            NPCAK = new NPack(lst.split("\n"), new String[]
                    {
                        "LST SAALLNK:"
                    }, new String[]
                    {
                        "List SAAL Link Configuration"
                    }, new String[]
                    {
                        "---    END"
                    });
            listaTymczasowa = NPCAK.getAllPacks();
        }
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa;
        } else
        {
            System.out.println(lst);
            return null;
        }

    }

    public Paczka LstAll2Path(String RncName, String ani, String pathid) throws NBIAnsException, IOException//LST AAL2PATH:
    {
        //north.make("REG NE:NAME="+RncName);
        String lst = north.make(RncName, "LST AAL2PATH: ANI=" + ani + ", PATHID=" + pathid + ",LSTFORMAT=VERTICAL");
        NPack NPCAK = new NPack(lst.split("\n"), new String[]
                {
                    "LST AAL2PATH:"
                }, new String[]
                {
                    "List AAL2 Path"
                }, new String[]
                {
                    "---    END"
                });
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa.get(0);
        } else
        {
            System.out.println(lst);
            return null;
        }
    }

    public Paczka LstImagrp(String RncName, String imagrp) throws NBIAnsException, IOException//LST IMAGRP:BYIMAGRPN
    {
        //north.make("REG NE:NAME="+RncName);
        String lst = north.make(RncName, "LST IMAGRP:IMAGRPN=" + imagrp);
        NPack NPCAK = new NPack(lst);//.split("\n"),new String[]{"LST AAL2PATH:"},new String[]{"List AAL2 Path"},new String[]{"---    END"});
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa.get(0);
        } else
        {
            System.out.println(lst);
            return null;
        }
    }

    public Paczka LstCmctrl(String nodeName) throws NBIAnsException, IOException//LST CMCTRL:
    {
        nodeName = nodeName.replaceAll("\"", "");
        //north.make("REG NE:NAME=\""+nodeName+"\"");
        String lst = north.make(nodeName, "LST CMCTRL:");
        //System.out.println(lst);
        NPack NPCAK = new NPack(lst.split("\n"), new String[]
                {
                    "LST CMCTRL:"
                }, new String[]
                {
                    "-------"
                }, new String[]
                {
                    "---    END"
                });
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa.get(0);
        } else
        {
            System.out.println(lst);
            return null;
        }
    }

    public java.util.ArrayList<Paczka> LstAll2Path(String RncName, String ani) throws NBIAnsException, IOException//LST AAL2PATH:
    {
        //north.make("REG NE:NAME="+RncName);
        String lst = north.make(RncName, "LST AAL2PATH: ANI=" + ani + ",LSTFORMAT=HORIZONTAL");
        NPack NPCAK = new NPack(lst);//.split("\n"),new String[]{"LST AAL2PATH:"},new String[]{"List AAL2 Path"},new String[]{"---    END"});
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa;//.get(0);
        } else
        {
            System.out.println(lst);
            return null;
        }
    }
    public String CellBarrIsAccess(String BtsName,String CellName)throws NBIAnsException, IOException
    {
		Paczka tmp=LST_GCELLIDLEBASIC(BtsName,CellName);
		String cellBarrAcccess=tmp.getWartosc("Cell Bar Access");
		if (cellBarrAcccess!=null)
		{
			return cellBarrAcccess;
		}
		else
			return "";
    }
    public Paczka LST_GCELLIDLEBASIC(String BscName,String CellName)throws NBIAnsException, IOException									//LST GCELL:
    {

		String lstUcell=north.make(BscName,"LST GCELLIDLEBASIC:IDTYPE=BYNAME,CELLNAME=\""+CellName+"\",LSTFORMAT=VERTICAL");
		NPack NPCAK=new NPack(lstUcell.split("\n"),new String[]{"LST GCELLIDLEBASIC:"},new String[]{"List Idle Basic Parameters of Cell"},new String[]{"---    END"});
		java.util.ArrayList<Paczka> listaTymczasowa=NPCAK.getAllPacks();

		if(listaTymczasowa!=null&&listaTymczasowa.size()>0)
			return listaTymczasowa.get(0);
		else{
			System.out.println(lstUcell);
			return null;
		}
    }

    public java.util.ArrayList<Paczka> LstIpPath(String RncName, String ani) throws NBIAnsException, IOException//LST IPPATH:
    {
        //north.make("REG NE:NAME="+RncName);
        String lst = north.make(RncName, "LST IPPATH: ANI=" + ani + ",LSTFORMAT=HORIZONTAL");
        NPack NPCAK = new NPack(lst);//.split("\n"),new String[]{"LST AAL2PATH:"},new String[]{"List AAL2 Path"},new String[]{"---    END"});
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa;//.get(0);
        } else
        {
            System.out.println(lst);
            return null;
        }
    }

    public java.util.ArrayList<Paczka> lstAll2PathOnNode(String NodeName) throws NBIAnsException, IOException//LST AAL2PATH: ON NODE SIDE
    {
        //String odpReg=north.make("REG NE:NAME=\""+NodeName+"\"");

        String lst = north.make(NodeName, "LST AAL2PATH:");
        NPack NPCAK = new NPack(lst.split("\n"), new String[]
                {
                    "LST AAL2PATH:"
                }, new String[]
                {
                    "AAL2 Path Data"
                }, new String[]
                {
                    "---    END"
                });
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        //System.out.println(lst);
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa;
        } else
        {
            lst = "";
            //odpReg=north.make("REG NE:NAME=\""+NodeName+"\"");
            lst = north.make(NodeName, "LST AAL2PATH:");
            //NPCAK=new NPack(lst.split("\n"),new String[]{"LST AAL2PATH:"},new String[]{"List AAL2 Path Configuration"},new String[]{"---    END"});
            NPCAK = new NPack(lst);
            listaTymczasowa = NPCAK.getAllPacks();
        }
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa;
        } else
        {
            System.out.println(lst);
            return null;
        }

    }

    public java.util.ArrayList<Paczka> lstIppathOnNode(String NodeName) throws NBIAnsException, IOException//LST IPPATH: ON NODE SIDE
    {
        NodeName = NodeName.replaceAll("\"", "");
        //String odpReg=north.make("REG NE:NAME=\""+NodeName+"\"");

        String lst = north.make(NodeName, "LST IPPATH:");
        NPack NPCAK = new NPack(lst.split("\n"), new String[]
                {
                    "LST IPPATH:"
                }, new String[]
                {
                    "-----"
                }, new String[]
                {
                    "---    END"
                });
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        //System.out.println(lst);
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa;
        } else
        {
            NPCAK = new NPack(lst);
            listaTymczasowa = NPCAK.getAllPacks();
            if (listaTymczasowa != null && listaTymczasowa.size() > 0)
                return listaTymczasowa;
            else
                System.out.println(lst);
            return null;
        }

    }

    public java.util.ArrayList<Paczka> lstIppmOnNode(String NodeName) throws NBIAnsException, IOException//LST IPPM: ON NODE SIDE
    {
        NodeName = NodeName.replaceAll("\"", "");
        //String odpReg=north.make("REG NE:NAME=\""+NodeName+"\"");

        String lst = north.make(NodeName, "LST IPPM:");
        if (tool.zawiera(lst, "REG_ERR"))
        {
            return null;
        } else
        {
            NPack NPCAK = new NPack(lst.split("\n"), new String[]
                    {
                        "LST IPPM:"
                    }, new String[]
                    {
                        "-----"
                    }, new String[]
                    {
                        "---    END"
                    });
            java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
            //System.out.println(lst);
            if (listaTymczasowa != null && listaTymczasowa.size() > 0)
            {
                return listaTymczasowa;
            } else
            {
                System.out.println(lst);
                return new java.util.ArrayList<Paczka>();
            }
        }

    }

    public java.util.ArrayList<Paczka> dspIppmsessionOnNode(String NodeName) throws NBIAnsException, IOException//DSP IPPMSESSION: ON NODE SIDE
    {
        NodeName = NodeName.replaceAll("\"", "");

        String lst = north.make(NodeName, "DSP IPPMSESSION:");
        if (tool.zawiera(lst, "REG_ERR"))
        {
            return null;
        } else
        {
            NPack NPCAK = new NPack(lst);//.split("\n"),new String[]{"LST IPPM:"},new String[]{"-----"},new String[]{"---    END"});
            java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
            //System.out.println(lst);
            if (listaTymczasowa != null && listaTymczasowa.size() > 0)
            {
                return listaTymczasowa;
            } else
            {
                System.out.println(lst);
                return new java.util.ArrayList<Paczka>();
            }
        }

    }

    public String getNodebNameById(String RncName, String NodeId)throws NBIAnsException, IOException
    {
        //String odpReg=north.make("REG NE:NAME=\""+RncName+"\"");
        String lst = north.make(RncName, "DSP UNODEB: DSPT=BYID, NodeBId=" + NodeId);
        String nodebName = "#";
        String[] NodeLines = tool.getLinia(new String[]
                {
                    "NodeB name", "="
                }, lst);
        if (NodeLines != null && NodeLines.length > 0)
        {
            nodebName = tool.getTokens(NodeLines[0], "2", "2", "=").trim();
        }

        return nodebName;
    }

    public java.util.ArrayList<Paczka> lstLocellOnNode(String NodeName) throws NBIAnsException, IOException//LST AAL2PATH: ON NODE SIDE
    {

        String lst = north.make(NodeName, "LST LOCELL: MODE=ALLLOCALCELL");
        NPack NPCAK = new NPack(lst);//.split("\n"),new String[]{"LST AAL2PATH:"},new String[]{"AAL2 Path Data"},new String[]{"---    END"});
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        //System.out.println(lst);
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa;
        } else
        {
            System.out.println(lst);
            return null;
        }
    }

    public java.util.ArrayList<Paczka> lstLocell(String RncName, String NodeName) throws NBIAnsException, IOException//LST AAL2PATH: ON RNC SIDE
    {
        //String odpReg=north.make("REG NE:NAME=\""+RncName+"\"");
        String lst = north.make(RncName, "LST ULOCELL:NODEBNAME=\"" + NodeName + "\",LSTFORMAT=HORIZONTAL");
        NPack NPCAK = new NPack(lst);//.split("\n"),new String[]{"LST ULOCELL:"},new String[]{"-----"},new String[]{"---    END"});
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        //System.out.println(lst);
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa;
        } else
        {
            System.out.println(lst);
            return null;
        }
    }

    public Paczka lstSpecificLocell(String NodeName, String locellId)throws NBIAnsException, IOException
    {
        //String regOdp=north.make("REG NE:NAME="+NodeName);

        String lstUcell = north.make(NodeName, "LST LOCELL: MODE=LOCALCELL, LOCELL=" + locellId);

        NPack NPCAK = new NPack(lstUcell.split("\n"), new String[]
                {
                    "LST LOCELL:"
                }, new String[]
                {
                    "------"
                }, new String[]
                {
                    "---    END"
                });
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();

        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa.get(0);
        } else
        {
            System.out.println(lstUcell);
            return null;
        }
    }

    
    public int maxNrOfLocellOldVer(String NodeName)throws NBIAnsException, IOException															//
	{
		//String regOdp=north.make("REG NE:NAME="+NodeName,"---    END","n");

		String lstUcell=north.make(NodeName,"DSP LICENSE:");


                String[] ttt=lstUcell.split("License Control Item Info");
		NPack NPCAK=new NPack(ttt[1]);//.split("\n"),new String[]{"DSP LICENSE"},new String[]{"------"},new String[]{"---    END"});
		java.util.ArrayList<Paczka> listaTymczasowa=NPCAK.getAllPacks();

		if(listaTymczasowa!=null&&listaTymczasowa.size()>0)
		{
			/*if(tool.isNumber(listaTymczasowa.get(0).getWartosc("Max Local Cell")))
				return Integer.parseInt(listaTymczasowa.get(0).getWartosc("Max Local Cell"));
			else
			{
				System.out.println("**"+lstUcell);
				String[] licencjeLinie=tool.getLinia(new String[]{"Local Cell Num "},lstUcell);
				if(licencjeLinie!=null&&licencjeLinie.length>0)
				{
					String intStr=tool.getTokens(licencjeLinie[0],"7","7").trim();
					if(tool.isNumber(intStr))
						return Integer.parseInt(intStr);
					else
					{
						System.out.println("**"+lstUcell);
						return -1;
					}

				}
				else
				{
					System.out.println("**"+lstUcell);
					return -1;
				}
			}*/
			boolean found=false;
			String nrLicencji="";
			for(int i=0;i<listaTymczasowa.size()&&!found;i++)
			{
				Paczka tmp=listaTymczasowa.get(i);
				nrLicencji=tmp.getWartosc("Local Cell Num");
				if(nrLicencji.equals(""))
					;
				else
				 found=true;


			}
			if(tool.isNumber(nrLicencji))
						return Integer.parseInt(nrLicencji);
			else
			{
				System.out.println("**"+lstUcell);
				return -1;
			}

		}
		else
		{
			
			return -1;
		}
	}

    	public int maxNrOfLocell(String NodeName)throws NBIAnsException, IOException															//
	{
		String lstUcell=north.make(NodeName,"DSP LICENSE:");

		String[] licencjeLinie=tool.getLinia(new String[]{"Local Cell Num"},lstUcell);
				if(licencjeLinie!=null&&licencjeLinie.length>0)
				{
					String intStr=tool.getTokens(licencjeLinie[0],"7","7").trim();
					if(tool.isNumber(intStr))
						return Integer.parseInt(intStr);
					else
					{
						System.out.println("**"+lstUcell);
						return -1;
					}

				}
				else
				{
					System.out.println("**"+lstUcell);
					return -1;
				}

	}
    public String softwareVersion(String NodeName)throws NBIAnsException, IOException //LST SOFTWARE:
    {
        String softVer = "";
        //String regOdp=north.make("REG NE:NAME=\""+NodeName+"\"");
        String lst = north.make(NodeName, "LST SOFTWARE:");
        NPack NPCAK = new NPack(lst);
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            for (int p = 0; p < listaTymczasowa.size(); p++)
            {
                Paczka pak = listaTymczasowa.get(p);
                String Software_Version = pak.getWartosc("Software Version");
                String storArea = pak.getWartosc("Storage Area");
                if (storArea != null && storArea.trim().equals("Main Area"))
                {
                    if (Software_Version != null)
                    {
                        softVer = Software_Version.trim();
                    }
                }
            }
        } else
        {
            //bledy.dopisz("name="+NodeName+" nie rozpoznano soft ver\n");
            //bledy.dopisz(regOdp+"\n"+lst+"\n");
            System.out.println(lst);
        }
        return softVer;
    }

    public boolean isRAN13(String NodeName)throws NBIAnsException, IOException
    {
        String sftVer = softwareVersion(NodeName);
        if (tool.zawiera(sftVer, "R013"))
        {
            return true;
        } else
        {
            return false;
        }
    }

    public boolean isRAN12(String NodeName)throws NBIAnsException, IOException
    {
        String sftVer = softwareVersion(NodeName);
        if (tool.zawiera(sftVer, "R012"))
        {
            return true;
        } else
        {
            return false;
        }
    }

    public boolean isRAN15(String NodeName)throws NBIAnsException, IOException
    {
        String sftVer = softwareVersion(NodeName);
        if (tool.zawiera(sftVer, "R008"))
        {
            return true;
        } else
        {
            return false;
        }
    }

    public Paczka LstAdjmap(String RncName, String ani)throws NBIAnsException, IOException
    {
        //north.make("REG NE:NAME="+RncName);
        String lst = north.make(RncName, "LST ADJMAP:ANI=" + ani);
        NPack NPCAK = new NPack(lst);//.split("\n"),new String[]{"LST AAL2PATH:"},new String[]{"List AAL2 Path"},new String[]{"---    END"});
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();

        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            //System.out.println("lst adjmap packs.size="+listaTymczasowa.size());
            return listaTymczasowa.get(0);
        } else
        {
            System.out.println(lst);
            return null;
        }
    }

    public java.util.ArrayList<Paczka> lstIubcpOnNode(String NodeName) throws NBIAnsException, IOException//LST IUBCP: ON NODE SIDE
    {
        //north.make("REG NE:NAME=\""+NodeName+"\"");
        String lst = north.make(NodeName, "LST IUBCP:");
        NPack NPCAK = new NPack(lst.split("\n"), new String[]
                {
                    "LST IUBCP:"
                }, new String[]
                {
                    "NCP/CCP Data"
                }, new String[]
                {
                    "---    END"
                });
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa;
        } else
        {
            NPCAK = new NPack(lst.split("\n"), new String[]
                    {
                        "LST IUBCP:"
                    }, new String[]
                    {
                        "List IUB Control Port Configuration"
                    }, new String[]
                    {
                        "---    END"
                    });
            listaTymczasowa = NPCAK.getAllPacks();
        }
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa;
        } else
        {
            System.out.println(lst);
            return null;
        }
    }

    public java.util.ArrayList<Paczka> lstIubcpOnRNC(String rncName, String NodeName) throws NBIAnsException, IOException//LST IUBCP: ON NODE SIDE
    {
        //north.make("REG NE:NAME=\""+rncName+"\"");
        String lst = north.make(rncName, "LST UIUBCP: NodeBName=\"" + NodeName + "\"");
        NPack NPCAK = new NPack(lst);//.split("\n"),new String[]{"LST IUBCP:"},new String[]{"NCP/CCP Data"},new String[]{"---    END"});
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();

        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa;
        } else
        {
            System.out.println(lst);
            return null;
        }
    }

    public Paczka lstAal2NodeOnNode(String NodeName) throws NBIAnsException, IOException//LST AAL2NODE: ON NODE SIDE
    {
        //north.make("REG NE:NAME=\""+NodeName+"\"");
        String lst = north.make(NodeName, "LST AAL2NODE:");

        NPack NPCAK = new NPack(lst.split("\n"), new String[]
                {
                    "LST AAL2NODE:"
                }, new String[]
                {
                    "AAL2 Node Data"
                }, new String[]
                {
                    "---    END"
                });
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa.get(0);
        } else
        {
            NPCAK = new NPack(lst.split("\n"), new String[]
                    {
                        "LST AAL2NODE:"
                    }, new String[]
                    {
                        "List AAL2 Node Configuration"
                    }, new String[]
                    {
                        "---    END"
                    });
            listaTymczasowa = NPCAK.getAllPacks();
        }
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa.get(0);
        } else
        {

            System.out.println(lst);
            return null;
        }
    }

    public String CellIsUnbloced(String RncName, String cellId) throws NBIAnsException, IOException//cell is Blocked/Unblocekd
    {
        //DSP UCELL: DSPT=BYCELL,CELLID=65337,LSTFORMAT=VERTICAL
        //north.make("REG NE:NAME="+RncName);
        String dspCell = north.make(RncName, "DSP UCELL: DSPT=BYCELL,CELLID=" + cellId + ",LSTFORMAT=VERTICAL");
        String[] Administrative_State = tool.getLinia(new String[]
                {
                    "Administrative state", "="
                }, dspCell);
        if (Administrative_State != null && Administrative_State.length > 0)
        {
            /*if (tool.getTokens(Administrative_State[0], "2", "2", "=").trim().equalsIgnoreCase("Unblocked"))
            {
                return "UNBLOCKED";
            } else
            {
                if (tool.getTokens(Administrative_State[0], "2", "2", "=").trim().equalsIgnoreCase("Blocked"))
                {
                    return "BLOCKED";
                } else
                {
                    System.out.println(Administrative_State[0] + " nierozpoznano");
                    return dspCell;
                }
            }*/
            return tool.getTokens(Administrative_State[0], "2", "2", "=").trim().toUpperCase();
        } else
        {
            System.out.println(dspCell + " nierozpoznano");
            return dspCell;
        }
    }

    public String CellStateExplanation(String RncName, String cellId) throws NBIAnsException, IOException//state explanation
    {
        //DSP UCELL: DSPT=BYCELL,CELLID=65337,LSTFORMAT=VERTICAL
        //north.make("REG NE:NAME="+RncName);
        String dspCell = north.make(RncName, "DSP UCELL: DSPT=BYCELL,CELLID=" + cellId + ",LSTFORMAT=VERTICAL");
        String[] Administrative_State = tool.getLinia(new String[]
                {
                    "State explanation", "="
                }, dspCell);
        //System.out.println(dspCell);
        if (Administrative_State != null && Administrative_State.length > 0)
        {
            /*if(tool.getTokens(Administrative_State[0],"2","2","=").trim().equalsIgnoreCase("Unblocked"))
            return "UNBLOCKED";
            else if(tool.getTokens(Administrative_State[0],"2","2","=").trim().equalsIgnoreCase("Blocked"))
            return "BLOCKED";
            else{
            System.out.println(Administrative_State[0]+" nierozpoznano");
            return dspCell;
            }*/
            return tool.getTokens(Administrative_State[0], "2", "2", "=").trim();
        } else
        {
            System.out.println(dspCell + " nierozpoznano");
            return dspCell;
        }
    }

    public String CellStateBTSExplanation(String BscName, String cellName) throws NBIAnsException, IOException//state explanation #for bts
    {
        //DSP UCELL: DSPT=BYCELL,CELLID=65337,LSTFORMAT=VERTICAL
        //north.make("REG NE:NAME=\""+BtsName+"\"");
        //DSP GCELLSTAT: IDTYPE=BYNAME, CELLNAMELST="1167211G_RYC3310A_O_SKLADOWA";
        String dspCell = north.make(BscName, "DSP GCELLSTAT: IDTYPE=BYNAME, CELLNAMELST=\"" + cellName + "\",LSTFORMAT=VERTICAL");
        String[] Administrative_State = tool.getLinia(new String[]
                {
                    "Channel Fault ", "="
                }, dspCell);
        if (Administrative_State != null && Administrative_State.length > 0)
        {
            /*if(tool.getTokens(Administrative_State[0],"2","2","=").trim().equalsIgnoreCase("Unblocked"))
            return "UNBLOCKED";
            else if(tool.getTokens(Administrative_State[0],"2","2","=").trim().equalsIgnoreCase("Blocked"))
            return "BLOCKED";
            else{
            System.out.println(Administrative_State[0]+" nierozpoznano");
            return dspCell;
            }*/
            return tool.getTokens(Administrative_State[0], "2", "2", "=").trim();
        } else
        {
            System.out.println(dspCell + " nierozpoznano");
            return dspCell;
        }
    }

    public String IsBlocked2Gcell(String BscName, String cellName) throws NBIAnsException, IOException//state explanation #for bts
    {
        //DSP UCELL: DSPT=BYCELL,CELLID=65337,LSTFORMAT=VERTICAL
        //north.make("REG NE:NAME=\""+BtsName+"\"");
        //DSP GCELLSTAT: IDTYPE=BYNAME, CELLNAMELST="1167211G_RYC3310A_O_SKLADOWA";
        String dspCell = north.make(BscName, "DSP GCELLSTAT: IDTYPE=BYNAME, CELLNAMELST=\"" + cellName + "\",LSTFORMAT=VERTICAL");
        String[] Administrative_State = tool.getLinia(new String[]
        {
                    "Blocked Manually", "="
                }, dspCell);
        if (Administrative_State != null && Administrative_State.length > 0)
        {
            /*if(tool.getTokens(Administrative_State[0],"2","2","=").trim().equalsIgnoreCase("Unblocked"))
            return "UNBLOCKED";
            else if(tool.getTokens(Administrative_State[0],"2","2","=").trim().equalsIgnoreCase("Blocked"))
            return "BLOCKED";
            else{
            System.out.println(Administrative_State[0]+" nierozpoznano");
            return dspCell;
            }*/
            return tool.getTokens(Administrative_State[0], "2", "2", "=").trim();
        } else
        {
            System.out.println(dspCell + " nierozpoznano");
            return dspCell;
        }
    }
    public String getNodeBType(String nodebName) throws NBIAnsException, IOException//Nodeb type LST NODEBTYPE:
    {
        if (nodebName != null)
        {

            String makeOut = north.make(nodebName, "LST NODEBTYPE:");
            String[] NodebType = tool.getLinia(new String[]
                    {
                        "NodeB Type", "="
                    }, makeOut);
            if (NodebType != null && NodebType.length > 0)
            {
                return tool.getTokens(NodebType[0], "2", "2", "=").trim();
            } else
            {
		makeOut = north.make(nodebName, "LST NODE:");
		NodebType = tool.getLinia(new String[]
                    {
                        "Product Type", "="
                    }, makeOut);
		if (NodebType != null && NodebType.length > 0)
		{
		    return tool.getTokens(NodebType[0], "2", "2", "=").trim();
		}
                return makeOut;
            }
        } else
        {
            return "RETCODE = x  nodebName=null";
        }
    }

    public Paczka LST_GCELL(String BtsName, String CellName) throws NBIAnsException, IOException//LST GCELL:
    {

        String lstUcell = north.make(BtsName, "LST GCELL: IDTYPE=BYNAME, CELLNAME=\"" + CellName + "\",LSTFORMAT=VERTICAL;");
        NPack NPCAK = new NPack(lstUcell.split("\n"), new String[]
                {
                    "LST GCELL:"
                }, new String[]
                {
                    "List GSM Cell at BSC"
                }, new String[]
                {
                    "---    END"
                });
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();

        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa.get(0);
        } else
        {
            return null;
        }
    }
    public java.util.ArrayList<Paczka> LST_GCELL_ALL(String BtsName) throws NBIAnsException, IOException//LST GCELL:
    {

        String lstUcell = north.make(BtsName, "LST GCELL: IDTYPE=BYNAME, LSTFORMAT=VERTICAL;");
        NPack NPCAK = new NPack(lstUcell.split("\n"), new String[]
                {
                    "LST GCELL:"
                }, new String[]
                {
                    "List GSM Cell at BSC"
                }, new String[]
                {
                    "---    END"
                });
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();

        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa;
        } else
        {
            return null;
        }
    }

    public java.util.ArrayList<Paczka> LST_GTRX(String BscName, String CellName) throws NBIAnsException, IOException//LST GTRX: for specyfic Cell
    {

        String lstUcell = north.make(BscName, "LST GTRX: IDTYPE=BYNAME, CELLNAME=\"" + CellName + "\",LSTFORMAT=VERTICAL");
        NPack NPCAK = new NPack(lstUcell.split("\n"), new String[]
                {
                    "LST GTRX:"
                }, new String[]
                {
                    "List TRX"
                }, new String[]
                {
                    "---    END"
                });
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();

        return listaTymczasowa;
    }

    public java.util.ArrayList<Paczka> LST_GTRX(String BtsName) throws NBIAnsException, IOException//LST GTRX: for whole bsc
    {


        String lstUcell = north.make(BtsName, "LST GTRX: IDTYPE=BYNAME,LSTFORMAT=VERTICAL");

        NPack NPCAK = new NPack(lstUcell.split("\n"), new String[]
                {
                    "LST GTRX:"
                }, new String[]
                {
                    "List TRX"
                }, new String[]
                {
                    "---    END"
                });
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        int size = listaTymczasowa.size() - 1;
        for (int i = 0; i < listaTymczasowa.size(); i++)
        {
            for (int j = 0; j < size; j++)
            {
                Paczka tymczPaka1 = listaTymczasowa.get(j);
                Paczka tymczPaka2 = listaTymczasowa.get(j + 1);
                int trxInd1 = Integer.parseInt(tymczPaka1.getWartosc("TRX ID"));
                int trxInd2 = Integer.parseInt(tymczPaka2.getWartosc("TRX ID"));
                if (trxInd1 > trxInd2)
                {
                    listaTymczasowa.set(j, tymczPaka2);
                    listaTymczasowa.set(j + 1, tymczPaka1);
                }
            }
            size--;
        }

        return listaTymczasowa;
    }

    public java.util.ArrayList<Paczka> LST_TRXBIND2PHYBRD(String BscName, String CellName) throws NBIAnsException, IOException//LST TRXBIND2PHYBRD:IDTYPE=BYNAME,
    {

        String lstUcell = north.make(BscName, "LST TRXBIND2PHYBRD: IDTYPE=BYNAME, CELLNAME=\"" + CellName + "\",LSTFORMAT=VERTICAL");

        NPack NPCAK = new NPack(lstUcell.split("\n"), new String[]
                {
                    "LST TRXBIND2PHYBRD:"
                }, new String[]
                {
                    "List Binding between Logical TRX and Channel on TRX Board"
                }, new String[]
                {
                    "---    END"
                });
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        return listaTymczasowa;
    }

    public Paczka LST_TRXBIND2PHYBRDbyTRX(String BscName, String TRXID) throws NBIAnsException, IOException//LST TRXBIND2PHYBRD:IDTYPE=BYID,
    {

        String lstUcell = north.make(BscName, "LST TRXBIND2PHYBRD: IDTYPE=BYID, TRXID=" + TRXID + ",LSTFORMAT=VERTICAL");
        System.out.println(lstUcell);
        NPack NPCAK = new NPack(lstUcell.split("\n"), new String[]
                {
                    "LST TRXBIND2PHYBRD:"
                }, new String[]
                {
                    "List Binding between Logical TRX and Channel on TRX Board"
                }, new String[]
                {
                    "---    END"
                });
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa.get(0);
        } else
        {
            return null;
        }
    }

    public java.util.ArrayList<Paczka> LST_GTRXDEV(String BscName, String CellName) throws NBIAnsException, IOException//LST GTRXDEV: IDTYPE=BYNAME,
    {

        String lstUcell = north.make(BscName, "LST GTRXDEV: IDTYPE=BYNAME, CELLNAME=\"" + CellName + "\",LSTFORMAT=VERTICAL");
        NPack NPCAK = new NPack(lstUcell.split("\n"), new String[]
                {
                    "LST GTRXDEV:"
                }, new String[]
                {
                    "List Device Attributes of TRX"
                }, new String[]
                {
                    "---    END"
                });
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        return listaTymczasowa;
    }

    public Paczka LST_GCELLFREQ(String BscName, String CellName) throws NBIAnsException, IOException//LST GCELLFREQ: IDTYPE=BYNAME,
    {

        String lstUcell = north.make(BscName, "LST GCELLFREQ: IDTYPE=BYNAME, CELLNAME=\"" + CellName + "\",LSTFORMAT=VERTICAL");
        NPack NPCAK = new NPack(lstUcell.split("\n"), new String[]
                {
                    "LST GCELLFREQ:"
                }, new String[]
                {
                    "List Frequencies of Cell"
                }, new String[]
                {
                    "---    END"
                });
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();

        if (listaTymczasowa != null && listaTymczasowa.size() > 0 && listaTymczasowa.get(0) != null)
        {
            return listaTymczasowa.get(0);
        } else
        {
            return null;
        }
    }

    public Paczka LST_GCELLMAGRP(String BscName, String CellName) throws NBIAnsException, IOException//LST GCELLMAGRP: IDTYPE=BYNAME,
    {

        String lstUcell = north.make(BscName, "LST GCELLMAGRP: IDTYPE=BYNAME, CELLNAME=\"" + CellName + "\",LSTFORMAT=VERTICAL");
        //System.out.println(lstUcell);
        NPack NPCAK = new NPack(lstUcell.split("\n"), new String[]
                {
                    "LST GCELLMAGRP:"
                }, new String[]
                {
                    "List MA Group of Cell"
                }, new String[]
                {
                    "---    END"
                });
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa.get(0);
        } else
        {
            return null;
        }
    }

    public Paczka LST_ITELSHUTDOWN(String NodebName) throws NBIAnsException, IOException//LST ITELSHUTDOWN,
    {

        String lstUcell = north.make(NodebName, "LST ITELSHUTDOWN:");
        NPack NPCAK = new NPack(lstUcell.split("\n"), new String[]
                {
                    "LST ITELSHUTDOWN:"
                }, new String[]
                {
                    "-------"
                }, new String[]
                {
                    "---    END"
                });
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa.get(0);
        } else
        {
            return null;
        }
    }

    public Paczka LST_LOWPOWERPARA(String NodebName) throws NBIAnsException, IOException//LST LOWPOWERPARA
    {

        String lstUcell = north.make(NodebName, "LST LOWPOWERPARA:");
        NPack NPCAK = new NPack(lstUcell.split("\n"), new String[]
                {
                    "LST LOWPOWERPARA:"
                }, new String[]
                {
                    "-------"
                }, new String[]
                {
                    "---    END"
                });
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa.get(0);
        } else
        {
            return null;
        }
    }

    public java.util.ArrayList<Paczka> LST_GTRXHOP(String BscName, String CellName) throws NBIAnsException, IOException//LST GTRXHOP: IDTYPE=BYNAME,
    {

        String lstUcell = north.make(BscName, "LST GTRXHOP: IDTYPE=BYNAME, CELLNAME=\"" + CellName + "\",LSTFORMAT=VERTICAL");
        NPack NPCAK = new NPack(lstUcell.split("\n"), new String[]
                {
                    "LST GTRXHOP:"
                }, new String[]
                {
                    "List FH Type of TRX"
                }, new String[]
                {
                    "---    END"
                });
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        return listaTymczasowa;
    }

    public java.util.ArrayList<Paczka> LST_GTRXCHANHOP(String BscName, String CellName) throws NBIAnsException, IOException//LST GTRXCHANHOP: IDTYPE=BYNAME,
    {

        String lstUcell = north.make(BscName, "LST GTRXCHANHOP: IDTYPE=BYNAME, CELLNAME=\"" + CellName + "\",LSTFORMAT=VERTICAL");
        NPack NPCAK = new NPack(lstUcell.split("\n"), new String[]
                {
                    "LST GTRXCHANHOP:"
                }, new String[]
                {
                    "List FH Index and MAIO of Channel"
                }, new String[]
                {
                    "---    END"
                });
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        return listaTymczasowa;
    }

    public java.util.ArrayList<Paczka> LST_GTRXCHAN(String BtsName, String CellName) throws NBIAnsException, IOException//LST GTRXCHAN: IDTYPE=BYNAME,
    {
        String lstUcell = north.make(BtsName, "LST GTRXCHAN: IDTYPE=BYNAME, CELLNAME=\"" + CellName + "\",LSTFORMAT=VERTICAL");
        NPack NPCAK = new NPack(lstUcell.split("\n"), new String[]
                {
                    "LST GTRXCHAN:"
                }, new String[]
                {
                    "List Channel Attributes of TRX"
                }, new String[]
                {
                    "---    END"
                });
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        return listaTymczasowa;
    }

    public Paczka dspRRUTC(String NodebName, String cn, String srn, String sn) throws NBIAnsException, IOException//DSP RRUTC
    {

        String kommOdp = north.make(NodebName, "DSP RRUTC:CN=" + cn + ",SN=" + sn + ",SRN=" + srn + "");
        NPack NPCAK = new NPack(kommOdp.split("\n"), new String[]
                {
                    "DSP RRUTC:"
                }, new String[]
                {
                    "-------"
                }, new String[]
                {
                    "---    END"
                });
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        //System.out.println(kommOdp);
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa.get(0);
        }
        else
        {
            System.out.println(kommOdp);
            return null;
        }
    }

    public Paczka dspMTRUTC(String NodebName, String cn, String srn, String sn) throws NBIAnsException, IOException//DSP RRUTC
    {

        String kommOdp = north.make(NodebName, "DSP MTRUTC:SN=" + sn);
        NPack NPCAK = new NPack(kommOdp.split("\n"), new String[]
                {
                    "DSP MTRUTC:"
                }, new String[]
                {
                    "-------"
                }, new String[]
                {
                    "---    END"
                });
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        //System.out.println(kommOdp);
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa.get(0);
        }
        else
        {
            System.out.println(kommOdp);
            return null;
        }
    }

    public Paczka dspRRU(String NodebName, String cn, String srn, String sn) throws NBIAnsException, IOException//DSP RRU:
    {

        String kommOdp = north.make(NodebName, "DSP RRU:CN=" + cn + ",SN=" + sn + ",SRN=" + srn + "");
        NPack NPCAK = new NPack(kommOdp.split("\n"), new String[]
                {
                    "DSP RRU:"
                }, new String[]
                {
                    "-------"
                }, new String[]
                {
                    "---    END"
                });
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        //System.out.println(kommOdp);
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa.get(0);
        } else
        {
            System.out.println(kommOdp);
            return null;
        }
    }
            public java.util.ArrayList<Paczka> LstAdjmap(String RncName)throws NBIAnsException, IOException													//
	{

		String lst =north.make(RncName,"LST ADJMAP:");
		NPack NPCAK=new NPack(lst);//.split("\n"),new String[]{"LST AAL2PATH:"},new String[]{"List AAL2 Path"},new String[]{"---    END"});
		java.util.ArrayList<Paczka> listaTymczasowa=NPCAK.getAllPacks();

		if(listaTymczasowa!=null&&listaTymczasowa.size()>0)
		{
			//System.out.println("lst adjmap packs.size="+listaTymczasowa.size());
			return listaTymczasowa;
		}
		else
		{
			System.out.println(lst);
			return null;
		}
	}


        public Paczka LstDifpri(String NodeName)throws NBIAnsException, IOException													//
	{

		String lst =north.make(NodeName,"LST DIFPRI:");
               // System.out.println(lst);
		NPack NPCAK=new NPack(lst.split("\n"),new String[]{"LST DIFPRI:"},new String[]{"------------"},new String[]{"---    END"});
		java.util.ArrayList<Paczka> listaTymczasowa=NPCAK.getAllPacks();

		if(listaTymczasowa!=null&&listaTymczasowa.size()>0)
		{
			//System.out.println("lst adjmap packs.size="+listaTymczasowa.size());
			return listaTymczasowa.get(0);
		}
		else
		{
			System.out.println(lst);
			return null;
		}
	}

    public java.util.ArrayList<Paczka> dspRRUTC(String NodebName) throws NBIAnsException, IOException//DSP RRUTC
    {

        String kommOdp = north.make(NodebName, "DSP RRUTC:");
        NPack NPCAK = new NPack(kommOdp.split("\n"), new String[]{"DSP RRUTC:"}, new String[]{"-------"}, new String[]{"---    END"});
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        System.out.println(kommOdp);
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa;//.get(0);
        } else
        {
            System.out.println(kommOdp);
            return null;
        }
    }

    public java.util.ArrayList<Paczka> dspMTRUTC(String NodebName) throws NBIAnsException, IOException//DSP RRUTC
    {

        String kommOdp = north.make(NodebName, "DSP MTRUTC:");
        NPack NPCAK = new NPack(kommOdp.split("\n"), new String[]
                {
                    "DSP MTRUTC:"
                }, new String[]
                {
                    "-------"
                }, new String[]
                {
                    "---    END"
                });
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        System.out.println(kommOdp);
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa;//.get(0);
        } else
        {
            System.out.println(kommOdp);
            return null;
        }
    }

    public java.util.ArrayList<Paczka> dspRRU(String NodebName) throws NBIAnsException, IOException//DSP RRU:
    {
        String kommOdp = north.make(NodebName, "DSP RRU:");
        NPack NPCAK = new NPack(kommOdp);//.split("\n"),new String[]{"DSP RRU:"},new String[]{"-------"},new String[]{"---    END"});
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        //System.out.println(kommOdp);
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa;//.get(0);
        }
        else
        {
            System.out.println(kommOdp);
            return null;
        }
    }

    public Paczka LST_GCELLHOPTP(String BtsName, String CellName) throws NBIAnsException, IOException//LST GCELLHOPTP: IDTYPE=BYNAME,
    {

        String lstUcell = north.make(BtsName, "LST GCELLHOPTP: IDTYPE=BYNAME, CELLNAME=\"" + CellName + "\",LSTFORMAT=VERTICAL");
        NPack NPCAK = new NPack(lstUcell.split("\n"), new String[]
                {
                    "LST GCELLHOPTP:"
                }, new String[]
                {
                    "List FH Type of Cell"
                }, new String[]
                {
                    "---    END"
                });
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa.get(0);
        } else
        {
            return null;
        }
    }

    public Paczka LST_NODEBNAME(String NodebName)throws NBIAnsException, IOException //1)	LST NODEBNAME
    {
        //north.make("REG NE:NAME=\""+NodebName.replaceAll("\"","")+"\"");
        String lstUcell = north.make(NodebName.replaceAll("\"", ""), "LST NODEBNAME:");

        NPack NPCAK = new NPack(lstUcell.split("\n"), new String[]
                {
                    "LST NODEBNAME:"
                }, new String[]
                {
                    "------"
                }, new String[]
                {
                    "---    END"
                });
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa.get(0);
        } else
        {
            System.out.println(lstUcell);
            return null;
        }
    }

    public Paczka LST_SITE(String NodebName)throws NBIAnsException, IOException //1)	LST SITE
    {
        //north.make("REG NE:NAME=\""+NodebName.replaceAll("\"","")+"\"");
        String lstUcell = north.make(NodebName, "LST SITE:");


        NPack NPCAK = new NPack(lstUcell.split("\n"), new String[]
                {
                    "LST SITE:"
                }, new String[]
                {
                    "------"
                }, new String[]
                {
                    "---    END"
                });
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            System.out.println("LST SITE " + NodebName + "ok");
            return listaTymczasowa.get(0);
        } else
        {
            System.out.println(lstUcell);
            NPCAK = new NPack(lstUcell);

            listaTymczasowa = NPCAK.getAllPacks();
        }
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa.get(0);
        } else
        {
            System.out.println(lstUcell);
            return null;
        }
    }

    public boolean sendCommend(String komenda, String regName)throws NBIAnsException, IOException
    {
        if (komenda != null)
        {

            String komendaOdp = "";

            komendaOdp = north.make(regName, komenda);
            if (tool.zawiera(komendaOdp, "RETCODE = 0"))
            {
                System.out.println("\tRETCODE = 0  Execution succeeded");
                return true;
            } else
            {
                bledy.dopisz(komendaOdp + "\n");
                System.out.println(komendaOdp);
                return false;
            }
        }
        return false;
    }
    public String sendCommendWithAnswear(String regName,String komenda)throws NBIAnsException, IOException
    {
        return north.make(regName, komenda);
    }

    public boolean sendCommend(String komenda_regName)throws NBIAnsException, IOException
    {
        if (komenda_regName != null && !komenda_regName.equals(""))
        {
            String komenda = tool.getTokens(komenda_regName, "1", "1", "[{]");
            komenda = komenda.replaceAll("[{]", "");
            String regname = tool.getTokens(komenda_regName, "2", "2", "[{]");
            regname = regname.replaceAll("[{]", "");
            regname = regname.replaceAll("[}]", "");
            return sendCommend(komenda, regname);
        }
        return false;
    }

    public boolean sendCommend(java.util.ArrayList<String> komendy)throws NBIAnsException, IOException
    {
        for (int z = 0; z < komendy.size(); z++)
        {
            sendCommend(komendy.get(z));
        }
        return true;
    }
    public Paczka LstImagrp(String RncName,String srn,String sn,String imagrp)		throws NBIAnsException, IOException	//LST IMAGRP:BYIMAGRPN,SN,SRN
    {

		String lst =north.make(RncName,"LST IMAGRP:SRN="+srn+", SN="+sn+", IMAGRPN="+imagrp);
		NPack NPCAK=new NPack(lst);//.split("\n"),new String[]{"LST AAL2PATH:"},new String[]{"List AAL2 Path"},new String[]{"---    END"});
		java.util.ArrayList<Paczka> listaTymczasowa=NPCAK.getAllPacks();
		if(listaTymczasowa!=null&&listaTymczasowa.size()>0)
			return listaTymczasowa.get(0);
		else
		{
			System.out.println(lst);
			return null;
		}
	}
    public java.util.ArrayList<Paczka> lstSaalnkn(String RncName,String srn,String sn)throws NBIAnsException, IOException	//LST SAALNKN:
    {

		String lst=north.make(RncName,"LST SAALLNK: SRN="+srn+", SN="+sn+"");
		//System.out.println(lst);
		NPack NPCAK=new NPack(lst);//.split("\n"),new String[]{"LST SAALLNK:"},new String[]{"List SAAL Link"},new String[]{"---    END"});
		java.util.ArrayList<Paczka> listaTymczasowa=NPCAK.getAllPacks();
		//System.out.println(lst);
		if(listaTymczasowa!=null&&listaTymczasowa.size()>0)
			return listaTymczasowa;//.get(0);
		else
		{
			System.out.println(lst);
			return listaTymczasowa;
		}
	}

    public Paczka LstImaGrpOnNode(String NodeName)throws NBIAnsException, IOException
    {
		NodeName=NodeName.replaceAll("\"","");

		String lst =north.make(NodeName,"LST IMAGRP:");
		//System.out.println(lst);
		NPack NPCAK=new NPack(lst.split("\n"),new String[]{"LST IMAGRP:"},new String[]{"---------"},new String[]{"---    END"});
		java.util.ArrayList<Paczka> listaTymczasowa=NPCAK.getAllPacks();
		if(listaTymczasowa!=null&&listaTymczasowa.size()>0)
			return listaTymczasowa.get(0);
		else
		{
			System.out.println(lst);
			return null;
		}
	}
    public String GCellIsUnblocked(String BtsName,String cellName)throws NBIAnsException, IOException
    {
		//DSP UCELL: DSPT=BYCELL,CELLID=65337,LSTFORMAT=VERTICAL
		//north.make("REG NE:NAME=\""+BtsName+"\"","---    END","n");
		//DSP GCELLSTAT: IDTYPE=BYNAME, CELLNAMELST="1167211G_RYC3310A_O_SKLADOWA";
		String dspCell=north.make(BtsName,"DSP GCELLSTAT: IDTYPE=BYNAME, CELLNAMELST=\""+cellName+"\",LSTFORMAT=VERTICAL");
		String[] Administrative_State=tool.getLinia(new String[]{"Blocked Manually","="},dspCell);
		if(Administrative_State!=null&&Administrative_State.length>0)
		{
			/*if(tool.getTokens(Administrative_State[0],"2","2","=").trim().equalsIgnoreCase("Unblocked"))
				return "UNBLOCKED";
			else if(tool.getTokens(Administrative_State[0],"2","2","=").trim().equalsIgnoreCase("Blocked"))
				return "BLOCKED";
			else{
				System.out.println(Administrative_State[0]+" nierozpoznano");
				return dspCell;
			}*/
			return tool.getTokens(Administrative_State[0],"2","2","=").trim();
		}
		else
		{
			System.out.println(dspCell+" nierozpoznano");
			return dspCell;
		}
	}

    public Paczka LstNodebstatus(String NodebName) throws NBIAnsException, IOException//DSP RRU:
    {
        /* LST NODEBSTATUS: po stronie NodeB,
         * Parametry:
         * NE Type
         * NE Status
         * Start Time of Status Setting
         * End Time of Status Setting
         * Remark of Status Setting
         */
        String kommOdp = north.make(NodebName, "LST NODEBSTATUS:");
        NPack NPCAK = new NPack(kommOdp.split("\n"),new String[]{"LST NODEBSTATUS:"},new String[]{"------"},new String[]{"---    END"});
        java.util.ArrayList<Paczka> listaTymczasowa = NPCAK.getAllPacks();
        //System.out.println(kommOdp);
        if (listaTymczasowa != null && listaTymczasowa.size() > 0)
        {
            return listaTymczasowa.get(0);
        }
        else
        {
            System.out.println(kommOdp);
            return null;
        }
    }
    public java.util.ArrayList<Paczka> lstALLGext3Gcell(String RncBscName)throws NBIAnsException, IOException//,String EXT3GCELLNAME)
    {
		
		String lst =north.make(RncBscName,"LST GEXT3GCELL:");
		NPack NPCAK=new NPack(lst);
		java.util.ArrayList<Paczka> listaTymczasowa=NPCAK.getAllPacks();
		//System.out.println(lst);
		if(listaTymczasowa!=null&&listaTymczasowa.size()>0)
			return listaTymczasowa;//.get(0);
		else
		{
			System.out.println(lst);
			return null;
		}
	}

    public java.util.ArrayList<Paczka> lstALLUext3Gcell(String rncName)throws NBIAnsException, IOException//,String EXT2GCELLNAME)
    {
		
		String lst =north.make(rncName,"LST UEXT3GCELL:LSTFORMAT=HORIZONTAL");
		//LST UEXT2GCELL:LSTTYPE=ByGSMCellName,GSMCELLNAME="4459813G_BYD2102A_O_LOCHOWO";

		NPack NPCAK=new NPack(lst);
		java.util.ArrayList<Paczka> listaTymczasowa=NPCAK.getAllPacks();
		//System.out.println(lst);
		if(listaTymczasowa!=null&&listaTymczasowa.size()>0)
			return listaTymczasowa;//.get(0);
		else
		{
			System.out.println(lst);
			return null;
		}
	}
    public Paczka dspEthPort(String NodeName,String srn,String pn)throws NBIAnsException, IOException									//LST GCELL: LSTTYPE=CELLID
    {

		String lstUcell=north.make(NodeName,"DSP ETHPORT:SRN="+srn+",PN="+pn+",SBT=BASE_BOARD");
		NPack NPCAK=new NPack(lstUcell.split("\n"),new String[]{"DSP ETHPORT:"},new String[]{"-------"},new String[]{"---    END"});
		java.util.ArrayList<Paczka> listaTymczasowa=NPCAK.getAllPacks();
		//System.out.println(lstUcell);
		if(listaTymczasowa!=null&&listaTymczasowa.size()>0)
			return listaTymczasowa.get(0);
		else
		{
			System.out.println(lstUcell);
			return null;
		}
	}
    public java.util.ArrayList<Paczka> LstVlanClass(String NodebName)throws NBIAnsException, IOException									//DSP UCELL:
    {
	String lstUcell=north.make(NodebName,"LST VLANCLASS:");
	NPack NPCAK=new NPack(lstUcell.split("\n"),new String[]{"LST VLANCLASS:"},new String[]{"-------------"},new String[]{"---    END"});
	java.util.ArrayList<Paczka> listaTymczasowa=NPCAK.getAllPacks();
	if(listaTymczasowa.size()>0)
	    ;
	else
		System.out.println(lstUcell);
	return listaTymczasowa;
    }
    public Paczka lstVlanMap(String NodeName)throws NBIAnsException, IOException	//LST GCELL: LSTTYPE=CELLID
    {

		String lstUcell=north.make(NodeName,"LST VLANMAP:");
		NPack NPCAK=new NPack(lstUcell.split("\n"),new String[]{"LST VLANMAP:"},new String[]{"-------"},new String[]{"---    END"});
		java.util.ArrayList<Paczka> listaTymczasowa=NPCAK.getAllPacks();
		//System.out.println(lstUcell);
		if(listaTymczasowa!=null&&listaTymczasowa.size()>0)
			return listaTymczasowa.get(0);
		else
		{
			System.out.println(lstUcell);
			return null;
		}
	}
    public Paczka LST_BTS(String BscName,String BtsName)throws NBIAnsException, IOException	//LST BTS: LSTTYPE=BYBTSNAME
    {
		String lstUcell=north.make(BscName,"LST BTS: LSTTYPE=BYBTSNAME, BTSNAME=\""+BtsName+"\"");
		NPack NPCAK=new NPack(lstUcell);//.split("\n"),new String[]{"LST BTS:"},new String[]{"-------"},new String[]{"---    END"});
		java.util.ArrayList<Paczka> listaTymczasowa=NPCAK.getAllPacks();
		if(listaTymczasowa!=null&&listaTymczasowa.size()>0)
			return listaTymczasowa.get(0);
		else
		{
			System.out.println(lstUcell);
			return null;
		}
	}
    public Paczka LST_BTSIP(String BscName,String btsIp)throws NBIAnsException, IOException	//LST BTSIP: LSTTYPE=BYBTSNAME
    {
		String lstUcell=north.make(BscName,"LST BTSIP: IDTYPE=BYID, BTSID="+btsIp);
		NPack NPCAK=new NPack(lstUcell);//.split("\n"),new String[]{"LST BTS:"},new String[]{"-------"},new String[]{"---    END"});
		java.util.ArrayList<Paczka> listaTymczasowa=NPCAK.getAllPacks();
		if(listaTymczasowa!=null&&listaTymczasowa.size()>0)
			return listaTymczasowa.get(0);
		else
		{
			System.out.println(lstUcell);
			return null;
		}
	}
    public java.util.ArrayList<Paczka> lstBtsVlan(String BscName,String BtsId)throws NBIAnsException, IOException	//LST IUBCP: ON NODE SIDE
    {
	String lst =north.make(BscName,"LST BTSVLAN:IDTYPE=BYID,BTSID="+BtsId);
	NPack NPCAK=new NPack(lst);//.split("\n"),new String[]{"LST IUBCP:"},new String[]{"NCP/CCP Data"},new String[]{"---    END"});
	java.util.ArrayList<Paczka> listaTymczasowa=NPCAK.getAllPacks();
	if(listaTymczasowa!=null&&listaTymczasowa.size()>0)
	    return listaTymczasowa;
	else
	{
	    System.out.println(lst);
	    return null;
	}
    }
    public java.util.ArrayList<Paczka> dspAllUcell(String RncName)throws NBIAnsException, IOException
    {
	String lstUcell=north.make(RncName,"DSP UCELL: DSPT=BYCELL, LSTFORMAT=VERTICAL");
	NPack NPCAK=new NPack(lstUcell.split("\n"),new String[]{"DSP UCELL:"},new String[]{"Cell state information"},new String[]{"---    END"});

	java.util.ArrayList<Paczka> listaTymczasowa=NPCAK.getAllPacks();
	if(listaTymczasowa.size()>0)
	    ;
	else
	    System.out.println(lstUcell);
	return listaTymczasowa;
    }
}