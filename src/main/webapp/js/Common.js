
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



function convertDateForOracle(dateString_) {

    if (!dateString_) { return ""; }

    var jsDate = new Date(dateString_);

    var month = jsDate.getMonth();
    var year = jsDate.getFullYear();
    var day = jsDate.getDate();

    var monthNames = ["JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"];

    var oracleDate = day + "-" + monthNames[month] + "-" + year;

    return oracleDate;
};


// This hides the contents of the DOM element specified by infoSelector, populates with message, fades in for 500ms, 
// keeps it visible for 3 seconds, and then takes 1 second to fade out. 
// TODO: parameterize the timing.
function fadeInfoInAndOut(class_, infoSelector_, message_) {

    jQuery(infoSelector_).hide().addClass(class_).html(message_).fadeIn(500, function () {
        setTimeout(function () {
            jQuery(infoSelector_).fadeOut(500);
        }, 1000);
    });
};


// Format a date string as MM/DD/YYYY
//Comment below function once it's not used anywhere in the code and not planning to use it anywhere as it's generating tomorrows date instead of today's
// Commenting below function and using the one I have defined to avoid changes at several places.
/*function formatDateString(date_) {

    if (!date_) { return ""; }

    var dateObject = new Date(date_);

    var month = new String(dateObject.getMonth() + 1);
    if (month.length < 2) { month = "0" + month; }
    
    var day = new String(dateObject.getDate() + 1);
    if (day.length < 2) { day = "0" + day; }

    date_ = month+ "/" + day + "/" + dateObject.getFullYear();

    return date_;
};*/

// Format a date string to MM/DD/YYYY and generating current date
function formatDateString(value_) {

                      if(moment(value_).isValid()){
                       return moment(value_).format('MM/DD/YYYY');

                       }
                       else {
                        return "";
                       }
}

// Standardize gender display / format abbreviations as the preferred text.
function formatGender(gender_) {
    
    gender_ = (!gender_ ? "" : gender_.trim());

    if (gender_ === "F" || gender_ === "f") {
        gender_ = "Female";
    } else if (gender_ === "M" || gender_ === "m") {
        gender_ = "Male";
    }
    
    return gender_;
};

// Standardize race display / format abbreviations as the preferred text.
function formatRace(race_) {

    race_ = (!race_ ? "" : race_.trim());

    if (race_ === "B" || race_ === "b") {
        race_ = "Black";
    } else if (race_ === "W" || race_ === "w") {
        race_ = "White";
    }
    return race_;
};


function formatRegistryStatus(status_) {

    if (!status_) { return ""; }

    // Not used: ui-color-info
    var statusClass = "ui-color-normal";

    if (status_ === "Accepted") {
        statusClass = "ui-color-primary";

    } else if (status_ === "Candidate") {
        statusClass = "ui-color-success";

    } else if (status_ === "Provisional") {
        statusClass = "ui-color-warning";

    } else if (status_ === "Rejected") {
        statusClass = "ui-color-danger";
    }

    return "<span class='" + statusClass + "'>" + status_ + "</span>";
};

function formatWorkflowStatus(status_) {

    if (!status_) {return ""; }

    // Not used: ui-color-info
    var statusClass = "ui-color-normal";

    if (status_ === "Under Review") {
        // A transient state indicating a patient is under review by the registrar
        statusClass = "ui-color-success";

    } else if (status_ === "Reviewed") {
        // Patient was reviewed by a registrar
        statusClass = "ui-color-primary"; 

    } else if (status_ === "Needs Review") {
        // Patient record requires registrar review, relevant new data detected.
        statusClass = "ui-color-danger";

    } else if (status_ === "Patient Never Reviewed") {
        // This patient has never been reviewed by a registrar.
        statusClass = "ui-color-warning";

    } else if (status_ === "Will Not Review") {
        // The patient will NOT be reviewed. Typically this is because the data on the patient is too old (past the registry cutoff date). 
        // This review status takes precedence over Needs Review or Patient Never Reviewed status.
        statusClass = "ui-color-ignore";
    }

    return "<span class='" + statusClass + "'>" + status_ + "</span>";
};


// TEMPORARY replacement
function isEmpty(value_) {
    if (!value_) {
        return true;
    } else {
        return false;
    }
};

// Convert a string to a boolean
function parseBool(string_) {
    return (/^true$/i).test(string_);
};
