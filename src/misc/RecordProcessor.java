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
		
		Scanner inputFile = openFile(fileName);
		int numberOfRecords = countNumberOfRecords(inputFile);
		initializeValues(numberOfRecords);
		inputFile.close();
		inputFile = openFile(fileName);
		String test;
		try {
           test = readNames(inputFile);
		}  catch(Exception e) {
			inputFile.close();
			return null;
		}
		if(test == null) {
			return null;
		}

		StringBuffer stringBuffer = buildString();
		inputFile.close();
		
		return stringBuffer.toString();
	}

	public static Scanner openFile(String fileName) {
		Scanner inputFile = null;
		try {
			inputFile = new Scanner(new File(fileName));
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			return null;
		}
		
		return inputFile;
		
	}
	
	public static int countNumberOfRecords (Scanner inputFile) {
		int numberOfRecords = 0;
		while(inputFile.hasNextLine()) {
			String record = inputFile.nextLine();
			if(record.length() > 0)
				numberOfRecords++;
		}
		
		return numberOfRecords;
	}
	
	public static void initializeValues (int numberOfRecords) {
		firstName = new String[numberOfRecords];
		lastName = new String[numberOfRecords];
		age = new int[numberOfRecords];
		employeeType = new String[numberOfRecords];
		pay = new double[numberOfRecords];
	}
	
	public static String readNames( Scanner inputFile) {
		int numberOfRecords = 0;
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
					throw e;
				}
				
				numberOfRecords++;
			}
		}
		
		if(numberOfRecords == 0) {
			System.err.println("No records found in data file");
			inputFile.close();
			return null;
		}
		
		return "sucess";
	}
	
	public static StringBuffer printData( StringBuffer stringBuffer) {

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
		
		return stringBuffer;
	}
	
	public static StringBuffer calculateAverages(StringBuffer stringBuffer) {
		int sumOfAges, numberOfHourlyEmployees, numberOfCommissionedEmployees, numberOfSalariedEmployees;
		sumOfAges = numberOfHourlyEmployees = numberOfCommissionedEmployees = numberOfSalariedEmployees = 0;
		float averageAge = 0f;
		double sumOfCommissions, averageCommission, sumOfHourlyWages, averageHourlyWage,
		sumOfSalaries, averageSalary;
		sumOfCommissions = averageCommission = sumOfHourlyWages = averageHourlyWage =
		sumOfSalaries = averageSalary = 0;
		
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
		
		return stringBuffer;
	}
	
	public static StringBuffer checkForUniqueFirstNames(StringBuffer stringBuffer) {
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
		
		return stringBuffer;
	}
	
	public static StringBuffer checkForUniqueLastNames(StringBuffer stringBuffer) {
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
		
		return stringBuffer;
	}
	
	public static StringBuffer buildString () {
		
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer = printData(stringBuffer);
		stringBuffer = calculateAverages(stringBuffer);
		stringBuffer = checkForUniqueFirstNames(stringBuffer);
		stringBuffer = checkForUniqueLastNames(stringBuffer);
		
		return stringBuffer;
	}
}
