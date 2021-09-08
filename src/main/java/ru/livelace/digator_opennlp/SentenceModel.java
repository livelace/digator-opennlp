package ru.livelace.digator_opennlp;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.json.Json;
import javax.json.JsonObject;
import opennlp.tools.sentdetect.SentenceDetectorME;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;

@ApplicationScoped
@Default
public class SentenceModel {
    @ConfigProperty(name = "app.models.path")
    String modelsPath;

    final private Logger logger;
    final private HashMap<String, Model> models;

    public SentenceModel() {
        this.logger = org.slf4j.LoggerFactory.getLogger(SentenceModel.class);
        this.models = new HashMap<>();
    }

    /**
     * Initialize model and label data with OpenNLP NER tags.
     * @param dataset
     * @param lang
     * @param data
     * @return
     */
    public JsonObject extract(String dataset, String lang, JsonObject data) {
        var json = Json.createObjectBuilder();
        var jsonArray = Json.createArrayBuilder();
        var modelSignature = String.format("%s-%s", dataset, lang);

        // Initialize model.
        if (models.get(modelSignature) == null) {
            try {
                models.put(modelSignature, new Model(dataset, lang));
            } catch (Exception e) {
                logger.error("cannot initialize model: {}", e.getMessage());
                json.add("error", e.getMessage());

                return json.build();
            }
        }

        // Check input data availability.
        var text = "";
        try {
            text = data.getString("text");
        } catch (NullPointerException e) {
            return Json.createObjectBuilder().add("error", "no data").build();
        }

        var sentences = models.get(modelSignature).getSentences(text);
        for (String sentence: sentences) {
            jsonArray.add(Json.createObjectBuilder().add("sentence", sentence));
        }

        return json.add("sentences", jsonArray.build()).build();
    }

    /**
     * Get information about OpenNLP model.
     * @param dataset
     * @param lang
     * @return
     */
    public JsonObject stat(String dataset, String lang) {
        return Json.createObjectBuilder().add("info", "not implemented. todo: add model stat to ci").build();
    }

    /**
     *
     */
    private class Model {
        final private opennlp.tools.sentdetect.SentenceModel model;

        public Model(String dataset, String lang) throws IOException {
            var modelFile = String.format("%s/%s/%s/sentence.bin", modelsPath, dataset, lang);
            var modelIn = new FileInputStream(modelFile);
            model = new opennlp.tools.sentdetect.SentenceModel(modelIn);
        }

        public String[] getSentences(String text) {
            String[] sentences = new String[0];
            try {
                var sentenceDetector = new SentenceDetectorME(model);
                sentences = sentenceDetector.sentDetect(text);
            } catch (Exception e) {
                logger.error("cannot extract sentences: {}", e.getMessage());
            }
            
            return sentences;
        }

        public String getStat() {
            return "not implemented. todo: add model stat to ci";
        }
    }
}
