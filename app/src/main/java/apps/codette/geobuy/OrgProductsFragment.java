package apps.codette.geobuy;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import apps.codette.forms.Product;
import apps.codette.geobuy.Constants.OrgService;
import apps.codette.geobuy.adapters.ProductAdapter;

public class OrgProductsFragment  extends Fragment {

    View rootView;

    List<Product> products;
    EditText products_search;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
          //  products = (List<Product>) getArguments().getSerializable("products");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_business_products, container, false);

        products_search = rootView.findViewById(R.id.products_search);
        products_search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String search = String.valueOf(s);
                filterSearchText(search);
            }
        });
        products = OrgService.organization.getProducts();
        updateRecyclerView(products);

        return rootView;
    }

    private void filterSearchText(String search) {
        Log.i("search", search);
        if(search != null && !search.isEmpty()){
            List<Product> filteredProducts = new ArrayList<>();
            for(Product product : products){
                if(product.getTitle() !=null && product.getTitle().toLowerCase().contains(search.toLowerCase())) {
                    filteredProducts.add(product);
                }   else if(product.getSearchkey() != null && product.getSearchkey().toLowerCase().contains(search.toLowerCase())) {
                    filteredProducts.add(product);
                }
            }
            updateRecyclerView(filteredProducts);
        } else {
            updateRecyclerView(products);
        }
    }

    private void updateRecyclerView(List<Product> products) {
        RecyclerView recyclerView = rootView.findViewById(R.id.products_recyclerview);
        if(products != null){
            ProductAdapter rbP = new ProductAdapter(this.getContext(), products,true, null);
            LinearLayoutManager ll = new LinearLayoutManager(this.getContext());
            recyclerView.setLayoutManager(ll);
            recyclerView.setAdapter(rbP);
            rbP.notifyDataSetChanged();
        }
    }

    private void toast(String s) {
        Toast.makeText(this.getContext(), ""+s, Toast.LENGTH_SHORT).show();
    }
}
