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

public class CountryDetailsFragment extends Fragment {
    private TextView emoji, name, capital, population, description;

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle state) {
        return inflater.inflate(R.layout.fragment_country_details, container, false);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        emoji = view.findViewById(R.id.textCountryEmoji);
        name = view.findViewById(R.id.textCountryName);
        capital = view.findViewById(R.id.textCountryCapital);
        population = view.findViewById(R.id.textCountryPopulation);
        description = view.findViewById(R.id.textCountryDescription);

        CountrySelectionViewModel selection = new ViewModelProvider(requireActivity()).get(CountrySelectionViewModel.class);
        selection.getSelectedCountry().observe(getViewLifecycleOwner(), country -> {
            if (country == null) return;
            emoji.setText(country.emoji);
            name.setText(country.name);
            capital.setText("Столица: " + country.capital);
            population.setText("Население: " + country.population);
            description.setText(country.description);
        });
        view.findViewById(R.id.buttonBack).setOnClickListener(v -> getParentFragmentManager().popBackStack());
    }
}
