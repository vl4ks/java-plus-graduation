**Java-plus-graduation**  — продолжение группового проекта Explore With Me, в рамках которого разработано приложение, позволяющее пользователям делиться информацией об интересных событиях и находить компанию для участия в них.

---

## Технологический стек

- Java 21  
- Spring Boot  
- Spring Cloud (Feign, Eureka, Config Server, Gateway)  
- PostgreSQL + JPA/Hibernate  
- Maven  
- Docker

---

## Архитектура

Приложение разделено на микросервисы, зарегистрированные в Eureka Discovery Server и конфигурируемые через Spring Cloud Config Server. Все внешние запросы идут через API Gateway.

| Сервис               | Описание                                            | Конфигурации в Config Server                   |
|----------------------|-----------------------------------------------------|------------------------------------------------|
| `user-service`       | Управление пользователями (CRUD)                    | `/config/core/user-service/application.yml`         |
| `event-service`      | Создание и модерация событий                        | `/config/core/event-service/application.yml`        |
| `request-service`    | Управление заявками на участие в событиях           | `/config/core/request-service/application.yml` |
| `comment-service`    | Управление комментариями пользователей              | `/config/core/comment-service/application.yml` |
| `stats-server`       | Сбор статистики просмотров и API просмотров         | `/config/stats/stats-server/application.yml`   |
| `gateway-server`     | Маршрутизация всех HTTP-запросов через шлюз         | `/config/infra/gateway-server/application.yml` |

---

## Внутреннее API (Feign-клиенты)

Сервисы взаимодействуют через Feign:

- **UserClient** (`user-service`)
- **EventClient** (`event-service`)
- **RequestClient** (`request-service`)
- **CommentClient** (`comment-service`)
- **StatClient** (`stats-server`)


---

## Внутренний REST API

### user-service (`/admin/users`)
- `GET  /admin/users` — список пользователей (ids, from, size)  
- `GET  /admin/users/{userId}` — получить пользователя по ID  
- `POST /admin/users` — создать нового пользователя  
- `DELETE /admin/users/{userId}` — удалить пользователя  

### event-service

#### Private API (`/users/{userId}/events`)
- `GET  /users/{userId}/events` — список своих событий  
- `POST /users/{userId}/events` — создать событие  
- `GET  /users/{userId}/events/{eventId}` — получить полную информацию о своём событии  
- `PATCH /users/{userId}/events/{eventId}` — обновить событие  
- `GET  /users/{userId}/events/{eventId}/requests` — заявки на своё событие  
- `PATCH /users/{userId}/events/{eventId}/requests` — изменение статусов заявок  

#### Public API (`/events`)
- `GET  /events` — список опубликованных событий (фильтры: text, categories, paid, date range, onlyAvailable, sort, from, size)  
- `GET  /events/{eventId}` — получить полное описание опубликованного события  

#### Admin API (`/admin/events`)
- `GET  /admin/events` — поиск событий (users, states, categories, date range, from, size)  
- `GET  /admin/events/{eventId}` — получить событие по ID  
- `PATCH /admin/events/{eventId}` — админ-обновление (state, fields)  
- `PUT  /admin/events/request/{eventId}` — синхронизировать `confirmedRequests`  

### request-service

### API (`/users/{userId}/requests`)
- `GET  /users/{userId}/requests` — заявки пользователя  
- `POST /users/{userId}/requests?eventId={eventId}` — создать заявку  
- `PATCH /users/{userId}/requests/{requestId}/cancel` — отменить заявку  

#### Admin API (`/admin/requests`)
- `GET  /requests/{ids}` — получить заявки по списку ID  
- `GET  /requests/event/{eventId}` — заявки по ID события  
- `PUT  /requests/status/{id}/{status}` — изменить статус заявки  

### comment-service

#### Public API (`/comments/{eventId}`)
- `GET    /comments/{eventId}` — комментарии события (сортировка, пагинация)  

#### Private API (`/users/{userId}/comments`)
- `POST   /users/{userId}/comments` — создать комментарий  
- `PATCH  /users/{userId}/comments/{commentId}` — редактировать  
- `DELETE /users/{userId}/comments/{commentId}` — удалить  
- `PUT    /users/{userId}/comments/{commentId}/like` — поставить лайк  
- `DELETE /users/{userId}/comments/{commentId}/like` — убрать лайк  

#### Admin API (`/admin/comments`)
- `PUT    /admin/comments/ban/{userId}` — забанить комментарии пользователя  
- `DELETE /admin/comments/ban/{userId}` — разбанить  
- `DELETE /admin/comments/{commentId}` — удалить конкретный комментарий  
- `GET    /admin/comments?id={commentId}` — получить комментарий по ID  

### stats-server
- `GET  /stats?start=&end=&uris=&unique=` — получить статистику просмотров  
- `POST /hit` — сохранить информацию о запросе (URI, IP, timestamp, app)  

---

## Внешний API

Спецификации доступны по ссылкам ниже:

- [Основной сервис](https://github.com/vl4ks/java-plus-graduation/blob/main/ewm-main-service-spec.json)
- [Сервис статистики](https://github.com/vl4ks/java-plus-graduation/blob/main/ewm-stats-service-spec.json)


---
