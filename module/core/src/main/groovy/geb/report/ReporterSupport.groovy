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

abstract class ReporterSupport implements Reporter {

	static public final DEFAULT_DO_CLEAN_VALUE = false
	
	private dir
	
	ReporterSupport(File dir, boolean doClean = DEFAULT_DO_CLEAN_VALUE) {
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
	
	protected getFile(String name, String extension) {
		new File(getReportDir(), "${name}.$extension")
	}
	
	protected getReportDir() {
		dir
	}

	static getDirForClass(File dir, Class clazz) {
		new File(dir, clazz.name.replace('.', '/'))
	}
}