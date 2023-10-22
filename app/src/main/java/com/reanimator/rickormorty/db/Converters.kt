package com.reanimator.rickormorty.db

fun String.convertUrlToIntId(): Int? =
    if (this.isEmpty()) null else this.split("/").last().toInt()

fun List<String>.convertUrlListToIdList(): List<Int?>? =
    if (this.isNullOrEmpty()) null else this.map {
        it.convertUrlToIntId()
    }

fun List<Int?>?.convertIdListToString(): String? {
    return this?.joinToString(",") { it.toString() }
}

fun List<String>.convertStringListToString(): String? =
    this.convertUrlListToIdList().convertIdListToString()

fun String.convertStringToIntList(): List<Int?>? =
    if (this.isEmpty()) null else this.split(",").map { it.toInt() }

fun String.convertStringToNonNullIntList() =
    this.split(",").map { it.toInt() }