# Практическая работа № 1

Дисциплина «Разработка мобильных приложений».  
Проектирование приложения и каркас Android-проекта.

**Студент:** Хречко  
**Группа:** БСБО-09-23  
**Проект:** FoodScan, пакет `ru.mirea.khrechko.foodscan`

---

## Содержание

1. [Цель работы](#1-цель-работы)
2. [Задание](#2-задание)
3. [Проектирование приложения](#3-проектирование-приложения)
   - [3.1 Описание приложения](#31-описание-приложения)
   - [3.2 Диаграмма вариантов использования](#32-диаграмма-вариантов-использования)
   - [3.3 Функциональные возможности](#33-функциональные-возможности)
   - [3.4 Экраны и разделение на слои](#34-экраны-и-разделение-на-слои)
4. [Реализация каркаса](#4-реализация-каркаса)
   - [4.1 Создание проекта](#41-создание-проекта)
   - [4.2 Структура слоёв](#42-структура-слоёв)
   - [4.3 Сущности и репозитории](#43-сущности-и-репозитории)
   - [4.4 Классы use-case](#44-классы-use-case)
   - [4.5 Связь presentation и domain](#45-связь-presentation-и-domain)
   - [4.6 Работа каркаса](#46-работа-каркаса)
5. [Вывод](#5-вывод)

---

# 1. Цель работы

Спроектировать собственное Android-приложение **FoodScan**: построить диаграмму вариантов использования, определить экраны и зоны ответственности слоёв `presentation`, `domain` и `data`, выполнить анализ готовой модели TensorFlow Lite. На основе проекта `Empty Views Activity` реализовать каркас приложения: классы use-case слоя `domain`, интерфейсы репозиториев и реализации репозиториев слоя `data`, возвращающие тестовые данные.

# 2. Задание

1. Сформировать диаграмму вариантов использования приложения в draw.io.
2. Обеспечить на диаграмме функциональные возможности, указанные в методических указаниях:
   - авторизация;
   - взаимодействие с внешним JSON-сервисом;
   - сохранение данных в БД;
   - отображение списка сущностей с изображениями;
   - отображение страницы сущности;
   - различие возможностей гостя и авторизованного пользователя;
   - использование обученной модели TensorFlow Lite.
3. На основе сформированных use-case спроектировать экраны приложения с указанием зон ответственности.
4. Создать проект `ru.mirea.khrechko.foodscan` на основе шаблона `Empty Views Activity`.
5. Создать классы use-case в слое `domain` и реализации репозиториев в слое `data`. На начальном этапе репозитории должны возвращать тестовые данные.

# 3. Проектирование приложения

## 3.1 Описание приложения

Разрабатываемое приложение **FoodScan** предназначено для получения и просмотра информации о продуктах питания по их штрихкоду, а также для определения общей категории продукта по фотографии.

Основной сценарий работы пользователя заключается в сканировании или вводе штрихкода продукта. После получения кода приложение обращается к внешнему сервису **Open Food Facts**, который возвращает информацию о продукте в формате JSON. На основе полученных данных приложение может показать название продукта, бренд, изображение, состав и энергетическую ценность.

Пользователь также может просматривать каталог продуктов, открывать подробную страницу отдельного продукта и добавлять интересующие продукты в избранное.

Гостевой пользователь имеет возможность просматривать каталог, открывать карточки продуктов, выполнять поиск по штрихкоду и использовать распознавание изображения. Авторизованный пользователь дополнительно получает возможность сохранять продукты в избранное, просматривать собственный список сохранённых продуктов и управлять профилем.

В приложение также закладывается функция распознавания продукта по фотографии с использованием готовой модели TensorFlow Lite. На первом этапе распознавание реализовано в виде тестового сценария, а фактическое подключение модели предусмотрено в дальнейшем.

Основными сущностями предметной области являются:

- `User`;
- `Product`;
- `FavoriteProduct`;
- `Barcode`;
- `RecognitionResult`.

## 3.2 Диаграмма вариантов использования

Диаграмма вариантов использования строится в draw.io. Система представлена прямоугольником **FoodScan**. Сценарии сгруппированы по нескольким функциональным контекстам:

- **Аккаунт**;
- **Каталог**;
- **Поиск продукта**;
- **Избранное**;
- **Распознавание**.

### Актёры

| Актёр                       | Назначение |
|-----------------------------|---|
| Гость                       | Просмотр каталога, просмотр информации о продукте, поиск по штрихкоду и распознавание изображения |
| Авторизованный пользователь | Все возможности гостя, а также добавление и удаление продуктов из избранного, просмотр профиля |
| Open Food Facts             | Внешняя система для получения информации о продуктах в формате JSON |
| Модель для распознования    | Компонент приложения для локального распознавания изображения |

Авторизованный пользователь является специализацией гостя и наследует все его основные возможности.

### Основные варианты использования

Для гостя:

- войти;
- зарегистрироваться;
- просмотреть каталог продуктов;
- открыть продукт;
- найти продукт по штрихкоду;
- распознать продукт по фотографии.

Для авторизованного пользователя дополнительно:

- добавить продукт в избранное;
- удалить продукт из избранного;
- просмотреть избранное;
- просмотреть профиль;
- выйти из аккаунта.

### Связи между сценариями

Диаграмма вариантов использования приводится на рисунке 1.
![UseCases.png](Images%20for%20repots/1/UseCases.png)
**Рисунок 1 – Диаграмма вариантов использования приложения FoodScan**

## 3.3 Функциональные возможности

В приложении предусмотрены функциональные возможности, соответствующие требованиям методических указаний.

| № | Требование | Реализация в FoodScan |
|---|---|---|
| 1 | Авторизация | Сценарии «Войти», «Зарегистрироваться», «Просмотреть профиль», «Выйти» |
| 2 | Внешний сервис в формате JSON | Open Food Facts API для получения информации о продуктах по штрихкоду |
| 3 | Сохранение данных в БД | Избранные продукты и связанные с пользователем данные |
| 4 | Список сущностей с изображениями | Каталог продуктов и список избранного |
| 5 | Страница сущности | Подробная карточка продукта с изображением и характеристиками |
| 6 | Различие гостя и пользователя | Гость может просматривать и искать продукты, авторизованный пользователь дополнительно использует избранное |
| 7 | Обученная модель TFLite | Определение категории продукта по фотографии |

## 3.4 Экраны и разделение на слои

На основе сформированных use-case спроектированы основные экраны приложения.

| Экран (presentation) | Use-case (domain) |
|---|---|
| Вход / регистрация | Войти, Зарегистрироваться |
| Главный экран | Просмотреть каталог продуктов |
| Поиск по штрихкоду | Найти продукт по штрихкоду |
| Карточка продукта | Открыть продукт, Добавить в избранное, Удалить из избранного |
| Распознавание | Распознать продукт по фотографии |
| Избранное | Просмотреть избранное |
| Профиль | Просмотреть профиль, Выйти |
![Screens.png](Images%20for%20repots/1/Screens.png)
**Рисунок 2 – Экраны приложения FoodScan и зоны ответственности**

### Слои приложения

**Presentation** отвечает за отображение пользовательского интерфейса, обработку действий пользователя и передачу событий в слой бизнес-логики.

**Domain** содержит сущности предметной области, интерфейсы репозиториев и классы use-case. Слой не зависит от конкретной базы данных, сетевого клиента или элементов Android UI.

**Data** отвечает за получение, хранение и преобразование данных. В дальнейшем здесь будет реализовано взаимодействие с Open Food Facts API, локальной БД и моделью TensorFlow Lite.

При использовании Repository Pattern интерфейс репозитория находится в `domain`, а конкретная реализация — в `data`. Это позволяет заменить источник данных без изменения бизнес-логики.

# 4. Реализация каркаса

## 4.1 Создание проекта

Создан Android-проект на основе шаблона `Empty Views Activity`.

Параметры проекта:

- название приложения: **FoodScan**;
- пакет: `ru.mirea.khrechko.foodscan`.

На первом этапе проект является каркасом для дальнейшего наращивания функционала. Используются тестовые данные, чтобы проверить взаимодействие `presentation`, `domain` и `data`.

## 4.2 Структура слоёв

Структура проекта имеет следующий вид:

```text
ru.mirea.khrechko.foodscan
├── MainActivity.java
│
├── domain
│   ├── LoginUseCase.java
│   ├── RegisterUseCase.java
│   ├── GetProfileUseCase.java
│   ├── LogoutUseCase.java
│   ├── GetProductCatalogUseCase.java
│   ├── GetProductDetailsUseCase.java
│   ├── FindProductByBarcodeUseCase.java
│   ├── RecognizeProductUseCase.java
│   ├── AddToFavoritesUseCase.java
│   ├── RemoveFromFavoritesUseCase.java
│   ├── GetFavoritesUseCase.java
│   │
│   ├── models
│   │   ├── User.java
│   │   ├── Product.java
│   │   ├── FavoriteProduct.java
│   │   ├── Barcode.java
│   │   └── RecognitionResult.java
│   │
│   └── repository
│       ├── AuthRepository.java
│       ├── ProductRepository.java
│       ├── FavoriteRepository.java
│       └── RecognitionRepository.java
│
└── data
    └── repository
        ├── AuthRepositoryImpl.java
        ├── ProductRepositoryImpl.java
        ├── FavoriteRepositoryImpl.java
        └── RecognitionRepositoryImpl.java
```

Интерфейсы репозиториев находятся в `domain`, а реализации с суффиксом `Impl` — в `data`.

## 4.3 Сущности и репозитории

Сущности представляют основные объекты предметной области. Они не содержат зависимостей от пользовательского интерфейса или конкретного способа хранения данных.

### Листинг 1 — сущность `User`

```java
package ru.mirea.khrechko.foodscan.domain.models;

public class User {

    private int id;
    private String login;
    private boolean guest;

    public User(int id, String login, boolean guest) {
        this.id = id;
        this.login = login;
        this.guest = guest;
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public boolean isGuest() {
        return guest;
    }
}
```

### Листинг 2 — сущность `Product`

```java
package ru.mirea.khrechko.foodscan.domain.models;

public class Product {

    private int id;
    private String name;
    private String brand;
    private String imageUrl;
    private String ingredients;
    private int calories;

    public Product(
            int id,
            String name,
            String brand,
            String imageUrl,
            String ingredients,
            int calories
    ) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.imageUrl = imageUrl;
        this.ingredients = ingredients;
        this.calories = calories;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getIngredients() {
        return ingredients;
    }

    public int getCalories() {
        return calories;
    }
}
```

### Листинг 3 — сущность `RecognitionResult`

```java
package ru.mirea.khrechko.foodscan.domain.models;

public class RecognitionResult {

    private String productType;
    private float confidence;

    public RecognitionResult(
            String productType,
            float confidence
    ) {
        this.productType = productType;
        this.confidence = confidence;
    }

    public String getProductType() {
        return productType;
    }

    public float getConfidence() {
        return confidence;
    }
}
```

### Листинг 4 — интерфейс `AuthRepository`

```java
package ru.mirea.khrechko.foodscan.domain.repository;

import ru.mirea.khrechko.foodscan.domain.models.User;

public interface AuthRepository {

    boolean login(String login, String password);

    boolean register(String login, String password);

    User getProfile();

    void logout();
}
```

### Листинг 5 — интерфейс `RecognitionRepository`

```java
package ru.mirea.khrechko.foodscan.domain.repository;

import ru.mirea.khrechko.foodscan.domain.models.RecognitionResult;

public interface RecognitionRepository {

    RecognitionResult recognize(String imagePath);
}
```

### Листинг 6 — `AuthRepositoryImpl`

```java
package ru.mirea.khrechko.foodscan.data.repository;

import android.content.Context;

import ru.mirea.khrechko.foodscan.domain.models.User;
import ru.mirea.khrechko.foodscan.domain.repository.AuthRepository;

public class AuthRepositoryImpl implements AuthRepository {

    private User currentUser;

    public AuthRepositoryImpl(Context context) {
        currentUser = new User(0, "guest", true);
    }

    @Override
    public boolean login(String login, String password) {

        currentUser = new User(
                1,
                login,
                false
        );

        return true;
    }

    @Override
    public boolean register(String login, String password) {

        currentUser = new User(
                2,
                login,
                false
        );

        return true;
    }

    @Override
    public User getProfile() {
        return currentUser;
    }

    @Override
    public void logout() {
        currentUser = new User(
                0,
                "guest",
                true
        );
    }
}
```


## 4.4 Классы use-case

Каждый основной сценарий приложения представлен отдельным классом с методом `execute`.

### Листинг 7 — `LoginUseCase`

```java
package ru.mirea.khrechko.foodscan.domain;

import ru.mirea.khrechko.foodscan.domain.repository.AuthRepository;

public class LoginUseCase {

    private final AuthRepository authRepository;

    public LoginUseCase(
            AuthRepository authRepository
    ) {
        this.authRepository = authRepository;
    }

    public boolean execute(
            String login,
            String password
    ) {

        if (login == null ||
                login.trim().isEmpty()) {
            return false;
        }

        if (password == null ||
                password.trim().isEmpty()) {
            return false;
        }

        return authRepository.login(
                login,
                password
        );
    }
}
```

### Листинг 8 — `RegisterUseCase`

```java
package ru.mirea.khrechko.foodscan.domain;

import ru.mirea.khrechko.foodscan.domain.repository.AuthRepository;

public class RegisterUseCase {

    private final AuthRepository authRepository;

    public RegisterUseCase(
            AuthRepository authRepository
    ) {
        this.authRepository = authRepository;
    }

    public boolean execute(
            String login,
            String password
    ) {

        if (login == null ||
                login.trim().isEmpty()) {
            return false;
        }

        if (password == null ||
                password.trim().isEmpty()) {
            return false;
        }

        if (password.length() < 4) {
            return false;
        }

        return authRepository.register(
                login,
                password
        );
    }
}
```


## 4.5 Связь presentation и domain

`MainActivity` создаёт реализации репозиториев из слоя `data`, после чего передаёт их в use-case слоя `domain`.

### Листинг 9 — создание репозиториев

```java
AuthRepository authRepository =
        new AuthRepositoryImpl(this);

ProductRepository productRepository =
        new ProductRepositoryImpl(this);

FavoriteRepository favoriteRepository =
        new FavoriteRepositoryImpl(this);

RecognitionRepository recognitionRepository =
        new RecognitionRepositoryImpl(this);
```

### Листинг 10 — создание use-case

```java
LoginUseCase loginUseCase =
        new LoginUseCase(authRepository);

GetProductCatalogUseCase catalogUseCase =
        new GetProductCatalogUseCase(
                productRepository
        );

FindProductByBarcodeUseCase barcodeUseCase =
        new FindProductByBarcodeUseCase(
                productRepository
        );

RecognizeProductUseCase recognizeUseCase =
        new RecognizeProductUseCase(
                recognitionRepository
        );
```

### Листинг 11 — `MainActivity.java`

```java
package ru.mirea.khrechko.foodscan;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import ru.mirea.khrechko.foodscan.data.repository.AuthRepositoryImpl;
import ru.mirea.khrechko.foodscan.data.repository.FavoriteRepositoryImpl;
import ru.mirea.khrechko.foodscan.data.repository.ProductRepositoryImpl;
import ru.mirea.khrechko.foodscan.data.repository.RecognitionRepositoryImpl;

import ru.mirea.khrechko.foodscan.domain.GetProductCatalogUseCase;
import ru.mirea.khrechko.foodscan.domain.LoginUseCase;
import ru.mirea.khrechko.foodscan.domain.models.Product;
import ru.mirea.khrechko.foodscan.domain.models.User;
import ru.mirea.khrechko.foodscan.domain.repository.AuthRepository;
import ru.mirea.khrechko.foodscan.domain.repository.FavoriteRepository;
import ru.mirea.khrechko.foodscan.domain.repository.ProductRepository;
import ru.mirea.khrechko.foodscan.domain.repository.RecognitionRepository;

public class MainActivity extends AppCompatActivity {

    private AuthRepository authRepository;
    private ProductRepository productRepository;
    private FavoriteRepository favoriteRepository;
    private RecognitionRepository recognitionRepository;

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_main
        );

        authRepository =
                new AuthRepositoryImpl(this);

        productRepository =
                new ProductRepositoryImpl(this);

        favoriteRepository =
                new FavoriteRepositoryImpl(this);

        recognitionRepository =
                new RecognitionRepositoryImpl(this);

        LoginUseCase loginUseCase =
                new LoginUseCase(
                        authRepository
                );

        GetProductCatalogUseCase catalogUseCase =
                new GetProductCatalogUseCase(
                        productRepository
                );

        boolean loginResult =
                loginUseCase.execute(
                        "student",
                        "1234"
                );

        User user =
                authRepository.getProfile();

        List<Product> products =
                catalogUseCase.execute();

        TextView textView =
                findViewById(
                        R.id.textViewResult
                );

        if (loginResult &&
                user != null) {

            StringBuilder result =
                    new StringBuilder();

            result.append("Пользователь: ")
                    .append(user.getLogin())
                    .append("\n\n");

            result.append(
                    "Каталог продуктов:\n"
            );

            for (Product product :
                    products) {

                result.append("- ")
                        .append(
                                product.getName()
                        )
                        .append("\n");
            }

            textView.setText(
                    result.toString()
            );
        }
    }
}
```

### Листинг 12 — `activity_main.xml`

На первом этапе для проверки каркаса достаточно одного текстового элемента:

```xml
<?xml version="1.0" encoding="utf-8"?>

<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="24dp">

    <TextView
        android:id="@+id/textViewResult"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:textSize="18sp" />

</LinearLayout>
```

Таким образом, `MainActivity` отвечает только за связь пользовательского интерфейса с use-case. Бизнес-логика не переносится в Activity.

## 4.6 Работа каркаса

После запуска приложения осуществляется создание репозиториев и use-case, выполняется тестовая авторизация и получается список тестовых продуктов.

Для тестирования поиска по штрихкоду предусмотрены следующие условные значения:

| Штрихкод | Тестовый продукт |
|---|---|
| `460000000001` | Молоко |
| `460000000002` | Банан |
| `460000000003` | Яблоко |
| `460000000004` | Шоколад |
| `460000000005` | Хлеб |
Данные штрихкоды используются только для тестирования каркаса. На следующих этапах вместо них будет использоваться реальное сканирование штрихкода и обращение к внешнему API.

![ui.png](Images%20for%20repots/1/ui.png)

**Рисунок 3 – Экран для проверки данных от каркаса приложения**


На данном этапе внешний API, локальная база данных и реальный инференс TensorFlow Lite не подключаются. Репозитории возвращают тестовые данные, что позволяет проверить архитектуру приложения и корректность взаимодействия между слоями.

# 5. Вывод

В ходе практической работы № 1 спроектировано мобильное приложение **FoodScan**, предназначенное для получения информации о продуктах питания по штрихкоду и определения категории продукта по фотографии.

1. Разработана структура диаграммы вариантов использования с актёрами «Гость», «Авторизованный пользователь», `Open Food Facts` и моделью TensorFlow Lite.
2. На диаграмме отражены обязательные возможности приложения: авторизация, взаимодействие с внешним JSON-сервисом, сохранение данных, список сущностей с изображениями, страница продукта, различие возможностей гостя и авторизованного пользователя и использование модели TFLite.
3. Определены основные экраны приложения и зоны ответственности слоёв `presentation`, `domain` и `data`.
4. Для получения информации о продуктах выбран внешний сервис Open Food Facts, с которым планируется взаимодействовать по HTTP через JSON API.
5. Для функции распознавания предусмотрено использование готовой модели TensorFlow Lite.
6. Создана структура Android-проекта `ru.mirea.khrechko.foodscan`.
7. В слое `domain` созданы сущности, интерфейсы репозиториев и классы use-case.
8. В слое `data` созданы реализации репозиториев, возвращающие тестовые данные.
9. Организация зависимостей позволяет в дальнейшем заменить тестовые реализации на реальные источники данных, не изменяя бизнес-логику domain.

Таким образом, созданный каркас приложения соответствует выбранной предметной области и может использоваться как основа для последующего подключения Open Food Facts API, локальной базы данных, сканера штрихкодов и модели TensorFlow Lite.
