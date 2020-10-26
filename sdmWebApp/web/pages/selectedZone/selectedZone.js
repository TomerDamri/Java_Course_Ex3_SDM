var selected_zone;
$(function () { // onload...do
    if (selected_zone === window.localStorage.getItem('zoneName')) {
        return;
    }
    selected_zone = window.localStorage.getItem('zoneName');
    $("#logged_in_user").text('Logged in as ' + window.localStorage.getItem('username'));
    //todo- add if statement according to user type
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
            $('#items-accordion').empty();
            $('#stores-accordion').empty();
            items.forEach(function (item) {
                $('#items-accordion').append(
                    '<div class="panel-group">' +
                    '<div class="panel panel-default">' +
                    '<div class="panel-heading" role="tab" id="heading_' + item.id + '">' +
                    '<h4 class="panel-title">' +
                    '<a class="collapsed" data-toggle="collapse" data-parent="#items-accordion" href="#collapse_item_' + item.id + '" aria-expanded="false" aria-controls="#collapse_item_' + item.id + '">' +
                    item.name +
                    '</a>' +
                    '</h4>' +
                    '</div>' +
                    '<div id="collapse_item_' + item.id + '" class="panel-collapse collapse">' +
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
            stores.forEach(function (store) {
                $('#stores-accordion').append(
                    '<div class="panel-group">' +
                    '<div class="panel panel-default">' +
                    '<div class="panel-heading" role="tab" id="heading_' + store.id + '">' +
                    '<h4 class="panel-title">' +
                    '<a class="collapsed" data-toggle="collapse" data-parent="#stores-accordion" href="#collapse_store_' + store.id + '" aria-expanded="false" aria-controls="#collapse_store_' + store.id + '">' +
                    store.name +
                    '</a>' +
                    '</h4>' +
                    '</div>' +
                    '<div id="collapse_store_' + store.id + '" class="panel-collapse collapse">' +
                    '<ul id="parent_' + store.id + '" class="list-group">' +
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
                    '<li class="list-group-item">' +
                    ' <button type="button" class="btn btn-info btn-lg" data-toggle="modal" data-target="#itemsModal_' + store.id + '">Store Items</button>' +
                    '<div class="modal fade" id="itemsModal_' + store.id + '" role="dialog">' +
                    '<div class="modal-dialog">' +
                    '<div class="modal-content">' +
                    '<div class="modal-header">' +
                    '<button type="button" class="close" data-dismiss="modal">&times;</button>' +
                    '<h4 class="modal-title">Store Items</h4>' +
                    '</div>' +
                    '<div class="modal-body" id="store-' + store.id + '-items">' +
                    '</div>' +
                    '<div class="modal-footer">' +
                    '<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>' +
                    '</div>' +
                    '</div>' +
                    '</div>' +
                    '</div>' +
                    '</li>' +
                    '<li class="list-group-item">' +
                    ' <button type="button" class="btn btn-info btn-lg" data-toggle="modal" data-target="#ordersModal_' + store.id + '">Store Items</button>' +
                    '<div class="modal fade" id="ordersModal_' + store.id + '" role="dialog">' +
                    '<div class="modal-dialog">' +
                    '<div class="modal-content">' +
                    '<div class="modal-header">' +
                    '<button type="button" class="close" data-dismiss="modal">&times;</button>' +
                    '<h4 class="modal-title">Store Orders</h4>' +
                    '</div>' +
                    '<div class="orders-modal-body" id="store-' + store.id + '-items">' +
                    '</div>' +
                    '<div class="modal-footer">' +
                    '<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>' +
                    '</div>' +
                    '</div>' +
                    '</div>' +
                    '</div>' +
                    '</li>' +
                    '</ul>' +
                    '</div>' +
                    '</div>'
                );
            });
            stores.forEach(function (store) {
                var itemsModal = $("#store-" + store.id + "-items");
                store.items.forEach(function (storeItem) {
                    itemsModal.append(
                        '<div class="panel-group">' +
                        '<div class="panel panel-default">' +
                        '<div class="panel-heading" role="tab" id="heading_' + storeItem.id + '">' +
                        '<h4 class="panel-title">' +
                        '<a class="collapsed" data-toggle="collapse" data-parent="#items-accordion" href="#collapse_store_' + store.id + 'item_' + storeItem.id + '" aria-expanded="false" aria-controls="#collapse_store_' + store.id + 'item_' + storeItem.id + '">' +
                        storeItem.name +
                        '</a>' +
                        '</h4>' +
                        '</div>' +
                        '<div id="collapse_store_' + store.id + 'item_' + storeItem.id + '" class="panel-collapse collapse">' +
                        '<ul class="list-group">' +
                        '<li class="list-group-item">' +
                        'Item ID :' + storeItem.id +
                        '</li>' +
                        '<li class="list-group-item">' +
                        'Purchase Catagory: ' + storeItem.purchaseCategory +
                        '</li>' +
                        '<li class="list-group-item">' +
                        'Stores Count : ' + storeItem.storesCount +
                        '</li>' +
                        '<li class="list-group-item">' +
                        'Average Price : ' + storeItem.avgPrice +
                        '</li>' +
                        '<li class="list-group-item">' +
                        'Total Number Of Purchases : ' + (storeItem.ordersCount + storeItem.discountOrderCount) +
                        '</li>' +
                        '</ul>' +
                        '</div>' +
                        '</div>'
                    )
                });
            });
        }
    });
// by default - we'll always return false so it doesn't redirect the user.
    return false;
});

function onOrderTypeSelect() {
    $.ajax({
        type: "GET",
        enctype: 'multipart/form-data',
        url: "/sdm/pages/salesAreas/selectedZone",
        timeout: 2000,
        error: function () {
            console.error("Failed to submit");
        },
        success: function (response) {
            var selection = $('#order-type').find(":selected").text();
            if (selection === "From Selected Store") {
                $("#select-store").css("visibility", "visible");
                $("#item-price").css("visibility", "visible");
                var storeSelect = $("#order-store");
                storeSelect.empty();
                response.stores.forEach(function (store) {
                    storeSelect.append(new Option(store.name, store.id));
                });

            } else {
                $("#select-store").css("visibility", "hidden");
                $("#item-price").css("visibility", "hidden");
                var tbody = $('#t-body');
                tbody.empty();
                response.items.forEach(function (item) {
                    tbody.append(
                        '<tr>' +
                        '<td>' + item.name + '</td>' +
                        '<td><input type="text" id="item_' + item.id + '_amount"></td>' +
                        '</tr>');
                });
            }

        }
    });
}
function onStoreSelect(){
    var selectedStore = selectedStore.find(":selected").text();
    //todo get store items
}


