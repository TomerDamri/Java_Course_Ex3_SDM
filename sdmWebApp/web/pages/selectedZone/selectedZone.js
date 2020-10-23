$(function () { // onload...do
//todo - get the zone details
    $("#logged_in_user").text('Logged in as ' + window.localStorage.getItem('username'));
    $.ajax({
        type: "GET",
        enctype: 'multipart/form-data',
        url: "/sdm/pages/salesAreas/selectedZone",
        timeout: 2000,
        error: function () {
            console.error("Failed to submit");
        },
        success: function (response) {
            //TODO - UPDATE ZONE DATA IN PAGE 3
            $("#logged_in_user").text('Logged in as ' + window.localStorage.getItem('username'));
            $("#zone").text(window.localStorage.getItem('zoneName'));
            var items = response.items;
            var stores = response.stores;
            items.forEach(function (item, index) {
                $('#items-accordion').append(
                    '<div class="panel-group">' +
                    '<div class="panel panel-default">' +
                    '<div class="panel-heading" role="tab" id="heading_' + index + '">' +
                    '<h4 class="panel-title">' +
                    '<a class="collapsed" data-toggle="collapse" data-parent="#items-accordion" href="#collapse_item_' + index + '" aria-expanded="false" aria-controls="#collapse_item_' + index + '">' +
                    item.name +
                    '</a>' +
                    '</h4>' +
                    '</div>' +
                    '<div id="collapse_item_' + index + '" class="panel-collapse collapse">' +
                    '<ul class="list-group">' +
                    '<li class="list-group-item">' +
                    'Item ID :' + item.id +
                    '</li>' +
                    '<li class="list-group-item">' +
                    'Purchase Catagory: ' + item.purchaseCategory +
                    '</li>' +
                    '<li class="list-group-item">' +
                    'Stores Count : ' + item.storesCount +
                    '</li>' +
                    '<li class="list-group-item">' +
                    'Average Price : ' + item.avgPrice +
                    '</li>' +
                    '<li class="list-group-item">' +
                    'Total Number Of Purchases : ' + (item.ordersCount + item.discountOrderCount) +
                    '</li>' +
                    '</ul>' +
                    '</div>' +
                    '</div>'
                );
            });
            stores.forEach(function (store, index) {
                $('#stores-accordion').append(
                    '<div class="panel-group">' +
                    '<div class="panel panel-default">' +
                    '<div class="panel-heading" role="tab" id="heading_' + index + '">' +
                    '<h4 class="panel-title">' +
                    '<a class="collapsed" data-toggle="collapse" data-parent="#stores-accordion" href="#collapse_store_' + index + '" aria-expanded="false" aria-controls="#collapse_store_' + index + '">' +
                    store.name +
                    '</a>' +
                    '</h4>' +
                    '</div>' +
                    '<div id="collapse_store_' + index + '" class="panel-collapse collapse">' +
                    '<ul class="list-group">' +
                    '<li class="list-group-item">' +
                    'Store ID :' + store.id +
                    '</li>' +
                    '<li class="list-group-item">' +
                    'Store Owner: ' + store.storeOwnerName +
                    '</li>' +
                    '<li class="list-group-item">' +
                    'Location : ' + store.location +
                    '</li>' +
                    '<li class="list-group-item">' +
                    'Orders Count : ' + store.orders.length +
                    '</li>' +
                    '<li class="list-group-item">' +
                    'Total Deliveries Payment : ' + (store.totalDeliveriesPayment) +
                    '</li>' +

                    '<li id="store-' + index + '-items-accordion" class="list-group-item panel-group">' +
                    '<div class="panel panel-default">' +
                    '<div class="panel-heading" role="tab" id="heading_' + index + '">' +
                    '<h4 class="panel-title">' +
                    '<a class="collapsed" data-toggle="collapse" data-parent="#store-items-accordion" href="#collapse_store_item_' + index + '" aria-expanded="false" aria-controls="#collapse_store_item_' + index + '">' +
                    'Store Items ' +
                    '</a>' +
                    '</h4>' +
                    '</div>' +
                    '</li>' +
                    '</ul>' +
                    '</div>' +
                    '</div>'
                );
            });
            stores.forEach(function (store, index1) {
                var store_accordion = $('#store-' + index1 + '-items-accordion');
                store.items.forEach(function (storeItem, index2) {
                    store_accordion.append(
                        '<div id="store_' + index1 + '_item_' + index2 + '" class="panel-collapse collapse">' +
                        storeItem.name +
                        '</div>'
                    );
                });
            });
        }
    });


// by default - we'll always return false so it doesn't redirect the user.
    return false;

});

