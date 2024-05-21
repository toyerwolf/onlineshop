package com.example.springsecurity.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.IOException;

public class PageableDeserializer extends JsonDeserializer<Pageable> {

    @Override
    public Pageable deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        int page = (node.get("pageNumber") != null) ? node.get("pageNumber").asInt() : 0;
        int size = (node.get("pageSize") != null) ? node.get("pageSize").asInt() : 10;
        Sort sort = Sort.unsorted();

        JsonNode sortNode = node.get("sort");
        if (sortNode != null && sortNode.isObject() && sortNode.has("sorted") && sortNode.get("sorted").asBoolean()) {
            sort = Sort.by("id"); // Замените "id" на нужное поле
        }

        return PageRequest.of(page, size, sort);
    }
}