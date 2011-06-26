import static java.util.Calendar.HOUR
import com.google.appengine.api.memcache.Expiration
import com.google.appengine.api.memcache.MemcacheService.SetPolicy

def feeds = [
	twitter: "http://twitter.com/favorites/GroovyGeb.atom",
	articles: "http://groups.diigo.com/group/geb-resources/rss"
]

def feedName = params.feed

if (!feeds.containsKey(feedName)) {
	return response.sendError(404) 
}

def cacheKey = "feed:$feedName".toString()
def feed = memcache[cacheKey]

if (feed) {
	log.info "feed '$feedName': cached, reusing"
} else {
	log.info "feed '$feedName': not cached, fetching"
	feed = [
		text: "",
		etag: "",
		contentType: "",
		createdAt: new Date()
	]

	def url = new URL(feeds[feedName])

	url.openConnection().with {
		readTimeout = 10 * 1000
		connectTimeout = 10 * 1000

		feed.text = getContent().getText("UTF-8")
		feed.etag = getHeaderField("etag")
		feed.contentType = getContentType()
	}

	
	memcache.put(cacheKey, feed, Expiration.byDeltaSeconds(3600), SetPolicy.ADD_ONLY_IF_NOT_PRESENT)
}

def expires = feed.createdAt.toCalendar()
expires.add(HOUR, 1)
response.addDateHeader("Expires", expires.timeInMillis)

response.setContentType(feed.contentType)
if (feed.etag) {
	response.addHeader("Etag", feed.etag)
}

out << feed.text
out.flush()
out.close()