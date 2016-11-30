package nbipackage;

public class Paczka
{
	java.util.ArrayList nazwy;
	java.util.Hashtable hash;

	public Paczka()
	{
		this.nazwy=new java.util.ArrayList();
		this.hash=new java.util.Hashtable();
	}

	public boolean dodaj(String klucz,String wartosc)
	{
			if(this.hash.containsKey(klucz.toUpperCase()))
			{
				this.hash.put(klucz.toUpperCase(),wartosc);
				return true;
			}
			else
			{
				this.nazwy.add(klucz.toUpperCase());
				this.hash.put(klucz.toUpperCase(),wartosc);
				return true;
			}
	}

	public String[] getNazwy()
	{
		String[] tmp=new String[this.nazwy.size()];
		for(int i=0;i<this.nazwy.size();i++)
		{
			tmp[i]=(String)this.nazwy.get(i);
		}
		return tmp;
	}

	public java.util.ArrayList getNazwy2()
	{
		java.util.Enumeration e=this.hash.keys();
		java.util.ArrayList<String> temp=new java.util.ArrayList<String>();
		while(e.hasMoreElements())
			temp.add((String)e.nextElement());
		return temp;
	}

	public String getNazwa(int i)
	{
		if(i<nazwy.size())
			return (String)this.nazwy.get(i);
		else
			return null;
	}

	public String getWartosc(String klucz)
	{
		if(hash.containsKey(klucz.toUpperCase()))
		{
			return ((String)hash.get(klucz.toUpperCase())).trim();
		}
		else
			return "";
	}

	public String getWartosc(int index)
	{
		if(index<nazwy.size())
			return (String)hash.get(((String)nazwy.get(index)).toUpperCase());
		else
			return "";
	}
}