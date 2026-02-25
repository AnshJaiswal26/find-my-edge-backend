package com.example.find_my_edge.workspace.converter;

import com.example.find_my_edge.workspace.model.WorkspaceData;
import com.example.find_my_edge.workspace.exception.WorkspaceSerializationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class WorkspaceDataConverter implements AttributeConverter<WorkspaceData, String> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(WorkspaceData attribute) {
        try {
            return mapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new WorkspaceSerializationException("Failed to serialize WorkspaceDTO", e);
        }
    }

    @Override
    public WorkspaceData convertToEntityAttribute(String dbData) {
        try {
            return mapper.readValue(dbData, WorkspaceData.class);
        } catch (Exception e) {
            throw new WorkspaceSerializationException("Failed to deserialize Workspace JSON", e);
        }
    }
}