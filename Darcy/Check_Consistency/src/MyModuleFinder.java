import java.lang.module.ModuleReference;
import java.util.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.module.ModuleDescriptor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.stream.Stream;

class MyModuleFinder {
    private Path baseDir;

    private MyModuleFinder(Path path) {
        this.baseDir = path;
    }

    public static MyModuleFinder of(Path path) {
        return new MyModuleFinder(path);
    }

    public Set<ModuleReference> findAll() {
        Set<ModuleReference> result = new HashSet<>();
        HashSet<String> moduleNames = new HashSet<>();
        try {
            Stream<Path> classPaths = Files.walk(this.baseDir);
            classPaths.forEach(p -> {
                if (p.getFileName().toString().equals("module-info.class")) {
                    try {
                        InputStream is = Files.newInputStream(p);
                        ModuleDescriptor md = ModuleDescriptor.read(is);
                        if (!moduleNames.contains(md.name())) {
                            moduleNames.add(md.name());
                            result.add(new MyModuleReference(md, null));
                        }
                    } catch (IOException ex) {
                        throw new UncheckedIOException(ex);
                    }
                }
            });
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        } finally {
            return result;
        }
    }
}