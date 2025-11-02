package uk.ac.newcastle.enterprisemiddleware;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.h2.tools.Server;

import java.sql.SQLException;

@QuarkusMain
public class Application {

    private static Server server;
    public static void main(String[] args) {

        // Start the Quarkus app
        Quarkus.run(args);
    }

}
