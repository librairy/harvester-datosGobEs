package org.librairy.harvester.datosgobes;

import com.google.common.base.Strings;
import org.librairy.harvester.datosgobes.service.DatasetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
//@EnableAutoConfiguration
public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    private static final String banner = "\n" +
            "                         ┬  ┬┌┐ ┬─┐╔═╗╦┬─┐┬ ┬                      \n" +
            "                         │  │├┴┐├┬┘╠═╣║├┬┘└┬┘                      \n" +
            "                         ┴─┘┴└─┘┴└─╩ ╩╩┴└─ ┴                       \n" +
            "┬ ┬┌─┐┬─┐┬  ┬┌─┐┌─┐┌┬┐┌─┐┬─┐       ┌┬┐┌─┐┌┬┐┌─┐┌─┐ ┌─┐┌─┐┌┐  ┌─┐┌─┐\n" +
            "├─┤├─┤├┬┘└┐┌┘├┤ └─┐ │ ├┤ ├┬┘  ───   ││├─┤ │ │ │└─┐ │ ┬│ │├┴┐ ├┤ └─┐\n" +
            "┴ ┴┴ ┴┴└─ └┘ └─┘└─┘ ┴ └─┘┴└─       ─┴┘┴ ┴ ┴ └─┘└─┘o└─┘└─┘└─┘o└─┘└─┘" +
            "                                            \n" +
            "\n" +
            "                         cbadenes@fi.upm.es\n" +
            "                          ocorcho@fi.upm.es\n" +
            "                     Ontology Engineering Group\n" +
            "                                2017\n";



    public static void main(String[] args) throws Exception {

        System.out.println(banner);

        // Read environment variables
        String librairyHostEnv         = System.getenv("LIBRAIRY_HOST");
        String librairyHost = !Strings.isNullOrEmpty(librairyHostEnv)? librairyHostEnv : "localhost:8080";

        StringBuilder summary = new StringBuilder("Harvesting Summary:\n");
        summary.append("- Librairy Host: ").append(librairyHost).append("\n");

        LOG.info(summary.toString());


    }
}
