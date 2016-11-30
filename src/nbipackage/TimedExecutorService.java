/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nbipackage;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author turczyt
 */
public class TimedExecutorService extends ThreadPoolExecutor {
    long timeout;
    public TimedExecutorService(int numThreads, long timeout, TimeUnit unit) {
        super(numThreads, numThreads, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(numThreads + 1));
        this.timeout = unit.toMillis(timeout);
    }

    @Override
    protected void beforeExecute(final Thread thread, Runnable runnable) {
        Thread interruptionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Wait until timeout and interrupt this thread
                    Thread.sleep(timeout);
                    System.out.println("The runnable times out.");
                    thread.interrupt();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        interruptionThread.start();
    }
    
}
