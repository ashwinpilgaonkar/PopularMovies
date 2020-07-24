package com.ashwinpilgaonkar.popularmovies.UI;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.ashwinpilgaonkar.popularmovies.Adapters.ImageAdapter;
import com.ashwinpilgaonkar.popularmovies.Backend.Favorite;
import com.ashwinpilgaonkar.popularmovies.Backend.FetchData;
import com.ashwinpilgaonkar.popularmovies.Models.MovieModel;
import com.ashwinpilgaonkar.popularmovies.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivityFragment extends Fragment {

    View v;

    private static final String CHOICE_SETTING_KEY = "choice";
    private static final String MOVIES_DATA_KEY = "movies";
    private static final String MOST_POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";
    private static final String FAVORITE = "favorite";

    public static ImageAdapter imageAdapter;
    public static ArrayList<MovieModel> movies = null;

    public static String CHOICE = MOST_POPULAR;
    AlertDialog themeDialog;

    @BindView(R.id.movie_gridview) GridView gridView;

    public MainActivityFragment() {
    }

    public interface Callback {
        void onItemSelected(MovieModel movie);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, v);

        if(MainActivity.theme.contentEquals("light"))
            v.setBackgroundColor(0xffe6e6e6);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(CHOICE_SETTING_KEY))
                CHOICE = savedInstanceState.getString(CHOICE_SETTING_KEY);

            if (savedInstanceState.containsKey(MOVIES_DATA_KEY)) {
                movies = savedInstanceState.getParcelableArrayList(MOVIES_DATA_KEY);
                imageAdapter.setData(movies);
            }
        }

        return v;
    }

    @Override
    public void onStart(){
        super.onStart();
        //Check if Internet connectivity is available on startup to avoid app crash.
        checkNetworkConnectivity();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem item;
        switch(CHOICE){
            case MOST_POPULAR:
                item = menu.getItem(0);
                item.setChecked(true);
                break;

            case TOP_RATED:
                item = menu.getItem(1);
                item.setChecked(true);
                break;

            case FAVORITE:
                item = menu.getItem(2);
                item.setChecked(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_action_popular) {

            if (!item.isChecked()) //To update RadioButton UI
                item.setChecked(true);

            CHOICE = MOST_POPULAR;
            checkNetworkConnectivity();

            return true;
        }

        if (id == R.id.menu_action_rating) {

            if(!item.isChecked()) //To update RadioButton UI
                item.setChecked(true);

            CHOICE = TOP_RATED;
            checkNetworkConnectivity();

            return true;
        }

        if (id == R.id.menu_action_favorites){
            if(!item.isChecked()) //To update RadioButton UI
                item.setChecked(true);

            CHOICE = FAVORITE;
            updateUI(CHOICE);
            return true;
        }

        if (id == R.id.menu_action_about) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle(getString(R.string.about_title));
            builder.setMessage(getString(R.string.about_text));

            builder.setPositiveButton(getString(R.string.about_positive),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

            builder.show();
            return true;
        }

        if (id == R.id.menu_action_theme) {
            final CharSequence[] items = {getString(R.string.theme_light),getString(R.string.theme_dark)};
            int checkeditem;

            if(MainActivity.theme.contentEquals(MainActivity.lightTheme))
                checkeditem = 0;

            else
                checkeditem = 1;

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getString(R.string.action_theme));
            builder.setSingleChoiceItems(items, checkeditem, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {

                    Intent i = getActivity().getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage( getActivity().getBaseContext().getPackageName() );
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                if(item==0 && !MainActivity.theme.contentEquals(MainActivity.lightTheme)) {
                    MainActivity.ed.putString(MainActivity.Theme, MainActivity.lightTheme);
                    MainActivity.ed.apply();
                    startActivity(i);
                    Toast.makeText(getActivity(), getString(R.string.theme_apply), Toast.LENGTH_SHORT).show();
                }

                else if(item==1 && !MainActivity.theme.contentEquals(MainActivity.darkTheme)){
                    MainActivity.ed.putString(MainActivity.Theme, MainActivity.darkTheme);
                    MainActivity.ed.apply();
                    startActivity(i);
                    Toast.makeText(getActivity(), getString(R.string.theme_apply), Toast.LENGTH_SHORT).show();
                }

                themeDialog.dismiss();
                }
            });
            themeDialog = builder.create();
            themeDialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateUI(String choice){
        if (!choice.contentEquals(FAVORITE))
            //fetch list of movies
            new FetchData(getActivity()).getMovies(CHOICE);

        else
            //fetch list of movies added to favorite section
            new Favorite(getActivity(), movies);

        //Update GridView with images
        imageAdapter = new ImageAdapter(getActivity(),  new ArrayList<MovieModel>());
        gridView.setAdapter(imageAdapter);

        //Check orientation of device and update UI accordingly
        if(getResources().getConfiguration().orientation==1)
            gridView.setNumColumns(2); //for Portrait

        else {
            if (MainActivity.isTablet)
                gridView.setNumColumns(3); //for Landscape Tablet

            else
                gridView.setNumColumns(4); //for Landscape Phone
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieModel movie = imageAdapter.getItem(position);
                ((Callback) getActivity()).onItemSelected(movie);
            }
        });
    }

    private void checkNetworkConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        //Check for Network Connectivity to prevent app crash in case no network connectivity is available
        if(!(activeNetworkInfo != null && activeNetworkInfo.isConnected())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setCancelable(false);
            builder.setTitle(getString(R.string.no_internet_connectivity_title));
            builder.setMessage(getString(R.string.no_internet_connectivity));

            builder.setPositiveButton(getString(R.string.no_internet_connectivity_positive),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            checkNetworkConnectivity();
                        }
                    });

            builder.setNegativeButton(getString(R.string.no_internet_connectivity_negative),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    });

            builder.show();
        }

        else {
            updateUI(CHOICE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (!CHOICE.contentEquals(MOST_POPULAR)) {
            outState.putString(CHOICE_SETTING_KEY, CHOICE);
        }
        if (movies != null) {
            outState.putParcelableArrayList(MOVIES_DATA_KEY, movies);
        }
        super.onSaveInstanceState(outState);
    }
}
