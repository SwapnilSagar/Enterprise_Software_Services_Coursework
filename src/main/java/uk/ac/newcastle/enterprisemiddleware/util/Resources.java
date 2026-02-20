package uk.ac.newcastle.enterprisemiddleware.util;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Named;
import java.util.logging.Logger;

/**
 * @author Swapnil Sagar
 * */

public class Resources {



    @Produces
    @Named("logger")
    public Logger produceLog(InjectionPoint injectionPoint) {
        return Logger.getLogger(injectionPoint.getMember().getDeclaringClass().getName());


    }

}

