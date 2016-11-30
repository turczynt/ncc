/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nbipackage;


/**
 *
 * @author turczyt
 */
public class argsFactory
{
    boolean nbiExecutorFlag;
    boolean userPaswordFlag;
    boolean specFileFlag;
    String[] args;

    public argsFactory(String[] args,String help)
    {
	this.args = args;
    }
}
