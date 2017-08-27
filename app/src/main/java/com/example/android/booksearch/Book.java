package com.example.android.booksearch;


public class Book {

    private String title;
    private String[] author;

    public Book(String name, String a[]) {
        title = name;
        author = a;

    }

    public String getAuthor(int i) {
        return author[i];
    }

    public String getAuthors() {

        String a = "";
        for (int i = 0; i < author.length; i++) {

            a += author[i] + " ";
        }
        return a;
    }

    public void setAuthor(String[] author) {
        this.author = author;
    }

    public String getTitle() {

        return title;
    }

    public void setTitle(String bookName) {
        this.title = bookName;
    }
}
