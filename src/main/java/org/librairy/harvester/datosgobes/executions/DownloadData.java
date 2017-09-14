package org.librairy.harvester.datosgobes.executions;

import org.librairy.harvester.datosgobes.service.DatasetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class DownloadData {

    private static final Logger LOG = LoggerFactory.getLogger(DownloadData.class);

    public static final String filePath = "/Users/cbadenes/Documents/OEG/Cloud/projects/DesafioAporta_2017/datasets-gob-es.csv";

    public static void main(String[] args) throws Exception {

        // Initialize Services
        DatasetService datasetService = new DatasetService();

        // Load Data
        Instant start = Instant.now();
        datasetService.load(filePath);
        Instant end = Instant.now();

        LOG.info("Datasets created from datos.gob.es at: " + filePath);

        LOG.info("Created in: "       + ChronoUnit.MINUTES.between(start,end) + "min " + (ChronoUnit.SECONDS.between(start,end)%60) + "secs");


    }
}
