package nbipackage;

import java.io.File;
public class Systemowe
{
	public Systemowe(){

		this.user=System.getProperty("user.name").toUpperCase();
                
		this.separator=System.getProperty("file.separator");
		this.system=System.getProperty("os.name");
		this.current=System.getProperty("user.dir");

	}

	public String user(){
		return this.user.toUpperCase();
	}
        public String userSmal()
	{
		return this.user;
	}
	public String separator(){
		return this.separator;
	}

	public String system(){
		return this.system;
	}

	public String current(){
		return this.current;
	}

	public String ojciec(){
		File temp=new File(this.current);
               
		return temp.getParent() ;
	}

	public static String user;
	public static String separator;
	public static String system;
	public static String current;
	public static String ojciec;
}
