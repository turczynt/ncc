/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package poolThread;

/**
 *
 * @author turczyt
 */
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class NewMain {
    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(new Task());


        try {
            System.out.println("Started..");
//            System.out.println(future.get(3, TimeUnit.SECONDS));
            System.out.println("Finished!");
        } catch (Exception e) {
            System.out.println("Terminated!");
	    e.printStackTrace();
        }

        executor.shutdownNow();
    }
}

class Task implements Callable<String> {
    @Override
    public String call() throws Exception {
        Thread.sleep(4000); // Just to demo a long running task of 4 seconds.
        return "Ready!";
    }
}