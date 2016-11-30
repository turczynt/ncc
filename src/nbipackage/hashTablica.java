package nbipackage;

import java.util.*;
public class hashTablica
{

	public hashTablica(String naglowek){
			StringTokenizer Cellnames=new StringTokenizer(naglowek);
		/*	tablica.put("key1",new ArrayList());
			tablica.put("key2",null);
			tablica.put("key3",null);*/
			tablica=new Hashtable();
			klucze=new ArrayList();
			while(Cellnames.hasMoreTokens()){
				String tmp=Cellnames.nextToken().toUpperCase().trim();
				this.tablica.put(tmp,new ArrayList());
				this.klucze.add(tmp);
			}


	}

	public hashTablica(){
	}

	public hashTablica(String naglowek,String dzielnik){
			StringTokenizer Cellnames=new StringTokenizer(naglowek,dzielnik);
		/*	tablica.put("key1",new ArrayList());
			tablica.put("key2",null);
			tablica.put("key3",null);*/
			tablica=new Hashtable();
			klucze=new ArrayList();
			while(Cellnames.hasMoreTokens()){
				String tmp=Cellnames.nextToken(dzielnik).toUpperCase().trim();
				this.tablica.put(tmp,new ArrayList());
				this.klucze.add(tmp);
			}


	}

	public boolean dodajC(String linia,String dzielnik){							//
		StringTokenizer token=new StringTokenizer(linia,dzielnik);	//
		String[] dane=linia.split(dzielnik);
															//
		/*for(int k=0;k<dane.length;k++)
		{
			if(k<klucze.size())
			{
			    dodaj((String)klucze.get(k),dane[k]);
			}
		}*/
		for(int k=0;k<klucze.size();k++)
		{
		    if(k<dane.length)
			dodaj((String)klucze.get(k),dane[k]);
		    else
			dodaj((String)klucze.get(k),"");
		}
		return true;
	}

	public boolean dodajC(String linia)
	{
	    StringTokenizer token=new StringTokenizer(linia);
	    String[] dane=new String[token.countTokens()];
	    int i=0;
	    while(token.hasMoreTokens())
	    {
		dane[i]=token.nextToken().trim();
		i++;
	    }
	    for(int k=0;k<dane.length;k++)
	    {
		if(k<klucze.size())
		    dodaj((String)klucze.get(k),dane[k]);
	    }
	    return true;
	}

	public boolean  dodaj(String klucz,String dana)
	{
	    if(tablica.containsKey(klucz.toUpperCase()))
	    {
		if(klucz.equals("MNC"))
		{
		    int dlugosc=dana.length();
		    if(dlugosc>0)
			for(int dl=dlugosc;dl<2;dl++)
			    dana="0"+dana;
		}
		ArrayList tmp=(ArrayList)tablica.get(klucz.toUpperCase());
		tmp.add(dana);
		tablica.put(klucz.toUpperCase(),tmp);
		return true;
	    }
	    else
	    {
		ArrayList tmparray=new ArrayList();
		tmparray.add(dana);
		tablica.put(klucz.toUpperCase(),tmparray);
		this.klucze.add(klucz.toUpperCase());
		return false;
	    }
	}

	public String pobierz(String klucz,int index)
	{
	    if(tablica.containsKey(klucz.toUpperCase()))
	    {
		ArrayList tmp=(ArrayList)tablica.get(klucz.toUpperCase());
		if(index<tmp.size())
		    return (String) tmp.get(index);
		else
		    return null;
	    }
	    else
		return null;
	}

	public String[] pobierz(String klucz)
	{
	    if(tablica.containsKey(klucz.toUpperCase()))
	    {
		ArrayList tmp=(ArrayList)tablica.get(klucz.toUpperCase());
		String[] tmptab=new String[tmp.size()];
		for(int i=0;i<tmp.size();i++)
		    tmptab[i]=(String)tmp.get(i);
		if(tmptab.length>0)
		    return tmptab;
		else
		    return null;
	    }
	    else
		return null;
	}

	public String[] pobierz(int nrklucza)
	{
	    if(nrklucza<klucze.size())
	    {
		ArrayList tmp=(ArrayList)tablica.get((String)klucze.get(nrklucza));
		String[] tmptab=new String[tmp.size()];
		for(int i=0;i<tmp.size();i++)
		    tmptab[i]=(String)tmp.get(i);
		if(tmptab.length>0)
		    return tmptab;
		else
		    return null;
	    }
	    else
		return null;
	}

	public String pobierz(int nrklucza,int nrNaLiscie)
	{
	    if(nrklucza<klucze.size())
	    {
		ArrayList tmp=(ArrayList)tablica.get((String)klucze.get(nrklucza));
		if(tmp.size()>nrNaLiscie)
		    return (String)tmp.get(nrNaLiscie);
		else
		    return null;
	    }
	    else
		return null;
	}

	public boolean usun(String klucz)
	{
	    if(tablica.containsKey(klucz.toUpperCase()))
	    {
		tablica.remove(klucz.toUpperCase());
		return true;
	    }
	    return false;
	}

	public boolean usun(String klucz,int index)
	{
	    if(tablica.containsKey(klucz.toUpperCase()))
	    {
		ArrayList tmp=(ArrayList)tablica.get(klucz.toUpperCase());
		if(index<tmp.size())
		{
		    tmp.remove(index);
		    return true;
		}
	    }
	    return false;
	}

	public void wypisz()
	{
	    for (int i=0;i<klucze.size();i++)
	    {
	    	System.out.print((String)klucze.get(i)+"||");
	    }
	}

	Hashtable tablica;
	ArrayList klucze;
}