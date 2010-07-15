package geb

import geb.error.DriveException

class Driver {

	private Page page
	private Geb geb
	
	Driver() {
		this(new Geb(""))
	}
	
	Driver(Class pageClass) {
		this(pageClass.url, pageClass)
	}
	
	Driver(String baseUrl, Class pageClass = null) {
		this(new Geb(baseUrl ?: ""), pageClass)
		if (baseUrl) {
			get "/"
		}
	}
	
	Driver(Geb geb, Class pageClass = null) {
		this.geb = geb
		page(pageClass ?: Page)
		if (pageClass) {
			go(pageClass.url)
		}
	}
	
	def methodMissing(String name, args) {
		(page ?: geb)."$name"(*args)
	}

	def propertyMissing(String name) {
		(page ?: geb)."$name"
	}
	
	def propertyMissing(String name, value) {
		(page ?: geb)."$name" = value
	}	
	
	void page(Class pageClass) {
		page = ___createPage(pageClass)
	}

	boolean at(Class pageClass) {
		if (!page) {
			page(pageClass)
		}
		page.verifyAt() && page.class == pageClass
	}
	
	Geb getGeb() {
		geb
	}
	
	def go(String url) {
		to([:], url)
	}
	
	def go(Map params, String url) {
		geb.get(url ?: "/") {
			params.each { k,v ->
				delegate."$k" = v
			}
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
	
	protected ___createPage(Class pageClass) {
		if (!Page.isAssignableFrom(pageClass)) {
			throw new IllegalArgumentException("$pageClass is not a subclass of ${Page}")
		}
		pageClass.newInstance(driver: this)
	}
	
	static drive(Closure script) {
		doDrive(new Driver(), script)
	}
	
	static drive(Class pageClass, Closure script) {
		doDrive(new Driver(pageClass), script)
	}
	
	static drive(String baseUrl, Closure script) {
		doDrive(new Driver(baseUrl), script)
	}
	
	static drive(String baseUrl, Class pageClass, Closure script) {
		doDrive(new Driver(baseUrl, pageClass), script)
	}
	
	static drive(Geb geb, Closure script) {
		doDrive(new Driver(geb), script)
	}
	
	static drive(Geb geb, Class pageClass, Closure script) {
		doDrive(new Driver(geb, pageClass), script)
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