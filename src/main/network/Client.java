package main.network;

import main.model.AutoCompleteResponse;
import main.model.CharInput;
import main.model.FlightsResponse;
import main.model.Itinerary;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

/** Клиент для взаимодействия с API Skyscanner */
public class Client {
    private static final String BASE_URL = "http://partners.api.skyscanner.net/";
    private static final String API_KEY = "ar859246605028105137488464917056";

    private static Client client;

    /** Получение экземпляра клиента - синглтон */
    public static Client getClient(){
        if (client == null){
            client = new Client();
            client.init();
        }

        return client;
    }

    private SkyScannerApi api;

    private Client() {
    }

    /** Инициализация клента */
    private void init(){
        OkHttpClient client = new OkHttpClient.Builder()
//                .addInterceptor(new LoggingInterceptor())
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        api = retrofit.create(SkyScannerApi.class);
    }

    /** Запрос на автозаполнение строки
     * @param charInput текущая строка
     */
    public AutoCompleteResponse autocomplete(CharInput charInput){
        return api.autocomplete(charInput.getInput(),API_KEY).blockingFirst();
    }

    /** Запрос билетов по выбранному направлению
     * @param itinerary параметры направления
     * @return
     */
    public FlightsResponse flightPrices(Itinerary itinerary){
        return api.flightsPrices(itinerary.getFromDestination(),itinerary.getToDestination(),
                itinerary.getFromDate(), itinerary.getToDate(), API_KEY).blockingFirst();
    }

//    Интерцептор для Http клиента
//    private static class LoggingInterceptor implements Interceptor {
//        @Override public Response intercept(Interceptor.Chain chain) throws IOException {
//            Request request = chain.request();
//
//            long t1 = System.nanoTime();
//            System.out.println(String.format("Sending request %s on %s%n%s",
//                    request.url(), chain.connection(), request.headers()));
//
//            Response response = chain.proceed(request);
//
//            long t2 = System.nanoTime();
//            System.out.println(String.format("Received response for %s in %.1fms%n%s",
//                    response.request().url(), (t2 - t1) / 1e6d, response.headers()));
//
//            return response;
//        }
//    }
}
