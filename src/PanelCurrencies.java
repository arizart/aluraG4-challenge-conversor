import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.json.*;

public class PanelCurrencies extends PanelTemplate {

	private String exchangeAPI;
	private HashMap<String, Float> exchangeRates;
	private ArrayList<String> currencies;
	private JPanel refreshPanel;
	private JButton refreshCurrencies;

	public PanelCurrencies() {

		super();

		exchangeAPI = "https://open.er-api.com/v6/latest/USD";
		exchangeRates = new HashMap<>();
		currencies = new ArrayList<>();
		UpdateExchangeRates();

		refreshPanel = new JPanel();

		refreshCurrencies = new JButton("Actualizar divisas");
		refreshCurrencies.setName("UpdateExchangeRates");
		refreshCurrencies.addActionListener(new ClickListener());

		setOriginUnit(new JComboBox<>());
		setTargetUnit(new JComboBox<>());

		getOriginUnit().setModel(new DefaultComboBoxModel<String>(currencies.toArray(new String[0])));
		getTargetUnit().setModel(new DefaultComboBoxModel<String>(currencies.toArray(new String[0])));

		getSwapButton().addActionListener(new ClickListener());
		getConvertButton().addActionListener(new ClickListener());

		refreshPanel.add(refreshCurrencies);
		getInputsPanel().add(getInputField());
		getInputsPanel().add(getOriginUnit());

		getInputsPanel().add(getSwapButton());
		getInputsPanel().add(getTargetUnit());
		getInputsPanel().add(getConvertButton());
		getOutputPanel().add(getOutput());

		add(refreshPanel, BorderLayout.PAGE_START);
		add(getInputsPanel(), BorderLayout.CENTER);
		add(getOutputPanel(), BorderLayout.PAGE_END);
	}

	public class ClickListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String sauce = ((JButton) e.getSource()).getName();
			if (sauce == "UpdateExchangeRates") {
				UpdateExchangeRates();
			} else if (sauce == "SwapUnits") {
				// TODO: Implement SwapUnits function.
			} else if (sauce == "ConvertUnits") {
				ConvertUnits();
			}
		}
	}

	public void UpdateExchangeRates() {

		UtilHttpRequest request = new UtilHttpRequest();
		String response;

		try {
			response = request.GET(exchangeAPI);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			getOutput().setText(
					"No se pudo actualizar las divisas. Revisa tu conecci??n a internet y vuelve a intentarlo.");
			return;
		}

		JSONObject rates = new JSONObject(response).getJSONObject("rates");
		Iterator<String> keys = rates.keys();

		while (keys.hasNext()) {
			String key = keys.next();
			exchangeRates.put(key, rates.getFloat(key));
			currencies.add(key);
		}

		Collections.sort(currencies);
		getOutput().setText("Actualizaci??n correcta.");
	}

	public void ConvertUnits() {
		float value;
		try {
			value = Float.parseFloat(getInputField().getText());
		} catch (NumberFormatException nfe) {
			getOutput().setText("Digite un monto.");
			nfe.printStackTrace();
			return;
		}

		String origin = String.valueOf(getOriginUnit().getSelectedItem());
		String target = String.valueOf(getTargetUnit().getSelectedItem());

		float result = value / exchangeRates.get(origin) * exchangeRates.get(target);

		getOutput().setText(value + " " + origin + " equivale a " + String.format("%.2f", result) + " " + target);
	}
}