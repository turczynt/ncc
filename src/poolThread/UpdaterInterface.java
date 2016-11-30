/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package poolThread;

/**
 *
 * @author turczyt
 */
public interface UpdaterInterface  extends Runnable
{
    public static final int ADD=0;
    public static final int TRUNCATE=1;

    public static final int CLEANER_NODE=2;
    public static final int CLEANER_BTS=3;
    public static final int CLEANER_CELL_2G=4;
    public static final int CLEANER_CELL_3G=5;
    public static final int CLEANER_BRD_2G=6;
    public static final int CLEANER_BRD_3G=7;
    public static final int CLEANER_ENODE=8;
    public static final int CLEANER_CELL_4G=9;



    
    @Override
    public void run();
    public void truncate()throws java.sql.SQLException;
    public boolean add()throws java.sql.SQLException;
    public String odp()throws java.sql.SQLException;
    public boolean isDone()throws java.sql.SQLException;
    public void setDOA(mysqlpackage.DataSource DOA);
    
}