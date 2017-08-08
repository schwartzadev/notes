/**
 * Created by Andrew Schwartz on 7/30/17.
 */


//IN ORDER TO RUN:
// START MYSQL (USE MYSQL CLI CLIENT)
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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

            Connection conn = DriverManager.getConnection(Database.DB_URL,Database.USER,Database.PASS);
            PreparedStatement sql =  conn.prepareStatement(
                    "INSERT into notes VALUES ( ? , ? , ? , ? , ? )" );
            sql.setInt(1, n.getId());
            sql.setString(2, n.getTitle());
            sql.setString(3, n.getBody());
            sql.setString(4, n.getColor());
            sql.setInt(5, n.getArchived());
            Database.executeQuery(conn, sql);


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
            int id;
            try {
                id = Integer.parseInt(ctx.param("id"));
                ctx.redirect("/viewall"); // redirect
                Connection conn = DriverManager.getConnection(Database.DB_URL,Database.USER,Database.PASS);
                PreparedStatement sql =  conn.prepareStatement(
                        "update notes set archived = 1 where id = ? ;" );
                sql.setInt(1, id);
                Database.executeQuery(conn, sql);
            } catch (NumberFormatException nfe) {
                ctx.html("invalid request. Specify a note id to delete.<br><a href=\"/viewall\">return to home</a>");
            }
        });
    }

}
