package geb

class Driver {

	private Page page
	private Geb geb
	
	Driver(Geb geb, Class pageClass = null) {
		this.geb = geb
		if (pageClass) {
			page(pageClass)
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
	
	def to(Class pageClass, Object[] args) {
		to([:], pageClass, *args)
	}

	def to(Map params, Class pageClass) {
		to(params, pageClass, null)
	}

	def to(Map params, Class pageClass, Object[] args) {
		page(pageClass)
		page.to(params, pageClass, *args)
	}
	
	protected ___createPage(Class pageClass) {
		if (!Page.isAssignableFrom(pageClass)) {
			throw new IllegalArgumentException("$pageClass is not a subclass of ${Page}")
		}
		pageClass.newInstance(driver: this)
	}
}