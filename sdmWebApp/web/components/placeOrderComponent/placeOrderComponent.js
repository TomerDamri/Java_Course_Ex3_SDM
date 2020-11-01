var order_id;
var discounts = [];
var storeRanks = [];
var orderSummary;

function onOrderTypeSelect() {
    $('#t-body').html('');
    $.ajax({
        type: "GET",
        enctype: 'multipart/form-data',
        url: "/sdm/pages/sellingZones/selectedZone",
        timeout: 2000,
        error: function (error) {
            alert(error.responseText);
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
            alert(error.responseText);
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

    availableItems.forEach(function (item) {
        var itemAmount = $('#item_' + item.id + '_amount').val();
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
            alert(error.responseText);
        },
        success: function (response) {
            order_id = response.orderId;
            var placeOrderModal = $('#placeOrderModal');
            placeOrderModal.modal('hide');
            placeOrderModal.find("input,textarea,select").val('').end();
            $('#t-body').html('');
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
    $.ajax({
        type: "GET",
        url: "/sdm/pages/discounts",
        data: {orderId: order_id},
        cache: false,
        timeout: 2000,
        error: function (error) {
            alert(error.responseText);
        },
        success: function (response) {
            var storesWithDiscounts = response.storeIdToValidDiscounts;
            if (storesWithDiscounts.length === 0) {
                alert("No Discounts For Your Order");
                submitDiscounts();
            } else {
                $('#selectDiscountsModal').modal('show');
                $('#selectDiscountsModalBody').empty();
                $('#selectDiscountsModalBody').append('<h4>Select Discounts For Your Order</h4><br><h3>Press Submit When Finished</h3>');
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
                                        thenYouGet = thenYouGet + '<option id="' + offer.id + '" value="' + offer.id + '">' + offer.quantity + ' ' + offer.offerItemName + ' ' + 'for additional ' + offer.forAdditional + '</option>';

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
        }
    });
}

function addDiscount(discount) {
    var splitted = discount.split('_');
    var storeId = splitted[0];
    var discountName = splitted[1];
    var itemId = splitted[2];
    var orOfferId;
    var selectedId = "select_discount_" + discountName + "_offer";
    var select = document.getElementById(selectedId);
    if (select != null) {
        var option = select.options[select.selectedIndex];
        orOfferId = option.attributes.id.nodeValue;
    }
    var inputId = "discount_" + discountName + "_amount";
    var numOfRealizations = document.getElementById(inputId).value;
    discounts.push({
        storeId: storeId,
        itemId: itemId,
        discountName: discountName,
        numOfRealizations: numOfRealizations,
        orOfferId: orOfferId
    })
    $('#selectDiscountsModalBody').find("input,textarea,select").val('').end();
    alert("Discount Added Successfully");
}

function submitDiscounts() {
    var orderSummaryModalBody = $('#orderSummaryModalBody');
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
            alert(error.responseText);
        },
        success: function (response) {
            orderSummary = response;
            $('#selectDiscountsModal').modal('hide');
            orderSummaryModalBody.empty();
            orderSummaryModalBody.append(
                '<h3>Press Submit To Confirm The Order</h3>' +
                '<h4>Items Price: ' + response.totalItemsPrice + '</h4><br>' +
                '<h4>Delivery Price: ' + response.totalDeliveryPrice + '</h4><br>' +
                '<h4>Total Price: ' + response.totalPrice + '</h4><br><br>');
            response.orderIncludedStoresDetails.forEach(store => {
                orderSummaryModalBody.append(
                    '<div class="panel-group">' +
                    '<div class="panel panel-default">' +
                    '<div class="panel-heading" role="tab">' +
                    '<h4 class="panel-title">' +
                    '<a class="collapsed" data-toggle="collapse" data-parent="#orderSummaryModalBody" href="#collapse_order_store' + store.id + '" aria-expanded="false" aria-controls="#collapse_order_store' + store.id + '">' +
                    store.name +
                    '</a>' +
                    '</h4>' +
                    '</div>' +
                    '<div id="collapse_order_store' + store.id + '" class="panel-collapse collapse">' +
                    '<ul id="order_store' + store.id + '" class="list-group">');
                for (var x in store) {
                    if (x === "storePurchasedItems") {
                        $("#collapse_order_store" + store.id).append('<br><span> Order Store Items: </span>' + '<br><br>');
                        store[x].forEach(purchasedItem => {
                            $("#collapse_order_store" + store.id).append(
                                '<span>Item Id: ' + purchasedItem.id + '</span><br>' +
                                '<span>Item Name: ' + purchasedItem.name + '</span><br>' +
                                '<span>Purchase Category: ' + purchasedItem.purchaseCategory + '</span><br>' +
                                '<span>Unit Price: ' + purchasedItem.unitPrice + '</span><br>' +
                                '<span>Purchased Quantity: ' + purchasedItem.quantity + '</span><br>' +
                                '<span>Total Price: ' + purchasedItem.totalPrice + '</span><br>' +
                                '<span>Purchased As Part Of Discount: ' + purchasedItem.isPurchasedAsPartOfDiscount + '</span><br><br>'
                            );
                        });
                    } else {
                        var keyName = x.replace(/([A-Z])/g, ' $1').trim();
                        keyName = keyName.charAt(0).toUpperCase() + keyName.slice(1)
                        $("#collapse_order_store" + store.id).append('<span>' + keyName + ': ' + store[x] + '</span>' + '<br>');
                    }
                }
                orderSummaryModalBody.append(
                    '</ul>' +
                    '</div>' +
                    '</div>' +
                    '</div>');

            });
            $('#orderSummaryModal').modal('show');
        }
    });
    discounts = [];
}

function confirmOrder(confirm) {
    var request = {
        orderId: order_id,
        confirmOrder: confirm
    }
    $.ajax({
        type: "GET",
        data: request,
        url: "/sdm/pages/placeOrder",
        // timeout: 2000,
        error: function (error) {
            alert(error.responseText);
        },
        success: function (response) {
            if (confirm) {
                alert("Order Created Successfully");
                rateOrder();
            } else {
                alert("Order Is Cancelled");
            }
        }
    })
}

function rateOrder() {
    $('#rateOrderModalBody').append('<p>Rate your order stores or press "Close" to skip</p>');
    orderSummary.orderIncludedStoresDetails.forEach(store => {
        $('#rateOrderModalBody').append(
            '<div class="panel-group">' +
            '<div class="panel panel-default">' +
            '<div class="panel-heading" role="tab" id="heading_' + store.storeId + '">' +
            '<h4 class="panel-title">' +
            '<a class="collapsed" data-toggle="collapse" data-parent="#rateOrderModalBody" href="#collapse_store_' + store.id + '_rate" aria-expanded="false" aria-controls="#collapse_store_' + store.id + '_rate">' +
            store.name +
            '</a>' +
            '</h4>' +
            '</div>' +
            '<div id="collapse_store_' + store.id + '_rate" class="panel-collapse collapse">' +
            '<label for="store_' + store.id + 'text_rate">Insert Textual Feedback:  </label><br>' +
            '<input type="text" id="store_' + store.id + 'text_rate"><br>' +
            '<label for="store_' + store.id + '_rank">Rank Store:  </label><br>' +
            '<select id="select_store_' + store.id + '_rank">' +
            '<option disabled selected value> -- select an option --</option>' +
            '<option>1</option>' +
            '<option>2</option>' +
            '<option>3</option>' +
            '<option>4</option>' +
            '<option>5</option>' +
            '</select>' +
            '<button type="button" class="btn" id="store_' + store.id + '_rate" onclick="addRate(id)">Add Rate</button>' +
            '</div>' +
            '</div>' +
            '</div>');
    });
    $('#rateOrderModal').modal('show');
}

function addRate(store) {
    var storeId = store.split('_')[1];
    var selectedId = "select_store_" + storeId + "_rank";
    var select = document.getElementById(selectedId);
    var rank;
    if (select != null) {
        var option = select.options[select.selectedIndex];
        rank = option.value;
    }
    var inputId = 'store_' + storeId + 'text_rate';
    var textualFeedback = document.getElementById(inputId).value;
    storeRanks.push({
        storeId: storeId,
        rank: rank,
        textualFeedback: textualFeedback
    });
    alert("Feedback Created");
    $('#rateOrderModalBody').find("input,textarea,select").val('').end();
}

function submitRateOrder() {
    var request = {
        orderId: order_id,
        zoneName: selected_zone_name,
        ranksCount: storeRanks.length,
        storeRanks: storeRanks
    }
    $.ajax({
        type: "POST",
        data: request,
        url: "/sdm/pages/rateStore",
        // timeout: 2000,
        error: function (error) {
            alert(error.responseText);
        },
        success: function (response) {
            alert("All Feedbacks Added Successfully");
        }
    })
    storeRanks = [];
}

function onModalClose() {
    $('#placeOrderModal').find("input,textarea,select").val('').end();
    $('#t-body').html('');
}
