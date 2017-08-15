import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private Connection conn;

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
                    "INSERT into notes VALUES ( ? , ? , ? , ? , ?, ?, 100 )" ); // TODO remove hardcoded user param
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

    public void restoreNote(int id) {
        PreparedStatement sql = null;
        try {
            sql = conn.prepareStatement(
                    "update notes set archived = 0 where id = ? ;" );
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

    public int getMaxID(String dbname) {
        int max = -1;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            PreparedStatement sql = conn.prepareStatement("select max(id) from " + dbname + ";");
            ResultSet rs = sql.executeQuery();
            while (rs.next()) {
                max = rs.getInt("max(id)");
            }
            rs.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return max;
    }

    public void addUser(User u) {
        PreparedStatement sql = null;
        u.hashPassword();
        try {
            sql = conn.prepareStatement(
                    "INSERT into users VALUES ( ? , ? , ? , ? )" );
            sql.setInt(1, u.getId());
            sql.setString(2, u.getUsername());
            sql.setString(3, u.getHashedPass());
            sql.setBoolean(4, u.isactive());
            Database.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getPasswordHashes() {
        List<String> hashes = new ArrayList<>();
        PreparedStatement sql = null;
        try {
            sql = conn.prepareStatement("select password from users;");
            ResultSet rs = sql.executeQuery();
            while (rs.next()) {
                hashes.add(rs.getString("password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hashes;
    }

    public User lookupUserByUsername(String username) {
        PreparedStatement sql = null;
        try {
            sql = conn.prepareStatement("select * from users where username = ?;");
            sql.setString(1, username);
            ResultSet rs = sql.executeQuery();
            while (rs.next()) {
                return new User(rs.getInt("id"), rs.getBoolean("isactive"), rs.getString("username"), rs.getString("password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}