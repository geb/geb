# History

This page lists the high level changes between versions of Geb.

## 0.5

* Navigator objects now implement the Groovy truth (empty == false, non empty == true)
* Introduced “js” short notation
* Added “[easyb](easyb)” support (`geb-easyb` and Grails support)
* Page change listening support through `geb.PageChangeListener`
* `waitFor()` methods added, making dealing with dynamic pages easier
* Support for `alert()` and `confirm()` dialogs
* Added jQuery integration
* Reporting integration classes (e.g. GebReportingSpec) now save a screenshot if using the FirefoxDriver

## 0.4

**Initial Public Release**