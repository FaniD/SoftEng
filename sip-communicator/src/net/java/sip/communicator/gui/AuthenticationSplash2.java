/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * Portions of this software are based upon public domain software
 * originally written at the National Center for Supercomputing Applications,
 * University of Illinois, Urbana-Champaign.
 */

package net.java.sip.communicator.gui;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.File;

import net.java.sip.communicator.SipCommunicator;
import net.java.sip.communicator.common.*;
import net.java.sip.communicator.gui.event.UserCallInitiationEvent;
import net.java.sip.communicator.newuser.*;;

//import samples.accessory.StringGridBagLayout;

/**
 * Sample login splash screen
 */
public class AuthenticationSplash2
    extends JDialog
{
	protected boolean flag;
    String userName = null;
    char[] password = null;
    String name=null, surname=null, email=null, address=null;
    JTextField userNameTextField = null;
    JTextField NameTextField = null;
    JTextField surnameTextField = null;
    JTextField emailTextField = null;
    JTextField adrTextField = null;

    JLabel     realmValueLabel = null;
    JPasswordField passwordTextField = null;

    /**
     * Resource bundle with default locale
     */
    private ResourceBundle resources = null;

    /**
     * Path to the image resources
     */
    private String imagePath = null;

    /**
     * Command string for a cancel action (e.g., a button).
     * This string is never presented to the user and should
     * not be internationalized.
     */
    private String CMD_CANCEL = "cmd.cancel" /*NOI18N*/;

    /**
     * Command string for a help action (e.g., a button).
     * This string is never presented to the user and should
     * not be internationalized.
     */
    private String CMD_HELP = "cmd.help" /*NOI18N*/;

    /**
     * Command string for a save action (e.g., a button).
     * This string is never presented to the user and should
     * not be internationalized.
     */
    private String CMD_SAVE = "cmd.save" /*NOI18N*/;

    // Components we need to manipulate after creation
    private JButton saveButton = null;
    private JButton cancelButton = null;
    private JButton helpButton = null;

    /**
     * Creates new form AuthenticationSplash
     */
    public AuthenticationSplash2(Frame parent, boolean modal)
    {
        super(parent, modal);
        initResources();
        initComponents();
        pack();
        centerWindow();
    }

    /**
     * Loads locale-specific resources: strings, images, et cetera
     */
    private void initResources()
    {
        Locale locale = Locale.getDefault();
        imagePath = ".";
    }

    /**
     * Centers the window on the screen.
     */
    private void centerWindow()
    {
        Rectangle screen = new Rectangle(
            Toolkit.getDefaultToolkit().getScreenSize());
        Point center = new Point(
            (int) screen.getCenterX(), (int) screen.getCenterY());
        Point newLocation = new Point(
            center.x - this.getWidth() / 2, center.y - this.getHeight() / 2);
        if (screen.contains(newLocation.x, newLocation.y,
                            this.getWidth(), this.getHeight())) {
            this.setLocation(newLocation);
        }
    } // centerWindow()

    /**
     *
     * We use dynamic layout managers, so that layout is dynamic and will
     * adapt properly to user-customized fonts and localized text. The
     * GridBagLayout makes it easy to line up components of varying
     * sizes along invisible vertical and horizontal grid lines. It
     * is important to sketch the layout of the interface and decide
     * on the grid before writing the layout code.
     *
     * Here we actually use
     * our own subclass of GridBagLayout called StringGridBagLayout,
     * which allows us to use strings to specify constraints, rather
     * than having to create GridBagConstraints objects manually.
     *
     *
     * We use the JLabel.setLabelFor() method to connect
     * labels to what they are labeling. This allows mnemonics to work
     * and assistive to technologies used by persons with disabilities
     * to provide much more useful information to the user.
     */
    private void initComponents()
    {
    	this.flag=true;
    	while (true) {
        Container contents = getContentPane();
        contents.setLayout(new BorderLayout());

        String title = Utils.getProperty("net.java.sip.communicator.gui.AUTH_WIN_TITLE");

        if(title == null)
            title = "Registrar SignUp";

        setTitle(title);
        setResizable(false);
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent event)
            {
                dialogDone(CMD_CANCEL);
            }
        });

        // Accessibility -- all frames, dialogs, and applets should
        // have a description
        getAccessibleContext().setAccessibleDescription("Authentication Splash");

        String authPromptLabelValue = null;//Utils.getProperty("net.java.sip.communicator.gui.AUTHENTICATION_PROMPT");

        if(authPromptLabelValue  == null)
            authPromptLabelValue  = "Please complete the following fields:";

        JLabel splashLabel = new JLabel(authPromptLabelValue );
        splashLabel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        splashLabel.setHorizontalAlignment(SwingConstants.CENTER);
        splashLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        contents.add(splashLabel, BorderLayout.NORTH);

        JPanel centerPane = new JPanel();
        centerPane.setLayout(new GridBagLayout());

	//USERNAME
        userNameTextField = new JTextField(); // needed below
        // user name label
        JLabel userNameLabel = new JLabel();
        userNameLabel.setDisplayedMnemonic('U');
        // setLabelFor() allows the mnemonic to work
        userNameLabel.setLabelFor(userNameTextField);
        String userNameLabelValue = Utils.getProperty("net.java.sip.communicator.gui.USER_NAME_LABEL");
        if(userNameLabelValue == null)
            userNameLabelValue = "User name";

        int gridy = 0;

        userNameLabel.setText(userNameLabelValue);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx=0;
        c.gridy=gridy;
        c.anchor=GridBagConstraints.WEST;
        c.insets=new Insets(12,12,0,0);
        centerPane.add(userNameLabel, c);

        // user name text
        c = new GridBagConstraints();
        c.gridx=1;
        c.gridy=gridy++;
        c.fill=GridBagConstraints.HORIZONTAL;
        c.weightx=1.0;
        c.insets=new Insets(12,7,0,11);
        centerPane.add(userNameTextField, c);

        //username example
        if(GuiManager.isThisSipphoneAnywhere)
        {

            String egValue = Utils.getProperty("net.java.sip.communicator.sipphone.USER_NAME_EXAMPLE");

            if(egValue == null)
                egValue = "Example: 1-747-555-1212";

            JLabel userNameExampleLabel = new JLabel();

            userNameExampleLabel.setText(egValue);
            c = new GridBagConstraints();
            c.gridx=0;
            c.gridy=gridy++;
            c.anchor=GridBagConstraints.WEST;
            c.fill=GridBagConstraints.HORIZONTAL;
            c.insets=new Insets(12,12,0,0);
            centerPane.add(userNameExampleLabel, c);

        }

	//PASSWORD
        passwordTextField = new JPasswordField(); //needed below

        // password label
        JLabel passwordLabel = new JLabel();
        passwordLabel.setDisplayedMnemonic('P');
        passwordLabel.setLabelFor(passwordTextField);
        String pLabelStr = PropertiesDepot.getProperty("net.java.sip.communicator.gui.PASSWORD_LABEL");
        passwordLabel.setText(pLabelStr==null?pLabelStr: "Password");
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = gridy;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(11, 12, 0, 0);

        centerPane.add(
            passwordLabel, c);

        // password text
        passwordTextField.setEchoChar('\u2022');
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = gridy++;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.insets = new Insets(11, 7, 0, 11);
        centerPane.add(passwordTextField, c);

        //Set a relevant realm value
        //Bug report by Steven Lass (sltemp at comcast.net)
        //JLabel realmValueLabel = new JLabel("SipPhone.com"); // needed below

/*
        // realm label

        JLabel realmLabel = new JLabel();
        realmLabel.setDisplayedMnemonic('R');
        realmLabel.setLabelFor(realmValueLabel);
        realmLabel.setText("Realm");
        realmValueLabel = new JLabel();

        if (!GuiManager.isThisSipphoneAnywhere) {
            c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = gridy;
            c.anchor = GridBagConstraints.WEST;
            c.insets = new Insets(11, 12, 0, 0);
            centerPane.add(realmLabel, c);
            c = new GridBagConstraints();
            c.gridx = 1;
            c.gridy = gridy++;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.insets = new Insets(11, 7, 0, 11);
            centerPane.add(realmValueLabel, c);
        }
*/


	//NAME
        NameTextField = new JTextField(); // needed below
        // name label
        JLabel NameLabel = new JLabel();
        NameLabel.setDisplayedMnemonic('N');
        // setLabelFor() allows the mnemonic to work
        NameLabel.setLabelFor(NameTextField);
        String NameLabelValue = Utils.getProperty("net.java.sip.communicator.gui.Name_Surname_Email_Address");
        if(NameLabelValue == null)
            NameLabelValue = "Name";

       // int gridy1 = 0;

        NameLabel.setText(NameLabelValue);
        c = new GridBagConstraints();
        c.gridx=0;
        c.gridy=gridy;
        c.anchor=GridBagConstraints.WEST;
        c.insets=new Insets(12,12,0,0);
        centerPane.add(NameLabel, c);

        // Name text
        c = new GridBagConstraints();
        c.gridx=1;
        c.gridy=gridy++;
        c.fill=GridBagConstraints.HORIZONTAL;
        c.weightx=1.0;
        c.insets=new Insets(12,7,0,11);
        centerPane.add(NameTextField, c);

	//SURNAME
        surnameTextField = new JTextField(); // needed below
        // surname label
        JLabel surnameLabel = new JLabel();
        surnameLabel.setDisplayedMnemonic('S');
        // setLabelFor() allows the mnemonic to work
        surnameLabel.setLabelFor(surnameTextField);
        String surnameLabelValue = Utils.getProperty("net.java.sip.communicator.gui.Name_Surname_Email_Address");
        if(surnameLabelValue == null)
            surnameLabelValue = "Surname";

        //int gridy2 = 0;

        surnameLabel.setText(surnameLabelValue);
        c = new GridBagConstraints();
        c.gridx=0;
        c.gridy=gridy;
        c.anchor=GridBagConstraints.WEST;
        c.insets=new Insets(12,12,0,0);
        centerPane.add(surnameLabel, c);

        // surname text
        c= new GridBagConstraints();
        c.gridx=1;
        c.gridy=gridy++;
        c.fill=GridBagConstraints.HORIZONTAL;
        c.weightx=1.0;
        c.insets=new Insets(12,7,0,11);
        centerPane.add(surnameTextField, c);


	//Email
        emailTextField = new JTextField(); // needed below
        // email label
        JLabel emailLabel = new JLabel();
        emailLabel.setDisplayedMnemonic('E');
        // setLabelFor() allows the mnemonic to work
        emailLabel.setLabelFor(emailTextField);
        String emailLabelValue = Utils.getProperty("net.java.sip.communicator.gui.Name_Surname_Email_Address");
        if(emailLabelValue == null)
            emailLabelValue = "E-mail";

        //int gridy3 = 0;

        emailLabel.setText(emailLabelValue);
        c = new GridBagConstraints();
        c.gridx=0;
        c.gridy=gridy;
        c.anchor=GridBagConstraints.WEST;
        c.insets=new Insets(12,12,0,0);
        centerPane.add(emailLabel, c);

        // email text
        c = new GridBagConstraints();
        c.gridx=1;
        c.gridy=gridy++;
        c.fill=GridBagConstraints.HORIZONTAL;
        c.weightx=1.0;
        c.insets=new Insets(12,7,0,11);
        centerPane.add(emailTextField, c);

	//Address
        adrTextField = new JTextField(); // needed below
        // adr label
        JLabel adrLabel = new JLabel();
        adrLabel.setDisplayedMnemonic('A');
        // setLabelFor() allows the mnemonic to work
        adrLabel.setLabelFor(adrTextField);
        String adrLabelValue = Utils.getProperty("net.java.sip.communicator.gui.Name_Surname_Email_Address");
        if(adrLabelValue == null)
            adrLabelValue = "Address";

        //int gridy4 = 0;

        adrLabel.setText(adrLabelValue);
        c = new GridBagConstraints();
        c.gridx=0;
        c.gridy=gridy;
        c.anchor=GridBagConstraints.WEST;
        c.insets=new Insets(12,12,0,0);
        centerPane.add(adrLabel, c);

        // adr text
        c = new GridBagConstraints();
        c.gridx=1;
        c.gridy=gridy++;
        c.fill=GridBagConstraints.HORIZONTAL;
        c.weightx=1.0;
        c.insets=new Insets(12,7,0,11);
        centerPane.add(adrTextField, c);



        // Buttons along bottom of window
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, 0));
        saveButton = new JButton();
        saveButton.setText("Save");
        saveButton.setActionCommand(CMD_SAVE);
        saveButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                dialogDone(event);
            }
        });
        buttonPanel.add(saveButton);

        // space
        buttonPanel.add(Box.createRigidArea(new Dimension(5, 0)));

        cancelButton = new JButton();
        cancelButton.setText("Cancel");
        cancelButton.setActionCommand(CMD_CANCEL);
        cancelButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                dialogDone(event);
            }
        });
        buttonPanel.add(cancelButton);


        buttonPanel.add(Box.createRigidArea(new Dimension(5, 0)));

        helpButton = new JButton();
        helpButton.setMnemonic('H');
        helpButton.setText("Help");
        helpButton.setActionCommand(CMD_HELP);
        helpButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                dialogDone(event);
            }
        });
        //buttonPanel.add(helpButton);
        
        
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 7;//3;
        c.gridwidth = 2;
        c.insets = new Insets(11, 12, 11, 11);

        centerPane.add(buttonPanel, c);

        contents.add(centerPane, BorderLayout.CENTER);
        getRootPane().setDefaultButton(saveButton);
        equalizeButtonSizes();

        setFocusTraversalPolicy(new FocusTraversalPol());
        if (this.flag) break;
    	}
    } // initComponents()

    /**
     * Sets the buttons along the bottom of the dialog to be the
     * same size. This is done dynamically by setting each button's
     * preferred and maximum sizes after the buttons are created.
     * This way, the layout automatically adjusts to the locale-
     * specific strings.
     */
    private void equalizeButtonSizes()
    {

        JButton[] buttons = new JButton[] {
            saveButton, cancelButton
        };

        String[] labels = new String[buttons.length];
        for (int i = 0; i < labels.length; i++) {
            labels[i] = buttons[i].getText();
        }

        // Get the largest width and height
        int i = 0;
        Dimension maxSize = new Dimension(0, 0);
        Rectangle2D textBounds = null;
        Dimension textSize = null;
        FontMetrics metrics = buttons[0].getFontMetrics(buttons[0].getFont());
        Graphics g = getGraphics();
        for (i = 0; i < labels.length; ++i) {
            textBounds = metrics.getStringBounds(labels[i], g);
            maxSize.width =
                Math.max(maxSize.width, (int) textBounds.getWidth());
            maxSize.height =
                Math.max(maxSize.height, (int) textBounds.getHeight());
        }

        Insets insets =
            buttons[0].getBorder().getBorderInsets(buttons[0]);
        maxSize.width += insets.left + insets.right;
        maxSize.height += insets.top + insets.bottom;

        // reset preferred and maximum size since BoxLayout takes both
        // into account
        for (i = 0; i < buttons.length; ++i) {
            buttons[i].setPreferredSize( (Dimension) maxSize.clone());
            buttons[i].setMaximumSize( (Dimension) maxSize.clone());
        }
    } // equalizeButtonSizes()

    /**
     * The user has selected an option. Here we close and dispose the dialog.
     * If actionCommand is an ActionEvent, getCommandString() is called,
     * otherwise toString() is used to get the action command.
     *
     * @param actionCommand may be null
     */
    private void dialogDone(Object actionCommand)
    {
        String cmd = null;
        if (actionCommand != null) {
            if (actionCommand instanceof ActionEvent) {
                cmd = ( (ActionEvent) actionCommand).getActionCommand();
            }
            else {
                cmd = actionCommand.toString();
            }
        }
        if (cmd == null) {
            // do nothing
        }
        else if (cmd.equals(CMD_CANCEL)) {
            userName = null;
            password = null;
        }
        else if (cmd.equals(CMD_HELP)) {
            System.out.println("your help code here...");
        }
        else if (cmd.equals(CMD_SAVE)) {
            userName = userNameTextField.getText();
            password = passwordTextField.getPassword();
	        name = NameTextField.getText();
	        surname = surnameTextField.getText();
	        email = emailTextField.getText();
	        address = adrTextField.getText();
	        
	        //This didn't work!
	        if ( (userName.equals(null)) || (name.equals(null)) || (surname.equals(null)) || (email.equals(null)) || (address.equals(null)) ) {
	        	//MESSAGE please fill in all the information for your billing account.
                JOptionPane.showMessageDialog(null, "Please fill in all the information for your billing account.", "Attention!", JOptionPane.INFORMATION_MESSAGE);
	        	this.flag=false;
	        	return;
	        }
	       // try {

//!!!! HAS TO BE REMOVED FROM COMMUNICATOR
           /* 	NewUser nu = new NewUser(userName);
            	if (nu.existingUser()) {
            		//POP UP MESSAGE This username already exists, please try another one.;
                    JOptionPane.showMessageDialog(null, "This username already exists, please try another one.", "Attention!", JOptionPane.INFORMATION_MESSAGE);
            		this.flag=false;
            		return;
	        	}
            	if (this.flag) { //Info were well written
            		nu.saveNew(name, surname, email, address);
              	};
            }
	        catch (Exception e) {
	        	e.printStackTrace();
	        }*/
        }

        setVisible(false);
        dispose();
    } // dialogDone()

    /**
     * This main() is provided for debugging purposes, to display a
     * sample dialog.
     */
    public static void main(String args[])
    {
        JFrame frame = new JFrame()
        {
            public Dimension getPreferredSize()
            {
                return new Dimension(200, 600);
            }
        };
        frame.setTitle("Debugging frame");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(false);

        AuthenticationSplash2 dialog = new AuthenticationSplash2(frame, true);
        dialog.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent event)
            {
                System.exit(0);
            }

            public void windowClosed(WindowEvent event)
            {
                System.exit(0);
            }
        });
        dialog.pack();
        dialog.setVisible(true);
    } // main()

    private class FocusTraversalPol extends LayoutFocusTraversalPolicy
    {
        public Component getDefaultComponent(Container cont)
        {
            if(  userNameTextField.getText() == null
               ||userNameTextField.getText().trim().length() == 0)
                return super.getFirstComponent(cont);
            else
                return passwordTextField;
        }
    }
} // class LoginSplash
