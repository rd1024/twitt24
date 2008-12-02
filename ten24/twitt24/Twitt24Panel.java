package ten24.twitt24;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.BadLocationException;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;
import winterwell.jtwitter.Twitter.Status;

public class Twitt24Panel extends JPanel implements CaretListener
{
  Twitt24 twitt24;
  JScrollPane tweetScPanel;
  JPanel tweetPanel;
  ArrayList<Status> archiveTimeline;
  ArrayList<Status> friendTimeline;
  Twitter twitter;
  JTextArea input;
  HyperlinkListener linkListener;
  Date lastUpdate;
  String homeDir;
  File dataDir;
  File iconCache;
  File tweetFile;
  String username;
  String password;
  String currentView;
  JButton tweetButton;

  public Twitt24Panel(Twitt24 twitt24, String username, String password)
  {
    this.twitt24 = twitt24;
    this.username = username;
    this.password = password;
    homeDir = System.getProperty("user.home");
    dataDir = new File(homeDir + "/.twitt24/");
    iconCache = new File(dataDir.getAbsoluteFile() + "/iconCache/");
    tweetFile = new File(dataDir, username + ".twitt24");

    if (!dataDir.exists())
    {
      dataDir.mkdir();
    }
    if (!iconCache.exists())
    {
      iconCache.mkdir();
    }

    JButton friendButton = new JButton("Friend");
    JButton publicButton = new JButton("Archive");
    tweetButton = new JButton("Tweet");
    JButton pokeButton = new JButton("Poke");
    input = new JTextArea();
    input.setLineWrap(true);
    input.setWrapStyleWord(true);
    input.addCaretListener(this);
    setLayout(new BorderLayout());
    JPanel buttonPanel = new JPanel();
    JPanel southPanel = new JPanel();
    tweetPanel = new JPanel();
    tweetPanel.setLayout(new GridLayout(0, 1, 0, 5));
    // System.out.println(tweetPanel.getBackground());
    setBackground(Color.WHITE);
    tweetScPanel = new JScrollPane();
    tweetScPanel.setBackground(Color.WHITE);
    // tweetScPanel.setPreferredSize(new Dimension(getWidth() - 10,
    // getHeight() - 150));
    // tweetScPanel.setBorder(BorderFactory.createTitledBorder("Friends"));
    tweetScPanel.getVerticalScrollBar().setUnitIncrement(35);
    // button.setBackgroundPainter(getButtonPainter());
    // AbstractPainter<JXButton> fgPainter = (AbstractPainter<JXButton>) button
    // .getForegroundPainter();
    // ColorTintFilter filter = new ColorTintFilter(Color.RED, 0.5f);
    // fgPainter.setFilters(filter);
    ButtonListener buttonListener = new ButtonListener(this);
    linkListener = new LinkListener();
    friendButton.addActionListener(buttonListener);
    publicButton.addActionListener(buttonListener);
    tweetButton.addActionListener(buttonListener);
    pokeButton.addActionListener(buttonListener);
    // button.setForegroundPainter(fgPainter);
    // button.setPreferredSize(new Dimension(200, 50));
    // button.setBorderPainted(false);
    // button.setDoubleBuffered(true);
    // button.setFont(new Font("Sans", Font.BOLD, 24));
    // showPublicTimeline(tweetPanel);
    buttonPanel.setLayout(new GridLayout(1, 0));
    southPanel.setLayout(new BorderLayout());
    add(tweetScPanel, BorderLayout.CENTER);
    buttonPanel.add(friendButton);
    buttonPanel.add(publicButton);
    buttonPanel.add(pokeButton);
    southPanel.add(buttonPanel, BorderLayout.NORTH);
    southPanel.add(input, BorderLayout.CENTER);
    southPanel.add(tweetButton, BorderLayout.EAST);
    add(southPanel, BorderLayout.SOUTH);
    // setDefaultCloseOperation(EXIT_ON_CLOSE);
    // setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    tweetScPanel.setViewportView(tweetPanel);
    // tweetScPanel.getViewport().setViewPosition(new Point(0,0));
    // tweetScPanel.getVerticalScrollBar().setValue(0);
    twitter = new Twitter(username, password);
    loadTweetsFromFile();
    // ArrayList<Status> withFriends = (ArrayList<Status>)
    // twitter.getFriendsTimeline();
    // ArrayList<Status> activeList = publicTimeline;
    showTweets(tweetPanel, friendTimeline);
    try
    {
      Thread.sleep(500);
    }
    catch (InterruptedException e)
    {
      e.printStackTrace();
    }
    tweetScPanel.getVerticalScrollBar().setValue(0);
  }

  void doUpdate()
  {
    updateFriendFeed();
    // tweetScPanel.setBorder(BorderFactory.createTitledBorder("Friends"));
    showTweets(tweetPanel, friendTimeline);
  }

  private void saveTweetsToFile()
  {
    try
    {
      FileOutputStream fos = new FileOutputStream(tweetFile);
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      oos.writeObject(friendTimeline);
      oos.writeObject(archiveTimeline);
      oos.writeObject(lastUpdate);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  private void loadTweetsFromFile()
  {
    try
    {
      if (!tweetFile.exists())
      {
        friendTimeline = new ArrayList<Status>();
        archiveTimeline = new ArrayList<Status>();
        return;
      }
      FileInputStream fis = new FileInputStream(tweetFile);
      ObjectInputStream ois = new ObjectInputStream(fis);
      friendTimeline = (ArrayList<Status>) ois.readObject();
      archiveTimeline = (ArrayList<Status>) ois.readObject();
      lastUpdate = (Date) ois.readObject();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    catch (ClassNotFoundException e)
    {
      e.printStackTrace();
    }
    // System.out.println("ff="+friendTimeline.size());
    // System.out.println("af="+((archiveTimeline!=null)?archiveTimeline.size():"null"));
    // System.out.println("lu="+lastUpdate);
  }

  private void showTweets(JPanel tweetPanel, ArrayList<Status> statusFeed)
  {
    tweetPanel.removeAll();
    for (Status status : statusFeed)
    {
      tweetPanel.add(new TweetPanel(status, 20, 100, new ButtonListener(this),
          linkListener));
    }
    tweetScPanel.setViewportView(tweetPanel);
    // tweetScPanel.getVerticalScrollBar().setValue(0);
    // tweetScPanel.scrollRectToVisible(new Rectangle(0,0,10,10));
  }

  // private Painter<JXButton> getButtonPainter()
  // {
  // MattePainter mp = new MattePainter(new Color(0f, 0f, 0.9f, 0.5f));
  // GlossPainter gp = new GlossPainter(new Color(1f, 1f, 1f, 0.3f),
  // GlossPainter.GlossPosition.TOP);
  // PinstripePainter pp = new PinstripePainter(
  // new Color(0.5f, 0.5f, 0.5f, 0.2f), 45d);
  // return (new CompoundPainter<JXButton>(mp, pp, gp));
  // }

  private ArrayList<Status> updateFeed(ArrayList<Status> list,
      ArrayList<Status> newList)
  {
    if (list == null)
    {
      list = new ArrayList<Status>();
    }
    for (Status s : newList)
    {
      if (!statusContained(list, s))
      {
        list.add(0, s);
        System.out.println("* " + s.user.screenName + ": " + s);
        if (SystemTray.isSupported())
        {
          twitt24.trayIcon.displayMessage("New Tweet", s.user.screenName + ": "
              + s, TrayIcon.MessageType.INFO);
        }
      }
    }
    return list;
  }

  private boolean statusContained(ArrayList<Status> list, Status status)
  {
    for (Status s : list)
    {
      if (s.id == status.id) return true;
    }
    return false;
  }

  void updateFriendFeed()
  {
    if (friendTimeline == null)
    {
      friendTimeline = (ArrayList<Status>) twitter.getFriendsTimeline();
    }
    else
    {
      friendTimeline = updateFeed(friendTimeline, (ArrayList<Status>) twitter
          .getFriendsTimeline("", lastUpdate));
    }
    int[] health = twitter.getRateLimitStatus();
    sortFeed(friendTimeline);
    archiveTweets();
    sortFeed(archiveTimeline);
    lastUpdate = friendTimeline.get(0).createdAt;
    saveTweetsToFile();
    twitt24.setHealth(health);
  }

  private <T extends Comparable<? super T>> void sortFeed(List<T> list)
  {
    Collections.sort(list);
  }

  private void archiveTweets()
  {
    if (archiveTimeline == null)
    {
      archiveTimeline = new ArrayList<Status>();
    }
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DATE, -1);
    Date date = cal.getTime();
    ArrayList<Status> newList = new ArrayList<Status>();
    for (Status s : friendTimeline)
    {
      if (s.createdAt.before(date))
      {
        archiveTimeline.add(s);
      }
      else
      {
        newList.add(s);
      }
    }
    // System.out.println("ff="+friendTimeline.size());
    // System.out.println("nl="+newList.size());
    // System.out.println("af="+archiveTimeline.size());
    // System.out.println("total="+(archiveTimeline.size()+newList.size()));
    friendTimeline = newList;
  }

  private void openURL(URL url)
  {
    try
    {
      String osName = System.getProperty("os.name");
      if (osName.startsWith("Windows"))
      {
        Runtime.getRuntime()
            .exec("rundll32 url.dll,FileProtocolHandler " + url);
      }
      else
      {
        Runtime.getRuntime().exec("firefox " + url);
      }
    }
    catch (IOException e1)
    {
      e1.printStackTrace();
    }
  }

  private class LinkListener implements HyperlinkListener
  {
    public void hyperlinkUpdate(HyperlinkEvent e)
    {
      if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
      {
        openURL(e.getURL());
      }
    }
  }

  private class ButtonListener implements ActionListener
  {
    Twitt24Panel parent;

    public ButtonListener(Twitt24Panel parent)
    {
      this.parent = parent;
    }

    public void actionPerformed(ActionEvent e)
    {
      if (e.getActionCommand().startsWith("@"))
      {
        input.setText(e.getActionCommand() + " ");
      }
      else if (e.getActionCommand().startsWith("D "))
      {
        input.setText(e.getActionCommand() + " ");
      }
      else if (e.getActionCommand().startsWith("R:"))
      {
        int id = Integer.parseInt(e.getActionCommand().substring(2));
        String user = null;
        String text = null;
        for (Status s : friendTimeline)
        {
          if (s.id == id)
          {
            user = s.user.screenName;
            text = s.text;
          }
        }
        input.setText("Retweet @" + user + ": " + text);
      }
      else if (e.getActionCommand().startsWith("T:"))
      {
        String user = e.getActionCommand().split(":")[1];
        try
        {
          openURL(new URL("http://twitter.com/" + user));
        }
        catch (MalformedURLException e1)
        {
          e1.printStackTrace();
        }
        catch (TwitterException e1)
        {
          e1.printStackTrace();
        }
      }
      else if (e.getActionCommand().startsWith("S:"))
      {
        String user = e.getActionCommand().split(":")[1];
        String id = e.getActionCommand().split(":")[2];
        try
        {
          openURL(new URL("http://twitter.com/" + user + "/statuses/" + id));
        }
        catch (MalformedURLException e1)
        {
          e1.printStackTrace();
        }
        catch (TwitterException e1)
        {
          e1.printStackTrace();
        }
      }
      else if (e.getActionCommand().equals("Friend"))
      {
        updateFriendFeed();
        // tweetScPanel.setBorder(BorderFactory.createTitledBorder("Friends"));
        parent.showTweets(tweetPanel, friendTimeline);
      }
      else if (e.getActionCommand().equals("Archive"))
      {
        // tweetScPanel.setBorder(BorderFactory.createTitledBorder("Archive"));
        parent.showTweets(tweetPanel, archiveTimeline);
      }
      else if (e.getActionCommand().contains("/140"))
      {
        if (parent.twitter.setStatus(parent.input.getText()).getText().equals(
            parent.input.getText()))
        {
          parent.input.setText("");
        }
        else
        {
          JXErrorPane
              .showDialog(
                  parent,
                  new ErrorInfo(
                      "Error",
                      "Could not post tweet",
                      "For some reason, your tweet could not be posted."
                          + "<br>This could be a connection problem, or Twitter may be down."
                          + "<br>Please check your internet connection and the current status of twitter and try again."
                          + "<br>If the problem still persists, contact the developer.",
                      null, null, null, null));
        }
      }
      else if (e.getActionCommand().equals("Poke"))
      {
        tweetScPanel.getVerticalScrollBar().setValue(0);
      }
    }
  }

  public void caretUpdate(CaretEvent e)
  {
    tweetButton.setEnabled(input.getText().length()<=140);
    tweetButton.setText(input.getText().length() + "/140");
  }
}