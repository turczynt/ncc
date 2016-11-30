package poolThread;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author turczyt
 */
public class Pool extends Thread{
    //int maxRunThread;
    java.util.ArrayList<Runnable> KolejkaGrup;
    boolean endTasks;
    boolean working;
    List<Future<Object>> answers;
    mysqlpackage.DataSource DOA;
    Logger loger;
 

    public Pool(mysqlpackage.DataSource DOA,Logger loger)
    {
	this.DOA=DOA;
//	this.maxRunThread = maxRunThread;
	this.KolejkaGrup = new java.util.ArrayList<Runnable>();
	endTasks=true;
	working=false;
	this.loger=loger;
    }

    public Pool(mysqlpackage.DataSource DOA,ArrayList<Runnable> KolejkaGrup,Logger loger)
    {
	this.DOA=DOA;
	//this.maxRunThread = maxRunThread;
	this.KolejkaGrup = KolejkaGrup;
	endTasks=false;
	working=false;
	this.loger=loger;
    }

    public int taskToExecute()
    {
        if(KolejkaGrup!=null)
            return KolejkaGrup.size();
        else
            return 0;
    }
    


    public ArrayList<Runnable> getKolejkaGrup()
    {
	return KolejkaGrup;
    }

    

    public void setKolejkaGrup(ArrayList<Runnable> KolejkaGrup)
    {
	this.KolejkaGrup = KolejkaGrup;
	endTasks=false;
	working=false;
    }

    public void add2KolejkaGrup(Runnable thread)
    {
	this.KolejkaGrup.add(thread);
	endTasks=false;
	working=false;
    }

    @Override
    public void run()
    {
        super.run(); //To change body of generated methods, choose Tools | Templates.
    }

    
    
    @Override
    public void start()
    {
	java.util.concurrent.ExecutorService serw=null;
	try
	{
	   endTasks=false;
	   working=true;
	   serw=  java.util.concurrent.Executors.newFixedThreadPool(1);
	   loger.info("POOL START");
	   List<Callable<Object>> todo = new ArrayList<Callable<Object>>(KolejkaGrup.size());
	   for(int z=0;z<KolejkaGrup.size();z++)
	   {
	       todo.add(Executors.callable(KolejkaGrup.get(z)));
	   }
	   serw.invokeAll(todo);

           
	   endTasks=true;
	   working=false;
	   loger.info("POOL END");
	}
	catch(Exception e)
	{
	     //loger.info("["+java.util.Calendar.getInstance().getTime().toString()+"]POOL ERROR);
	    loger.info(" POOL ERROR:"+e.getLocalizedMessage());
	    e.printStackTrace();
	}
	finally
	{
	    loger.info(" STATUS POOLa"+serw.isShutdown()+" "+serw.isTerminated()+" "+serw.toString());
	    if(serw!=null)
	    {
		
		serw.shutdown();
		loger.info("POOL SHUTDOWN"+serw.isShutdown()+" ");
	    }
	}
    }

    public boolean isRunning()
    {
	return this.working;
    }

    public boolean isDoAll()
    {
	return endTasks;
    }
    public List<Future<Object>> getAnswers()
    {
	return answers;
    }
}
