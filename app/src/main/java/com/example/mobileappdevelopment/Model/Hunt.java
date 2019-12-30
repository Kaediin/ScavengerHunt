package com.example.mobileappdevelopment.Model;


import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Hunt {

    private String title, author, huntCode;

    private List<String> questions, answer1, answer2, answer3, correctAnswer;

    private List<Integer> radius;

    private List<LatLng> coordinates;

    public Hunt(){}

    public Hunt(String huntCode, String title, String author, List<String> questions, List<String> answer1, List<String> answer2, List<String> answer3, List<String> correctAnswer, List<Integer> radius, List<LatLng> coordinates){
        this.title = title;
        this.author = author;
        this.questions = questions;
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.correctAnswer = correctAnswer;
        this.radius = radius;
        this.coordinates = coordinates;
        this.huntCode = huntCode;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public List<String> getQuestions() {
        return questions;
    }

    public List<String> getAnswer1() {
        return answer1;
    }

    public List<String> getAnswer2() {
        return answer2;
    }

    public List<String> getAnswer3() {
        return answer3;
    }

    public List<String> getCorrectAnswer() {
        return correctAnswer;
    }

    public List<Integer> getRadius() {
        return radius;
    }

    public List<LatLng> getCoordinates() {
        return coordinates;
    }

    public String getHuntCode() {
        return huntCode;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setAnswer1(List<String> answer1) {
        this.answer1 = answer1;
    }

    public void setAnswer2(List<String> answer2) {
        this.answer2 = answer2;
    }

    public void setAnswer3(List<String> answer3) {
        this.answer3 = answer3;
    }

    public void setCorrectAnswer(List<String> correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public void setQuestions(List<String> questions) {
        this.questions = questions;
    }

    public void setCoordinates(List<LatLng> coordinates) {
        this.coordinates = coordinates;
    }

    public void setRadius(List<Integer> radius) {
        this.radius = radius;
    }

    public void setHuntCode(String huntCode) {
        this.huntCode = huntCode;
    }
}
