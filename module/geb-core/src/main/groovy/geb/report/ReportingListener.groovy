package geb.report

/**
 * Gets notified whenever a report is taken.
 *
 * Reporting listeners are notified by reporters whenever reports are taken. This can be used to output links
 * to report files which is sometimes utilized by continuous integration servers.
 * <p>
 * They can be registered via the config mechanism…
 * <pre>
 * reportingListener = new ReportingListener() {
 *   void onReport(Reporter reporter, ReportState reportState, List<File> reportFiles) {
 * 	   reportFiles.each {
 * 	     println "Report taken: $it.absolutePath"
 * 	   }
 *   }
 * }
 * </pre>
 */
public interface ReportingListener {

	/**
	 * Called when a report is taken.
	 *
	 * @param reporter The reporter instance that created the report.
	 * @param reportState Information about what was reported on.
	 * @param reportFiles The report files that the reporter created.
	 */
	void onReport(Reporter reporter, ReportState reportState, List<File> reportFiles)

}