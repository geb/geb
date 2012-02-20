<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <meta name="Robots" content="index,follow" />
  <script type="text/javascript">
    var _gaq = _gaq || [];
    _gaq.push(['_setAccount', 'UA-15031038-2']);
    _gaq.push(['_trackPageview']);

    (function() {
      var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
      ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
      var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
    })();
  </script>
  
  <link type="text/css" rel="stylesheet" href="css/shCore.css"></link>
  <link type="text/css" rel="stylesheet" href="css/shThemeEclipse.css"></link>
  <script language="javascript" src="js/jquery.js"></script>
  <script language="javascript" src="js/shCore.js"></script>
  <script language="javascript" src="js/shBrushGroovy.js"></script>
  <script type="text/javascript" charset="utf-8">
    SyntaxHighlighter.defaults['toolbar'] = false;
    SyntaxHighlighter.defaults['gutter'] = false;
    SyntaxHighlighter.all();
  </script>
    <link rel="stylesheet" href="css/main.css" type="text/css" />
  <title>Geb - Very Groovy Browser Automation</title>
</head>

<body>
  
  <div id="header-wrap">
    <div id="header-content"> 
    
      <h1 id="logo"><a href="/" title="">Geb <span class="orange" style="font-size: 50%; font-style: italic">(pronounced “jeb”)</span></a></h1>  
      <h2 id="slogan">very groovy browser automation… web testing, screen scraping and more</h2>
      
      <ul>
        <li>
          <a href="/manual/">Manual</a>
          <ul class="manuals-list">
            <li><a href="manual/current/">${request.currentManual} - current</a></li>
            <% request.oldManuals.each { %>
              <li><a href="manual/$it/">${it}</a></li>
            <% } %>
            <li><a href="manual/snapshot/">${request.snapshotManual}</a></li>
          </ul>
        </li>
        <li><a href="http://xircles.codehaus.org/projects/geb/lists">Mailing List</a></li>
        <li><a href="http://jira.codehaus.org/browse/GEB">Issues</a></li>
        <li><a href="http://github.com/geb/geb">Code</a></li>
      </ul>
  
    </div>
  </div>
        
  <div id="content-wrap">
    <div id="content">
      <div id="main">
        <% include "/WEB-INF/includes/${params.page}.gtpl" %>
      </div>
      <div id="sidebar">
          <% request.pages.each { section, pages -> %>
            <h1>${section}</h1>
            <ul class="sidemenu">
             <% pages.each { label, name -> %>
              <li class="${label} ${label == params.page ? 'selected' : ''}"><a href="$label">${name}</a></li>
             <% } %>
            </ul>
          <% } %>
      </div>
    </div>
  </div>

  
  <div id="footer-wrap">
    <div id="footer-content">
      <p>Geb is free, open source software licensed under the <a href="http://www.apache.org/licenses/LICENSE-2.0.html" title="Apache License, Version 2.0">Apache License, Version 2.0</a>.</p>
    </div>
  </div>
  

</body>
</html>
