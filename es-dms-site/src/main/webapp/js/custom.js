$(window).load(function(){
	
$('.popover-markup>.trigger').popover({ 
	animation: true,
	placement: 'bottom',
    html : true,
    title: function() {
      return $(this).parent().find('.head').html();
    },
    content: function() {
      return $(this).parent().find('.content').html();
    }
	});
});

$(window).click(function(){
	alert("Closing");
	$('.popover-markup>.btn').popover('hide');
});