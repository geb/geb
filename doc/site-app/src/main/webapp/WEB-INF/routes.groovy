
all "/manual/**", ignore: true, cache: 12.hours
all "/manual/snapshot/**", ignore: true, cache: 10.minutes
all "/favicon.ico", ignore: true
all "/robots.txt", ignore: true
all "/images/**", ignore: true
all "/css/**", ignore: true
all "/js/**", ignore: true


get "/feed/@feed", forward: "/feedproxy.groovy?feed=@feed", cache: 1.hours
all "/@page", forward: "site.groovy?page=@page", cache: 12.hours
all "/", forward: "site.groovy?page=index", cache: 12.hours
