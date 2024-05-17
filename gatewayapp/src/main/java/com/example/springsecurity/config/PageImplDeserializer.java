//package com.example.springsecurity.config;
//
//import com.fasterxml.jackson.core.JsonParser;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.DeserializationContext;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//
//import java.io.IOException;
//import java.util.List;
//
//public class PageImplDeserializer<T>  extends StdDeserializer<PageImpl<T>> {
//
//    private Class<T> contentType;
//
//    public PageImplDeserializer(Class<T> contentType) {
//        super(PageImpl.class);
//        this.contentType = contentType;
//    }
//
//    @Override
//    public PageImpl<T> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
//        ObjectMapper mapper = (ObjectMapper) p.getCodec();
//        JsonNode root = mapper.readTree(p);
//
//        List<T> content = mapper.readValue(root.get("content").traverse(), new TypeReference<List<T>>() {});
//        int number = root.get("number").asInt();
//        int size = root.get("size").asInt();
//        int totalPages = root.get("totalPages").asInt();
//        int numberOfElements = root.get("numberOfElements").asInt();
//        boolean first = root.get("first").asBoolean();
//        boolean last = root.get("last").asBoolean();
//
//        Pageable pageable = mapper.treeToValue(root.get("pageable"), Pageable.class);
//
//        return new PageImpl<>(content, pageable, totalElements);
//    }
//}
//
