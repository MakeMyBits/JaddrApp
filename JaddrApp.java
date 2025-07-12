package com.example.addressbook;

import java.awt.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.sql.*;
import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

public class JaddrApp extends JFrame {

    private String DB_URL;
    
    private DefaultListModel<Contact> contactListModel;
    
    private JList<Contact> jListContent;
    private JScrollPane jSpListView;
        
    private JLabel jLabelSearch;
    private JLabel jLabelName;
    private JLabel jLabelPhone;
    private JLabel jLabelEmail;
    
    private JLabel jLabelStatus;
    
    public JTextField jTextFieldSearch;
    public JTextField jTextFieldName;
    public JTextField jTextFieldPhone;
    public JTextField jTextFieldEmail;
    
    private JButton jButtonSearch;
    private JButton jButtonAddContact;
    private JButton jButtonDeleteContact;
    private JButton jButtonUpdateContact;
    private JButton jButtonQuitApp;
    
    private GridBagConstraints gbc;
    private GridBagLayout gbl;
    
    private JPanel jPanelInfoView;
    private JPanel jPanelSearchView;
    private JPanel jPanelCommand;
    private JPanel jPanelContainer;
	
	public JaddrApp() {
		
		super("Jaddr - A simple adressbook");
		
		this.initialize();
		this.configJFrame();
		this.configJLabel();
		this.configJButton();
		this.configJTextField();
		this.createLayout();
		
		try {
		
		this.initializeDatabase();
		this.loadContacts();
			
		} catch (SQLException ex) { ex.printStackTrace(); }
	}	
	
	private void initialize() {
		
	    this.DB_URL = "jdbc:sqlite:jaddr.db";
	
	    this.contactListModel = new DefaultListModel<Contact>();
	    this.jListContent = new JList<Contact>(contactListModel);
	    this.jSpListView = new JScrollPane(jListContent);
	    
	    this.jLabelSearch = new JLabel("Search");
	    this.jLabelName = new JLabel("Name");
	    this.jLabelPhone = new JLabel("Phone");
	    this.jLabelEmail = new JLabel("Email");
	    
	    this.jLabelStatus = new JLabel();
	    
	    this.jTextFieldSearch = new JTextField();
	    this.jTextFieldName = new JTextField();
	    this.jTextFieldPhone = new JTextField();
	    this.jTextFieldEmail = new JTextField();
	    
	    this.jButtonSearch = new JButton("Search");
	    this.jButtonAddContact = new JButton("Add");
	    this.jButtonDeleteContact = new JButton("Delete");
	    this.jButtonUpdateContact = new JButton("Update");
	    this.jButtonQuitApp = new JButton("Quit");
	    
	    this.gbc = new GridBagConstraints();
	    this.gbl = new GridBagLayout();
	    
	    this.jPanelInfoView = new JPanel(gbl);
	    this.jPanelSearchView = new JPanel(gbl);
	    this.jPanelCommand = new JPanel(gbl);
	    this.jPanelContainer = new JPanel(gbl);
	    
	}
	
	private void configJFrame() {
		
		this.setSize(600,440);
        this.setLocationRelativeTo(null);
        this.setContentPane(jPanelContainer);
        this.registerEvents();
		
	}
	
	private void registerEvents() {
		
		this.jListContent.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				
				if (!e.getValueIsAdjusting()) {
					
			        Contact selected = jListContent.getSelectedValue();
			        
			        if (selected != null) {
			        	
			        	String name = selected.getName();
			        	String phone = selected.getPhone();
			        	String email = selected.getEmail();
			        				        	
			        	jLabelStatus.setText(
			            "<html><b>Name:</b> " + name +
			            "<br><b>Phone:</b> " + phone +
			            "<br><b>Email:</b> " + email + "</html>");
			        	
			        }
					
				}				
			}			
		});
		
		this.jButtonSearch.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				try {
				performSearch();	
				} catch (SQLException ex) { ex.printStackTrace(); }
				
			}			
		});
		
		this.jButtonAddContact.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				try {
				addContact();
				} catch (SQLException ex) { ex.printStackTrace(); }
				
			}			
		});
		
		this.jButtonDeleteContact.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        Contact selected = jListContent.getSelectedValue();
		        if (selected != null) {
		        	
		            try {
		                
		            	deleteContact(selected.getId());
		                loadContacts();
		                jLabelStatus.setText("Kontakt raderad: " + selected.getName());
		            
		            } catch (SQLException ex) {
		            
		            	ex.printStackTrace();
		                jLabelStatus.setText("Fel vid radering.");
		            
		            }
		        }
		    }
		});
		
		this.jButtonUpdateContact.addActionListener(new ActionListener() {
		    
			@Override
		    public void actionPerformed(ActionEvent e) {
		        
		    	Contact selected = jListContent.getSelectedValue();
		        
		    	if (selected != null) {
		        
		    		String name = jTextFieldName.getText().trim();
		            String phone = jTextFieldPhone.getText().trim();
		            String email = jTextFieldEmail.getText().trim();
		            
		            if (!name.isEmpty()) {
		            
		            	try {
		                    updateContact(selected.getId(), name, phone, email);
		                    loadContacts();
		                    jLabelStatus.setText("Kontakt uppdaterad: " + name);
		                } catch (SQLException ex) {
		                    ex.printStackTrace();
		                    jLabelStatus.setText("Fel vid uppdatering.");
		                }
		            }
		        }
		    }
		});
		
		this.jButtonQuitApp.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        
		    	JaddrApp.this.dispose();
		    	System.exit(0);
		    	
		    }
		});
		
		this.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				
				JaddrApp.this.dispose();
								
			}			
		});
	}
	
	private void configJLabel() {
		
		Dimension dim = new Dimension();
		dim.height = 55;
		
		this.jLabelStatus.setMinimumSize(dim);
		this.jLabelStatus.setPreferredSize(dim);
		this.jLabelStatus.setMaximumSize(dim);
		
	}
	
	private void configJButton() {
		
		Dimension dim = new Dimension(75,33);
		
		this.jButtonSearch.setFocusable(false);
		this.jButtonSearch.setMinimumSize(dim);
		this.jButtonSearch.setPreferredSize(dim);
		this.jButtonSearch.setMaximumSize(dim);
		this.jButtonSearch.setToolTipText("Search contact");
		
		this.jButtonAddContact.setFocusable(false);
		this.jButtonAddContact.setMinimumSize(dim);
		this.jButtonAddContact.setPreferredSize(dim);
		this.jButtonAddContact.setMaximumSize(dim);
		this.jButtonAddContact.setToolTipText("Add current contact");
		
		this.jButtonDeleteContact.setFocusable(false);
		this.jButtonDeleteContact.setMinimumSize(dim);
		this.jButtonDeleteContact.setPreferredSize(dim);
		this.jButtonDeleteContact.setMaximumSize(dim);
		this.jButtonDeleteContact.setToolTipText("Do delete contact");
		
		this.jButtonUpdateContact.setFocusable(false);
		this.jButtonUpdateContact.setMinimumSize(dim);
		this.jButtonUpdateContact.setPreferredSize(dim);
		this.jButtonUpdateContact.setMaximumSize(dim);
		this.jButtonUpdateContact.setToolTipText("Do update contact");
		
		this.jButtonQuitApp.setFocusable(false);
		this.jButtonQuitApp.setMinimumSize(dim);
		this.jButtonQuitApp.setPreferredSize(dim);
		this.jButtonQuitApp.setMaximumSize(dim);
		this.jButtonQuitApp.setToolTipText("Exit Jaddr");
				
	}
	
	private void configJTextField() {
		
		Dimension dim = new Dimension();
		dim.height = 33;
		
		this.jTextFieldSearch.setMinimumSize(dim);
		this.jTextFieldSearch.setPreferredSize(dim);
		this.jTextFieldSearch.setMaximumSize(dim);
		this.jTextFieldSearch.setMargin(new Insets(0,4,0,4));
		
		this.jTextFieldName.setMinimumSize(dim);
		this.jTextFieldName.setPreferredSize(dim);
		this.jTextFieldName.setMaximumSize(dim);
		this.jTextFieldName.setMargin(new Insets(0,4,0,4));
		
		this.jTextFieldPhone.setMinimumSize(dim);
		this.jTextFieldPhone.setPreferredSize(dim);
		this.jTextFieldPhone.setMaximumSize(dim);
		this.jTextFieldPhone.setMargin(new Insets(0,4,0,4));
		
		this.jTextFieldEmail.setMinimumSize(dim);
		this.jTextFieldEmail.setPreferredSize(dim);
		this.jTextFieldEmail.setMaximumSize(dim);
		this.jTextFieldEmail.setMargin(new Insets(0,4,0,4));
	}
	
	private void createLayout() {
		
		gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = GridBagConstraints.BOTH;
        gbc.weighty = GridBagConstraints.BOTH;
        gbc.insets = new Insets(3,3,3,3);
        gbl.setConstraints(jSpListView,  gbc);
        this.jPanelContainer.add(jSpListView);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = GridBagConstraints.BOTH;
        gbc.weighty = GridBagConstraints.NONE;
        gbc.insets = new Insets(3,3,3,3);
        gbl.setConstraints(jPanelSearchView,  gbc);
        this.jPanelContainer.add(jPanelSearchView);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = GridBagConstraints.BOTH;
        gbc.weighty = GridBagConstraints.NONE;
        gbc.insets = new Insets(0,3,3,3);
        gbl.setConstraints(jPanelInfoView,  gbc);
        this.jPanelContainer.add(jPanelInfoView);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = GridBagConstraints.BOTH;
        gbc.weighty = GridBagConstraints.NONE;
        gbc.insets = new Insets(0,10,6,6);
        gbl.setConstraints(jLabelStatus,  gbc);
        this.jPanelContainer.add(jLabelStatus);
        
        this.populateJPanelSearchView();
        this.populateJPanelInfoView();
                
	}
	
	private void populateJPanelSearchView() {
		
		gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = GridBagConstraints.NONE;
        gbc.weighty = GridBagConstraints.NONE;
        gbc.insets = new Insets(3,3,3,3);
        gbl.setConstraints(jLabelSearch,  gbc);
        this.jPanelSearchView.add(jLabelSearch);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = GridBagConstraints.BOTH;
        gbc.weighty = GridBagConstraints.NONE;
        gbc.insets = new Insets(3,3,3,3);
        gbl.setConstraints(jTextFieldSearch,  gbc);
        this.jPanelSearchView.add(jTextFieldSearch);
        
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = GridBagConstraints.NONE;
        gbc.weighty = GridBagConstraints.NONE;
        gbc.insets = new Insets(3,0,3,3);
        gbl.setConstraints(jButtonSearch,  gbc);
        this.jPanelSearchView.add(jButtonSearch);
		
	}
	
	private void populateJPanelInfoView() {
		
		gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = GridBagConstraints.NONE;
        gbc.weighty = GridBagConstraints.NONE;
        gbc.insets = new Insets(3,6,3,0);
        gbl.setConstraints(jLabelName,  gbc);
        this.jPanelInfoView.add(jLabelName);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = GridBagConstraints.BOTH;
        gbc.weighty = GridBagConstraints.NONE;
        gbc.insets = new Insets(3,10,3,3);
        gbl.setConstraints(jTextFieldName,  gbc);
        this.jPanelInfoView.add(jTextFieldName);
		
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = GridBagConstraints.NONE;
        gbc.weighty = GridBagConstraints.NONE;
        gbc.insets = new Insets(0,6,3,0);
        gbl.setConstraints(jLabelPhone,  gbc);
        this.jPanelInfoView.add(jLabelPhone);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = GridBagConstraints.BOTH;
        gbc.weighty = GridBagConstraints.NONE;
        gbc.insets = new Insets(0,10,3,3);
        gbl.setConstraints(jTextFieldPhone,  gbc);
        this.jPanelInfoView.add(jTextFieldPhone);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = GridBagConstraints.NONE;
        gbc.weighty = GridBagConstraints.NONE;
        gbc.insets = new Insets(0,6,3,0);
        gbl.setConstraints(jLabelEmail,  gbc);
        this.jPanelInfoView.add(jLabelEmail);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = GridBagConstraints.BOTH;
        gbc.weighty = GridBagConstraints.NONE;
        gbc.insets = new Insets(0,10,3,3);
        gbl.setConstraints(jTextFieldEmail,  gbc);
        this.jPanelInfoView.add(jTextFieldEmail);
		        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = GridBagConstraints.BOTH;
        gbc.weighty = GridBagConstraints.NONE;
        gbc.insets = new Insets(6,6,3,0);
        gbl.setConstraints(jPanelCommand,  gbc);
        this.jPanelInfoView.add(jPanelCommand);
        
        this.populateJPanelCommand();
        
	}
	
	private void populateJPanelCommand() {
		
		gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = GridBagConstraints.NONE;
        gbc.weighty = GridBagConstraints.NONE;
        gbc.insets = new Insets(0,0,0,0);
        gbl.setConstraints(jButtonAddContact,  gbc);
        this.jPanelCommand.add(jButtonAddContact);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = GridBagConstraints.NONE;
        gbc.weighty = GridBagConstraints.NONE;
        gbc.insets = new Insets(0,3,0,0);
        gbl.setConstraints(jButtonDeleteContact,  gbc);
        this.jPanelCommand.add(jButtonDeleteContact);
        		
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = GridBagConstraints.NONE;
        gbc.weighty = GridBagConstraints.NONE;
        gbc.insets = new Insets(0,6,0,0);
        gbl.setConstraints(jButtonUpdateContact,  gbc);
        this.jPanelCommand.add(jButtonUpdateContact);
        
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = GridBagConstraints.NONE;
        gbc.weighty = GridBagConstraints.NONE;
        gbc.insets = new Insets(0,6,0,0);
        gbl.setConstraints(jButtonQuitApp,  gbc);
        this.jPanelCommand.add(jButtonQuitApp);
        
	}
	
	private void loadDriver() {
		
		try {
		    Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
		    e.printStackTrace();
		}		
	}
	
	private void initializeDatabase() throws SQLException {
		
		this.loadDriver();
		
		Connection conn = DriverManager.getConnection(DB_URL);
		Statement stat = conn.createStatement();
		
		String sql = "CREATE TABLE IF NOT EXISTS Contacts (" +
        "ContactID INTEGER PRIMARY KEY AUTOINCREMENT, " +
        	"Name TEXT NOT NULL, " +
        	"Phone TEXT, " +
        	"Email TEXT" +
        ");";
		
		stat.execute(sql);
		stat.close();
		conn.close();
		
	}
	
	private void performSearch() throws SQLException {
		
		loadDriver();
		
		String personName = jTextFieldSearch.getText().trim();
		if (personName == null) return;
		
		Connection conn = DriverManager.getConnection(DB_URL);
	    String sql = "SELECT * FROM contacts WHERE name LIKE ?";
	    PreparedStatement stat = conn.prepareStatement(sql);
	    stat.setString(1, "%" + personName + "%");
	    
	    int index = -1;
	    
	    for (int i = 0; i < contactListModel.getSize(); i++) {
	    	
	    	Contact contact = contactListModel.getElementAt(i);
	    	String name = contact.getName();
	    	
	    	if (name.equalsIgnoreCase(personName)) {
	            
	    		index = i;
	            break;
	        
	    	}	    	
	    }
	    
	    if (index != -1) {
	    	
	        this.jListContent.setSelectedIndex(index);
	        this.jListContent.ensureIndexIsVisible(index);
	    
	    }	    
	    
	    ResultSet rs = stat.executeQuery();
	    	    
	    if (rs.next()) {
	    	
	    	String name = rs.getString("name");
	    	String phone = rs.getString("phone");
	    	String email = rs.getString("email");
	    	
	    	this.jTextFieldName.setText(name);
	    	this.jTextFieldPhone.setText(phone);
	    	this.jTextFieldEmail.setText(email);
	    	this.jTextFieldSearch.setText(null);
	    	
	    }
	    
	    else {
	    	
	    	this.jButtonSearch.setEnabled(false);
	    	
            JOptionPane.showMessageDialog(this, "No contacts found");
            this.jTextFieldSearch.setText(null);
            
            this.jButtonSearch.setEnabled(true);
	    	
	    }
	    
	    rs.close();
	    stat.close();
	    conn.close();
	    
	}
	
	private void deleteContact(int id) throws SQLException {
	
		this.loadDriver();
		
	    Connection conn = DriverManager.getConnection(DB_URL);
	    PreparedStatement stmt = conn.prepareStatement("DELETE FROM Contacts WHERE ContactID = ?");
	    stmt.setInt(1, id);
	    stmt.executeUpdate();
	    stmt.close();
	    conn.close();
	
	}
	
	private void updateContact(int id, String name, 
		String phone, String email) 
		throws SQLException {
	    
		this.loadDriver();
	    Connection conn = DriverManager.getConnection(DB_URL);
	    PreparedStatement stmt = conn.prepareStatement(
	        "UPDATE Contacts SET Name = ?, " + 
	    	"Phone = ?, Email = ? WHERE ContactID = ?");
	    
	    stmt.setString(1, name);
	    stmt.setString(2, phone);
	    stmt.setString(3, email);
	    stmt.setInt(4, id);
	    stmt.executeUpdate();
	    stmt.close();
	    conn.close();
	
	}

	
	private void loadContacts() throws SQLException {
		
		contactListModel.clear();
		
        String sql = "SELECT ContactID,Name,Phone,Email " + 
        "FROM Contacts ORDER BY Name";
		
        Connection conn = DriverManager.getConnection(DB_URL);
        Statement stmt = conn.createStatement();
        
        ResultSet rs = stmt.executeQuery(sql);
        
        while (rs.next()) {
        	
        	int id = rs.getInt(1);
        	String name = rs.getString("Name");
        	String phone = rs.getString("Phone");
        	String email = rs.getString("Email");
        	        	
        	Contact contact = new Contact(id,name,phone,email);
        	this.contactListModel.addElement(contact);
        	
        }
        
        rs.close();
        stmt.close();
        conn.close();
        
	}
	
	private void addContact() throws SQLException {
		
		String name = jTextFieldName.getText().trim();
	    String phone = jTextFieldPhone.getText().trim();
	    String email = jTextFieldEmail.getText().trim();
	    
	    if (name.isEmpty()) return;
	    
	    if (!contactExists(name,phone,email)) {
	    
		    String sql = "INSERT INTO Contacts (Name, Phone, Email) VALUES (?, ?, ?)";
		    
		    this.loadDriver();
		    
		    Connection conn = DriverManager.getConnection(DB_URL);
		    PreparedStatement stat = conn.prepareStatement(sql);
		    stat.setString(1, name);
		    stat.setString(2, phone);
		    stat.setString(3, email);
		    
		    stat.executeUpdate();
		    stat.close();
		    conn.close();
		    
		    loadContacts();
		    
		    this.jTextFieldName.setText(null);
		    this.jTextFieldPhone.setText(null);
		    this.jTextFieldEmail.setText(null);	    
	    
	    }		
	    
	    else if (contactExists(name,phone,email)) {
	    	
	    	this.jButtonAddContact.setEnabled(false);
	    	
	    	String title = "Person already exisets";
	    	String message = "Please add a new person";
	    	JOptionPane.showMessageDialog(this, message, 
	    	title, JOptionPane.INFORMATION_MESSAGE);
	    	
	    	this.jTextFieldName.setText(null);
	    	this.jTextFieldPhone.setText(null);
	    	this.jTextFieldEmail.setText(null);
	    	
	    	this.jButtonAddContact.setEnabled(true);
	    	
	    }
	}
	
	private boolean contactExists(String name, String phone, String email) {
		
		boolean check = false;
		
		try {
			
		loadDriver();
		Connection conn = DriverManager.getConnection(DB_URL);
		String sql = "SELECT COUNT(*) FROM contacts "
				+ "WHERE name = ? AND phone = ? AND email = ?";
		
		PreparedStatement stat = conn.prepareStatement(sql);
		stat.setString(1, name);
		stat.setString(2, phone);
		stat.setString(3, email);
		
		ResultSet rs = stat.executeQuery();
		
		if (rs.next()) {
			
			int index = rs.getInt(1);
			
			if (index > 0) {
				
				check = true;
				
			}			
		}
		
		rs.close();
		stat.close();
		conn.close();
			
		} catch (SQLException ex) { ex.printStackTrace(); }
		
		return check;		
		
	}
		
    public static void main(String[] args) {
    	
    	JaddrApp app = new JaddrApp();
        app.setVisible(true);
    	
    }
}
