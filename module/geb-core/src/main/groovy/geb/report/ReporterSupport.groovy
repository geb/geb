/* Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package geb.report

/**
 * Common support for reporter implemenations.
 */
abstract class ReporterSupport implements Reporter {
	
	private dir
	
	/**
	 * Configures the dir for the reporter, optionally emptying it.
	 * 
	 * @param dir the directory to write reports to
	 * @param doClean whether or not to empty the directory
	 */
	ReporterSupport(File dir, boolean doClean = false) {
		this.dir = dir
		if (doClean && dir.exists()) {
			if (!dir.deleteDir()) {
				throw new IllegalStateException("Could not clean report dir '${dir}'")
			}
		}
		
		if (!dir.exists() && !dir.mkdirs()) {
			throw new IllegalStateException("Could not create report dir '${dir}'")
		}
	}
	
	/**
	 * Gets a file reference for the object with the given name and extension within the dir.
	 */
	protected getFile(String name, String extension) {
		new File(getReportDir(), "${escapeFileName(name)}.${escapeFileName(extension)}")
	}
	
	/**
	 * Replaces all non word chars with underscores to avoid using reserved characters in file paths
	 */
	protected escapeFileName(String name) {
		name.replaceAll("\\W", "_")
	}
	
	/**
	 * The directory that this reporter writes to
	 */
	protected getReportDir() {
		dir
	}

	/**
	 * Given a file at path {@code /some/dir} and class {@code java.lang.String} will return
	 * a file for path {@code /some/dir/java/lang/String}.
	 */
	static getDirForClass(File dir, Class clazz) {
		new File(dir, clazz.name.replace('.', '/'))
	}
}