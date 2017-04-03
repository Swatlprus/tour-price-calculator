package main.network;

import io.reactivex.Observable;
import main.model.AutoCompleteResponse;
import main.model.FlightsResponse;
import retrofit2.http.*;


/** Интерфейс запросов клиента */
public interface SkyScannerApi {
    /** Запрос на автозаполнение строки
     * @param search строка для поиска
     * @param apikey API ключ
     */
    @Headers("Accept: application/json")
    @GET("/apiservices/autosuggest/v1.0/RU/RUB/ru-RU/")
    Observable<AutoCompleteResponse> autocomplete(@Query("query") String search, @Query("apikey") String apikey);

    /** Запрос на получение цен билетов
     * @param originPlace место отправления
     * @param destinationPlace место назначения
     * @param outboundPartialDate дата отправления
     * @param inboundPartialDate дата возвращения
     * @param apikey API ключ
     */
    @Headers("Accept: application/json")
    @GET("/apiservices/browsequotes/v1.0/RU/RUB/ru-RU/{originPlace}/{destinationPlace}/{outboundPartialDate}/{inboundPartialDate}")
    Observable<FlightsResponse> flightsPrices(@Path("originPlace") String originPlace,
                                              @Path("destinationPlace") String destinationPlace,
                                              @Path("outboundPartialDate") String outboundPartialDate,
                                              @Path("inboundPartialDate") String inboundPartialDate,
                                              @Query("apikey") String apikey);
}
