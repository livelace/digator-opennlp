package ru.livelace.digator_opennlp;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.json.Json;
import javax.json.JsonObject;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;

@ApplicationScoped
@Default
public class NerModel {
    @ConfigProperty(name = "app.models.path")
    String modelsPath;

    private static final String ERROR = "error";
    private final Logger logger;
    private final HashMap<String, Model> models;

    public NerModel() {
        this.logger = org.slf4j.LoggerFactory.getLogger(NerModel.class);
        this.models = new HashMap<>();
    }

    /**
     * Format OpenNLP spans into Label Studio format.
     * @param tokens
     * @param spans
     * @return
     */
    private JsonObject formatToLabelStudio(String text, String[] tokens, Span[] spans) {
        var result = Json.createObjectBuilder();
        var items = Json.createArrayBuilder();

        // Form "item" with "value".
        for (Span span: spans) {
            var item = Json.createObjectBuilder();
            item.add("from_name", "label");
            item.add("to_name", "text");
            item.add("type", "labels");

            // Calc margins.
            var beforeSpan = new StringBuilder();
            var spanText = new StringBuilder();

            for (int i=0; i < span.getStart(); i++) {
                var tmp = beforeSpan.toString() + String.format("%s ", tokens[i]);

                if (text.contains(tmp)) {
                    beforeSpan.append(String.format("%s ", tokens[i]));
                } else {
                    beforeSpan.append(String.format("%s", tokens[i]));
                }
            }

            var lastCharWasSpace = 0;
            for (int i=span.getStart(); i < span.getEnd(); i++) {
                var tmp = spanText.toString() + String.format("%s ", tokens[i]);

                if (text.contains(tmp)) {
                    spanText.append(String.format("%s ", tokens[i]));
                    lastCharWasSpace = 1;
                } else {
                    spanText.append(String.format("%s", tokens[i]));
                    lastCharWasSpace = 0;
                }
            }

            var from = beforeSpan.toString().length();
            var to = from + spanText.toString().length() - lastCharWasSpace;

            logger.debug("TYPE: {}, SPAN: {}, BEFORE SPAN: {}, RANGE: {}:{}",
                    span.getType(), spanText, beforeSpan, from, to);

            // Set label for item (PER, LOC, FAC etc.).
            var labels = Json.createArrayBuilder();
            labels.add(span.getType());

            // Assembly all together.
            var value = Json.createObjectBuilder();

            value.add("start", from);
            value.add("end", to);
            value.add("text", spanText.toString().trim());
            value.add("labels", labels);

            item.add("value", value);

            // Add item to array.
            items.add(item);
        }

        result.add("result", items);
        result.add("score", 1);

        return result.build();
    }

    /**
     * Format OpenNLP spans into OpenNLP train format.
     * @param tokens
     * @param spans
     * @return
     */
    private static String formatToOpennlp(String[] tokens, Span[] spans) {
        for (Span span: spans) {
            tokens[span.getStart()] = String.format("<START:%s> %s", span.getType(), tokens[span.getStart()]);
            tokens[span.getEnd()-1] = String.format("%s <END>", tokens[span.getEnd()-1]);
        }
        return String.join(" ", tokens);
    }

    /**
     * Initialize model and label data with OpenNLP NER tags.
     * @param dataset
     * @param lang
     * @param type
     * @param format
     * @param data
     * @return
     */
    public JsonObject label(String dataset, String lang, String type, String format, JsonObject data) {
        var json = Json.createObjectBuilder();
        var modelSignature = String.format("%s-%s-%s", dataset, lang, type);

        // Set default format.
        if (format == null) format = "opennlp";

        // Initialize model.
        if (models.get(modelSignature) == null) {
            try {
                models.put(modelSignature, new Model(dataset, lang, type));
            } catch (Exception e) {
                logger.error("cannot initialize model: {}", e.getMessage());
                json.add(ERROR, e.getMessage());

                return json.build();
            }
        }

        // Check input data availability.
        var text = "";
        try {
            text = data.getString("text");
        } catch (NullPointerException e) {
            return Json.createObjectBuilder().add(ERROR, "no data").build();
        }

        // Tokenize and label data.
        String[] tokens = models.get(modelSignature).getTokens(text);
        String tokensString = Arrays.toString(tokens);

        logger.debug("ORIGINAL: {}", text);
        logger.debug("TOKENS: {}", tokensString);

        var spans = models.get(modelSignature).getLabel(tokens);

        // Return labeled data in different formats.
        if (format.equals("label-studio")) {
            return formatToLabelStudio(text, tokens, spans);

        } else if (format.equals("opennlp")) {
            return json.add("text", formatToOpennlp(tokens, spans)).build();

        } else {
            logger.error("unknown format: {}", format);
            return json.add(ERROR, "unknown format: " + format).build();
        }
    }

    /**
     * Get information about OpenNLP model.
     * @param dataset
     * @param lang
     * @param type
     * @return
     */
    public JsonObject stat(String dataset, String lang, String type) {
        String msg = String.format("not implemented. todo: add model stat to ci: %s, %s, %s", dataset, lang, type);

        return Json.createObjectBuilder().add("info", msg).build();
    }

    /**
     *
     */
    private class Model {
        private final TokenNameFinderModel opennlpModel;
        private final SimpleTokenizer opennlpTokenizer;

        public Model(String dataset, String lang, String type) throws IOException {
            var modelFile = String.format("%s/%s/%s/%s.bin", modelsPath, dataset, lang, type);
            var modelIn = new FileInputStream(modelFile);
            opennlpModel = new TokenNameFinderModel(modelIn);
            opennlpTokenizer = SimpleTokenizer.INSTANCE;
        }

        public Span[] getLabel(String[] tokens) {
            Span[] spans = new Span[0];
            try {
                var nameFinder = new NameFinderME(opennlpModel);
                spans = nameFinder.find(tokens);
                nameFinder.clearAdaptiveData();
            } catch (Exception e) {
                logger.error("cannot extract spans: {}", e.getMessage());
            }
            
            return spans;
        }

        public String[] getTokens(String text) {
            return opennlpTokenizer.tokenize(text);
        }
    }
}
