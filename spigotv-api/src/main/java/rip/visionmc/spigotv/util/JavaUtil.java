package rip.visionmc.spigotv.util;

public class JavaUtil {
    public static int getJavaVersionOpcode() {
        String javaVersion = System.getProperty("java.version");
        int majorVersion;

        if (javaVersion.startsWith("1.")) {
            majorVersion = Integer.parseInt(javaVersion.substring(2, 3));
        } else {
            int dotIndex = javaVersion.indexOf('.');
            majorVersion = dotIndex == -1
                    ? Integer.parseInt(javaVersion)
                    : Integer.parseInt(javaVersion.substring(0, dotIndex));
        }

        switch (majorVersion) {
            case 8:  return 52;
            case 9:  return 53;
            case 10: return 54;
            case 11: return 55;
            case 12: return 56;
            case 13: return 57;
            case 14: return 58;
            case 15: return 59;
            case 16: return 60;
            case 17: return 61;
            case 18: return 62;
            case 19: return 63;
            case 20: return 64;
            case 21: return 65;
            case 22: return 66;
            case 23: return 67;
            default: throw new UnsupportedOperationException("Unsupported Java version: " + majorVersion);
        }
    }
}
