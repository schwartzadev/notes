/**
 * Created by Andrew Schwartz on 7/30/17.
 */


//IN ORDER TO RUN:
// START MYSQL (USE MYSQL CLI CLIENT)
import java.util.List;

import io.javalin.Javalin;

/**
 */


public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create()
                .port(7777)
                .enableStaticFiles("/public")
                .start();

        app.post("/make-note", ctx -> {
            Note n = new Note(ctx.formParam("title"), ctx.formParam("body"), (Database.getMaxID()+1), ctx.formParam("color"));
            if (n.getTitle().equals("")) {
                n.setTitle("NULL");;
            } else {
                n.setTitle("'" + n.getTitle() + "'");
            }

            if (n.getColor().equals("")) {
                n.setColor("NULL");
            } else {
                n.setColor("'" + n.getColor() + "'");
            }
            String sql = "INSERT into notes VALUES (" + n.getId() + ", " + n.getTitle() + ", '" + n.getBody() + "', " + n.getColor() + ", " + n.getArchived() + ");";
            System.out.println(sql);
            Database.executeQuery(sql);
            ctx.redirect("/viewall"); // redirect
            System.out.println("created " + n.getId() + "...");
        });

        app.get("/viewall", ctx -> {
            System.out.println("checking notes...");
            StringBuilder sb = new StringBuilder();
            List<Note> dbNotes = Database.getActiveNotes();
            sb.append("<head><link rel=\"stylesheet\" type=\"text/css\" href=\"styles.css\"></head>");
            sb.append("<h1 class=\"pagetitle\">all notes  </h1>");
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
            Database.executeQuery("update notes set archived = 1 where id =" + ctx.param("id") + ";"); // delete query
            ctx.redirect("/viewall"); // redirect
        });
    }

}
