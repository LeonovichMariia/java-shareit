package ru.practicum.shareit.messages;

public enum LogMessages {
    ADD_REQUEST("Запрос на добавление объекта {}"),
    RENEWAL_REQUEST("Запрос на обновление объекта {}"),
    GET_BY_ID_REQUEST("Запрос на получение объекта по id {}"),
    GET_ALL_REQUEST("Запрос владельцем списка всех его вещей"),
    GET_ALL_USERS("Запрос списка всех пользователей"),
    SEARCH_REQUEST("Запрос на поиск вещи"),
    REMOVE_REQUEST("Запрос на удаление пользователя {} "),
    BAD_REQUEST_STATUS("Ошибка 400!"),
    NOT_FOUND_STATUS("Ошибка 404!"),
    INTERNAL_SERVER_ERROR_STATUS("Ошибка 500!"),
    EMPTY_USER_NAME("Имя пользователя пустое"),
    EMPTY_EMAIL("Email пользователя пуст"),
    ALREADY_EXIST("Такой объект {} уже есть"),
    AVAILABLE_NULL("Статус доступности вещи отсутствует {}"),
    BLANK_TEXT("Задан пустой поисковый запрос"),
    EMPTY_NAME("Имя не может быть пустым {}"),
    EMPTY_DESCRIPTION("Описание не может быть пустым {}"),
    NOT_FOUND("Объект не найден {} "),
    ILLEGAL_ACCESS("Данный пользователь не оболадает правами доступа к вещи");


    private final String messageText;

    LogMessages(String messageText) {
        this.messageText = messageText;
    }
}