package com.cont;

public class LinkAndId {
    private String bookName;
    private String bookUrl;
    private String linkUrl;
    private String filePath;
    private String id;

    public LinkAndId(String bookName, String bookUrl, String linkUrl, String id, String filePath) {
        this.bookName = bookName;
        this.bookUrl = bookUrl;
        this.linkUrl = linkUrl;
        this.filePath = filePath;
        this.id = id;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookUrl() {
        return bookUrl;
    }

    public void setBookUrl(String bookUrl) {
        this.bookUrl = bookUrl;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bookName == null) ? 0 : bookName.hashCode());
        result = prime * result + ((bookUrl == null) ? 0 : bookUrl.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((linkUrl == null) ? 0 : linkUrl.hashCode());
        return result;
    }
}