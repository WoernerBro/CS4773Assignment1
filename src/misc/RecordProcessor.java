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
		
		//	Count number of records
		int numberOfRecords = 0;
		while(inputFile.hasNextLine()) {
			String record = inputFile.nextLine();
			if(record.length() > 0)
				numberOfRecords++;
		}

		//	Finish setting up number of records
		firstName = new String[numberOfRecords];
		lastName = new String[numberOfRecords];
		age = new int[numberOfRecords];
		employeeType = new String[numberOfRecords];
		pay = new double[numberOfRecords];

		//	Reset where inputFile is reading from to beginning of file
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
					
					if(lastName[j].compareTo(recordItems[1]) > 0) {
						for(int i = numberOfRecords; i > j; i--) {
							firstName[i] = firstName[i - 1];
							lastName[i] = lastName[i - 1];
							age[i] = age[i - 1];
							employeeType[i] = employeeType[i - 1];
							pay[i] = pay[i - 1];
						}
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
		
		int sumOfAges = 0;
		float averageAge = 0f;
		int numberOfCommissionedEmployees = 0;
		double sumOfCommissions = 0;
		double averageCommission = 0;
		int numberOfHourlyEmployees = 0;
		double sumOfHourlyWages = 0;
		double averageHourlyWage = 0;
		int numberOfSalariedEmployees = 0;
		double sumOfSalaries = 0;
		double averageSalary = 0;
		
		//	Aggregate sums of numbers
		for(int i = 0; i < firstName.length; i++) {
			sumOfAges += age[i];
			if(employeeType[i].equals("Commission")) {
				sumOfCommissions += pay[i];
				numberOfCommissionedEmployees++;
			} else if(employeeType[i].equals("Hourly")) {
				sumOfHourlyWages += pay[i];
				numberOfHourlyEmployees++;
			} else if(employeeType[i].equals("Salary")) {
				sumOfSalaries += pay[i];
				numberOfSalariedEmployees++;
			}
		}
		
		//	Find and print averages
		averageAge = (float) sumOfAges / firstName.length;
		stringBuffer.append(String.format("\nAverage age:         %12.1f\n", averageAge));
		averageCommission = sumOfCommissions / numberOfCommissionedEmployees;
		stringBuffer.append(String.format("Average commission:  $%12.2f\n", averageCommission));
		averageHourlyWage = sumOfHourlyWages / numberOfHourlyEmployees;
		stringBuffer.append(String.format("Average hourly wage: $%12.2f\n", averageHourlyWage));
		averageSalary = sumOfSalaries / numberOfSalariedEmployees;
		stringBuffer.append(String.format("Average salary:      $%12.2f\n", averageSalary));
		
		//	Finding and counting number of unique first names
		HashMap<String, Integer> uniqueFirstNames = new HashMap<String, Integer>();
		int numberOfUniqueFirstNames = 0;
		for(int i = 0; i < firstName.length; i++) {
			if(uniqueFirstNames.containsKey(firstName[i])) {
				uniqueFirstNames.put(firstName[i], uniqueFirstNames.get(firstName[i]) + 1);
				numberOfUniqueFirstNames++;
			} else {
				uniqueFirstNames.put(firstName[i], 1);
			}
		}

		//	Printing number of all shared first names
		stringBuffer.append(String.format("\nFirst names with more than one person sharing it:\n"));
		if(numberOfUniqueFirstNames > 0) {
			Set<String> allUniqueFirstNames = uniqueFirstNames.keySet();
			for(String firstName : allUniqueFirstNames) {
				if(uniqueFirstNames.get(firstName) > 1) {
					stringBuffer.append(String.format("%s, # people with this name: %d\n", firstName, uniqueFirstNames.get(firstName)));
				}
			}
		} else { 
			stringBuffer.append(String.format("All first names are unique"));
		}

		//	Finding and counting number of unique last names
		HashMap<String, Integer> uniqueLastNames = new HashMap<String, Integer>();
		int numberOfUniqueLastNames = 0;
		for(int i = 0; i < lastName.length; i++) {
			if(uniqueLastNames.containsKey(lastName[i])) {
				uniqueLastNames.put(lastName[i], uniqueLastNames.get(lastName[i]) + 1);
				numberOfUniqueLastNames++;
			} else {
				uniqueLastNames.put(lastName[i], 1);
			}
		}

		//	Printing number of all shared last names
		stringBuffer.append(String.format("\nLast names with more than one person sharing it:\n"));
		if(numberOfUniqueLastNames > 0) {
			Set<String> allUniqueLastNames = uniqueLastNames.keySet();
			for(String lastName : allUniqueLastNames) {
				if(uniqueLastNames.get(lastName) > 1) {
					stringBuffer.append(String.format("%s, # people with this name: %d\n", lastName, uniqueLastNames.get(lastName)));
				}
			}
		} else { 
			stringBuffer.append(String.format("All last names are unique"));
		}
		
		inputFile.close();
		
		return stringBuffer.toString();
	}
	
	public static void addRecord() {
		
	}
	
	public static Scanner openFile(String fileName) {
		try {
			return new Scanner(new File(fileName));
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			return null;
		}
	}
}
