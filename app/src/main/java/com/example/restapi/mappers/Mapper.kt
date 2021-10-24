package com.example.restapi.mappers

interface Mapper<F, T> {
    fun map(from: F): T
}