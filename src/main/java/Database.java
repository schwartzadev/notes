//STEP 1. Import required packages
import java.sql.*;
import java.util.Random;

public class Database {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/NOTES";

    //  Database credentials
    static final String USER = "REDACTED";
    static final String PASS = "REDACTED";
    static final String[] colors = {"70d5d8", "8dffcd", "ebbab9", "eda6dd", "c09bd8", "9F97F4", "A4DEF9"};

    public static ResultSet getNotes() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
//        System.out.println("Connecting to database...");
        Connection conn = DriverManager.getConnection(DB_URL,USER,PASS);
//        System.out.println("Creating statement...");
        String sql = "SELECT * FROM Notes";
        ResultSet rs = conn.createStatement().executeQuery(sql);
//        rs.close();
//        conn.close();
        return rs;
    }

    public static String getRandom(String[] array) {
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }

    public static void setNoteColor(int id, String color) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL,USER,PASS);
            String sql = "UPDATE notes SET color='" + color + "' WHERE id = " + id + ";";
            conn.createStatement().executeUpdate(sql);
            conn.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void addNoteByQuery(String sql) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL,USER,PASS);
            conn.createStatement().executeUpdate(sql);
            conn.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String generateNoteHtml(String title, int id, String body, String color) {
        StringBuilder sb = new StringBuilder();
        if (color == null) {
            color = getRandom(colors); // sets color for this load
            setNoteColor(id, color); // stores color for all future loads
        }
        sb.append("<div class=\"note\" id=\"").append(id).append("\" style=\"background-color:").append(color).append("\";>");
        if (title != null) {
            sb.append("<h2 class=\"title\">").append(title).append("</h2>");
        }
        sb.append("<p class=\"content notecontent\">").append(body).append("</p>");
        sb.append("<div class=\"toolbar\">\n" +
                "<img class=\"trash\" src=\"./img/trash.svg\">\n" + // TODO make this a link to a method that removes note from database
                "</div");
        sb.append("</div>");
        sb.append("</div>");
        sb.append("</div>");
        return sb.toString();
    }

    public static int getMaxID() {
        int max = -1;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            String sql = "select max(id) from notes;";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                max = rs.getInt("max(id)");
            }
            rs.close();
            conn.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return max;
    }

    public static void main(String[] args) {
//        Connection conn = null;
//        Statement stmt = null;
//        try{
//            // STEP 2: Register JDBC driver
//            Class.forName("com.mysql.jdbc.Driver");
//
//            // STEP 3: Open a connection
//            System.out.println("Connecting to database...");
//            conn = DriverManager.getConnection(DB_URL,USER,PASS);
//
//            // STEP 4: Execute a query
//            System.out.println("Creating statement...");
//            stmt = conn.createStatement();
//            String sql;
//            sql = "SELECT title, body FROM Employees";
////            sql = "SELECT id, title, body, age FROM Employees";
//            ResultSet rs = stmt.executeQuery(sql);
//
//            // STEP 5: Extract data from result set
//            while(rs.next()){
//                // Retrieve by column name
////                int id  = rs.getInt("id");
////                int age = rs.getInt("age");
//                String first = rs.getString("title");
//                String last = rs.getString("body");
//
//                // Display values
////                System.out.print("ID: " + id);
////                System.out.print(", Age: " + age);
//                System.out.println(first +" "+ last);
//            }
//            // STEP 6: Clean-up environment
//            rs.close();
//            stmt.close();
//            conn.close();
//        } catch(Exception e){
//            //Handle errors for JDBC and Class.forName
//            e.printStackTrace();
//        } finally {
//            // finally block used to close resources
//            try {
//                if(stmt!=null) {
//                    stmt.close();
//                }
//            }catch(SQLException se2) {
//                // nothing we can do
//            }
//            try {
//                if (conn!=null) {
//                    conn.close();
//                }
//            } catch(SQLException se){
//                se.printStackTrace();
//            }
//        }
//        System.out.println("Done");
    }
}