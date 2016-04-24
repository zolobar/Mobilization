package com.zolobar.mobilization;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class ArtistInfoActivity extends AppCompatActivity {

    Context context;
    //Данные о текущем (отображаемом) артисте
    Artist artist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Анимация при открытии активити
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_info);

        context = this;

        //До загрузки изображения не отображать TextView Биография
        findViewById(R.id.biography_lable_info).setVisibility(View.INVISIBLE);

        //Установка и отображение кнопки назад
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Получение данных из главного активити
        Intent intent = getIntent();
        int numberOfArtist = 0;
        //Получение порядкового номера выбранного артиста из ArrayList<Artist>
        numberOfArtist = intent.getIntExtra("numberOfArtist", numberOfArtist);
        //Помещаем выбранного артиста в переменную artist
        artist = MainActivity.artistArrayList.get(numberOfArtist);

        //Установка заголовка активити - название артиста
        setTitle(artist.name);

        //Находим ImageView для отображения большой картинки
        ImageView imageView = (ImageView) findViewById(R.id.big_image_info);
        //Адрес большой картинки помещаем в переменную Uri
        Uri uri = Uri.parse(artist.cover.big);

        //Запускаем загрузку большой картинки
        //После загрузки вызывается Callback - это позволяет отобразить всю информацию
        //одновременно, при неудаче картинка не отобразится, высветится уведомление
        Picasso.with(context).load(uri).into(imageView, new com.squareup.picasso.Callback() {
            //Метод при удачной попытке загрузить картинку
            @Override
            public void onSuccess() {
                //Делаем видимым TextView Биография
                findViewById(R.id.biography_lable_info).setVisibility(View.VISIBLE);

                //TextView жанры
                if (artist.genres.length != 0) {
                    String genres = "";
                    if (artist.genres.length > 1) {
                        genres = "Жанры: ";
                    } else if (artist.genres.length == 1) {
                        genres = "Жанр: ";
                    }
                    for (int i = 0; i < artist.genres.length - 1; ++i) {
                        genres += artist.genres[i] + ", ";
                    }
                    genres += artist.genres[artist.genres.length - 1];
                    ((TextView) findViewById(R.id.artist_genres_info)).setText(genres);
                }

                //TextView альбомы
                if (artist.albums != 0) {
                    String albums = "Альбомов: " + artist.albums;
                    ((TextView) findViewById(R.id.artist_albums_info)).setText(albums);
                }

                //TextView трэки
                if (artist.tracks != 0) {
                    String tracks = "Композиций: " + artist.tracks;
                    ((TextView) findViewById(R.id.artist_tracks_info)).setText(tracks);
                }

                //TextView биография
                ((TextView) findViewById(R.id.artist_biography_info)).setText(artist.description);

                //TextView ссылка
                ((TextView) findViewById(R.id.artist_url_info)).setText(artist.link);
            }

            //При неудачной загрузке страницы
            @Override
            public void onError() {
                //Выводим уведомление о неуданой загрузке
                Toast dataChangedNotification = Toast.makeText(context, "Изображение загрузить не удалось", Toast.LENGTH_SHORT);
                dataChangedNotification.setGravity(Gravity.CENTER, 0, 0);
                dataChangedNotification.show();

                ////Делаем видимым TextView Биография
                findViewById(R.id.biography_lable_info).setVisibility(View.VISIBLE);

                //TextView жанры
                if (artist.genres.length != 0) {
                    String genres = "";
                    for (int i = 0; i < artist.genres.length - 1; ++i) {
                        genres += artist.genres[i] + ", ";
                    }
                    genres += artist.genres[artist.genres.length - 1];
                    ((TextView) findViewById(R.id.artist_genres_info)).setText(genres);
                }

                //TextView альбомы
                if (artist.albums != 0) {
                    String albums = "Альбомов: " + artist.albums;
                    ((TextView) findViewById(R.id.artist_albums_info)).setText(albums);
                }

                //TextView трэки
                if (artist.tracks != 0) {
                    String tracks = "Композиций: " + artist.tracks;
                    ((TextView) findViewById(R.id.artist_tracks_info)).setText(tracks);
                }

                //TextView биография
                ((TextView) findViewById(R.id.artist_biography_info)).setText(artist.description);

                //TextView ссылка
                ((TextView) findViewById(R.id.artist_url_info)).setText(artist.link);
            }
        });
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    //Обработчик нажатия на item в меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Ищем по id айтема
        switch (item.getItemId()) {
            //Id айтема кнопки назад
            case android.R.id.home:
                //Возвращаемся назад
                onBackPressed();
        }
        return true;
    }
}
