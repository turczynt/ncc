package nbipackage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;
import java.net.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author turczyt
 */
public class NorthB
{
    String serwer ;
    Socket echoSocket ;
    PrintWriter out ;
    InputStream in;
    boolean zapFlag;
    NewFile logi;
    String login;
    String pass;
    String flagaSave;
    String ostatniRegName;
    String lastRegOperation;
    long timeout=60000;
    long lastExecCommend;
    boolean throTimeOut;
    long oneCommTimeout=3000000;//5 minut na jedna komende po czym reconnect

    /**
     *
     * @param serwer Ip wybranego M2000
     * @param login login do M2000
     * @param pass  Haslo do M2000 w postaci jawnej
     * @param flagaSave true- tryb zapisujacy wszystkie operacje wraz z odpowiedziami  to pliku z logami, false -brak zapisu operacji wraz z odpowiedziami
     */

    public NorthB(String serwer,String login,String pass,String flagaSave) throws NBIAnsException
    {
	System.runFinalizersOnExit(true);
	this.in=null;
	this.out=null;
	this.echoSocket=null;

        this.zapFlag=false;
        this.serwer=serwer;
	this.login=login;
	this.pass=pass;
	this.flagaSave=flagaSave;
	if(flagaSave!=null)
	    this.zapFlag=true;
	Systemowe syst=new Systemowe();
	String dataFormat = "yyyy-MM-dd HH:mm:ss";
	java.text.SimpleDateFormat  sdf = new java.text.SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
	;

	logi=new NewFile("/usr/samba/utran/PP/WO/SCRIPTS/LOGI_TESTOWE_NORTHB_"+sdf.format(java.util.Calendar.getInstance().getTime())+".txt");
	ostatniRegName="";
        lastRegOperation="";
	init();
    }

    public NorthB(String serwer,String login,String pass,String logiPath,boolean flagaZapLogi)throws NBIAnsException
    {
	System.runFinalizersOnExit(true);
        this.serwer=serwer;
	this.login=login;
	this.pass=pass;
        this.flagaSave="";
	Systemowe syst=new Systemowe();
        if(flagaZapLogi)
            this.zapFlag=true;
	logi=new NewFile(logiPath);
        this.ostatniRegName="";
        lastRegOperation="";
	init();
    }

    /**
     *
     * @return
     */

    public boolean init() throws NBIAnsException
    {
        try
	{
           // if(this!=null)
            //{
	    try{
		
		if(this.echoSocket!=null)
		{
                    this.closeBuffor();
		    /*this.in.close();
		    this.in=null;
		    this.out.close();
		    this.out=null;
		    this.echoSocket.close();*/

		}
	    }
	    catch(Exception ee)
	    {
		ee.printStackTrace();
	    }
            lastRegOperation="";
            this.echoSocket = new Socket(serwer, 31114);
	    //echoSocket.setSoTimeout(300000);
	   // this.echoSocket.setSoTimeout(900000);
            this.out = new PrintWriter(echoSocket.getOutputStream(), true);
            this.in = echoSocket.getInputStream();
            int im=13;
            int ij=10;
            //String userInput="LGI:OP=\"utranek\",PWD=\"12345678\""+";\r";
            String userInput="LGI:OP=\""+login+"\",PWD=\""+pass+"\""+";\r";
            this.out.println(userInput);
            String odp="";
            String zbior="";
            byte[] tmp=new byte[1024];
            while(true)
            {
                while(in.available()>0)
                {
                    int i=in.read(tmp,0,1024);
		    if(zapFlag)
			logi.dopisz(new String(tmp,0,i));
                    if(i<=0)
			break;
                    odp=odp+new String(tmp,0,i);
                    //logi.dopisz(odp);
		}
		//System.out.println("#"+odp+"#");
		if(!odp.trim().equals(""))
		{
                    zbior=zbior+odp;
                    if(zawiera(odp,new String[]{"To","be","continued"}))
			;
                    else
                    {
			break;
                    }
                    odp="";
		}
            }
	    int iloscPowtorzen=0;
	    if(zawiera(odp,new String[]{"RETCODE = 0"}))
		return true;
	    else
	    {
		System.out.println("DUPA PRZY INICJACJI:"+odp);

	    }
            
            
                return false;
	}
	catch (Exception e)
	{
	    if(zapFlag)
		logi.dopisz("\r\nERROR"+e.getLocalizedMessage()+" "+e.getMessage());
            throw new NBIAnsException("LGI:OP=\"******\",PWD=\"******\""+";\r","NIE POWIODLO SIE LOGOWANIE");
	}/*
	catch (IOException e)
	{
	    System.out.println("Couldn't get I/O for "+ "the connection to: "+serwer);
	    e.printStackTrace();
            return false;
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	    return false;
	}*/
    }

    public String  make2(String polecenie) throws NBIAnsException,java.io.IOException
    {
     /**
     *
     * @param polecenie przystosowywane do wyslania przez northb wyciananie na koncu \\r \\n oraz sprawdzenie czy polecenie zakonczone ";"
     * @return odpowiedz z M2000
     */
	

	//try
	//{


	//this.in.skip(this.in.available());

	if(this.zapFlag)
		    logi.dopisz("\r\n");
	throTimeOut=false;
	    StringBuffer calosc = new StringBuffer();
	    polecenie=polecenie.replaceAll(";","");
	    String nazwaPolecenia=NewFile.getTokens(polecenie, "1", "1", ":");
	    int puste=0;
	    polecenie=obetnij(polecenie);
	    StringBuilder str=new StringBuilder();
	    int im=13;
	    int ij=10;
	    String userInput=polecenie+";\r";
	    out.println(userInput);

	    String odp="";
	    String pattern="---    END";
	    String conntPattern="To be continued...\r\n\r\n---    END";
	    String conntPattern2="To be continued...\r\n---    END";
	    String sendFaild="Send Command Failed";
	    String odpowiedz="";

		
                char lastChar = pattern.charAt(pattern.length() - 1);
                StringBuilder sb = new StringBuilder();
                boolean found = false;
	
                //char ch = (char) in.read();
		int sign=0;
		
	       while((sign=in.read())>0)
               {
		   char ch=(char)sign;
		    if(ch!=((char)-1))
                    sb.append(ch);
		    if(ch!=((char)-1))
                    calosc.append(ch);
                    if (ch == lastChar)
                    {
                        String tmp=sb.toString();
                        if (tmp.endsWith(pattern))
                        {
                            if (tmp.endsWith(conntPattern)||tmp.endsWith(conntPattern2))
                            {
                                sb.delete(0, sb.length());
                            }
                            else
                            {
	                        break;
                            }
                        }
			else if(tmp.endsWith(sendFaild))
			{
			    break
			    ;//throw new NBIAnsException(polecenie+"{"+ostatniRegName+"}",calosc.toString());
			}
                    }

                   /* ch = (char) in.read();
		    if(this.zapFlag)
			logi.dopisz(ch);*/
		   // if(this.zapFlag)
			//logi.dopisz(Character.toString(ch));
                }
		//timer.
		if(throTimeOut)
		{
		    throw new NBIAnsException(polecenie,calosc.toString()+" THROW TIME OUT");
		}
		if(calosc!=null&&calosc.length()>0&&calosc.toString().contains(nazwaPolecenia)&&calosc.toString().contains("---    END"))
		{
		    //System.out.println(ostatniRegName+"|"+polecenie+" CZYTANIE OK");
		    return calosc.toString();
		    
		}
		else
		{
		    //System.out.println(ostatniRegName+"|"+polecenie+" DUPA");
                        try{
                            this.closeBuffor();
                        }
                        catch(Exception ee)
                        {
                            ;
                        }
                        if(calosc.toString().contains("Over Max Number"))
                        {
                            throw new NBIAnsException(polecenie,calosc.toString()+" Over Max Number");
                        }
                        else
                        {
                            ;//init();
                            this.ostatniRegName="";
                            throw new NBIAnsException(polecenie,calosc.toString()+" BLEDNA ODPOWIEDZ NBI");
                            //this.regOperation(this.ostatniRegName);
                            //return makeAgain(polecenie);		   
                        }
		}
            //}
            //catch (Exception e)
            //{
		//System.out.println("WYwala read:");
		//e.printStackTrace();
		//return "ERROR";
	    //}
		
            
	
    }

     public String  makeAgain(String polecenie) throws NBIAnsException,java.io.IOException
    {
     /**
     *
     * @param polecenie przystosowywane do wyslania przez northb wyciananie na koncu \\r \\n oraz sprawdzenie czy polecenie zakonczone ";"
     * @return odpowiedz z M2000
     */


	//try
	//{

	 if(this.zapFlag)
		    logi.dopisz("\r\n");
	
	    StringBuffer calosc = new StringBuffer();
	    polecenie=polecenie.replaceAll(";","");
	    String nazwaPolecenia=NewFile.getTokens(polecenie, "1", "1", ":");
	    int puste=0;
	    polecenie=obetnij(polecenie);
	    StringBuilder str=new StringBuilder();
	    int im=13;
	    int ij=10;
	    String userInput=polecenie+";\r";
	    out.println(userInput);

	    String odp="";
	    String pattern="---    END";
	    String conntPattern="To be continued...\r\n\r\n---    END";
	    String conntPattern2="To be continued...\r\n---    END";
	    String sendFaild="Send Command Failed";

	    String odpowiedz="";


                char lastChar = pattern.charAt(pattern.length() - 1);
                StringBuilder sb = new StringBuilder();
                boolean found = false;

                int sign=0;

	       while((sign=in.read())>0)
               {
		   char ch=(char)sign;
		    if(ch!=((char)-1))
                    sb.append(ch);
		    if(ch!=((char)-1))
                    calosc.append(ch);
                    if (ch == lastChar)
                    {
                        String tmp=sb.toString();
                        if (tmp.endsWith(pattern))
                        {
                            if (tmp.endsWith(conntPattern)||tmp.endsWith(conntPattern2))
                            {
                                sb.delete(0, sb.length());
                            }
                            else
                            {
	                        break;
                            }
                        }
			else if(tmp.endsWith(sendFaild))
			{
			    break;
			    //throw new NBIAnsException(polecenie+"{"+ostatniRegName+"}",calosc.toString());
			}
                    }

                    ch = (char) in.read();
		    if(this.zapFlag)
			logi.dopisz(ch);
		   // if(this.zapFlag)
			//logi.dopisz(Character.toString(ch));
                }
		if(throTimeOut)
		{
		    throw new NBIAnsException(polecenie,calosc.toString()+" THROW TIME OUT");
		}

		if(calosc!=null&&calosc.length()>0&&calosc.toString().contains(nazwaPolecenia)&&calosc.toString().contains("---    END"))
		{
		    //System.out.println(ostatniRegName+"|"+polecenie+" CZYTANIE OK");
		    return calosc.toString();

		}
		else
		{
		    //System.out.println(ostatniRegName+"|"+polecenie+" DUPA");

		    throw new NBIAnsException(polecenie+"{"+ostatniRegName+"}",calosc.toString());
		}
     }

    /**
     *
     * @param regName nazwa NE
     * @param polecenie {LST,DSP,ADD,MOD, ACT,DEA,BLK,UBL,SET,RMV}
     * @return Odpowiedz_NBI
     */

    public String make(String regName,String polecenie)  throws NBIAnsException,java.io.IOException
    {
       /* boolean zalogowanyNe=false;
        if(ostatniRegName!=null&&ostatniRegName.equals(regName))
        {
            zalogowanyNe=true;
        }
        else
        {
            zalogowanyNe=regOperation(regName);
        }
        if(zalogowanyNe)
        {
            String   odp=this.make2(polecenie);
	    if(this.zapFlag)
		logi.dopisz("MAKE2="+odp+"\n");
            if(this.zawiera(odp, "Login or Register needed")||this.zawiera(odp, "Login or Register needed"))
            {
                if(regOperation(regName))
                {
                    ostatniRegName=regName;
                    odp=this.make2(polecenie);
		    if(this.zapFlag)
	    		logi.dopisz("MAKE2="+odp+"\n");
                    return odp;
                }
                else
		{
		    if(this.zapFlag)
			logi.dopisz("lastRegOperation:MAKE2="+this.lastRegOperation+"\n");
                    return this.lastRegOperation;
		}
            }
            ostatniRegName=regName;
	    if(this.zapFlag)
		logi.dopisz("MAKE2="+odp+"\n");
            return odp;
        }
        else
        {
		    if(this.zapFlag)
			logi.dopisz("lastRegOperation:MAKE2="+this.lastRegOperation+"\n");
                    return this.lastRegOperation;
		}
	*
	* /
	*
	*
	*
	*/

	return makeNew(regName,polecenie);
    }

    public String makeNew(String regName,String polecenie) throws NBIAnsException,java.io.IOException
    {
	//try
	//{
	String odp="";
        String nazwaPolecenia=NewFile.getTokens(polecenie, "1", "1", ":");
       
	int zalogowanyNe=-1;
        if(ostatniRegName!=null&&ostatniRegName.equals(regName))
        {
            zalogowanyNe=1;
        }
        else
        {
            zalogowanyNe=regOperation(regName);
        }
        if(zalogowanyNe==1)
        {
	    odp=this.make2(polecenie);
	    if(odp.contains("RETCODE = 0")&&odp.contains(nazwaPolecenia))
		return odp;
	    else
	    {
		System.out.println(regName+" "+polecenie+"\r\n"+odp);
	    }
	}
        else 
            odp=lastRegOperation;
	
	return odp;
    }

    public String MakeWithReg(String komenda_regName) throws NBIAnsException,java.io.IOException
    {
	if (komenda_regName != null && !komenda_regName.equals(""))
        {
            String komenda = logi.getTokens(komenda_regName, "1", "1", "[{]");
            komenda = komenda.replaceAll("[{]", "");
            String regname = logi.getTokens(komenda_regName, "2", "2", "[{]");
            regname = regname.replaceAll("[{]", "");
            regname = regname.replaceAll("[}]", "");
            return this.make(regname, komenda);
        }
        else
            return "";
    }

    private int regOperation(String regName) throws NBIAnsException,java.io.IOException
    {
        boolean regOk=false;
        lastRegOperation=this.make2("REG NE:NAME=\""+regName+"\"");
        ;
        if(this.zawiera(lastRegOperation, "RETCODE = 0  Success"))
        {  
            this.ostatniRegName=regName;
            return 1;
        
        }
        else if(this.zawiera(lastRegOperation, "RETCODE = 1  NE does not Connection"))
        {  
               System.out.println(lastRegOperation);
            //this.ostatniRegName=regName;
            return 0;
        
        }
        else
        {
            System.out.println(lastRegOperation);
            return -1;
        }
    }

    /**
     *
     * @return true -dla poprawnego zamkniecia bufforow, false- dla nieudanej operacji
     */
    public boolean closeBuffor()
    {
        try
        {
	  
	    String userInput="LGO:OP="+login+";\r";
            
            out.println(userInput);
            System.out.println(userInput);
            out.close();

            in.close();
	    
            echoSocket.close();
	    
            return true;
	}
        catch (UnknownHostException e)
	{
            System.out.println("Don't know about host: "+serwer);
            return false;
	}
	catch (IOException e)
	{
            System.out.println("Couldn't get I/O for "+ "the connection to: "+serwer);
            return false;
	}
        catch(Exception e)
        {
            System.out.println("BLA bla");
	    e.printStackTrace();
            return false;
        }
    }

    private boolean zawiera(String tekst,String szukany)
    {
	if(tekst!=null&&szukany!=null&&!tekst.equals("")&&!szukany.equals(""))
	{
            Pattern p = Pattern.compile(szukany);
            Matcher matcher= p.matcher(tekst);
            if(matcher.find())
		return true;
            return false;
	}
	return false;
    }

    private boolean zawiera(String tekst,String[] szukany)
    {
	if(tekst!=null&&szukany!=null&&!tekst.equals("")&&!szukany.equals(""))
    	{
            boolean flaga=true;
            for (int s=0;s<szukany.length ;s++ )
            {
                if(!zawiera(tekst,szukany[s]))
                    return false;
            }
            return true;
	}
	return false;
    }

    private String obetnij(String wej)
    {
	char[]tab=wej.toCharArray();
	String str="";
	for(int i=0;i<tab.length;i++)
	{
            if(tab[i]==10||tab[i]==13)
		;
            else
		str=str+tab[i];
	}
	return str;
    }

   @Override
   protected void finalize () throws Throwable
    {
	try
	{
	  
	    String userInput="LGO:OP=\""+login+"\";\r";
            out.println(userInput);
            out.close();
            in.close();
            echoSocket.close();
	    System.out.println("ZAMKNIECIE OK unreg="+this.ostatniRegName);
	}
	catch(Exception ee)
 	{
	    System.out.println("ZAMKNIECIE BLAD unreg="+this.ostatniRegName);
	    ee.printStackTrace();
	}
	finally
	{
	    super.finalize();
	}
    }
}