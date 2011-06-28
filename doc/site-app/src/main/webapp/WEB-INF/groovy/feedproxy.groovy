import static java.util.Calendar.MINUTE
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
	
	def fetchFeed = {
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
	}
	
	fetchFeed()
	
	// Twitter sometimes returns this, when just making another request will make it work, so try again
	if (feedName == "twitter" && feed.text.contains("<error>Rate limit exceeded.")) {
		log.warning "twitter gave bogus rate limit error, trying again"
		fetchFeed()
		
		if (feed.text.contains("<error>Rate limit exceeded.")) {
			log.warning "gave the bogus rate limit error again, giving up"
			return response.sendError(500, "failed to get twitter feed")
		}
	}
	
	memcache.put(cacheKey, feed, Expiration.byDeltaSeconds(1800), SetPolicy.ADD_ONLY_IF_NOT_PRESENT)
}

def expires = feed.createdAt.toCalendar()
expires.add(MINUTE, 30)
response.addDateHeader("Expires", expires.timeInMillis)

response.setContentType(feed.contentType)
if (feed.etag) {
	response.addHeader("Etag", feed.etag)
}

out << feed.text
out.flush()
out.close()