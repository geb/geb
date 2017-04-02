$('.menu .manuals')
    .popup({
        popup: '#manuals-popup',
        hoverable: true,
        exclusive: true,
        position: 'bottom left',
        delay: {
            show: 300,
            hide: 800
        }
    });

$('.menu .apis')
    .popup({
        popup: '#apis-popup',
        hoverable: true,
        exclusive: true,
        position: 'bottom left',
        delay: {
            show: 300,
            hide: 800
        }
    });

$('.menu .mailing-lists')
    .popup({
        popup: '#mailing-lists-popup',
        hoverable: true,
        exclusive: true,
        position: 'bottom left',
        delay: {
            show: 300,
            hide: 800
        }
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