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
        var userType = user.userType === "CUSTOMER" ? "Customer" : "Store Owner";
        //create a new <li> tag with a value in it and append it to the #userslist (div with id=userslist) element
        $('<li>' + user.name + " " + userType + '</li>')
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

function uploadFile() {
    var file = $('#fileform')[0];
    var data = new FormData(file);
    $.ajax({
        type: "POST",
        enctype: 'multipart/form-data',
        url: '/sdm/pages/sellingZones/uploadFile',
        data: data,
        processData: false,
        contentType: false,
        cache: false,
        timeout: 2000,
        // error: function (error) {
        //     // console.error("Failed to submit");
        //     alert('Error Uploading File' + error.getResponseHeader());
        // },
        error: function (error) {
            alert("Error occurred while uploading file:\n" + error.responseText);
        },
        success: function (response) {
            alert('File Uploaded Successfully');
        }
    });
}

$(function () {
    //The users list is refreshed automatically every second
    setInterval(ajaxUsersList, refreshRate);
    setInterval(tableCreate, refreshRate);
    setInterval(ajaxAccountBalance, refreshRate);
    document.getElementById('sales_areas_table').style.visibility = "hidden";
    // $("#sales_areas_table").style = "visibility : hidden";
    $("#logged_in_user").text('Logged in as ' + window.localStorage.getItem('username'));
    var table = $("#sales_areas_table");
    table.on("click", "td", onAreaClick); // <-- magic!
    var url = "http://dbpedia.org/sparql"
    if (window.localStorage.getItem("userType") === "CUSTOMER") {
        document.getElementById("fileUpload").style.visibility = "hidden";
        document.getElementById("accountDeposit").style.visibility = "visible";
    } else {
        document.getElementById("fileUpload").style.visibility = "visible";
        document.getElementById("accountDeposit").style.visibility = "hidden";
    }
});

function ajaxAccountBalance() {
    $.ajax({
        type: "GET",
        url: "/sdm/pages/userAccount",
        // timeout: 2000,
        error: function (error) {
            console.error("Failed to submit");
        },
        success: function (response) {
            $("#balance").text(response.balance);
        }
    });

}

function deposit() {
    var amountToDeposit = $('#deposit').val();
    $.ajax({
        type: "POST",
        data: {amount: amountToDeposit},
        url: "/sdm/pages/userAccount",
        // timeout: 2000,
        error: function (error) {
            alert(error.responseText);
        },
        success: function (response) {
            alert("Deposit " + amountToDeposit + " Shekels Successfully");
            $('#deposit').val("").end();
        }
    });

}

function displayTransactions() {
    var displayTransactionsModalBody = $('#displayTransactionsModalBody');
    displayTransactionsModalBody.empty();
    $.ajax({
        type: "GET",
        url: "/sdm//pages/userTransactions",
        error: function (error) {
            alert(error.responseText);
        },
        success: function (response) {
            displayTransactionsModalBody.append('<h3>Your Transactions</h3>');
            if (response.transactions.length === 0) {
                alert("There are no transactions to display")
            } else {
                response.transactions.forEach(transaction => {
                    for (var x in transaction) {
                        var keyName = x.replace(/([A-Z])/g, ' $1').trim();
                        keyName = keyName.charAt(0).toUpperCase() + keyName.slice(1)
                        displayTransactionsModalBody.append('<span>' + keyName + ': ' + transaction[x] + '</span>' + '<br>');
                    }
                    displayTransactionsModalBody.append('<br>');
                })
                $('#displayTransactionsModal').modal('show');
            }
        }
    });
}

function onAreaClick() {
    var zone = this.attributes.zoneName.value;

    $.ajax({
        type: "POST",
        enctype: 'multipart/form-data',
        url: "/sdm/pages/sellingZones/selectedZone",
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
        url: "/sdm/pages/sellingZones/zonesList",
        error: function (error) {
            console.error("Failed to submit");
        },
        success: function (r) {
            document.getElementById('sales_areas_table').style.visibility = "visible";
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