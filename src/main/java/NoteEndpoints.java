import io.javalin.Context;
import io.javalin.Javalin;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.mindrot.jbcrypt.BCrypt;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Andrew Schwartz on 8/9/17.
 */
public class NoteEndpoints {
    private Javalin app;
    private Database db;
    private List<Extension> extensions = Arrays.asList(StrikethroughExtension.create());
    private Parser parser = Parser.builder().extensions(extensions).build();
    private HtmlRenderer renderer = HtmlRenderer.builder().extensions(extensions).build();
    private PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.FORMATTING);
    private TemplateEngine te = new TemplateEngine();


    public NoteEndpoints(Database db) {
        this.db = db;
        Javalin newApp = io.javalin.Javalin.create()
                .port(getHerokuAssignedPort())
                .enableStaticFiles("/public")
                .start();
        setApp(newApp);
        registerEndpoints();
    }

    private static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 7777;
    }

    private void registerEndpoints() {
        getApp().get("index.html", this::index);
        getApp().post("/make-note", this::makeNote);
        getApp().get("/delete/:id", this::deleteNote);
        getApp().get("/edit/:id", this::editNote);
        getApp().get("/restore/:id", this::restore);
        getApp().get("/archived.html", this::archived);
        getApp().post("/update-note", this::updateNote);
        getApp().post("/signup", this::registerHandler);
        getApp().get("/register", this::registerPage);
        getApp().get("/login", this::loginPage);
        getApp().post("/sign-in", this::loginHandler);
        getApp().get("/", this::rootRedirect);
        getApp().get("/logout", this::logOut);
        getApp().get("/pin/:id", this::pin);
        getApp().get("/un-pin/:id", this::unpin);
//        getApp().get("/herokuvartest", this::herokuTest);
        getApp().get("/status", this::status);
    }

    private void status(Context context) {
        context.html("<h1>online!</h1>");
    }

    private void herokuTest(Context c) {
        String user = System.getenv("SqlUser");
        String pass = System.getenv("SqlPass");
        String url = System.getenv("SqlUrl");
        c.html("user: " + user + "<br><br>pass: " + pass + "<br><br>url: " + url);
    }

    private void logOut(Context context) {
        context.status(202);
        db.deleteLogin(db.checkCookie(context.cookie("com.aschwartz.notes")));
        context.removeCookie("com.aschwartz.notes");
        context.redirect("/login");
    }

    private void loginPage(Context ctx) {
        ctx.html(te.loginPage());
        ctx.status(200);
    }

    private void loginHandler(Context ctx) {
        User user = db.lookupUserByUsername(ctx.formParam("username"));

        if (BCrypt.checkpw(ctx.formParam("pwd"), user.getPassword())) {
//            correct password
            ctx.html("correct pass");
            ctx.cookie("com.aschwartz.notes", db.saveLogin(user));
            ctx.redirect("/index.html");
            ctx.status(200);
        }
        else {
            ctx.status(401);
            ctx.redirect("/login");
        }
    }

    private void registerPage(Context ctx) {
        ctx.html(te.registerPage());
        ctx.status(200);
    }

    private void registerHandler(Context ctx) {
        boolean remember = false;
        if (ctx.formParam("remember").equals("on")) {
            remember = true;
        }
        String pass = ctx.formParam("pwd");
        String user = ctx.formParam("username");
        if (pass.length() < 6 ||
                pass.length() > 255 ||
                user.length() > 255 ||
                user.length() < 4) {
            ctx.redirect("/register");
        }
        else {
            db.addUser(new User(db.getMaxID("users") + 1, remember, user, pass));
            ctx.status(200);
            ctx.redirect("/login");
        }
    }

    private void rootRedirect(Context ctx) {
        ctx.status(200);
        ctx.redirect("/index.html");
    }

    private void archived(Context ctx) {
        if (db.checkCookie(ctx.cookie("com.aschwartz.notes")) != null) {
            List<Note> notes = getDb().getArchivedNotes(db.checkCookie(ctx.cookie("com.aschwartz.notes")).getUserId());
            List<IconDetail> details = new ArrayList<>();
//            details.add(new IconDetail("pencil", "edit"));
            details.add(new IconDetail("undo", "restore"));
            ctx.html(notePage(notes, details, ctx.cookie("com.aschwartz.notes")));
            ctx.status(200);
        }
        else {
            ctx.status(401);
            ctx.redirect("/login");
        }
    }

    private void restore(Context ctx) {
        int id = Integer.parseInt(ctx.param("id"));
        System.out.println("[" + ctx.ip() + "] restoring " + id + "...");
        if (checkAuth(ctx)) {
            try {
                ctx.status(200);
                getDb().restoreNote(id); // can throw nfe
                ctx.redirect("/archived.html"); // redirect
            } catch (NumberFormatException nfe) {
                ctx.status(400);
                ctx.html("invalid request. Specify a note id to restore.<br><a href=\"/index.html\">return to home</a>");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ctx.status(403);
        }
    }

    private void pin(Context ctx) {
        System.out.println("[" + ctx.ip() + "] pinned " + ctx.param("id"));
        if (checkAuth(ctx)) {
            int nId = Integer.parseInt(ctx.param("id"));
            try {
                getDb().pinNote(nId); // can throw nfe
                ctx.status(201);
                ctx.redirect("/index.html#" + nId); // redirect
            } catch (NumberFormatException nfe) {
                ctx.status(500);
                ctx.html("invalid request. Specify a note id to pin.<br><a href=\"/index.html\">return to home</a>");
            } catch (Exception e) {
                ctx.status(500);
                e.printStackTrace();
            }
        } else {
            ctx.status(403);
        }
    }

    private void unpin(Context ctx) {
        System.out.println("[" + ctx.ip() + "] un-pinned " + ctx.param("id"));
        if (checkAuth(ctx)) {
            int nId = Integer.parseInt(ctx.param("id"));
            try {
                getDb().unPinNote(nId); // can throw nfe
                ctx.status(201);
                ctx.redirect("/index.html"); // redirect
            } catch (NumberFormatException nfe) {
                ctx.status(500);
                ctx.html("invalid request. Specify a note id to pin.<br><a href=\"/index.html\">return to home</a>");
            } catch (Exception e) {
                ctx.status(500);
                e.printStackTrace();
            }
        } else {
//            ctx.html("access denied");
            ctx.status(403);
        }
    }

    private void deleteNote(Context ctx) {
        System.out.println("[" + ctx.ip() + "] deleting " + ctx.param("id") + "...");
        if (checkAuth(ctx)) {
            try {
                getDb().archiveNote(Integer.parseInt(ctx.param("id"))); // can throw nfe
                ctx.status(201);
            } catch (NumberFormatException nfe) {
                ctx.status(500);
                ctx.html("invalid request. Specify a note id to delete.<br><a href=\"/index.html\">return to home</a>");
            } catch (Exception e) {
                ctx.status(500);
                e.printStackTrace();
            }
        } else {
//            ctx.html("access denied");
            ctx.status(403);
        }
    }

    private void editNote(Context ctx) {
        int id = -1;
        if (checkAuth(ctx)) {
            try {
                id = Integer.parseInt(ctx.param("id")); // can throw nfe
            } catch (NumberFormatException nfe) {
                ctx.status(403);
            }
            Note n = getDb().getNoteByID(id);
            ctx.status(200);
            ctx.html(te.editPage(n));
        }
    }

    private void updateNote(Context ctx) {
        if (db.checkCookie(ctx.cookie("com.aschwartz.notes")) != null) {
            int userId = db.checkCookie(ctx.cookie("com.aschwartz.notes")).getUserId();
            String safeBody = policy.sanitize((ctx.formParam("body")));
            String title = policy.sanitize(ctx.formParam("title"));
            Node genHtml = parser.parse(safeBody);
            int id = -1;
            try {
                id = Integer.parseInt(ctx.formParam("id")); // can throw nfe
            } catch (NumberFormatException nfe) {
                System.out.println("[" + ctx.ip() + "] ***update " + id + " failed");
            }
            Note n = new Note(title, safeBody, id, ctx.formParam("color"), renderer.render(genHtml), userId);
            if (n.getTitle().equals("")) {
                n.setTitle(null);
            }
            if (n.getColor().equals("")) {
                n.setColor(null);
            }
            getDb().updateNote(n);
            ctx.status(200);
            ctx.redirect("/index.html#" + n.getId()); // redirect
            System.out.println("[" + ctx.ip() + "] created " + n.getId() + "...");
        }
        else {
            ctx.status(401);
            ctx.redirect("/login");
        }
    }

    private String notePage(List<Note> notes, List<IconDetail> iconDetails, String cookie) {
        /**
         * template for index and archived pages
         */
        return (te.noteListHtml(notes, null,
                iconDetails,
                db.getUserByID(db.checkCookie(cookie).getUserId()))
        );
    }

    private String notePage(List<Note> notes, List<Note> pins, List<IconDetail> iconDetails, String cookie) {
        /**
         * template for index and archived pages
         */
        return (te.noteListHtml(notes, pins,
                iconDetails,
                db.getUserByID(db.checkCookie(cookie).getUserId()))
        );
    }

    private void index(Context ctx) {
        // check for login cookie
        String cookie = ctx.cookie("com.aschwartz.notes");
        int loggedInUser = 0;
        if (cookie != null) { // only check cookie if it exists
            loggedInUser = db.checkCookie(cookie).getUserId();
            System.out.println("[" + ctx.ip() + "] " + loggedInUser + " is logged in");
            List<Note> dbNotes = getDb().getMainNotes(loggedInUser);
            List<IconDetail> details = new ArrayList<>();
            details.add(new IconDetail("trash", "delete"));
            details.add(new IconDetail("pencil", "edit"));
            details.add(new IconDetail("push-pin", "pin"));
            details.add(new IconDetail("share", "share"));
            ctx.html(notePage(dbNotes, getDb().getPinnedNotes(loggedInUser), details, ctx.cookie("com.aschwartz.notes")));
            ctx.status(200);
        } else {
            ctx.status(401);
            ctx.redirect("/login");
        }
    }

    private void makeNote(Context ctx) {
        Login cookie  = db.checkCookie(ctx.cookie("com.aschwartz.notes"));
        if (cookie != null) {
            int userid = cookie.getUserId();
            String cleanBody = policy.sanitize(ctx.formParam("body"));
            String cleanTitle  = policy.sanitize(ctx.formParam("title"));
            Node body = parser.parse(cleanBody);
            Note n = new Note(cleanTitle, cleanBody, (getDb().getMaxID("notes") + 1), ctx.formParam("color"), renderer.render(body), userid);
            if (n.getTitle().equals("")) {
                n.setTitle(null);
            }
            if (n.getColor().equals("")) {
                n.setColor(null);
            }
            getDb().addNote(n);
            ctx.status(200);
            ctx.redirect("/index.html");
            System.out.println("[" + ctx.ip() + "] " + userid + " created " + n.getId() + "...");
        }
        else {
            ctx.status(401);
            ctx.redirect("/login");
        }
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

    private boolean checkAuth(Context ctx) {
        int loggedInUserId;
        int noteOwnerUserId;
        try {
            loggedInUserId = db.checkCookie(ctx.cookie("com.aschwartz.notes")).getUserId();
            noteOwnerUserId = getDb().getNoteByID(Integer.parseInt(ctx.param("id"))).getUserId();
        } catch (NullPointerException npe) {
            return false;
        }
        return loggedInUserId == noteOwnerUserId;
    }

}
