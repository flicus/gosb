package org.schors.data;

public class Person {
    private String name;
    private String date;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Person() {
    }

    public Person(String name, String date) {
        this.name = name;
        this.date = date;
    }

    @Override
    public String toString() {
        return "Person [name=" + name + ", date=" + date + "]";
    }

}
