package ru.livelace.digator_opennlp;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@ApplicationScoped
@Path("/ner")
public class NerModelPath {
    private static final String PATH = "{dataset}/{lang}/{type}";

    @Inject
    NerModel model;

    @GET
    @Path(PATH)
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject stat(
            @PathParam("dataset") String dataset,
            @PathParam("lang") String lang,
            @PathParam("type") String type) {

        return model.stat(dataset, lang, type);
    }

    @POST
    @Path(PATH)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject predict(
            @PathParam("dataset") String dataset,
            @PathParam("lang") String lang,
            @PathParam("type") String type,
            @QueryParam("format") String format,
            JsonObject text) {

        return model.label(dataset, lang, type, format, text);
    }
}