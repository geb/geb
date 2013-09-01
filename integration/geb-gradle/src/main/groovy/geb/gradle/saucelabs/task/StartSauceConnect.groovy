package geb.gradle.saucelabs.task

import geb.gradle.saucelabs.SauceConnect
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class StartSauceConnect extends DefaultTask {

	boolean inBackground = false
	File sauceConnectJar
	File workingDir
	SauceConnect sauceConnect

	@TaskAction
	void start() {
		sauceConnect.startTunnel(getSauceConnectJar(), workingDir, inBackground)
	}
}
