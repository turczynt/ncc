package nbipackage;

import java.io.*;
import java.lang.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileWriter;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NewFile
{
FileOutputStream stream;//=new FileOutputStream(this.sciezka,true);
java.io.OutputStreamWriter zap;//=new java.io.OutputStreamWriter(stream);

	static public double Dbm2Wat(double DecDbm)
        {
	    try{
	    double wynik=0.0;
	    wynik=Math.pow(10, (DecDbm/100))/1000;
	    return wynik;
	    }
	    catch(Exception e)
	    {
		e.printStackTrace();
	    }
	    return -1.0;
	}
	static public double Wat2Dbm(double Wat)
	{
	    try{
	    double wynik=0.0;
	    wynik=100*Math.log10(Wat*1000);
	    return wynik;
	    }
	    catch(Exception e)
	    {
		e.printStackTrace();
	    }
	    return -1.0;

	}
	public NewFile(String sciezka)
	{
            /** Podstawowy konstruktor klasy NewFile*/
            /**sciezka- sciezka do Pliku*/
			this.sciezka=sciezka;
	}

	public NewFile(String sciezka,String n)
	{
            /** Konstruktor klasy NewFile, tworzacy nowy plik( jezeli plik istnieje jest najpierw kasowany)*/
            /** sciezka- sciezka do Pliku*/
			this.sciezka=sciezka;
			File a =new File(sciezka);

			if(a.exists())
				a.delete();

	}
        public NewFile()
	{
		;
	}

	public boolean istnieje()
	{
		File ex=new File(this.sciezka);
                		return ex.exists();
	}

	public void clear()
	{
	    try
		{
		FileOutputStream zap=new FileOutputStream(this.sciezka,false);
			zap.write("".getBytes());
			zap.close();
	    }
	    catch(Exception ee)
	    {
		;
	    }
	}
	
	public boolean dopisz(String dane)
	{
		boolean flaga=true;
		try
		{
			FileOutputStream stream=new FileOutputStream(this.sciezka,true);
			java.io.OutputStreamWriter zap=new java.io.OutputStreamWriter(stream);
			zap.write(dane);
//			zap.write(dane.getBytes());
			zap.close();
			if(!istnieje())
			{
				System.out.println("	added file	:  "+Nazwa(this.sciezka));
				flaga =false;
			}
		}
		catch (Exception e)
		{
			System.out.println("Error opening file "+this.sciezka);
					return false;
		}
		return flaga;
	}
        
        public boolean openStream() throws FileNotFoundException
        {
           
			stream=new FileOutputStream(this.sciezka,true);
			zap=new java.io.OutputStreamWriter(stream);
           
            return true;
        }
        public boolean closeStream()
        {
            try
            {
                this.zap.close();
                this.stream.close();
                return true;
            }
            catch (IOException ex)
            {
                return false;
            }

        }
        public boolean dopiszStream(String dane)
	{
		boolean flaga=true;
		try
		{
			zap.write(dane);
//			zap.write(dane.getBytes());
			
			if(!istnieje())
			{
				System.out.println("	added file	:  "+Nazwa(this.sciezka));
				flaga =false;
			}
		}
		catch (Exception e)
		{
			System.out.println("Error opening file "+this.sciezka);
					return false;
		}
		return flaga;
	}
        
	public boolean dopisz(char dane)
	{
		boolean flaga=true;
		try
		{
			FileOutputStream stream=new FileOutputStream(this.sciezka,true);
			java.io.OutputStreamWriter zap=new java.io.OutputStreamWriter(stream);
			char[] danee=new char[]{dane};
			zap.write(danee);

//			zap.write(dane.getBytes());
			zap.close();
			if(!istnieje())
			{
				System.out.println("	added file	:  "+Nazwa(this.sciezka));
				flaga =false;
			}
		}
		catch (Exception e)
		{
			System.out.println("Error opening file "+this.sciezka);
					return false;
		}
		return flaga;
	}

	public boolean dopisz(String[] dane)
	{
		for(int i=0;i<dane.length;i++)
		{
			dopisz(dane[i]+"\n");
		}
		return true;
	}
	public void dopisz(java.util.ArrayList<String> linieLst)
	{
		for (int i=0;i<linieLst.size();i++)
		{
			dopisz(linieLst.get(i)+"\n");
		}
	}

	public String name()
	{
		StringTokenizer nazwa=new StringTokenizer(Nazwa(this.sciezka),".");
		String tmp=new String();
		if(nazwa.hasMoreTokens())
			tmp=nazwa.nextToken(".");
		if(tmp!=null)
			return tmp;
		else
			return null;
	}

	public String atrValue(String line,String atr)
	{
		String tmp="";
		String[] atrybuty=line.split(",");
		if(atrybuty.length>0)
		{
			String atrName=getTokens(getTokens(atrybuty[0].trim(),"1","1","="),"2","2",":").trim();
			String atrVal=getTokens(atrybuty[0].trim(),"2","2","=").trim();
			if(atrName.trim().equalsIgnoreCase(atr.trim()))
				return atrVal.trim();
			for(int i=1;i<atrybuty.length;i++)
			{
				atrName=getTokens(atrybuty[i].trim(),"1","1","=").trim();
				atrVal=getTokens(atrybuty[i].trim(),"2","2","=").trim();
				if(atrName.trim().equalsIgnoreCase(atr.trim()))
					return atrVal.trim().replaceAll(";","");
			}
		}
		return tmp;
	}

	public String rozszezenie()
	{
			StringTokenizer nazwa=new StringTokenizer(Nazwa(this.sciezka),".");
			String tmp=new String();
			
			while(nazwa.hasMoreTokens())
				tmp=nazwa.nextToken(".");
			if(tmp!=null)
				return tmp;
			else
				return null;
	}

	private String Nazwa(String pass)
	{
		StringTokenizer s=new StringTokenizer(pass,syskonf.separator());
		String tmp=new String();
		while(s.hasMoreTokens())
			tmp=s.nextToken(syskonf.separator());
		return tmp;
	}

	public String Naglowek()
	{
		try
		{
			plik = new FileReader(this.sciezka);
			wejscie = new BufferedReader(plik);
			return wejscie.readLine();
		}
		catch(IOException e)
		{
			System.err.println(e);
			zakBuforr();
			return null;
		}
		finally
		{
			zakBuforr();
		}
	}

	public String[] getLinia(String szukany)
	{////////////////////////W pliku jeden String
		String nazwaplik=this.sciezka;
		String data=new String();
		String[] tablini;
		String candidateString=new String();
		ArrayList linie=new ArrayList();
		String tmp;
		Pattern p = Pattern.compile(szukany);
		Matcher matcher;
		try
		{
			plik = new FileReader(nazwaplik);
			wejscie = new BufferedReader(plik);
			while((tmp=wejscie.readLine())!=null)
			{
				matcher= p.matcher(tmp);
				if(matcher.find())
					linie.add(tmp);
			}
		}
		catch(IOException e)
		{
			System.err.println(e);
		}
		finally
		{
			zakBuforr();
		}
		int qw=linie.size();
		tablini=new String[qw];
		for(int w=0;w<qw;w++)
		{
			tablini[w]=(String)linie.get(w);
		}
		return tablini;
	}

	public java.util.ArrayList<String> getLiniaList(String szukany)
	{////////////////////////W pliku jeden String
		String nazwaplik=this.sciezka;
		String data=new String();

		String candidateString=new String();
		ArrayList<String> linie=new ArrayList<String>();
		String tmp;
		
		try
		{
			plik = new FileReader(nazwaplik);
			wejscie = new BufferedReader(plik);
			while((tmp=wejscie.readLine())!=null)
			{
				
				if(tmp.contains(szukany))
					linie.add(tmp);
			}
		}
		catch(IOException e)
		{
			System.err.println(e);
		}
		finally
		{
			zakBuforr();
		}
		return linie;
	}

        public boolean changeAllinFile(String szukanaLinia,String nowaLinia)
        {
            String all=this.getAll();
            all=all.replaceAll(szukanaLinia, nowaLinia);
            try
            {
                try
                {
                    this.zakBuforr();
                }
                catch(Exception eee)
                {
                    System.out.println("Zamykanie strumieni itp, "+eee.toString());
                }
                File a =new File(sciezka);
                if(a.exists())
                    a.delete();
                this.dopisz(all);
                return true;
            }
            catch(Exception ee)
            {

                return false;
            }

        }
        public boolean changeFirstinFile(String szukanaLinia,String nowaLinia)
        {
            String all=this.getAll();
            all=all.replaceFirst(szukanaLinia, nowaLinia);
            try
            {
                try{
                this.zakBuforr();
                }
                catch(Exception eee)
                {
                    System.out.println("Zamykanie strumieni itp, "+eee.toString());
                }
                File a =new File(sciezka);
                if(a.exists())
                    a.delete();

                //System.out.println("#####\n"+)
                this.dopisz(all);
                return true;
            }
            catch(Exception ee)
            {
                
                StackTraceElement[] traceElements = ee.getStackTrace();
                            for (int ste = 0; ste < traceElements.length; ste++)
                            {
                                
                                System.out.println(traceElements[ste]);
                            }
                return false;
            }
        }


	static private String rFConsol()
	{
		try
		{
			BufferedReader wejscie = new BufferedReader(new InputStreamReader(System.in));
			String linia=wejscie.readLine();
			return linia;
		}
		catch(IOException e)
		{
			System.err.println(e);
			return "";
		}
	}

	static public String readFromConsol()
	{
		String odp=rFConsol();
		while(odp==null||odp.equals(""))
			odp=rFConsol();
		return odp;
	}

	public String chooseFromConsol(java.util.ArrayList wybor,String komunikat)
	{
		String odp=readFromConsol();
		while(!wybor.contains(odp))
		{
			System.out.println(komunikat);
			odp=readFromConsol();
		}
		return odp;
	}
	public String chooseFromConsol(String[] wyborT,String komunikat)
	{
		String odp=readFromConsol();
		boolean ok=false;
		for(int i=0;i<wyborT.length;i++)
			if(wyborT[i].equalsIgnoreCase(odp))
				ok=true;
		if(ok)
			return odp;
		else
		{
			System.out.println(komunikat);
			return chooseFromConsol(wyborT,komunikat);
		}
		/*while(!wybor.contains(odp))
		{
			System.out.println(komunikat);
			odp=readFromConsol();
		}
		return odp;
		*/
	}

	public String getAll()
	{////////////////////////W pliku jeden String
		String nazwaplik=this.sciezka;
		StringBuffer data=new StringBuffer();
		String tmp="";
		int licznik=0;
		try
		{
			//plik = new FileReader(nazwaplik);
			InputStreamReader In=new InputStreamReader(new FileInputStream(nazwaplik), "UTF-8");
			wejscie = new BufferedReader(In);
			System.out.println("set "+In.getEncoding()+" encoding file");
			while((tmp=wejscie.readLine())!=null)
			{

				data.append(tmp+"\n");
			}

		}
		catch(IOException e)
		{
			System.out.println(e);
		}
		finally
		{
			zakBuforr();
		}
		return data.toString();
	}

	public String[] getAllTab()
	{////////////////////////W pliku jeden String
		String nazwaplik=this.sciezka;
		String data="";
		ArrayList array=new ArrayList();
		String tmp="";
		int licznik=0;
		try
		{
			plik = new FileReader(nazwaplik);
			wejscie = new BufferedReader(plik);
			while((tmp=wejscie.readLine())!=null)
			{
				array.add(tmp);
			}
			System.out.println();
		}
		catch(IOException e)
		{
			System.err.println(e);
		}
		finally
		{
			zakBuforr();
		}
		String[] tmpt=new String[array.size()];
		for(int z=0;z<tmpt.length;z++)
		{
			tmpt[z]=(String)array.get(z);
			//System.out.println(tmpt[z]+"\t\t *********");
		}
		return tmpt;
	}

	public static String[] getLinia(String szukany,String tekst)
	{////////////////////////W danych jeden String
		String[] tablini;
		String candidateString=new String();
		ArrayList linie=new ArrayList();
		String tmp=new String();
		if(szukany!=null&&(!szukany.equals("")))
		{
			Pattern p = Pattern.compile(szukany);
			Matcher matcher;
			StringTokenizer line=new StringTokenizer(tekst,"\n");
			while(line.hasMoreTokens())
			{
				tmp=line.nextToken("\n");
				matcher= p.matcher(tmp);
				if(matcher.find())
					linie.add(tmp);
			}
			int qw=linie.size();
			tablini=new String[qw];
			for(int w=0;w<qw;w++)
			{
				tablini[w]=(String)linie.get(w);
			}
			return tablini;
		}
		else
			return null;
	}

	public static String[] getLinia(String[] szukany,String tekst)
	{////////////////////////W danych  kilka stringów
		String data=new String();
		String[] tablini;
		String candidateString=new String();
		String[] linie=tekst.split("\n");
		String tmp;
		ArrayList temp=new ArrayList();
		Pattern p = Pattern.compile(szukany[0]);
		Matcher matcher;

		Pattern pat;
		int qw=linie.length;
		for(int ind=0;ind<qw;ind++)
		{
			boolean flaga=true;
			Matcher matcher2;
			for(int l=0;l<szukany.length;l++)
			{
				pat= Pattern.compile(szukany[l]);
				matcher2=pat.matcher(linie[ind]);
				if(!matcher2.find())
				{
					flaga=false;
				}
			}
			if(flaga)
				temp.add(linie[ind]);
		}
		int qwq=temp.size();
		tablini=new String[qwq];
		for(int w=0;w<qwq;w++)
		{
			tablini[w]=(String)temp.get(w);
		}
		return tablini;
	}

	public static java.util.ArrayList<String> getLinesFromText(String[] szukany,String tekst)
	{////////////////////////W danych  kilka stringów
		//String data=new String();
		String[] tablini;
		//String candidateString=new String();
		String[] linie=tekst.split("\n");
		String tmp;
		ArrayList<String> temp=new ArrayList<String>();
		Pattern p = Pattern.compile(szukany[0]);
		//Matcher matcher;

		Pattern pat;
		int qw=linie.length;
		for(int ind=0;ind<qw;ind++)
		{
			boolean flaga=true;
			Matcher matcher2;
			for(int l=0;l<szukany.length;l++)
			{
				pat= Pattern.compile(szukany[l]);
				matcher2=pat.matcher(linie[ind]);
				if(!matcher2.find())
				{
					flaga=false;
				}
			}
			if(flaga)
				temp.add(linie[ind]);
		}
		return temp;
	}

	public static String getFirstLine(String[] szukany,String tekst)
	{

		String data=new String();
		String[] tablini;
		String candidateString=new String();
		String[] linie=tekst.split("\n");
		String tmp;
		String temp="";
		Pattern p = Pattern.compile(szukany[0]);
		Matcher matcher;

		Pattern pat;
		int qw=linie.length;
		for(int ind=0;ind<qw;ind++)
		{
			boolean flaga=true;
			Matcher matcher2;
			for(int l=0;l<szukany.length;l++)
			{
				pat= Pattern.compile(szukany[l]);
				matcher2=pat.matcher(linie[ind]);
				if(!matcher2.find())
				{
					flaga=false;
				}
			}
			if(flaga){
				temp=linie[ind];
				break;
			}
		}
		return temp;
	}

	public String[] getLinia(String[] szukany,String[] wyloczone)
	{////////////W pliku kilka stringów
		String nazwaplik=this.sciezka;
		String data=new String();
		String[] tablini;
		String candidateString=new String();
		ArrayList linie=new ArrayList();
		String tmp;
		ArrayList temp=new ArrayList();
		String szukStart="";
		if(szukany.length>0)
			szukStart=szukany[0];
		else
			szukStart="$";
		Pattern p = Pattern.compile(szukStart);
		Matcher matcher;
		try
		{
			plik = new FileReader(nazwaplik);
			wejscie = new BufferedReader(plik);
			while((tmp=wejscie.readLine())!=null)
			{
				matcher= p.matcher(tmp);
				if(matcher.find())
					linie.add(tmp);
			}
		}
		catch(IOException e)
		{
			System.err.println(e);
		}
		finally
		{
			zakBuforr();
		}
		Pattern pat;
		int qw=linie.size();
		for(int ind=0;ind<qw;ind++)
		{
			boolean flaga=true;
			Matcher matcher2;
			for(int l=0;l<szukany.length;l++)
			{
				pat= Pattern.compile(szukany[l]);
				matcher2=pat.matcher((String)linie.get(ind));
				if(!matcher2.find())
				{
					flaga=false;
				}
			}
			for(int l=0;l<wyloczone.length;l++)
			{
				if(wyloczone[l]!=null)
				{
					pat= Pattern.compile(wyloczone[l]);
					matcher2=pat.matcher((String)linie.get(ind));
					if(matcher2.find())
					{
						flaga=false;
					}
				}
			}
			if(flaga)
				temp.add((String)linie.get(ind));
		}
		int qwq=temp.size();
		tablini=new String[qwq];
		for(int w=0;w<qwq;w++)
		{
			tablini[w]=(String)temp.get(w);
		}
		return tablini;
	}
	public String[] getLiniaIgnoreCase(String[] szukany,String[] wyloczone)
	{////////////W pliku kilka stringów
		String nazwaplik=this.sciezka;
		String data=new String();
		String[] tablini;
		String candidateString=new String();
		ArrayList linie=new ArrayList();
		String tmp;
		ArrayList temp=new ArrayList();
		Pattern p = Pattern.compile(szukany[0].toUpperCase());
		Matcher matcher;
		try
		{
			plik = new FileReader(nazwaplik);
			wejscie = new BufferedReader(plik);
			while((tmp=wejscie.readLine())!=null)
			{
				matcher= p.matcher(tmp.toUpperCase());
				if(matcher.find())
					linie.add(tmp);
			}
		}
		catch(IOException e)
		{
			System.err.println(e);
		}
		finally
		{
			zakBuforr();
		}
		Pattern pat;
		int qw=linie.size();
		for(int ind=0;ind<qw;ind++)
		{
			boolean flaga=true;
			Matcher matcher2;
			for(int l=0;l<szukany.length;l++)
			{
				pat= Pattern.compile(szukany[l].toUpperCase());
				matcher2=pat.matcher(((String)linie.get(ind)).toUpperCase());
				if(!matcher2.find())
				{
					flaga=false;
				}
			}
			for(int l=0;l<wyloczone.length;l++)
			{
				if(wyloczone[l]!=null)
				{
					pat= Pattern.compile(wyloczone[l].toUpperCase());
					matcher2=pat.matcher(((String)linie.get(ind)).toUpperCase());
					if(matcher2.find())
					{
						flaga=false;
					}
				}
			}
			if(flaga)
				temp.add((String)linie.get(ind));
		}
		int qwq=temp.size();
		tablini=new String[qwq];
		for(int w=0;w<qwq;w++)
		{
			tablini[w]=(String)temp.get(w);
		}
		return tablini;
	}

	public void setReadOnlyForOwner()
	{
	    File a =new File(sciezka);
	    //a.setExecutable(true);
	    //a.setWritable(true);
	    //a.setReadable(true, true);
	    try
	    {
		Process p = Runtime.getRuntime().exec("chmod 733 "+a.getAbsolutePath());
	    }
	    catch(Exception ee)
	    {
		ee.printStackTrace();
	    }
	}

	public String[] getLinia(String[] szukany)
	{////////////W pliku kilka stringów
		String nazwaplik=this.sciezka;
		String data=new String();
		String[] tablini;
		String candidateString=new String();
		ArrayList linie=new ArrayList();
		String tmp;
		ArrayList temp=new ArrayList();
		Pattern p = Pattern.compile(szukany[0]);
		Matcher matcher;
		try
		{
			plik = new FileReader(nazwaplik);
			wejscie = new BufferedReader(plik);
			while((tmp=wejscie.readLine())!=null)
			{
				matcher= p.matcher(tmp);
				if(matcher.find())
					linie.add(tmp);
			}
		}
		catch(IOException e)
		{
			System.err.println(e);
		}
		finally
		{
			zakBuforr();
		}
		Pattern pat;
		int qw=linie.size();
		for(int ind=0;ind<qw;ind++)
		{
			boolean flaga=true;
			Matcher matcher2;
			for(int l=0;l<szukany.length;l++)
			{
				pat= Pattern.compile(szukany[l]);
				matcher2=pat.matcher((String)linie.get(ind));
				if(!matcher2.find())
				{
					flaga=false;
				}
			}
			if(flaga)
				temp.add((String)linie.get(ind));
		}
		int qwq=temp.size();
		tablini=new String[qwq];
		for(int w=0;w<qwq;w++)
		{
			tablini[w]=(String)temp.get(w);
		}
		return tablini;
	}

	

	public boolean isNumber(String t)
	{
		if(t!=null&&!t.equals(""))
		{
			Pattern patern=Pattern.compile("^[0-9]+$");
			Matcher matcher=patern.matcher(t.trim());
			if(matcher.find())
				return true;
			else
				return false;
		}
		else
			return false;
	}

	public String pobierzDo(String szukany)
	{
		String nazwaplik=this.sciezka;
		String data=new String();
		String tablini;
		String candidateString=new String();
		ArrayList linie=new ArrayList();
		String tmp;
		Pattern p = Pattern.compile(szukany);
		Matcher matcher;
		try
		{
			plik = new FileReader(nazwaplik);
			wejscie = new BufferedReader(plik);
			boolean fla=true;
			while(((tmp=wejscie.readLine())!=null)&&fla)
			{
				matcher= p.matcher(tmp);
				if(matcher.find())
				{
					fla=false;
				}
				linie.add(tmp);
			}
		}
		catch(IOException e)
		{
			System.err.println(e);
		}
		finally{
			zakBuforr();
		}
		int qw=linie.size();
		tablini="";
		for(int w=0;w<qw-1;w++){
				tablini=tablini+"\n"+(String)linie.get(w);
		}
		return tablini;
	}

	

		

	public String[] pobierzOd(String szukany){
		String nazwaplik=this.sciezka;
		String data=new String();
		String tablini[];
		String candidateString=new String();
		ArrayList linie=new ArrayList();
		String tmp;
		Pattern p = Pattern.compile(szukany);
		Matcher matcher;
		try{
			plik = new FileReader(nazwaplik);
			wejscie = new BufferedReader(plik);
			boolean fla=false;

			while(((tmp=wejscie.readLine())!=null))
                        {
				boolean fla2=true;
				matcher= p.matcher(tmp);
				if(matcher.find())
                                {
					fla=true;
					fla2=false;
				}
				if(fla&&fla2)
                                    linie.add(tmp);
			}

		}
		catch(IOException e){
			System.err.println(e);
		}
		finally{
			zakBuforr();}

		int qw=linie.size();
		tablini=new String[qw];
		for(int w=0;w<qw;w++){
				tablini[w]=(String)linie.get(w);
		}
		return tablini;
	}

	public static String getTokens(String tekst,String t1,String t2)
	{
		StringTokenizer tokens=new StringTokenizer(tekst);
		boolean flag=true;
		int pocz=1;
		int kon=tokens.countTokens();
		String tmp="";
		String token;
		if(!t1.equals("*"))
			pocz=Integer.parseInt(t1);
		if(!t2.equals("*"))
			kon=Integer.parseInt(t2);
		for(int i=1;i<=kon;i++)
		{
			if(tokens.hasMoreTokens())
			{
				token=tokens.nextToken();
				if(i==pocz)
				{
					if(!flag)
					{
						tmp=tmp+" "+token;
					}
					if(flag)
					{
						tmp=token;
						flag=false;
					}
					pocz++;
				}
			}

		}
		return tmp;
	}

	public static String getTokens(String tekst,String t1,String t2,String dzielnik)
	{
		StringTokenizer tokens=new StringTokenizer(tekst,dzielnik);
		boolean flag=true;
		int pocz=1;
		int kon=tokens.countTokens();
		String tmp="";
		String token;
                try{
		if(!t1.equals("*"))
			pocz=Integer.parseInt(t1);
		if(!t2.equals("*"))
			kon=Integer.parseInt(t2);
		for(int i=1;i<=kon;i++)
		{
			if(tokens.hasMoreTokens())
			{
				token=tokens.nextToken(dzielnik);
				if(i==pocz)
				{
					if(!flag)
					{
						tmp=tmp+dzielnik+token;
					}
					if(flag)
					{
						tmp=token;
						flag=false;
					}
					pocz++;
				}
			}
		}
                }
                catch(Exception e)
                {
                    ;

                }
		return tmp;
	}



	public String type(String t){
		StringTokenizer token=new StringTokenizer(t,":");
		if (token.hasMoreTokens()){
			return token.nextToken(":")+":";
		}
		else
			return null;
	}

	public boolean przenies(String dir)
	{
		try
		{
			File dirFile=new File(dir);
			if(!dirFile.exists())
				dirFile.mkdirs();
			File file=new File(this.sciezka);
			//FileUtils f=new FileUtils();
			FileUtils.copyFile(this.sciezka,dir+Nazwa(this.sciezka));
			zakBuforr();
			;
			file.delete();
			return true;
		}
		catch(IOException e){
			System.err.println(e);
			return false;
		}
		catch(SecurityException w){
			System.err.println(w);
			return false;}
	}

	public boolean usun(){
		try{
			File file=new File(this.sciezka);
			//zakBuforr();
			file.delete();
		}
		//	catch(IOException e){
		//		System.err.println(e);
		//		return false;
		//	}
		catch(SecurityException w)
		{
			System.err.println(w);
			return false;
		}
			System.out.println("	removed file	:  "+Nazwa(this.sciezka));
			return true;
	}

	public String pass(){
		return this.sciezka;
	}
        public java.util.ArrayList<String> getAllList()
	{
		String nazwaplik=this.sciezka;
		String data="";
		ArrayList<String> array=new ArrayList<String>();
		String tmp="";
		int licznik=0;
		try
		{
			plik = new FileReader(nazwaplik);
			wejscie = new BufferedReader(plik);
			while((tmp=wejscie.readLine())!=null)
			{
				if(tmp.trim().equalsIgnoreCase(""))
					;
				else
					array.add(tmp);
			}

		}
		catch(IOException e)
		{
			System.err.println(e);
		}
		finally
		{
			zakBuforr();
		}
		return array;
	}

	public void zakBuforr(){
		try{
				if(this.wejscie!=null)
					this.wejscie.close();
				if(this.plik!=null)
				this.plik.close();
		}
		catch(IOException ss){
			System.err.println(ss);
		}
	}

	public boolean zawiera(String tekst,String szukany){
		Pattern p = Pattern.compile(szukany);
		Matcher matcher= p.matcher(tekst);
		if(matcher.find())
			return true;
		return false;
	}



	public boolean liniaZawiera(String tekst,String[]szukany){
		String[] tmp=tekst.split("\n");
		for(int i=0;i<tmp.length;i++){
			if(zawiera(tmp[i],szukany))
				return true;
		}
		return false;


	}

	public boolean zawiera(String tekst[],String[]szukany){
		boolean zaw=false;
		for(int i=0;i<tekst.length;i++){
			if(zawiera(tekst[i],szukany)){
				zaw=true;

			}
		}
		return zaw;

	}

	public boolean zawiera(String tekst,String[]szukany){
		boolean zawiera=true;
		if(szukany!=null&&szukany.length!=0){
		for(int i=0;i<szukany.length;i++){
			if(!zawiera(tekst,szukany[i]))
				zawiera=false;

		}
		return zawiera;
		}
		else
			return false;
	}
	public boolean zawieraIgnoreCase(String tekst,String[]szukany){
		boolean zawiera=true;
		if(szukany!=null&&szukany.length!=0){
		for(int i=0;i<szukany.length;i++){
			if(!zawiera(tekst.toUpperCase(),szukany[i].toUpperCase()))
				zawiera=false;

		}
		return zawiera;
		}
		else
			return false;
	}

	public String getFirstLine(String[] szukany,String[] wyloczone){////////////W pliku kilka stringów
		String nazwaplik=this.sciezka;
		String data=new String();
		String[] tablini;
		String candidateString=new String();
		ArrayList linie=new ArrayList();
		String tmp;
		ArrayList temp=new ArrayList();
		Pattern p = Pattern.compile(szukany[0]);
		Matcher matcher;
		boolean znalezione=false;
		String wynik="";
		try{
			plik = new FileReader(nazwaplik);
			wejscie = new BufferedReader(plik);
			while((tmp=wejscie.readLine())!=null&&!znalezione){
				if(zawiera(tmp,szukany)&&!zawiera(tmp,wyloczone)){
					wynik=tmp;

					znalezione=true;
				}
			}

		}
		catch(IOException e){
			System.err.println(e);
		}
		finally{
			zakBuforr();
		}
		return wynik;
	}

	public String getParamValue(String nazwaParam)
	{
	    try{
		String linia= getFirstLine(new String[]{nazwaParam,"="}, new String[]{});
		String val=getTokens(linia,"2","2","=");
		if(val!=null&&!val.equals(""))
		    return val;
		else
		    return null;
	    }
	    catch(Exception ee)
	    {
		return null;
	    }
	}
	
        
        public java.util.ArrayList<String> findIN(String szukany)
	{////////////W pliku kilka stringów
		String nazwaplik=this.sciezka;
		ArrayList linie=new ArrayList();
		String tmp;
	
		Pattern p = Pattern.compile(szukany);
		Matcher matcher;
		try
		{
			plik = new FileReader(nazwaplik);
			wejscie = new BufferedReader(plik);
			while((tmp=wejscie.readLine())!=null)
			{
				matcher= p.matcher(tmp);
				if(matcher.find())
					linie.add(tmp);
			}
		}
		catch(IOException e)
		{
			System.err.println(e);
		}
		finally
		{
			zakBuforr();
		}
               
                return linie;
		
	}

	public java.util.ArrayList<String> getSubArrayMatchTo(java.util.ArrayList<String> source,String pattern)
        {
            ArrayList<String> tmp=new java.util.ArrayList<String>();
            for(int z=0;z<source.size();z++)
            {
                if(zawiera(source.get(z),pattern))
                    tmp.add(source.get(z));
            }
            return tmp;
        }

	//BufferedWriter writer;
	//StringTokenizer token;
	String sciezka;
	//FileWriter fW ;
	FileReader plik;
	BufferedReader wejscie;
	Systemowe syskonf=new Systemowe();
}