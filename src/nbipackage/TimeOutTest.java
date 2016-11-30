/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nbipackage;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author turczyt
 */
public class TimeOutTest
{

    /**
     * @param args the command line arguments
     */
       public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(new Task("TESTOWA KOMENDA"));

        try {
            System.out.println("Started..");
            System.out.println(future.get(3, TimeUnit.SECONDS));
            System.out.println("Finished!");
        } catch (TimeoutException e) {
            future.cancel(true);
            System.out.println("Terminated!");
        }

        executor.shutdownNow();
    }
}

class Task implements Callable<String> {
   String komenda;
    public Task(String komenda)
    {
        this.komenda=komenda;
    }
     @Override
    public String call() throws Exception {
        Thread.sleep(4000); // Just to demo a long running task of 4 seconds.
        return komenda;
    }
}
