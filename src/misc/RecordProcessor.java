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
	private static Scanner inputFile;	/*	If you get weird memory stuff when testing,
										 *	check here, because this was not static before
										 *	and was declared in String Buffer. Hope it wasn't
										 *	for a good reason!
										 */
	
	public static String processFile(String fileName) {
		StringBuffer stringBuffer = new StringBuffer();
		
		inputFile = openFile(fileName);
		
		int numberOfRecords = 0;
		while(inputFile.hasNextLine()) {
			String record = inputFile.nextLine();
			if(record.length() > 0)
				numberOfRecords++;
		}

		firstName = new String[numberOfRecords];
		lastName = new String[numberOfRecords];
		age = new int[numberOfRecords];
		employeeType = new String[numberOfRecords];
		pay = new double[numberOfRecords];

		inputFile.close();
		inputFile = openFile(fileName);

		//	Actually adding to arrays
		numberOfRecords = 0;
		while(inputFile.hasNextLine()) {
			String record = inputFile.nextLine();
			if(record.length() > 0) {
				
				//	/SEEMS/ like a natural point to make a method
				String [] recordItems = record.split(",");

				int j = 0; 
				for(;j < lastName.length; j++) {
					if(lastName[j] == null)
						break;
					//	Alphabetizing records
					if(lastName[j].compareTo(recordItems[1]) > 0) {
						moveRecords(j, numberOfRecords);
						
						break;
					}
				}
				
				//	The next point that /SEEMS/ like a natural point to make a method
				firstName[j] = recordItems[0];
				lastName[j] = recordItems[1];
				employeeType[j] = recordItems[3];

				try {
					age[j] = Integer.parseInt(recordItems[2]);
					pay[j] = Double.parseDouble(recordItems[4]);
				} catch(Exception e) {
					System.err.println(e.getMessage());
					inputFile.close();
					return null;
				}
				
				numberOfRecords++;
			}
		}
		
		if(numberOfRecords == 0) {
			System.err.println("No records found in data file");
			inputFile.close();
			return null;
		}
		
		//print the rows
		stringBuffer.append(String.format("# of people imported: %d\n", firstName.length));
		
		stringBuffer.append(String.format("\n%-30s %s  %-12s %12s\n", "Person Name", "Age", "Emp. Type", "Pay"));
		for(int i = 0; i < 30; i++)
			stringBuffer.append(String.format("-"));
		stringBuffer.append(String.format(" ---  "));
		for(int i = 0; i < 12; i++)
			stringBuffer.append(String.format("-"));
		stringBuffer.append(String.format(" "));
		for(int i = 0; i < 12; i++)
			stringBuffer.append(String.format("-"));
		stringBuffer.append(String.format("\n"));
		
		for(int i = 0; i < firstName.length; i++) {
			stringBuffer.append(String.format("%-30s %-3d  %-12s $%12.2f\n", firstName[i] + " " + lastName[i], age[i]
				, employeeType[i], pay[i]));
		}
		
		stringBuffer.append(printAverages());
		
		stringBuffer.append(findUniqueNames(firstName, "First"));
		stringBuffer.append(findUniqueNames(lastName, "Last"));
		
		inputFile.close();
		
		return stringBuffer.toString();
	}
	
	public static Scanner openFile(String fileName) {
		try {
			return new Scanner(new File(fileName));
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			return null;
		}
	}
	
	public static void moveRecords (int j, int numberOfRecords) {
		for(int i = numberOfRecords; i > j; i--) {
			firstName[i] = firstName[i - 1];
			lastName[i] = lastName[i - 1];
			age[i] = age[i - 1];
			employeeType[i] = employeeType[i - 1];
			pay[i] = pay[i - 1];
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
	
	public static String findUniqueNames(String [] nameArray, String nameType) {
		String output = "";
		HashMap<String, Integer> uniqueNames = new HashMap<String, Integer>();
		
		int numUniqueNames = findNumberOfNames(uniqueNames, nameArray);
		output += String.format("\n" + nameType + " names with more than one person sharing it:\n");
		if(numUniqueNames > 0) {
			output += displayUniqueNames(uniqueNames, numUniqueNames);
		} else { 
			output += String.format("All " + nameType + " names are unique\n");
		}
		return output;
	}
	
	public static String displayUniqueNames(HashMap<String, Integer> uniqueNames, int numUniqueNames) {
		String output = "";
		Set<String> allUniqueNames = uniqueNames.keySet();
		for(String name : allUniqueNames) {
			if(uniqueNames.get(name) > 1) {
				output += String.format("%s, # people with this name: %d\n", name, uniqueNames.get(name));
			}
		}
		
		return output;
	}
	
	public static int findNumberOfNames(HashMap<String, Integer> uniqueNames, String [] nameArray) {
		int numUniqueNames = 0;
		for(int i = 0; i < nameArray.length; i++) {
			if(uniqueNames.containsKey(nameArray[i])) {
				uniqueNames.put(nameArray[i], uniqueNames.get(nameArray[i]) + 1);
				numUniqueNames++;
			} else {
				uniqueNames.put(nameArray[i], 1);
			}
		}
		
		return numUniqueNames;
	}
}
