
// Commonly-used functionality
var Common = {

    // TODO: rework this!!!
    showSpinner: function (id_, top_, left_, action_){
        if (action_ === 'add'){
            jQuery('#'+id_).append('<div class="spinner2" style=""><img src="images/ajax-loader-green.gif" alt="Loading" /></div>');
            jQuery('#'+id_+' .spinner2').css('top',top_+'px');
            jQuery('#'+id_+' .spinner2').css('left',left_+'px');
        } else {
            jQuery('#'+id_+' .spinner2').remove();
        }
    },



    // Generate a unique ID using the current Date's time.
    uniqueID: function () { return (new Date()).getTime(); },

};

if (!String.prototype.trim) {
   String.prototype.trim=function(){return this.replace(/^\s+|\s+$/g, '');};
}


// Courtesy of http://www.htmlgoodies.com/html5/javascript/calculating-the-difference-between-two-dates-in-javascript.html#fbid=1Cu_jbopvUT
function calculateAge(dateOfBirth_) {

    // If the DOB was provided as a string, convert to a Date object.
    if (typeof (dateOfBirth_) === "string") { dateOfBirth_ = new Date(dateOfBirth_); }

    //Get 1 year in milliseconds
    var one_year = 1000 * 60 * 60 * 24 * 365;

    var today = new Date();

    // Convert both dates to milliseconds
    var dob_ms = dateOfBirth_.getTime();
    var today_ms = today.getTime();

    // Calculate the difference in milliseconds
    var difference_ms = today_ms - dob_ms;

    // Convert back to years and return
    return Math.round(difference_ms / one_year);
};


// Is this value null, an empty string, or zero?
function isEmpty(value) {
    if (typeof(value) == "undefined") {
        return true;
    } else if (value == null) {
        return true;
    } else if (typeof(value) == "string" && value == "") {
        return true;
    } else if (typeof(value) == "number" && value == 0) {
        return true;
    } else {
        return false;
    }
};


// Convert a string to a boolean
function parseBool(string_) {
    return (/^true$/i).test(string_);
};
