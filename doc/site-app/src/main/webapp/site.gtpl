<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <meta name="Robots" content="index,follow" />
  <link rel="stylesheet" href="css/main.css" type="text/css" />
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
  <script language="javascript" src="js/shCore.js"></script>
  <script language="javascript" src="js/shBrushGroovy.js"></script>
  <script type="text/javascript" charset="utf-8">
    SyntaxHighlighter.defaults['toolbar'] = false;
    SyntaxHighlighter.defaults['gutter'] = false;
    SyntaxHighlighter.all();
  </script>
  <title>Geb - Very Groovy Browser Automation</title>
</head>

<body>
  
  <div id="header-wrap">
    <div id="header-content"> 
    
      <h1 id="logo"><a href="/" title="">Geb <span class="orange" style="font-size: 50%; font-style: italic">(pronounced “jeb”)</span></a></h1>  
      <h2 id="slogan">very groovy browser automation… web testing, screen scraping and more</h2>
      
      <ul>
        <li><a href="manual/">Manual</a></li>
        <li><a href="http://xircles.codehaus.org/projects/geb/lists">Mailing List</a></li>
        <li><a href="http://jira.codehaus.org/browse/GEB">Issues</a></li>
        <li><a href="http://github.com/geb/geb">Source</a></li>      
      </ul>
  
    </div>
  </div>
        
  <div id="content-wrap">
    <div id="content">
      <div id="main"><% include "/WEB-INF/includes/${params.page}.gtpl" %></div>
      <div id="sidebar">
          <% request.pages.each { section, pages -> %>
            <h1>${section}…</h1>
            <ul class="sidemenu">
             <% pages.each { label, name -> %>
              <li class="${label} ${label == params.page ? 'selected' : ''}"><a href="$label">${name}</a></li>
             <% } %>
            </ul>
          <% } %>
      </div>
    </div>
  </div>

  <!-- footer starts here --> 
  <div id="footer-wrap"><div id="footer-content">
  
    <div class="col float-left space-sep">
      <h2>Site Partners</h2>
      <ul class="columns">
                <li class="top"><a href="http://www.dreamtemplate.com" title="Website Templates">DreamTemplate</a></li>
                <li><a href="http://www.themelayouts.com" title="WordPress Themes">ThemeLayouts</a></li>
                <li><a href="http://www.imhosted.com" title="Website Hosting">ImHosted.com</a></li>
                <li><a href="http://www.dreamstock.com" title="Stock Photos">DreamStock</a></li>
                <li><a href="http://www.evrsoft.com" title="Website Builder">Evrsoft</a></li>
                <li><a href="http://www.webhostingwp.com" title="Web Hosting">Web Hosting</a></li>
      </ul>     
    </div>
    
    <div class="col float-left">
      <h2>Links</h2>
      <ul class="columns">        
        <li class="top"><a href="index.html">Link One</a></li>
        <li><a href="index.html">Link Two</a></li>
        <li><a href="index.html">Link Three</a></li>
        <li><a href="index.html">Link Four</a></li>
        <li><a href="index.html">Link Five</a></li>
                <li><a href="index.html">Link Six</a></li>
      </ul>
    </div>    
  
    <div class="col2 float-right">
            <h2>Site Links</h2>
      <ul class="columns">
        <li class="top"><a href="index.html">Home</a></li>
                <li><a href="index.html">About</a></li>
        <li><a href="index.html">Sitemap</a></li>
        <li><a href="index.html">RSS Feed</a></li>                
      </ul>

            <p>
      &copy; copyright 2010 <strong>Your Company Name</strong><br />
      <a href="http://www.bluewebtemplates.com/" title="Website Templates">website templates</a> by <a href="http://www.styleshout.com/">styleshout</a> <br />

      Valid <a href="http://jigsaw.w3.org/css-validator/check/referer">CSS</a> |
              <a href="http://validator.w3.org/check/referer">XHTML</a>
      </p>
    </div>
    
    <br class="clear" />
  
  </div></div>
  <!-- footer ends here -->

</body>
</html>
