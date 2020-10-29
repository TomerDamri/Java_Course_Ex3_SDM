var selected_zone_name;
var selected_zone;
var availableItems;


$(function () { // onload...do
    if (selected_zone_name === window.localStorage.getItem('zoneName')) {
        return;
    }
    selected_zone_name = window.localStorage.getItem('zoneName');
    $("#logged_in_user").text('Logged in as ' + window.localStorage.getItem('username'));
    //todo- add if statement according to user type
    $.ajax({
        type: "GET",
        enctype: 'multipart/form-data',
        url: "/sdm/pages/sellingZones/selectedZone",
        timeout: 2000,
        error: function () {
            console.error("Failed to submit");
        },
        success: function (response) {
            $("#place-order").load("../../components/placeOrderComponent/placeOrderComponent.html");
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
                    '</li>'
                );
                if (store.storeOwnerName === username) {
                    $('#parent_' + store.id ).append(
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
                            order.name +
                            '</a>' +
                            '</h4>' +
                            '</div>' +
                            '<div id="collapse_store_' + store.id + 'order_' + order.id + '" class="panel-collapse collapse">' +
                            '<ul class="list-group">' +
                            '<li class="list-group-item">' +
                            'Order ID :' + order.id +
                            '</li>' +
                            '</ul>' +
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
// by default - we'll always return false so it doesn't redirect the user.
    return false;
});


//
// function onOrderTypeSelect() {
//     $('#t-body').html('');
//     $.ajax({
//         type: "GET",
//         enctype: 'multipart/form-data',
//         url: "/sdm/pages/salesAreas/selectedZone",
//         timeout: 2000,
//         error: function () {
//             console.error("Failed to submit");
//         },
//         success: function (response) {
//             $("#item-price").css("visibility", "hidden");
//             var selection = $('#order-type').find(":selected").text();
//             if (selection === "From Selected Store") {
//                 $("#select-store").css("visibility", "visible");
//                 $("#item-price").css("visibility", "visible");
//                 var storeSelect = $("#order-store");
//                 storeSelect.empty();
//                 storeSelect.append('<option disabled selected value> -- select an option --</option>');
//                 response.stores.forEach(function (store) {
//                     storeSelect.append(new Option(store.name, store.id));
//                 });
//
//             } else {
//                 $("#select-store").css("visibility", "hidden");
//                 $("#item-price").css("visibility", "hidden");
//                 var tbody = $('#t-body');
//                 availableItems = response.items;
//                 tbody.empty();
//                 response.items.forEach(function (item) {
//                     tbody.append(
//                         '<tr>' +
//                         '<td>' + item.id + '</td>' +
//                         '<td>' + item.name + '</td>' +
//                         '<td>' + item.purchaseCategory + '</td>' +
//                         '<td><input type="text" id="item_' + item.id + '_amount"></td>' +
//                         '</tr>');
//                 });
//             }
//
//         }
//     });
// }
//
// function onStoreSelect() {
//     $('#t-body').html('');
//     var selectedStoreId = $('#order-store').find(":selected").val();
//     var request = {
//         zoneName: window.localStorage.getItem('zoneName'),
//         storeId: selectedStoreId
//     }
//     $.ajax({
//         type: "GET",
//         data: request,
//         url: "/sdm/pages/storeItems",
//         timeout: 2000,
//         error: function () {
//             console.error("Failed to submit");
//         },
//         success: function (response) {
//             var storeItems = response.storeItems;
//             availableItems = response.storeItems;
//             var tbody = $('#t-body');
//             tbody.empty();
//             storeItems.forEach(function (item) {
//                 tbody.append(
//                     '<tr>' +
//                     '<td>' + item.id + '</td>' +
//                     '<td>' + item.name + '</td>' +
//                     '<td>' + item.purchaseCategory + '</td>' +
//                     '<td>' + item.price + '</td>' +
//                     '<td><input type="text" id="item_' + item.id + '_amount"></td>' +
//                     '</tr>');
//             });
//         }
//     });
// }
//
// function placeOrder() {
//     var request;
//     var xCoordinate = $('#x-coordinate').val();
//     var yCoordinate = $('#y-coordinate').val();
//     var date = $('#order-date').val();
//     var items = [];
//     var itemsTable = $('#order-items-table');
//     availableItems.forEach(function (item) {
//         var itemAmount = $('#item_' + item.id + '_amount').val();
//         //todo validation on item amount (a positive double or integer)
//         if (itemAmount) {
//             items.push({
//                 itemId: item.id,
//                 amount: itemAmount
//             });
//         }
//     });
//     request = {
//         customerId: window.localStorage.getItem('userId'),
//         zoneName: selected_zone_name,
//         xCoordinate: xCoordinate,
//         yCoordinate: yCoordinate,
//         orderDate: date,
//         itemsCount: items.length,
//         orderItemToAmount: items
//     }
//
//     var selection = $('#order-type').find(":selected").text();
//     if (selection === "From Selected Store") {
//         request.storeId = $('#order-store').find(":selected").val();
//     }
//
//
//     $.ajax({
//         type: "POST",
//         url: "/sdm/pages/placeOrder",
//         data: request,
//         cache: false,
//         timeout: 2000,
//         error: function () {
//             console.error("Failed to submit");
//         },
//         success: function (response) {
//             var placeOrderModal = $('#placeOrderModal');
//             placeOrderModal.modal('toggle');
//             placeOrderModal.find("input,textarea,select").val('').end();
//             $('#t-body').html('');
//
//             //todo- in case of dynamic display offer
//             //todo handle discounts
//             if (selection === "From Selected Store") {
//
//             } else {
//                 var dynamicOrderOfferModalBody = $('#dynamicOrderOfferModalBody');
//                 response.dynamicOrderEntityDTO.forEach(storeToOrderFrom=>{
//                     dynamicOrderOfferModalBody.empty();
//                     dynamicOrderOfferModalBody.append('<h3>This is the offer for your order</h3>');
//                     for (var x in storeToOrderFrom) {
//                         dynamicOrderOfferModalBody.append('<span>' + x + ': ' + storeToOrderFrom[x] + '</span>' +'<br>');
//                     }
//                     dynamicOrderOfferModalBody.append('<br>');
//                 })
//
//                 $('#dynamicOrderOfferModal').modal('show');
//
//             }
//         }
//
//     });
// }
function confirmDynamicOrder() {
    $('#dynamicOrderOfferModal').modal('toggle');
}

// function onModalClose() {
//     $('#placeOrderModal').find("input,textarea,select").val('').end();
//     $('#t-body').html('');
// }



