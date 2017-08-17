import io.javalin.Context;

import java.util.Date;

/**
 * Created by Andrew Schwartz on 8/16/17.
 */
public class Login {
    private int id;
    private int userId;
    private String random;
    private String nameHash;
    private Date dateCreated;

    public Login(int id, int userId, String random, String nameHash, Date dateCreated) {
        this.id = id;
        this.userId = userId;
        this.random = random;
        this.nameHash = nameHash;
        this.dateCreated = dateCreated;
    }


    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getRandom() {
        return random;
    }

    public String getNameHash() {
        return nameHash;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

}
