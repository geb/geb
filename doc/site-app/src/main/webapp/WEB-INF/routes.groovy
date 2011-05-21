
all "/manual/**", ignore: true
all "/favicon.ico", ignore: true
all "/images/**", ignore: true
all "/css/**", ignore: true
all "/js/**", ignore: true

all "/@page", forward: "site.groovy?page=@page"
all "/", forward: "site.groovy?page=index"
