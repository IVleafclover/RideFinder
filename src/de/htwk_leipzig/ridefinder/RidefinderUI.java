package de.htwk_leipzig.ridefinder;

import java.io.File;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import de.htwk_leipzig.ridefinder.elasticsearch.Search;
import de.htwk_leipzig.ridefinder.model.RideLoopHelper;

@SuppressWarnings("serial")
@Theme("ridefinderTheme")
public class RidefinderUI extends UI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = RidefinderUI.class)
	public static class Servlet extends VaadinServlet {
	}

	// final Variablen
	private final String[] destinations = { "Leipzig", "Magdeburg", "Dresden", "Berlin" };
	private final String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
	private final String imagePath = basepath + "/VAADIN/themes/ridefinderTheme/images/";
	private final java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("dd.MM.yyyy");

	// Layout Objekte
	private final Panel layoutBackground = new Panel();
	private final HorizontalLayout layoutWrapper = new HorizontalLayout();
	private final VerticalLayout layoutMain = new VerticalLayout();
	private final VerticalLayout layoutSearchResults = new VerticalLayout();
	private ComboBox comboboxFrom;
	private ComboBox comboboxTo;
	private DateField datefieldDate;

	@Override
	protected void init(final VaadinRequest request) {
		initWrapper();
		initHeader();
		initEmptySearchResults();
	}

	private void initWrapper() {
		layoutBackground.setSizeFull();
		layoutBackground.setStyleName("panelBackground");
		setContent(layoutBackground);
		layoutBackground.setContent(layoutWrapper);
		layoutMain.setWidth("771px");
		layoutWrapper.addComponent(layoutMain);
		layoutWrapper.setComponentAlignment(layoutMain, Alignment.TOP_LEFT);
	}

	private void initHeader() {
		final VerticalLayout layoutHeader = new VerticalLayout();
		layoutHeader.setStyleName("layoutHeader");

		final Label labelHeaderTopSpace = new Label("&nbsp;", ContentMode.HTML);
		labelHeaderTopSpace.addStyleName("headerTopSpace");
		layoutHeader.addComponent(labelHeaderTopSpace);

		final FileResource resourceLogo = new FileResource(new File(imagePath + "logo.png"));
		final Image imageLogo = new Image(null, resourceLogo);
		layoutHeader.addComponent(imageLogo);

		initSearchInput(layoutHeader);

		final Label labelHeaderBottomSpace = new Label("&nbsp;", ContentMode.HTML);
		labelHeaderBottomSpace.addStyleName("headerBottomSpace");
		layoutHeader.addComponent(labelHeaderBottomSpace);

		layoutMain.addComponent(layoutHeader);
	}

	private void initSearchInput(final VerticalLayout layoutHeader) {
		final HorizontalLayout layoutsearchInput = new HorizontalLayout();

		comboboxFrom = new ComboBox("Von", new ArrayList<String>(Arrays.asList(destinations)));
		comboboxFrom.addStyleName("searchComponent");
		layoutsearchInput.addComponent(comboboxFrom);

		final Button buttonChangeFromAndTo = new Button("< >");
		buttonChangeFromAndTo.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(final ClickEvent event) {
				changeFromAndTo();
			}
		});
		buttonChangeFromAndTo.addStyleName("searchComponent");
		layoutsearchInput.addComponent(buttonChangeFromAndTo);
		layoutsearchInput.setComponentAlignment(buttonChangeFromAndTo, Alignment.BOTTOM_LEFT);

		comboboxTo = new ComboBox("Nach", new ArrayList<String>(Arrays.asList(destinations)));
		comboboxTo.addStyleName("searchComponent");
		layoutsearchInput.addComponent(comboboxTo);

		datefieldDate = new DateField("Datum", new Date());
		datefieldDate.addStyleName("searchComponent");
		layoutsearchInput.addComponent(datefieldDate);

		final Button buttonSearch = new Button("Suchen");
		buttonSearch.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(final ClickEvent event) {
				if (validateInputs()) {
					removeSearchResults();
					addSearchResults();
				}
			}
		});
		buttonSearch.addStyleName("searchComponent");
		layoutsearchInput.addComponent(buttonSearch);
		layoutsearchInput.setComponentAlignment(buttonSearch, Alignment.BOTTOM_LEFT);
		layoutHeader.addComponent(layoutsearchInput);
		layoutHeader.setComponentAlignment(layoutsearchInput, Alignment.TOP_LEFT);

	}

	private boolean validateInputs() {
		boolean isValid = true;
		if (comboboxFrom.getValue() == null) {
			comboboxFrom.addStyleName("errorField");
			isValid = false;
		} else {
			comboboxFrom.removeStyleName("errorField");
		}
		if (comboboxTo.getValue() == null) {
			comboboxTo.addStyleName("errorField");
			isValid = false;
		} else {
			comboboxTo.removeStyleName("errorField");
		}
		if (!datefieldDate.isValid()) {
			isValid = false;
		}
		return isValid;
	}

	private void changeFromAndTo() {
		final Object tempValue = comboboxFrom.getValue();
		comboboxFrom.setValue(comboboxTo.getValue());
		comboboxTo.setValue(tempValue);
	}

	private void initEmptySearchResults() {
		layoutMain.addComponent(layoutSearchResults);
	}

	private void addSearchResults() {
		try {
			SearchResponse searchResponse;
			searchResponse = Search.searchWithOutClient((String) comboboxFrom.getValue(),
					(String) comboboxTo.getValue(), dateFormat.format(datefieldDate.getValue()));

			if (searchResponse.getHits().getTotalHits() > 0) {

				final List<RideLoopHelper> searchResults = new ArrayList<>();
				for (final SearchHit hit : searchResponse.getHits()) {
					searchResults.add(new RideLoopHelper(hit.getId(), hit.getSource()));
				}

				Collections.sort(searchResults);

				for (final RideLoopHelper hit : searchResults) {
					final BrowserWindowOpener linkToRide = new BrowserWindowOpener(new ExternalResource(hit.getId()));

					final HorizontalLayout layoutSearchResult = new HorizontalLayout();
					layoutSearchResult.setWidth("100%");
					layoutSearchResult.addStyleName("layoutSearchResult");
					addProviderImageForSearchResult(hit, layoutSearchResult);
					addImageAndLabelForSearchResultField(hit.getTime(), "time_logo.png", layoutSearchResult);
					addImageAndLabelForSearchResultField(hit.getSeat(), "seat_logo.png", layoutSearchResult);
					addImageAndLabelForSearchResultField(hit.getPrice(), "euro_logo.png", layoutSearchResult);

					linkToRide.extend(layoutSearchResult);

					layoutSearchResults.addComponent(layoutSearchResult);
					layoutSearchResults.setComponentAlignment(layoutSearchResult, Alignment.TOP_LEFT);
				}
			} else {
				final Label labelNoResult = new Label("Es wurde keine Mitfahrgelegenheit von " + comboboxFrom.getValue()
						+ " nach " + comboboxTo.getValue() + " am " + dateFormat.format(datefieldDate.getValue())
						+ " gefunden.");
				labelNoResult.addStyleName("labelNoResult");
				labelNoResult.setWidth("100%");
				layoutSearchResults.addComponent(labelNoResult);
			}
		} catch (final UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void addProviderImageForSearchResult(final RideLoopHelper hit, final HorizontalLayout layoutSearchResult) {
		final String logoPath = loadHitLogoPath(hit);
		final FileResource resourceLogo = new FileResource(new File(logoPath));
		final Image imageLogo = new Image(null, resourceLogo);
		layoutSearchResult.addComponent(imageLogo);
	}

	private void addImageAndLabelForSearchResultField(final String hitFieldContent, final String hitLogoPath,
			final HorizontalLayout layoutSearchResult) {
		final HorizontalLayout layoutLablAndImage = new HorizontalLayout();
		FileResource resourceLogo;
		Image imageLogo;
		layoutLablAndImage.addComponent(new Label(hitFieldContent));
		resourceLogo = new FileResource(new File(imagePath + hitLogoPath));
		imageLogo = new Image(null, resourceLogo);
		layoutLablAndImage.addComponent(imageLogo);
		layoutSearchResult.addComponent(layoutLablAndImage);
		layoutSearchResult.setComponentAlignment(layoutLablAndImage, Alignment.MIDDLE_RIGHT);
	}

	private String loadHitLogoPath(final RideLoopHelper hit) {
		String logoPath = imagePath;
		switch (hit.getProvider()) {
		case "fahrgemeinschaft":
			logoPath += "fahrgemeinschaft_logo.png";
			break;

		case "bessermitfahren":
			logoPath += "bessermitfahren_logo.png";
			break;

		default:
			break;
		}
		return logoPath;
	}

	private void removeSearchResults() {
		layoutSearchResults.removeAllComponents();
	}

}