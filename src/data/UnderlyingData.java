package data;

import java.io.File;
import java.util.ArrayList;

import loader.FileLoader;
import main.Controller;

public class UnderlyingData {
	
	private final String masterPath = "B:\\option_database\\cleaned\\dailyData\\";
	private String path;
	private boolean memBased = false; 
	
	private String underlying;
	private int id;
	private boolean mem; 
	
	private String startDate;
	private String endDate;
	
	private File files[];
	private ArrayList<DailyData> dData = new ArrayList<DailyData>();
	
	
	//part is for only loading a percentage of data 
	public UnderlyingData(String underlying, boolean b, double part, int id) {
		this.underlying = underlying;
		this.id = id;
		this.mem = b;
		this.memBased = b;
		path = masterPath+underlying;
		
		//get all files in directory
		File file = new File(path);
		files = file.listFiles();

		if(part > 0) {
			int max = (int)((double)files.length*part);
			File[] fss = new File[max];
			for(int i = 0; i < max; i++) fss[i] = files[i];
			files = fss;
		}
		
		startDate = files[0].getName();
		endDate = files[files.length-1].getName();
		startDate = startDate.replaceAll(" ", "");
		endDate = endDate.replaceAll(" ", "");
		
//		System.out.println(startDate + ", " + endDate);
		
//		System.out.println(startDate + ", " + endDate + ", " + files.length);
	}
	
	//loading data
	public void loadAllData(Controller c) {
		if(memBased) 
			for(int i = 0; i < files.length; i++) {
				dData.add(loadDate(i));
				
				double percentLoaded = ((double)(i)/(double)files.length);
				percentLoaded*=100.0;
				percentLoaded = Math.round(percentLoaded);
				
				c.updateTable(id, percentLoaded+"%"); //give the user an update on loading
			}
	}
	
	public DailyData getDailyData(int i) {
		if(memBased) {
			return dData.get(i);
		} else {
			return loadDate(i);
		}
	}
	
	private FileLoader loader = new FileLoader();
	private DailyData loadDate(int i) {
		String date;
		double closePrice;
		double iv; 
		int totalVolume, totalInterest; 
		String lines[] = loader.loadData(files[i].getPath()+"\\generalData.csv");

		//load general data
		date = files[i].getName().trim();
		closePrice = Double.parseDouble(lines[1].split(",")[2]);
		iv = Double.parseDouble(lines[1].split(",")[3]);
		totalVolume = Integer.parseInt(lines[1].split(",")[4]);
		totalInterest = Integer.parseInt(lines[1].split(",")[5]);
		
		//get the paths for expiration data files
		File f[] = files[i].listFiles();
		
		
		//load expirations 
		ArrayList<ExpirationData> expire = new ArrayList<ExpirationData>();
		ArrayList<String> expireDates = new ArrayList<String>();
		for(int ii = 2; ii < lines.length; ii++) {
			//these will be the expirations
			ExpirationData data = new ExpirationData();
			String split[] = lines[ii].split(",");
			expireDates.add(split[0]);
			data.setDetails(split[0], Integer.parseInt(split[1]), (split[2].equals("1")), split[12]);
			data.setIv(Double.parseDouble(split[3]), Double.parseDouble(split[4]), Double.parseDouble(split[5]));
			data.setVolume(Integer.parseInt(split[6]), Integer.parseInt(split[7]), Integer.parseInt(split[8]));
			data.setInterest(Integer.parseInt(split[9]), Integer.parseInt(split[10]), Integer.parseInt(split[11]));
			data.loadOptions(f[ii-2].getPath());
			expire.add(data);
		}
		
		GeneralData general = new GeneralData();
		general.create(closePrice, iv, totalVolume, totalInterest, expireDates, date);
		DailyData daily = new DailyData(general, expire);
		return daily;
	}

	public ArrayList<DailyData> getAllDays() {
		return dData;
	}
	
	public int getMaxData() {
		return files.length;
	}
	
	public String getUnderlying() {return underlying;}
	public int getId() {return id;}
	public boolean memBased() {return mem;}
}
