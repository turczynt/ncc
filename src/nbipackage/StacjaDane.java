package nbipackage;

public class StacjaDane
{
	java.util.ArrayList<Paczka> listaPaczek;
	String nazwaStacji;
	String[] linie;
	public StacjaDane()
	{
		listaPaczek=new java.util.ArrayList<Paczka>();
		nazwaStacji="";
	}
	public void setNazwa(String nazwa)
	{
		this.nazwaStacji=nazwa;
	}
	public void addPack(Paczka pack)
	{
		listaPaczek.add(pack);
	}
	public Paczka getPack(int i)
	{
		if(i<listaPaczek.size())
		{
			return (Paczka)listaPaczek.get(i);
		}
		else
		{
			return null;
		}
	}
	public StacjaDane(String dane)
	{
		listaPaczek=new java.util.ArrayList<Paczka>();
		nazwaStacji="";
		this.linie =dane.split("\n");
		NewFile n=new NewFile();
		boolean nazwa=false;
		boolean daneBegin=false;
		boolean paczkaB=false;
		boolean paczkaEnd=false;
		boolean daneEnd=false;
		boolean dzielnik=false;
		boolean puste=true;
		String linia="";
		int zz=0;
		Paczka pack=new Paczka();
		for(int l=0;l<linie.length;l++)
		{
			linia=linie[l].trim();
			if(!nazwa&&n.zawiera(linia,"[+++]"))
			{
				nazwaStacji=n.getTokens(linia,"2","2");
				nazwa=true;
			}
			if(n.zawiera(linia,"-----"))
			{
				daneBegin=true;
				paczkaEnd=false;
				daneEnd=false;
				paczkaB=false;
				//dzielnik=true;
			}
			if(n.zawiera(linia,new String[]{"---","END"}))
			{
				daneBegin=false;
				paczkaB=false;
				paczkaEnd=true;
				daneEnd=true;
			}
			if(daneBegin&&n.zawiera(linia,"=")&&dzielnik)
			{
				pack=new Paczka();
			}
			if(daneBegin&&n.zawiera(linia,"="))
			{
				paczkaB=true;
				dzielnik=false;
			}
			if(n.zawiera(linia,new String[]{"Number","of","results"}))
			{
				paczkaB=false;
				paczkaEnd=true;
				daneEnd=true;
				daneBegin=false;
				dzielnik=true;
			}
			if(daneBegin&&(linia==null||linia.equals("")))
			{
				dzielnik=true;
				paczkaB=false;
			}
			if(paczkaB)
			{
				pack.dodaj(n.getTokens(linia,"1","1","=").trim(),n.getTokens(linia,"2","2","=").trim());
				puste=false;
			}
			if(dzielnik&&!puste)
			{
				listaPaczek.add(pack);
				puste=true;
			}
		}
	}
	public Paczka[] getAllPacks()
	{
		if(listaPaczek.size()<=0)
			return new Paczka[0];
		else{
			Paczka[] arrayTmp=new Paczka[listaPaczek.size()];
			for(int ii=0;ii<listaPaczek.size();ii++)
				arrayTmp[ii]=(Paczka)listaPaczek.get(ii);
			return arrayTmp;

		}

	}
	
	public String[] getBody()
	{
		return this.linie;
	}
	public String getNazwa()
	{
		return nazwaStacji;
	}
}
