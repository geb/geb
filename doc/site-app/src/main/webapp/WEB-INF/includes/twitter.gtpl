<h1>Twitter</h1>

<script type="text/javascript" charset="utf-8">
    jQuery(function() {
        jQuery.ajax({
            url: 'feed/${params.page}',
            success: function(xml) {
                var items = new Array();
                
                jQuery('#tweets').children().remove();
                
                jQuery('entry', xml).each( function() {
                    var item = {};
                    item.tweet = jQuery(this).find('title').eq(0).text();
                    item.published = new Date(jQuery(this).find('published').eq(0).text());
                    item.link = jQuery(this).find('link[rel=alternate]').eq(0).attr("href");
                    item.image = jQuery(this).find('link[rel=image]').eq(0).attr("href");
                    item.author = jQuery(this).find('author').find("name").text();
                    item.site = jQuery(this).find('author').find("uri").text();
                    
                    item.tweet = item.tweet.substring(item.tweet.indexOf(" ") + 1);
                    
                    items.push(item);
                });
                    
                for(var i = 0; i < items.length && i < 8; i++) {
                    var item = items[i];
                    
                    jQuery('#tweets').append(
                         "<div class='tweet'>" 
                        +   "<img src='" + item.image + "' style='float: left'/>"
                        +   "<div class='tweet-content'>"
                        +     "<p><a href='" + item.link + "'>" + item.tweet + "</a></p>"
                        +     "<p class='meta'>» By <a href='" + item.site + "'>" + item.author + "</a> @ " + item.published.toDateString() + "</p>"
                        +   "</div>"
                        + "</div>" 
                    );
                    
                }
            },
            error: function() {
                jQuery('#tweets').children().replaceWith("<p class='error'>An error occurred loading the tweets, please reload the page.</p>");
            }  
        });
    });
</script>

<p>Here is what people are tweeting about Geb…</p>
<span id="tweets"><p class="loading-tweets">Loading tweets…</p></span>
<p>These are the tweets that were favorited by <a href="http://twitter.com/GroovyGeb">Geb's twitter account</a> and only shows the last 8 favorited tweets. Please see <a href="http://twitter.com/#!/GroovyGeb/favorites">here</a> for the full listing.</p>
