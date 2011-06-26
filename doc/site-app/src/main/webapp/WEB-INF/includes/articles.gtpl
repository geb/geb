<h1>Articles</h1>

<script type="text/javascript" charset="utf-8">
    jQuery(function() {
        jQuery.ajax({
            url: 'feed/${params.page}',
            success: function(xml) {
                var items = new Array();
                
                jQuery('#articles').children().remove();
                
                jQuery('item', xml).each( function() {
                    var item = {};
                    item.title = jQuery(this).find('title').eq(0).text();
                    item.published = new Date(jQuery(this).find('pubDate').eq(0).text());
                    item.link = jQuery(this).find('link').eq(0).text();
                    item.description = jQuery(jQuery(this).find('description').eq(0).text());
                    
                    items.push(item);
                });
                    
                for(var i = 0; i < items.length && i < 8; i++) {
                    var item = items[i];
                    // alert(item.description);
                    jQuery('#articles').append(
                      "<div class='article'>" 
                    +   "<p class='title'><a href='" + item.link + "'>" + item.title + "</a></p>"
                    +   item.description.outerHtml()
                    + "</div>" 
                    );
                    
                }
                
                jQuery(".article").each(function() {
                    var article = jQuery(this);
                    article.find("strong:contains(by:)").parent().remove();
                    article.find("li small").remove();
                    article.find("li").each(function() {
                        var item = jQuery(this);
                        var text = item.text();
                        
                        item.text(text.substring(0, text.lastIndexOf(" - ")));
                    })
                });
            },
            error: function() {
                jQuery('#articles').children().replaceWith("<p class='error'>An error occurred loading the articles, please reload the page.</p>");
            }    
        });
    });
</script>

<p>Here are some recently posted articles about Geb…</p>
<span id="articles"><p class="loading-articles">Loading articles…</p></span>
<p>This list is collated by the developers of Geb using <a href="http://www.diigo.com/" title="">Diigo</a> and only shows the last 8 collated articles. Please see <a href="http://groups.diigo.com/group/geb-resources/" title="Best content in geb-resources | Diigo - Groups">here</a> for the full, searchable, list.</p>
