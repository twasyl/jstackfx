package io.twasyl.jstackfx.ui;


import de.jensd.fx.glyphs.octicons.OctIcon;
import de.jensd.fx.glyphs.octicons.OctIconView;
import io.twasyl.jstackfx.search.Query;
import io.twasyl.jstackfx.search.exceptions.UnparsableQueryException;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.Collection;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Control allowing to perform a search inside a set of data. The search is performed each time a key is released.
 * The control exposes two main properties to be used:
 * <ul>
 * <li>{@link #dataSetProperty()} which is the data within which the search will be performed;</li>
 * <li>{@link #resultsProperty()} which represents the results of the search.</li>
 * </ul>
 * <p>
 * The {@link #resultsProperty()} will always be a subset of {@link #dataSetProperty()}.
 *
 * @author Thierry Wasylczenko
 * @since JStackFX 1.1
 */
public class SearchField<T> extends StackPane {
    private static final Logger LOGGER = Logger.getLogger(SearchField.class.getName());

    protected TextField textField = new TextField();
    protected OctIconView icon = new OctIconView(OctIcon.SEARCH);
    protected Text numberOfResults = new Text();

    protected Query<T> query;
    protected final DoubleProperty prefColumnCount = new SimpleDoubleProperty();
    protected final ObjectProperty<Class<T>> searchingClass = new SimpleObjectProperty<>();
    protected final ListProperty<T> dataSet = new SimpleListProperty<>(FXCollections.observableArrayList());
    protected final ReadOnlyListProperty<T> results = new SimpleListProperty<>(FXCollections.observableArrayList());

    public SearchField() {
        this.getStyleClass().add("search-field");
        this.icon.getStyleClass().add("search");

        this.prefColumnCount.bindBidirectional(this.textField.prefColumnCountProperty());

        this.getChildren().addAll(this.textField, this.icon, this.numberOfResults);

        this.initializeKeyPressed();
        this.initializeNumberOfResults();
    }

    protected void initializeKeyPressed() {
        this.textField.setOnKeyReleased(event -> {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, this.textField.getText());
            }

            final String cleanedText = this.getCleanedText();

            if (cleanedText.isEmpty()) {
                this.clearResults();
                this.addResults(this.dataSet);
            } else {
                if (this.query != null && !Objects.equals(query.getRawQuery(), cleanedText)) {
                    try {
                        if (query.parse(cleanedText)) {
                            this.clearResults();

                            this.dataSet.forEach(data -> {
                                if (query.match(data)) {
                                    this.addResult(data);
                                }
                            });
                        }
                    } catch (UnparsableQueryException e) {
                        if (LOGGER.isLoggable(Level.FINE)) {
                            LOGGER.log(Level.WARNING, "Can not parse query", e);
                        }
                    }
                }
            }
        });
    }

    protected void initializeNumberOfResults() {
        this.numberOfResults.getStyleClass().add("number");
        this.numberOfResults.setWrappingWidth(50);
        this.numberOfResults.setTextAlignment(TextAlignment.RIGHT);
        this.numberOfResults.translateXProperty().bind(this.textField.widthProperty().subtract(this.numberOfResults.wrappingWidthProperty()).subtract(5));
        this.numberOfResults.textProperty().bind(this.resultsProperty().sizeProperty().asString().concat("/").concat(this.dataSetProperty().sizeProperty().asString()));
    }

    /**
     * Clear the content of the search field.
     */
    public void clear() {
        this.textField.clear();
    }

    public DoubleProperty prefColumnCountProperty() {
        return prefColumnCount;
    }

    public double getPrefColumnCount() {
        return prefColumnCount.get();
    }

    public void setPrefColumnCount(double prefColumnCount) {
        this.prefColumnCount.set(prefColumnCount);
    }

    protected String getCleanedText() {
        return this.textField.getText().trim();
    }

    protected void clearResults() {
        ((SimpleListProperty) results).clear();
    }

    protected void addResult(final T result) {
        ((SimpleListProperty) this.results).add(result);
    }

    protected void addResults(final Collection<T> results) {
        ((SimpleListProperty) this.results).addAll(results);
    }

    public ObjectProperty<Class<T>> searchingClassProperty() {
        return searchingClass;
    }

    public Class getSearchingClass() {
        return searchingClass.get();
    }

    public void setSearchingClass(Class searchingClass) {
        this.searchingClass.set(searchingClass);
        this.query = Query.create(searchingClass);
    }

    public ListProperty<T> dataSetProperty() {
        return dataSet;
    }

    public ObservableList getDataSet() {
        return dataSet.get();
    }

    public void setDataSet(ObservableList dataSet) {
        this.dataSet.set(dataSet);
        this.clearResults();
        this.addResults(this.dataSet);
    }

    public ReadOnlyListProperty<T> resultsProperty() {
        return results;
    }

    public ObservableList getResults() {
        return results.get();
    }
}
