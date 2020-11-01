function displayOrders() {
    var displayOrdersModalBody = $('#displayOrdersModalBody');
    $.ajax({
        type: "GET",
        url: "/sdm/pages/userOrders",
        // timeout: 2000,
        error: function (error) {
            alert(error.responseText);
        },
        success: function (response) {
            displayOrdersModalBody.empty();
            if (response.orders.length === 0) {
                alert("No Orders To Display");
            } else {
                response.orders.forEach(function (order) {
                    displayOrdersModalBody.append(
                        '<div class="panel-group">' +
                        '<div class="panel panel-default">' +
                        '<div class="panel-heading" role="tab" id="heading_' + order.orderID + '">' +
                        '<h4 class="panel-title">' +
                        '<a class="collapsed" data-toggle="collapse" data-parent="#displayOrdersModalBody" href="#collapse_parent_order_' + order.orderID + '" aria-expanded="false" aria-controls="#collapse_parent_order_' + order.orderID + '">' +
                        order.orderID +
                        '</a>' +
                        '</h4>' +
                        '</div>' +
                        '<div id="collapse_parent_order_' + order.orderID + '" class="panel-collapse collapse">' +
                        '<ul id="parent_order_' + order.orderID + '" class="list-group">' +
                        '</ul>' +
                        '</div>' +
                        '</div>' +
                        '</div>');

                });
                response.orders.forEach(function (order) {
                    order.subOrders.forEach(function (subOrder) {
                        $('#parent_order_' + order.orderID).append(
                            '<div class="panel-group">' +
                            '<div class="panel panel-default">' +
                            '<div class="panel-heading" role="tab">' +
                            '<h4 class="panel-title">' +
                            '<a class="collapsed" data-toggle="collapse" data-parent="#parent_order_' + order.orderID + '" href="#collapse_sub_order_' + subOrder.id + '" aria-expanded="false" aria-controls="#collapse_sub_order_' + subOrder.id + '">' +
                            'Sub Order:' + subOrder.id +
                            '</a>' +
                            '</h4>' +
                            '</div>' +
                            '<div id="collapse_sub_order_' + subOrder.id + '" class="panel-collapse collapse">' +
                            '<span>Store Id: ' + subOrder.storeId + '</span><br>' +
                            '<span>Store Name: ' + subOrder.storeName + '</span><br>' +
                            '<span>Order Date: ' + subOrder.orderDate.day + '/' + subOrder.orderDate.month + '/' + subOrder.orderDate.year + '</span><br>' +
                            '<span>Zone: ' + subOrder.zoneName + '</span><br>' +
                            '<span>Store Location: (' + subOrder["location"]["xCoordinate"] + ',' + subOrder["location"]["yCoordinate"] + ')' + '</span><br>' +
                            '<span>Items Types: ' + subOrder.numOfItemTypes + '</span><br>' +
                            '<span>Items Amount: ' + subOrder.amountOfItems + '</span><br>' +
                            '<span>Items Price: ' + subOrder.itemsPrice + '</span><br>' +
                            '<span>Delivery Price: ' + subOrder.deliveryPrice + '</span><br>' +
                            '<span>Total Price: ' + subOrder.totalPrice + '</span><br>' +
                            '</div>' +
                            '</div>');
                    });
                });
                $('#displayOrdersModal').modal('show');
            }
        }
    });

}