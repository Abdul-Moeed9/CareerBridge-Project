package utility;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

public class DatasetController {

    private static final String DATASETS_DIR =
        AppPaths.getDatasetsDir().getAbsolutePath();

    private static final String PROCESSED_DIR =
        AppPaths.getProcessedDatasetsDir().getAbsolutePath();

    private static final Map<String, Class<? extends AbstractDatasetLoader>> LOADER_REGISTRY = new HashMap<>();

    static {
        LOADER_REGISTRY.put("Indeed", IndeedDatasetLoader.class);
        LOADER_REGISTRY.put("Rozee", RozeeDatasetLoader.class);
    }

    public static String refreshOneDataset() {
        File datasetsFolder = new File(DATASETS_DIR);
        File processedFolder = new File(PROCESSED_DIR);

        if (!datasetsFolder.exists() || !datasetsFolder.isDirectory()) {
            System.out.println("Datasets folder not found: " + DATASETS_DIR);
            return null;
        }

        if (!processedFolder.exists()) {
            processedFolder.mkdirs();
        }

        File[] files = datasetsFolder.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null || files.length == 0) {
            System.out.println("No dataset files found in: " + DATASETS_DIR);
            return null;
        }

        for (File file : files) {
            String fileName = file.getName();
            String prefix = extractPrefix(fileName);

            if (prefix == null || !LOADER_REGISTRY.containsKey(prefix)) {
                System.out.println("[WARNING] Skipping file with unknown loader type: " + fileName);
                continue;
            }

            System.out.println("Processing " + fileName + " (type: " + prefix + ")...");

            try {
                Class<? extends AbstractDatasetLoader> loaderClass = LOADER_REGISTRY.get(prefix);
                AbstractDatasetLoader loader = loaderClass
                    .getConstructor(String.class)
                    .newInstance(file.getAbsolutePath());
                loader.load();

                Path source = file.toPath();
                Path destination = new File(processedFolder, fileName).toPath();
                Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Moved " + fileName + " to processed_datasets/");
                return fileName;

            } catch (Exception e) {
                System.out.println("Error processing " + fileName + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        return null;
    }

    private static String extractPrefix(String fileName) {
        int underscoreIndex = fileName.indexOf('_');
        if (underscoreIndex <= 0) return null;
        return fileName.substring(0, underscoreIndex);
    }

    public static void main(String[] args) {
        refreshOneDataset();
    }
}
