package ru.livelace.digator_opennlp;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@ApplicationScoped
@Path("/sentence")
public class SentenceModelPath {
    private static final String PATH = "{dataset}/{lang}";

    @Inject
    SentenceModel model;

    @GET
    @Path(PATH)
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject stat(
            @PathParam("dataset") String dataset,
            @PathParam("lang") String lang) {

        return model.stat(dataset, lang, "");
    }

    @POST
    @Path(PATH)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject predict(
            @PathParam("dataset") String dataset,
            @PathParam("lang") String lang,
            JsonObject text) {

        return model.extract(dataset, lang, text);
    }
}