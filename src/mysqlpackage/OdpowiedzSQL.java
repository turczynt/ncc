package mysqlpackage;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.sql.ResultSet;
import java.util.ArrayList;
import nbipackage.*;

/**
 *
 * @author turczyt
 */

public class OdpowiedzSQL
{
    java.util.ArrayList<String> nazwyKolumn;
    java.util.ArrayList<java.util.ArrayList<String>> rekordy;
    java.sql.ResultSet rs;

    public OdpowiedzSQL(java.sql.ResultSet rs,ArrayList<String> nazwyKolumn, ArrayList<ArrayList<String>> rekordy)
    {
        this.nazwyKolumn = nazwyKolumn;
        this.rekordy = rekordy;
    }

    public OdpowiedzSQL()
    {
        this.rs=null;
        this.nazwyKolumn=new java.util.ArrayList<String>();
        this.rekordy=new java.util.ArrayList<java.util.ArrayList<String>>();
    }

    public ArrayList<String> getNazwyKolumn()
    {
        return nazwyKolumn;
    }

    public void setNazwyKolumn(ArrayList<String> nazwyKolumn)
    {
        this.nazwyKolumn = nazwyKolumn;
    }

    public ArrayList<ArrayList<String>> getRekordy()
    {
        return rekordy;
    }

    public void setRekordy(ArrayList<ArrayList<String>> rekordy)
    {
        this.rekordy = rekordy;
    }

    public java.util.ArrayList<String> getRekord(int i)
    {
        if(i<rekordy.size())
            return rekordy.get(i);
        else
            return new java.util.ArrayList<String>();
    }

    public ResultSet getRs()
    {
        return rs;
    }

    public void setRs(ResultSet rs)
    {
        this.rs = rs;
    }

    public String getValue(String nazwaKolumny,int rekord)
    {
        try
	{
            int indexKolumny=this.nazwyKolumn.indexOf(nazwaKolumny);
            return getValue(indexKolumny,rekord);
        }
        catch(Exception ee)
        {
            ee.printStackTrace();
        }
        return "";
    }

    public String getValue(int indexKolumny,int rekord)
    {
        try
	{
	    if(indexKolumny>=0&&rekord>=0&&this.rekordy.size()>rekord&&this.rekordy.get(rekord).size()>indexKolumny)
		return this.rekordy.get(rekord).get(indexKolumny);
        }
        catch(Exception ee)
        {
	    ee.printStackTrace();
        }
        return "";
    }
    @Override
    public String toString()
    {
        StringBuffer odp=new StringBuffer("#############Nazwy Kolumn##########################\r\n");
        for(int i=0;i<this.nazwyKolumn.size();i++)
            odp.append(" | "+this.nazwyKolumn.get(i));
        odp.append("\r\n\r\n###############################REKORDY##################\r\n");
        for(int r=0;r<this.rekordy.size();r++)
        {
            java.util.ArrayList<String> pojRekord=this.rekordy.get(r);
            for(int k=0;k<pojRekord.size();k++)
            {
               odp.append(" | "+pojRekord.get(k));
            }
            odp.append("\r\n");
        }
        return odp.toString();
    }

    public String toCSV()
    {
        StringBuffer odp=new StringBuffer("");
        for(int i=0;i<this.nazwyKolumn.size();i++)
            odp.append(this.nazwyKolumn.get(i)+";");
        odp.append("\r\n");
        for(int r=0;r<this.rekordy.size();r++)
        {
            java.util.ArrayList<String> pojRekord=this.rekordy.get(r);
            for(int k=0;k<pojRekord.size();k++)
            {
		if(pojRekord.get(k)==null||pojRekord.get(k).equals(""))
		    odp.append("#;");
		else
		    odp.append(pojRekord.get(k)+";");
            }
            odp.append("\r\n");
        }
        return odp.toString();
    }


    public String[][] toTabTab()
    {
	String[][] dane=new String[this.rekordy.size()+1][this.nazwyKolumn.size()];

        for(int i=0;i<this.nazwyKolumn.size();i++)
            dane[0][i]=this.nazwyKolumn.get(i);

        for(int r=0;r<this.rekordy.size();r++)
        {
            java.util.ArrayList<String> pojRekord=this.rekordy.get(r);
            for(int k=0;k<pojRekord.size();k++)
            {
		if(pojRekord.get(k)==null||pojRekord.get(k).equals(""))
		    dane[r+1][k]="";
		else
		    dane[r+1][k]=pojRekord.get(k);
            }

        }
        return dane;
    }


    public int rowCount()
    {
        return this.rekordy.size();
    }

    public int kolumnCount()
    {
        return this.nazwyKolumn.size();
    }
    public java.util.ArrayList<Paczka> getAllPacks()
    {
	java.util.ArrayList<Paczka> odp=new java.util.ArrayList<Paczka>();
	for(int i=0;i<rowCount();i++)
	{
	    Paczka p=new Paczka();
	    java.util.ArrayList<String> kolumny=getNazwyKolumn();

	    for(int k=0;k<kolumny.size();k++)
	    {
		p.dodaj(kolumny.get(k), getValue(k,i));
	    }
	    odp.add(p);
	}
	return odp;
    }
}