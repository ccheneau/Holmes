/**
 * JQuery plugin - logger
 */
(function ($) {
	$.logger = function(message){
        if ((window['console'] !== undefined)) {
          console.log(message);
        };
    }
})(jQuery);
