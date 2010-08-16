$(function() {
	$("pre code").each(function (i) {
		var code = $(this);
		var pre = code.parent("pre");
		
		pre.text(code.detach().text()).addClass("brush: groovy");
	});
})
SyntaxHighlighter.defaults['toolbar'] = false;
SyntaxHighlighter.all();
