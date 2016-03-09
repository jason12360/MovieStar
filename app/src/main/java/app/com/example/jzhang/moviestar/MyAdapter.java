package app.com.example.jzhang.moviestar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by jason12360 on 2016/2/27.
 */
public class MyAdapter extends BaseAdapter {

    ArrayList<MovieData> myList = new ArrayList<MovieData>();
    LayoutInflater inflater;
    Context context;

    public MyAdapter(Context context, ArrayList<MovieData> myList,LayoutInflater inflater){
        this.myList = myList;
        this.context = context;
        this.inflater = inflater;
    }

    @Override
    public int getCount(){
        return myList.size();
    }

    @Override
    public MovieData getItem(int position){
        return myList.get(position);
    }

    @Override
    public long getItemId(int position){
        return 0;
    }

    @Override
    public View getView(int position, View convertView,ViewGroup parent){
        MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.grid_item_movie, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        MovieData currentMovieData = getItem(position);


        Picasso.with(context).load(currentMovieData.getMovieImg()).into(mViewHolder.src);

        return convertView;
    }
    private class MyViewHolder {
        TextView name;
        ImageView src;

        public MyViewHolder(View item) {

            src = (ImageView) item.findViewById(R.id.grid_item_movie_imageview);
        }
    }
}
