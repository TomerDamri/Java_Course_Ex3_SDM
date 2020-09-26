package model.response;

import java.util.Iterator;
import java.util.List;

public class FinalSummaryForOrder {

    List<StoreSummaryForOrder> orderIncludedStoresDetails;

    public FinalSummaryForOrder (List<StoreSummaryForOrder> orderIncludedStoresDetails) {
        this.orderIncludedStoresDetails = orderIncludedStoresDetails;
    }

    public List<StoreSummaryForOrder> getOrderIncludedStoresDetails () {
        return orderIncludedStoresDetails;
    }

    @Override
    public String toString () {
        StringBuilder builder = new StringBuilder("Final order summary:\n");
        Iterator<StoreSummaryForOrder> iterator = orderIncludedStoresDetails.iterator();
        while (iterator.hasNext()) {
            StoreSummaryForOrder storeSummaryForOrder = iterator.next();
            builder.append("\n{" + storeSummaryForOrder + "}");
            if (iterator.hasNext()) {
                builder.append(",\n");
            }
        }
        builder.append("\n");

        return builder.toString();
    }
}
