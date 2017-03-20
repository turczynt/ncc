/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nbipackage;

/**
 *
 * @author turczyt
 */
public class NBIAnsException extends Exception
{
    
    
   public static int DISCONNECT_PROBLEM=0;
   public static int NOT_SUSPECTED_ANSWER=1;
   public static int UNDEFINE_PROBLEM=-1;
   private double amount;
   private int problem;
   public NBIAnsException(String komenda,String odp)
   {
       super("BLEDNA WARTOSC ODPOWIEDZI NBI DLA KOMENDY:"+komenda+" :"+odp);
       this.problem=NBIAnsException.UNDEFINE_PROBLEM;
       System.err.println("BLEDNA WARTOSC ODPOWIEDZI NBI DLA KOMENDY:"+komenda+ ":"+odp);
      
   }
   public NBIAnsException(String komenda,String odp,int problem)
   {
       super("BLEDNA WARTOSC ODPOWIEDZI NBI DLA KOMENDY:"+komenda+" :"+odp);
       this.problem=problem;
       System.err.println("BLEDNA WARTOSC ODPOWIEDZI NBI DLA KOMENDY:"+komenda+ ":"+odp);
      
   }
   public int getProblem()
   {
       return this.problem;
   }
   
   /*public double getAmount()
   {
      return amount;
   }*/
}
