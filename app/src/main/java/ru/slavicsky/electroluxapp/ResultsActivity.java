package ru.slavicsky.electroluxapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;
import ru.slavicsky.electroluxapp.adapters.ReposAdapter;
import ru.slavicsky.electroluxapp.data.GithubRepo;
import ru.slavicsky.electroluxapp.data.JSONResponse;
import ru.slavicsky.electroluxapp.services.GithubClient;

public class ResultsActivity extends AppCompatActivity {

    @BindView(R.id.recycler_view_repos) RecyclerView recyclerView;
    public ArrayList<GithubRepo> repos;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        //passing the text input
        Bundle arguments = getIntent().getExtras();
        String query = arguments.get("query").toString();

        ButterKnife.bind(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ReposAdapter adapter = new ReposAdapter(repos);
        recyclerView.setAdapter(adapter);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .client(httpClient)
                .addConverterFactory(MoshiConverterFactory.create());
        Retrofit retrofit = builder.build();

        GithubClient client = retrofit.create(GithubClient.class);

        Call<JSONResponse> call = client.getRepos(query);

        call.enqueue(new Callback<JSONResponse>() {
            @Override
            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                JSONResponse items = response.body();
                repos = new ArrayList<>((Arrays.asList(items.getResults())));
                adapter.setRepos(repos);
            }

            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) {
                Toast.makeText(ResultsActivity.this, "error :(", Toast.LENGTH_SHORT).show();
                System.out.println("=============================================");
                t.printStackTrace();
            }
        });
    }

}
