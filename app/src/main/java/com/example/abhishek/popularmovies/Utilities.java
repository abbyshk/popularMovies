package com.example.abhishek.popularmovies;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.DisplayMetrics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by abhishek on 03/07/17.
 */

public final class Utilities {

    private static final int RESPONSE_CODE_OK = 200;
    private static final String NOT_SPECIFIED = "Not Specified";


    public static List<Movies> getMovies(String requestUrl) {

        URL url = makeUrl(requestUrl);

        if (url == null)
            return null;

        String jsonResponse = null;

        try {
            jsonResponse = makehttpRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return parseDataFromJson(jsonResponse);
    }

    public static List<Trailers> getTrailers(String requestUrl) {

        if (requestUrl == null)
            return null;

        URL url = makeUrl(requestUrl);

        if (url == null)
            return null;

        String jsonResult = null;

        try {
            jsonResult = makehttpRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return getTrailersData(jsonResult);
    }

    private static URL makeUrl(String stringUrl) {
        URL url = null;

        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    private static String makehttpRequest(URL url) throws IOException {

        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        String jsonResponse = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");

            if (httpURLConnection.getResponseCode() == RESPONSE_CODE_OK) {
                inputStream = httpURLConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (inputStream != null)
                inputStream.close();
        }

        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {

        StringBuilder output = new StringBuilder();

        InputStreamReader inputStreamReader =
                new InputStreamReader(inputStream, Charset.forName("UTF-8"));

        BufferedReader reader = new BufferedReader(inputStreamReader);
        String line = reader.readLine();

        while (line != null) {
            output.append(line);
            line = reader.readLine();
        }

        return output.toString();
    }

    private static List<Movies> parseDataFromJson(String jsonResponse) {

        if (jsonResponse == null)
            return null;

        List<Movies> moviesData = new ArrayList<>();

        try {
            JSONObject root = new JSONObject(jsonResponse);
            JSONArray results = root.getJSONArray("results");
            String title = NOT_SPECIFIED;
            String description = NOT_SPECIFIED;
            double voteAverage = 0;
            String releasedDate = NOT_SPECIFIED;
            int movieId = 0;
            Bitmap posterImage = null;
            Bitmap backdropImage = null;

            for (int i = 0; i < results.length(); i++) {

                JSONObject object = results.getJSONObject(i);

                if (object.has("title"))
                    title = object.getString("title");

                if (object.has("id"))
                    movieId = object.getInt("id");

                if (object.has("vote_average"))
                    voteAverage = object.getDouble("vote_average");

                if (object.has("overview"))
                    description = object.getString("overview");

                if (object.has("release_date"))
                    releasedDate = object.getString("release_date");

                if (object.has("poster_path"))
                    posterImage = getPoster(object.getString("poster_path"), false);

                if (object.has("backdrop_path"))
                    backdropImage = getPoster(object.getString("backdrop_path"), false);

                moviesData.add(new Movies(title, description, movieId, releasedDate, voteAverage, posterImage, backdropImage));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return moviesData;
    }

    private static Bitmap getPoster(String posterPath, boolean isThumbnail) {

        Bitmap poster = null;

        Uri.Builder baseUri = new Uri.Builder();
        String posterUrl;

        if (!isThumbnail) {
            baseUri.scheme("https")
                    .authority("image.tmdb.org")
                    .appendPath("t")
                    .appendPath("p")
                    .appendPath("w300");

            posterUrl = baseUri.toString() + posterPath;

        } else {
            baseUri.scheme("https")
                    .authority("img.youtube.com")
                    .appendPath("vi")
                    .appendPath(posterPath)
                    .appendPath("0.jpg");

            posterUrl = baseUri.toString();
        }


        URL url = makeUrl(posterUrl);

        if (url == null)
            return null;

        try {

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            InputStream inputStream = null;
            if (httpURLConnection.getResponseCode() == 200) {
                inputStream = httpURLConnection.getInputStream();
                poster = BitmapFactory.decodeStream(inputStream);

            }
            if (inputStream != null) {
                inputStream.close();
            }
            httpURLConnection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return poster;
    }

    private static List<Trailers> getTrailersData(String jsonResult) {

        List<Trailers> data = new ArrayList<>();
        String key = null;
        Bitmap thumbnail = null;
        try {
            JSONObject object = new JSONObject(jsonResult);

            if (!object.has("results"))
                return null;

            JSONArray results = object.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject resultObject = results.getJSONObject(i);

                if (resultObject.has("key"))
                    key = resultObject.getString("key");

                if (key != null)
                    thumbnail = getPoster(key, true);

                data.add(new Trailers(key, thumbnail));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return data;

    }

    public static int calculateNumberOfColumns(Context context) {

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        float displayWidth = displayMetrics.widthPixels / displayMetrics.density;

        int numberOfColumns = (int) displayWidth / 180;

        return numberOfColumns > 2 ? numberOfColumns : 2;
    }

    public static byte[] compressImage(Bitmap bmp) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        return stream.toByteArray();
    }

    public static Bitmap processImage(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static List<Reviews> getReviews(String requestUrl) {

        if (requestUrl == null)
            return null;

        URL url = makeUrl(requestUrl);

        if (url == null)
            return null;

        String jsonResponse = null;

        try {
            jsonResponse = makehttpRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (jsonResponse == null)
            return null;

        return getReviewsFromJson(jsonResponse);
    }

    private static List<Reviews> getReviewsFromJson(String jsonResponse) {

        List<Reviews> data = new ArrayList<>();

        String author = null;
        String content = null;

        try {
            JSONObject root = new JSONObject(jsonResponse);

            if (root.has("results")) {

                JSONArray objectArray = root.getJSONArray("results");

                for (int i = 0; i < objectArray.length(); i++) {

                    JSONObject resultObject = objectArray.getJSONObject(i);

                    if (resultObject.has("author"))
                        author = resultObject.getString("author");

                    if (resultObject.has("content"))
                        content = resultObject.getString("content");

                    data.add(new Reviews(author, content));

                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return data;
    }

    public static boolean getNetworkState(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

}
