package com.example.exchange;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.SpringLayout;
import javax.swing.table.DefaultTableModel;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;


public class ExchangeApplication {
	private JPanel panel;
	private JTextField unosPolje;
	private JTextField rezultat;
	private JComboBox<String> UlaznaValuta;
	private JComboBox<String> IzlaznaValuta;

	private Map<String, Double> tecajevi;
	private List<Double> Stecajevi = new ArrayList<>();
	public ExchangeApplication() {
		try {
			URL url = new URL("https://api.hnb.hr/tecajn-eur/v3");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			int responseCode = connection.getResponseCode();
			if(responseCode == HttpURLConnection.HTTP_OK) {
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				StringBuilder responseLines = new StringBuilder();

				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					responseLines.append(inputLine);
				}
				in.close();
				String entireResponse = responseLines.toString();
				ObjectMapper objectMapper = new ObjectMapper();
				JsonNode rootNode = objectMapper.readTree(entireResponse);


				List<String> middleCourses = new ArrayList<>();
				for (JsonNode entry : rootNode) {
					if (entry.get("drzava").asText().equals("Australija") ||
							entry.get("drzava").asText().equals("Kanada") ||
							entry.get("drzava").asText().equals("SAD") ||
							entry.get("drzava").asText().equals("Japan") ||
							entry.get("drzava").asText().equals("Velika Britanija")) {
						if (entry.has("srednji_tecaj"))
							middleCourses.add(entry.get("srednji_tecaj").toString());
					}
				}
				for(String s : middleCourses)
				{
					StringBuilder stringBuilder = new StringBuilder(s);
					stringBuilder.deleteCharAt(0);
					stringBuilder.deleteCharAt(stringBuilder.length()-1);
					for(int i = 0;i <stringBuilder.length(); ++i) {
						if (stringBuilder.charAt(i) == ',') {
							stringBuilder.setCharAt(i, '.');
							break;
						}
					}
					Stecajevi.add(Double.parseDouble(stringBuilder.toString()));
				}
			}
			} catch (Exception e) {
				e.printStackTrace();
			}







			panel = new JPanel();
			SpringLayout layout = new SpringLayout();
			panel.setLayout(layout);

			JLabel Iznos = new JLabel("Iznos");
			unosPolje = new JTextField(10);

			String[] valute = {"Valuta", "EURO", "AM.DOLAR", "JEN", "FUNTA", "AU.DOLAR","KAN.DOLAR"};
			UlaznaValuta = new JComboBox<>(valute);
			UlaznaValuta.setSelectedIndex(0);

			JLabel tekst = new JLabel("Odaberite valutu u koju želite prebaciti novac");

			String[] valute2 = {"Valuta", "EURO", "AM.DOLAR", "JEN", "FUNTA", "AU.DOLAR","KAN.DOALR"};
			IzlaznaValuta = new JComboBox<>(valute);
			IzlaznaValuta.setSelectedIndex(0);

			JLabel ispis = new JLabel("Konvertirani iznos");

			JButton konvertitrajGumb = new JButton("Konvertiraj");
			konvertitrajGumb.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					konvertiraj();
				}
			});

			rezultat = new JTextField(10);
			rezultat.setEditable(false);

			JLabel tekst2 = new JLabel("Tablica tečajeva");

			Object[][] data = {
					{"SREDNJI TEČAJ","AU.DOLAR ", "AM.DOLAR", "JEN", "FUNTA", "KAN.DOLAR"},
					{" -" ,Stecajevi.get(0).toString() ,Stecajevi.get(4).toString(),Stecajevi.get(2).toString(),Stecajevi.get(3).toString(),Stecajevi.get(1).toString()}
			};
			String[] columnNames = {"VALUTE","AU.DOLAR", "EURO", "AM.DOLAR", "JEN", "FUNTA", "KAN.DOLAR"};

			DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
			JTable table = new JTable(tableModel);
			table.setGridColor(Color.BLACK);

			tecajevi = new HashMap<>();
			tecajevi.put("AM.DOLAR", Stecajevi.get(4));
			tecajevi.put("JEN", Stecajevi.get(2));
			tecajevi.put("FUNTA", Stecajevi.get(3));
			tecajevi.put("AU.DOLAR", Stecajevi.get(0));
			tecajevi.put("KAN.DOLAR", Stecajevi.get(1));


			panel.add(Iznos);
			panel.add(unosPolje);
			panel.add(UlaznaValuta);
			panel.add(tekst);
			panel.add(IzlaznaValuta);
			panel.add(konvertitrajGumb);
			panel.add(ispis);
			panel.add(rezultat);
			panel.add(tekst2);
			panel.add(table);

			layout.putConstraint(SpringLayout.WEST, Iznos, 5, SpringLayout.WEST, panel);
			layout.putConstraint(SpringLayout.NORTH, Iznos, 0, SpringLayout.NORTH, panel);

			layout.putConstraint(SpringLayout.WEST, unosPolje, 5, SpringLayout.EAST, Iznos);
			layout.putConstraint(SpringLayout.NORTH, unosPolje, 0, SpringLayout.NORTH, Iznos);

			layout.putConstraint(SpringLayout.WEST, UlaznaValuta, 5, SpringLayout.EAST, unosPolje);
			layout.putConstraint(SpringLayout.NORTH, UlaznaValuta, 0, SpringLayout.NORTH, unosPolje);

			layout.putConstraint(SpringLayout.WEST, tekst, 5, SpringLayout.WEST, panel);
			layout.putConstraint(SpringLayout.NORTH, tekst, 5, SpringLayout.SOUTH, UlaznaValuta);

			layout.putConstraint(SpringLayout.WEST, IzlaznaValuta, 5, SpringLayout.WEST, panel);
			layout.putConstraint(SpringLayout.NORTH, IzlaznaValuta, 5, SpringLayout.SOUTH, tekst);

			layout.putConstraint(SpringLayout.WEST, konvertitrajGumb, 5, SpringLayout.EAST, IzlaznaValuta);
			layout.putConstraint(SpringLayout.NORTH, konvertitrajGumb, 0, SpringLayout.NORTH, IzlaznaValuta);

			layout.putConstraint(SpringLayout.WEST, ispis, 5, SpringLayout.WEST, panel);
			layout.putConstraint(SpringLayout.NORTH, ispis, 5, SpringLayout.SOUTH, IzlaznaValuta);

			layout.putConstraint(SpringLayout.WEST, rezultat, 5, SpringLayout.WEST, panel);
			layout.putConstraint(SpringLayout.NORTH, rezultat, 5, SpringLayout.SOUTH, ispis);

			layout.putConstraint(SpringLayout.WEST, tekst2, 5, SpringLayout.WEST, panel);
			layout.putConstraint(SpringLayout.NORTH, tekst2, 5, SpringLayout.SOUTH, rezultat);

			layout.putConstraint(SpringLayout.WEST, table, 5, SpringLayout.WEST, panel);
			layout.putConstraint(SpringLayout.NORTH, table, 10, SpringLayout.SOUTH, tekst2);
		}

		private void konvertiraj() {
			String uneseniTekst = unosPolje.getText();
			double vrijednost = 0.0;
			if (uneseniTekst.isEmpty()) {
				JOptionPane.showMessageDialog(null, "Morate unjeti vrijednost.", "Ispravak", JOptionPane.ERROR_MESSAGE);
			}
			if (!uneseniTekst.isEmpty())
			{
				try {
					vrijednost = Double.parseDouble(uneseniTekst);
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(null, "Unesena vrijednost nije valjani broj.", "Pogreška", JOptionPane.ERROR_MESSAGE);
				}
			}
			if (UlaznaValuta.getSelectedItem() == "Valuta") {
				JOptionPane.showMessageDialog(null, "Morate odabrati valutu iz koje želite konvertirati.", "Ispravak", JOptionPane.ERROR_MESSAGE);
			} if (IzlaznaValuta.getSelectedItem() == "Valuta") {
				JOptionPane.showMessageDialog(null, "Morate odabrati valutu u koju želite konvertirati.", "Ispravak", JOptionPane.ERROR_MESSAGE);
			} else {
				String uValuta = (String) UlaznaValuta.getSelectedItem();
				String iValuta = (String) IzlaznaValuta.getSelectedItem();
				if (Objects.equals(uValuta, iValuta))
					JOptionPane.showMessageDialog(null, "Ne možete konvertirati u istu valutu.", "Pogreška", JOptionPane.ERROR_MESSAGE);
				else {
					double izlaznaVrijednost = 0;
					if(Objects.equals(uValuta, "EURO"))
					{
						izlaznaVrijednost = vrijednost * tecajevi.get(iValuta);
					}
					else if(Objects.equals(iValuta, "EURO"))
					{
						izlaznaVrijednost = vrijednost / tecajevi.get(uValuta);
					}
					else
					{
						double priv = vrijednost / tecajevi.get(uValuta);
						izlaznaVrijednost =  priv * tecajevi.get(iValuta);
					}
					String s = String.format("%.2f", izlaznaVrijednost);
					rezultat.setText(s);
				}
			}

		}

		public static void main(String[] args) {
			JFrame frame = new JFrame("Mjenjacnica");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			ExchangeApplication m = new ExchangeApplication();
			frame.setContentPane(m.panel);

			frame.setSize(500, 400);
			frame.setVisible(true);
		}


		{
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
			$$$setupUI$$$();
		}

		/**
		 * Method generated by IntelliJ IDEA GUI Designer
		 * >>> IMPORTANT!! <<<
		 * DO NOT edit this method OR call it in your code!
		 *
		 * @noinspection ALL
		 */
		private void $$$setupUI$$$() {
			panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		}

		/**
		 * @noinspection ALL
		 */
		public JComponent $$$getRootComponent$$$() {
			return panel;
		}

	}