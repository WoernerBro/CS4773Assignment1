package misc;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

public class RecordProcessor {
	private static String [] firstName;
	private static String [] lastName;
	private static int [] age;
	private static String [] employeeType;
	private static double [] pay;
	private static Scanner inputFile;
	
	public static StringBuffer createReport() {
		StringBuffer stringBuffer = new StringBuffer();

		stringBuffer.append(printData());
		stringBuffer.append(printAverages());
		stringBuffer.append(findUniqueNames(firstName, "First"));
		stringBuffer.append(findUniqueNames(lastName, "Last"));
		
		return stringBuffer;
	}
	
	public static String displayUniqueNames(HashMap<String, Integer> uniqueNames, int numUniqueNames) {
		String output = "";
		Set<String> allUniqueNames = uniqueNames.keySet();
		for(String name : allUniqueNames)
			if(uniqueNames.get(name) > 1)
				output += String.format("%s, # people with this name: %d\n", name, uniqueNames.get(name));
		
		return output;
	}
	
	public static double findAverageByEmployeeType(String payType) {
		double averagePay = 0;
		int numEmployeeType = 0;
		for(int i = 0; i < lastName.length; i++)
			if(employeeType[i].equals(payType)) {
				averagePay += pay[i];
				numEmployeeType++;
			}
		
		return averagePay/(double)numEmployeeType;
	}
	
	public static int findNumberOfNames(HashMap<String, Integer> uniqueNames, String [] nameArray) {
		int numUniqueNames = 0;
		for(int i = 0; i < nameArray.length; i++) {
			if(uniqueNames.containsKey(nameArray[i])) {
				uniqueNames.put(nameArray[i], uniqueNames.get(nameArray[i]) + 1);
				numUniqueNames++;
			} else
				uniqueNames.put(nameArray[i], 1);
		}
		
		return numUniqueNames;
	}
	
	public static String findUniqueNames(String [] nameArray, String nameType) {
		String output = "";
		HashMap<String, Integer> uniqueNames = new HashMap<String, Integer>();
		
		int numUniqueNames = findNumberOfNames(uniqueNames, nameArray);
		output += String.format("\n" + nameType + " names with more than one person sharing it:\n");
		if(numUniqueNames > 0) {
			output += displayUniqueNames(uniqueNames, numUniqueNames);
		} else
			output += String.format("All " + nameType + " names are unique\n");
		
		return output;
	}
	
	public static void initializeRecords(int numberOfRecords) {
		firstName = new String[numberOfRecords];
		lastName = new String[numberOfRecords];
		age = new int[numberOfRecords];
		employeeType = new String[numberOfRecords];
		pay = new double[numberOfRecords];
	}
	
	public static int insertRecord(String [] recordItems, int numberOfRecords) {
		int insertionIndex; 
		for(insertionIndex = 0; insertionIndex < lastName.length; insertionIndex++) {
			if(lastName[insertionIndex] == null)
				break;
			
			if(lastName[insertionIndex].compareTo(recordItems[1]) > 0) {
				moveRecords(insertionIndex, numberOfRecords);
				break;
			}
		}
		
		return insertionIndex;
	}
	
	public static void moveRecords(int j, int numberOfRecords) {
		for(int i = numberOfRecords; i > j; i--) {
			firstName[i] = firstName[i - 1];
			lastName[i] = lastName[i - 1];
			age[i] = age[i - 1];
			employeeType[i] = employeeType[i - 1];
			pay[i] = pay[i - 1];
		}
	}
	
	public static Scanner openFile(String fileName) {
		try {
			return new Scanner(new File(fileName));
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			return null;
		}
	}
	
	public static String printAverages() {
		String output = "";
		
		float sumOfAges = 0;
		
		for(int i = 0; i < lastName.length; i++)
			sumOfAges += age[i];
		
		output += String.format("\nAverage age:         %12.1f\n", sumOfAges/lastName.length);
		output += String.format("Average commission:  $%12.2f\n", findAverageByEmployeeType("Commission"));
		output += String.format("Average hourly wage: $%12.2f\n", findAverageByEmployeeType("Hourly"));
		output += String.format("Average salary:      $%12.2f\n", findAverageByEmployeeType("Salary"));
		
		return output;
	}
	
	public static String printData() {
		String output = "";
		output += String.format("# of people imported: %d\n", firstName.length);
		
		output += String.format("\n%-30s %s  %-12s %12s\n", "Person Name", "Age", "Emp. Type", "Pay");
		for(int i = 0; i < 30; i++)
			output += String.format("-");
			output += String.format(" ---  ");
		for(int i = 0; i < 12; i++)
			output += String.format("-");
		output += String.format(" ");
		for(int i = 0; i < 12; i++)
			output += String.format("-");
		output += String.format("\n");
		
		for(int i = 0; i < firstName.length; i++)
			output += String.format("%-30s %-3d  %-12s $%12.2f\n", firstName[i] + " " + lastName[i], age[i]
				, employeeType[i], pay[i]);
		
		return output;
	}
	
	public static String processFile(String fileName) {
		inputFile = openFile(fileName);
		int numberOfRecords = 0;
		while(inputFile.hasNextLine()) {
			String record = inputFile.nextLine();
			if(record.length() > 0)
				numberOfRecords++;
		}

		initializeRecords(numberOfRecords);
		inputFile.close();
		inputFile = openFile(fileName);
		numberOfRecords = validateRecords();
		
		if(numberOfRecords <= 0) {
			System.err.println("No records found in data file");
			throw (new IllegalStateException("No records found in data file"));
		}
		
		inputFile.close();
		
		return createReport().toString();
	}
	
	public static void processRecord(String record, int numberOfRecords) {
		String [] recordItems = record.split(",");

		int insertionIndex = insertRecord(recordItems, numberOfRecords);
		
		firstName[insertionIndex] = recordItems[0];
		lastName[insertionIndex] = recordItems[1];
		employeeType[insertionIndex] = recordItems[3];

		try {
			age[insertionIndex] = Integer.parseInt(recordItems[2]);
			pay[insertionIndex] = Double.parseDouble(recordItems[4]);
		} catch(NumberFormatException failureToParseData) {
			System.err.println("Incorrect age or payment format for entry:\n" + firstName[insertionIndex] + " " + lastName[insertionIndex]);
			System.err.println(failureToParseData.getMessage());
			throw(failureToParseData);
		}
	}
	
	public static int validateRecords() {
		int numberOfRecords = 0;
		
		while(inputFile.hasNextLine()) {
			String record = inputFile.nextLine();
			if(record.length() > 0) {
				try {
					processRecord(record, numberOfRecords);
				}
				catch (NumberFormatException failureToParseData){
					inputFile.close();
					throw(failureToParseData);
				}
				
				numberOfRecords++;
			}
		}
		
		return numberOfRecords;
	}
}
