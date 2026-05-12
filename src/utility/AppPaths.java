package utility;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AppPaths {

    private static final Path APP_DIR = resolveAppDir();

    private static Path resolveAppDir() {
        String dir = System.getProperty("app.dir");
        if (dir != null) {
            return Paths.get(dir);
        }
        try {
            Path jarPath = Paths.get(
                AppPaths.class.getProtectionDomain().getCodeSource().getLocation().toURI()
            );
            if (jarPath.toString().endsWith(".jar")) {
                // jpackage layout: CareerBridge/app/careerbridge.jar -> go up to CareerBridge/
                return jarPath.getParent().getParent();
            }
            // Running from bin/ directory in dev mode
            return jarPath.getParent();
        } catch (Exception e) {
            return Paths.get(System.getProperty("user.dir"));
        }
    }

    public static Path getAppDir() {
        return APP_DIR;
    }

    public static Path getDataDir() {
        Path dir = APP_DIR.resolve("data");
        dir.toFile().mkdirs();
        return dir;
    }

    public static File getModelFile() {
        return getDataDir().resolve("careerbridge_model.model").toFile();
    }

    public static File getDatasetsDir() {
        File dir = getDataDir().resolve("datasets").toFile();
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    public static File getProcessedDatasetsDir() {
        File dir = getDataDir().resolve("processed_datasets").toFile();
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }
}
