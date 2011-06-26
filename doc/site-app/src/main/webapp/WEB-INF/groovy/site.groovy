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

log.info "request for $params.page"

forward "site.gtpl"