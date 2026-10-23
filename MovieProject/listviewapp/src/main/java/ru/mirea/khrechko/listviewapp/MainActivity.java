package ru.mirea.khrechko.listviewapp;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<Book> books = createBooks();
        ListView booksList = findViewById(R.id.booksList);
        ArrayAdapter<Book> adapter = new ArrayAdapter<Book>(this,
                android.R.layout.simple_list_item_2, android.R.id.text1, books) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View row = super.getView(position, convertView, parent);
                TextView title = row.findViewById(android.R.id.text1);
                TextView author = row.findViewById(android.R.id.text2);
                Book book = getItem(position);
                if (book != null) {
                    title.setText(String.format(Locale.getDefault(), "%d. %s", position + 1, book.getTitle()));
                    author.setText(book.getAuthor());
                }
                return row;
            }
        };
        booksList.setAdapter(adapter);
        booksList.setOnItemClickListener((parent, view, position, id) -> {
            Book selected = books.get(position);
            Toast.makeText(this, selected.getTitle() + " — " + selected.getAuthor(), Toast.LENGTH_SHORT).show();
        });
    }

    private List<Book> createBooks() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("Мастер и Маргарита", "Михаил Булгаков"));
        books.add(new Book("Преступление и наказание", "Фёдор Достоевский"));
        books.add(new Book("Идиот", "Фёдор Достоевский"));
        books.add(new Book("Война и мир", "Лев Толстой"));
        books.add(new Book("Анна Каренина", "Лев Толстой"));
        books.add(new Book("Отцы и дети", "Иван Тургенев"));
        books.add(new Book("Обломов", "Иван Гончаров"));
        books.add(new Book("Мёртвые души", "Николай Гоголь"));
        books.add(new Book("Ревизор", "Николай Гоголь"));
        books.add(new Book("Евгений Онегин", "Александр Пушкин"));
        books.add(new Book("Капитанская дочка", "Александр Пушкин"));
        books.add(new Book("Герой нашего времени", "Михаил Лермонтов"));
        books.add(new Book("Доктор Живаго", "Борис Пастернак"));
        books.add(new Book("Тихий Дон", "Михаил Шолохов"));
        books.add(new Book("Мы", "Евгений Замятин"));
        books.add(new Book("1984", "Джордж Оруэлл"));
        books.add(new Book("Скотный двор", "Джордж Оруэлл"));
        books.add(new Book("О дивный новый мир", "Олдос Хаксли"));
        books.add(new Book("451 градус по Фаренгейту", "Рэй Брэдбери"));
        books.add(new Book("Марсианские хроники", "Рэй Брэдбери"));
        books.add(new Book("Дюна", "Фрэнк Герберт"));
        books.add(new Book("Солярис", "Станислав Лем"));
        books.add(new Book("Левая рука тьмы", "Урсула Ле Гуин"));
        books.add(new Book("Автостопом по галактике", "Дуглас Адамс"));
        books.add(new Book("Властелин колец", "Дж. Р. Р. Толкин"));
        books.add(new Book("Хоббит", "Дж. Р. Р. Толкин"));
        books.add(new Book("Сто лет одиночества", "Габриэль Гарсиа Маркес"));
        books.add(new Book("Любовь во время чумы", "Габриэль Гарсиа Маркес"));
        books.add(new Book("Имя розы", "Умберто Эко"));
        books.add(new Book("Парфюмер", "Патрик Зюскинд"));
        books.add(new Book("Цветы для Элджернона", "Дэниел Киз"));
        books.add(new Book("Убить пересмешника", "Харпер Ли"));
        books.add(new Book("Над пропастью во ржи", "Джером Д. Сэлинджер"));
        books.add(new Book("Пикник на обочине", "Аркадий и Борис Стругацкие"));
        books.add(new Book("Трудно быть богом", "Аркадий и Борис Стругацкие"));
        return books;
    }
}
