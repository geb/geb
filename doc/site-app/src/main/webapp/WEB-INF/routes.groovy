def cacheFor = 12.hours

all "/manual/snapshot/**", ignore: true

all "/manual/**", ignore: true, cache: cacheFor
all "/favicon.ico", ignore: true, cache: cacheFor
all "/robots.txt", ignore: true, cache: cacheFor
all "/images/**", ignore: true, cache: cacheFor
all "/css/**", ignore: true, cache: cacheFor
all "/js/**", ignore: true, cache: cacheFor

all "/clearcaches", forward: "clearcaches.groovy"
get "/feed/@feed", forward: "/feedproxy.groovy?feed=@feed"
all "/@page", forward: "site.groovy?page=@page", cache: cacheFor
all "/", forward: "site.groovy?page=index", cache: cacheFor