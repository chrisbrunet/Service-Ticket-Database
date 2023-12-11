import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import java.util.ArrayList;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

/**
 * @author chrisbrunet
 * The RandomIncidentGenerator class prompts user login input to connect to the server, then generates random
 * service tickets based on user inputed date range and number of incidents
 */
public class RandomIncidentGenerator {
	
	/**
	 * Executes an SQL query to get the last ID value in a given table
	 * @param table: name of table to query
	 * @param column: name of column to sort by (usually "ID")
	 * @param con: connection to MySQL database
	 * @return id: last ID in table
	 * @throws SQLException
	 */
	public static int getLastID(String table, String column, Connection con) throws SQLException {
		try { 
			Statement statement = con.createStatement();
			int id = 0;
			ResultSet res = statement.executeQuery("SELECT ID FROM " + table + " ORDER BY " + column + " DESC LIMIT 1;"); // execute query
			while(res.next()) {
				id = res.getInt(1); // loop to find last ID
			}
			return id;
		
		} catch (SQLException e) {
			System.out.println(e);
			return 0;
		}
	}
	
	
	/**
	 * Creates an ArrayList with all values from a column of a given table
	 * @param table: name of the table to query
	 * @param column: column to fetch data from
	 * @param con: connection to MySQL database
	 * @return result: list of values in given column
	 * @throws SQLException
	 */
	public static ArrayList<String> getTableColumnData(String table, String column, Connection con) throws SQLException {
		try { 
			Statement statement = con.createStatement();
			ArrayList<String> result = new ArrayList<String>();
			ResultSet res = statement.executeQuery("SELECT " + column + " FROM " + table); // execute SQL query
			while(res.next()) {
				result.add(res.getString(1)); // add column values to result list
			}
			
			return result;

		} catch (SQLException e) {
			System.out.println(e);
			return null;
		}
	}
	
	/**
	 * Generates a line of the SQL statement to add values into the EventLog table
	 * @param caseID
	 * @param activity
	 * @param urgency
	 * @param impact
	 * @param priority
	 * @param startDate
	 * @param endDate
	 * @param status
	 * @param updateDT
	 * @param duration
	 * @param origin
	 * @param eventClass
	 * @return SQL statement
	 */
	public static String GenerateEventLine(String caseID, 
			String activity, int urgency, int impact, int priority,
			LocalDate startDate, LocalDate endDate, String status,
			LocalDateTime updateDT, int duration, String origin, String eventClass) {
		
		// generate string
		String line = "('" 
				+ caseID + "', '" 
				+ activity + "', "
				+ urgency + ", "
				+ impact + ", "
				+ priority + ", '"
				+ startDate + "', '"
				+ endDate + "', '"
				+ status + "', '"
				+ updateDT + "', "
				+ duration + ", '"
				+ origin + "', '"
				+ eventClass + "')";
		
		return line;
	}
	
	/**
	 * Generates random values to be added to the EventLog table. Creates SQL statement and executes statement.
	 * @param n: number of rows to be added to table
	 * @param startDateRange: beginning of date range to add rows
	 * @param endDateRange: end of date range to add tickets
	 * @param con: connection to MySQL database
	 * @return Complete SQL statement
	 * @throws Exception
	 */
	public static void insertRandomTickets(int n, LocalDate startDateRange, LocalDate endDateRange, Connection con) throws Exception {
		// create lists from other tables in database
		ArrayList<String> activityNames = getTableColumnData("EventActivity", "ActivityName", con);
		ArrayList<String> originNames = getTableColumnData("EventOrigin", "OriginName", con);
		ArrayList<String> statusNames = getTableColumnData("EventStatus", "Status", con);
		ArrayList<String> classNames = getTableColumnData("EventClass", "Class", con);
		int currentID = getLastID("EventLog", "ID", con);

		
		for(int i = 0; i < n; i++) { // looping through n lines and concatenating each line to SQL statement
			// begin SQL statement
			String insertRandomEventsSQL = "INSERT INTO EventLog (CaseID, Activity, Urgency, Impact, Priority, StartDate, EndDate, TicketStatus, UpdateDateTime, Duration, Origin, Class) VALUES ";
			
			Random random = new Random();
			
			// setting new caseID
			String caseID = "CS_" + (i+currentID+1);
			
			// setting random start date, end date, duration, and updateDT
			long daysBetween = ChronoUnit.DAYS.between(startDateRange, endDateRange);
	        int range = Math.toIntExact(daysBetween);
			LocalDate startDate = startDateRange.plusDays(random.nextInt(range)-1);
			LocalDateTime updateDT = startDate.atTime(random.nextInt(24), random.nextInt(60));
			long durationDaysBetween = ChronoUnit.DAYS.between(startDate, endDateRange);
			int durationRange = Math.toIntExact(durationDaysBetween);
	        int duration = random.nextInt(durationRange);
			LocalDate endDate = startDate.plusDays(duration);
								
			// setting random urgency, impact and priority
			int urgency = random.nextInt(3) + 1;
			int impact = random.nextInt(3) + 1;
			int priority = urgency + impact - 1;
			
			// setting activity, status, origin, className based on lists
			String activity = activityNames.get(random.nextInt(activityNames.size()));
			String status = statusNames.get(random.nextInt(statusNames.size()));
			String origin = originNames.get(random.nextInt(originNames.size()));
			String className = classNames.get(random.nextInt(classNames.size()));
			
			// generating and concatenating line to SQL statement
			String newLine = GenerateEventLine(caseID, activity, urgency, impact, priority, startDate, endDate,
					status, updateDT, duration, origin, className);
			
			insertRandomEventsSQL += newLine + ";";
			
			// executing statement
			Statement statement = con.createStatement();
			statement.executeUpdate(insertRandomEventsSQL);
		}
		
	}

	public static void main(String[] args) {
		
		// ensuring driver is installed properly
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e){
			e.printStackTrace();
		}
			
		try { // test JDBC connection
			String url = "jdbc:mysql://34.31.30.192:3306/SERVICE_TICKETS";
			System.out.println("JDBC URL is: " + url);

			// prompt for user inputs
			Scanner input = new Scanner(System.in);
						
			System.out.print("\nEnter Username: ");
			String name = input.nextLine();
			
			System.out.print("\nEnter Password: ");
			String pw = input.nextLine();
			
			System.out.print("Establishing connection to database... ");
			Connection con = DriverManager.getConnection(url, name, pw);
			System.out.println("connection established");
			
			System.out.println("\nPlease enter the following information for the START of the date range:");
			System.out.print("Year (YYYY): ");
			int startYear = input.nextInt();
			System.out.print("Month (MM): ");
			int startMonth = input.nextInt();
			System.out.print("Day (DD): ");
			int startDay = input.nextInt();
			
			System.out.println("\nPlease enter the following information for the END of the date range:");
			System.out.print("Year (YYYY): ");
			int endYear = input.nextInt();
			System.out.print("Month (MM): ");
			int endMonth = input.nextInt();
			System.out.print("Day (DD): ");
			int endDay = input.nextInt();
			
			System.out.println("\nPlease enter the number of entries to randomly generate for this period:");
			System.out.print("Number of Entries: ");
			int numEntries = input.nextInt();
			
			LocalDate startDateRange = LocalDate.of(startYear, startMonth, startDay);
			LocalDate endDateRange = LocalDate.of(endYear, endMonth, endDay);
			
			// creating and executing SQL statements
			System.out.print("\nExecuting SQL statements...");
			insertRandomTickets(numEntries, startDateRange, endDateRange, con);
			System.out.println("Finished!");
		
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			

				
	}

}
