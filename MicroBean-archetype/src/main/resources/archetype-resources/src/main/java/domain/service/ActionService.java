package ${package}.domain.service;

import com.jasonpercus.microbean.api.Service;
import ${package}.domain.api.ActionServiceAPI;
import ${package}.domain.spi.FileSPI;

@Service
public class ActionService implements ActionServiceAPI {

    final FileSPI fileSPI;

    public ActionService(FileSPI fileSPI) {
        this.fileSPI = fileSPI;
    }

    @Override
    public void executeAction(String actionName) {
        System.out.println("Executing action: " + actionName);
        System.out.println("Using file separator from SPI: " + fileSPI.getSeparator());
    }
}
