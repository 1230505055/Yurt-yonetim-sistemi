package model;

import pattern.*;
import java.time.LocalDate;

public class LeaveRequest {
    private int dbId;
    private int studentId;
    private String studentName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;

    // İzin durumunu yöneten State nesnesi (State Pattern)
    private RequestState state;

    public LeaveRequest(int studentId, LocalDate startDate, LocalDate endDate) {
        this.studentId = studentId;
        this.startDate = startDate;
        this.endDate = endDate;
        // Varsayılan durum: Beklemede
        this.state = new PendingState();
    }

    public int getDbId() {
        return dbId;
    }

    public void setDbId(int dbId) {
        this.dbId = dbId;
    }

    public int getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    // Admin/Personel paneli için State nesnesini döndürür
    public RequestState getState() {
        return state;
    }

    // Öğrenci paneli ve tablo gösterimi için durumu String olarak döndürür
    public String getStatus() {
        if (state instanceof ApprovedState) {
            return "Onaylandı";
        } else if (state instanceof RejectedState) {
            return "Reddedildi";
        } else {
            return "Beklemede";
        }
    }

    // Veritabanından gelen metni State nesnesine çevirir
    public void setStatusByString(String statusStr) {
        if (statusStr == null) {
            this.state = new PendingState();
            return;
        }

        if (statusStr.equalsIgnoreCase("Onaylandı") || statusStr.equalsIgnoreCase("Approved")) {
            this.state = new ApprovedState();
        } else if (statusStr.equalsIgnoreCase("Reddedildi") || statusStr.equalsIgnoreCase("Rejected")) {
            this.state = new RejectedState();
        } else {
            this.state = new PendingState();
        }
    }

    @Override
    public String toString() {
        return studentName + " (" + startDate + " - " + endDate + ") [" + getStatus() + "]";
    }
}