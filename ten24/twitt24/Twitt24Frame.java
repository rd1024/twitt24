package ten24.twitt24;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.Border;

import com.sun.java.swing.plaf.motif.MotifBorders.BevelBorder;

public class Twitt24Frame extends JFrame
{
  Twitt24Panel twitt24Panel;
  Twitt24 twitt24;

  public Twitt24Frame(Twitt24 twitt24, String username, String password)
  {
    this.twitt24 = twitt24;
    setTitle(Twitt24.NAME + "(" + Twitt24.VERSION + ")");
    setBackground(Color.WHITE);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    twitt24Panel = new Twitt24Panel(twitt24, username, password);
    ClassLoader loader = getClass().getClassLoader();
    URL trayImageLoc = loader.getResource("logo1.png");
    URL borderLoc = loader.getResource("border.png");
    Image trayImage = Toolkit.getDefaultToolkit().getImage(trayImageLoc);
    ImageIcon borderIcon = new ImageIcon(borderLoc);
    setIconImage(trayImage);
    setLayout(new BorderLayout());
    // JLabel titleLabel = new JLabel("Twit24");
    // JLabel westLabel = new JLabel("");
    // JLabel southLabel = new JLabel("");
    // JLabel eastLabel = new JLabel("");
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    setSize(400, screenSize.height - 50);
    // westLabel.setBackground(Color.CYAN);
    // eastLabel.setBackground(Color.CYAN);
    // southLabel.setBackground(Color.CYAN);
    // westLabel.setForeground(Color.CYAN);
    // eastLabel.setForeground(Color.CYAN);
    // southLabel.setForeground(Color.CYAN);
    // westLabel.setPreferredSize(new Dimension(10,getHeight()));
    // eastLabel.setPreferredSize(new Dimension(10,getHeight()));
    // southLabel.setPreferredSize(new Dimension(getWidth(),10));
    // add(titleLabel,BorderLayout.NORTH);
    // add(southLabel,BorderLayout.SOUTH);
    // add(westLabel,BorderLayout.WEST);
    // add(eastLabel,BorderLayout.EAST);
    add(twitt24Panel, BorderLayout.CENTER);
    // twitt24Panel.setBorder(BorderFactory.createMatteBorder(5,5,5,5,Color.CYAN));
    setLocation(screenSize.width - 400, 0);
    setVisible(true);
  }

  @Override
  public void dispose()
  {
//    super.dispose();
    twitt24.onFrameDispose();
  }
}