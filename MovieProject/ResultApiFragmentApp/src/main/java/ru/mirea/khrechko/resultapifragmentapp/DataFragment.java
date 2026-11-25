package ru.mirea.khrechko.resultapifragmentapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DataFragment extends Fragment {
    public static final String REQUEST_KEY = "requestKey";
    public static final String RESULT_KEY = "key";

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle state) {
        return inflater.inflate(R.layout.fragment_data, container, false);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        TextInputEditText input = view.findViewById(R.id.editTextInfo);
        view.findViewById(R.id.buttonOpenBottomSheet).setOnClickListener(v -> {
            String value = input.getText() == null ? "" : input.getText().toString().trim();
            if (TextUtils.isEmpty(value)) {
                Toast.makeText(requireContext(), "Введите сообщение", Toast.LENGTH_SHORT).show();
                return;
            }
            Bundle result = new Bundle();
            result.putString(RESULT_KEY, value);
            // The host FragmentManager is shared by this fragment and the Bottom Sheet.
            getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);
            new BottomSheetFragment().show(getParentFragmentManager(), "message_bottom_sheet");
        });
    }
}
