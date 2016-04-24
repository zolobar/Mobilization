package com.zolobar.mobilization;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static ArrayList<Artist> artistArrayList = new ArrayList<Artist>();

    ListView mainListView;
    SwipeRefreshLayout refreshLayout;
    Context context;
    MainListAdapter mainAdapter;
    SwingBottomInAnimationAdapter animationAdapter;

    //Для принудительного обновления данных
    Boolean onlineRefresh = false;

    //Json файл на карте памяти с данными об артистах
    final String ARTISTS_FILE = "artistsJsonFile.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Исполнители");

        //Запуск загрузки json файла в другом потоке
        new DownloadArtists().execute();
        //Запуск метода проверки изменения данных на сервере в другом потоке
        new DataOnServerChanged().execute();

        context = this;
        //Адаптер для главного listView
        mainAdapter = new MainListAdapter(context, artistArrayList);
        //Главный listView
        mainListView = (ListView) findViewById(R.id.artists_view_list);

        //Передача адаптеру анимации адаптера для listView
        animationAdapter = new SwingBottomInAnimationAdapter(mainAdapter);
        //Задаётся listView для адаптера анимации
        animationAdapter.setAbsListView(mainListView);
        mainListView.setAdapter(animationAdapter);

        //Задаётся лэйаут обновления главного активити
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_main);
        //Установка в качестве слушателя события
        refreshLayout.setOnRefreshListener(this);

        //Обработка нажатия на элемент списка
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Установка в интенте активити с информацией о выбранном артисте
                Intent intent = new Intent(context, ArtistInfoActivity.class);
                //Передача в активити с информацией об артисте порядкового номера
                //артиста в ArrayList<Artist> artists
                //Данный лист является public static и виден из всех классов
                //находится в классе данного активити
                intent.putExtra("numberOfArtist", i);
                //Запуск активити с информацией о исполнителе
                startActivity(intent);
            }
        });
    }

    //Метод вызываемый при свайпе страницы вниз
    @Override
    public void onRefresh() {
        //Запуск анимации обновления страницы
        refreshLayout.setRefreshing(true);
        //Принудительное обновление базы артистов из интернета
        onlineRefresh = true;
        //Запуск обновления страницы в другом потоке
        new DownloadArtists().execute();
    }

    //Класс для асинхронного вызова загрузки артистов
    class DownloadArtists extends AsyncTask<Void, ArrayList<Artist>, ArrayList<Artist>> {
        //Чтение данных об артистах
        private String readAll(Reader rd) throws IOException {
            //Чтение происходит в StringBuilder, безопаснее чем в String
            StringBuilder sb = new StringBuilder();
            //Проверка на конец чтения
            int cp;
            while((cp = rd.read()) != -1) {
                //Запись данных в StringBuilder
                sb.append((char) cp);
            }
            //Возвращаем String с данными об артистах
            return sb.toString();
        }

        //Читает данные из интернета, адрес задаётся с помощью url
        //Возвращает ArrayList<Artist>
        public ArrayList<Artist> readJsonFromURL(String url) throws IOException, JSONException {
            //Поток чтения с источником по url
            InputStream is = new URL(url).openStream();
            try {
                //Чтение в буфер
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                //Полученные данные в виде String в формате json
                String jsonText = readAll(rd);
                //Преобразование json->Artist[]->List->ArrayList
                Gson gson = new Gson();
                Artist[] artistDatas = gson.fromJson(jsonText, Artist[].class);
                List bufList = Arrays.asList(artistDatas);
                ArrayList<Artist> listArtists = new ArrayList<>(bufList);
                //Запись файлов в External Storage (SD)
                writeFileSD(jsonText);
                return  listArtists;
            } finally {
                //Закрытие потока чтения
                is.close();
            }
        }

        //Запись данных в External Storage
        void writeFileSD(String textToWrite) {
            //Проверяем доступность SD
            if (!Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                return;
            }
            //Получаем путь к SD
            File sdPath = Environment.getExternalStorageDirectory();
            //Добавляем свой каталог к пути
            sdPath = new File(sdPath.getAbsolutePath() + "/Mobilization");
            //Создаем каталог
            sdPath.mkdirs();
            //Формируем объект File, который содержит путь к файлу
            File sdFile = new File(sdPath, ARTISTS_FILE);
            try {
                //Запись в файл
                FileUtils.writeStringToFile(sdFile, textToWrite);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Чтение json файла из External Storage
        String readFileSD() {
            String str = "";
            //Проверяем доступность SD
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                return str;
            }
            //Получаем путь к SD
            File sdPath = Environment.getExternalStorageDirectory();
            //Добавляем свой каталог к пути
            sdPath = new File(sdPath.getAbsolutePath() + "/Mobilization");
            //Формируем объект File, который содержит путь к файлу
            File sdFile = new File(sdPath, ARTISTS_FILE);
            try {
                //Чтение
                str = FileUtils.readFileToString(sdFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return str;
        }

        //Метод загрузки данных в отдельном потоке
        @Override
        protected ArrayList<Artist> doInBackground(Void... params) {
            ArrayList<Artist> arts = new ArrayList<>();
            try {
                //Чтение файла из External Storage
                String readedJsonFromSD = readFileSD();
                //Если файл на карте присутствует и не пустой и не нужно принудительно обновить данные из интернета
                //Запускаем чтение из фала
                if (readedJsonFromSD != "" && !onlineRefresh) {
                    Gson gson = new Gson();
                    Artist[] artistDatas = gson.fromJson(readedJsonFromSD, Artist[].class);
                    //Преобразуем в ArrayList<Artist>
                    List bufList = Arrays.asList(artistDatas);
                    arts = new ArrayList<>(bufList);
                } else {
                    //Запуск чтения файла из url
                    arts = readJsonFromURL("http://download.cdn.yandex.net/mobilization-2016/artists.json");
                }
                return arts;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return arts;
        }

        @Override
        protected void onPostExecute(ArrayList<Artist> artists) {
            super.onPostExecute(artists);
            artistArrayList = artists;

            //Обновление данных в адаптере для отображения полученных данных
            MainListAdapter adapter = new MainListAdapter(context, artists);
            SwingBottomInAnimationAdapter animAdapter = new SwingBottomInAnimationAdapter(adapter);
            animAdapter.setAbsListView(mainListView);
            mainListView.setAdapter(animAdapter);

            //Завершение анимации обновления страницы
            refreshLayout.setRefreshing(false);

        }
    }

    //Класс для запуска проверки новых данных на сервере в другом потоке
    class DataOnServerChanged extends AsyncTask<Void, Boolean, Boolean> {
        //Чтение данных об артистах
        private String readAll(Reader rd) throws IOException {
            //Чтение происходит в StringBuilder, безопаснее чем в String
            StringBuilder sb = new StringBuilder();
            //Проверка на конец чтения
            int cp;
            while((cp = rd.read()) != -1) {
                //Запись данных в StringBuilder
                sb.append((char) cp);
            }
            //Возвращаем String с данными об артистах
            return sb.toString();
        }

        //Читает данные из интернета, адрес задаётся с помощью url
        //Возвращает ArrayList<Artist>
        public ArrayList<Artist> readJsonFromURL(String url) throws IOException, JSONException {
            //Поток чтения с источником по url
            InputStream is = new URL(url).openStream();
            try {
                //Чтение в буфер
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                //Полученные данные в виде String в формате json
                String jsonText = readAll(rd);
                //Преобразование json->Artist[]->List->ArrayList
                Gson gson = new Gson();
                Artist[] artistDatas = gson.fromJson(jsonText, Artist[].class);
                List bufList = Arrays.asList(artistDatas);
                ArrayList<Artist> listArtists = new ArrayList<>(bufList);
                return  listArtists;
            } finally {
                //Закрытие потока чтения
                is.close();
            }
        }

        //Метод проверки обновлённых данных на сервере, запуск в другом потоке
        @Override
        protected Boolean doInBackground(Void... params) {
            ArrayList<Artist> arts = new ArrayList<>();
            try {
                //Загрузка данных с сервера
                arts = readJsonFromURL("http://download.cdn.yandex.net/mobilization-2016/artists.json");
                //Если длина ArrayList полученная ранее не равна длине полученной сейчас - нужно обновить
                if (arts.size() != artistArrayList.size()) {
                    return true;
                //Если старые и новые данные не совпадают - обновить
                } else if (!arts.equals(artistArrayList)) {
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean dataChanged) {
            //Если появились новые данные
            if (dataChanged) {
                //Выводим соответствующее уведомление
                Toast dataChangedNotification = Toast.makeText(context, "Обновите страницу", Toast.LENGTH_SHORT);
                dataChangedNotification.setGravity(Gravity.CENTER, 0, 0);
                dataChangedNotification.show();
            }
        }

    }
}












































