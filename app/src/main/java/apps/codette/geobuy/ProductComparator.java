package apps.codette.geobuy;

import java.util.Comparator;

import apps.codette.forms.Product;
import apps.codette.geobuy.Constants.SortBy;

public class ProductComparator implements Comparator<Product> {

    private SortBy sort;
    public ProductComparator(SortBy sortBy){
        this.sort = sortBy;
    }


    @Override
    public int compare(Product product, Product product1) {
        int val=-1;
        Product pr1 = (product.getProductDetails() != null ? product.getProductDetails().get(0) : product);
        Product pr2 = (product1.getProductDetails() != null ? product1.getProductDetails().get(0) : product1);
        switch (sort){
            case RATING: {
                if(pr1.getRating() < pr2.getRating())
                    val = 1;
                if(pr1.getRating() == pr2.getRating())
                    val = 0;
                break;
            }
            case POPULARITY: {
                if(pr1.getGpriority() < pr2.getGpriority())
                    val = 1;
                if(pr1.getGpriority() == pr2.getGpriority())
                    val = 0;
                break;
            }
            case PRICEHIGHTOLOW: {
                if(pr1.getPrice() < pr2.getPrice())
                    val = 1;
                if(pr1.getPrice() == pr2.getPrice())
                    val = 0;

                break;
            }
            case PRICELOWTOHIGH: {
                if(pr1.getPrice() > pr2.getPrice())
                    val = 1;
                if(pr1.getPrice() == pr2.getPrice())
                    val = 0;
                break;
            }
        }

        return val;
    }
}
