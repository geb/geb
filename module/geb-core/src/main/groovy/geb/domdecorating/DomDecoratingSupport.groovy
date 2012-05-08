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
package geb.domdecorating

/**
 * Designed to be used with the "Delegate" transform to allow
 * configurable processing of a page DOM after it has been loaded.
 */
class DomDecoratingSupport {
	
	private owner
	private def domDecoratingJsFiles = null
	
	/**
	 * Constructor which registers on load listener
	 */
	DomDecoratingSupport(owner) {
		this.owner = owner
		this.owner.registerOnLoadListener( new DomDecoratingPageOnLoadListener() )
	}
	
	void setDomDecoratingJsFile( String jsFile ) {
		domDecoratingJsFiles = [ jsFile ]
	}
	
	void setDomDecoratingJsFile( File jsFile ) {
		domDecoratingJsFiles = [ jsFile.path ]
	}
	
	void setDomDecoratingJsFiles( String[] jsFiles ) {
		domDecoratingJsFiles = jsFiles.toList()
	}
	
	void setDomDecoratingJsFiles( File[] jsFiles ) {
		domDecoratingJsFiles = jsFiles.toList()*.toString()
	}
	
	void setDomDecoratingJsFiles( List jsFiles ) {
		domDecoratingJsFiles = jsFiles*.toString()
	}
	
	def getDomDecoratingJsFiles() {
		if ( !(this.@domDecoratingJsFiles?.size()) ) {
			if ( getJsResourcePath( this.defaultRootPackageJsFile ) ) {
				this.@domDecoratingJsFile = [ this.defaultRootPackageJsFile ]
			} else if ( getJsResourcePath( this.defaultOwnerPackageJsFile ) ) {
				this.@domDecoratingJsFile = [ this.defaultOwnerPackageJsFile ]
			}
		}
		this.@domDecoratingJsFiles
	}
	
	private getDefaultRootPackageJsFile() {
		"/" + owner.getClass().name + ".js"
	}
	
	private getDefaultOwnerPackageJsFile() {
		owner.getClass().simpleName + ".js"
	}
	
	private def getJsResourcePath( String jsFile ) {
		owner.getClass().getResource( jsFile )?.path
	}
	
	/**
	 * <p>
	 * Executes a list of scripts on the page DOM
	 * 
	 * From Selenium's JavascriptExecutor:
	 * <p>
	 * Executes JavaScript in the context of the currently selected frame or window. The script fragment provided will be executed as the body of an anonymous function.
	 * <p>
	 * Within the script, use document to refer to the current document. Note that local variables will not be available once the script has finished executing, though global variables will persist.
	 * <p>
	 * If the script has a return value (i.e. if the script contains a return statement), then the following steps will be taken:
	 * <ul>
	 * <li>For an HTML element, this method returns a WebElement
	 * <li>For a decimal, a Double is returned
	 * <li>For a non-decimal number, a Long is returned
	 * <li>For a boolean, a Boolean is returned
	 * <li>For all other cases, a String is returned.
	 * <li>For an array, return a List<Object> with each object following the rules above. We support nested lists.
	 * <li>Unless the value is null or there is no return value, in which null is returned
	 * </ul>
	 * <p>
	 * @return A list with the return value for each script executed.
	 */
	def decorateDom() {
		def result = []
		for ( jsFile in getDomDecoratingJsFiles() ) {
			final String jsFilePath = getJsResourcePath( jsFile )
			result << js.exec( ( new File( jsFilePath ) ).text )
		}
		return result
	}
}
