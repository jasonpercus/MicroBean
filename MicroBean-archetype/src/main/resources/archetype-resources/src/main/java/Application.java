package ${package};

import com.jasonpercus.microbean.MicroBean;
import com.jasonpercus.microbean.api.MicroBeanApplication;
import ${package}.domain.MainService;

@MicroBeanApplication
public class Application {

    public static void main(String[] args) {
        MicroBean.run(Application.class, args, MainService.class);
    }
}
