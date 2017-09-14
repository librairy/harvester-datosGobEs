package org.librairy.harvester.datosgobes.model;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class Row {

    private static final Logger LOG = LoggerFactory.getLogger(Row.class);

    String id;

    String name;

    String locations;

    String categories;

    String tags;

    String creationTime;

    String modificationTime;

    String description;

    public String getId() {
        return id;
    }

    public Row setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Row setName(String name) {
        this.name = name;
        return this;
    }

    public String getLocations() {
        return locations;
    }

    public Row setLocations(String locations) {
        this.locations = locations;
        return this;
    }

    public String getCategories() {
        return categories;
    }

    public Row setCategories(String categories) {
        this.categories = categories;
        return this;
    }

    public String getTags() {
        return tags;
    }

    public Row setTags(String tags) {
        this.tags = tags;
        return this;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public Row setCreationTime(String creationTime) {
        this.creationTime = creationTime;
        return this;
    }

    public String getModificationTime() {
        return modificationTime;
    }

    public Row setModificationTime(String modificationTime) {
        this.modificationTime = modificationTime;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Row setDescription(String description) {
        StringBuilder descriptionContent = new StringBuilder();
        if (!description.startsWith("\"")) descriptionContent.append("\"");
        descriptionContent.append(description);
        if (!description.endsWith("\"")) descriptionContent.append("\"");
        this.description = descriptionContent.toString();
        return this;
    }

    public String toCSV(){
        return new StringBuilder()
                .append(id).append(",")
                .append(name).append(",")
                .append(locations).append(",")
                .append(categories).append(",")
                .append(creationTime).append(",")
                .append(modificationTime).append(",")
                .append(tags).append(",")
                .append(description)
                .toString();

    }

    public static Row fromCSV(String line){
        Row row = new Row();
        try{
            StringTokenizer tokenizer = new StringTokenizer(StringUtils.replace(line,",,",", ,"));
            row.setId(tokenizer.nextToken(","));
            row.setName(tokenizer.nextToken(","));
            row.setLocations(tokenizer.nextToken(","));
            row.setCategories(tokenizer.nextToken(","));
            row.setCreationTime(tokenizer.nextToken(","));
            row.setModificationTime(tokenizer.nextToken(","));
            row.setTags(tokenizer.nextToken(","));
            row.setDescription(tokenizer.nextToken());
        }catch (NoSuchElementException e){
            LOG.error("Error parsing CVS line: \n " + line,e);
            throw e;
        }


        return row;
    }

    @Override
    public String toString() {
        return "Row{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", locations='" + locations + '\'' +
                ", categories='" + categories + '\'' +
                ", tags='" + tags + '\'' +
                ", creationTime='" + creationTime + '\'' +
                ", modificationTime='" + modificationTime + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public static void main(String[] args){

        String text = " http://datos.gob.es/catalogo/e00136201-1009prensa-extranjera-ii,1009|PRENSA EXTRANJERA  II ,,cultura-ocio sociedad-bienestar,20161024,20161024,Estudio_Cualitativo Prensa,\" Índices de frecuencia de noticias relativas a España aparecidas en la prensa extranjera.  Análisis temático de las noticias relativas a España.  Actitud ante los temas concernientes a España.\"";

        System.out.println(Row.fromCSV(text));


    }
}
