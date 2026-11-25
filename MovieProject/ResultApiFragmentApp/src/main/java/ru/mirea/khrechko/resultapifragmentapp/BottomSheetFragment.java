package ru.mirea.khrechko.resultapifragmentapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetFragment extends BottomSheetDialogFragment {
    private String receivedText = "Ожидание сообщения…";

    @Override public void onCreate(@Nullable Bundle state) {
        super.onCreate(state);
        getParentFragmentManager().setFragmentResultListener(
                DataFragment.REQUEST_KEY, this, (requestKey, bundle) -> {
                    String value = bundle.getString(DataFragment.RESULT_KEY, "");
                    receivedText = value;
                    if (getView() != null) {
                        TextView target = getView().findViewById(R.id.textReceivedData);
                        if (target != null) target.setText(value);
                    }
                });
    }

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle state) {
        View view = inflater.inflate(R.layout.fragment_bottom_sheet, container, false);
        ((TextView)view.findViewById(R.id.textReceivedData)).setText(receivedText);
        return view;
    }
}
