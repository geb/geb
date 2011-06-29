import static java.util.Calendar.MINUTE
import com.google.appengine.api.memcache.Expiration
import com.google.appengine.api.memcache.MemcacheService.SetPolicy

import oauth.signpost.basic.*
import oauth.signpost.*

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
		if (feedName == "twitter") {
			
			def credentialsStream = context.getResourceAsStream("/WEB-INF/twitter-credentials.properties")
			if (credentialsStream == null) {
				return binding.response.sendError(500, "no twitter credentials found")
			}
			
			def credentials = new Properties()
			credentials.load(credentialsStream)
			
			for (credential in ["consumerKey", "consumerSecret", "accessToken", "secretToken"]) {
				if (!credentials.containsKey(credential)) {
					return binding.response.sendError(500, "no twitter credential '$credential'")
				}
			}
			
			def consumer = new DefaultOAuthConsumer(credentials.consumerKey, credentials.consumerSecret)
			consumer.setTokenWithSecret(credentials.accessToken, credentials.secretToken)
			consumer.sign(delegate)
		}

		readTimeout = 10 * 1000
		connectTimeout = 10 * 1000

		if (responseCode != 200) {
			return binding.response.sendError(responseCode, responseMessage ?: "error")
		}
		
		feed.text = getContent().getText("UTF-8")
		feed.etag = getHeaderField("etag")
		feed.contentType = getContentType()
	}
	
	memcache.put(cacheKey, feed, Expiration.byDeltaSeconds(600), SetPolicy.ADD_ONLY_IF_NOT_PRESENT)
}

def expires = feed.createdAt.toCalendar()
expires.add(MINUTE, 10)
response.addDateHeader("Expires", expires.timeInMillis)

response.setContentType(feed.contentType)
if (feed.etag) {
	response.addHeader("Etag", feed.etag)
}

out << feed.text
out.flush()