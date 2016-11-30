package poolThread;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
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
public class GrupaZadan extends Thread
{
     String nazwa;
    String output;
    java.util.ArrayList<UpdaterInterface> lista;
    int maxThread;
    List<Future<Object>> answers;
    mysqlpackage.DataSource DOA;
    java.util.concurrent.ExecutorService serw=null;
    Logger loger;
    

    public GrupaZadan(java.util.ArrayList<UpdaterInterface> lista,int maxThread,String identyfikator,Logger loger)
    {
	output="";
	this.lista=lista;
	this.maxThread=maxThread;
	this.loger=loger;
	nazwa=identyfikator;
	

    }

    public GrupaZadan(int maxThread,String identyfikator,Logger loger)
    {
	output="";

	this.maxThread=maxThread;
	lista=new java.util.ArrayList<UpdaterInterface>();
	this.loger=loger;
	nazwa=identyfikator;
	
    }

    @Override

    public void run()
    {
	
	try
	{

	   serw=  java.util.concurrent.Executors.newFixedThreadPool(maxThread);
           
	   loger.info("["+this.nazwa+"]START GRUPY ZADAN");
	   List<Callable<Object>> todo = new ArrayList<Callable<Object>>(lista.size());
	   for(int z=0;z<lista.size();z++)
	   {
	       todo.add(Executors.callable(lista.get(z)));


	    	//serw.submit(lista.get(z));
	   }
	   answers = serw.invokeAll(todo);///,10,TimeUnit.MINUTES);
           
	   serw.shutdownNow();
	  // timer.destroy();


	   /*
	    * serw=  java.util.concurrent.Executors.newFixedThreadPool(1);
	   loger.info("POOL START");
	   List<Callable<Object>> todo = new ArrayList<Callable<Object>>(KolejkaGrup.size());
	   for(int z=0;z<KolejkaGrup.size();z++)
	   {
	       todo.add(Executors.callable(KolejkaGrup.get(z)));
	   }
	   serw.invokeAll(todo);
	    */

	   String tasksOdp="";
	    for(Future f : answers)
	    {
		try
		{

		   tasksOdp=tasksOdp+";"+f.get();
		}
		catch(Exception e)
		{
		    loger.throwing(output, tasksOdp, e);
		}
	    }



	   //

	   //loger.info("ZAKONCZONO GRUPE ZADAN "+this.nazwa+" "+tasksOdp);
	   loger.info("["+this.nazwa+"]ZAKONCZONO GRUPE ZADAN "+tasksOdp);

	}
	catch(java.lang.InterruptedException ew)
	{
	    System.out.println("######################################## INTERUPT EXCEPTION EEE");
//	    ew.printStackTrace();
	    //loger.info("EXCEPTION "+this.nazwa+" "+ew.getMessage());
	    loger.throwing(this.getClass().getName()+"."+this.nazwa,"run", ew);
	     ew.printStackTrace();
	}
	catch(Exception e)
	{
	     //loger.info("["+java.util.Calendar.getInstance().getTime().toString()+"]POOL ERROR);
	    loger.throwing(this.getClass().getName()+"."+this.nazwa,"run", e);
	    e.printStackTrace();
	}
	finally
	{
	    try{

	  

		serw.shutdown();
		loger.info("["+this.nazwa+"] EXECUTOR  GRUPY ZADAN SHUTDOWN="+serw.isShutdown()+" ");
	    }
	    catch(Exception ee)
	    {

		loger.throwing(this.getClass().getName()+"."+this.nazwa,"FINALLY_SERW_SHUTDOWN", ee);
	    }
	  
	}
    }

    public void setDOA(mysqlpackage.DataSource DOA)
    {
	this.DOA=DOA;
    }
    public String getOutputInfo()
    {
	try
	{
	    for(int i=0;i<lista.size();i++)
		System.out.println(lista.get(i).odp());
	    
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	}
	return output;
    }

    public void setLista(ArrayList<UpdaterInterface> lista)
    {
	/*for(int i=0;i<lista.size();i++)
	    lista.get(i).setDOA(DOA);*/
	this.lista = lista;
    }

    public void setMaxThread(int maxThread)
    {
	this.maxThread = maxThread;
    }
    public void add(UpdaterInterface thread)
    {
	//thread.setDOA(DOA);

	this.lista.add(thread);
    }
    public List<Future<Object>> getAnswers()
    {
	return this.answers;
    }

}