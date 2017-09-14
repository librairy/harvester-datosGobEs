package org.librairy.harvester.datosgobes.executions;

import org.librairy.harvester.datosgobes.model.Row;
import org.librairy.harvester.datosgobes.service.ParsingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class ParseData {

    private static final Logger LOG = LoggerFactory.getLogger(ParseData.class);

    public static final String filePath = "/Users/cbadenes/Documents/OEG/Cloud/projects/DesafioAporta_2017/datasets-gob-es-parsed.csv";

    private static Function<String, Row> mapToItem = (line) -> {
        return Row.fromCSV(line);
    };

    private static List<Row> load(String csvFile){
        List<Row> rows = new ArrayList<Row>();
        try{
            File inputF = new File(csvFile);
            InputStream inputFS = new FileInputStream(inputF);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));
            //inputList = br.lines().skip(1).map(mapToItem).collect(Collectors.toList());
            rows = br.lines().map(mapToItem).collect(Collectors.toList());
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rows;
    }


    public static void main(String[] args) throws Exception {

        String inputPath    = DownloadData.filePath;
        String outputPath   = filePath;

        // Prepare output file
        FileWriter writer = new FileWriter(new File(outputPath));


        // Initialize Services
        ParsingService parsingService = new ParsingService();


        // Tokenize descriptions
        Instant start = Instant.now();
        load(inputPath).parallelStream().map( d -> d.setDescription(parsingService.tokenize(d.getDescription()))).forEach(d -> {
            try {
                writer.write(d.toCSV()+"\n");
                System.out.println("Dataset processed: " + d.getId());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Instant end = Instant.now();

        writer.close();


        LOG.info("Datasets parsed from datos.gob.es at: " + outputPath);

        LOG.info("Executed in: "       + ChronoUnit.MINUTES.between(start,end) + "min " + (ChronoUnit.SECONDS.between(start,end)%60) + "secs");


    }



}
