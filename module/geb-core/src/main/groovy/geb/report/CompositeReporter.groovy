package geb.report

import geb.Browser

/**
 * Delegates to one or more other reporters.
 */
class CompositeReporter implements Reporter {

	private final List<Reporter> reporters

	CompositeReporter(Reporter... reporters) {
		this.reporters = reporters.toList()
	}

	@Override
	void writeReport(Browser browser, String label, File outputDir) {
		for (reporter in reporters) {
			reporter.writeReport(browser, label, outputDir)
		}
	}

}
