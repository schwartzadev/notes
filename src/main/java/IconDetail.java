/**
 * Created by Andrew Schwartz on 8/13/17.
 */
public class IconDetail {
    public String getIconName() {
        return iconName;
    }

    public String getEndpointName() {
        return endpointName;
    }

    private String iconName;
    private String endpointName;

    public IconDetail(String iconName, String endpointName) {
        this.iconName = iconName;
        this.endpointName = endpointName;
    }
}
