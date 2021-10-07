package ru.livelace.digator_opennlp;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class BaseModel {
    @ConfigProperty(name = "app.models.path")
    String modelsPath;

    protected static final String ERROR = "error";
    protected Logger logger;

    /**
     * Get information about OpenNLP model.
     * @param dataset
     * @param lang
     * @param type
     * @return
     */
    public JsonObject stat(String dataset, String lang, String type) {
        var modelStat = String.format("%s/%s/%s/%s.stat", modelsPath, dataset, lang, type);

        try {
            String[] meta = Files.readString(Path.of(modelStat)).split(",");
            return Json.createObjectBuilder().add("records", meta[0]).add("labels", meta[1]).build();

        } catch (IOException e) {
            logger.error("cannot get model stat: {}\n{}", modelStat, e.getMessage());
            return Json.createObjectBuilder().add(ERROR, e.getMessage()).build();
        }
    }
}
