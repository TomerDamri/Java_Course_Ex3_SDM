var order_id;
var discounts = [];

$(function () { // onload...do

    if (selected_zone_name === window.localStorage.getItem('zoneName')) {
        return;
    }
    selected_zone_name = window.localStorage.getItem('zoneName');
    $("#logged_in_user").text('Logged in as ' + window.localStorage.getItem('username'));
    //todo- add if statement according to user type (actions)
    $.ajax({
        type: "GET",
        enctype: 'multipart/form-data',
        url: "/sdm/pages/sellingZones/selectedZone",
        timeout: 2000,
        error: function (error) {
            alert.error(error.responseText);
        },
        success: function (response) {
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
    $('#t-body').html('');
    $.ajax({
        type: "GET",
        enctype: 'multipart/form-data',
        url: "/sdm/pages/sellingZones/selectedZone",
        timeout: 2000,
        error: function (error) {
            alert.error(error.responseText);
        },
        success: function (response) {
            $("#item-price").css("visibility", "hidden");
            var selection = $('#order-type').find(":selected").text();
            if (selection === "From Selected Store") {
                $("#select-store").css("visibility", "visible");
                $("#item-price").css("visibility", "visible");
                var storeSelect = $("#order-store");
                storeSelect.empty();
                storeSelect.append('<option disabled selected value> -- select an option --</option>');
                response.stores.forEach(function (store) {
                    storeSelect.append(new Option(store.name, store.id));
                });

            } else {
                $("#select-store").css("visibility", "hidden");
                $("#item-price").css("visibility", "hidden");
                var tbody = $('#t-body');
                availableItems = response.items;
                tbody.empty();
                response.items.forEach(function (item) {
                    tbody.append(
                        '<tr>' +
                        '<td>' + item.id + '</td>' +
                        '<td>' + item.name + '</td>' +
                        '<td>' + item.purchaseCategory + '</td>' +
                        '<td></td>' +
                        '<td><input type="text" id="item_' + item.id + '_amount"></td>' +
                        '</tr>');
                });
            }

        }
    });
}

function onStoreSelect() {
    $('#t-body').html('');
    var selectedStoreId = $('#order-store').find(":selected").val();
    var request = {
        zoneName: window.localStorage.getItem('zoneName'),
        storeId: selectedStoreId
    }
    $.ajax({
        type: "GET",
        data: request,
        url: "/sdm/pages/storeItems",
        timeout: 2000,
        error: function (error) {
            alert.error(error.responseText);
        },
        success: function (response) {
            var storeItems = response.storeItems;
            availableItems = response.storeItems;
            var tbody = $('#t-body');
            tbody.empty();
            storeItems.forEach(function (item) {
                tbody.append(
                    '<tr>' +
                    '<td>' + item.id + '</td>' +
                    '<td>' + item.name + '</td>' +
                    '<td>' + item.purchaseCategory + '</td>' +
                    '<td>' + item.price + '</td>' +
                    '<td><input type="text" id="item_' + item.id + '_amount"></td>' +
                    '</tr>');
            });
        }
    });
}

function placeOrder() {
    var request;
    var xCoordinate = $('#x-coordinate').val();
    var yCoordinate = $('#y-coordinate').val();
    var date = $('#order-date').val();
    var items = [];
    var itemsTable = $('#order-items-table');
    availableItems.forEach(function (item) {
        var itemAmount = $('#item_' + item.id + '_amount').val();
        //todo validation on item amount (a positive double or integer)
        if (itemAmount) {
            items.push({
                itemId: item.id,
                amount: itemAmount
            });
        }
    });
    request = {
        customerId: window.localStorage.getItem('userId'),
        zoneName: selected_zone_name,
        xCoordinate: xCoordinate,
        yCoordinate: yCoordinate,
        orderDate: date,
        itemsCount: items.length,
        orderItemToAmount: items
    }

    var selection = $('#order-type').find(":selected").text();
    if (selection === "From Selected Store") {
        request.storeId = $('#order-store').find(":selected").val();
    }

    $.ajax({
        type: "POST",
        url: "/sdm/pages/placeOrder",
        data: request,
        cache: false,
        timeout: 2000,
        error: function (error) {
            alert.error(error.responseText);
        },
        success: function (response) {
            order_id = response.orderId;
            var placeOrderModal = $('#placeOrderModal');
            placeOrderModal.modal('hide');
            placeOrderModal.find("input,textarea,select").val('').end();
            $('#t-body').html('');

            //todo- in case of dynamic display offer
            //todo handle discounts
            if (selection === "From Selected Store") {
                getDiscounts();
            } else {
                order_id = response.id;
                var dynamicOrderOfferModalBody = $('#dynamicOrderOfferModalBody');
                dynamicOrderOfferModalBody.empty();
                dynamicOrderOfferModalBody.append('<h3>This is the offer for your order</h3>');
                response.dynamicOrderEntityDTO.forEach(storeToOrderFrom => {
                    for (var x in storeToOrderFrom) {
                        if (x === "location") {

                            dynamicOrderOfferModalBody.append('<span> Location: (' + storeToOrderFrom[x]["xCoordinate"] + ',' + storeToOrderFrom[x]["yCoordinate"] + ')</span>' + '<br>');
                        } else {
                            var keyName = x.replace(/([A-Z])/g, ' $1').trim();
                            keyName = keyName.charAt(0).toUpperCase() + keyName.slice(1)
                            dynamicOrderOfferModalBody.append('<span>' + keyName + ': ' + storeToOrderFrom[x] + '</span>' + '<br>');
                        }
                    }
                    dynamicOrderOfferModalBody.append('<br>');
                })

                $('#dynamicOrderOfferModal').modal('show');
            }
        }

    });
}

function getDiscounts() {
    $('#dynamicOrderOfferModal').modal('hide');
    $('#selectDiscountsModal').modal('show');
    $.ajax({
        type: "GET",
        url: "/sdm/pages/discounts",
        data: {orderId: order_id},
        cache: false,
        timeout: 2000,
        error: function (error) {
            alert.error(error.responseText);
        },
        success: function (response) {
            var storesWithDiscounts = response.storeIdToValidDiscounts;
            $('#selectDiscountsModalBody').empty();
            storesWithDiscounts.forEach(function (store) {
                $('#selectDiscountsModalBody').append(
                    '<div class="panel-group">' +
                    '<div class="panel panel-default">' +
                    '<div class="panel-heading" role="tab" id="heading_' + store.storeId + '">' +
                    '<h4 class="panel-title">' +
                    '<a class="collapsed" data-toggle="collapse" data-parent="#discounts-accordion" href="#collapse_store_' + store.storeId + '_items_discounts" aria-expanded="false" aria-controls="#collapse_store_' + store.storeId + '_items_discounts">' +
                    store.storeName +
                    '</a>' +
                    '</h4>' +
                    '</div>' +
                    '<div id="collapse_store_' + store.storeId + '_items_discounts" class="panel-collapse collapse">' +
                    '<ul id="store_' + store.storeId + '_items_discounts" class="list-group">' +
                    '</ul>' +
                    '</div>' +
                    '</div>' +
                    '</div>');

            });
            storesWithDiscounts.forEach(function (store) {
                store.validItemsDiscounts.forEach(function (item) {
                    $('#store_' + store.storeId + '_items_discounts').append(
                        '<div class="panel-group">' +
                        '<div class="panel panel-default">' +
                        '<div class="panel-heading" role="tab">' +
                        '<h4 class="panel-title">' +
                        '<a class="collapsed" data-toggle="collapse" data-parent="#store_' + store.storeId + '_items_discounts" href="#collapse_store_' + store.storeId + '_item_' + item.itemId + '_discounts" aria-expanded="false" aria-controls="#collapse_store_' + store.storeId + '_item_' + item.itemId + '_discounts">' +
                        item.itemName +
                        '</a>' +
                        '</h4>' +
                        '</div>' +
                        '<div id="collapse_store_' + store.storeId + '_item_' + item.itemId + '_discounts" class="panel-collapse collapse">' +
                        '<ul id="store_' + store.storeId + '_item_' + item.itemId + '_discounts" class="list-group">' +
                        '</ul>' +
                        '</div>' +
                        '</div>' +
                        '</div>');
                });
                // });
                storesWithDiscounts.forEach(function (store) {
                    store.validItemsDiscounts.forEach(function (item) {
                        $('#store_' + store.storeId + '_item_' + item.itemId + '_discounts').empty();
                        item.validDiscounts.forEach(function (discount) {
                            var thenYouGet;
                            if (discount.operator === "ONE_OF") {
                                thenYouGet = '<select id="select_discount_' + discount.discountName + '_offer">' +
                                    '<option disabled selected value> -- select an option --</option>';
                                discount.offers.forEach(function (offer) {
                                    thenYouGet = thenYouGet + '<option id="' + offer.id + '">' + offer.quantity + ' ' + offer.offerItemName + ' ' + 'for additional ' + offer.forAdditional + '</option>';

                                })
                                thenYouGet = thenYouGet + '</select>';
                            } else {
                                thenYouGet = '<div>';
                                discount.offers.forEach(function (offer) {
                                    thenYouGet = thenYouGet + '<div>' + offer.quantity + ' ' + offer.offerItemName + ' ' + 'for additional ' + offer.forAdditional + '</div>';

                                })
                                thenYouGet = thenYouGet + '</div>';
                            }
                            $('#store_' + store.storeId + '_item_' + item.itemId + '_discounts').append(
                                '<ul class="list-group">' +
                                '<li class="list-group-item">' +
                                '<h4>Discount Name: ' + discount.discountName +
                                '</h4>' +
                                '</li>' +
                                '<li class="list-group-item">' +
                                'If You Buy:' + discount.ifYouBuyQuantity + ' ' + discount.ifYouBuyItemName +
                                '</li>' +
                                '<li class="list-group-item">' +
                                'Then You Get:' +
                                '<div class="form-group" >' +
                                thenYouGet +
                                '</div>' +
                                '</li>' +
                                '<li class="list-group-item">' +
                                '  <div class="form-group">' +
                                '<label for="discount_' + discount.discountName + '_amount">Number Of Redeems</label>' +
                                // <input id="x-coordinate" type="text" class="form-control" required="true">
                                '<input type="text" id="discount_' + discount.discountName + '_amount">' +
                                '</div>' +
                                '</li>' +
                                '</ul>' +
                                '<button type="button" class="btn" id="' + store.storeId + '_' + discount.discountName + '_' + item.itemId + '" onclick="addDiscount(id)">Add Discount'
                            )
                        });
                    });
                });

            });
        }
    });
}

function addDiscount(discount) {
    //todo fix bug offerId num of realizations
    var splitted = discount.split('_');
    var storeId = splitted[0];
    var discountName = splitted[1];
    var itemId = splitted[2];
    var orOfferId;
    try {

        var selectedOffer = $("#select_discount_" + discountName + "_offer");
        orOfferId = selectedOffer.find(":selected").text();
    } catch (error) {


    }
    var discountAmountInput = $("#discount_" + discountName + "_amount");
    var numOfRealizations = discountAmountInput.val();
    discounts.push({
        storeId: storeId,
        itemId: itemId,
        discountName: discountName,
        numOfRealizations: 2,
        orOfferId: 1
    })
    $('#selectDiscountsModalBody').find("input,textarea,select").val('').end();
    alert("Discount Added Successfully");
}

//todo - make a generic func that get the modal id

function submitDiscounts() {
    var request = {
        orderId: order_id,
        discountsCount: discounts.length,
        chosenDiscounts: discounts
    }

    $.ajax({
        type: "POST",
        data: request,
        url: "/sdm/pages/discounts",
        // timeout: 2000,
        error: function (error) {
            alert.error(error.responseText);
        },
        success: function (response) {
            $('#selectDiscountsModal').modal('hide');
            var orderSummaryModal = $('#orderSummaryModal');
            var orderSummaryModalBody = $('#orderSummaryModalBody');
            orderSummaryModalBody.empty();
            orderSummaryModalBody.append('<h3>The order Summary</h3>' +
                '<span>Items Price: ' + response.totalItemsPrice + '</span><br>' +
                '<span>Delivery Price: ' + response.totalDeliveryPrice + '</span><br>' +
                '<span>Total Price: ' + response.totalPrice + '</span><br>'
            );
            response.orderIncludedStoresDetails.forEach(store => {
                for (var x in store) {
                    if (x === "storePurchasedItems") {
//todo
//                             dynamicOrderOfferModalBody.append('<span> Location: (' + storeToOrderFrom[x]["xCoordinate"] + ',' + storeToOrderFrom[x]["yCoordinate"] + ')</span>' + '<br>');
                    } else {
                        var keyName = x.replace(/([A-Z])/g, ' $1').trim();
                        keyName = keyName.charAt(0).toUpperCase() + keyName.slice(1)
                        orderSummaryModalBody.append('<span>' + keyName + ': ' + store[x] + '</span>' + '<br>');
                    }
                }
                orderSummaryModalBody.append('<br>');
            })
            orderSummaryModal.modal('show');
        }
    });
}


function onModalClose() {
    $('#placeOrderModal').find("input,textarea,select").val('').end();
    $('#t-body').html('');
}