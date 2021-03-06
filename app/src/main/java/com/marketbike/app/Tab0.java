package com.marketbike.app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;

import com.marketbike.app.RefreshableListView.onListLoadMoreListener;
import com.marketbike.app.RefreshableListView.onListRefreshListener;
import com.marketbike.app.adapter.ListAdapter;
import com.marketbike.app.custom.ListItem;
import com.marketbike.app.helper.JsonHelper;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;


public class Tab0 extends Fragment implements onListRefreshListener, onListLoadMoreListener {

    private ArrayList<HashMap<String, String>> DataList;
    private HashMap map;
    private ListAdapter listAdpt;
    protected ArrayList<HashMap<String, String>> sList;
    private AsyncTask<Void, Void, Void> task;
    private Menu optionsMenu;
    private boolean isfirst = true;
    private static final int LIMIT = 10;
    private int OFFSET = 0;
    private RefreshableListView lv;
    private boolean FLAG_END;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.tab0, container, false);
        //RefreshableList lines start
        setHasOptionsMenu(true);
        this.lv = (RefreshableListView) rootView.findViewById(R.id.news_listView);
        this.lv.setOnListRefreshListener(this);//---------------------------------------------------------------Important
        this.lv.setOnListLoadMoreListener(this);
        this.lv.setDistanceFromBottom(2);
        this.lv.getListView().setFooterDividersEnabled(false);
        this.lv.getListView().setDivider(null);
        this.lv.getListView().setDividerHeight(0);
        //RefreshableList Lines end
        this.sList = new ArrayList<HashMap<String, String>>();
        this.DataList = new ArrayList<HashMap<String, String>>();
        this.firstload();

       /* AdView adView = (AdView) rootView.findViewById(R.id.adView0);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);*/

        return rootView;
    }


    private void bindList() {
        this.listAdpt = new ListAdapter(getActivity(), this.sList);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.abc_fade_in);
        this.lv.startAnimation(animation);
        this.lv.setAdapter(this.listAdpt);

    }

    private void firstload() {
        this.task = new AsyncTask<Void, Void, Void>() {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                setRefreshActionButtonState(true);
            }

            @Override
            protected Void doInBackground(Void... arg0) {
                try {
                    OFFSET = 0;
                    loadItemList(0);
                } catch (Throwable e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                bindList();
                setRefreshActionButtonState(false);
            }

        };
        this.task.execute((Void[]) null);

        this.lv.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                // a.finish();

                String URL = sList.get(position).get(ListItem.KEY_URL).toString();
                String TITLE = sList.get(position).get(ListItem.KEY_TITLE).toString();
                String ID = sList.get(position).get(ListItem.KEY_MENU_ID).toString();
                Intent newActivity = new Intent(getActivity(), News_detail.class);
                newActivity.putExtra(ListItem.KEY_URL, URL);
                newActivity.putExtra(ListItem.KEY_TITLE, TITLE);
                newActivity.putExtra(ListItem.KEY_ID, ID);
                startActivity(newActivity);

                //  overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
            }
        });


/*
AdRequest request = new AdRequest.Builder()
    .setGender(AdRequest.GENDER_FEMALE)
    .setBirthday(new GregorianCalendar(1985, 1, 1).getTime())
    .setLocation(location)
    .build();
 */

      /*  AdView adView = (AdView) this.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
*/

    }

    public void setRefreshActionButtonState(final boolean refreshing) {
        if (optionsMenu != null) {
            final MenuItem refreshItem = optionsMenu.findItem(R.id.action_refresh);
            if (refreshItem != null) {
                if (refreshing) {
                    refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
                } else {
                    refreshItem.setActionView(null);
                }
            }
        }
    }

    private void loadItemList(float size) {

        try {
            String url = "http://marketbike.zoaish.com/api/get_all_content/" + OFFSET + "/" + LIMIT;
            JSONArray data = JsonHelper.getJson(url).getJSONArray("result");
            if (data.length() == 0) {
                FLAG_END = true;
            } else {
                FLAG_END = false;
            }
            for (int i = 0; i < data.length(); i++) {

                String id = data.getJSONObject(i).getString("ID");
                String title = data.getJSONObject(i).getString("Headline");
                String shortdesc = data.getJSONObject(i).getString("Short_Description");
                String createDate = data.getJSONObject(i).getString("Create_Date");
                String thumbnail = "http://marketbike.zoaish.com/public/uploads/" + data.getJSONObject(i).getString("Thumbnail_Image");
                String thumbnail_logo = "http://marketbike.zoaish.com/public/uploads/" + data.getJSONObject(i).getString("Thumbnail_Logo");

                if (isfirst) {
                    map = new HashMap<String, String>();
                    map.put(ListItem.KEY_MENU_ID, id);
                    map.put(ListItem.KEY_TYPE, "HILIGHT");
                    map.put(ListItem.KEY_TITLE, title);
                    map.put(ListItem.KEY_DESC, shortdesc);
                    map.put(ListItem.KEY_CREATEDATE, createDate);
                    map.put(ListItem.KEY_IMAGE, thumbnail);
                    map.put(ListItem.KEY_URL, thumbnail);
                    map.put(ListItem.KEY_IMAGE_LOGO, thumbnail_logo);


                    sList.add(map);
                } else {

                    map = new HashMap<String, String>();
                    map.put(ListItem.KEY_MENU_ID, id);
                    map.put(ListItem.KEY_TYPE, "CONTENT");
                    map.put(ListItem.KEY_TITLE, title);
                    map.put(ListItem.KEY_DESC, shortdesc);
                    map.put(ListItem.KEY_CREATEDATE, createDate);
                    map.put(ListItem.KEY_IMAGE, thumbnail);
                    map.put(ListItem.KEY_URL, thumbnail);
                    map.put(ListItem.KEY_IMAGE_LOGO, thumbnail_logo);

                    sList.add(map);
                }

                isfirst = false;
                OFFSET++;
            }


        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    private void onLoad() {
        //Log.i("mylog", "onLoad: ");

        this.setRefreshActionButtonState(false);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.refresh, menu);
        this.optionsMenu = menu;
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem Item) {
        switch (Item.getItemId()) {
            case R.id.action_refresh:
                this.Refresh(this.lv);
                return true;
        }
        return super.onOptionsItemSelected(Item);
    }

    @Override
    public void LoadMore(RefreshableListView list) {
        if (FLAG_END != true) {
            this.setRefreshActionButtonState(true);
            ////This just asyncly waits 3 seconds then does the finishRefresh()
            new AsyncTask<RefreshableListView, Object, RefreshableListView>() {
                protected RefreshableListView doInBackground(RefreshableListView... params) {
                    try {
                        loadItemList(0);
                    } catch (Exception e) {
                    }
                    return params[0];

                }

                @Override
                protected void onPostExecute(RefreshableListView list) {
                    //I just finish both here to not have to write two example mocks
                    onLoad();
                    listAdpt.notifyDataSetChanged();
                    lv.finishLoadingMore();//---------------------------------------------------------------------Important
                    super.onPostExecute(list);
                }
            }.execute(list);
        }
    }


    @Override
    public void Refresh(RefreshableListView list) {
        this.setRefreshActionButtonState(true);
        OFFSET = 0;
        isfirst = true;
        FLAG_END = false;
        sList.clear();

        lv.getListView().setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        ////This just asyncly waits 3 seconds then does the finishRefresh()
        new AsyncTask<RefreshableListView, Object, RefreshableListView>() {
            protected RefreshableListView doInBackground(RefreshableListView... params) {
                try {
                    loadItemList(0);
                } catch (Exception e) {
                }
                return params[0];

            }

            @Override
            protected void onPostExecute(RefreshableListView list) {


                Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.abc_fade_in);
                lv.startAnimation(animation);

                //I just finish both here to not have to write two example mocks
                onLoad();
                listAdpt.notifyDataSetChanged();
                lv.getListView().setOnTouchListener(new View.OnTouchListener() {

                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                });

                lv.finishRefresh();//-------------------------------------------------------------------------Important


                super.onPostExecute(list);
            }
        }.execute(list);


    }
}