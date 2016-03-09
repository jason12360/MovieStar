package app.com.example.jzhang.moviestar;

import java.io.Serializable;

/**
 * Created by jason12360 on 2016/2/27.
 */
public class MovieData  implements Serializable {
    String movieName;
    String movieImg;
    String movieOverview;
    String movieRating;
    String movieDate;
    public String getMovieRating() {
        return movieRating;
    }

    public void setMovieRating(String movieRating) {
        this.movieRating = movieRating;
    }

    public String getMovieDate() {
        return movieDate;
    }

    public void setMovieDate(String movieDate) {
        this.movieDate = movieDate;
    }


    public String getMovieOverview() {
        return movieOverview;
    }

    public void setMovieOverview(String movieOverview) {
        this.movieOverview = movieOverview;
    }


    public String getMovieName(){
        return movieName;
    }
    public String getMovieImg(){
        return movieImg;
    }
    public void setMovieName(String movieName){
        this.movieName = movieName;
    }
    public void setMovieImg(String movieImg){
        this.movieImg = movieImg;
    }
}
