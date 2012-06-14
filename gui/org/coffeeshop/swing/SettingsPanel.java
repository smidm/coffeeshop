package org.coffeeshop.swing;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.coffeeshop.awt.StackLayout;
import org.coffeeshop.awt.StackLayout.Orientation;
import org.coffeeshop.dialogs.OrganizedSettings;
import org.coffeeshop.dialogs.SettingsGroup;
import org.coffeeshop.dialogs.SettingsNode;
import org.coffeeshop.dialogs.SettingsValue;
import org.coffeeshop.settings.Settings;
import org.coffeeshop.settings.SettingsChangedEvent;
import org.coffeeshop.settings.SettingsListener;
import org.coffeeshop.string.StringUtils;
import org.coffeeshop.string.parsers.BoundedIntegerStringParser;
import org.coffeeshop.string.parsers.EnumeratedStringParser;
import org.coffeeshop.string.parsers.EnumeratedSubsetStringParser;
import org.coffeeshop.string.parsers.IntegerStringParser;
import org.coffeeshop.string.parsers.ParseException;
import org.coffeeshop.string.parsers.StringParser;

public class SettingsPanel extends JPanel {

	public interface SettingsRenderer {
		
		public JComponent renderComponent();
		
	}
	
	private static final long serialVersionUID = 1L;
	
	private HashMap<String, JComponent> components = new HashMap<String, JComponent>();
	
	private OrganizedSettings settings;
	
	private class ChangeListenerSpinner implements ChangeListener {

		private String key;
		private int defaultValue; 
		private JSpinner spinner;
		
		public ChangeListenerSpinner(String key, JSpinner spinner, int defaultValue) {
			this.key = key;
			this.defaultValue = defaultValue;
			this.spinner = spinner;
		}
		
		@Override
		public void stateChanged(ChangeEvent e) {
			if (e.getSource() != spinner)
				return;

			try {
				settings.setString(key, spinner.getValue().toString());
			} catch (RuntimeException ex) {
				spinner.setValue(settings.getInt(key, defaultValue));
			}
		}
		
	}
	
	private class ChangeListenerSlider implements ChangeListener {

		private String key;
		private int defaultValue; 
		private JSlider slider;
		
		public ChangeListenerSlider(String key, JSlider slider, int defaultValue) {
			this.key = key;
			this.slider = slider;
			this.defaultValue = defaultValue;
		}
		
		@Override
		public void stateChanged(ChangeEvent e) {
			if (e.getSource() != slider)
				return;

			if (slider.getValueIsAdjusting())
				return;
			try {
				settings.setInt(key, slider.getValue());
			} catch (RuntimeException ex) {
				slider.setValue(settings.getInt(key, defaultValue));
			}
		}
		
	}
	
	private class ChangeListenerCombo implements ItemListener {

		private String key;
		private JComboBox combo;
		
		public ChangeListenerCombo(String key, JComboBox combo) {
			this.key = key;
			this.combo = combo;
		}

		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getSource() != combo)
				return;

			try {
				Object obj = combo.getSelectedItem();
				if (obj != null)
					settings.setString(key, combo.getSelectedItem().toString());
			} catch (RuntimeException ex) {
				combo.setSelectedItem(settings.getString(key, null));
			}
		}
		
	}
	
	private class ChangeListenerList implements ListSelectionListener {

		private String key;
		private JList list;
		
		public ChangeListenerList(String key, JList list) {
			this.key = key;
			this.list = list;
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			
			if (e.getSource() != list)
				return;
			
			int[] selection = list.getSelectedIndices();
			
			String value = "";
			
			if (selection.length > 0) {
				value = list.getModel().getElementAt(selection[0]).toString();
		
				
				for (int i = 1; i < selection.length; i++) {
					value += "," + list.getModel().getElementAt(selection[i]).toString();
				}
			}
			
			try {
				settings.setString(key, value);
			} catch (RuntimeException ex) {
				//list.setSelectedItem(settings.getString(key));
			}
		}
		
	}
	
	
	private class ChangeListenerTextField implements DocumentListener {

		private String key;
		private JTextField field;
		
		public ChangeListenerTextField(String key, JTextField field) {
			this.key = key;
			this.field = field;
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			
			try {
				settings.setString(key, field.getText());
			} catch (RuntimeException ex) {}
			
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			try {
				settings.setString(key, field.getText());
			} catch (RuntimeException ex) {}
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			try {
				settings.setString(key, field.getText());
			} catch (RuntimeException ex) {}
		}
		
	}
	
	public SettingsPanel(OrganizedSettings settings) {
		super();
		
		this.settings = settings;
		
		build(settings);
		
		settings.addSettingsListener(new SettingsListener() {

			@Override
			public void settingsChanged(SettingsChangedEvent e) {
				
				
				
			}

			@Override
			public void storeSettings(Settings s) {
			}
			
		});
	}
	
	private void build(OrganizedSettings settings) {
		
		//setLayout(new StackLayout(Orientation.HORIZONTAL));
		setLayout(new BorderLayout());
		
		add(buildGroup(settings), BorderLayout.CENTER);
		
	}
	
	private JComponent buildGroup(SettingsGroup group) {
		
		JPanel panel = new JPanel(new StackLayout(Orientation.VERTICAL, 2, 5, true));
		
		if (!StringUtils.empty(group.getTitle())) {
			panel.setBorder(new TitledBorder(group.getTitle()));
		}
		
		for (SettingsNode n : group) {
			
			if (n instanceof SettingsGroup) {
				JComponent c = buildGroup((SettingsGroup) n);
				panel.add(c);
				continue;
			}
			
			JComponent c = buildComponent((SettingsValue) n);
			panel.add(c);
		}
		
		return panel;
	}
	
	private JComponent buildComponent(SettingsValue value) {

		StringParser p = value.getParser();
		
		if (p instanceof IntegerStringParser) {
			
			JPanel panel = new JPanel(new BorderLayout());
			
			panel.add(new JLabel(value.getTitle()), BorderLayout.WEST);
			
			JSpinner spinner = new JSpinner(new SpinnerNumberModel());
			
			spinner.setValue(settings.getInt(value.getName(), 0));
			
			//spinner.setValue(settings.getString(value.getName()));
			
			spinner.addChangeListener(new ChangeListenerSpinner(value.getName(), spinner, 0));
			
			components.put(getName(), spinner);
			
			panel.add(spinner, BorderLayout.EAST);
			
			return panel;
		}
		
		if (p instanceof BoundedIntegerStringParser) {
			
			BoundedIntegerStringParser bi = (BoundedIntegerStringParser) p;
			
			JPanel panel = new JPanel(new BorderLayout());
			
			panel.add(new JLabel(value.getTitle()), BorderLayout.NORTH);

			int defval = 0;
			
			if (bi.getMin() > 0)
				defval = bi.getMin();

			if (bi.getMax() < 0)
				defval = bi.getMax();
			
			int val = Math.min(bi.getMax(), Math.max(bi.getMin(), settings.getInt(value.getName(), defval)));

			JSlider slider = new JSlider(bi.getMin(), bi.getMax(), val);
			
			slider.addChangeListener(new ChangeListenerSlider(value.getName(), slider, defval));
			
			components.put(getName(), slider);
			
			panel.add(slider, BorderLayout.SOUTH);
			
			return panel;
		}		
				
		if (p instanceof EnumeratedSubsetStringParser) {
			
			EnumeratedSubsetStringParser bi = (EnumeratedSubsetStringParser) p;
			
			JPanel panel = new JPanel(new BorderLayout());
			
			panel.add(new JLabel(value.getTitle()), BorderLayout.NORTH);
			
			JList list = new JList(bi.getValues());
			
			list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			
			list.addListSelectionListener(new ChangeListenerList(value.getName(), list));
			
			try {
				
				@SuppressWarnings("unchecked")
				Set<String> subset = (Set<String>) bi.parse(settings.getString(value.getName(), ""));
				
				if (!subset.isEmpty()) {
					int[] indices = new int[subset.size()];
					int j = 0;
					String[] values = bi.getValues();
					
					for (int i = 0; i < values.length; i++) {
						if (subset.contains(values[i])) {
							indices[j++] = i;
						}
					}
					
					list.setSelectedIndices(indices);
					
				}
			} catch (ParseException e) {

			}
			
			components.put(getName(), list);
			
			panel.add(new JScrollPane(list), BorderLayout.SOUTH);
			
			return panel;
		}
		
		if (p instanceof EnumeratedStringParser) {
			
			EnumeratedStringParser bi = (EnumeratedStringParser) p;
			
			JPanel panel = new JPanel(new BorderLayout());
			
			panel.add(new JLabel(value.getTitle()), BorderLayout.NORTH);
			
			JComboBox combo = new JComboBox(bi.getValues());
			
			combo.addItemListener(new ChangeListenerCombo(value.getName(), combo));
			
			combo.setSelectedItem(settings.getString(value.getName(), null));
			
			components.put(getName(), combo);
			
			panel.add(combo, BorderLayout.SOUTH);
			
			return panel;
		}	
		
		if (value instanceof SettingsRenderer) {
			
			return ((SettingsRenderer) value).renderComponent();
			
		}
		
		
		JPanel panel = new JPanel(new BorderLayout());
		
		panel.add(new JLabel(value.getTitle()), BorderLayout.NORTH);
		
		JTextField line = new JTextField(settings.getString(value.getName(), ""));
		
		line.getDocument().addDocumentListener(new ChangeListenerTextField(value.getName(), line));
		
		components.put(getName(), line);
		
		panel.add(line, BorderLayout.SOUTH);
		
		return panel;
		
	}
	

	
}
