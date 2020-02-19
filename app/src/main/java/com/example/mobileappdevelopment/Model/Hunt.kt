package com.example.mobileappdevelopment.Model

import com.google.android.gms.maps.model.LatLng

class Hunt {
    var title: String? = null
    var author: String? = null
    var huntCode: String? = null
    var authorID: String? = null
    var questions: MutableList<String>? = null
    var answer1: MutableList<String>? = null
    var answer2: MutableList<String>? = null
    var answer3: MutableList<String>? = null
    var correctAnswer: MutableList<String>? = null
    var radius: MutableList<Int>? = null
    var coordinates: MutableList<LatLng>? = null
    var isPrivate = false

    constructor() {}
    constructor(
            huntCode: String?,
            title: String?,
            author: String?,
            questions: MutableList<String>?,
            answer1: MutableList<String>?,
            answer2: MutableList<String>?,
            answer3: MutableList<String>?,
            correctAnswer: MutableList<String>?,
            radius: MutableList<Int>?,
            coordinates: MutableList<LatLng>?,
            isPrivate: Boolean,
            authorID: String?) {
        this.title = title
        this.author = author
        this.questions = questions
        this.answer1 = answer1
        this.answer2 = answer2
        this.answer3 = answer3
        this.correctAnswer = correctAnswer
        this.radius = radius
        this.coordinates = coordinates
        this.huntCode = huntCode
        this.isPrivate = isPrivate
        this.authorID = authorID
    }

}