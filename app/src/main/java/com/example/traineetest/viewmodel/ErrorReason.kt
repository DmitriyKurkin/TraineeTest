package com.example.traineetest.viewmodel


import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import retrofit2.HttpException

enum class ErrorReason {
    NO_INTERNET,
    SERVER_ERROR,
    UNKNOWN
}

fun Throwable.toErrorReason(): ErrorReason {
    return when (this) {
        is UnknownHostException,
        is SocketTimeoutException,
        is ConnectException -> ErrorReason.NO_INTERNET
        is HttpException -> ErrorReason.SERVER_ERROR
        else -> ErrorReason.UNKNOWN
    }
}

fun ErrorReason.criticalTitle(): String {
    return when (this) {
        ErrorReason.NO_INTERNET -> "Не могу загрузить данные"
        ErrorReason.SERVER_ERROR -> "Что-то пошло не так"
        ErrorReason.UNKNOWN -> "Неизвестная ошибка"
    }
}

fun ErrorReason.criticalSubtitle(): String {
    return when (this) {
        ErrorReason.NO_INTERNET -> "Проверь соединение с интернетом и попробуй снова."
        ErrorReason.SERVER_ERROR -> "Сервер не смог обработать запрос. Попробуй повторить позже."
        ErrorReason.UNKNOWN -> "Попробуй повторить загрузку."
    }
}

fun ErrorReason.refreshMessage(): String {
    return when (this) {
        ErrorReason.NO_INTERNET -> "Не могу обновить данные. Проверь соединение с интернетом."
        ErrorReason.SERVER_ERROR,
        ErrorReason.UNKNOWN -> "Не могу обновить данные. Что-то пошло не так."
    }
}