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
package geb.internal

import java.util.regex.Pattern

class TextMatchingSupport {

	Pattern startsWith(CharSequence str) {
		Pattern.compile(Pattern.quote(str) + ".*")
	} 
	
	Pattern contains(CharSequence str) {
		Pattern.compile(".*" + Pattern.quote(str) + ".*")
	}
	
	Pattern endsWith(CharSequence str) {
		Pattern.compile(".*" + Pattern.quote(str))
	}

	Pattern iStartsWith(CharSequence str) {
		Pattern.compile("(?i)" + Pattern.quote(str) + ".*")
	}

	Pattern iContains(CharSequence str) {
		Pattern.compile("(?i).*" + Pattern.quote(str) + ".*")
	}
	
	Pattern iEndsWith(CharSequence str) {
		Pattern.compile("(?i).*" + Pattern.quote(str))
	}

	Pattern containsWord(CharSequence str) {
		Pattern.compile("(\$|\\s)" + Pattern.quote(str) + "(^|\\s)")
	}
	
	Pattern iContainsWord(CharSequence str) {
		Pattern.compile("(?i)(\$|\\s)" + Pattern.quote(str) + "(^|\\s)")
	}

	Pattern startsWith(Pattern pattern) {
		Pattern.compile(pattern.pattern() + ".*")
	} 
	
	Pattern contains(Pattern pattern) {
		Pattern.compile(".*" + pattern.pattern() + ".*")
	}
	
	Pattern endsWith(Pattern pattern) {
		Pattern.compile(".*" + pattern.pattern())
	}

	Pattern iStartsWith(Pattern pattern) {
		Pattern.compile("(?i)" + pattern.pattern() + ".*")
	}

	Pattern iContains(Pattern pattern) {
		Pattern.compile("(?i).*" + pattern.pattern() + ".*")
	}
	
	Pattern iEndsWith(Pattern pattern) {
		Pattern.compile("(?i).*" + pattern.pattern())
	}

	Pattern containsWord(Pattern pattern) {
		Pattern.compile("(\$|\\s)" + pattern.pattern() + "(^|\\s)")
	}
	
	Pattern iContainsWord(Pattern pattern) {
		Pattern.compile("(?i)(\$|\\s)" + pattern.pattern() + "(^|\\s)")
	}

}