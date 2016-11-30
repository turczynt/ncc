/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MML_UPDATER;

import nbipackage.NewFile;

/**
 *
 * @author turczyt
 */
public class test
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        // TODO code application logic here
        String input="ADD UCELL: ABCD=";
        String commandName=input.substring(input.indexOf(" ")+1,input.indexOf(":"));
        String commandType=input.substring(0, input.indexOf(" "));
        System.out.println(commandType+"|"+commandName);
        NewFile mml=new NewFile("C:\\Printouts\\KATBSC4.mml");
         String[] cellParametrizations=mml.getLinia("[\\s:]{1}CELLID="+573+"[,;]");
        for(int z=0;z<cellParametrizations.length;z++)
            System.out.println(cellParametrizations[z]);
        System.out.println("cellParametrizations.size="+cellParametrizations.length);

    }
}
