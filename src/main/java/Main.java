/**
 * Created by Andrew Schwartz on 7/30/17.
 */


//IN ORDER TO RUN:
// START MYSQL (USE MYSQL CLI CLIENT)
import java.util.ArrayList;
import java.util.List;

import io.javalin.Javalin;

import javax.xml.crypto.Data;

/** TODO add trash to notes - removes from database, reloads
 *  TODO fix css - toolbar spacing
 *  TODO map sql rows to objects with jooq
 *  TODO ADD GIT **** priority
 */


public class Main {
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
            Database.executeQuery(sql);
            ctx.redirect("/viewall"); // redirect
        });

        app.get("/viewall", ctx -> {
            System.out.println("checking notes...");
            StringBuilder sb = new StringBuilder();
            List<Note> dbNotes = new ArrayList<Note>();
            dbNotes = Database.getNotes();
            sb.append("<head><link rel=\"stylesheet\" type=\"text/css\" href=\"styles.css\"></head>");
            sb.append("<h1>all notes</h1>");
            sb.append("<a href=\"index.html\">Add a new note</a>");
            sb.append("<div class=\"container\">");
            for (Note n : dbNotes) {
                sb.append(Database.generateNoteHtml(n));
            }
            sb.append("</div>"); // end container
            ctx.html(sb.toString());
        });

        app.get("/delete/:id", ctx -> {
            System.out.println(("deleting " + ctx.param("id")) + "...");
            Database.executeQuery("delete from notes where id=" + ctx.param("id") + ";"); // delete query
            ctx.redirect("/viewall"); // redirect
        });
    }

}
