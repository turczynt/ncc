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
   private double amount;
   public NBIAnsException(String komenda,String odp)
   {
       super("BLEDNA WARTOSC ODPOWIEDZI NBI DLA KOMENDY:"+komenda+" :"+odp);
       System.err.println("BLEDNA WARTOSC ODPOWIEDZI NBI DLA KOMENDY:"+komenda+ ":"+odp);
      
   }
   /*public double getAmount()
   {
      return amount;
   }*/
}
