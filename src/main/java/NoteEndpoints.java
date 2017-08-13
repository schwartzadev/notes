import io.javalin.Context;
import io.javalin.Javalin;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Andrew Schwartz on 8/9/17.
 */
public class NoteEndpoints {
    private Javalin app;
    private Database db;
    private Parser parser = Parser.builder().build();
    private HtmlRenderer renderer = HtmlRenderer.builder().build();
    private PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.FORMATTING);

    public NoteEndpoints(Database db) {
        this.db = db;
        Javalin newApp = io.javalin.Javalin.create()
                .port(7777)
                .enableStaticFiles("/public")
                .start();
        setApp(newApp);
        registerEndpoints();
    }

    private void registerEndpoints() {
        getApp().get("index.html", this::index);
        getApp().post("/make-note", this::makeNote);
        getApp().get("/delete/:id", this::deleteNote);
        getApp().get("/edit/:id", this::editNote);
        getApp().get("/restore/:id", this::restore);
        getApp().get("/archived.html", this::archived);
        getApp().post("/update-note", this::updateNote);
    }

    private void archived(Context ctx) {
        List<Note> notes = getDb().getArchivedNotes();
        Map<String, String> icons = new HashMap<String, String>();
        icons.put("edit", "pencil");
        icons.put("restore", "undo");
        notePage(ctx, notes, icons);
    }

    private void restore(Context ctx) {
        System.out.println("restoring " + ctx.param("id") + "...");
        try {
            ctx.redirect("/index.html"); // redirect
            getDb().restoreNote(Integer.parseInt(ctx.param("id"))); // can throw nfe
        } catch (NumberFormatException nfe) {
            ctx.html("invalid request. Specify a note id to restore.<br><a href=\"/index.html\">return to home</a>");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteNote(Context ctx) {
        System.out.println(("deleting " + ctx.param("id")) + "...");
        try {
            ctx.redirect("/index.html"); // redirect
            getDb().archiveNote(Integer.parseInt(ctx.param("id"))); // can throw nfe
        } catch (NumberFormatException nfe) {
            ctx.html("invalid request. Specify a note id to delete.<br><a href=\"/index.html\">return to home</a>");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void editNote(Context ctx) {
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
    }

    private void updateNote(Context ctx) {
        String safe = policy.sanitize((ctx.formParam("body")));
        Node body = parser.parse(safe);
        int id = -1;
        try {
            id = Integer.parseInt(ctx.formParam("id")); // can throw nfe
        } catch (NumberFormatException nfe) {
            System.out.println("***update " + id + " failed");
        }

        Note n = new Note(ctx.formParam("title"), safe, id, ctx.formParam("color"), renderer.render(body));
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
    }

    private void notePage(Context ctx, List<Note> notes, Map<String, String> icons) {
        /**
         * template for index and archived pages
         */
        StringBuilder sb = new StringBuilder();
        try {
            sb.append(new String(Files.readAllBytes(Paths.get("src/main/resources/public/header.html"))));
            sb.append(new String(Files.readAllBytes(Paths.get("src/main/resources/public/index.html"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("checking notes...");
        sb.append("<head><link rel=\"stylesheet\" type=\"text/css\" href=\"styles.css\"></head>");
        sb.append("<h1 class=\"pagetitle\">all notes</h1>");
        sb.append("<div class=\"container\">");
        for (Note n : notes) {
            sb.append(getDb().generateNoteHtml(n, icons));
        }
        sb.append("</div>"); // end container
        ctx.html(sb.toString());
    }

    private void index(Context ctx) {
        List<Note> dbNotes = getDb().getActiveNotes();
        Map<String, String> icons = new HashMap<String, String>();
        icons.put("delete", "trash");
        icons.put("edit", "pencil");

        notePage(ctx, dbNotes, icons);
    }

    private void makeNote(Context ctx) {
        String safe = policy.sanitize(ctx.formParam("body"));
        Node body = parser.parse(safe);
        Note n = new Note(ctx.formParam("title"), safe, (getDb().getMaxID()+1), ctx.formParam("color"), renderer.render(body));
        if (n.getTitle().equals("")) {
            n.setTitle(null);;
        }
        if (n.getColor().equals("")) {
            n.setColor(null);
        }
        getDb().addNote(n);

        ctx.redirect("/index.html"); // redirect
        System.out.println("created " + n.getId() + "...");
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
