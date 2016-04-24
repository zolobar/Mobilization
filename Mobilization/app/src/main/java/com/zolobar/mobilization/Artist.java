package com.zolobar.mobilization;

/**
 * Created by Александр on 23.04.2016.
 */
public class Artist {
    int id;
    String name;
    String[] genres;
    int tracks;
    int albums;
    String link;
    String description;
    Covers cover;

    //Переопределения метода сравнивания
    @Override
    public boolean equals(Object o) {
        Artist art = (Artist) o;

        //Объекты сравниваются по id
        if (art.id == this.id) {
            return true;
        }
        return false;
    }

    //Хэш код возвращает id как уникальный идентификатор
    @Override
    public int hashCode() {
        return id;
    }
}
