package com.j15r.common.server;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ImageStripServlet extends HttpServlet {

  private static HashMap<String, BufferedImage> cache = new HashMap<String, BufferedImage>();

  private static class Fetcher extends Thread {
    public BufferedImage image;
    private final URL url;

    public Fetcher(URL url) {
      this.url = url;
    }

    public void run() {
      try {
        image = ImageIO.read(url);
      } catch (IOException e) {
        // TODO
      }
    }
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse rsp)
      throws ServletException, IOException {
    int imgWidth = Integer.parseInt(req.getParameter("w"));
    int imgHeight = Integer.parseInt(req.getParameter("h"));
    String[] urls = (String[]) req.getParameterMap().get("u");

    BufferedImage[] images = fetch(urls);

    // Render the strip.
    BufferedImage strip = new BufferedImage(imgWidth, imgHeight * urls.length,
        BufferedImage.TYPE_INT_RGB);
    Graphics2D g2d = strip.createGraphics();
    g2d.setBackground(Color.WHITE);
    g2d.clearRect(0, 0, imgWidth, imgHeight * urls.length);

    int i = 0;
    for (BufferedImage image : images) {
      if (image != null) {
        int x = (imgWidth - image.getWidth()) / 2;
        int y = (imgHeight - image.getHeight()) / 2;
        g2d.drawImage(image, x, i * imgHeight + y, null);
      }
      ++i;
    }
    g2d.dispose();

    // Write the strip.
    rsp.addHeader("Content-Type", "image/jpeg");
    ImageIO.write(strip, "jpg", rsp.getOutputStream());
    rsp.setStatus(HttpServletResponse.SC_OK);
  }

  // Synchronize access to this method, so that only one group of images will
  // be fetched at a time (this assumes we'll only have one copy of the servlet)
  private synchronized BufferedImage[] fetch(String[] urls)
      throws MalformedURLException {
    // Start fetcher threads for all the requested images.
    BufferedImage[] images = new BufferedImage[urls.length];
    Fetcher[] fetchers = new Fetcher[urls.length];
    int i = 0;
    for (String url : urls) {
      images[i] = cache.get(url);
      if (images[i] == null) {
        fetchers[i] = new Fetcher(new URL(url));
        fetchers[i].start();
      }
      ++i;
    }

    // Wait on them to complete.
    i = 0;
    for (Fetcher fetcher : fetchers) {
      if (fetcher != null) {
        while (true) {
          try {
            fetcher.join();
            images[i] = fetcher.image;
            cache.put(urls[i], fetcher.image);
            break;
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
      ++i;
    }

    return images;
  }
}
