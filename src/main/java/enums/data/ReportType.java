package main.java.enums.data;

public enum ReportType {
    RECORDS_REPORT("Records report"),
    SALES_REPORT("Sales report");

    private final String reportType;

    ReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getReportType() {
        return reportType;
    }

    public static ReportType getReportTypeByValue(String value) {
        for (ReportType rt : ReportType.values()) {
            if (rt.reportType.equalsIgnoreCase(value)) {
                return rt;
            }
        }
        return null;
    }
}
