package ru.practicum.shareit.messages;

public interface ValidationMessages {
    String EMPTY_NAME = "Имя не может быть пустым";
    String EMPTY_DESCRIPTION = "Описание не может быть пустым";
    String END_DATA = "Дата конца бронирования должна дыть в настоящем или будущем";
    String REQUEST_CREATED_DATA = "Дата создания запроса должна быть в прошлом или настоящем";
}