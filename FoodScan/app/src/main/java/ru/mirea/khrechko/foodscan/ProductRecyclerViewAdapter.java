package ru.mirea.khrechko.foodscan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import ru.mirea.khrechko.foodscan.domain.models.Product;

/** RecyclerView adapter for the mock product catalog supplied by the repository. */
public class ProductRecyclerViewAdapter extends RecyclerView.Adapter<ProductRecyclerViewAdapter.ProductViewHolder> {
    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    private final List<Product> items = new ArrayList<>();
    private final OnProductClickListener clickListener;

    public ProductRecyclerViewAdapter(OnProductClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setItems(List<Product> products) {
        items.clear();
        if (products != null) items.addAll(products);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = items.get(position);
        holder.name.setText(product.getName());
        holder.subtitle.setText(product.getBrand() + " · " + product.getCalories() + " ккал");
        Glide.with(holder.image)
                .load(product.getImageUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .centerCrop()
                .into(holder.image);
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) clickListener.onProductClick(product);
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        final ImageView image;
        final TextView name;
        final TextView subtitle;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageProduct);
            name = itemView.findViewById(R.id.textProductName);
            subtitle = itemView.findViewById(R.id.textProductSubtitle);
        }
    }
}
