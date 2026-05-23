# KMP клиент для Stepik

Кроссплатформенное мобильное приложение (Android + iOS) для платформы Stepik, написанное на Kotlin Multiplatform с общим UI на Compose Multiplatform. Позволяет авторизоваться через Stepik OAuth, искать и проходить курсы, отвечать на задания разных типов, читать и оставлять комментарии, отслеживать прогресс и получать сертификаты.

---

## Стек

| Слой | Технология |
|---|---|
| Язык | Kotlin 2.3.20 |
| Multiplatform | Kotlin Multiplatform, Compose Multiplatform 1.10 |
| UI | Material 3, Navigation Compose |
| DI | Koin (compose / viewmodel) |
| Сеть | Ktor 3 (`Auth.bearer`, ContentNegotiation, Logging) |
| Сериализация | kotlinx.serialization |
| Локальное хранилище | Room (KSP), DataStore Preferences |
| Безопасное хранилище токенов | KVault (EncryptedSharedPreferences / Keychain) |
| Видео | AndroidX Media3 ExoPlayer (Android), AVPlayer (iOS) |
| Логи | Napier |
| Картинки | Coil 3 (compose + ktor + svg) |
| Crash reporting | Firebase Crashlytics |

Минимальная Android SDK — 26, target — 36. iOS — через `iosApp` Xcode project.

---

## Архитектура

Каждая фича построена по Clean Architecture:

```
feature/<name>/
├── data/
│   ├── network/    — Ktor API + DTO
│   ├── local/      — Room entities + DAO
│   ├── repository/ — RepositoryImpl, мапит DTO ↔ Domain
│   └── mapper/
├── domain/
│   ├── entity/     — sealed классы / data классы
│   ├── repository/ — интерфейс, который видит presentation
│   └── usecase/    — (опционально) бизнес-операции
├── di/             — Koin module
└── presentation/
    ├── state/      — UiState (data class)
    ├── viewmodel/  — ViewModel с MutableStateFlow<UiState>
    └── ui/         — Composable экранов и компонентов
```

Общие принципы:

- **Один `MutableStateFlow<UiState>` на ViewModel.** Мутация только через `_state.update { it.copy(...) }`.
- **Repository всегда возвращает `Result<Domain>`** через `runCatchingCancellable` (ловит исключения, не ломая отмену корутин).
- **expect / actual** для платформенно-зависимого: `VideoPlayer`, `HtmlText`, `TokenStorage`, `HttpClientEngine`, `LoginWebView`.
- **DI через Koin** с named-квалификаторами для разных HTTP-клиентов (`publicClient`, `authClient`).

---

## Структура проекта

```
composeApp/src/
├── commonMain/kotlin/org/kts/tazmin/
│   ├── core/
│   │   ├── common/        — Config, AppError, runCatchingCancellable
│   │   ├── datastore/     — UserPreferences (DataStore)
│   │   ├── network/       — HttpClientFactory (public + authenticated)
│   │   ├── parser/        — HtmlText (expect/actual)
│   │   ├── session/       — SessionManager (StateFlow<Active|Expired>)
│   │   ├── token/         — TokenStorage (expect/actual KVault)
│   │   └── utils/         — DateFormatter и т.п.
│   ├── feature/
│   │   ├── auth/          — OAuth login, refresh, logout
│   │   ├── catalog/       — каталог курсов
│   │   ├── course_details/— страница курса, модули, программа
│   │   ├── course_reviews/— отзывы на курсы
│   │   ├── lesson/        — прохождение шагов урока
│   │   ├── comments/      — обсуждения и решения
│   │   ├── certificates/  — сертификаты пользователя
│   │   ├── profile/       — профиль
│   │   ├── notifications/ — уведомления
│   │   ├── wishlist/      — желаемое
│   │   ├── settings/      — настройки
│   │   └── reviews_stub/  — заглушка
│   ├── navigation/        — Screen sealed class + NavGraph
│   ├── theme/             — CatTheme, Color, Typography
│   └── Platform.kt
├── androidMain/   — actual для Android (WebView, ExoPlayer, KVault Android, Ktor OkHttp)
└── iosMain/       — actual для iOS (AVPlayer, KVault iOS, Ktor Darwin)
```

---
# Screenshots
<img width="1471" height="880" alt="изображение" src="https://github.com/user-attachments/assets/4bc87931-307b-48b6-a0dc-f90bb2e3e7ed" />

<img width="1448" height="889" alt="изображение" src="https://github.com/user-attachments/assets/72b6f928-4e01-4cae-a163-fc5ddd5b9401" />

<img width="1482" height="895" alt="изображение" src="https://github.com/user-attachments/assets/bfe31110-f794-4b0e-90f2-08b8997cb9e3" />

## Фичи

### Авторизация (OAuth 2.0)

Реализован **OAuth 2.0 Authorization Code Flow** с прозрачным auto-refresh access-токена.

**Поток логина:**
1. `OnboardingScreen` → пользователь жмёт «Войти через Stepik».
2. Открывается `LoginWebView` (WebView на Android / WKWebView на iOS) с `https://stepik.org/oauth2/authorize/?response_type=code&client_id=...&redirect_uri=...`.
3. `WebViewClient.shouldOverrideUrlLoading` перехватывает редирект на `redirect_uri?code=...`.
4. `OAuthViewModel.onCodeReceived(code)` → `LoginUseCase` → `AuthRepository.login(code)`.
5. `AuthApi` делает `POST /oauth2/token/` (`grant_type=authorization_code`, form-urlencoded), получает `TokenResponse`.
6. Токены пишутся в `TokenStorage`, кеш `BearerAuthProvider` инвалидируется.
7. `SessionManager.notifyLoginCompleted()` → state-flow `Active` → UI редиректит на главный экран.

**Auto-refresh:** Ktor `Auth.bearer { refreshTokens { ... } }` срабатывает на 401, делает `POST /oauth2/token/` (`grant_type=refresh_token`), сохраняет новую пару, повторяет исходный запрос. При провале рефреша — `logout()` + `SessionManager.notifyExpired()`, корневой Composable редиректит на онбординг.

**Защиты:**
- `Lazy<AuthRepository>` в `HttpClientFactory` для разрыва цикла DI (auth-клиент нужен репозиторию, репозиторий нужен auth-клиенту).
- Graceful fallback при `AEADBadTagException` после переустановки приложения (Keystore-ключ удалён, файл остался — пересоздаём KVault).
- Защита от гонки на старте: если `refreshToken == null` в момент refresh — не выкидываем юзера, KVault мог ещё не подняться.

### Каталог курсов

`feature/catalog` — список курсов с поиском и пагинацией. Подгрузка следующих страниц по достижении конца `LazyColumn`.

### Детали курса и отзывы

`feature/course_details` — страница курса: обложка, описание, программа модулей, статистика. Программа загружается отдельным запросом и разворачивается по модулям → секциям → урокам.

`feature/course_reviews` — пагинированные отзывы с возможностью оставить свой.

### Прохождение урока

`feature/lesson` — основной флоу обучения. Шаг — `sealed class LessonStep`:

| Тип | Описание |
|---|---|
| `Text` | HTML-теория |
| `Video` | видео через ExoPlayer / AVPlayer |
| `Choice` | one-of / many-of (варианты приходят через `attempt.dataset`, не через step-DTO) |
| `StringInput` / `NumberInput` | свободный ввод |
| `Code` | блок кода с языком и шаблоном |
| `Sorting` | расставить варианты в правильном порядке |
| `Matching` | сопоставить пары через dropdown |
| `FillBlanks` | пропуски в тексте через `FlowRow` |
| `Unknown` | фолбэк для неизвестных типов |

**ViewModel:**
- `loadLesson` — грузит шаги через `CourseModulesApi`, подтягивает кешированные результаты из Room для всех `stepIds`. Для первого `Choice` предзагружает `attempt`.
- `setReply` — сохраняет черновик ввода в `stepReplies` (память).
- `submitAnswer` — `createAttempt` → `POST /submissions` → при `EVALUATION` поллинг `GET /submissions/{id}` каждые 1 с, до 10 раз → пишет финальный результат в Room и state.
- `goToStep` / `goToNext` / `goToPrevious` — обнуляют `attempt`, сохраняют `stepResults` (Room) и `stepReplies` (память), для `Choice` пересоздают attempt.
- `clearSubmissionResult` — retry: чистит Room+state, `stepReplies` оставляет, чтобы пользователь видел свой прежний ввод.

**Проверка правильности целиком на сервере** — клиент шлёт reply, получает enum `CORRECT | WRONG | EVALUATION`.

**UI:** `LessonScreen` со свайп-навигацией, прогресс-баром, `AnimatedContent` для слайдов между шагами. `StepContent` диспатчит в нужный `*StepView`. Каждая вьюха держит локальный стейт через `remember(step.id)`, при изменении дёргает `onAnswerReady`. HTML рендерится через `HtmlText` (WebView с inline CSS на Android).

### Комментарии

`feature/comments` — обсуждения к каждому шагу. Доступ из `LessonScreen` через `ModalBottomSheet`.

- API возвращает плоский список — `mapToTree` группирует в дерево глубины 2 (родитель → ответы) через `groupBy { parent }`.
- Две вкладки `CommentThread`: `DISCUSSION` / `SOLUTION`.
- `toggleExpand` — четыре ветки: свернуть / уже в памяти / нет ответов / докачать через `getReplies`. Ответы сохраняются прямо в `Comment.replies`.
- `submitComment` — оптимистично вставляет новый коммент в state без перезапроса ленты (`insertComment` либо в начало списка, либо в `replies` родителя).
- `vote` — PATCH + локальное обновление `voteDelta`.

ViewModel шарится между шагами в рамках одного `LessonScreen`, при смене `stepId` — сброс state, но выбранная `thread` сохраняется.

### Профиль, Wishlist, уведомления, настройки

- `feature/profile` — данные пользователя, статистика.
- `feature/wishlist` — список сохранённых курсов.
- `feature/notifications` — уведомления Stepik с пагинацией.
- `feature/settings` — тема, язык, выход из аккаунта (через `AuthRepository.logout`).

### Сертификаты

`feature/certificates` — экран со списком полученных сертификатов пользователя.

---

## Сетевой слой

`HttpClientFactory` создаёт два Ktor-клиента:

| Клиент | Назначение |
|---|---|
| `publicClient` | для `AuthApi` (token endpoint). Без `Auth.bearer` — иначе цикл (для рефреша нужен токен). |
| `authClient` | для всех остальных API. С `Auth.bearer { loadTokens, refreshTokens, sendWithoutRequest }`. |

Общая конфигурация:
- `ContentNegotiation` + `kotlinx.serialization.Json` (`ignoreUnknownKeys = true`, `explicitNulls = false`).
- `Logging` (level = HEADERS) → Napier.
- `HttpTimeout` 30 секунд.

`sendWithoutRequest { url.host.contains("stepik.org") }` — оптимизация, сразу отправляем `Authorization`, не дожидаясь 401-challenge.

---

## Хранилище токенов

`TokenStorage` (`expect class`) с реализациями на платформах через **KVault**:
- **Android** — EncryptedSharedPreferences с ключом из Android Keystore.
- **iOS** — Keychain Services.

Хранит `access_token`, `refresh_token`, `expires_at` (абсолютный epoch ms = `now + expires_in * 1000`). `isTokenExpired()` сравнивает с буфером 10 секунд.

Защита от `AEADBadTagException` после переустановки: при ошибке инициализации удаляем зашифрованный prefs-файл и пересоздаём KVault.

---

## Локальный кеш

**Room (commonMain через KSP)** — кеш результатов прохождения шагов:
- `StepSubmissionEntity { stepId: Int (PK), status: String, hint: String? }`
- `StepSubmissionDao` — `getByStepIds`, `upsert`, `deleteByStepId`.

При входе в урок ViewModel батчем подтягивает результаты для всех шагов — пройденные шаги остаются «заблокированными» между сессиями. Статус `EVALUATION` не кешируется (только финальные `CORRECT` / `WRONG`).

**DataStore Preferences** — `UserPreferences` для флагов (например, `onboardingShown`).

---

## APK

https://drive.google.com/file/d/1rFKTUbW1Uc4XTMVBNGd7FLi_95GV_JN7/view?usp=sharing

## Презентация

https://docs.google.com/presentation/d/1Y8irVgMWa8CqvJBz_SYRVA4T-nPXdykj/edit?usp=sharing&ouid=106862959450532499215&rtpof=true&sd=true

## Сертификат прохождения курса

<img width="1666" height="1168" alt="изображение" src="https://github.com/user-attachments/assets/1c9ef03d-e738-4c9d-a49e-829e4dc1d97e" />


