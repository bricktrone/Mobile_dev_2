package ru.mirea.khrechko.recyclerviewapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = findViewById(R.id.recyclerEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new EventRecyclerViewAdapter(createEvents()));
    }

    private List<HistoricalEvent> createEvents() {
        List<HistoricalEvent> events = new ArrayList<>();
        events.add(new HistoricalEvent(1147, "Первое упоминание Москвы",
                "Летопись сообщает о встрече князей в Москве; эта дата считается первым письменным упоминанием города.", "ic_event_city"));
        events.add(new HistoricalEvent(1380, "Куликовская битва",
                "Войско Дмитрия Донского сражалось с силами Мамая; победа стала важным событием объединения русских земель.", "ic_event_shield"));
        events.add(new HistoricalEvent(1480, "Стояние на реке Угре",
                "Противостояние завершилось отходом ордынских войск и символизировало конец ордынской зависимости.", "ic_event_scroll"));
        events.add(new HistoricalEvent(1612, "Освобождение Москвы",
                "Народное ополчение под руководством Кузьмы Минина и Дмитрия Пожарского освободило Москву от интервентов.", "ic_event_flag"));
        events.add(new HistoricalEvent(1703, "Основание Санкт-Петербурга",
                "Пётр I заложил крепость на Заячьем острове; вокруг неё начал развиваться новый город.", "ic_event_building"));
        events.add(new HistoricalEvent(1812, "Отечественная война",
                "Война с армией Наполеона стала одним из ключевых событий российской истории XIX века.", "ic_event_star"));
        events.add(new HistoricalEvent(1861, "Отмена крепостного права",
                "Манифест Александра II объявил об освобождении крестьян от крепостной зависимости.", "ic_event_book"));
        events.add(new HistoricalEvent(1945, "Победа во Второй мировой войне",
                "В мае 1945 года в Европе завершились боевые действия, связанные с капитуляцией нацистской Германии.", "ic_event_medal"));
        events.add(new HistoricalEvent(1961, "Первый полёт человека в космос",
                "Юрий Гагарин совершил орбитальный полёт на корабле «Восток-1» 12 апреля 1961 года.", "ic_event_rocket"));
        events.add(new HistoricalEvent(1989, "Предложение Всемирной паутины",
                "Тим Бернерс-Ли предложил в CERN систему обмена связанными документами, ставшую основой World Wide Web.", "ic_event_web"));
        return events;
    }
}
