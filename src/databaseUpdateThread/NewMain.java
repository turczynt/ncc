/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseUpdateThread;

/**
 *
 * @author turczyt
 */
public class NewMain
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        // TODO code application logic here
        String in="123456789";
        if(in.matches("[0-9]{8}"))
            System.out.println("OK");
        else
            System.out.println("Dupa");
        
        
        
    }
}
