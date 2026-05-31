package util;


/*
 *  ██████╗ ██╗      █████╗ ███╗   ██╗███████╗██╗   ██╗███╗   ██╗ ██████╗
 *  ██╔══██╗██║     ██╔══██╗████╗  ██║██╔════╝╚██╗ ██╔╝████╗  ██║██╔════╝
 *  ██████╔╝██║     ███████║██╔██╗ ██║███████╗ ╚████╔╝ ██╔██╗ ██║██║     
 *  ██╔═══╝ ██║     ██╔══██║██║╚██╗██║╚════██║  ╚██╔╝  ██║╚██╗██║██║     
 *  ██║     ███████╗██║  ██║██║ ╚████║███████║   ██║   ██║ ╚████║╚██████╗
 *  ╚═╝     ╚══════╝╚═╝  ╚═╝╚═╝  ╚═══╝╚══════╝   ╚═╝   ╚═╝  ╚═══╝ ╚═════╝
 *
 *  PlanSync source guide
 *  - This file includes a short header describing the class or interface purpose.
 *  - Method comments mark the responsibility of each section so the flow is easier to follow.
 */
/**
 * File purpose: This class supports the AppPaths part of PlanSync and documents the main responsibilities of the file.
 */

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class AppPaths {
    private static final Path APP_ROOT = detectAppRoot();
    private static final Path DATA_DIR = APP_ROOT.resolve("data");

    private AppPaths() {}

    // Section: Return the data used to app root.
    public static Path getAppRoot() {
        return APP_ROOT;
    }

    // Section: Return the data used to data dir.
    public static Path getDataDir() {
        // Section: Handle the logic for ensure directory.
        ensureDirectory(DATA_DIR);
        return DATA_DIR;
    }

    // Section: Return the data used to data file.
    public static File getDataFile(String fileName) {
        return getDataDir().resolve(fileName).toFile();
    }

    // Section: Return the data used to app file.
    public static File getAppFile(String relativePath) {
        return APP_ROOT.resolve(relativePath).toFile();
    }

    // Section: Handle the logic for resolve from app root.
    public static Path resolveFromAppRoot(String relativePath) {
        return APP_ROOT.resolve(relativePath);
    }

    // Section: Handle the logic for detect app root.
    private static Path detectAppRoot() {
        try {
            Path location = Paths.get(AppPaths.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI());

            if (Files.isRegularFile(location)) {
                return location.getParent();
            }
            return location;
        // Section: Handle the logic for catch.
        } catch (URISyntaxException | RuntimeException e) {
            return Paths.get(System.getProperty("user.dir", ".")).toAbsolutePath().normalize();
        }
    }

    // Section: Handle the logic for ensure directory.
    private static void ensureDirectory(Path dir) {
        try {
            Files.createDirectories(dir);
        // Section: Handle the logic for catch.
        } catch (Exception ignored) {
        }
    }
}
