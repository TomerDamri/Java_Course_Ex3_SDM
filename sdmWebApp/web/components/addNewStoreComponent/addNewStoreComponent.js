var availableItems;
var selected_zone_name;

function insertItemsToModal() {
    $('#t-body').html('');
    selected_zone_name = window.localStorage.getItem('zoneName');
    $.ajax({
        type: "GET",
        enctype: 'multipart/form-data',
        url: "/sdm/pages/sellingZones/selectedZone",
        timeout: 2000,
        error: function (error) {
            alert(error.responseText);
        },
        success: function (response) {
            var tbody = $('#t-body');
            availableItems = response.items;
            tbody.empty();
            response.items.forEach(function (item) {
                tbody.append(
                    '<tr>' +
                    '<td>' + item.id + '</td>' +
                    '<td>' + item.name + '</td>' +
                    '<td>' + item.purchaseCategory + '</td>' +
                    '<td><input type="text" id="item_' + item.id + '_price"></td>' +
                    '</tr>');
            });
        }
    });
}

function addStore() {
    var request;
    var storeName = $('#store-name').val();
    var xCoordinate = $('#store-x-coordinate').val();
    var yCoordinate = $('#store-y-coordinate').val();
    var deliveryPpk = $('#store-ppk').val();
    var items = [];
    availableItems.forEach(function (item) {
        var itemPrice = $('#item_' + item.id + '_price').val();
        if (itemPrice && itemPrice > 0) {
            items.push({
                itemId: item.id,
                price: itemPrice
            });
        }
    });
    request = {
        storeOwnerId: window.localStorage.getItem('userId'),
        storeName: storeName,
        zoneName: selected_zone_name,
        xCoordinate: xCoordinate,
        yCoordinate: yCoordinate,
        deliveryPpk: deliveryPpk,
        storeItems: items,
        itemsCount: items.length,
    }

    $.ajax({
        type: "POST",
        data: request,
        url: "/sdm/pages/addStore",
        timeout: 2000,
        error: function (error) {
            alert(error.responseText);
        },
        success: function (response) {
            alert("Store added successfully to'" + selected_zone_name + "'");
            $('#newStoreModal').find("input,textarea,select").val('').end();
            location.reload();
        }
    });
}