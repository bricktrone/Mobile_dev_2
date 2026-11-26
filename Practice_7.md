# Практическая работа № 7
## Навигация в Android-приложениях: Navigation Component, Bottom Navigation и Navigation Drawer

**Дисциплина:** Разработка мобильных приложений  
**Язык реализации:** Java  
**Проекты:** `MovieProject` и `FoodScan`  
**Студент:** Хречко Роман  
**Группа:** БСБО-09-23  

---

## Содержание

1. [Цель работы](#1-цель-работы)
2. [Задачи работы](#2-задачи-работы)
3. [Теоретические сведения](#3-теоретические-сведения)
4. [Базовая часть: MovieProject](#4-базовая-часть-movieproject)
   - [4.1. Подготовка модулей и зависимостей](#41-подготовка-модулей-и-зависимостей)
   - [4.2. BottomNavigationApp](#42-bottomnavigationapp)
   - [4.3. NavigationDrawerApp](#43-navigationdrawerapp)
   - [4.4. Работа с навигационным графом и back stack](#44-работа-с-навигационным-графом-и-back-stack)
5. [Контрольное задание: FoodScan](#5-контрольное-задание-foodscan)
   - [5.1. Подключение Navigation Component](#51-подключение-navigation-component)
   - [5.2. Навигационный граф FoodScan](#52-навигационный-граф-foodscan)
   - [5.3. Нижняя навигация](#53-нижняя-навигация)
   - [5.4. Переходы к карточке продукта и распознаванию](#54-переходы-к-карточке-продукта-и-распознаванию)
6. [Заключение](#6-заключение)

---

## 1. Цель работы

Изучить принципы организации навигации в Android-приложениях, познакомиться с различными способами перехода между экранами и освоить Navigation Component. На практике требуется создать приложения с нижней панелью навигации и боковой выдвижной шторкой, а также применить Navigation Component в уже разработанном проекте.

Базовые задания выполняются в `MovieProject` в отдельных модулях `BottomNavigationApp` и `NavigationDrawerApp`. Контрольное задание выполняется в `FoodScan`: необходимо организовать навигацию приложения с использованием Navigation Component и одного из рассмотренных вариантов навигации — Bottom Navigation или Navigation Drawer. В рамках реализации для `FoodScan` используется Bottom Navigation, при этом сохраняется ранее согласованный зелёный дизайн приложения.

## 2. Задачи работы

1. Изучить назначение View Binding и включить его в создаваемые модули.
2. Рассмотреть принципы навигации, начальный пункт назначения, back stack и поведение кнопок Back и Up.
3. Познакомиться с линейной, иерархической, модальной навигацией, навигацией через Activity и Fragment.
4. Подключить Navigation Component и изучить основные элементы: `NavHostFragment`, `NavGraph`, `NavController` и `NavDestination`.
5. Создать модуль `BottomNavigationApp` с нижней панелью и несколькими фрагментами.
6. Добавить иконки и настроить цветовую гамму `BottomNavigationApp`.
7. Создать модуль `NavigationDrawerApp` с боковой навигационной шторкой.
8. Реализовать обработку системной кнопки Back так, чтобы открытая шторка закрывалась прежде, чем выполнялся переход по back stack.
9. В контрольном задании подключить Navigation Component в `FoodScan`.
10. Связать основные разделы `FoodScan` с `BottomNavigationView` и настроить переходы к дополнительным экранам.

## 3. Теоретические сведения

**View Binding** — механизм Android, который генерирует класс привязки для каждой XML-разметки. С помощью него можно обращаться к элементам интерфейса через поля binding-класса, не вызывая `findViewById()` для каждого элемента. Для включения механизма в модуле используется настройка:

```kotlin
android {
    buildFeatures {
        viewBinding = true
    }
}
```

В Activity binding-класс создаётся с помощью `inflate()`, после чего корневое представление передаётся в `setContentView()`. Во фрагментах binding обычно обнуляется в `onDestroyView()`, поскольку жизненный цикл представления фрагмента может завершиться раньше жизненного цикла самого экземпляра Fragment.

**Navigation Component** — библиотека AndroidX, упрощающая управление переходами между пунктами назначения приложения. Она позволяет централизованно описывать структуру переходов, управлять back stack, передавать аргументы и согласовывать интерфейс навигации с текущим экраном.

Основные элементы Navigation Component:

- `NavHostFragment` — контейнер, отображающий текущий экран навигации;
- `NavGraph` — граф, в котором описаны пункты назначения и переходы между ними;
- `NavController` — объект, выполняющий навигацию и управляющий back stack;
- `NavDestination` — конкретный пункт назначения графа, например экран главной страницы или профиль;
- `NavOptions` — параметры перехода, например поведение при повторном открытии пункта назначения.

Граф навигации может храниться в XML в каталоге `res/navigation`. В корневом элементе `navigation` указывается `startDestination` — экран, который открывается первым. Внутри графа описываются фрагменты, а переходы между ними задаются элементами `action`.

**Back stack** — стек истории навигации. Новый экран добавляется в верхнюю часть стека. При нажатии системной кнопки Back верхний пункт назначения удаляется, и пользователь возвращается к предыдущему экрану. Кнопка Up в панели приложения предназначена для возврата внутри структуры приложения и на верхнем уровне не должна закрывать приложение.

**Bottom Navigation** — нижняя панель, предоставляющая быстрый доступ к основным разделам приложения. Она подходит для приложений с несколькими часто используемыми корневыми экранами. Каждый пункт меню имеет уникальный идентификатор, подпись и иконку. В связке с Navigation Component панель можно подключить через `NavigationUI.setupWithNavController()`.

**Navigation Drawer** — выдвижная боковая панель, содержащая меню разделов приложения. Для её реализации обычно применяются `DrawerLayout` и `NavigationView`. В верхней части панели может находиться Header с логотипом и информацией о приложении или пользователе. При совместном использовании с Navigation Component открываемая панель передаётся в `AppBarConfiguration`, а `NavigationUI` связывает пункты меню с навигационным графом.

В работе изученные компоненты применяются следующим образом: `BottomNavigationApp` использует нижнюю навигацию, `NavigationDrawerApp` — боковую шторку, а `FoodScan` — нижнюю навигацию с общим `NavController`.

## 4. Базовая часть: MovieProject

Все задания до контрольного выполнены в проекте `MovieProject`. Добавлены два отдельных Android application-модуля: `BottomNavigationApp` и `NavigationDrawerApp`. Исходные модули проекта сохранены.

В `MovieProject/settings.gradle.kts` зарегистрированы новые модули:

```kotlin
include(":BottomNavigationApp")
include(":NavigationDrawerApp")
```

В version catalog проекта добавлена версия Navigation Component `2.8.4` и зависимости `navigation-fragment` и `navigation-ui`. В обоих новых модулях включён View Binding. Для оформления используется стандартная Material 3 тема с фиолетовой цветовой схемой, близкая к типовой теме Android Studio.

### 4.1. Подготовка модулей и зависимостей

Каждый модуль является самостоятельным приложением с отдельными `MainActivity`, `AndroidManifest.xml`, XML-разметками, меню и графом навигации. Благодаря этому задания можно запускать независимо друг от друга, выбирая соответствующую конфигурацию запуска в Android Studio.

Структура добавленных модулей:

```text
MovieProject/
├── BottomNavigationApp/
│   └── src/main/
│       ├── java/ru/mirea/khrechko/bottomnavigationapp/
│       │   ├── MainActivity.java
│       │   ├── HomeFragment.java
│       │   ├── InfoFragment.java
│       │   └── ProfileFragment.java
│       └── res/
│           ├── drawable/       # иконки нижней панели
│           ├── layout/         # Activity и три фрагмента
│           ├── menu/           # bottom_nav_menu.xml
│           ├── navigation/     # bottom_nav_graph.xml
│           └── values/         # строки, цвета и тема
│
└── NavigationDrawerApp/
    └── src/main/
        ├── java/ru/mirea/khrechko/navigationdrawerapp/
        │   ├── MainActivity.java
        │   ├── HomeFragment.java
        │   ├── InfoFragment.java
        │   └── ProfileFragment.java
        └── res/
            ├── drawable/       # иконки пунктов меню
            ├── layout/         # Activity, Toolbar, NavHost и Header
            ├── menu/           # drawer_menu.xml
            ├── navigation/     # drawer_nav_graph.xml
            └── values/         # строки, цвета и тема
```

### 4.2. BottomNavigationApp

Модуль `BottomNavigationApp` демонстрирует работу нижней панели навигации на примере приложения-каталога фильмов «Кинотека». Для основного интерфейса используется вертикальная разметка, содержащая верхний `MaterialToolbar`, `FragmentContainerView` и `BottomNavigationView`.

`FragmentContainerView` объявлен как `NavHostFragment` и подключает граф `bottom_nav_graph.xml`. Нижняя панель получает пункты меню из `bottom_nav_menu.xml`. В меню определены три раздела:

| Пункт меню | Fragment | Назначение |
|---|---|---|
| Фильмы | `HomeFragment` | Главный экран каталога фильмов |
| Избранное | `InfoFragment` | Раздел сохранённых фильмов |
| Профиль | `ProfileFragment` | Экран пользователя и настроек |

Каждому пункту назначены собственные иконка и идентификатор. Оформление использует Material 3 и стандартную фиолетовую палитру: основной цвет `#6750A4`, более тёмный оттенок `#4F378B`, цвет поверхности и белые элементы. Визуальное оформление соответствует учебному модулю и не повторяет индивидуальный зелёный дизайн `FoodScan`.

Для связи графа и нижней панели в `MainActivity` получен `NavController` из `NavHostFragment`. Затем вызывается `NavigationUI.setupWithNavController()`:

```java
NavHostFragment host = (NavHostFragment)
        getSupportFragmentManager()
                .findFragmentById(R.id.nav_host);

navController = host.getNavController();

NavigationUI.setupWithNavController(
        binding.bottomNavigation,
        navController
);
```

Таким образом, при выборе пункта `BottomNavigationView` Navigation Component автоматически открывает соответствующий фрагмент и обновляет состояние выбранной вкладки.

![2.png](Images%20for%20repots/7/1.png)

**Рисунок 1 — Главный экран BottomNavigationApp с нижней панелью навигации.**

![3.png](Images%20for%20repots/7/2.png)

**Рисунок 2 — Переход между разделами BottomNavigationApp.**

### 4.3. NavigationDrawerApp

Модуль `NavigationDrawerApp` демонстрирует навигацию с помощью боковой выдвижной панели. Корневым контейнером Activity является `DrawerLayout`, который объединяет основное содержимое и `NavigationView`.

Основное содержимое вынесено в `app_bar_main.xml`. В нём размещается `MaterialToolbar` и подключается `content_main.xml` с `NavHostFragment`. Навигационный хост загружает граф `drawer_nav_graph.xml`. Сама панель задаётся через `NavigationView`, которому передаются ресурсы `nav_header_main.xml` и `drawer_menu.xml`.

В боковом меню предусмотрены три раздела:

- **Каталог** — основной раздел приложения;
- **Жанры** — экран жанров и подборок;
- **Профиль** — экран профиля пользователя.

В Header панели отображается иконка профиля, название `MovieProject` и подпись «Каталог кино и подборки». Для пунктов меню используются иконки, а переключение экранов выполняется через `NavigationUI.setupWithNavController()`.

Для корректной работы кнопки Back добавлен обработчик через `OnBackPressedDispatcher`. Сначала он проверяет, открыта ли шторка. Если `DrawerLayout` показывает Navigation Drawer, шторка закрывается. Если панель закрыта, обработчик передаёт событие стандартному механизму Activity и Navigation Component, чтобы сработал back stack.

```java
if (binding != null
        && binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
    binding.drawerLayout.closeDrawer(GravityCompat.START);
} else {
    setEnabled(false);
    getOnBackPressedDispatcher().onBackPressed();
    setEnabled(true);
}
```

![4.png](Images%20for%20repots/7/3.png)

**Рисунок 3 — Главный экран NavigationDrawerApp с Toolbar и кнопкой открытия бокового меню.**

![5.png](Images%20for%20repots/7/4.png)

**Рисунок 4 — Открытая боковая шторка Navigation Drawer.**

### 4.4. Работа с навигационным графом и back stack

В обоих учебных модулях граф навигации хранится в XML внутри `res/navigation`. В `bottom_nav_graph.xml` и `drawer_nav_graph.xml` стартовым пунктом назначен `nav_home`. Каждый фрагмент имеет уникальный ID и имя Java-класса.

В графе нижней навигации определены действия:

- `action_nav_home_to_nav_info` — переход с главного экрана к разделу избранного;
- `action_nav_info_to_nav_profile` — переход с экрана избранного к профилю.

Для выполнения этих переходов фрагменты используют `Navigation.findNavController(view).navigate(...)`. В `MainActivity` полученный `NavController` также используется для настройки ActionBar и пунктов навигации. В качестве корневых назначений в `AppBarConfiguration` указаны главная страница, информация и профиль. Это позволяет NavigationUI корректно определять, когда отображается кнопка Up.

В `NavigationDrawerApp` те же три типа пунктов назначения связаны с меню шторки. В `AppBarConfiguration` дополнительно передаётся `DrawerLayout` через `setOpenableLayout()`. Благодаря этому верхняя панель работает совместно с боковым меню, а NavigationUI может использовать кнопку-гамбургер на корневых экранах.

При переходе между пунктами назначения `NavController` обновляет back stack. Кнопка Back возвращает к предыдущему пункту назначения, а кнопка Up обрабатывается через `NavigationUI.navigateUp()`.

## 5. Контрольное задание: FoodScan

В контрольной части Navigation Component подключён к существующему проекту `FoodScan`. Сохранены основные возможности приложения, созданные в предыдущих практических работах: авторизация, каталог Open Food Facts, локальное избранное Room, поиск по штрихкоду, карточка продукта и демонстрационный экран распознавания.

Для навигации используется вариант Bottom Navigation. Отдельный Navigation Drawer в `FoodScan` не добавлялся, поскольку формулировка контрольного задания допускает использование Navigation Drawer или Bottom Navigation. Нижняя панель подходит для четырёх часто используемых разделов FoodScan и соответствует уже разработанному прототипу.

### 5.1. Подключение Navigation Component

В проекте `FoodScan` используется version catalog. В `gradle/libs.versions.toml` определена версия Navigation Component `2.8.4`, а также алиасы библиотек `navigation-fragment` и `navigation-ui`.

В модуле `app` подключены зависимости:

```kotlin
implementation(libs.navigation.fragment)
implementation(libs.navigation.ui)
```

View Binding включён в настройках Android-модуля:

```kotlin
android {
    buildFeatures {
        viewBinding = true
    }
}
```

Главная Activity использует сгенерированный класс `ActivityMainBinding` для разметки `activity_main.xml`. В корневой разметке расположены `NavHostFragment` и `BottomNavigationView`.

### 5.2. Навигационный граф FoodScan

Граф навигации создан в файле `app/src/main/res/navigation/foodscan_nav_graph.xml`. В качестве стартового пункта назначения указан `nav_home`, соответствующий `HomeFragment`.

В граф включены шесть пунктов назначения:

| Идентификатор | Фрагмент | Назначение |
|---|---|---|
| `nav_home` | `HomeFragment` | Главная страница и каталог продуктов |
| `nav_scan` | `ScanFragment` | Поиск продукта по штрихкоду |
| `nav_favorites` | `FavoritesFragment` | Список избранных продуктов |
| `nav_profile` | `ProfileFragment` | Профиль пользователя и состояние авторизации |
| `recognitionFragment` | `RecognitionFragment` | Выбор фотографии и запуск сценария распознавания |
| `productDetailFragment` | `ProductDetailFragment` | Подробная карточка выбранного продукта |

Для экрана продукта в графе объявлен аргумент `product_id` типа `integer`. Это позволяет передать в новый пункт назначения идентификатор конкретного продукта и загрузить необходимые данные в `ProductDetailFragment`.

### 5.3. Нижняя навигация

В `activity_main.xml` добавлены `NavHostFragment` и `BottomNavigationView`. Хост занимает основную часть экрана, а нижняя панель располагается внизу. Для хоста указаны `app:defaultNavHost="true"` и `app:navGraph="@navigation/foodscan_nav_graph"`.

Пункты нижней панели описаны в `menu_bottom_navigation.xml`:

| Пункт меню | ID | Фрагмент |
|---|---|---|
| Главная | `nav_home` | `HomeFragment` |
| Поиск | `nav_scan` | `ScanFragment` |
| Избранное | `nav_favorites` | `FavoritesFragment` |
| Профиль | `nav_profile` | `ProfileFragment` |

Для управления переходами `MainActivity` получает `NavController` из `NavHostFragment` и связывает его с нижней панелью:

```java
NavHostFragment navHost = (NavHostFragment)
        getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

navController = navHost.getNavController();
NavigationUI.setupWithNavController(
        binding.bottomNavigation,
        navController
);
```

После настройки Bottom Navigation выбор вкладки автоматически приводит к переходу на связанный пункт назначения графа. Текущий активный пункт меню отображается выделенным. Переходы на дополнительные экраны не являются отдельными вкладками — они выполняются через `NavController` и попадают в историю навигации.

![8.png](Images%20for%20repots/7/5.png)

**Рисунок 5 — Главный экран FoodScan с нижней панелью навигации.**

![9.png](Images%20for%20repots/7/6.png)

**Рисунок 9 — Экран поиска продукта по штрихкоду в FoodScan.**

### 5.4. Переходы к карточке продукта и распознаванию

Для открытия карточки продукта `MainActivity` использует метод `openProduct(Product product)`. Перед навигацией в общую `MainViewModel` сохраняется ID выбранного продукта, затем создаётся `Bundle` с аргументом `product_id` и выполняется переход на `productDetailFragment`.

```java
public void openProduct(@NonNull Product product) {
    viewModel.selectProduct(product.getId());
    Bundle args = new Bundle();
    args.putInt("product_id", product.getId());
    NavOptions options = new NavOptions.Builder()
            .setLaunchSingleTop(true)
            .build();
    navController.navigate(R.id.productDetailFragment, args, options);
}
```

В `ProductDetailFragment` аргумент читается и передаётся в `ProductDetailViewModel` для загрузки данных. На экране отображаются название, бренд, изображение, калорийность, белки, жиры и состав. Кнопка возврата вызывает `NavHostFragment.findNavController(this).navigateUp()`, то есть возвращает пользователя по навигационному стеку.

Аналогичным образом вызывается экран распознавания. Метод `openRecognition()` выполняет переход к `recognitionFragment`. На экране можно выбрать фотографию через Activity Result API, после чего пользователь может запустить демонстрационный сценарий распознавания. Кнопка возврата использует `navigateUp()`.

![10.png](Images%20for%20repots/7/7.png)

**Рисунок 7 — Карточка продукта FoodScan, открытая как отдельный пункт назначения Navigation Component.**

![11.png](Images%20for%20repots/7/8.png)

**Рисунок 8 — Экран распознавания изображения в FoodScan.**

**Как сделать скриншот:** открыть распознавание с главной страницы или из профиля. Сделать снимок начального состояния экрана с кнопками выбора изображения и запуска распознавания. Дополнительно можно выбрать фотографию через системный файловый диалог и сделать второй снимок с изображением в области предпросмотра.

## 6. Заключение

В ходе практической работы изучены способы организации навигации в Android-приложениях и освоен Navigation Component. В проекте `MovieProject` созданы два самостоятельных модуля. `BottomNavigationApp` реализует переключение между фрагментами с помощью нижней панели, а `NavigationDrawerApp` — навигацию через боковую шторку. В обоих случаях используется XML-граф, NavController и связка NavigationUI с элементами интерфейса.

В контрольном задании `FoodScan` навигация переведена на Navigation Component. Основные экраны приложения доступны через Bottom Navigation, а карточка продукта и распознавание представлены дополнительными пунктами назначения. Для карточки продукта реализована передача идентификатора через аргумент графа. Предыдущий зелёный дизайн FoodScan сохранён.
