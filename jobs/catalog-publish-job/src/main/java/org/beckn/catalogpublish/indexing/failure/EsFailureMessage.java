package org.beckn.catalogpublish.indexing.failure;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EsFailureMessage(
        @JsonAlias("itemId") String resourceId,
        String catalogId,
        String indexKey,
        String payload,
        String errorReason,
        Instant failedAt,
        int attempt,
        // Item.network_id at publish time — payload never carries networkId, so this must be
        // threaded through rather than re-derived from the payload on retry (see M-Discovr empty-catalog bug).
        List<String> networkIds) {

    public EsFailureMessage {
        networkIds = networkIds != null ? List.copyOf(networkIds) : List.of();
    }

    public EsFailureMessage withNextAttempt() {
        return new EsFailureMessage(resourceId, catalogId, indexKey, payload, errorReason, Instant.now(), attempt + 1, networkIds);
    }
}
