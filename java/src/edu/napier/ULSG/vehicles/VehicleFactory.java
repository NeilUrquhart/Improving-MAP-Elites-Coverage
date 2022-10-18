package edu.napier.ULSG.vehicles;

public class VehicleFactory {
	public static Vehicle[] createFleet(String[] vehicles) {
		Vehicle[] res = new Vehicle[vehicles.length];
		for(int x=0; x < vehicles.length;x++){
			res[x]=createVehicle(vehicles[x]);
		}
		return res;
	}
	public static Vehicle createVehicle(String type) {
		Vehicle v =null;
		try {
			v = (Vehicle)Class.forName("edu.napier.ULSG.vehicles."+type).newInstance();
			System.out.println(v.toString());
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
		
		return v;
	}
}
