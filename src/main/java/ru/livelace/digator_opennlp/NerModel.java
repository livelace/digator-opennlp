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
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.Span;

@ApplicationScoped
@Default
public class NerModel extends BaseModel {
    private static final int MAX_SPACES = 10;
    private final HashMap<String, Model> models;

    public NerModel() {
        this.logger = org.slf4j.LoggerFactory.getLogger(NerModel.class);
        this.models = new HashMap<>();
    }

    private String appendSpacesUntilMatch(String text, String textBefore, String token) {
        var tmp = new StringBuilder(textBefore);

        for (int i=0; i < MAX_SPACES; i++) {
            if (text.contains(tmp + token)) return tmp.append(token).toString();
            tmp.append(" ");
        }

        return "";
    }

    private String extractTextByOffset(String text, String[] tokens, int start, int end) {
        var extractedText = new StringBuilder();

        for (int i=start; i < end; i++) {
            if (text.contains(extractedText + tokens[i])) {
                extractedText.append(tokens[i]);

            } else {
                var matched = appendSpacesUntilMatch(text, extractedText.toString(), tokens[i]);

                if (matched.isEmpty()) {
                    return "";

                } else {
                    extractedText = new StringBuilder(matched);
                }
            }
        }

        return extractedText.toString();
    }

    private JsonObject formatToLabelArray(String text, String[] tokens, Span[] spans) {
        var result = Json.createObjectBuilder();
        var items = Json.createArrayBuilder();

        for (Span span: spans) {
            var item = Json.createObjectBuilder();

            var spanText = extractTextByOffset(text, tokens, span.getStart(), span.getEnd());

            item.add(span.getType(), spanText);

            items.add(item);
        }

        result.add("result", items);

        return result.build();
    }

    /**
     * Format OpenNLP spans into Label Studio format.
     * @param tokens
     * @param spans
     * @return
     */
    private JsonObject formatToLabelStudio(String text, String[] tokens, Span[] spans) {
        /*
        The main problem here is the tokenization. Example:
        Original text: США<SPACE><SPACE>и страны ЕС пытаются законодательно закрепить антироссийские санкции, поэтому они могут продлиться неопределенно долго.
        Tokenized text: [США, и, страны, ЕС, пытаются, законодательно, закрепить, антироссийские, санкции, ,, поэтому, они, могут, продлиться, неопределенно, долго, .]
        We need these spaces for LabelStudio start-end ranges, otherwise LabelStudio outline entities incorrectly.
         */

        var result = Json.createObjectBuilder();
        var items = Json.createArrayBuilder();

        // Form "item" with "value".
        for (Span span: spans) {
            var item = Json.createObjectBuilder();
            item.add("from_name", "label");
            item.add("to_name", "text");
            item.add("type", "labels");

            // Extract text before the span.
            var textBefore = extractTextByOffset(text, tokens, 0, span.getStart());

            // Extract span body itself.
            var spanText = extractTextByOffset(text, tokens, span.getStart(), span.getEnd());

            // Extend textBefore if there is a space at the end, it needs for proper offsets.
            var textBeforeEndWithSpace = text.substring(0, textBefore.length() + 1);
            if (textBeforeEndWithSpace.endsWith(" ")) textBefore = textBeforeEndWithSpace;

            var from = textBefore.length();
            var to = from + spanText.length();

            // Exclude "," and "." at the end of persons.
            if (span.getType().startsWith("PER") && (spanText.endsWith(",") || spanText.endsWith("."))) {
                spanText = spanText.substring(0, spanText.length() - 1);
                to -= 1;
            }

            logger.debug("span info: type: {}, start: {}, end: {}, range: {}:{}, text before: \"{}\", text: \"{}\"",
                    span.getType(), span.getStart(), span.getEnd(), from, to, textBefore, spanText);

            // Assembly result.
            var labels = Json.createArrayBuilder();
            labels.add(span.getType());

            var value = Json.createObjectBuilder();

            value.add("start", from);
            value.add("end", to);
            value.add("text", spanText);
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
    private JsonObject formatToOpennlp(String[] tokens, Span[] spans) {
        var result = Json.createObjectBuilder();
        var item = Json.createObjectBuilder();

        for (Span span: spans) {
            tokens[span.getStart()] = String.format("<START:%s> %s", span.getType(), tokens[span.getStart()]);
            var lastToken = tokens[span.getEnd()-1];

            // Exclude "," and "." from the end of persons.
            if (span.getType().startsWith("PER") && (lastToken.endsWith(",") || lastToken.endsWith("."))) {
                tokens[span.getEnd()-1] = String.format("%s <END>%s",
                        lastToken.substring(0, lastToken.length()-1), lastToken.charAt(lastToken.length()-1));

            } else {
                tokens[span.getEnd()-1] = String.format("%s <END>", lastToken);
            }
        }

        var labeledString = String.join(" ", tokens);

        logger.debug("labeled text: \"{}\"", labeledString);

        item.add("text", labeledString);

        result.add("result", item);

        return result.build();
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
                var model = new Model(dataset, lang, type);
                models.put(modelSignature, model);
                logger.debug("model has been loaded: {}/{}/{}/{}.bin, {}, {}",
                        modelsPath, dataset, lang, type, model.getVersion(), model.getLanguage());

            } catch (Exception e) {
                logger.error("cannot load model: {}", e.getMessage());
                json.add(ERROR, e.getMessage());

                return json.build();
            }
        }

        // Check if input data was provided.
        // Replace "&nbsp;" with space.
        var text = "";
        try {
            text = data.getString("text").replaceAll("\\xa0", " ");
        } catch (NullPointerException e) {
            return Json.createObjectBuilder().add(ERROR, "data not provided").build();
        }

        // Tokenize and label data.
        String[] tokens = models.get(modelSignature).getTokens(text);
        String tokensString = Arrays.toString(tokens);

        logger.debug("original text: {}", text);
        logger.debug("original text tokens: {}", tokensString);

        var spans = models.get(modelSignature).getLabel(tokens);

        // Return labeled data in different formats.
        if (format.equals("label-array")) {
            return formatToLabelArray(text, tokens, spans);

        } else if (format.equals("label-studio")) {
            return formatToLabelStudio(text, tokens, spans);

        }else if (format.equals("opennlp")) {
            return formatToOpennlp(tokens, spans);

        } else {
            logger.error("unknown format: {}", format);
            return json.add(ERROR, "unknown format: " + format).build();
        }
    }

    /**
     *
     */
    private class Model {
        private final TokenNameFinderModel opennlpModel;
        private final WhitespaceTokenizer opennlpTokenizer;

        public Model(String dataset, String lang, String type) throws IOException {
            var modelFile = String.format("%s/%s/%s/%s.bin", modelsPath, dataset, lang, type);
            var modelIn = new FileInputStream(modelFile);
            opennlpModel = new TokenNameFinderModel(modelIn);
            opennlpTokenizer = WhitespaceTokenizer.INSTANCE;
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

        public String getLanguage() {
            return opennlpModel.getLanguage();
        }

        public String[] getTokens(String text) {
            return opennlpTokenizer.tokenize(text);
        }

        public String getVersion() {
            return opennlpModel.getVersion().toString();
        }
    }
}
