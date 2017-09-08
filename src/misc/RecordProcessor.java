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
		
		int numLines = 0;
		while(inputFile.hasNextLine()) {
			String line = inputFile.nextLine();
			if(line.length() > 0)
				numLines++;
		}

		firstName = new String[numLines];
		lastName = new String[numLines];
		age = new int[numLines];
		employeeType = new String[numLines];
		pay = new double[numLines];

		inputFile.close();
		try {
			inputFile = new Scanner(new File(fileName));
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			return null;
		}

		numLines = 0;
		while(inputFile.hasNextLine()) {
			String line = inputFile.nextLine();
			if(line.length() > 0) {
				
				String [] tokens = line.split(",");

				int c2 = 0; 
				for(;c2 < lastName.length; c2++) {
					if(lastName[c2] == null)
						break;
					
					if(lastName[c2].compareTo(tokens[1]) > 0) {
						for(int i = numLines; i > c2; i--) {
							firstName[i] = firstName[i - 1];
							lastName[i] = lastName[i - 1];
							age[i] = age[i - 1];
							employeeType[i] = employeeType[i - 1];
							pay[i] = pay[i - 1];
						}
						break;
					}
				}
				
				firstName[c2] = tokens[0];
				lastName[c2] = tokens[1];
				employeeType[c2] = tokens[3];

				try {
					age[c2] = Integer.parseInt(tokens[2]);
					pay[c2] = Double.parseDouble(tokens[4]);
				} catch(Exception e) {
					System.err.println(e.getMessage());
					inputFile.close();
					return null;
				}
				
				numLines++;
			}
		}
		
		if(numLines == 0) {
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
		
		int sum1 = 0;
		float avg1 = 0f;
		int c2 = 0;
		double sum2 = 0;
		double avg2 = 0;
		int c3 = 0;
		double sum3 = 0;
		double avg3 = 0;
		int c4 = 0;
		double sum4 = 0;
		double avg4 = 0;
		for(int i = 0; i < firstName.length; i++) {
			sum1 += age[i];
			if(employeeType[i].equals("Commission")) {
				sum2 += pay[i];
				c2++;
			} else if(employeeType[i].equals("Hourly")) {
				sum3 += pay[i];
				c3++;
			} else if(employeeType[i].equals("Salary")) {
				sum4 += pay[i];
				c4++;
			}
		}
		avg1 = (float) sum1 / firstName.length;
		stringBuffer.append(String.format("\nAverage age:         %12.1f\n", avg1));
		avg2 = sum2 / c2;
		stringBuffer.append(String.format("Average commission:  $%12.2f\n", avg2));
		avg3 = sum3 / c3;
		stringBuffer.append(String.format("Average hourly wage: $%12.2f\n", avg3));
		avg4 = sum4 / c4;
		stringBuffer.append(String.format("Average salary:      $%12.2f\n", avg4));
		
		HashMap<String, Integer> hm = new HashMap<String, Integer>();
		int c1 = 0;
		for(int i = 0; i < firstName.length; i++) {
			if(hm.containsKey(firstName[i])) {
				hm.put(firstName[i], hm.get(firstName[i]) + 1);
				c1++;
			} else {
				hm.put(firstName[i], 1);
			}
		}

		stringBuffer.append(String.format("\nFirst names with more than one person sharing it:\n"));
		if(c1 > 0) {
			Set<String> set = hm.keySet();
			for(String str : set) {
				if(hm.get(str) > 1) {
					stringBuffer.append(String.format("%s, # people with this name: %d\n", str, hm.get(str)));
				}
			}
		} else { 
			stringBuffer.append(String.format("All first names are unique"));
		}

		HashMap<String, Integer> hm2 = new HashMap<String, Integer>();
		int c21 = 0;
		for(int i = 0; i < lastName.length; i++) {
			if(hm2.containsKey(lastName[i])) {
				hm2.put(lastName[i], hm2.get(lastName[i]) + 1);
				c21++;
			} else {
				hm2.put(lastName[i], 1);
			}
		}

		stringBuffer.append(String.format("\nLast names with more than one person sharing it:\n"));
		if(c21 > 0) {
			Set<String> set = hm2.keySet();
			for(String str : set) {
				if(hm2.get(str) > 1) {
					stringBuffer.append(String.format("%s, # people with this name: %d\n", str, hm2.get(str)));
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
