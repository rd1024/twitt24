package ten24.twitt24;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Twitt24
{
  public static final String NAME = "Twitt24";
  public static final String VERSION = "Private Beta 2";
  public static final String LICENSE = "Not for distribution";
  String[] auth;
  File authFile;
  Twitt24Frame twitt24Frame;
  Twitt24Window twitt24Window;
  TrayIcon trayIcon;

  public Twitt24()
  {
    String homeDir = System.getProperty("user.home");
    File dataDir = new File(homeDir + "/.twitt24/");
    authFile = new File(dataDir, ".auth");
    if (authFile.exists()) login();
    else
    {
      if (!dataDir.exists()) dataDir.mkdir();
      new LoginDialog(this);
    }
  }

  private void login()
  {
    auth = readAuthFile(authFile);
    ClassLoader loader = getClass().getClassLoader();
    URL trayImageLoc = loader.getResource("logo1.png");
    Image trayImage = Toolkit.getDefaultToolkit().getImage(trayImageLoc);
    if (SystemTray.isSupported())
    {
      trayIcon = new TrayIcon(trayImage, "Twitt24");
      trayIcon.setImageAutoSize(true);
      trayIcon.setActionCommand("Tray");

      PopupMenu trayMenu = new PopupMenu();
      MenuItem menuExit = new MenuItem("Exit");
      ActionListener menuListener = new MenuListener();
      MouseListener mouseListener = new TrayMouseListener();
      trayMenu.add(menuExit);
      trayMenu.add("Last Update: Unknown");
      trayMenu.add("Last Tweet: Unknown");
      trayMenu.add("Health: Unknown");
      trayIcon.setPopupMenu(trayMenu);
      menuExit.addActionListener(menuListener);
      trayIcon.addMouseListener(mouseListener);
      try
      {
        SystemTray.getSystemTray().add(trayIcon);
      }
      catch (AWTException e)
      {
        e.printStackTrace();
      }
      // twitt24Window = new Twitt24Window(this, auth[0], auth[1]);
    }
    // else
    // {
    twitt24Frame = new Twitt24Frame(this, auth[0], auth[1]);
    // }
    // four lines above were for the Window imp.
    Twitt24Scheduler scheduler = new Twitt24Scheduler(this);
    scheduler.start();
  }

  private class TrayMouseListener implements MouseListener
  {
    public void mouseClicked(MouseEvent e)
    {
      if (e.getButton() == MouseEvent.BUTTON1)
      {
        if (twitt24Window != null) twitt24Window.setVisible(!twitt24Window
            .isVisible());
        if (twitt24Frame != null) twitt24Frame.setVisible(!twitt24Frame
            .isVisible());
      }
    }

    public void mouseEntered(MouseEvent e)
    {
    }

    public void mouseExited(MouseEvent e)
    {
    }

    public void mousePressed(MouseEvent e)
    {
    }

    public void mouseReleased(MouseEvent e)
    {
    }
  }

  private class MenuListener implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
      if (e.getActionCommand().equals("Exit"))
      {
        System.exit(0);
      }
    }
  };

  void doMagicUpdate()
  {
    if (twitt24Frame != null) twitt24Frame.twitt24Panel.doUpdate();
    if (twitt24Window != null) twitt24Window.twitt24Panel.doUpdate();
  }

  private class LoginDialog extends JDialog
  {
    JLabel nameLabel, pwdLabel;
    JTextField nameField;
    JPasswordField pwdField;
    Twitt24 parent;

    public LoginDialog(final Twitt24 parent)
    {
      this.parent = parent;
      nameLabel = new JLabel("Username");
      pwdLabel = new JLabel("Password");
      nameField = new JTextField();
      pwdField = new JPasswordField();
      ClassLoader loader = getClass().getClassLoader();
      URL logoLoc = loader.getResource("logo2.png");
      Image logo = Toolkit.getDefaultToolkit().getImage(logoLoc);
      JLabel logoLabel = new JLabel(new ImageIcon(logo));
      JButton loginButton = new JButton("Login");
      ActionListener listener = new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          parent.setAuth(nameField.getText(),
              new String(pwdField.getPassword()));
          parent.login();
          dispose();
        }
      };
      loginButton.addActionListener(listener);
      setTitle(Twitt24.NAME + " Login");
      JPanel northPanel = new JPanel();
      JPanel centrePanel = new JPanel();
      JPanel southPanel = new JPanel();
      setLayout(new BorderLayout());
      northPanel.add(logoLabel);
      centrePanel.setLayout(new GridLayout(0, 2));
      southPanel.setLayout(new BorderLayout());
      centrePanel.add(nameLabel);
      centrePanel.add(nameField);
      centrePanel.add(pwdLabel);
      centrePanel.add(pwdField);
      southPanel.add(loginButton, BorderLayout.NORTH);
      southPanel.add(new JLabel(Twitt24.VERSION + " - " + Twitt24.LICENSE),
          BorderLayout.CENTER);
      add(northPanel, BorderLayout.NORTH);
      add(centrePanel, BorderLayout.CENTER);
      add(southPanel, BorderLayout.SOUTH);
      pack();
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      setLocation((screenSize.width / 2) - (getWidth() / 2),
          (screenSize.height / 2) - (getHeight() / 2));
      setVisible(true);
    }
  }

  private String[] readAuthFile(File authFile)
  {
    Scanner sc = null;
    try
    {
      sc = new Scanner(authFile);
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
    String user = sc.nextLine();
    String pwd = sc.nextLine();
    return new String[]
    { user, pwd };
  }

  public static void main(String[] args)
  {
    Twitt24 twitt24 = new Twitt24();
    // SwingUtilities.invokeLater(new Runnable()
    // {
    // public void run()
    // {
    // twitt24 = new Twitt24();
    // }
    // });
  }

  void setAuth(String a1, String a2)
  {
    auth = new String[]
    { a1, a2 };
    FileWriter fw;
    try
    {
      fw = new FileWriter(authFile);
      fw.write(a1 + "\n" + a2);
      fw.flush();
      fw.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  public void onFrameDispose()
  {
    if (SystemTray.isSupported()) twitt24Frame.setVisible(false);
    else System.exit(0);
  }

  public void setHealth(int[] health)
  {
    trayIcon.getPopupMenu().remove(trayIcon.getPopupMenu().getItemCount()-1);
    trayIcon.getPopupMenu().remove(trayIcon.getPopupMenu().getItemCount()-1);
    trayIcon.getPopupMenu().remove(trayIcon.getPopupMenu().getItemCount()-1);
    trayIcon.getPopupMenu().add("Last Update: "+DateFormat.getInstance().format(new Date()));
    trayIcon.getPopupMenu().add("Last Tweet: "+DateFormat.getInstance().format(twitt24Frame.twitt24Panel.lastUpdate));
    trayIcon.getPopupMenu().add("Health: "+health[0] + "/" + health[1]);
  }
  
  static String toRelative(Date date)
  {
    Date now = new Date();
    long diff = now.getTime() - date.getTime();
    if (diff < 60 * 1000)
    {
      return "About " + (int) ((diff / (1000)) + 0.5) + " secs ago";
    }
    else if (diff < 60 * 60 * 1000)
    {
      return "About " + (int) ((diff / (60 * 1000)) + 0.5) + " mins ago";
    }
    else if (diff < 48 * 60 * 60 * 1000)
    {
      return "About " + (int) ((diff / (60 * 60 * 1000)) + 0.5) + " hours ago";
    }
    return DateFormat.getInstance().format(date);
  }
}