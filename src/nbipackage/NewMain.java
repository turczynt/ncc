/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nbipackage;

/**
 *
 * @author turczyt
 */
public class NewMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
	String[] argsTab=new String[]{};//"-u","turczyt","-p","****"};
	LoggerNorth loger=new LoggerNorth("C:\\toolPP\\WO\\WO_TO_DO\\","turczyt",argsTab);
	System.out.println("login="+loger.getLogin()+" haslo="+loger.getPasswd());
	
    }
}
