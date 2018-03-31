package main;

import java.awt.Color;

import javax.swing.UIManager;

public class Main extends Controller {

	
	Main() {
	}
	
	public String sendCommand(String command) {
		
		//PARSING FOR DATA MANAGER LOADING
		if(command.contains("exit")) System.exit(0);
		if(command.startsWith("load")) {
			String split[] = command.split(" ");
			//load (id/stock) (id/stock) (m/i)
			if(split.length != 4) return "Invalid args";
			boolean mem = split[3].contains("m");
			int id = -1; 
			if(split[1].contains("id")) {
				try { id = Integer.parseInt(split[2]); } catch(Exception e) {return "Invalid id " + e.getMessage();}
				if(id == -1) return "Id out of range";
			} else if(split[1].contains("stock")) {
				id = this.convertId(split[2]);
				if(id == -2) return "Invalid stock";
			} else {
				return "Invlaid item type";
			}
		
			int status = isLoaded(id);
			if(status == 1) return id+" is loading";
			else if(status == 2) return id +" already filled";
			else if(status == 0) {
				dm.addUnderlying(getUnderlyingName(id), mem, -1, id);
				if(mem){ 
					return id+" loading started";
				}
				else { 
					gui.updateDataTable(id, "instanced");
					return id+" instanced";
				}
			}
		
		} else if(command.startsWith("clear")) {
			//clear data
			String split[] = command.split(" ");
			if(split.length != 3) return "Invalid args";
			int id = -1;
			if(split[1].contains("id")) {
				try { id = Integer.parseInt(split[2]); } catch(Exception e) {return "Invalid id " + e.getMessage();}
				if(id == -1) return "Id out of range";
			} else if(split[1].contains("stock")) {
				id = this.convertId(split[2]);
				if(id == -2) return "Invalid stock";
			} else {
				return "Invlaid item type";
			}
			
			int status = isLoaded(id);
			if(status == 1 || status == 2) {
				dm.clearData(id);
				underlyingStatus[id] = 0;
				gui.updateDataTable(id, "empty");
				System.gc();
				return "Cleard: " + id;
			} else if(status == 0) {
				return "already clear";
			}
		}
		return "Unknown command";
	}
	
	public static void main(String[] args) {
		
		//copy paste from stack overflow for dark look and feel
		  UIManager.put( "control", new Color( 128, 128, 128) );
		  UIManager.put( "info", new Color(128,128,128) );
		  UIManager.put( "nimbusBase", new Color( 18, 30, 49) );
		  UIManager.put( "nimbusAlertYellow", new Color( 248, 187, 0) );
		  UIManager.put( "nimbusDisabledText", new Color( 128, 128, 128) );
		  UIManager.put( "nimbusFocus", new Color(115,164,209) );
		  UIManager.put( "nimbusGreen", new Color(176,179,50) );
		  UIManager.put( "nimbusInfoBlue", new Color( 66, 139, 221) );
		  UIManager.put( "nimbusLightBackground", new Color( 18, 30, 49) );
		  UIManager.put( "nimbusOrange", new Color(191,98,4) );
		  UIManager.put( "nimbusRed", new Color(169,46,34) );
		  UIManager.put( "nimbusSelectedText", new Color( 255, 255, 255) );
		  UIManager.put( "nimbusSelectionBackground", new Color( 104, 93, 156) );
		  UIManager.put( "text", new Color( 230, 230, 230) );
		  try {
		    for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
		      if ("Nimbus".equals(info.getName())) {
		          javax.swing.UIManager.setLookAndFeel(info.getClassName());
		          break;
		      }
		    }
		  } catch (ClassNotFoundException e) {
		    e.printStackTrace();
		  } catch (InstantiationException e) {
		    e.printStackTrace();
		  } catch (IllegalAccessException e) {
		    e.printStackTrace();
		  } catch (javax.swing.UnsupportedLookAndFeelException e) {
		    e.printStackTrace();
		  } catch (Exception e) {
		    e.printStackTrace();
		  }
		
		new Main();
	}

}
