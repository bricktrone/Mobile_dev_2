package ru.mirea.khrechko.foodscan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.List;

import ru.mirea.khrechko.foodscan.domain.models.Product;

public class ProductAdapter extends ArrayAdapter<Product> {
    private final LayoutInflater inflater;

    public ProductAdapter(@NonNull Context context, @NonNull List<Product> products) {
        super(context, 0, products);
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        if (row == null) row = inflater.inflate(R.layout.item_product, parent, false);
        Product product = getItem(position);
        ImageView image = row.findViewById(R.id.imageProduct);
        TextView name = row.findViewById(R.id.textProductName);
        TextView subtitle = row.findViewById(R.id.textProductSubtitle);
        if (product != null) {
            name.setText(product.getName());
            subtitle.setText(product.getBrand() + " · " + product.getCalories() + " ккал");
            Glide.with(getContext()).load(product.getImageUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
                    .centerCrop().into(image);
        }
        return row;
    }
}
