/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nbipackage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author turczyt
 */
public class MmlKommand
{
   private String commandType;//add,act,dea ..
   private String commandName;// ucellsetup,nodeb itp itd : commandType commandName: parname1=val1,parname2=val2 ...
   //public String input;
   private HashMap<String,String> paramValues;//key= param name, val= param value;

   public MmlKommand(String input)
   {
       this.commandType=null;
       this.commandName=null;
       //this.input=input;
       this.paramValues=new HashMap<>() ;
       parseInput(input);
   }

   private void parseInput(String input)
   {
       try
       {
           int indexOfSpace=input.indexOf(" ");
           int indexOfColon=input.indexOf(":");
	   this.commandName=input.substring(indexOfSpace+1,indexOfColon);
           this.commandType=input.substring(0, indexOfSpace);

           if(!commandName.contains("UIOPTRULE"))
           {
	   String[] parameters=input.substring(indexOfColon+1,input.indexOf(";")).split(",");
           
	   for(int w=0;w<parameters.length;w++)
	   {
               int idexOfEqual=parameters[w].indexOf("=");
               if(idexOfEqual!=-1)
               {
                    String paramName=parameters[w].substring(0, idexOfEqual).trim();
                    String paramVal=parameters[w].substring(idexOfEqual+1).trim();
                    if(paramVal.contains("\""))
                        paramVal=paramVal.replaceAll("\"", "");
	            this.paramValues.put(paramName,paramVal);
               }
               else
                   System.out.println("ZJEBANE W LINI:"+input+"\r\nW PATAMETRZE="+parameters[w]);
	   }
           }
       }
       catch(Exception ee)
       {
	    ee.printStackTrace();
       }
   }

   public String getValue(String paramName)
    {

       if(this.paramValues.containsKey(paramName.toUpperCase()))
	   return this.paramValues.get(paramName.toUpperCase());
       else
	   return null;
   }

    public String getCommandName()
    {
	return commandName;
    }

    public void setCommandName(String commandName)
    {
	this.commandName = commandName;
    }

    public String getCommandType()
    {
	return commandType;
    }

    public void setCommandType(String commandType)
    {
	this.commandType = commandType;
    }
    public java.util.ArrayList<String> getParamNames()
    {
	Set<String> tmp=this.paramValues.keySet();
        
	java.util.ArrayList<String> names=new java.util.ArrayList<>();
	//String[] namess=(String[])tmp.toArray();
       
        Object[] klucze= tmp.toArray();
        for(int z=0;z<klucze.length;z++)
        names.add((String)klucze[z]);
	return names;
    }

    public boolean ifContainsParam(String paramName)
    {
	 if(paramName!=null&&this.paramValues.containsKey(paramName.toUpperCase()))
	     return true;
	 else
	     return false;
    }
}
