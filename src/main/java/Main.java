import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.ConfigurationBuilderEvent;
import org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.reloading.PeriodicReloadingTrigger;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class Main {

    public static PropertiesConfiguration config;

    public static void main(String[] args) {
        Parameters params = new Parameters();
        File propertiesFile = new File(args[0]);
        ReloadingFileBasedConfigurationBuilder<PropertiesConfiguration> builder = new ReloadingFileBasedConfigurationBuilder<>(PropertiesConfiguration.class)
                .configure(params.fileBased().setFile(propertiesFile));
        builder.addEventListener(ConfigurationBuilderEvent.ANY, event -> {
            if (event.getEventType() == ConfigurationBuilderEvent.RESET) {
                try {
                    config = builder.getConfiguration();
                } catch (ConfigurationException e) {
                    System.out.println("Reload configuration error.");
                    e.printStackTrace();
                }
            }
        });
        PeriodicReloadingTrigger periodicReloadingTrigger = new PeriodicReloadingTrigger(builder.getReloadingController(), null, 15,
                TimeUnit.SECONDS);
        periodicReloadingTrigger.start();
        try {
            config = builder.getConfiguration();
        } catch (ConfigurationException e) {
            System.out.println("Init configuration error.");
            e.printStackTrace();
        }
        config.getKeys().forEachRemaining(System.out::println);
    }
}
