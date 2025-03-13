package updater.data;

public class AppInfo {

    public final String appName;
    public final String fullName;
    public final AppVersion appVersion;

    public AppInfo(String appName, AppVersion appVersion, String url) {
        this.appName = appName;
        this.appVersion = appVersion;
        this.fullName = appName + " " + appVersion;
    }

}
