package com.zolobar.mobilization;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Александр on 23.04.2016.
 */
public class MainListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater lInflater;
    ArrayList<Artist> artists;

    MainListAdapter(Context context, ArrayList<Artist> artists) {
        this.context = context;
        this.artists = artists;
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return artists.size();
    }

    @Override
    public Object getItem(int position) {
        return artists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Используем созданные, но не используемые view
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.main_list_item, parent, false);
        }

        Artist currentArtist = getArtist(position);

        //Заполняем View в пункте списка данными об исполнителях
        //Поле - название
        if (currentArtist.name != null) {
            ((TextView) view.findViewById(R.id.text_name_main)).setText(currentArtist.name);
        }
        //Поле - жанры
        if (currentArtist.genres.length != 0) {
            String genres = "";
            for (int i = 0; i < currentArtist.genres.length - 1; ++i)
            {
                genres += currentArtist.genres[i] + ", ";
            }
            genres += currentArtist.genres[currentArtist.genres.length - 1];
            ((TextView) view.findViewById(R.id.text_genres_main)).setText(genres);
        }
        //Поле - количество альбомов
        if (currentArtist.albums != 0) {
            String albums = "Альбомов: " + currentArtist.albums;
            ((TextView) view.findViewById(R.id.text_albums_main)).setText(albums);
        }
        //Поле - Количество трэков
        if (currentArtist.tracks != 0) {
            String tracks = "Композиций: " + currentArtist.tracks;
            ((TextView) view.findViewById(R.id.text_tracks_main)).setText(tracks);
        }
        //Поле с изображением
        if (currentArtist.cover.small != null) {
            //Изображения не хешируются для уменьшения задействованной памяти
            Uri uri = Uri.parse(currentArtist.cover.small);
            ImageView imageView = (ImageView) view.findViewById(R.id.small_image_main);
            //В качастве placeholder выступают уже подгруженные другие изображения
            Picasso.with(context).load(uri).placeholder(imageView.getDrawable()).into(imageView);
        }

        return view;
    }

    Artist getArtist(int position) {
        return ((Artist) getItem(position));
    }
}
