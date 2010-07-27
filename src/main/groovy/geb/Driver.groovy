package geb

import geb.error.DriveException
import com.gargoylesoftware.htmlunit.WebClient
import be.roam.hue.doj.Doj

class Driver {

	Page page
	final WebClient client
	
	String baseUrl
	
	Driver(Class pageClass, Map params = null) {
		this(null, null, pageClass, params)
	}
	
	Driver(String baseUrl, Class pageClass = null, Map params = null) {
		this(null, baseUrl, pageClass)
	}
	
	Driver(WebClient client = null, String baseUrl = null, Class pageClass = null, Map params = null) {
		this.client = client ?: new WebClient()
		this.baseUrl = baseUrl
		
		params = params == null ? [:] : params
		
		if (pageClass) {
			to(pageClass, *:params)
		} else {
			page Page
		}
	}
	
	def methodMissing(String name, args) {
		page."$name"(*args)
	}

	def propertyMissing(String name) {
		page."$name"
	}
	
	def propertyMissing(String name, value) {
		page."$name" = value
	}	
	
	void page(Class pageClass) {
		page(createPage(pageClass))
	}

	void page(Page page) {
		this.page = page
		page.driver = this
	}
	
	boolean at(Class pageClass) {
		if (!page) {
			page(pageClass)
		}
		page.verifyAt() && page.class == pageClass
	}

	def go() {
		go([:], null)
	}
	
	def go(Map params) {
		go(params, null)
	}
	
	def go(String url) {
		go([:], url)
	}
	
	def go(Map params, String url) {
		def newUrl = _calculateUri(url, params)
		def newPage = client.getPage(newUrl)
		if (!page) {
			page(Page)
		}
	}
	
	def to(Class pageClass, Object[] args) {
		to([:], pageClass, *args)
	}

	def to(Map params, Class pageClass) {
		to(params, pageClass, null)
	}

	def to(Map params, Class pageClass, Object[] args) {
		page(pageClass)
		page.to(params, *args)
	}
	
	protected _calculateUri(String path, Map params) {
		def uri = new URI(path)
		if (!uri.absolute) {
			uri = new URI(baseUrl).resolve(uri)
		}
		
		def queryString = _toQueryString(params)
		if (queryString) {
			def joiner = uri.query ? '&' : '?'
			new URL(uri.toString() + joiner + queryString)
		} else {
			uri.toURL()
		}
	}
	
	Page createPage(Class pageClass) {
		if (!Page.isAssignableFrom(pageClass)) {
			throw new IllegalArgumentException("$pageClass is not a subclass of ${Page}")
		}
		pageClass.newInstance(driver: this)
	}
	
	protected _toQueryString(Map params) {
		if (params) {
			params.collect { name, value ->
				def values = value instanceof Collection ? value : [value]
				values.collect { v ->
					"${URLEncoder.encode(name.toString(), "UTF-8")}=${URLEncoder.encode(v.toString(), "UTF-8")}" 
				}
			}.flatten().join("&")
		} else {
			""
		}
	}
	
	static drive(Closure script) {
		doDrive(new Driver(), script)
	}
	
	static drive(Class pageClass, Closure script) {
		doDrive(new Driver(pageClass), script)
	}
	
	static drive(String baseUrl, Closure script) {
		def driver = new Driver(baseUrl)
		driver.go("")
		doDrive(driver, script)
	}
	
	static drive(String baseUrl, Class pageClass, Closure script) {
		doDrive(new Driver(baseUrl, pageClass), script)
	}
	
	static drive(WebClient client, Closure script) {
		doDrive(new Driver(client), script)
	}
	
	static drive(WebClient client, Class pageClass, Closure script) {
		doDrive(new Driver(client, pageClass), script)
	}
	
	private static doDrive(Driver driver, Closure script) {
		script.delegate = driver
		try {
			script()
		} catch (Throwable e) {
			throw new DriveException(driver, e)
		}
	}
}