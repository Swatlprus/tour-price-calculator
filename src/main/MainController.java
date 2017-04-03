package main;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import main.model.*;
import main.network.Client;
import main.network.SampleHotelService;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static main.model.CharInput.FROM;
import static main.model.CharInput.TO;

/** Контроллер */
public class MainController implements Initializable {
    public TextField fromDestination;
    public DatePicker fromDate;
    public ListView<String> fromAutocomplete;

    public TextField toDestination;
    public DatePicker toDate;
    public ListView<String> toAutocomplete;

    public ListView<String> flightsListView;
    public ListView<String> hotelsListView;
    public Label priceTotal;

    private Client client;
    private CharInput charInput;

    private Itinerary itinerary;

    /** Создание переменных при инициализации контроллера */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        client = Client.getClient();
        charInput = new CharInput();
        itinerary = new Itinerary();

        setAutocompleteListener(fromAutocomplete,fromDestination,FROM);
        setAutocompleteListener(toAutocomplete,toDestination,TO);

        // Создание потока
        charInput.getCharStream()
                .map(charInput -> client.autocomplete(charInput))
                .map(AutoCompleteResponse::getCities)
                .map(FXCollections::observableList)
                .subscribe(this::addAutoCompleteResults);

        // Добавление параметра даты в обьект
        fromDate.setOnAction(event -> itinerary.setFromDate(fromDate.getValue().toString()));
        toDate.setOnAction(event -> itinerary.setToDate(toDate.getValue().toString()));
    }

    /** Отправка символа в поток */
    public void fromCharInput(KeyEvent keyEvent) {
        charInput.event(keyEvent.getCharacter(), FROM);
    }

    /** Отправка символа в поток */
    public void toCharInput(KeyEvent keyEvent) {
        charInput.event(keyEvent.getCharacter(), TO);
    }

     /** Поиск результатов по параметрам запроса */
    public void search(MouseEvent mouseEvent) {
        FlightsResponse flightPrices = client.flightPrices(itinerary);
        List<String> hotelPrices = SampleHotelService.getPricesForDestination(itinerary);
        flightsListView.setItems(FXCollections.observableArrayList(flightPrices.getPrices()));
        hotelsListView.setItems(FXCollections.observableArrayList(hotelPrices));
        flightsListView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> priceTotal.setText(newValue));
        hotelsListView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) ->
                        priceTotal.setText(String.valueOf(Integer.parseInt(priceTotal.getText()) + Integer.parseInt(newValue))));
    }

    public void fromReset(MouseEvent mouseEvent) {
        charInput.reset(FROM);
        fromDestination.setText("");
    }

    public void toReset(MouseEvent mouseEvent) {
        charInput.reset(TO);
        toDestination.setText("");
    }

    //----------------------

    /** Добавление результатов в ListView
     * @param results
     */
    private void addAutoCompleteResults(final ObservableList<String> results){
        final ListView<String> listView = getListView();
        listView.setVisible(true);
        Platform.runLater(() -> listView.setItems(results));
        listView.setPrefHeight(results.size()*listView.getFixedCellSize());
    }

    /** Получение ListView соответсвующее типу результируещей строки */
    private ListView<String> getListView(){
        switch (charInput.getType()){
            case FROM: return fromAutocomplete;
            case TO: return toAutocomplete;
            default: return null;
        }
    }

    /** Добавление слушателя на выбор параметра в выпадающем списке с последующей записью в текстовое поле
     * @param autocomplete выпадающий список
     * @param destination текстовое поле
     * @param type тип поля
     */
    private void setAutocompleteListener(final ListView<String> autocomplete, final TextField destination, final int type){
        autocomplete.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue!=null) {
                        destination.setText(newValue);
                        autocomplete.setVisible(false);
                        autocomplete.setPrefHeight(0);
                        itinerary.setDestination(newValue, type);
                    }
                });
    }
}
