package edu.vnu.jvntext.utils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;

public class PathUtils {
    private static final String JAR_SCHEME = "jar";

    /**
     * Retrieve a path to the resource directory for the given class. This may be inside a JAR file or may be in the
     * regular filesystem.
     *
     * @param _class to get resources for
     * @return path to resources directory for {@code _class}
     * @throws URISyntaxException
     * @throws IOException
     */
    public static Path getResourceDirectory(Class<?> _class) throws URISyntaxException, IOException {
        String packageName = _class.getPackage().getName();
        String packageDir = "/" + packageName.replace('.', '/');
        return getPath(_class.getResource(packageDir).toURI());
    }

    /**
     * Retrieves a path for URI; automatically loads a new filesystem for jars when reading from "jar:" URI's.
     *
     * @return A path for the given URI
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
