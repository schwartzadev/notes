import org.mindrot.jbcrypt.BCrypt;

/**
 * Created by Andrew Schwartz on 8/14/17.
 */
public class User {
    private int id;
    private boolean rememberme;
    private String username;
    private String password;
    private boolean active = true;
    private String hashedPass;


    public String getHashedPass() {
        return hashedPass;
    }

    public boolean isactive() {
        return active;
    }

    public void setactive(boolean active) {
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isRememberme() {
        return rememberme;
    }

    public void setRememberme(boolean rememberme) {
        this.rememberme = rememberme;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

//    // Hash a password for the first time
//    String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
//
//// Check that an unencrypted password matches one that has
//// previously been hashed
//if (BCrypt.checkpw(candidate, hashed))
//            System.out.println("It matches");
//else
//        System.out.println("It does not match");

    public String hashPassword() {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public String hashUsername() {
        return BCrypt.hashpw(username, BCrypt.gensalt());
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User(boolean rememberme, String username, String password) {
        this.rememberme = rememberme;
        this.username = username;
        this.password = password;
        this.hashedPass = hashPassword();
    }

    public User(int id, boolean rememberme, String username, String password) {
        this.id = id;
        this.rememberme = rememberme;
        this.username = username;
        this.password = password;
        this.hashedPass = hashPassword();
    }

    public User(int id, String username, String password) {
        this.id = id;
        this.rememberme = true;
        this.username = username;
        this.password = password;
        this.hashedPass = hashPassword();
    }

}
