import static java.util.Calendar.HOUR

feeds = [
	twitter: "http://twitter.com/favorites/GroovyGeb.atom",
	articles: "http://groups.diigo.com/group/geb-resources/rss"
]

if (!feeds.containsKey(params.feed)) {
	return response.sendError(404) 
}

def feedText
def ct
def etag

def url = new URL(feeds[params.feed])

url.openConnection().with {
	readTimeout = 10 * 1000
	connectTimeout = 10 * 1000
	def content = getContent()
	feedText = content.getText("UTF-8")
	etag = getHeaderField("etag")
	ct = getContentType()
}

def expires = new Date().toCalendar()
expires.add(HOUR, 1)
response.addDateHeader("Expires", expires.timeInMillis)
response.setContentType(ct)
//response.addHeader("Etag", etag)

out << feedText
out.flush()
out.close()