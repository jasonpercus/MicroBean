package ${package}.gateway.file;

import com.jasonpercus.microbean.api.Adapter;
import com.jasonpercus.microbean.api.OS;
import ${package}.domain.spi.FileSPI;

@Adapter(os = OS.MAC)
public class FileMacAdapter implements FileSPI {

    @Override
    public String getSeparator() {
        return "/";
    }
}
