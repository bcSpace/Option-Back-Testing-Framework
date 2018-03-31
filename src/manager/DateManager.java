package manager;

import java.util.ArrayList;

import loader.FileLoader;
import loader.FileWriter;

public class DateManager {
	
	//CURRENTLY UNUSED
	//CURRENTLY UNUSED
	//CURRENTLY UNUSED
	//CURRENTLY UNUSED
	//CURRENTLY UNUSED
	//CURRENTLY UNUSED
	//CURRENTLY UNUSED
	
	private static String path = "C:\\option_database\\general\\dates.csv"; 
	private static ArrayList<String> dates = new ArrayList<String>();
	
	private static int startingDay = 99999;
	private static int currentDay = 99999;
	
	public DateManager() {
	}
	
	public static void init() {
		FileLoader fl = new FileLoader();
		String s[] = fl.loadData(path);
		
		for(int i = 0; i < s.length; i++) {
			dates.add(s[i].replaceAll(" ", ""));
		}
		
		String sss = "";
		ArrayList<String> list = new ArrayList<String>();
		for(int i = 0; i < dates.size(); i++) {
			String ss[] = dates.get(i).split("-");
			if(ss[1].length() == 1) ss[1] = "0"+ss[1];
			if(ss[2].length() == 1) ss[2] = "0"+ss[2];
			list.add(ss[2]+"-"+ss[0]+"-"+ss[1]);
			sss += list.get(list.size()-1)+"\n";
		}
	}
	
	public static int getMod(String startDate) {
		for(int i = 0; i < dates.size(); i++) {
			if(startDate.contains(dates.get(i))){ 
				if(i < startingDay){
					startingDay = i;
					currentDay = startingDay;
				}
				return i;
			}
		}
		System.out.println("Big boi error " + startDate);
		System.exit(0);
		return 0;
	}
	
	public static void nextDay() {
		currentDay++;
	}
	
	public static void clear() {
		startingDay = 99999;
		currentDay = 99999;
	}
	
	public static int getDay() {return currentDay;}
	
}
