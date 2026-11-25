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

import com.squareup.picasso.Picasso;

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
            String energy = product.getCalories() > 0 ? product.getCalories() + " ккал" : "Калорийность не указана";
            subtitle.setText(product.getBrand() + " · " + energy);
            String imageUrl = product.getImageUrl();
            if (imageUrl == null || imageUrl.trim().isEmpty()) {
                image.setImageResource(R.drawable.ic_package);
            } else {
                Picasso.get().load(imageUrl)
                        .placeholder(R.drawable.ic_package)
                        .error(R.drawable.ic_package)
                        .fit().centerCrop().into(image);
            }
        }
        return row;
    }
}
