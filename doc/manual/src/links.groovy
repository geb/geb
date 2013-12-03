import java.util.regex.Matcher
import java.util.regex.Pattern

/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

def processFile(File file) {
	List documentLines = file.readLines()

	new File('references.markdown').eachLine { line ->
		def splitIndex = line.indexOf(':')
		def split = [line.subSequence(0, splitIndex), line.subSequence(splitIndex, line.length())]
		String link = split[1][2..-1]
		link = link.startsWith('http:') ? link : "link:$link"
		String reference = split[0].replace('-', "\\-").replace('[', "\\[").replace(']', "\\]")
		String stimpy = split[0]

		documentLines = documentLines.collect { documentLine ->
			String[] chunks = documentLine.split(reference)
			while (chunks.size() > 1) {
				String chunk = chunks[0]
				int index = chunk.lastIndexOf('[')
				if (index != -1) {
					chunk = chunk[0..(index - 1)] + link + chunk.substring(index)
					documentLine = chunk + chunks[1..-1].join(stimpy)
					chunks = documentLine.split(reference)
				} else {
					chunks = [chunk + stimpy + chunks[1..-1].join(stimpy)]
				}
			}
			chunks.join('')
		}
	}
	file.text = documentLines.join('\n')
	file.renameTo(new File(file.parentFile, file.name.replace('.md', '.adoc')))
}

dir = new File('asciidoc')
dir.eachFileMatch({ it.endsWith('.md') }) { file ->
	processFile(file)
}