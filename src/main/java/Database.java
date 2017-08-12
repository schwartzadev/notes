import com.sun.org.apache.xpath.internal.operations.Bool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Database {
    private final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private final String[] colors = {"70d5d8", "8dffcd", "ebbab9", "eda6dd", "c09bd8", "9f97f4", "a4def9"};
    private Connection conn;
//    private Parser parser = Parser.builder().build();
//    private HtmlRenderer renderer = HtmlRenderer.builder().build();

    public Database(Config config) {
        try {
            this.conn = DriverManager.getConnection(config.getDbUrl(),config.getSqlUsername(),config.getSqlPassword());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Note> getAllNotes() {
        try {
            return getNotes(conn.prepareStatement("SELECT * FROM Notes;" ));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Note> getArchivedNotes() {
        return getNotesByArchived(true);
    }
    public List<Note> getActiveNotes() {
        /**
         * Returns all notes that are not archived
         */
        return getNotesByArchived(false);
    }

    private List<Note> getNotesByArchived(Boolean bool) {
        /**
         * gets archived / non-archived (active) notes, depending on bool
         */
        try {
            PreparedStatement statement = conn.prepareStatement("select * from notes where archived = ? order by id desc;");
            statement.setBoolean(1, bool);
            return getNotes(statement);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Note> getNotes(PreparedStatement sql) {
        List<Note> notes = new ArrayList<Note>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            ResultSet rs = sql.executeQuery();
            while(rs.next()){
                String title = rs.getString("title");
                int id = Integer.parseInt(rs.getString("id"));
                String body = rs.getString("body");
                String color = rs.getString("color");
                String html = rs.getString("html");
                notes.add(new Note(title, body, id, color, html));
            }
            rs.close();
            // conn.close();
            return notes;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addNote(Note n) {
        PreparedStatement sql = null;
        try {
            sql = conn.prepareStatement(
                    "INSERT into notes VALUES ( ? , ? , ? , ? , ?, ? )" );
            sql.setInt(1, n.getId());
            sql.setString(2, n.getTitle());
            sql.setString(3, n.getBody());
            sql.setString(4, n.getColor());
            sql.setBoolean(5, n.getArchived());
            sql.setString(6, n.getHtml());
            Database.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteNote(int id) {
        PreparedStatement sql = null;
        try {
            sql = conn.prepareStatement(
                    "delete from notes WHERE id = ? ;" );
            sql.setInt(1, id);
            Database.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void archiveNote(int id) {
        PreparedStatement sql = null;
        try {
            sql = conn.prepareStatement(
                    "update notes set archived = 1 where id = ? ;" );
            sql.setInt(1, id);
            Database.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Note getNoteByID(int id) {
        PreparedStatement sql = null;
        try {
            sql = conn.prepareStatement(
                    "select * from notes where id=?;" );
            sql.setInt(1, id);
            ResultSet rs = sql.executeQuery();
            while(rs.next()){
                String title = rs.getString("title");
                int noteId = Integer.parseInt(rs.getString("id"));
                String body = rs.getString("body");
                String color = rs.getString("color");
                return new Note(title, body, id, color);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // if try fails
    }

    public String getRandom(String[] array) {
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }

    public void setNoteColor(Note n, String color) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            PreparedStatement sql =  conn.prepareStatement(
                    "UPDATE notes SET color = ? WHERE id = ? ;" );
            sql.setString(1, color);
            sql.setInt(2, n.getId());
            sql.executeUpdate();
            // conn.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void executeQuery(PreparedStatement sql) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            sql.executeUpdate();
//             conn.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String generateNoteHtml(Note note) {
//        Node body = parser.parse(note.getBody());
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
            sb.append("<div class=\"content larger\">").append(note.getHtml()).append("</div>");
        } else {
            sb.append("<div class=\"content hastitle\">").append(note.getHtml()).append("</div>");
        }
        sb.append("<div class=\"toolbar\">\n" + "<a href=\"/delete/").append(note.getId()).append("\">").append("<img class=\"icon\" src=\"./img/trash.svg\"></a>\n").append("</div");
        sb.append("<div class=\"toolbar\">\n" + "<a href=\"/edit/").append(note.getId()).append("\">").append("<img class=\"icon\" src=\"./img/pencil.svg\"></a>\n").append("</div");
        sb.append("</div>");
        sb.append("</div>");
        sb.append("</div>");
        return sb.toString();
    }

    public int getMaxID() {
        int max = -1;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            PreparedStatement sql = conn.prepareStatement("select max(id) from notes;");
            ResultSet rs = sql.executeQuery();
            while (rs.next()) {
                max = rs.getInt("max(id)");
            }
            rs.close();
            // conn.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return max;
    }
}