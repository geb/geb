# Advanced Page Navigation

Page classes can customise how they generate URLs when used in conjunction with the browser `to()` method. 

Consider the following example…

    import geb.*
    
    class ExamplePage extends Page {
        static url = "/example"
    }

    Browser.drive("http://myapp.com") {
        to ExamplePage
    }

This will result in a request being made to “`http://myapp.com/example`”. 

The `to()` method can also take arguments…

    Browser.drive("http://myapp.com") {
        to ExamplePage, 1, 2
    }

This will result in a request being made to “`http://myapp.com/example/1/2`”. This is because by default, any arguments passed to the `to()` method after the page class are converted to a URL path by calling `toString()` on each argument and joining them with “`/`”. 

However, this is extensible. You can specify how a set of arguments is converted to a URL path to be added to the page URL. This is done by overriding the `convertToPath()` method. The `geb.Page` implementation of this method looks like this…

    def convertToPath(Object[] args) {
        args ? args*.toString().join('/') : ""
    }

You can either overwrite this catch all method control path conversion for all invocations, or provide an overloaded version for a specific type signature. Consider the following…

    class Person {
        Long id
        String name
    }
    
    class PersonPage {
        static url = "/person"
        
        def convertToPath(Person person) {
            person.id.toString()
        }
    }
    
    def newPerson = new Person(id: 5, name: "Bruce")
    
    Browser.driver("http://myapp.com") {
        to PersonPage, newPerson
    }

This will result in a request to “`http://myapp.com/person/5`”.

## Named params

Any type of argument can be used with the `to()` method, **except** named parameters (i.e. a `Map`). Named parameters are **always** interpreted as query parameters. Using the classes from the above example…

    Browser.driver("http://myapp.com") {
        to PersonPage, newPerson, flag: true
    }

This will result in a request to “`http://myapp.com/person/5?flag=true`”. The query parameters are **not** sent to the `convertToPath()` method.
