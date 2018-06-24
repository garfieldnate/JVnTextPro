package vnu.jvntext.utils;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;

public class PathUtils {
    private static final String JAR_SCHEME = "jar";
    /**
     * Retrieves a path for URI; automatically loads a new filesystem for jars when reading from "jar:" URI's.
     *
     * @return A path for the given URI
     * @throws IOException
     */
    public static Path getPath(URI uri) throws IOException {
        if (JAR_SCHEME.equals(uri.getScheme())) {
            for (FileSystemProvider provider : FileSystemProvider.installedProviders()) {
                if (provider.getScheme().equalsIgnoreCase(JAR_SCHEME)) {
                    try {
                        // caches the new provider
                        provider.getFileSystem(uri);
                    } catch (FileSystemNotFoundException e) {
                        // initialize the filesystem for this jar
                        provider.newFileSystem(uri, Collections.emptyMap());
                    }
                    break;
                }
            }
        }
        return Paths.get(uri);
    }
}
