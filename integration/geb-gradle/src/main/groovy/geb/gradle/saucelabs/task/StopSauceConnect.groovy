package geb.gradle.saucelabs.task

import geb.gradle.saucelabs.SauceConnect
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class StopSauceConnect extends DefaultTask {

	SauceConnect sauceConnect

	@TaskAction
	void stop() {
		sauceConnect.stopTunnel()
	}
}
