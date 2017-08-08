/**
 * Created by Andrew Schwartz on 8/7/17.
 */
public class Note {
    private String title;
    private String body;
    private int id;
    private String color;

    public Note(String title, String body, int id, String color) {
        this.title = title;
        this.body = body;
        this.id = id;
        this.color = color;
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

    public String getColor(){
        return color;
    }

    public void setColor(String color){
        this.color=color;
    }
}
