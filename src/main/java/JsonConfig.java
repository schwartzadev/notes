import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Andrew Schwartz on 8/16/17.
 */
public class JsonConfig implements Config {
    private JSONObject config;
    public JsonConfig() {
        String content = null;
        try {
            content = new String(Files.readAllBytes(Paths.get("config.json")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject root = new JSONObject(content);
        this.config = root;
    }
    public String getSqlUsername() {
        return config.getJSONObject("SQL").getString("username");
    }
    public String getSqlPassword() {
        return config.getJSONObject("SQL").getString("password");
    }

    public String getDbUrl() {
        return config.getJSONObject("SQL").getString("url");
    }
}
