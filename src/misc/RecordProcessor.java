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
	
	public static String processFile(String fileName) {
		StringBuffer stringBuffer = new StringBuffer();
		
		Scanner inputFile = null;
		try {
			inputFile = new Scanner(new File(fileName));
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			return null;
		}
		
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
		try {
			inputFile = new Scanner(new File(fileName));
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			return null;
		}

		numberOfRecords = 0;
		while(inputFile.hasNextLine()) {
			String record = inputFile.nextLine();
			if(record.length() > 0) {
				
				String [] recordItems = record.split(",");

				int c2 = 0; 
				for(;c2 < lastName.length; c2++) {
					if(lastName[c2] == null)
						break;
					
					if(lastName[c2].compareTo(recordItems[1]) > 0) {
						for(int i = numberOfRecords; i > c2; i--) {
							firstName[i] = firstName[i - 1];
							lastName[i] = lastName[i - 1];
							age[i] = age[i - 1];
							employeeType[i] = employeeType[i - 1];
							pay[i] = pay[i - 1];
						}
						break;
					}
				}
				
				firstName[c2] = recordItems[0];
				lastName[c2] = recordItems[1];
				employeeType[c2] = recordItems[3];

				try {
					age[c2] = Integer.parseInt(recordItems[2]);
					pay[c2] = Double.parseDouble(recordItems[4]);
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
		averageAge = (float) sumOfAges / firstName.length;
		stringBuffer.append(String.format("\nAverage age:         %12.1f\n", averageAge));
		averageCommission = sumOfCommissions / numberOfCommissionedEmployees;
		stringBuffer.append(String.format("Average commission:  $%12.2f\n", averageCommission));
		averageHourlyWage = sumOfHourlyWages / numberOfHourlyEmployees;
		stringBuffer.append(String.format("Average hourly wage: $%12.2f\n", averageHourlyWage));
		averageSalary = sumOfSalaries / numberOfSalariedEmployees;
		stringBuffer.append(String.format("Average salary:      $%12.2f\n", averageSalary));
		
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

		stringBuffer.append(String.format("\nFirst names with more than one person sharing it:\n"));
		if(numberOfUniqueFirstNames > 0) {
			Set<String> setOfUniqueFirstNames = uniqueFirstNames.keySet();
			for(String firstName : setOfUniqueFirstNames) {
				if(uniqueFirstNames.get(firstName) > 1) {
					stringBuffer.append(String.format("%s, # people with this name: %d\n", firstName, uniqueFirstNames.get(firstName)));
				}
			}
		} else { 
			stringBuffer.append(String.format("All first names are unique"));
		}

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

		stringBuffer.append(String.format("\nLast names with more than one person sharing it:\n"));
		if(numberOfUniqueLastNames > 0) {
			Set<String> setOfUniqueLastNames = uniqueLastNames.keySet();
			for(String lastName : setOfUniqueLastNames) {
				if(uniqueLastNames.get(lastName) > 1) {
					stringBuffer.append(String.format("%s, # people with this name: %d\n", lastName, uniqueLastNames.get(lastName)));
				}
			}
		} else { 
			stringBuffer.append(String.format("All last names are unique"));
		}
		
		//close the file
		inputFile.close();
		
		return stringBuffer.toString();
	}
	
}
