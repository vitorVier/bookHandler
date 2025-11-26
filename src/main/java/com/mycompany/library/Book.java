package com.mycompany.library;

public class Book {
    private String title;
    private String author;
    private String description;
    private String thumbnail;
    private String publishedDate;
    private String pageCount;
    private String categories;
    
    public Book(String title, String author, String description, 
                String thumbnail, String publishedDate, String pageCount, String categories) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.thumbnail = thumbnail;
        this.publishedDate = publishedDate;
        this.pageCount = pageCount;
        this.categories = categories;
    }
    
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getDescription() { return description; }
    public String getThumbnail() { return thumbnail; }
    public String getPublishedDate() { return publishedDate; }
    public String getPageCount() { return pageCount; }
    public String getCategories() { return categories; }
}