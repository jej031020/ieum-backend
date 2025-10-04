// BioRequest.java
package com.ieum.backend.ml_service.dto;
import java.util.Map;

public class BioRequest {
    private Long userId;              // 컨트롤러에서 세팅
    private Long sessionId;
    private Map<String, Double> measures;

    public BioRequest() {}
    public BioRequest(Long sessionId, Map<String, Double> measures) {
        this.sessionId = sessionId; this.measures = measures;
    }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    public Map<String, Double> getMeasures() { return measures; }
    public void setMeasures(Map<String, Double> measures) { this.measures = measures; }
}