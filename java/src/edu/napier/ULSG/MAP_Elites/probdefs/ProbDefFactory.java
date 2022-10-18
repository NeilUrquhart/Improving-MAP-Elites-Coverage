package edu.napier.ULSG.MAP_Elites.probdefs;

public class ProbDefFactory {
	
	public static ProblemDefinition createKeyGen(String type) {
		ProblemDefinition k =null;
		try {
			k = (ProblemDefinition)Class.forName("edu.napier.ULSG.MAP_Elites.probdefs."+type).newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return k;
	}
}
