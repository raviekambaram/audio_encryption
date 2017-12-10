

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import java.io.*;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;

public class ImageHiding extends JFrame implements ActionListener
{
 BufferedImage hostImage;
 BufferedImage secretImage;

 JPanel controlPanel;
 JPanel imagePanel;

 JTextField encodeBitsText;
 JButton encodeBitsPlus;
 JButton encodeBitsMinus;

 JTextField nBitsText;
 JButton nBitsPlus;
 JButton nBitsMinus;

 ImageCanvas hostCanvas;
 ImageCanvas secretCanvas;
 JTextField field=new JTextField("0",5);

 Steganography s;
 String content="";
 public BufferedImage getHostImage()
 {
  BufferedImage img = null;

  try
  {
   img = ImageIO.read(new File("host_image1.jpg"));
  }
  catch (IOException ioe) { ioe.printStackTrace(); }

  return img;
 }

 public int getBits()
 {
	 System.out.println("bits: "+encodeBitsText.getText());
  return Integer.parseInt(encodeBitsText.getText());
 }

 public void actionPerformed(ActionEvent event)
 {
  Object source = event.getSource();


  if (source == encodeBitsPlus)
  {
   int bits =  this.getBits() + 1; // this.getBits()

   if (bits > 8) { bits = 8; }

   encodeBitsText.setText(Integer.toString(bits));

   s = new Steganography(this.getHostImage());
   s.encode(content, bits); // this.getSecretImage()

   hostCanvas.setImage(s.getImage());
   hostCanvas.repaint();
   field.setText(String.valueOf(s.change()));
  }
 
  else if (source == encodeBitsMinus)
  {
   int bits =  this.getBits() - 1; // this.getBits()

   if (bits < 0) { bits = 0; }

   encodeBitsText.setText(Integer.toString(bits));

   s = new Steganography(this.getHostImage());
   s.encode(content, bits);

   hostCanvas.setImage(s.getImage());
   hostCanvas.repaint();
   field.setText(String.valueOf(s.change()));
  
  }
 }

 public ImageHiding()
 {
  GridBagLayout layout = new GridBagLayout();
  GridBagConstraints gbc = new GridBagConstraints();
  this.setTitle("wav Hiding Demo");

  Container container = this.getContentPane();
     
     
  Player player = new Player("president_speech.wav");
  InputStream stream = new ByteArrayInputStream(player.getSamples());
  content = player.play(stream);

  this.setLayout(layout);

  this.add(new JLabel("Bits to encode into host image:"));

  encodeBitsText = new JTextField("0", 5);
  encodeBitsText.setEditable(false);

  gbc.weightx = -1.0;
  layout.setConstraints(encodeBitsText, gbc);
  this.add(encodeBitsText);
  this.add(field);
  
  encodeBitsPlus = new JButton("+");
  encodeBitsPlus.addActionListener(this);
  
  
  
  encodeBitsMinus = new JButton("-");
  encodeBitsMinus.addActionListener(this);

  gbc.weightx = 1.0;
  layout.setConstraints(encodeBitsPlus, gbc);
  this.add(encodeBitsPlus);
  
  
  
  gbc.gridwidth = GridBagConstraints.REMAINDER;
  layout.setConstraints(encodeBitsMinus, gbc);
  this.add(encodeBitsMinus);

  GridBagLayout imageGridbag = new GridBagLayout();
  GridBagConstraints imageGBC = new GridBagConstraints();

  imagePanel = new JPanel();
  imagePanel.setLayout(imageGridbag);

  JLabel hostImageLabel = new JLabel("Host image:");
  JLabel secretImageLabel = new JLabel(""); // label for secret image

  imagePanel.add(hostImageLabel);

  imageGBC.gridwidth = GridBagConstraints.REMAINDER;
  imageGridbag.setConstraints(secretImageLabel, imageGBC);
  imagePanel.add(secretImageLabel);

  hostCanvas = new ImageCanvas(this.getHostImage());  

  imagePanel.add(hostCanvas);

  gbc.gridwidth = GridBagConstraints.REMAINDER;
  layout.setConstraints(imagePanel, gbc);
  this.add(imagePanel);

  Steganography host = new Steganography(this.getHostImage());
  host.encode(content, this.getBits()); 
  hostCanvas.setImage(host.getImage());

  this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  this.pack();

  this.setVisible(true);
 }

 public static void main(String[] args)
 {
  ImageHiding frame = new ImageHiding();
  frame.setVisible(true);
 }

 public class ImageCanvas extends JPanel
 { 
  Image img;

  public void paintComponent(Graphics g)
  {
   g.drawImage(img, 0, 0, this);
  }

  public void setImage(Image img)
  {
   this.img = img;
  }

  public ImageCanvas(Image img)
  {
   this.img = img;
   this.setPreferredSize(new Dimension(img.getWidth(this), img.getHeight(this)));
  }
 }
}

class Steganography
{
 BufferedImage image;
 int newsize=0;
 public void getMaskedImage(int bits)
 {
  int[] imageRGB = image.getRGB(0, 0, image.getWidth(null), image.getHeight(null), null, 0, image.getWidth(null));

  int maskBits = (int)(Math.pow(2, bits)) - 1 << (8 - bits);
  int mask = (maskBits << 24) | (maskBits << 16) | (maskBits << 8) | maskBits;

  for (int i = 0; i < imageRGB.length; i++)
  {
   imageRGB[i] = imageRGB[i] & mask;
  }

  image.setRGB(0, 0, image.getWidth(null), image.getHeight(null), imageRGB, 0, image.getWidth(null));
 }
 public void encode(String dataencode, int encodeBits)
 {
  String[] sarr = dataencode.split("");
  int[] imageRGB = image.getRGB(0, 0, image.getWidth(null), image.getHeight(null), null, 0, image.getWidth(null));
  int[] encodeRGB = new int[imageRGB.length];
  for(int i=0; i< encodeRGB.length; i++)
  {
      if(i>dataencode.length())
      break;
      encodeRGB[i] = Integer.parseInt(sarr[i]);
  }
     
  int encodeByteMask = (int)(Math.pow(2, encodeBits)) - 1 << (8 - encodeBits);
  int encodeMask = (encodeByteMask << 24) | (encodeByteMask << 16) | (encodeByteMask << 8) | encodeByteMask;

  int decodeByteMask = ~(encodeByteMask >>> (8 - encodeBits)) & 0xFF;
  int hostMask = (decodeByteMask << 24) | (decodeByteMask << 16) | (decodeByteMask << 8) | decodeByteMask;

  for (int i = 0; i < imageRGB.length; i++)
  {
   int encodeData = (encodeRGB[i] & encodeMask) >>> (8 - encodeBits);
   imageRGB[i] = (imageRGB[i] & hostMask) | (encodeData & ~hostMask);
  }

  image.setRGB(0, 0, image.getWidth(null), image.getHeight(null), imageRGB, 0, image.getWidth(null));
  try
  {
  ByteArrayOutputStream tmp=new ByteArrayOutputStream();
  ImageIO.write(image, "png", tmp);
  newsize=tmp.size();  
  }
  catch(IOException ioe)
  {
  ioe.printStackTrace();
  }
 }

 public Image getImage()
 {
  return image;
 }

 public Steganography(BufferedImage image)
 {
  this.image = image;
 }
 public int change()
 {
	 return newsize;
 }
}

class Player
{
    private AudioFormat format;
    private byte[] samples;
    static int totalbytes = 0;
    public Player(String filename)
    {
        try
        {
            AudioInputStream stream = AudioSystem.getAudioInputStream(new File(filename));
            format = stream.getFormat();
            samples = getSamples(stream);
        }
        catch (UnsupportedAudioFileException e){e.printStackTrace();}
        catch (IOException e){e.printStackTrace();}
    }
    
    public byte[] getSamples()
    {
        return samples;
    }
    
    public byte[] getSamples(AudioInputStream stream)
    {
        //int length = (int)(stream.getFrameLength() * format.getFrameSize());
       // System.out.println(length);
        byte[] samples = new byte[569856];
        DataInputStream in = new DataInputStream(stream);
        try
        {
            in.readFully(samples);
        }
        catch (IOException e){e.printStackTrace();}
        return samples;
    }
    
    static String getbits(byte b)
    {
        String result = "";
        for(int i = 0; i < 8; i++)
            result += (b & (1 << i)) == 0 ? "0" : "1";
        return result;
    }
    
    public String play(InputStream source)
    {
        int bufferSize = format.getFrameSize() * Math.round(format.getSampleRate());// / 10);
        byte[] buffer = new byte[bufferSize];
        String content ="";
        try
        {
            int numBytesRead = 0;
            while (numBytesRead != -1)
            {
                numBytesRead = source.read(buffer, 0, buffer.length);
                for(byte b : buffer)
                {
                    content += getbits(b);
                }
                totalbytes+=numBytesRead;
                numBytesRead = -1;
                
            }
        }
        catch (IOException e){e.printStackTrace();}
        System.out.println("Bytes Read"+ totalbytes);
        return content;
    }
    
    
}