package ten24.twitt24;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import winterwell.jtwitter.Twitter.Status;

public class TweetPanel extends JPanel
{

  private final boolean DEBUG = false;

  public TweetPanel(Status status, int width, int height,
      ActionListener buttonListener, HyperlinkListener linkListener)
  {
    setPreferredSize(new Dimension(width, height));
    setBackground(Color.WHITE);
    setLayout(new BorderLayout());
    JPanel textPanel = new JPanel();
    JPanel detailPanel = new JPanel();
    JPanel buttonPanel = new JPanel();
    JPanel iconPanel = new JPanel();
    JButton reply = new JButton("@");
    JButton direct = new JButton("D");
    JButton retweet = new JButton("R");
    reply.setBorder(null);
    reply.setFocusable(false);
    reply.setBackground(Color.WHITE);
    reply.setActionCommand("@" + status.user.screenName);
    reply.addActionListener(buttonListener);
    direct.setBorder(null);
    direct.setFocusable(false);
    direct.setBackground(Color.WHITE);
    direct.setActionCommand("D " + status.user.screenName);
    direct.addActionListener(buttonListener);
    retweet.setBorder(null);
    retweet.setFocusable(false);
    retweet.setBackground(Color.WHITE);
    retweet.setActionCommand("R:" + status.id);
    retweet.addActionListener(buttonListener);
    textPanel.setBackground(Color.WHITE);
    detailPanel.setBackground(Color.WHITE);
    iconPanel.setBackground(Color.WHITE);
    detailPanel.setLayout(new BorderLayout());
    buttonPanel.setLayout(new GridLayout(1,0));
    textPanel.setLayout(new BorderLayout());
    AAJTextPane text = new AAJTextPane(new HTMLDocument());
    text.setEditorKit(new HTMLEditorKit());
    text.setEditable(false);
    text.setContentType("text/html");
    text.setFocusable(false);
    String[] word = status.text.split(" ");
    String sentance = "";
    for (int i = 0; i < word.length; i++)
    {
      if (word[i].startsWith("http://"))
      {
        word[i] = "<a href=\"" + word[i] + "\">" + word[i] + "</a>";
      }
      else if (word[i].startsWith("@"))
      {
        // TODO Find the correct tweet to link to
        word[i] = "<a href=\"http://twitter.com/" + word[i].substring(1)
            + "\">" + word[i] + "</a>";
      }
      else if (word[i].startsWith("#"))
      {
        word[i] = "<a href=\"http://www.roomatic.com/%23" + word[i].substring(1)
            + "\">" + word[i] + "</a>";
      }
      sentance = sentance + " " + word[i];
    }
    text.setText("<style>" + "body{font-size:12pt;}"
        + "a{text-decoration:none;color:black;background-color:#EEEEEE;}"
        + "</style><body>" + sentance + "</body>");
    
    text.addHyperlinkListener(linkListener);
    JButton date = new JButton(Twitt24.toRelative(status.createdAt));
    JButton name = new JButton(status.user.name + " (" + status.user.screenName
        + ")");
    date.setActionCommand("S:" + status.user.screenName + ":" + status.id);
    date.addActionListener(buttonListener);
    date.setBorder(null);
    date.setFocusable(false);
    date.setBackground(Color.WHITE);
    date.setHorizontalAlignment(SwingConstants.LEFT);
    name.setActionCommand("T:" + status.user.screenName);
    name.addActionListener(buttonListener);
    name.setBorder(null);
    name.setFocusable(false);
    name.setBackground(Color.WHITE);
    name.setHorizontalAlignment(SwingConstants.LEFT);
    JLabel icon = null;
    // ImageIcon imgIcon = null;
    String iconCache = System.getProperty("user.home") + "/.twitt24/iconCache";
    try
    {
      // if (DEBUG)
      // {
      // imgIcon = new ImageIcon("unknown.png");
      // icon = new JLabel(imgIcon);
      // }
      // else
      // {
      String urlFile = status.user.profileImageUrl.toURL().getFile();
      urlFile = "/"+urlFile.split("/", 4)[3].replace('/', '_');
      if (new File(iconCache + urlFile).exists())
      {
        icon = new JLabel(new ImageIcon(iconCache+urlFile));
        if (DEBUG) System.out.println("Cache " + urlFile);
      }
      else
      {
        File toCache = new File(iconCache + urlFile);
        // imgIcon = new ImageIcon(status.user.profileImageUrl.toURL());
        Image img = ImageIO.read(status.user.profileImageUrl.toURL());
        ImageIO.write((RenderedImage) img, "PNG", toCache);
        icon = new JLabel(new ImageIcon(iconCache + urlFile));
        if (DEBUG) System.out.println("Net " + urlFile);
      }
      // }
    }
    catch (MalformedURLException e)
    {
      e.printStackTrace();
    }
    catch (IOException e)
    {
      icon = new JLabel(new ImageIcon("unknown.png", null));
    }
    detailPanel.add(name, BorderLayout.WEST);
    buttonPanel.add(reply);
    buttonPanel.add(retweet);
    buttonPanel.add(direct);
    detailPanel.add(buttonPanel, BorderLayout.EAST);
    textPanel.add(text, BorderLayout.CENTER);
    textPanel.add(detailPanel, BorderLayout.NORTH);
    iconPanel.add(icon);
    add(textPanel, BorderLayout.CENTER);
    add(iconPanel, BorderLayout.EAST);
    add(date, BorderLayout.SOUTH);
  }
}