function displayFeedbacks() {
    var displayFeedbackModalBody = $('#displayFeedbackModalBody');
    $.ajax({
        type: "GET",
        data: {zoneName: window.localStorage.getItem("zoneName")},
        url: "/sdm/pages/rateStore",
        // timeout: 2000,
        error: function (error) {
            alert(error.responseText);
        },
        success: function (response) {
            displayFeedbackModalBody.empty();
            response.orderStoreRanks.forEach(function (store) {
                displayFeedbackModalBody.append(
                    '<div class="panel-group">' +
                    '<div class="panel panel-default">' +
                    '<div class="panel-heading" role="tab" id="heading_' + store.storeId + '">' +
                    '<h4 class="panel-title">' +
                    '<a class="collapsed" data-toggle="collapse" data-parent="#displayFeedbackModalBody"" href="#collapse_store_' + store.storeId + '_feedbacks" aria-expanded="false" aria-controls="#collapse_store_' + store.storeId + '_feedbacks">' +
                    store.storeName +
                    '</a>' +
                    '</h4>' +
                    '</div>' +
                    '<div id="collapse_store_' + store.storeId + '_feedbacks" class="panel-collapse collapse">' +
                    '<ul id="store_' + store.storeId + '_feedbacks" class="list-group">' +
                    '</ul>' +
                    '</div>' +
                    '</div>' +
                    '</div>');

            });
            response.orderStoreRanks.forEach(function (store) {
                store.customersFeedbacks.forEach(function (feedback) {
                    $('#store_' + store.storeId + '_feedbacks').append(
                        '<div class="panel-group">' +
                        '<div class="panel panel-default">' +
                        '<div class="panel-heading" role="tab">' +
                        '<h4 class="panel-title">' +
                        '<a class="collapsed" data-toggle="collapse" data-parent="#store_' + store.storeId + '_feedbacks" href="#collapse_store_feedback_' + feedback.feedbackId + '" aria-expanded="false" aria-controls="#collapse_store_feedback_' + feedback.feedbackId + '">' +
                        feedback.feedbackId +
                        '</a>' +
                        '</h4>' +
                        '</div>' +
                        '<div id="collapse_store_feedback_' + feedback.feedbackId + '" class="panel-collapse collapse">' +
                        '<span>Customer Name: ' + feedback.customerName + '</span><br>' +
                        '<span>Rank: ' + feedback.rank + '</span><br>' +
                        '<span>Feedback: ' + feedback.textualFeedback + '</span><br>' +
                        '<span>Order Date: ' + feedback.orderDate.day + '/' + feedback.orderDate.month + '/' + feedback.orderDate.year + '</span><br>' +
                        '</div>' +
                        '</div>');
                });
            });
            $('#displayFeedbackModal').modal('show');
        }
    });

}