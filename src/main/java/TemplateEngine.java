import freemarker.template.Configuration;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Andrew Schwartz on 8/13/17.
 */
public class TemplateEngine {
    public String noteListHtml(List<Note> notes, List<IconDetail> iconDetails, User u) {
        Configuration cfg = new Configuration();
        Map<String, Object> root = new HashMap<>();
        root.put("notes", notes);
        root.put("iconlist", iconDetails);
        root.put("user", u);
        Writer out = new StringWriter();
        freemarker.template.Template temp = null;
        try {
            temp = cfg.getTemplate("src/main/resources/private/freemarker/notes.ftl");
            temp.process(root, out);
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
//        System.out.println("checking notes...");
        return out.toString();
    }

    public String registerPage() {
        Configuration cfg = new Configuration();
        Map<String, Object> root = new HashMap<>();
        Writer out = new StringWriter();
        freemarker.template.Template temp = null;
        try {
            temp = cfg.getTemplate("src/main/resources/private/freemarker/register.ftl");
            temp.process(root, out);
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        return out.toString();
    }

    public String genericPage(String fileUrl) {
        Configuration cfg = new Configuration();
        Map<String, Object> root = new HashMap<>();
        Writer out = new StringWriter();
        freemarker.template.Template temp = null;
        try {
            temp = cfg.getTemplate(fileUrl);
            temp.process(root, out);
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        return out.toString();
    }

    public String genericPage(String fileUrl, Note note) {
        Configuration cfg = new Configuration();
        Map<String, Object> root = new HashMap<>();
        root.put("n", note);
        Writer out = new StringWriter();
        freemarker.template.Template temp = null;
        try {
            temp = cfg.getTemplate(fileUrl);
            temp.process(root, out);
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        return out.toString();
    }

    public String loginPage() {
        return genericPage("src/main/resources/private/freemarker/login.ftl");
    }

    public String editPage(Note n) {
        return genericPage("src/main/resources/private/freemarker/edit.ftl", n);
    }
}
