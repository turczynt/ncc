package mysqlpackage;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



/**
 *
 * @author turczyt
 * Klasa odpowiedzialna za komunikacje z baza danych
 */
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;

public class Baza {

    String login;
    String paswd;
    String ip;
    String URL;
    Connection con;
    String CatalogName;
    Statement stmt;
    boolean connectionSucced;
    int port;
    public Baza(String urlBegin,int port,String login, String paswd, String ip,String CatalogName)
    {
        this.login = login;
        this.paswd = paswd;
        this.ip = ip;
        this.port=port;
        //URL="jdbc:sybase:Tds:"+ip+":4100";
        this.URL=urlBegin+ip+":"+port;
        
        this.CatalogName=CatalogName;
       
    }

    public Baza()
    {
        this.login = null;
        this.paswd = null;
        this.ip = null;
        this.port= -1;
        //URL="jdbc:sybase:Tds:"+ip+":4100";
        this.URL=null;

        this.CatalogName=null;
       
    }

    public boolean connect() throws java.sql.SQLException
    {
        connectionSucced=false;
        //try
        {

	    DriverManager.setLoginTimeout(0);
	    this.con = DriverManager.getConnection(URL, login, paswd);

            //conn = DriverManager.getConnection("jdbc:mysql://172.16.5.38:3306/oncall?user=" + login + "&password=" + pass + "&useUnicode=yes&characterEncoding=UTF-8");
	    if(CatalogName!=null)
                this.con.setCatalog(CatalogName);

	    //con.setHoldability(Connection.);
            this.stmt = con.createStatement();

            connectionSucced=true;
        }
        //catch(Exception ee)
        //{
          //  ee.printStackTrace();
	//}
        return connectionSucced;
    }

    public boolean disconnect()
    {

        try
        {

	    stmt.close();
            this.con.close();
            //conn = DriverManager.getConnection("jdbc:mysql://172.16.5.38:3306/oncall?user=" + login + "&password=" + pass + "&useUnicode=yes&characterEncoding=UTF-8");

        }
        catch(Exception ee)
        {
            //System.out.println(ee.toString());
            ee.printStackTrace();
        }
        return connectionSucced;
    }

    public OdpowiedzSQL wykonajZapytanie(String zapytanie) throws java.sql.SQLException
    {
        java.sql.ResultSet rs=this.executeRequest(zapytanie);
        return this.createAnswer(rs);
    }

    public java.sql.ResultSet executeRequest(String request) throws java.sql.SQLException
    {
           //con.setCatalog("logdb");
	    String[] kolejne=request.split(";");
		ResultSet rs=null;
		for(int z=0;z<kolejne.length;z++)
		    rs = stmt.executeQuery(kolejne[z]);

		//con.commit();
             return rs;
       
    }

    public boolean executeQuery(String query) throws java.sql.SQLException
    {
        //try
	//{
            boolean odp=stmt.execute(query);

	    
	    if(stmt.getWarnings()!=null)
	    {
		System.out.println(stmt.getWarnings().getMessage());
		;//throw new java.lang.Exception();
		
	    }
	   // con.commit();
            return  odp;
        //}
        //catch(Exception ee)
        //{
            //System.out.println(ee.toString());
	    
          //  ee.printStackTrace();
	    //return false;
        //}
        
    }
    public boolean executeBatch(String batch)
    {
        try
	{

	    String[] kom=batch.split(";");
	    for(int z=0;z<kom.length;z++)
	    {
		stmt.addBatch(kom[z]);
	    }
            stmt.executeBatch();

	    //con.commit();
	    if(stmt.getWarnings()!=null)
	    {
		System.out.println(stmt.getWarnings().getMessage());

		return false;
	    }
	    else
		return true;

        }
        catch(Exception ee)
        {
            //System.out.println(ee.toString());

            ee.printStackTrace();
	    return false;
        }

    }

    public static OdpowiedzSQL createAnswer(java.sql.ResultSet input)
    {
	String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT_NOW);
	java.util.ArrayList<String> nazwyKolumn=new java.util.ArrayList<String>();
        java.util.ArrayList<java.util.ArrayList<String>> rekordy=new java.util.ArrayList<java.util.ArrayList<String>>();
        try
	{
            java.sql.ResultSetMetaData MetaData = input.getMetaData();
            int numberOfColumns = MetaData.getColumnCount();
            for (int i = 1; i < numberOfColumns + 1; i++)
            {
		String columnName = MetaData.getColumnLabel(i);
		nazwyKolumn.add(columnName);
	    }
            while (input.next())
            {
                try
		{
                    java.util.ArrayList<String> rekTmp=new java.util.ArrayList<String>();
                    for (int i = 1; i < numberOfColumns + 1; i++)
                    {
			String valTMp=input.getString(i);
                        if(valTMp!=null)
                        {
                            if(MetaData.getColumnName(i).equalsIgnoreCase("beginDate"))
                            {
                                Long ll=new java.lang.Long(valTMp+"000");
                                java.sql.Time tst=new java.sql.Time(ll);
                                String tmpData=sdf.format(tst);
                                rekTmp.add(tmpData);
                            }
                            else
                                rekTmp.add(valTMp);
                        }
                        else
                        {
                            rekTmp.add("");
                        }
                    }
                    rekordy.add(rekTmp);
                }
                catch(Exception ewewe)
                {
  //                  System.out.println(ewewe.toString());
                    ewewe.printStackTrace();
                }
            }
        }
        catch(Exception ee)
        {
	    ee.printStackTrace();
        }
        OdpowiedzSQL odp=new OdpowiedzSQL(input,nazwyKolumn,rekordy);
      //  System.out.println("zakonczenie tworzenia odp");
        return odp;
    }
}
