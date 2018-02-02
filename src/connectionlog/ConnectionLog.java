/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connectionlog;


//JavaImports
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Daniel Sha
 *  Note: Directory is hard coded: "D:\\ConnectionLog.txt" 
 *                  . Changeable as Static String directoryAndName
 * 
 *
 *  Note: Website to Ping is also hardcoded: "www.google.com"
 *                  . Changeable as Static String websiteToCheck
 *   */
public class ConnectionLog {
    
    static boolean fileExists = false;
    static String directoryAndName = "D:\\ConnectionLogData.txt";
    static String percentageFile = "D:\\ConnectionLogPercentage.txt";
    static String websiteToCheck = "www.google.com";

    /**
     * @param args the command line arguments
     */
    
    static String getTimpeStampString(){
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        return timeStamp;
    }
    
    static boolean checkPingSuccess(String pingReturnString){
    
        if(pingReturnString.contains("Ping request could not find host"))
        {return false;}
        else
        {return true;}
    }
    
    static void printTimeStamp() throws FileNotFoundException {
        if(fileExists)
        {
            File fileName = new File(directoryAndName);
            PrintWriter mrPrintWriter = new PrintWriter(new FileOutputStream(fileName,true));
            String timeStamp = "Couldn't get TimeStamp";
            timeStamp = getTimpeStampString();
            mrPrintWriter.println("");
            mrPrintWriter.append(timeStamp);
            mrPrintWriter.print("   ");
            mrPrintWriter.close();
        }
        else{
            File fileName = new File(directoryAndName);
            PrintWriter mrPrintWriter = new PrintWriter(fileName);
            String timeStamp = "Couldn't get TimeStamp";
            timeStamp = getTimpeStampString();
            mrPrintWriter.println(timeStamp);
            mrPrintWriter.close();
        }
        
    }
    
    static void create() throws FileNotFoundException {
        File fileName = new File(directoryAndName);
        PrintWriter mrPrintWriter = new PrintWriter(fileName);
        mrPrintWriter.println("DATA LOG");
        mrPrintWriter.println("");
        mrPrintWriter.close();
        
        File updatingFile = new File(percentageFile);
        PrintWriter mrsPrintWriter = new PrintWriter(updatingFile);
        mrsPrintWriter.println("0/0");
        mrsPrintWriter.close();
      }
    
     public static ArrayList<String> runSystemCommand(String command) {
         //Shared by: https://gist.github.com/madan712/4509039
         //Modified to return string arraylist of system.out
         
         ArrayList<String> pingStrArrLst = new ArrayList<>();

		try {
			Process p = Runtime.getRuntime().exec(command);
			BufferedReader inputStream = new BufferedReader(
					new InputStreamReader(p.getInputStream()));

			String s = "";
			// reading output stream of the command
			while ((s = inputStream.readLine()) != null) {
				System.out.println(s);
                                pingStrArrLst.add(s);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
                
              return pingStrArrLst;
	}
     
     public static String updatedPercentage(boolean connectionResult) throws FileNotFoundException, IOException {
         
         BufferedReader in = new BufferedReader(new FileReader(percentageFile));
         String fractionLine = "";
         fractionLine = in.readLine();
         int slashCharPosition = fractionLine.indexOf("/");
         String strNumerator = fractionLine.substring(0, slashCharPosition);
         String strDenominator = fractionLine.substring(slashCharPosition+1);
         int numerator = Integer.parseInt(strNumerator);
         int denominator = Integer.parseInt(strDenominator);
         if(connectionResult)
            {
                numerator++;denominator++;
            }
         else{denominator++;}
         strNumerator = Integer.toString(numerator);
         strDenominator = Integer.toString(denominator);
         String toReturn = strNumerator + "/" + strDenominator;
         return toReturn;
     }
     
     public static void printPingResults(ArrayList<String> pingArrayList) throws FileNotFoundException, IOException {
         if(fileExists)
        {
            String currentLine;
            
            File fileName = new File(directoryAndName);
            PrintWriter mrPrintWriter = new PrintWriter(new FileOutputStream(fileName,true));
            String pingResultLine = pingArrayList.get(0);
            if(checkPingSuccess(pingResultLine))
                {
                    mrPrintWriter.print("   Connected   ");
                    for(int arrayListIndex = 0; arrayListIndex < pingArrayList.size(); arrayListIndex++)
                    {
                        currentLine = pingArrayList.get(arrayListIndex);
                        if(currentLine.contains("Minimum"))
                        {
                            mrPrintWriter.print(currentLine);
                        }
                    }
                    mrPrintWriter.close();
                    String percentageString = updatedPercentage(true);
                    PrintWriter mrsPrintWriter = new PrintWriter(new FileOutputStream(percentageFile, false));
                    mrsPrintWriter.println(percentageString);
                    mrsPrintWriter.close();
                    
                }
            else //pingFailure
                {
                    mrPrintWriter.print("   Disconnected   ");
                    mrPrintWriter.close();
                    String percentageString = updatedPercentage(false);
                    PrintWriter mrsPrintWriter = new PrintWriter(new FileOutputStream(percentageFile, false));
                    mrsPrintWriter.println(percentageString);
                    mrsPrintWriter.close();
                    
                }
            
            
            
            
        }
        else{
            //NO Else as file should ALWAYS EXIST by this point. (Possible error location if code changed).
        }
     }
    
    public static void main(String[] args) throws FileNotFoundException, InterruptedException, IOException {
        // TODO code application logic here
        fileExists = false;
        
        //Check if DataLog has already been created
        File dataLog = new File(directoryAndName);
        if(!(dataLog.exists()))
            {
                //Create New Log
                create();
                fileExists = true;
                printTimeStamp();
                ArrayList<String> pingStrArrLst = runSystemCommand("ping " + websiteToCheck);
                printPingResults(pingStrArrLst);
                
                
            }
        else
            {
                //Append to Log
                fileExists = true;
                printTimeStamp();
                ArrayList<String> pingStrArrLst = runSystemCommand("ping " + websiteToCheck);
                printPingResults(pingStrArrLst);
            }
        
            
            while(true)
            {
                TimeUnit.MINUTES.sleep(1);
                //Printing Time Stamp
                printTimeStamp();
                ArrayList<String> pingStrArrLst = runSystemCommand("ping " + websiteToCheck);
                printPingResults(pingStrArrLst);
            }
                
            
    }
    
}
