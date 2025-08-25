package com.packt.accounting.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Earnings {
    private Long id;
    private Long contentManagerId;
    private int totalStreams;
    private int totalDownloads;
    private BigDecimal totalAmount;   
    private LocalDateTime lastCalculated;

    public Earnings() {}

    public Earnings(Long id, Long contentManagerId, int totalStreams, int totalDownloads, BigDecimal totalAmount, LocalDateTime lastCalculated) {
        this.id = id;
        this.contentManagerId = contentManagerId;
        this.totalStreams = totalStreams;
        this.totalDownloads = totalDownloads;
        this.totalAmount = totalAmount;
        this.lastCalculated = lastCalculated;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getContentManagerId() {
        return contentManagerId;
    }
    public void setContentManagerId(Long contentManagerId) {
        this.contentManagerId = contentManagerId;
    }

    public int getTotalStreams() {
        return totalStreams;
    }
    public void setTotalStreams(int totalStreams) {
        this.totalStreams = totalStreams;
    }

    public int getTotalDownloads() {
        return totalDownloads;
    }
    public void setTotalDownloads(int totalDownloads) {
        this.totalDownloads = totalDownloads;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getLastCalculated() {
        return lastCalculated;
    }
    public void setLastCalculated(LocalDateTime lastCalculated) {
        this.lastCalculated = lastCalculated;
    }
}
