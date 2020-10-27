var refreshRate = 2000; //milli seconds
var USER_LIST_URL = buildUrlWithContextPath("userslist");
var IS_STORE_OWNER = buildUrlWithContextPath("chat");

//users = a list of usernames, essentially an array of javascript strings:
// ["moshe","nachum","nachche"...]
function refreshUsersList(users) {
    //clear all current users
    $("#userslist").empty();

    // rebuild the list of users: scan all users and add them to the list of users
    $.each(users || [], function (index, user) {
        console.log("Adding user #" + index + ": " + user.name);
        //
        //create a new <li> tag with a value in it and append it to the #userslist (div with id=userslist) element
        $('<li>' + user.name + " " + user.userType.toLowerCase() + '</li>')
            .appendTo($("#userslist"));
    });
}

function ajaxUsersList() {
    $.ajax({
        url: USER_LIST_URL,
        success: function (users) {
            refreshUsersList(users);
        }
    });
}

//add a method to the button in order to make that form use AJAX
//and not actually submit the form
$(function () { // onload...do
    //add a function to the submit event
    var form = $('#fileform')[0];
    var data = new FormData(form);

    $.ajax({
        type: "POST",
        enctype: 'multipart/form-data',
        url: this.action,
        data: data,
        processData: false,
        contentType: false,
        cache: false,
        timeout: 2000,
        error: function () {
            console.error("Failed to submit");
        },
        success: function (r) {
        }
    });

    // by default - we'll always return false so it doesn't redirect the user.
    return false;
});

$(function () {
    //The users list is refreshed automatically every second
    setInterval(ajaxUsersList, refreshRate);
    setInterval(tableCreate, refreshRate);
});

function initPage() {
    $("#logged_in_user").text('Logged in as ' + window.localStorage.getItem('username'));
    var table = $("#sales_areas_table");
    table.on("click", "td", onAreaClick); // <-- magic!
    var url = "http://dbpedia.org/sparql"
    if (window.localStorage.getItem("userType") === "CUSTOMER") {
        document.getElementById("fileUpload").style.visibility = "hidden";
    } else {
        document.getElementById("fileUpload").style.visibility = "visible";

    }
}

function onAreaClick() {
    var zone = this.attributes.zoneName.value;

    $.ajax({
        type: "POST",
        enctype: 'multipart/form-data',
        url: "/sdm/pages/salesAreas/selectedZone",
        data: {zone: zone},
        // timeout: 2000,
        error: function (error) {
            console.error("Failed to submit");
        },
        success: function (r) {
            window.location.replace("../selectedZone/selectedZone.html");
            window.localStorage.setItem("zoneName", zone);
        }
    });
    //todo rout to 3rd page

}

function tableCreate() {

    //todo - get zones
    $.ajax({
        type: "GET",
        url: "/sdm/pages/salesAreas/zonesList",
        error: function (error) {
            console.error("Failed to submit");
        },
        success: function (r) {
          var zones = r.systemZones;
            var tableBody = document.getElementsByTagName('tbody')[0];
            while (tableBody.firstChild) {
                tableBody.removeChild(tableBody.lastChild);
            }
            for (var i = 0; i < zones.length; i++) {
                var tr = document.createElement('tr');

                var td = document.createElement('td');
                td.appendChild(document.createTextNode(zones[i].zoneOwnerName));
                td.setAttribute('zoneName', zones[i].zoneName);
                tr.appendChild(td);

                td = document.createElement('td');
                td.appendChild(document.createTextNode(zones[i].zoneName));
                td.setAttribute('zoneName', zones[i].zoneName);
                tr.appendChild(td);

                td = document.createElement('td');
                td.appendChild(document.createTextNode(zones[i].numOfItemsForSale));
                td.setAttribute('zoneName', zones[i].zoneName);
                tr.appendChild(td);

                td = document.createElement('td');
                td.appendChild(document.createTextNode(zones[i].numOfStoresInZone));
                td.setAttribute('zoneName', zones[i].zoneName);
                tr.appendChild(td);

                td = document.createElement('td');
                td.appendChild(document.createTextNode(zones[i].numOfExecutedOrders));
                td.setAttribute('zoneName', zones[i].zoneName);
                tr.appendChild(td);

                td = document.createElement('td');
                td.appendChild(document.createTextNode(zones[i].avgOrdersPrice));
                td.setAttribute('zoneName', zones[i].zoneName);
                tr.appendChild(td);

                tableBody.appendChild(tr);
            }
        }
    });
}