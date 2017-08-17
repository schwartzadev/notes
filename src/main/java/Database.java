import org.apache.commons.lang.RandomStringUtils;

import javax.xml.crypto.Data;
import java.sql.*;
import java.sql.Date;
import java.util.*;

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

    public List<Note> getAllNotes(int userid) {
        try {
            return getNotes(conn.prepareStatement("SELECT * FROM Notes;" ), userid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Note> getArchivedNotes(int userid) {
        return getNotesByArchived(true, userid);
    }

    public List<Note> getActiveNotes(int userid) {
        /**
         * Returns all notes that are not archived
         */
        return getNotesByArchived(false, userid);
    }

    private List<Note> getNotesByArchived(Boolean bool, int userid) {
        /**
         * gets archived / non-archived (active) notes, depending on bool
         */
        try {
            PreparedStatement statement = conn.prepareStatement("select * from notes where archived = ? and user_id = ? order by id desc;");
            statement.setBoolean(1, bool);
            statement.setInt(2, userid);
            return getNotes(statement, userid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<Note> getNotes(PreparedStatement sql, int userid) {
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
                notes.add(new Note(title, body, id, color, html, userid));
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
                    "INSERT into notes VALUES ( ? , ? , ? , ? , ?, ?, ? )" );
            sql.setInt(1, n.getId());
            sql.setString(2, n.getTitle());
            sql.setString(3, n.getBody());
            sql.setString(4, n.getColor());
            sql.setBoolean(5, n.getArchived());
            sql.setString(6, n.getHtml());
            sql.setInt(7, n.getUserId());
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
                int userid = rs.getInt("user_id");
                return new Note(title, body, id, color, userid);
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

    public String saveLogin(User u) {
        PreparedStatement sql = null;
        int id = getMaxID("logins")+1;
        String random = RandomStringUtils.random(20, true, true);
        String hashedusername = u.hashUsername();
        try {
            sql = conn.prepareStatement("INSERT into logins VALUES ( ? , ? , ? , ?, ? )" );
            sql.setInt(1, id); // id
            sql.setInt(2, u.getId()); // user_id
            sql.setString(3, random); // random string
            sql.setString(4, hashedusername); // hashed username
            sql.setDate(5, new java.sql.Date(Calendar.getInstance().getTimeInMillis())); // date created TODO make this get date time, not just date
            Database.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return random+hashedusername;
    }

    public List<Login> getAllLogins() {
        List<Login> logins = new ArrayList<>();
        try {
            PreparedStatement statement = conn.prepareStatement("select * from logins;");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                logins.add(new Login(rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("random"),
                        rs.getString("name_hash"),
                        rs.getDate("date_created")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return logins;
    }

    public int checkCookie(String cookie) {
        /**
         * returns the id of the signed in user
         * returns -1 if no user is signed in
         */
        for (Login l : getAllLogins()) {
            if ((l.getRandom() + l.getNameHash()).equals(cookie)) { // TODO add check if cookie is over n days old, return false
                return l.getUserId();
            }
        }
        return -1;
    }
}