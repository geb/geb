request['pages'] = [
	"Highlights": [
		crossbrowser: "Cross Browser",
		content: "jQuery-like API",
		pages: "Page Objects",
		async: "Asynchronous Pages",
		testing: "Testing",
		integration: "Build Integration"
	],
	"Talk": [
		twitter: "Twitter about Geb",
		articles: "Articles about Geb"
	]

]

def manualPropertiesStream = context.getResourceAsStream("/WEB-INF/manual.properties")
if (manualPropertiesStream == null) {
	return binding.response.sendError(500, "no manual properties found")
}

def manualProperties = new Properties()
manualProperties.load(manualPropertiesStream)

if (!manualProperties.containsKey('old')) {
	return binding.response.sendError(500, "no old manual list found")
}

request.oldManuals = manualProperties.old.tokenize(',')
request.currentManual = manualProperties.current
request.snapshotManual = manualProperties.snapshot

log.info "request for $params.page"

forward "site.gtpl"