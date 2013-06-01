/**
 * JQuery plugin - logger
 */
(function ($) {
	$.logger = function(logvar){
        if ((window['console'] !== undefined)) {
          console.log(logvar);
        };
    }
})(jQuery);
