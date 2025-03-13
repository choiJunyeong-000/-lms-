package com.lms.project.LMS.glances;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/metrics-view")
public class MetricsController {

    @Autowired
    private GlancesService glancesService;

    @GetMapping(produces = "application/json")
    public ResponseEntity<String> getMetricsView() {
        try {
            JSONObject metricsData = glancesService.getMetrics();

            // 접속자 수를 가져와서 metricsData에 추가
            int activeConnections = glancesService.getActiveConnections();
            metricsData.put("activeConnections", activeConnections);

            return ResponseEntity.ok(metricsData.toString());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("{\"error\":\"Failed to fetch metrics: " + e.getMessage() + "\"}");
        }
    }
    
    // 경로 변경: /metrics-views
    @GetMapping(value = "/views", produces = "application/json")
    public ResponseEntity<String> getMetricsViews() {
        try {
            JSONObject metricsData = glancesService.getMetricses();
            return ResponseEntity.ok(metricsData.toString());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("{\"error\":\"Failed to fetch metrics: " + e.getMessage() + "\"}");
        }
    }
}
