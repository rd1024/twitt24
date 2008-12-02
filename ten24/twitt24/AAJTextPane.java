package ten24.twitt24;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JTextPane;
import javax.swing.text.StyledDocument;

public class AAJTextPane extends JTextPane
{

  public AAJTextPane()
  {
  }

  public AAJTextPane(StyledDocument doc)
  {
    super(doc);
  }

  @Override
  protected void paintComponent(Graphics g)
  {
    Graphics2D g2d = (Graphics2D) g;
    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
        RenderingHints.VALUE_RENDER_QUALITY);
    // if wanted to smooth geometric shapes too
     g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
     RenderingHints.VALUE_ANTIALIAS_ON );
    super.paintComponent(g2d);
  }
}
