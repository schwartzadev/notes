//STEP 1. Import required packages
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Database {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/NOTES?useSSL=false";

    //  Database credentials
    static final String USER = "REDACTED";
    static final String PASS = "REDACTED";
    static final String[] colors = {"70d5d8", "8dffcd", "ebbab9", "eda6dd", "c09bd8", "9F97F4", "A4DEF9"};

    public static List<Note> getNotes() throws Exception {
        List<Note> notes = new ArrayList<Note>();
        Class.forName("com.mysql.jdbc.Driver");
//        System.out.println("Connecting to database...");
        Connection conn = DriverManager.getConnection(DB_URL,USER,PASS);
//        System.out.println("Creating statement...");
        String sql = "SELECT * FROM Notes";
        ResultSet rs = conn.createStatement().executeQuery(sql);
        while(rs.next()){
            String title = rs.getString("title");
            int id = Integer.parseInt(rs.getString("id"));
            String body = rs.getString("body");
            String color = rs.getString("color");
            notes.add(new Note(title, body, id, color));
        }
        rs.close();
        conn.close();
        return notes;
    }

    public static String getRandom(String[] array) {
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }

    public static void setNoteColor(Note n, String color) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL,USER,PASS);
            String sql = "UPDATE notes SET color='" + color + "' WHERE id = " + n.getId() + ";";
            conn.createStatement().executeUpdate(sql);
            conn.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void executeQuery(String sql) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL,USER,PASS);
            conn.createStatement().executeUpdate(sql);
            conn.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String generateNoteHtml(Note note) {
        StringBuilder sb = new StringBuilder();
        if (note.getColor() == null) {
            note.setColor(getRandom(colors)); // sets color for this load
            setNoteColor(note, note.getColor()); // stores color for all future loads
        }
        sb.append("<div class=\"note\" id=\"").append(note.getId()).append("\" style=\"background-color:").append(note.getColor()).append("\";>");
        if (note.getTitle() != null) {
            sb.append("<h2 class=\"title\">").append(note.getTitle()).append("</h2>");
        }
        sb.append("<p class=\"content notecontent\">").append(note.getBody()).append("</p>");
        sb.append("<div class=\"toolbar\">\n" + "<a href=\"/delete/").append(note.getId()).append("\">").append("<img class=\"trash\" src=\"./img/trash.svg\"></a>\n").append("</div");
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

    public static void main(String[] args) { }
}