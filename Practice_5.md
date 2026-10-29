# Практическая работа № 5
## Retrofit и загрузка изображений с Picasso

**Дисциплина:** Разработка мобильных приложений  
**Язык реализации:** Java  
**Проекты:** `MovieProject` и `FoodScan`  
**Студент:** Хречко Роман    
**Группа:** БСБО-09-23

---

## Содержание

- [Что требовалось](#что-требовалось)
- [1. MovieProject: RetrofitApp](#1-movieproject-retrofitapp)
- [2. Обновление дела через PUT](#2-обновление-дела-через-put)
- [3. Загрузка и настройка изображений Picasso](#3-загрузка-и-настройка-изображений-picasso)
- [4. Обработка сетевых ошибок](#4-обработка-сетевых-ошибок)
- [5. FoodScan: Retrofit и Picasso](#5-foodscan-retrofit-и-picasso)
- [Проверка реализации](#проверка-реализации)
- [Соответствие методичке](#соответствие-методичке)

---

## Что требовалось

| Часть | Требование | Реализация |
|---|---|---|
| MovieProject | Создать модуль `RetrofitApp`, получать список дел и показывать статус выполнения | `retrofitapp` — Retrofit, Gson, RecyclerView и CheckBox |
| MovieProject | При изменении CheckBox отправлять запрос на обновление дела | `ApiService.updateTodo()` с HTTP PUT |
| MovieProject | Загружать несколько изображений из интернета и дать возможность менять способ отображения | Picasso, Spinner с четырьмя режимами |
| MovieProject | Обрабатывать сетевые ошибки и неуспешные HTTP-ответы | Отдельный блок ошибки и кнопка «Повторить» |
| FoodScan | Получать сущности из сети через Retrofit | Open Food Facts API в `NetworkApi` |
| FoodScan | Обрабатывать возможные ошибки сети/API | Понятное сообщение о проблеме и повторная загрузка |
| FoodScan | Отображать изображения полученных сущностей с помощью библиотеки загрузки изображений | Picasso в списке и карточке продукта |

Структура основных каталогов сохранена. Новый учебный модуль подключён в `MovieProject/settings.gradle.kts` как `:retrofitapp`.

---

## 1. MovieProject: RetrofitApp

Новый модуль находится в `MovieProject/retrofitapp/`, пакет — `ru.mirea.khrechko.retrofitapp`. В качестве сервиса используется [JSONPlaceholder](https://jsonplaceholder.typicode.com/), возвращающий список дел в JSON.

Основные файлы:

```text
MovieProject/retrofitapp/src/main/
├── AndroidManifest.xml
├── java/ru/mirea/khrechko/retrofitapp/
│   ├── ApiService.java
│   ├── CircleTransformation.java
│   ├── MainActivity.java
│   ├── Todo.java
│   ├── TodoAdapter.java
│   └── TodoViewHolder.java
└── res/
    ├── layout/activity_main.xml
    ├── layout/item_todo.xml
    └── values/strings.xml
```

В `retrofitapp/build.gradle.kts` добавлены Retrofit `2.11.0`, Gson-конвертер `2.11.0`, Picasso `2.8`, RecyclerView и CardView. В манифесте установлен `android.permission.INTERNET`.

### Модель Todo

Класс `Todo` представляет JSON-объект. Gson заполняет поля `userId`, `id`, `title` и `completed`; дополнительные геттеры и `setCompleted()` используются адаптером и экраном.

```java
public class Todo {
    @SerializedName("userId") private int userId;
    @SerializedName("id") private int id;
    @SerializedName("title") private String title;
    @SerializedName("completed") private boolean completed;

    public int getUserId() { return userId; }
    public int getId() { return id; }
    public String getTitle() { return title == null ? "Без названия" : title; }
    public boolean getCompleted() { return completed; }
    public void setCompleted(boolean value) { completed = value; }

    public String getImageUrl() {
        return "https://picsum.photos/seed/todo" + id + "/300/300";
    }
}
```

URL изображения формируется на основе идентификатора дела. Поэтому элементы загружают несколько разных изображений, а Picasso может кэшировать их по URL.

### Интерфейс запросов

```java
public interface ApiService {
    @GET("todos")
    Call<List<Todo>> getTodos();

    @PUT("todos/{id}")
    Call<Todo> updateTodo(@Path("id") int id, @Body Todo todo);
}
```

`@GET("todos")` запрашивает список дел, а `@PUT("todos/{id}")` передаёт обновлённый объект на адрес конкретного дела.

### Получение списка

В `MainActivity` создаётся Retrofit-клиент с базовым адресом `https://jsonplaceholder.typicode.com/`, после чего интерфейс запросов создаётся методом `retrofit.create(ApiService.class)`. Запрос выполняется асинхронно через `enqueue()`, чтобы не блокировать основной поток интерфейса.

При успешном ответе список передаётся адаптеру RecyclerView. Каждая строка содержит изображение, название дела, идентификатор, пользователя и CheckBox со статусом выполнения.

![1.png](Images%20for%20repots/5/1.png)     
*Рисунок 1 — Список дел, полученный с помощью Retrofit и отображённый в RecyclerView.*

---

## 2. Обновление дела через PUT

При нажатии на CheckBox адаптер вызывает обработчик `MainActivity.updateTodo()`. Перед установкой слушателя значение CheckBox сбрасывается, а сам слушатель устанавливается после привязки текущего состояния. Проверка `buttonView.isPressed()` позволяет не отправлять запросы из-за повторного использования ViewHolder при прокрутке.

В обработчике сохраняется старое значение статуса, устанавливается новое и выполняется `apiService.updateTodo(todo.getId(), todo)`. При успешном ответе приложение показывает Toast с идентификатором дела и его статусом. Если запрос завершается сетевой ошибкой либо сервер возвращает неуспешный HTTP-код, старое значение восстанавливается, а пользователю показывается сообщение об ошибке.

Важно: JSONPlaceholder является демонстрационным API. Успешный ответ на PUT подтверждает обработку запроса сервисом, но не означает постоянного сохранения изменений в удалённой базе.

![2.png](Images%20for%20repots/5/2.png)     
*Рисунок 2 — Результат отправки PUT-запроса после изменения статуса дела.*

---

## 3. Загрузка и настройка изображений Picasso

В верхней части экрана размещён Spinner с четырьмя режимами отображения:

| Режим | Настройка Picasso |
|---|---|
| Обычные | `fit().centerInside()` |
| Обрезка centerCrop | `fit().centerCrop()` |
| Круглые | `fit().centerCrop().transform(new CircleTransformation())` |
| Маленькие 48 dp | `resize(48, 48).centerInside()` |

Для загрузки используется конструкция `Picasso.get().load(url)`. Метод `placeholder()` задаёт системную заглушку на время загрузки, а `error()` — изображение, которое отображается при ошибке. Для круглого режима создана трансформация `CircleTransformation`, реализующая интерфейс Picasso `Transformation`.

```java
RequestCreator request = Picasso.get().load(todo.getImageUrl())
        .placeholder(android.R.drawable.ic_menu_gallery)
        .error(android.R.drawable.ic_dialog_alert);

switch (imageMode) {
    case MODE_CENTER_CROP:
        request.fit().centerCrop();
        break;
    case MODE_CIRCLE:
        request.fit().centerCrop().transform(new CircleTransformation());
        break;
    case MODE_SMALL:
        request.resize(48, 48).centerInside();
        break;
    default:
        request.fit().centerInside();
}
request.into(holder.imageView);
```
![3.png](Images%20for%20repots/5/3.png)     
*Рисунок 3 — Отображение изображений дел в режиме centerCrop.*  
![4.png](Images%20for%20repots/5/4.png)     
*Рисунок 4 — Круглые изображения, полученные с помощью `CircleTransformation`.*  
![5.png](Images%20for%20repots/5/5.png)     
*Рисунок 5 — Уменьшенные изображения размером 48 dp.*

---

## 4. Обработка сетевых ошибок

Экран `RetrofitApp` имеет три состояния:

1. **Загрузка:** отображается ProgressBar.
2. **Успех:** отображается RecyclerView со списком дел.
3. **Ошибка:** выводится текст ошибки и кнопка «Повторить».

Если запрос не удаётся выполнить из-за отключённого интернета, проблем DNS или иной ошибки соединения, Retrofit вызывает `onFailure()`. Если сервер ответил с HTTP-кодом вне диапазона успешных ответов, вызывается `onResponse()`, но `response.isSuccessful()` возвращает `false`; код ошибки показывается пользователю. Кнопка «Повторить» повторно вызывает `loadTodos()`.

Для проверки включить авиарежим на эмуляторе и повторить загрузку. После появления сообщения об ошибке выключить авиарежим и нажать «Повторить».

![6.png](Images%20for%20repots/5/6.png)     
*Рисунок 6 — Обработка сбоя сетевого запроса и повторная загрузка списка.*

---

## 5. FoodScan: Retrofit и Picasso

В контрольном задании мок-реализация каталога заменена запросом через Retrofit к [Open Food Facts](https://world.openfoodfacts.org/). Для поиска используется JSON endpoint `/api/v2/search`; запрос ограничен 30 записями и полями, необходимыми приложению: штрихкод, название, бренд, изображение, состав и энергетическая ценность.

### Поток данных

```mermaid
flowchart LR
    API["Open Food Facts API<br/>JSON"] --> RF["Retrofit + Gson"]
    RF --> NA["NetworkApi"]
    NA --> Repo["ProductRepositoryImpl"]
    Repo --> UC["GetProductCatalogUseCase"]
    UC --> VM["MainViewModel"]
    VM -->|LiveData| Act["MainActivity"]
    Act --> Adapter["ProductRecyclerViewAdapter"]
    Adapter --> Pic["Picasso"]
```

### Сетевой слой и обработка ошибок

Создан `OpenFoodFactsApi` с `@GET("api/v2/search")`. Ответ разбирается Gson в модель ответа с массивом продуктов. `NetworkApi` выполняет сетевой вызов в фоновом потоке, проверяет HTTP-статус и содержимое ответа, преобразует записи в `ProductDto` и кэширует успешно загруженный каталог для открытия карточки продукта. Для поиска по штрихкоду добавлен отдельный запрос `GET /api/v2/product/{barcode}`: он позволяет искать не только в первой странице каталога, но и по введённому коду. Найденный продукт кэшируется по штрихкоду и идентификатору, поэтому затем открывается обычная страница продукта.

`ProductRepositoryImpl` продолжает преобразовывать `ProductDto` в доменную модель `Product`, поэтому UseCase и экраны не зависят напрямую от Retrofit.

Ошибки соединения, HTTP-ответы с ошибочными статусами и пустые/некорректные ответы преобразуются в `NetworkException`. `MainViewModel` ловит исключение и передаёт сообщение в состояние интерфейса через `LiveData`. Для повторной загрузки каталога на главном экране появляется кнопка «Повторить загрузку каталога».

### Изображения Picasso

Загрузка картинок главного списка и страницы продукта переведена с Glide на Picasso. Используются `load()`, `placeholder()`, `error()`, `fit()` и `centerCrop()`. Если запись Open Food Facts не содержит URL изображения или изображение не загружается, показывается системная заглушка, а приложение продолжает работать.

Остальная структура FoodScan сохранена: Firebase отвечает за авторизацию, Room — за избранные продукты, `ProductRepository` и UseCase остаются границей между сетевыми DTO и доменными моделями. Демонстрационное распознавание продукта не изменялось в рамках этой практической работы.

![7.png](Images%20for%20repots/5/7.png)     
*Рисунок 7 — Каталог продуктов, загруженный через Retrofit и отображённый с помощью Picasso.*

![8.png](Images%20for%20repots/5/8.png)     
*Рисунок 8 — Карточка продукта FoodScan с изображением, загруженным Picasso.*

![9.png](Images%20for%20repots/5/9.png)     
*Рисунок 9 — Обработка сетевой ошибки в контрольном задании FoodScan.*


---

## Соответствие требованиям

| Требование | Реализация |
|---|---|
| Retrofit-модуль в MovieProject | `MovieProject/retrofitapp`, подключён в `settings.gradle.kts` |
| POJO под ответ JSON | `Todo.java` с полями `userId`, `id`, `title`, `completed` |
| GET-запрос списка дел | `ApiService.getTodos()` |
| Retrofit + Gson-конвертер | Конфигурация Retrofit в `MainActivity` |
| RecyclerView, ViewHolder и адаптер | `TodoAdapter`, `TodoViewHolder`, `item_todo.xml` |
| PUT при изменении CheckBox | `ApiService.updateTodo()` и `MainActivity.updateTodo()` |
| Picasso и настройка изображений | Четыре режима Spinner, `CircleTransformation` |
| Обработка сетевых и HTTP ошибок | Блок ошибки, `onFailure()`, проверка `isSuccessful()`, Retry |
| Контрольное: получение сущностей из сети | Retrofit-клиент Open Food Facts в `FoodScan/data/network` |
| Контрольное: показ изображений | Picasso в `ProductRecyclerViewAdapter`, `ProductAdapter` и `ProductDetailActivity` |
| Контрольное: обработка ошибок | `NetworkException`, состояние `LiveData` и кнопка повтора |

## Источники API

- [Методичка к практической работе № 5](Практика%20№5%20Retrofit%20(1).pdf)
- [JSONPlaceholder](https://jsonplaceholder.typicode.com/)
- [Open Food Facts — официальная документация API](https://openfoodfacts.github.io/openfoodfacts-server/api/)
- [Open Food Facts — поиск продуктов API v2](https://openfoodfacts.github.io/documentation/docs/Product-Opener/v2/search/get-search/)
