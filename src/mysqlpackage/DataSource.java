/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mysqlpackage;


import java.util.logging.Logger;



import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

//import org.apache.log4j.Logger;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.sql.ResultSet;

public class DataSource {

    /**
    * A singleton that represents a pooled datasource. It is composed of a C3PO
    * pooled datasource. Can be changed to any connect pool provider
    */
    private Properties props;
    private ComboPooledDataSource cpds;
    private static DataSource datasource;
    
   // private static final Logger log = Logger.getLogger(DataSource.class);

    private DataSource() throws IOException, SQLException {
        // load datasource properties
        //log.info("Reading datasource.properties from classpath");
        //props = Utils.readProperties("datasource.properties");


	cpds = new ComboPooledDataSource();
        /*cpds.setJdbcUrl("jdbc:mysql://localhost:3306");
	cpds.setUser("root");
        cpds.setPassword("vertrigo");*/
	cpds.setJdbcUrl("jdbc:mysql://172.16.5.52:3306");
	cpds.setUser("turczyt");
	cpds.setPassword("turczyt123");

        cpds.setInitialPoolSize(new Integer((String) "1"));
        cpds.setAcquireIncrement(new Integer((String) "00"));
        cpds.setMaxPoolSize(new Integer((String) "12"));
        cpds.setMinPoolSize(new Integer((String) "1"));
	
        //cpds.setMaxStatements(new Integer((String) "200"));

    }
    public void setServerPort(String serwer,String port)
    {
	cpds.setJdbcUrl("jdbc:mysql://"+serwer+":"+port);
    }
    public void setUserPasword(String user,String pass)
    {
	cpds.setUser(user);
        cpds.setPassword(pass);
    }

    public static DataSource getInstance() throws IOException, SQLException {
        if (datasource == null) {
              datasource = new DataSource();
              return datasource;
            } else {
              return datasource;
            }
    }

    public Connection getConnection() throws SQLException {

	
        return this.cpds.getConnection();
    }
    public OdpowiedzSQL createAnswer(java.sql.ResultSet input)
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