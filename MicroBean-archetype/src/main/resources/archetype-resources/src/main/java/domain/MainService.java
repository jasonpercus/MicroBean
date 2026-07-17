package ${package}.domain;

import java.time.LocalDateTime;
import com.jasonpercus.microbean.api.ApplicationEntryPoint;
import com.jasonpercus.microbean.api.EntryPointService;
import com.jasonpercus.microbean.api.LifecycleEntryPoint;
import ${package}.domain.api.ActionServiceAPI;

@EntryPointService(lifecycle = LifecycleEntryPoint.ONE_SHOT)
public class MainService implements ApplicationEntryPoint {

    final LocalDateTime now;
    final ActionServiceAPI actionServiceAPI;

    public MainService(LocalDateTime now, ActionServiceAPI actionServiceAPI) {
        this.now = now;
        this.actionServiceAPI = actionServiceAPI;
    }

    @Override
    public void main(String[] args) {
        //TODO Implement your main service logic here.
        System.out.println("Hello from MainService! Current time: " + now);

        actionServiceAPI.executeAction("SampleAction");
    }
}
