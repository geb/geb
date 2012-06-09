$('#book-details th').each(function(i, el) {
	var fieldName = $(el).text()
    	.replace(/\s/g, '-')
    	.toLowerCase();
	$(el).siblings('td').addClass('__' + fieldName + '_value');
});
