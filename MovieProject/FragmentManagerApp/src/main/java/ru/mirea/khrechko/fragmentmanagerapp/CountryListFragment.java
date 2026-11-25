package ru.mirea.khrechko.fragmentmanagerapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Arrays;
import java.util.List;

public class CountryListFragment extends Fragment {
    private final List<CountrySelectionViewModel.CountryInfo> countries = Arrays.asList(
            new CountrySelectionViewModel.CountryInfo("Россия", "🇷🇺", "Москва", "около 146 млн", "Крупнейшее по площади государство мира. Столица — Москва. Россия расположена в Восточной Европе и Северной Азии."),
            new CountrySelectionViewModel.CountryInfo("Япония", "🇯🇵", "Токио", "около 124 млн", "Островное государство в Восточной Азии. Известно современной технологической индустрией и богатой культурой."),
            new CountrySelectionViewModel.CountryInfo("Франция", "🇫🇷", "Париж", "около 68 млн", "Государство в Западной Европе. Столица Париж является одним из крупнейших культурных центров мира."),
            new CountrySelectionViewModel.CountryInfo("Бразилия", "🇧🇷", "Бразилиа", "около 203 млн", "Крупнейшая страна Южной Америки, на территории которой находится значительная часть Амазонии."),
            new CountrySelectionViewModel.CountryInfo("Канада", "🇨🇦", "Оттава", "около 41 млн", "Государство в Северной Америке с обширной территорией и большим количеством озёр."));

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle state) {
        return inflater.inflate(R.layout.fragment_country_list, container, false);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        CountrySelectionViewModel selection = new ViewModelProvider(requireActivity()).get(CountrySelectionViewModel.class);
        RecyclerView recycler = view.findViewById(R.id.recyclerCountries);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        recycler.setAdapter(new CountryAdapter(countries, country -> {
            selection.select(country);
            getParentFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_container, new CountryDetailsFragment(), "country_details")
                    .addToBackStack("country_details")
                    .commit();
        }));
    }

    interface OnCountryClick { void onClick(CountrySelectionViewModel.CountryInfo country); }
    static final class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.Holder> {
        private final List<CountrySelectionViewModel.CountryInfo> items; private final OnCountryClick click;
        CountryAdapter(List<CountrySelectionViewModel.CountryInfo> items, OnCountryClick click) { this.items=items; this.click=click; }
        @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_country,parent,false));
        }
        @Override public void onBindViewHolder(@NonNull Holder holder, int position) {
            CountrySelectionViewModel.CountryInfo country=items.get(position);
            holder.emoji.setText(country.emoji); holder.name.setText(country.name);
            holder.capital.setText("Столица: " + country.capital);
            holder.itemView.setOnClickListener(v -> click.onClick(country));
        }
        @Override public int getItemCount(){return items.size();}
        static class Holder extends RecyclerView.ViewHolder {
            final TextView emoji,name,capital;
            Holder(@NonNull View view){super(view);emoji=view.findViewById(R.id.textCountryEmoji);name=view.findViewById(R.id.textCountryName);capital=view.findViewById(R.id.textCountryCapital);}
        }
    }
}
