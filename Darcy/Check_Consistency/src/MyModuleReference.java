import java.io.IOException;
import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleReader;
import java.lang.module.ModuleReference;
import java.net.URI;

public class MyModuleReference extends ModuleReference {

    public MyModuleReference(ModuleDescriptor md, URI location) {
        super(md, location);
    }

    @Override
    public ModuleReader open() throws IOException {
        return null;
    }
}
