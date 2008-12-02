package ten24.twitt24;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

public class Twitt24Window extends JWindow implements MouseMotionListener
{
  Twitt24Panel twitt24Panel;
  Twitt24 twitt24;
  Point mousePointer;

  public Twitt24Window(Twitt24 twitt24, String username, String password)
  {
    this.twitt24 = twitt24;
    twitt24Panel = new Twitt24Panel(twitt24, username, password);
    ClassLoader loader = getClass().getClassLoader();
    URL trayImageLoc = loader.getResource("Twitt24.png");
    URL resizeLoc = loader.getResource("resize.png");
    ImageIcon resize = new ImageIcon(resizeLoc);
    Image trayImage = Toolkit.getDefaultToolkit().getImage(trayImageLoc);
    setIconImage(trayImage);
    setLayout(new BorderLayout());
    JLabel titleLabel = new JLabel(Twitt24.NAME + " (" + Twitt24.VERSION + ")");
    // JLabel westLabel = new JLabel("");
    JPanel southPanel = new JPanel();
    southPanel.setSize(getWidth(), 10);
    JLabel southLabel = new JLabel(resize);
    resize.setImage(resize.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
    southPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    // JLabel eastLabel = new JLabel("");
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    System.out.println(screenSize);
    setSize(400, screenSize.height - 100);
    titleLabel.setOpaque(true);
    southLabel.setOpaque(true);
    Color borderColour = new Color(0xaa4400);
    Color fgColour = new Color(0xffd42a);
    titleLabel.setBackground(borderColour);
    titleLabel.setHorizontalAlignment(JLabel.CENTER);
    southLabel.setHorizontalAlignment(JLabel.RIGHT);
    // titleLabel.setBorder(BorderFactory.createLineBorder(Color.RED));
    // westLabel.setBackground(borderColour);
    // eastLabel.setBackground(borderColour);
    southLabel.setBackground(borderColour);
    southPanel.setBackground(borderColour);
    titleLabel.setForeground(fgColour);
    // westLabel.setForeground(borderColour);
    // eastLabel.setForeground(borderColour);
    // southLabel.setForeground(borderColour);
    // westLabel.setPreferredSize(new Dimension(10,getHeight()));
    // eastLabel.setPreferredSize(new Dimension(10,getHeight()));
    // southLabel.setPreferredSize(new Dimension(getWidth(),10));
    add(twitt24Panel, BorderLayout.CENTER);
    add(titleLabel, BorderLayout.NORTH);
    southPanel.add(southLabel);
    add(southPanel, BorderLayout.SOUTH);
    // add(westLabel,BorderLayout.WEST);
    // add(eastLabel,BorderLayout.EAST);
    twitt24Panel.setBorder(BorderFactory.createMatteBorder(0, 5, 0, 5,
        borderColour));
    setLocation(screenSize.width - 420, 20);
    southLabel.setName("South");
    titleLabel.setName("Title");
    titleLabel.addMouseMotionListener(this);
    southLabel.addMouseMotionListener(this);
    setAlwaysOnTop(false);
    setVisible(true);
  }

  public void mouseDragged(MouseEvent e)
  {
    Point aPoint = e.getPoint();
    int x = getX() + aPoint.x - mousePointer.x;
    int y = getY() + aPoint.y - mousePointer.y;
    if (((JComponent) (e.getSource())).getName().equals("South"))
    {
      int newWidth = e.getLocationOnScreen().x - getLocation().x;
      int newHeight = e.getLocationOnScreen().y - getLocation().y;
      setSize(newWidth, newHeight);
    }
    else
    {
      setLocation(x, y);
    }
    Graphics graphics = getGraphics();
    paint(graphics);
  }

  public void mouseMoved(MouseEvent e)
  {
    mousePointer = e.getPoint();
  }
}