package org.tensorflow.yolo.model;

import org.tensorflow.yolo.Config;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class NetworkServicer {

    public static final String HOST_URL = Config.SERVER_HOST;
    public final Retrofit retrofit;
    public final Host host;
    public String result;

    public interface Host{
        @POST("upload/")
        Call<String> putData(@Body JsonObject param);
    }

    public NetworkServicer(){
        retrofit = new Retrofit.Builder()
                .baseUrl(HOST_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        host = retrofit.create(Host.class);
    }

    public String requestJsonObject(JsonObject jsonObject){

        System.out.println(jsonObject);
        Call<String> call = host.putData(jsonObject);

        result = null;
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call,
                                   Response<String> response) {
                result = response.body();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                call.cancel();
                try {
                    throw t;
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
        return result;
    }
}
