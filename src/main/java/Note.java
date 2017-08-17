import java.util.Random;

/**
 * Created by Andrew Schwartz on 8/7/17.
 */
public class Note {
    private String title;
    private String body;
    private int id;
    private int userId;
    private String color;
    private boolean archived; // 0 is false, non-zero is true (will use 1)
    private String html;

    public Note(String title, String body, int id, String color, int userId) {
        this.title = title;
        this.body = body;
        this.id = id;
        this.color = color;
        this.archived = false; // not archived
        this.userId = userId;
//        this.html = null;
    }

    public Note(String title, String body, int id, String color, String html, int userId) {
        this.title = title;
        this.body = body;
        this.id = id;
        this.color = color;
        this.archived = false; // not archived
        this.html = html;
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public boolean getArchived() {
        return archived;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public void archive() {
        this.archived = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title=title;
    }

    public String getBody(){
        return body;
    }

    public void setBody(String body){
        this.body=body;
    }

    public String getColor() {
        if (color == null) {
            setColor(getRandomColor());
        }
        return color;
    }

    public String getRandomColor() {
        final String[] colors  = {"70d5d8", "8dffcd", "ebbab9", "eda6dd", "c09bd8", "9f97f4", "a4def9"};
        int rnd = new Random().nextInt(colors.length);
        return colors[rnd];
    }

    public void setColor(String color){
        this.color=color;
    }
}
