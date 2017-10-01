package com.example.prashant_tripathi.rateradius;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Prashant_Tripathi on 29-07-2017.
 */
public class ViewAllFeedbacks extends AppCompatActivity {
    private String JSON_STRING,user,view_user,designation;
    ListView feedback_all_feeds;
    TextView feedback_avg;
    RatingBar feedback_avg_stars;
    Spinner feedback_all_sort_by;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_feedbacks_layout);
        Bundle bundle=getIntent().getExtras();

        user=bundle.getString("user");


        Toolbar top=(Toolbar)findViewById(R.id.toolbar_top);
        Toolbar bottom=(Toolbar)findViewById(R.id.toolbar_bottom);

        TextView username=(TextView)findViewById(R.id.toolbar_top_username);
        username.setText("@" + user);

        setSupportActionBar(top);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setSupportActionBar(bottom);

        top.setBackgroundColor(Color.parseColor("#87ceeb"));
        bottom.setBackgroundColor(Color.parseColor("#87ceeb"));
        Init();

        GetUserDesignation();
        GetFeedbacks(Config.URL_ALL_FEEDBACKS_NEWEST);

    }

    public void  Init(){
        feedback_all_feeds=(ListView)findViewById(R.id.view_feedbacks_layout_list);
        feedback_avg=(TextView)findViewById(R.id.view_feedbacks_layout_avg_rating);
        feedback_avg_stars=(RatingBar)findViewById(R.id.view_feedbacks_layout_avg_stars);

        feedback_all_sort_by=(Spinner)findViewById(R.id.toolbar_top_sort_by);

        feedback_all_sort_by.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    GetFeedbacks(Config.URL_ALL_FEEDBACKS_NEWEST);
                } else if (position == 1) {
                    GetFeedbacks(Config.URL_ALL_FEEDBACKS_OLDEST);
                } else if (position == 2) {
                    GetFeedbacks(Config.URL_ALL_FEEDBACKS_BEST);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }



    public void OptionSelected(View v){
        switch(v.getId()){

            case R.id.toolbar_bottom_home:
                Intent i=new Intent(getApplicationContext(),Home.class);
                i.putExtra("user",user);
                startActivity(i);
                finish();
                break;

            case R.id.toolbar_bottom_my_feedbacks:
                Intent j=new Intent(getApplicationContext(),MyFeedbacks.class);
                j.putExtra("user", user);
                startActivity(j);
                finish();
                break;

            case R.id.toolbar_bottom_edit_profile:
                Intent k=new Intent(getApplicationContext(),EditProfile.class);
                k.putExtra("user",user);
                startActivity(k);
                finish();
                break;

            case R.id.toolbar_bottom_logout:
                Intent l=new Intent(getApplicationContext(),Welcome.class);
                SharedPreferences share=getSharedPreferences("RateRadius",MODE_PRIVATE);
                SharedPreferences.Editor ed=share.edit();
                ed.remove("user");
                ed.remove("pass");
                ed.commit();
                startActivity(l);
                finish();
                break;
        }
    }


    public class SpecialAdapter extends SimpleAdapter {
        private String[] colors = new String[]{"#e0c9ad","#c0c0c0"};

        public SpecialAdapter(Context context, List<HashMap<String, String>> items, int resource, String[] from, int[] to) {
            super(context, items, resource, from, to);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            TextView category=(TextView)view.findViewById(R.id.feedback_all_list_category);

            final TextView username=(TextView)view.findViewById(R.id.feedback_all_list_username);

            username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view_user=username.getText().toString().trim();
                    Intent j=new Intent(getApplicationContext(),ViewProfile.class);
                    j.putExtra("user",user);
                    j.putExtra("view_user",view_user);
                    startActivity(j);
                    finish();
                }
            });

            String s=category.getText().toString();

            if(s.equals("H")){
                view.setBackgroundColor(Color.parseColor("#ffa07a"));
            }

            else if(s.equals("M")){
                view.setBackgroundColor(Color.parseColor("#f0e68c"));
            }

            if(s.equals("F")){
                view.setBackgroundColor(Color.parseColor("#d8bfd8"));
            }

            if(s.equals("A")){
                view.setBackgroundColor(Color.parseColor("#ffc0cb"));
            }

            if(s.equals("O")){
                view.setBackgroundColor(Color.parseColor("#8fbc8f"));
            }
            return view;

        }
    }


    private void ShowFeedbacks() {
        JSONObject jsonObject = null;
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        float avg_rating=0;
        //   ArrayList<String> l2=new ArrayList<String>();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);
            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);

                String category =jo.getString(Config.KEY_CATEGORY);
                if(category.equals("MANAGEMENT")&&!designation.contains("MANAGER")) {

                }
                else{
                    String user = jo.getString(Config.KEY_USERNAME);

                    String t=jo.getString(Config.KEY_TIME);

                    String time=t.substring(8,10)+"-"+t.substring(5,7)+"-"+t.substring(0,4)+" "+t.substring(10,t.length()-3);

                    String rating = jo.getString(Config.KEY_RATING);
                    avg_rating=avg_rating+Float.parseFloat(rating);
                    String feedback = jo.getString(Config.KEY_FEEDBACK);
                    HashMap<String, String> all = new HashMap<>();
                    all.put(Config.KEY_USERNAME, user);
                    all.put(Config.KEY_TIME, time);
                    all.put(Config.KEY_RATING, rating);
                    all.put(Config.KEY_FEEDBACK, feedback);

                    if(category.equals("HUMAN RESOURCES"))
                        all.put(Config.KEY_CATEGORY,"H");

                    else if(category.equals("MANAGEMENT"))
                        all.put(Config.KEY_CATEGORY,"M");

                    else if(category.equals("OTHERS"))
                        all.put(Config.KEY_CATEGORY,"O");

                    else if(category.equals("FOOD"))
                        all.put(Config.KEY_CATEGORY,"F");

                    else if(category.equals("ADMINISTRATION"))
                        all.put(Config.KEY_CATEGORY,"A");

                    list.add(all);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        avg_rating=avg_rating/list.size();
        if(avg_rating>=0.0&&avg_rating<=1.99){
            feedback_avg.setBackgroundColor(Color.parseColor("#FF3333"));
            feedback_avg_stars.setBackgroundColor(Color.parseColor("#FF3333"));
        }

        else if(avg_rating>=2.0&&avg_rating<=2.99){
            feedback_avg.setBackgroundColor(Color.parseColor("#E633FF"));
            feedback_avg_stars.setBackgroundColor(Color.parseColor("#E633FF"));
        }

        else if(avg_rating>=3.0&&avg_rating<=5.00){
            feedback_avg.setBackgroundColor(Color.parseColor("#33FF77"));
            feedback_avg_stars.setBackgroundColor(Color.parseColor("#33FF77"));
        }

        String rate=String.valueOf(String.format("%.2f",avg_rating));
        if(rate.equals("NaN"))
            feedback_avg.setText("-");
        else
            feedback_avg.setText(rate);

        feedback_avg_stars.setRating(avg_rating);
        feedback_avg_stars.setIsIndicator(true);

        SpecialAdapter adapter=new SpecialAdapter(ViewAllFeedbacks.this,list,R.layout.feedback_all_list,new String[]{Config.KEY_USERNAME,Config.KEY_RATING,Config.KEY_TIME,Config.KEY_CATEGORY,Config.KEY_FEEDBACK},new int[]{R.id.feedback_all_list_username,R.id.feedback_all_list_rating,R.id.feedback_all_list_date,R.id.feedback_all_list_category,R.id.feedback_all_list_feedback});
        feedback_all_feeds.setAdapter(adapter);
    }


    private void GetFeedbacks(final String url) {
        class ReceiveJson extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ViewAllFeedbacks.this, "Loading ...", "Please Wait ...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                if(JSON_STRING!=null){
                    ShowFeedbacks();
                }
                else{
                    feedback_avg.setText("-");
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.SendGetRequestParam(url);
                return s;
            }
        }

        ReceiveJson rj = new ReceiveJson();
        rj.execute();
    }




    private void CheckPrivilege() {
        JSONObject jsonObject = null;
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);
            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                designation=jo.getString(Config.KEY_DESIGNATION);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void GetUserDesignation() {
        class ReceiveJson extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ViewAllFeedbacks.this, "Loading ...", "Please Wait ...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                CheckPrivilege();
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.SendGetRequestParam(Config.URL_USER_DESIGNATION,user);
                return s;
            }
        }

        ReceiveJson rj = new ReceiveJson();
        rj.execute();
    }
}
