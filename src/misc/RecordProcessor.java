package misc;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

public class RecordProcessor {
	private static String [] fn;
	private static String [] ln;
	private static int [] a;
	private static String [] tp;
	private static double [] py;
	
	public static String processFile(String f) {
		StringBuffer st = new StringBuffer();
		
		Scanner s = null;
		try {
			s = new Scanner(new File(f));
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			return null;
		}
		
		int c = 0;
		while(s.hasNextLine()) {
			String l = s.nextLine();
			if(l.length() > 0)
				c++;
		}

		fn = new String[c];
		ln = new String[c];
		a = new int[c];
		tp = new String[c];
		py = new double[c];

		s.close();
		try {
			s = new Scanner(new File(f));
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			return null;
		}

		c = 0;
		while(s.hasNextLine()) {
			String l = s.nextLine();
			if(l.length() > 0) {
				
				String [] words = l.split(",");

				int c2 = 0; 
				for(;c2 < ln.length; c2++) {
					if(ln[c2] == null)
						break;
					
					if(ln[c2].compareTo(words[1]) > 0) {
						for(int i = c; i > c2; i--) {
							fn[i] = fn[i - 1];
							ln[i] = ln[i - 1];
							a[i] = a[i - 1];
							tp[i] = tp[i - 1];
							py[i] = py[i - 1];
						}
						break;
					}
				}
				
				fn[c2] = words[0];
				ln[c2] = words[1];
				tp[c2] = words[3];

				try {
					a[c2] = Integer.parseInt(words[2]);
					py[c2] = Double.parseDouble(words[4]);
				} catch(Exception e) {
					System.err.println(e.getMessage());
					s.close();
					return null;
				}
				
				c++;
			}
		}
		
		if(c == 0) {
			System.err.println("No records found in data file");
			s.close();
			return null;
		}
		
		//print the rows
		st.append(String.format("# of people imported: %d\n", fn.length));
		
		st.append(String.format("\n%-30s %s  %-12s %12s\n", "Person Name", "Age", "Emp. Type", "Pay"));
		for(int i = 0; i < 30; i++)
			st.append(String.format("-"));
		st.append(String.format(" ---  "));
		for(int i = 0; i < 12; i++)
			st.append(String.format("-"));
		st.append(String.format(" "));
		for(int i = 0; i < 12; i++)
			st.append(String.format("-"));
		st.append(String.format("\n"));
		
		for(int i = 0; i < fn.length; i++) {
			st.append(String.format("%-30s %-3d  %-12s $%12.2f\n", fn[i] + " " + ln[i], a[i]
				, tp[i], py[i]));
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
		for(int i = 0; i < fn.length; i++) {
			sum1 += a[i];
			if(tp[i].equals("Commission")) {
				sum2 += py[i];
				c2++;
			} else if(tp[i].equals("Hourly")) {
				sum3 += py[i];
				c3++;
			} else if(tp[i].equals("Salary")) {
				sum4 += py[i];
				c4++;
			}
		}
		avg1 = (float) sum1 / fn.length;
		st.append(String.format("\nAverage age:         %12.1f\n", avg1));
		avg2 = sum2 / c2;
		st.append(String.format("Average commission:  $%12.2f\n", avg2));
		avg3 = sum3 / c3;
		st.append(String.format("Average hourly wage: $%12.2f\n", avg3));
		avg4 = sum4 / c4;
		st.append(String.format("Average salary:      $%12.2f\n", avg4));
		
		HashMap<String, Integer> hm = new HashMap<String, Integer>();
		int c1 = 0;
		for(int i = 0; i < fn.length; i++) {
			if(hm.containsKey(fn[i])) {
				hm.put(fn[i], hm.get(fn[i]) + 1);
				c1++;
			} else {
				hm.put(fn[i], 1);
			}
		}

		st.append(String.format("\nFirst names with more than one person sharing it:\n"));
		if(c1 > 0) {
			Set<String> set = hm.keySet();
			for(String str : set) {
				if(hm.get(str) > 1) {
					st.append(String.format("%s, # people with this name: %d\n", str, hm.get(str)));
				}
			}
		} else { 
			st.append(String.format("All first names are unique"));
		}

		HashMap<String, Integer> hm2 = new HashMap<String, Integer>();
		int c21 = 0;
		for(int i = 0; i < ln.length; i++) {
			if(hm2.containsKey(ln[i])) {
				hm2.put(ln[i], hm2.get(ln[i]) + 1);
				c21++;
			} else {
				hm2.put(ln[i], 1);
			}
		}

		st.append(String.format("\nLast names with more than one person sharing it:\n"));
		if(c21 > 0) {
			Set<String> set = hm2.keySet();
			for(String str : set) {
				if(hm2.get(str) > 1) {
					st.append(String.format("%s, # people with this name: %d\n", str, hm2.get(str)));
				}
			}
		} else { 
			st.append(String.format("All last names are unique"));
		}
		
		//close the file
		s.close();
		
		return st.toString();
	}
	
}
