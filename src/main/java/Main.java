/**
 * Created by Andrew Schwartz on 7/30/17.
 */


//IN ORDER TO RUN:
// START MYSQL (USE MYSQL CLI CLIENT)
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import io.javalin.Javalin;



public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create()
                .port(7777)
                .enableStaticFiles("/public")
                .start();

        app.post("/make-note", ctx -> {
            Note n = new Note(ctx.formParam("title"), ctx.formParam("body"), (Database.getMaxID()+1), ctx.formParam("color"));
            if (n.getTitle().equals("")) {
                n.setTitle(null);;
            }
            if (n.getColor().equals("")) {
                n.setColor(null);
            }
            Database.addNote(n);

            ctx.redirect("/index.html"); // redirect
            System.out.println("created " + n.getId() + "...");
        });

        app.get("index.html", ctx -> {
            StringBuilder sb = new StringBuilder();
            String content = new String(Files.readAllBytes(Paths.get("src/main/resources/public/index.html")));
            sb.append(content);
            System.out.println("checking notes...");
            List<Note> dbNotes = Database.getActiveNotes();
            sb.append("<head><link rel=\"stylesheet\" type=\"text/css\" href=\"styles.css\"></head>");
            sb.append("<h1 class=\"pagetitle\">all notes</h1>");
            sb.append("<div class=\"container\">");
            for (Note n : dbNotes) {
                sb.append(Database.generateNoteHtml(n));
            }
            sb.append("</div>"); // end container
            ctx.html(sb.toString());
        });

        app.get("/delete/:id", ctx -> {
            System.out.println(("deleting " + ctx.param("id")) + "...");
            try {
                ctx.redirect("/index.html"); // redirect
                Database.deleteNote(Integer.parseInt(ctx.param("id"))); // can throw nfe
            } catch (NumberFormatException nfe) {
                ctx.html("invalid request. Specify a note id to delete.<br><a href=\"/index.html\">return to home</a>");
            }
        });
    }

}
