/**
 * Created by Andrew Schwartz on 7/30/17.
 */


// IN ORDER TO RUN:
// START MYSQL

public class Main {
    public static void main(String[] args) {
        Database database = new Database(new JsonConfig());
        new NoteEndpoints(database);
    }
}