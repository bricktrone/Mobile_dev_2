package ru.mirea.khrechko.fragmentmanagerapp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/** Activity-scoped state shared by the country list and details fragments. */
public class CountrySelectionViewModel extends ViewModel {
    private final MutableLiveData<CountryInfo> selectedCountry = new MutableLiveData<>();

    public LiveData<CountryInfo> getSelectedCountry() { return selectedCountry; }
    public void select(CountryInfo country) { selectedCountry.setValue(country); }

    public static final class CountryInfo {
        public final String name;
        public final String emoji;
        public final String capital;
        public final String population;
        public final String description;

        public CountryInfo(String name, String emoji, String capital, String population, String description) {
            this.name = name;
            this.emoji = emoji;
            this.capital = capital;
            this.population = population;
            this.description = description;
        }
    }
}
