package ru.mirea.khrechko.fragmentapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

/** Host for the first Fragment practice module. Change STUDENT_LIST_NUMBER to the value from your group list. */
public class MainActivity extends AppCompatActivity {
    // TODO: replace 0 with your actual number in the student list.
    public static final int STUDENT_LIST_NUMBER = 25;
    public static final String ARG_STUDENT_NUMBER = "my_number_student";

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            Bundle args = new Bundle();
            args.putInt(ARG_STUDENT_NUMBER, STUDENT_LIST_NUMBER);
            TasksFragment fragment = new TasksFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container, fragment, "tasks")
                    .commit();
        }
    }
}
