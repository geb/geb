$('.menu .apis').click(function() {
    $('#manuals-menu').hide();
    $('#mailing-lists-menu').hide();
    $('#apis-menu').transition('fade down');
});

$('.menu .manuals').click(function() {
    $('#apis-menu').hide();
    $('#mailing-lists-menu').hide();
    $('#manuals-menu').transition('fade down');
});

$('.menu .mailing-lists').click(function() {
    $('#manuals-menu').hide();
    $('#apis-menu').hide();
    $('#mailing-lists-menu').transition('fade down');
});

$.get('https://api.github.com/repos/geb/geb')
    .done(function(body) {
        var watchers = body.subscribers_count;
        var stars = body.stargazers_count;
        var forks = body.network_count;
        $('[data-stars]').text(stars);
        $('[data-watchers]').text(watchers);
        $('[data-forks]').text(forks);
    })
    .catch(function(e) {
        console.error('Github api request failed. No stats available.')
        $('[data-github-stats] > .statistic:not(.static)').transition('fade')
    })