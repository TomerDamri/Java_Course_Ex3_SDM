var selected_zone_name;
var selected_zone;
var availableItems;
var refreshRate = 2000; //milli seconds


function ajaxNotifications() {
    $.ajax({
        type: 'GET',
        url: '/sdm/pages/alerts',
        error: function (error) {
        },
        success: function (response) {
            if (response.userNotifications && response.userNotifications.length > 0) {
                var notification = "";
                for (var i = 0, j = 1; i < response.userNotifications.length; i++, j++) {
                    notification = notification + j + '. ' + response.userNotifications[i] + '\n\n';
                }
                alert(notification);
            }
        }
    });
}

$(function () { // onload
    if (selected_zone_name === window.localStorage.getItem('zoneName')) {
        return;
    }
    selected_zone_name = window.localStorage.getItem('zoneName');
    $("#logged_in_user").text('Logged in as ' + window.localStorage.getItem('username'));
    $.ajax({
        type: "GET",
        enctype: 'multipart/form-data',
        url: "/sdm/pages/sellingZones/selectedZone",
        timeout: 2000,
        error: function (error) {
            alert(error.responseText);
        },
        success: function (response) {
            if (window.localStorage.getItem('userType') === "CUSTOMER") {
                $("#place-order").load("../../components/placeOrderComponent/placeOrderComponent.html");
                $("#display-orders").load("../../components/displayOrdersComponent/displayOrdersComponent.html")
            } else {
                $("#newStore").load("../../components/addNewStoreComponent/addNewStoreComponent.html");
                $("#store-owner-rates").load("../../components/displayFeedbackComponent/displayFeedbackComponent.html");
                setInterval(ajaxNotifications, refreshRate);
            }
            var username = window.localStorage.getItem("username");
            $("#logged_in_user").text('Logged in as ' + window.localStorage.getItem('username'));
            $("#zone").text(window.localStorage.getItem('zoneName'));
            selected_zone = response;
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
                    'Location : (' + store.location.xCoordinate + ',' + store.location.yCoordinate + ')' +
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
                    '</li>'
                );
                if (store.storeOwnerName === username) {
                    $('#parent_' + store.id).append(
                        '<li class="list-group-item">' +
                        ' <button type="button" class="btn btn-info btn-lg" data-toggle="modal" data-target="#ordersModal_' + store.id + '">Store Orders</button>' +
                        '<div class="modal fade" id="ordersModal_' + store.id + '" role="dialog">' +
                        '<div class="modal-dialog">' +
                        '<div class="modal-content">' +
                        '<div class="modal-header">' +
                        '<button type="button" class="close" data-dismiss="modal">&times;</button>' +
                        '<h4 class="modal-title">Store Orders</h4>' +
                        '</div>' +
                        '<div class="orders-modal-body" id="store-' + store.id + '-orders">' +
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

                    store.orders.forEach(function (order) {
                        var ordersModalBody = $("#store-" + store.id + '-orders');
                        ordersModalBody.append(
                            '<div class="panel-group">' +
                            '<div class="panel panel-default">' +
                            '<div class="panel-heading" role="tab" id="heading_' + order.id + '">' +
                            '<h4 class="panel-title">' +
                            '<a class="collapsed" data-toggle="collapse" data-parent="#items-accordion" href="#collapse_store_' + store.id + 'order_' + order.id + '" aria-expanded="false" aria-controls="#collapse_store_' + store.id + 'order_' + order.id + '">' +
                            order.id +
                            '</a>' +
                            '</h4>' +
                            '</div>' +
                            '<div id="collapse_store_' + store.id + 'order_' + order.id + '" class="panel-collapse collapse">' +
                            '<span>Items Quantity: ' + order.amountOfItems + '</span><br>' +
                            '<span>Delivery Price: ' + order.deliveryPrice + '</span><br>' +
                            '<span>Items Price: ' + order.itemsPrice + '</span><br>' +
                            '<span>Order Location: (' + order.location.xCoordinate + ',' + order.location.yCoordinate + ')</span><br>' +
                            '<span>Number Of Items Types: ' + order.numOfItemTypes + '</span><br>' +
                            '<span>Parent Order Id: ' + order.parentId + '</span><br>' +
                            '<span>Total Price: ' + order.totalPrice + '</span><br>' +
                            '</div>' +
                            '</div>' +
                            '</div>'
                        )
                    })
                }

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
                        'Price : ' + storeItem.price +
                        '</li>' +
                        '<li class="list-group-item">' +
                        'Purchases in store: ' + storeItem.purchasesCount +
                        '</li>' +
                        '</ul>' +
                        '</div>' +
                        '</div>'
                    )
                });
            });
        }
    });
    return false;
});