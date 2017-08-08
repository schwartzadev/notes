//STEP 1. Import required packages
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Database {
    // JDBC driver name and database URL
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/NOTES?useSSL=false";

    //  Database credentials
    static final String USER = Config.MySQLUser;
    static final String PASS = Config.MySQLPass;
    private static final String[] colors = {"70d5d8", "8dffcd", "ebbab9", "eda6dd", "c09bd8", "9f97f4", "a4def9"};

    public static List<Note> getAllNotes() throws Exception {
        Connection conn = DriverManager.getConnection(DB_URL,USER,PASS);
        return getNotes(conn.prepareStatement("SELECT * FROM Notes;" ));
    }

    public static List<Note> getActiveNotes() throws Exception {
        /**
         * Returns all notes that are not deleted
         */
        Connection conn = DriverManager.getConnection(DB_URL,USER,PASS);
        return getNotes(conn.prepareStatement("select * from notes where archived = 0 order by id desc;" ));
    }


    public static List<Note> getNotes(PreparedStatement sql) throws Exception {
        List<Note> notes = new ArrayList<Note>();
        Class.forName("com.mysql.jdbc.Driver");
//        System.out.println("Connecting to database...");
        Connection conn = DriverManager.getConnection(DB_URL,USER,PASS);
//        System.out.println("Creating statement...");
        ResultSet rs = sql.executeQuery();
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
            PreparedStatement sql =  conn.prepareStatement(
                    "UPDATE notes SET color = ? WHERE id = ? ;" );
            sql.setString(1, color);
            sql.setInt(2, n.getId());
            sql.executeUpdate();
            conn.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void executeQuery(Connection conn, PreparedStatement sql) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            sql.executeUpdate();
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
        if (note.getTitle() == null) {
            sb.append("<p class=\"content larger\">").append(note.getBody()).append("</p>");
        } else {
            sb.append("<p class=\"content\">").append(note.getBody()).append("</p>");
        }
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
            PreparedStatement sql = conn.prepareStatement("select max(id) from notes;");
            ResultSet rs = sql.executeQuery();
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
}