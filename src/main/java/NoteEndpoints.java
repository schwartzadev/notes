import io.javalin.Javalin;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by Andrew Schwartz on 8/9/17.
 */
public class NoteEndpoints {
    private Javalin app;
    private Database db;

    public NoteEndpoints(Database db) {
        this.db = db;
        Javalin newApp = io.javalin.Javalin.create()
                .port(7777)
                .enableStaticFiles("/public")
                .start();
        setApp(newApp);
        addIndex();
        addMakeNote();
        addDelete();
        addEdit();
        addUpdate();
    }


    private void addDelete() {
        getApp().get("/delete/:id", ctx -> {
            System.out.println(("deleting " + ctx.param("id")) + "...");
            try {
                ctx.redirect("/index.html"); // redirect
                getDb().archiveNote(Integer.parseInt(ctx.param("id"))); // can throw nfe
            } catch (NumberFormatException nfe) {
                ctx.html("invalid request. Specify a note id to delete.<br><a href=\"/index.html\">return to home</a>");
            }
        });
    }

    private void addEdit() {
        getApp().get("/edit/:id", ctx -> {
            StringBuilder sb = new StringBuilder();
            System.out.println(("editing " + ctx.param("id")) + "...");
            int id = -1;
            try {
                id = Integer.parseInt(ctx.param("id")); // can throw nfe
            } catch (NumberFormatException nfe) {
                ctx.html("invalid request. Specify a note id to edit.<br><a href=\"/index.html\">return to home</a>");
            }
            Note n = getDb().getNoteByID(id);
            sb.append("<form id=\"note-factory\" method=\"post\" action=\"/update-note\">\n" +
                    "            <label for=\"title\">Title:</label>\n");
            if (n.getTitle() != null) {
                sb.append("<input name=\"title\" id=\"title\" type=\"text\" value=\"" + n.getTitle() + "\">\n");
            }
            else {
                sb.append("<input name=\"title\" id=\"title\" type=\"text\">\n");
            }
            sb.append("<label for=\"textarea\">Body *</label>\n" +
                    "            <textarea id=\"textarea\" name=\"body\" value=\"" + n.getBody() +  "\" required>" + n.getBody() + "</textarea>" +
                    "            <label class=\"colorText\" for=\"colorchoice\">Custom hex color:</label>\n" +
                    "            <input type=\"text\" name=\"color\" id=\"colorchoice\" maxlength=\"6\" value=\"" + n.getColor() + "\">" +
                    "            <input type=\"hidden\" name=\"id\" value=\"" + n.getId() + "\">\n" +
                    "<button class=\"submit\" >Submit</button>" +
                    "</form>");
            ctx.html(sb.toString());
        });
    }

    private void addUpdate() {
        getApp().post("/update-note", ctx -> {
            int id = -1;
            try {
                id = Integer.parseInt(ctx.formParam("id")); // can throw nfe
            } catch (NumberFormatException nfe) {
                System.out.println("***update " + id + " failed");
            }

            Note n = new Note(ctx.formParam("title"), ctx.formParam("body"), id, ctx.formParam("color"));
            if (n.getTitle() != null && n.getTitle().equals("")) {
                n.setTitle(null);
            }
            if (n.getColor().equals("")) {
                n.setColor(null);
            }
            getDb().deleteNote(id);
            getDb().addNote(n);

            ctx.redirect("/index.html"); // redirect
            System.out.println("created " + n.getId() + "...");
        });
    }

    private void addIndex() {
        getApp().get("index.html", ctx -> {
            StringBuilder sb = new StringBuilder();
            String content = new String(Files.readAllBytes(Paths.get("src/main/resources/public/index.html")));
            sb.append(content);
            System.out.println("checking notes...");
            List<Note> dbNotes = getDb().getActiveNotes();
            sb.append("<head><link rel=\"stylesheet\" type=\"text/css\" href=\"styles.css\"></head>");
            sb.append("<h1 class=\"pagetitle\">all notes</h1>");
            sb.append("<div class=\"container\">");
            for (Note n : dbNotes) {
                sb.append(getDb().generateNoteHtml(n));
            }
            sb.append("</div>"); // end container
            ctx.html(sb.toString());
        });
    }
    private void addMakeNote() {
        getApp().post("/make-note", ctx -> {
            Note n = new Note(ctx.formParam("title"), ctx.formParam("body"), (getDb().getMaxID()+1), ctx.formParam("color"));
            if (n.getTitle().equals("")) {
                n.setTitle(null);;
            }
            if (n.getColor().equals("")) {
                n.setColor(null);
            }
            getDb().addNote(n);

            ctx.redirect("/index.html"); // redirect
            System.out.println("created " + n.getId() + "...");
        });
    }
    public void setApp(Javalin app) {
        this.app = app;
    }

    public Database getDb() {
        return db;
    }

    public Javalin getApp() {
        return app;
    }

}
