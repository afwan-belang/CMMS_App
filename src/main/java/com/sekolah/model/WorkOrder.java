package com.sekolah.model;

import java.sql.Timestamp;

public class WorkOrder {
    private int id;
    private String assetName;
    private String technicianName;
    private String issue;
    private String priority;
    private String status;
    private Timestamp createdAt;

    public WorkOrder(int id, String assetName, String technicianName, String issue, String priority, String status, Timestamp createdAt) {
        this.id = id;
        this.assetName = assetName;
        this.technicianName = technicianName;
        this.issue = issue;
        this.priority = priority;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public String getAssetName() { return assetName; }
    public String getTechnicianName() { return technicianName; }
    public String getIssue() { return issue; }
    public String getPriority() { return priority; }
    public String getStatus() { return status; }
    public Timestamp getCreatedAt() { return createdAt; }
}