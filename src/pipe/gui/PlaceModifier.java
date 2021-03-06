package pipe.gui;

import javax.swing.JTabbedPane;

import pipe.dataLayer.Arc;
import pipe.dataLayer.BasicType;
import pipe.dataLayer.DataLayer;
import pipe.dataLayer.DataType;
import pipe.dataLayer.Place;
import pipe.dataLayer.Token;

import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ActionEvent;
import java.awt.GridBagLayout;
import javax.swing.JTextPane;
import java.awt.List;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager;
import javax.swing.JCheckBox;
import java.awt.Button;
import javax.swing.JComboBox;

public class PlaceModifier extends JPanel {
	private Place currentPlace;
	private JTextField txtName;
	private JTextField txtVariable;
	private JTextField txtCapacity;
	private JTextPane txtNewTokens;
	private JCheckBox chckbxShowAttributes;
	private JLabel lblPlaceHeaderName;
	private JLabel lblTokenHeaderName;
	private JList list;
	private boolean isNewText;
	// Place place;
	Boolean attributesVisible;
	String name;
	int capacity;
	DataLayer pnmlData;
	GuiView view;
	JRootPane rootPane;
	DefaultListModel dml;
	TitledBorder tokenTextBoxTitle;
	TitledBorder tokenListTitle;

	private String originalDataType, originalName, originalVariableValue;
	private int originalWeight;
	private int originalCapacity;
	private boolean originalAttribute;
	private JTextField txtFillTokenAmount;

	public PlaceModifier(Place _place, DataLayer _pnmlData, GuiView _view) {
		setLayout(null);

		tokenTextBoxTitle = BorderFactory.createTitledBorder("New Tokens");
		tokenListTitle = BorderFactory.createTitledBorder("Current Tokens");

		dml = new DefaultListModel();
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 400, 365);
		add(tabbedPane);

		JPanel PlaceEditor = new JPanel();
		tabbedPane.addTab("Place Editor", null, PlaceEditor, null);
		PlaceEditor.setLayout(null);

		JLabel lblVariable = new JLabel("Type:");
		lblVariable.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblVariable.setBounds(10, 37, 57, 23);
		PlaceEditor.add(lblVariable);

		JLabel lblDataType = new JLabel("Null Data Type");
		lblDataType.setFont(new Font("Monospaced", Font.PLAIN, 13));
		lblDataType.setBounds(81, 42, 262, 14);
		PlaceEditor.add(lblDataType);

		JSeparator separator = new JSeparator();
		separator.setBounds(10, 27, 375, 2);
		PlaceEditor.add(separator);

		JLabel lblName = new JLabel("Name:");
		lblName.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblName.setBounds(10, 78, 57, 23);
		PlaceEditor.add(lblName);

		JLabel lblCapacity = new JLabel("Capacity:");
		lblCapacity.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblCapacity.setBounds(10, 125, 71, 23);
		PlaceEditor.add(lblCapacity);

		txtName = new JTextField();
		txtName.setBounds(80, 81, 305, 20);
		PlaceEditor.add(txtName);
		txtName.setColumns(10);

		txtCapacity = new JTextField();
		txtCapacity.setColumns(10);
		txtCapacity.setBounds(80, 128, 307, 20);
		PlaceEditor.add(txtCapacity);

		lblPlaceHeaderName = new JLabel("ARC_NAME");
		lblPlaceHeaderName.setFont(new Font("Monospaced", Font.PLAIN, 14));
		lblPlaceHeaderName.setHorizontalAlignment(SwingConstants.CENTER);
		lblPlaceHeaderName.setBounds(10, 2, 375, 24);
		PlaceEditor.add(lblPlaceHeaderName);

		JButton btnEditorOK = new JButton("OK");
		btnEditorOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doOK();
			}
		});
		btnEditorOK.setBounds(26, 225, 144, 36);
		PlaceEditor.add(btnEditorOK);

		JButton btnEditorReset = new JButton("Reset");
		btnEditorReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblDataType.setText(originalDataType);
				txtName.setText(originalName);
				txtCapacity.setText("" + originalCapacity);
				// .setText("" + originalWeight);
			}
		});
		btnEditorReset.setBounds(216, 225, 144, 36);
		PlaceEditor.add(btnEditorReset);

		// HANDLE DATA
		currentPlace = _place;
		setDataType(lblDataType);
		setName(txtName);
		setCapacity(txtCapacity);

		setHeaderName(lblPlaceHeaderName);

		chckbxShowAttributes = new JCheckBox("Display Attributes");
		chckbxShowAttributes.setBounds(80, 172, 159, 23);
		PlaceEditor.add(chckbxShowAttributes);

		JLabel lblAttributes = new JLabel("Attributes:");
		lblAttributes.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblAttributes.setBounds(10, 170, 71, 23);
		PlaceEditor.add(lblAttributes);

		JPanel Tokens = new JPanel();
		tabbedPane.addTab("Tokens", null, Tokens, null);
		Tokens.setLayout(null);

		txtNewTokens = new JTextPane();
		txtNewTokens.setBounds(10, 47, 177, 186);
		Tokens.add(txtNewTokens);
		txtNewTokens.setBorder(tokenTextBoxTitle);

		txtNewTokens.setText(this.getNewTokenPlaceHolderText());

		JScrollPane scrollNewTokenListPane = new JScrollPane();
		scrollNewTokenListPane.setBounds(208, 41, 177, 192);

		initType();
		initializeTokens();

		list = new JList(dml);

		list.setBounds(208, 47, 177, 186);
		scrollNewTokenListPane.setViewportView(list);
		scrollNewTokenListPane.setBorder(tokenListTitle);
		Tokens.add(scrollNewTokenListPane);

		JSeparator separator_2 = new JSeparator();
		separator_2.setBounds(10, 27, 375, 2);
		Tokens.add(separator_2);

		lblTokenHeaderName = new JLabel((String) null);
		lblTokenHeaderName.setHorizontalAlignment(SwingConstants.CENTER);
		lblTokenHeaderName.setFont(new Font("Monospaced", Font.PLAIN, 14));
		lblTokenHeaderName.setBounds(10, 2, 375, 24);
		Tokens.add(lblTokenHeaderName);

		setHeaderName(lblTokenHeaderName);

		Button btnAddToken = new Button("Add Tokens");
		btnAddToken.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				if (!currentPlace.getDataType().getPow() && currentPlace.getToken().getTokenCount() > 0) {
					JOptionPane.showMessageDialog(CreateGui.getApp(), "Token count exceeds capacity!",
							"Capacity Limit Violation", JOptionPane.ERROR_MESSAGE);
					return;
				}
				doAdd();

				if (currentPlace.getDataType().getPow()) {
					int cap = currentPlace.getCapacity();
					currentPlace.setCapacity(cap + 1);
				}

				isNewText = false;
				txtCapacity.setText(Integer.toString(currentPlace.getCapacity()));
				dml.clear();
				txtNewTokens.setText(getNewTokenPlaceHolderText());
				initType();
				initializeTokens();
				// initializeTextToNewToken(true);

			}
		});

		btnAddToken.setBounds(10, 283, 118, 44);
		Tokens.add(btnAddToken);

		Button btnFillTokens = new Button("Fill Tokens");
		btnFillTokens.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				try {

					if (!currentPlace.getDataType().getPow()) {
						return;
					}

					// String value = JOptionPane.showInputDialog(this, "Enter the number of tokens
					// you want to add.", "Number of tokens", JOptionPane.OK_CANCEL_OPTION);
					int numberOfTokens = Integer.parseInt(txtFillTokenAmount.getText());
					Random random = new Random(System.nanoTime());
					for (int index = 0; index < numberOfTokens; index++) {
						Token token = currentPlace.addToken(generateTokens(random));
						dml.addElement(token.displayToken());
						currentPlace.setCapacity(currentPlace.getToken().getTokenCount());
						txtCapacity.setText(Integer.toString(currentPlace.getCapacity()));
					}

					txtFillTokenAmount.setText("");
				} catch (Exception ex) {
					txtFillTokenAmount.setText("");
				}

			}
		});
		btnFillTokens.setBounds(138, 283, 118, 44);
		Tokens.add(btnFillTokens);

		Button btnDeleteToken = new Button("Delete");
		btnDeleteToken.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doDel();
			}
		});
		btnDeleteToken.setBounds(266, 283, 118, 44);
		Tokens.add(btnDeleteToken);

		JLabel lblNewLabel = new JLabel("Fill Token Amount:");
		lblNewLabel.setBounds(238, 247, 88, 14);
		Tokens.add(lblNewLabel);

		txtFillTokenAmount = new JTextField();
		txtFillTokenAmount.setBounds(336, 244, 49, 20);
		Tokens.add(txtFillTokenAmount);
		txtFillTokenAmount.setColumns(10);

		// TOKEN LIST

		// initToken();

		// END TOKEN LIST

		// END HANDLE DATA

		// initializeTextToNewToken(true);

		PlaceEditor.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				// displayMessage("Focus gained", e);
			}

			public void focusLost(FocusEvent e) {
				try {
					if (txtName.getText().equals(currentPlace.getName())) {
						originalName = currentPlace.getName();
					}
					if (Integer.parseInt(txtCapacity.getText()) == currentPlace.getCapacity()) {
						originalCapacity = currentPlace.getCapacity();
					}
					if (chckbxShowAttributes.isSelected() == currentPlace.getAttributesVisible()) {
						originalAttribute = currentPlace.getAttributesVisible();
					}
				} catch (Exception ex) {
					System.out.println("Invalid Selection in one of the Place Editor components");
				}

			}
		});

		txtNewTokens.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				if (!isNewText) {
					txtNewTokens.setText("");
				}
			}

			public void focusLost(FocusEvent e) {
				try {
					// If user has typed something
					if (!txtNewTokens.getText().equals("")) {
						isNewText = true;
					}

					if (!isNewText) {
						txtNewTokens.setText(getNewTokenPlaceHolderText());
					}
				} catch (Exception ex) {
					System.out.println("Invalid Selection in one of the Place Editor components");
				}

			}

		});

	}

	/**
	 * Changes Data Type label
	 * 
	 * @param aDataType
	 */
	private void setDataType(JLabel aDataType) {
		DataType d = currentPlace.getDataType();
		if (d.getDef()) {
			Vector<String> types = d.getTypes();
			String s;
			if (d.getPow())
				s = "P(< ";
			else
				s = "< ";
			for (int j = 0; j < types.size(); j++) {
				s += types.get(j);
				if (j < types.size() - 1) {
					s += " ,";
				}
			}
			if (d.getPow())
				s += " >)";
			else
				s += " >";
			aDataType.setText(s);

			originalDataType = s;

		}
	}

	/**
	 * Sets the header fields on all forms
	 * 
	 * @param aHeaderName
	 */
	private void setHeaderName(JLabel aHeaderName) {
		aHeaderName.setText(currentPlace.getName());
	}

	/**
	 * Changes Name text field
	 * 
	 * @param anArc
	 */
	private void setName(JTextField aNameField) {
		originalName = currentPlace.getName();
		aNameField.setText(currentPlace.getName());

	}

	/**
	 * 
	 * @param aCapacityField
	 */
	private void setCapacity(JTextField aCapacityField) {
		originalCapacity = currentPlace.getCapacity();
		aCapacityField.setText("" + currentPlace.getCapacity());

	}

	/**
	 * 
	 * @param aCheckBox
	 */
	private void setAttribute(JCheckBox aCheckBox) {
		originalAttribute = currentPlace.getAttributesVisible();
		aCheckBox.setSelected(currentPlace.getAttributesVisible());
	}

	public String getNewTokenPlaceHolderText() {
		if (currentPlace.getDataType() != null) {
			return String.format("Input Format:%n[%s]", currentPlace.getDataType().getJoinedTypes());
		}

		return "";
	}

	private void initializeTokens() {
		if (currentPlace.getDataType() != null && currentPlace.getToken() != null) {
			for (Token token : currentPlace.getToken().listToken) {
				dml.addElement(token.displayToken());
			}
		}
	}

	private void initType() {
		dml.addElement(String.format("T:%s",
				currentPlace.getDataType() == null ? "" : currentPlace.getDataType().getStringRepresentation()));
	}

	/**
	 * 
	 */
	private void doOK() {

		// doadd();
		// view.getUndoManager().newEdit(); // new "transaction""

		String newName = txtName.getText();
		if (!newName.equals(currentPlace.getName())) {
			currentPlace.setPNObjectName(newName);
			setName(txtName);
			setHeaderName(lblPlaceHeaderName);
			setHeaderName(lblTokenHeaderName);

		}

		String newCapacity = txtCapacity.getText();
		if (Integer.parseInt(newCapacity) != currentPlace.getCapacity()) {
			currentPlace.setCapacity(Integer.parseInt(newCapacity));
			setCapacity(txtCapacity);
		}

		if (currentPlace.getAttributesVisible() != chckbxShowAttributes.isSelected()) {
			currentPlace.toggleAttributesVisible();

		}
		currentPlace.repaint();
		// exit();
	}

	/**
	 * Adds tokens manually
	 */
	private void doAdd() {
		DataType datatype = currentPlace.getDataType();
		if (datatype == null) {
			JOptionPane.showMessageDialog(CreateGui.getApp(), "Please specify datatype for place first", "",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		Pattern pattern = Pattern.compile("\\[(.*)\\](\\n?)");
		Matcher matcher = pattern.matcher(txtNewTokens.getText());
		while (matcher.find()) {
			String[] elements = matcher.group(1).split(",");
			if (elements.length != datatype.getTypes().size()) {
				JOptionPane.showMessageDialog(CreateGui.getApp(),
						String.format("Number of elements mismatched for %s. It will be ignored.", matcher.group(1)),
						"", JOptionPane.ERROR_MESSAGE);
				continue;
			}
			BasicType[] tokenElements = new BasicType[elements.length];
			for (int i = 0; i < elements.length; i++) {
				BasicType basicType = new BasicType(datatype.getTypebyIndex(i), elements[i]);
				tokenElements[i] = basicType;
			}
			currentPlace.addToken(tokenElements);
		}

	}

	private void doDel() {
		int index = list.getSelectedIndex();
		if (index != 0) {
			dml.remove(index);
			currentPlace.token.listToken.remove(index - 1);
		}
	}

	private BasicType[] generateTokens(final Random pRandom) {
		DataType dataType = currentPlace.getDataType();
		BasicType[] tokenElements = new BasicType[dataType.getTypes().size()];
		for (int i = 0; i < tokenElements.length; i++) {
			if (dataType.getTypes().get(i).equals(BasicType.TYPES[0])) {
				tokenElements[i] = new BasicType(0, pRandom.nextInt(1000), null);
			} else {
				tokenElements[i] = new BasicType(1, 0, toAlpha(pRandom.nextInt(1000)));
			}
		}

		return tokenElements;
	}

	private String toAlpha(final int pInt) {
		String temp = Integer.toString(pInt);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < temp.length(); i++) {
			char c = temp.charAt(i);
			sb.append((char) (c + 17));
		}
		return sb.toString();
	}

}
