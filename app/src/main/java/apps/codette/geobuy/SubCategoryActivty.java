package apps.codette.geobuy;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import apps.codette.forms.Category;
import apps.codette.geobuy.adapters.SubCategoryAdapter;

public class SubCategoryActivty extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Intent intent = getIntent();
        String categoryJson = intent.getStringExtra("category");
        Gson gson = new Gson();
        Type  type = new TypeToken<Category>() {}.getType();
        Category category = gson.fromJson(categoryJson, type);
        toolbar.setTitle(category.getTittle());
        setTitle(category.getTittle());
        if(category.getSubcategory() != null && !category.getSubcategory().isEmpty()) {
            SubCategoryAdapter subCategoryAdapter = new SubCategoryAdapter(this, category.getSubcategory());
            RecyclerView recyclerView = findViewById(R.id.category_recycler_view);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            recyclerView.setAdapter( subCategoryAdapter );
        }
    }



}
