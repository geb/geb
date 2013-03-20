package geb.report

public interface ReportingListener {

	void onReport(Reporter reporter, ReportState reportState, List<File> reportFiles)

}