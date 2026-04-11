package it.vitalegi.cosucce.budget.exception;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ETagNotMatchedException extends RuntimeException {
    private final String expectedEtag;
    private final String actualETag;
    private final UUID entityId;
    private final String entityClass;

    public ETagNotMatchedException(String expectedEtag, String actualETag, UUID entityId, String entityClass) {
        super("Expected '" + expectedEtag + "', found '" + actualETag + "' on entity " + entityId + " " + entityClass);
        this.expectedEtag = expectedEtag;
        this.actualETag = actualETag;
        this.entityId = entityId;
        this.entityClass = entityClass;
    }
}
