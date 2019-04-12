package org.tensorflow.yolo.model;

import org.tensorflow.yolo.Config;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.PUT;

public class NetworkServicer {
    public static final String HOST_URL = Config.SERVER_HOST;
    public final Retrofit retrofit;
    public final Host host;
    public String result;

    public interface Host{
        @PUT("/api")
        Call<JsonObject> putData(@Body JsonObject param);
    }

    public NetworkServicer(){
        retrofit = new Retrofit.Builder()
                .baseUrl(HOST_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        host = retrofit.create(Host.class);
    }

    public String requestJsonObject(JsonObject jsonObject){

        Call<JsonObject> call = host.putData(jsonObject);

        result = null;
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call,
                                   Response<JsonObject> response) {
                result = response.body().toString();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                call.cancel();
            }
        });
        return result;
    }
}
