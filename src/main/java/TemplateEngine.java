import freemarker.template.Configuration;
import freemarker.template.Template;
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
    public String noteListHtml(List<Note> notes, List<IconDetail> iconDetails) {
        Configuration cfg = new Configuration();
        Map<String, Object> root = new HashMap<>();
        root.put("notes", notes);
        root.put("iconlist", iconDetails);
        Writer out = new StringWriter();
        freemarker.template.Template temp = null;
        try {
            temp = cfg.getTemplate("src/main/resources/public/freemarker/notes.ftl");
            temp.process(root, out);
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        System.out.println("checking notes...");
        return out.toString();
    }
}
