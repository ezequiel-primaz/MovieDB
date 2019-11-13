package com.example.myapplication.ui.models;

import java.util.Date;
import java.util.List;

public class MovieDetail extends Movie {

    public Date release_date;
    public long revenue;
    public int runtime;
    public String tagline;
    public String overview;
    public int vote_count;
    public List<Genre> genres;

}
