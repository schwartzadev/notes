/**
 * Created by Andrew Schwartz on 7/30/17.
 */


//IN ORDER TO RUN:
// START MYSQL (USE MYSQL CLI CLIENT)
import java.sql.ResultSet;

import io.javalin.Javalin;

/** TODO add trash to notes - removes from database, reloads
 *  TODO fix css - toolbar spacing
 *  TODO map sql rows to objects with jooq
 *  TODO ADD GIT **** priority
 */


public class Main {

    public static class Note {

        public String toHtml() {
            StringBuilder sb = null;

            return sb.toString();
        }

        String title;
        String body;
        String color;

        public String getTitle() {
            return title;
        }

        public String getBody() {
            return body;
        }

        public String getColor() {
            return color;
        }

        public Note(String title, String body, String color) {
            this.title = title;
            this.body = body;
            this.color = color;
            //            attrs: color, id, body, title
        }

        @Override
        public String toString() {
            return this.title + ": " + this.body + "\t\t" + this.color;
        }
    }

    public static void main(String[] args) {
        Javalin app = Javalin.create()
                .port(7777)
                .enableStaticFiles("/public")
                .start();

        app.post("/make-note", ctx -> {
            String title = ctx.formParam("title");
            String body = ctx.formParam("body");
            String color = ctx.formParam("color");
            if (title.equals("")) {
                title = "NULL";
            } else {
                title = "'" + title + "'";
            }

            if (color.equals("")) {
                color = "NULL";
            } else {
                color = "'" + color + "'";
            }

            String sql = "INSERT into notes VALUES (" + (Database.getMaxID()+1) + ", " + title + ", '" + body + "', " + color + ");";
            System.out.println(sql);
            Database.addNoteByQuery(sql);
            ctx.redirect("/viewall"); // redirect
        });

        app.get("/viewall", ctx -> {
            System.out.println("checking notes...");
            StringBuilder sb = new StringBuilder();
            ResultSet rs = Database.getNotes();
            sb.append("<head><link rel=\"stylesheet\" type=\"text/css\" href=\"styles.css\"></head>");
            sb.append("<h1>all notes</h1>");
            sb.append("<a href=\"index.html\">Add a new note</a>");
            sb.append("<div class=\"container\">");
            while(rs.next()){
                String title = rs.getString("title");
                int id = Integer.parseInt(rs.getString("id"));
                String body = rs.getString("body");
                String color = rs.getString("color");
                sb.append(Database.generateNoteHtml(title, id, body, color));
            }
            sb.append("</div>"); // end container
            ctx.html(sb.toString());
        });
    }

}