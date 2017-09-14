package org.librairy.harvester.datosgobes.rest;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.jayway.jsonpath.Predicate;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class DatosGobEsRestClient {

    private static final Logger LOG = LoggerFactory.getLogger(DatosGobEsRestClient.class);

    private String baseUrl = "http://datos.gob.es/apidata/catalog";

    public DatosGobEsRestClient(String service){

        if (Strings.isNullOrEmpty(service)) {
            LOG.info("No service set to rest client. Base URL: " + baseUrl );
        }else{
            this.baseUrl += "/"+service;
        }

    }


    public String get(Map<String,Object> parameters) throws UnirestException {

        HttpResponse<String> response = Unirest.get(baseUrl)
                .headers(
                        ImmutableMap.of(
                                "Accept","application/json"))
                .queryString(parameters)
                .asString();

        if (response.getStatus() != 200) throw new RuntimeException("Http error: " + response.getStatus() + " in " +
                "url: " + baseUrl + " by query parameters: '" + parameters+ "'");

        return response.getBody();
    }

}
