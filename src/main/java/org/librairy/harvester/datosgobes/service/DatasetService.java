package org.librairy.harvester.datosgobes.service;

import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.Predicate;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.minidev.json.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.librairy.harvester.datosgobes.model.Row;
import org.librairy.harvester.datosgobes.model.TimeExpression;
import org.librairy.harvester.datosgobes.rest.DatosGobEsRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class DatasetService {

    private static final Logger LOG = LoggerFactory.getLogger(DatasetService.class);

    private static final Predicate validPredicate = context -> true;

    private final DatosGobEsRestClient client;

    public DatasetService(){
        this.client = new DatosGobEsRestClient("dataset");
    }

    public void load(String filePath){

        boolean finished = false;

        Map<String,Object> parameters = new HashMap<>();
        parameters.put("_sort","title");
        Integer itemsPerPage = 200;
        parameters.put("_pageSize",String.valueOf(itemsPerPage));
        int index = 0;

        Escaper escaper = Escapers.builder()
                .addEscape('\'',"")
                .addEscape('('," ")
                .addEscape(')'," ")
                .addEscape('['," ")
                .addEscape(']'," ")
                .addEscape('-',"")
                .addEscape('\n'," ")
                .addEscape('\"',"")
                .build();

        Escaper nameEscaper = Escapers.builder()
                .addEscape('\'',"")
                .addEscape('('," ")
                .addEscape(')'," ")
                .addEscape('['," ")
                .addEscape(']'," ")
                .addEscape('-',"")
                .addEscape('\n'," ")
                .addEscape('\"',"")
                .addEscape(','," ")
                .build();


        try {
            // Patients With Gener and Date
            FileWriter writer1 = new FileWriter(new File(filePath));

            while(!finished){

                parameters.put("_page",String.valueOf(index));

                LOG.info("Dataset Page: " + index);
                String response = client.get(parameters);


                DocumentContext documentContext = JsonPath.parse(response);
                try{


                    List<LinkedHashMap> items = documentContext.read("$.result.items[*]", validPredicate);


                    for (LinkedHashMap item : items){

                        Row row = new Row();

                        // Identifier
                        String id           = (String) item.get("_about");
                        row.setId(id);

                        // Category
                        Object themes       = item.getOrDefault("theme"," ");
                        String categories   = (themes instanceof JSONArray)?
                                ((List<String>) themes).stream().map(t -> StringUtils.replace(t," ","_")).map(t -> StringUtils.substringAfterLast(t,"/")).collect(Collectors.joining(" ")):
                                StringUtils.substringAfterLast(themes.toString(),"/");
                        row.setCategories(categories);

                        // Name
                        Object titles       = item.getOrDefault("title"," ");
                        String name         = nameEscaper.escape((titles instanceof JSONArray)? ((List<String>) titles).get(0) : String.valueOf(titles));
                        row.setName(name);

                        // Tags
                        Object keywords     = item.getOrDefault("keyword"," ");
                        String tags = (keywords instanceof JSONArray)?
                                ((List<String>) keywords).stream().map(t -> StringUtils.replace(t," ","_")).collect(Collectors.joining(" ")):
                                keywords.toString();
                        row.setTags(tags);

                        // Description
                        Object descriptions = (List<String>) item.get("description");
                        String description  = escaper.escape((descriptions instanceof JSONArray) ? ((List<LinkedHashMap<String, String>>) descriptions).stream().filter(p -> p.get("_lang").equalsIgnoreCase("es")).map(p -> p.get("_value")).collect(Collectors.toList()).get(0) : descriptions.toString());
                        row.setDescription(description);

                        // Location
                        Object spatial      = item.getOrDefault("spatial"," ");
                        String locations    = (spatial instanceof JSONArray)?
                                ((List<String>) spatial).stream().map(t -> StringUtils.replace(t," ","_")).map(t -> StringUtils.substringAfterLast(t,"/")).collect(Collectors.joining(" ")):
                                StringUtils.substringAfterLast(spatial.toString(),"/");
                        row.setLocations(locations);


                        // Creation Time
                        String creationTime     = new TimeExpression((String) item.get("issued")).getISO8601();
                        row.setCreationTime(creationTime);

                        // Modification Time
                        String modificationTime = new TimeExpression((String) item.get("modified")).getISO8601();
                        row.setModificationTime(modificationTime);

                        System.out.println(row.toCSV());
                        writer1.write(row.toCSV());


                    }

                    if (items.size() < itemsPerPage) finished = true;

//                    // Reference
//                    List<String> references     = documentContext.read("$.result.items[*]._about", validPredicate);
//
//                    // Title
//                    List titles                 = documentContext.read("$.result.items[*].title", validPredicate);
//
//                    // Tags
//                    List keywords         = documentContext.read("$.result.items[*].keyword", validPredicate);
//
//                    // Descriptions
//                    List<String> descriptions   = documentContext.read("$.result.items[*].description[?(@._lang=='es')]._value", validPredicate);


                }catch (PathNotFoundException e){
                    LOG.warn("Error parsing response: " + e.getMessage());
                }

                index += 1;

            }
            writer1.close();

        } catch (UnirestException e) {
            LOG.info("Current index: " + index);
            LOG.error("Error getting datasets from datos.gob.es",e);
        } catch (IOException e) {
            LOG.error("Error creating CSV file from datos.gob.es",e);
        }

    }
}
