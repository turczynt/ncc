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
    String comparableCommandPart;

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
            this.ostatniRegName="";
        lastRegOperation="";
            this.echoSocket = new Socket(serwer, 31114);
	    //echoSocket.setSoTimeout(300000);
	    this.echoSocket.setSoTimeout(900000);
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
		System.out.println("DUPA PRZY INICJACJI");

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
	
                char ch = (char) in.read();
		if(this.zapFlag)
		    logi.dopisz(ch);
	        while (true &&!throTimeOut)
                {
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

                    ch = (char) in.read();
		    if(this.zapFlag)
			logi.dopisz(ch);
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
		   
			init();
			this.regOperation(this.ostatniRegName);
			return makeAgain(polecenie);		   
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

                char ch = (char) in.read();
		if(this.zapFlag)
		    logi.dopisz(ch);
	        while (true&&throTimeOut)
                {
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
            //}
            //catch (Exception e)
            //{
		//System.out.println("WYwala read:");
		//e.printStackTrace();
		//return "ERROR";
	    //}



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
        String odp="";
                try{
                    odp=makeNew(regName,polecenie);
                    return odp;
                }
                catch(java.net.SocketException e )
                {
                    
                    init();
                    return makeNew(regName,polecenie);
                }
	
    }

    public String makeNew(String regName,String polecenie) throws NBIAnsException,java.io.IOException
    {
	//try
	//{
	String   odp="";
	boolean zalogowanyNe=false;
        String nazwaPolecenia=NewFile.getTokens(polecenie, "1", "1", ":");
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
	    odp=this.make2(polecenie);
	    if(odp.contains("RETCODE = 0")&&odp.contains(nazwaPolecenia))
		return odp;
	    else
	    {
		System.out.println(regName+" "+polecenie+"\r\n"+odp);
		;//throw new NBIAnsException("ERROR "+polecenie+"\r\n"+odp);
	    }

	}
	//System.err.println(regName+" "+polecenie+"\r\n"+odp);
	;
	//throw new NBIAnsException("ERROR "+polecenie+"\r\n"+odp);
	//}
	//catch(Exception e)
	/*{
	    try{
		e.printStackTrace();
	    LogManager lm = LogManager.getLogManager();
	    java.util.logging.Logger logger;
	    FileHandler fh = new java.util.logging.FileHandler("C:\\toolPP\\log_test2.txt", true);//FileHandler("log_test2.txt");

	    logger = Logger.getLogger("LoggingExample1");
	    //Logger logger2 = Logger.getLogger("LogExamle2");

	    lm.addLogger(logger);
	    logger.setLevel(Level.ALL);
	    fh.setFormatter(new java.util.logging.SimpleFormatter());


	    logger.addHandler(fh);
	    logger.log(Level.WARNING, "Blad przy komendzie:"+polecenie, e);
	    return null;
	    }
	    catch(Exception ee)
	    {
		e.printStackTrace();
		return null;
	    }

	}*/
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

    private boolean regOperation(String regName) throws NBIAnsException,java.io.IOException
    {
        boolean regOk=false;
        lastRegOperation=this.make2("REG NE:NAME=\""+regName+"\"");
        regOk=this.zawiera(lastRegOperation, "RETCODE = 0  Success");
        if(regOk)
            this.ostatniRegName=regName;

        return regOk;
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
            out.close();
            in.close();
            echoSocket.close();
	    //System.out.println("ZAMKNIETE");
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

	try{


	    String unreg="UNREG NE:NAME=\""+this.ostatniRegName+"\";\r";

	    out.println(unreg);
	    String userInput="	LGO:OP=\""+login+"\";\r";

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