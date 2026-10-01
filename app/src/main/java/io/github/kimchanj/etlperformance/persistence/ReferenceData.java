package io.github.kimchanj.etlperformance.persistence;

public record ReferenceData(long id, String code, String activeFlag) {
    public boolean active() {
        return "Y".equals(activeFlag);
    }
}
